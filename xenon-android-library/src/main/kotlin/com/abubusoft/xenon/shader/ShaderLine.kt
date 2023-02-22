/**
 *
 */
package com.abubusoft.xenon.shader

import android.graphics.Color
import android.opengl.GLES20
import com.abubusoft.xenon.R

/**
 * @author Francesco Benincasa
 */
class ShaderLine : Shader() {
    /**
     * Costruisce una tile
     *
     * @param context
     * @param options
     */
    init {
        builder = ShaderBuilder.Companion.build(
            R.raw.shader_line_vertex,
            R.raw.shader_line_fragment,
            ArgonShaderOptions.Companion.build().name("line").numberOfTextures(0).numberOfUniformAttributes(0)
        )
    }

    /**
     *
     *
     * @param a
     * da 0 a 255
     * @param r
     * da 0 a 255
     * @param g
     * da 0 a 255
     * @param b
     * da 0 a 255
     */
    fun setColor(a: Int, r: Int, g: Int, b: Int) {
        // il formato dei colori è rgba
        setUniform4f(colorPtr, r / 255f, g / 255f, b / 255f, a / 255f)
    }

    /**
     *
     *
     * @param r
     * da 0 a 255
     * @param g
     * da 0 a 255
     * @param b
     * da 0 a 255
     */
    fun setColor(argb: Int) {
        // il formato dei colori è rgba
        setUniform4f(colorPtr, Color.red(argb) / 255f, Color.green(argb) / 255f, Color.blue(argb) / 255f, Color.alpha(argb) / 255f)
    }

    override fun assignPtrs() {
        super.assignPtrs()
        colorPtr = GLES20.glGetUniformLocation(programId, "u_color")
    }
}