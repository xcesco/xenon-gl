package com.abubusoft.xenon.shader

import android.content.Context
import android.opengl.GLES20
import com.abubusoft.xenon.R
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import java.nio.FloatBuffer

/**
 * Forma che rappresenta una tiledMap
 *
 * @author Francesco Benincasa
 */
class ShaderTiledMap(oneTextureForLayer: Boolean, options: ArgonShaderOptions) : Shader() {
    /*
	 * public ShaderTiledMap(Context context, int vertexProgramId, int fragmentProgramId, ArgonShaderOptions options) { super(context, vertexProgramId, fragmentProgramId, options);
	 * }
	 */
    protected var textureSelectorPtr = 0
    protected var opacityPtr = 0

    /**
     * Costruisce una tile
     *
     * @param context
     * @param oneTextureForLayer
     * @param options
     */
    init {
        val context = getBean<Context>(XenonBeanType.CONTEXT)
        // imposta gli shader program ed il numero di texture
        setupFromFiles(
            context,
            R.raw.shader_tiledmap_vertex,
            R.raw.shader_tiledmap_fragment,
            options.define("MORE_TEXTURES", !(options.numberOfTextures == 1 || oneTextureForLayer))
        )
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.shader.Shader#assignPtrs()
	 */
    override fun assignPtrs() {
        super.assignPtrs()
        textureSelectorPtr = GLES20.glGetAttribLocation(programId, "a_textureIndex")
        opacityPtr = GLES20.glGetUniformLocation(programId, "u_opacity")
    }

    /**
     *
     *
     * Imposta l'array del texture selector.
     *
     *
     * @param i
     * @param textureCoords
     */
    fun setTextureSelectorArray(textureSelector: FloatBuffer?) {
        GLES20.glVertexAttribPointer(textureSelectorPtr, 1, GLES20.GL_FLOAT, false, 0, textureSelector)
        GLES20.glEnableVertexAttribArray(textureSelectorPtr)
        checkGlError("Shader (id=$programId) setTextureSelectorArray")
    }

    /**
     *
     *
     * Imposta un opacity value, da 0f a 1f.
     *
     *
     * @param initialValue
     */
    fun setOpacity(value: Float) {
        GLES20.glUniform1f(opacityPtr, value)
        checkGlError("Shader (id=$programId) setOpacity")
    }
}