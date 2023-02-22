package com.abubusoft.xenon.render

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.shader.ArgonShaderOptions
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.shader.drawers.ShaderDrawer
import com.abubusoft.xenon.texture.RenderedTexture

/**
 *
 *
 * Gli effetti che derivano da questa classe devono avere l'annotazione UseShader, pena eccezione.
 *
 *
 *
 *
 * Ogni shader che viene utilizzato ha a disposizione le seguenti constanti:
 *
 *
 *
 *  * RESOLUTION_X: larghezza della texture
 *  * RESOLUTION_Y: altezza della texture
 *  * INV_RESOLUTION_X: 1 / larghezza della texture
 *  * INV_RESOLUTION_Y: 1 / altezza della texture
 *
 *
 * @author Francesco Benincasa
 * @param <P>
</P> */
abstract class AbstractEffect<E : Shader> {
    /**
     * indica se l'effetto è abilitato o meno.
     */
    var enabled = false

    /**
     * indice dell'effetto
     */
    var index = 0
    protected var shader: E? = null
    protected var updater: EffectUpdater<*, *>? = null
    fun setUpdater(updater: EffectUpdater<*, E>?) {
        this.updater = updater
    }

    var clearColor = Color.BLACK
    fun execute(texture: RenderedTexture, shape: Mesh, matrix: Matrix4x4, enlapsedTime: Long, speedAdapter: Float) {
        shader!!.use()
        shader!!.setVertexCoordinatesArray(shape.vertices!!)
        shader!!.setTexture(0, texture)
        shader!!.setTextureCoordinatesArray(0, shape.textures[0])
        update(shader, enlapsedTime, speedAdapter)
        ShaderDrawer.draw(shader!!, shape, matrix)
    }

    /**
     *
     *
     * Serve ad aggiornare gli attributi dello shader che devono essere definite dalla classe effect.
     *
     *
     * @param shader
     * @param enlapsedTime
     * @param speedAdapter
     */
    protected abstract fun updateShader(shader: E, enlapsedTime: Long, speedAdapter: Float)

    /**
     *
     *
     * Aggiorna lo shader.
     *
     *
     * @param shader
     * @param enlapsedTime
     * @param speedAdapter
     */
    protected fun update(shader: E, enlapsedTime: Long, speedAdapter: Float) {
        updateShader(shader, enlapsedTime, speedAdapter)
        if (updater != null) {
            updater!!.update(this, shader, enlapsedTime, speedAdapter)
        }
    }

    /**
     *
     *
     * Pulisce il color buffer e lo depth buffer.
     *
     */
    fun clear() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(Color.red(clearColor).toFloat(), Color.green(clearColor).toFloat(), Color.blue(clearColor).toFloat(), 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    /**
     *
     *
     * Effettua il setup dell'effetto.
     *
     *
     * @param context
     * @param options
     * @param pipeline
     */
    fun setup(context: Context?, screenWidth: Float, screenHeight: Float, options: ArgonShaderOptions, pipeline: RenderPipeline) {
        val ann = this.javaClass.getAnnotation(UseShader::class.java) ?: throw RuntimeException("No @UseShader defined for effect")
        val shaderClazz = ann.value() as Class<E>
        index = pipeline.effects.size
        options.costant("RESOLUTION_X", java.lang.Float.toString(screenWidth))
        options.costant("RESOLUTION_Y", java.lang.Float.toString(screenHeight))
        options.costant("INV_RESOLUTION_X", java.lang.Float.toString(1f / screenWidth))
        options.costant("INV_RESOLUTION_Y", java.lang.Float.toString(1f / screenHeight))
        setShaderOptions(options)
        shader = ShaderManager.createShader(shaderClazz, options)
    }

    /**
     *
     *
     * Permette di configurare i parametri dello shader
     *
     *
     * @param options
     */
    fun setShaderOptions(options: ArgonShaderOptions?) {}
}