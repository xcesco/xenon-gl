/**
 *
 */
package com.abubusoft.xenon.shader

import android.content.Context
import android.util.SparseArray
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.XenonGL.clearGlError

/**
 * @author Francesco Benincasa
 */
object ShaderManager {
    /**
     * array delle texture
     */
    private val shaders: SparseArray<Shader>

    /**
     * mappa degli shader in base al nome
     */
    private val mapShaders: HashMap<String, Shader>

    /**
     * Costruttore
     */
    init {
        shaders = SparseArray()
        mapShaders = HashMap()
    }

    /**
     *
     *
     * Crea un video shader.
     *
     *
     * @return
     * video shader
     */
    fun createShaderVideo(): VideoShader {
        return createShader(VideoShader::class.java)
    }

    /**
     * Crea uno shader che si preoccupa semplicemente di disegnare mediante una texture
     *
     * @return shader per disegnare una texture con un sistema di coordinate
     */
    fun createShaderTexture(): ShaderTexture {
        return createShader(ShaderTexture::class.java)
    }

    /**
     * Crea uno shader per le linee
     *
     * @return shader per le linee
     */
    fun createShaderLine(): ShaderLine {
        return createShader(ShaderLine::class.java)
    }

    fun <E : Shader> createShader(vertexProgramId: Int, fragmentProgramId: Int, shaderClazz: Class<E>, options: ArgonShaderOptions?): E? {
        var shader: E? = null
        try {
            val context = getBean<Context>(XenonBeanType.CONTEXT)
            Logger.info("Create shader %s", shaderClazz.toString())
            shader = shaderClazz.newInstance()
            shader!!.setupFromFiles(context, vertexProgramId, fragmentProgramId, options)
            addShader(shader)
            Logger.info("Builded shader kind: %s, programId: %s", shaderClazz.toString(), shader.programId)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return shader
    }

    /**
     *
     *
     * Crea uno shader custom partendo dalla sua classe. Nel costruttore della classe deve essere inizializzato il builder.
     *
     *
     * @param shaderClazz
     * class shader
     * @param options
     * opzioni per la costruzione dello shader
     * @return shader
     */
    fun <E : Shader> createShader(shaderClazz: Class<E>, options: ArgonShaderOptions?): E {
        return try {
            val context = getBean<Context>(XenonBeanType.CONTEXT)
            Logger.info("Create shader %s", shaderClazz.toString())
            val shader = shaderClazz.newInstance()
            shader.setupFromBuilder(context, options)
            addShader(shader)
            Logger.info("Builded shader kind: %s, programId: %s", shaderClazz.toString(), shader.programId)
            shader
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw(e)
        }
    }

    /**
     *
     *
     * Crea uno shader custom partendo dalla sua classe. Nel costruttore della classe deve essere inizializzato il builder.
     *
     *
     * @param shaderClazz
     * class shader
     * @return shader
     */
    fun <E : Shader> createShader(shaderClazz: Class<E>): E {
        return createShader(shaderClazz, null)
    }

    /**
     * Creao uno shader custom, al quale bastano i parametri di base offerti dalla classe base shader.
     *
     * @param vertexProgramId
     * @param fragmentProgramId
     * @param options
     * @return
     * shader
     */
    fun createShader(vertexProgramId: Int, fragmentProgramId: Int, options: ArgonShaderOptions?): Shader {
        return createShader(vertexProgramId, fragmentProgramId, Shader::class.java, options)!!
    }

    fun createShaderTiledMap(oneTextureForLayer: Boolean, options: ArgonShaderOptions): ShaderTiledMap {
        val shader = ShaderTiledMap(oneTextureForLayer, options)
        addShader(shader)
        return shader
    }

    fun getShader(name: String?): Shader? {
        return mapShaders[name]
    }

    /**
     * Aggiunge uno shader
     *
     * @param shader
     */
    private fun addShader(shader: Shader) {
        shaders.append(shader.programId, shader)
        if (shader.name != null) {
            mapShaders[shader.name!!] = shader
        }
    }

    /**
     * Rigenera gli shader, partendo dal loro source. Non possono essere fatte modifiche agli shader.
     */
    fun reloadShaders() {
        var shader: Shader
        for (i in 0 until shaders.size()) {
            shader = shaders.valueAt(i)
            shader.reload()
        }
    }

    /**
     * Azzera gli shader e dealloca le risorse utilizzate
     */
    fun clearShaders() {
        var shader: Shader?
        clearGlError()
        Logger.debug("Clear " + shaders.size() + " old shaders, without deleting them ")
        for (i in 0 until shaders.size()) {
            shader = shaders.valueAt(i)
            shader?.unbind()
        }
        shaders.clear()
        mapShaders.clear()
    }
}