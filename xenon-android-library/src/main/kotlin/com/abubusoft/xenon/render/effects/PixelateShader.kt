package com.abubusoft.xenon.render.effects

import android.opengl.GLES20
import com.abubusoft.xenon.R
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class PixelateShader : Shader() {
    protected var pixelAmountPtr = 0

    init {
        builder = build(R.raw.effect_pixelate_vertex, R.raw.effect_pixelate_fragment, null)
    }

    override fun assignPtrs() {
        super.assignPtrs()
        pixelAmountPtr = GLES20.glGetUniformLocation(programId, "u_pixel_amount")
    }

    /**
     * Imposta le dimensioni dei pixel
     *
     * @param currentFramePercentage
     */
    fun setPixelAmount(value: Float) {
        GLES20.glUniform1f(pixelAmountPtr, value)
        checkGlError("Shader (id=$programId) setPixelAmount")
    }
}