package com.abubusoft.xenon.render.effects

import android.opengl.GLES20
import com.abubusoft.xenon.R
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class LedShader : Shader() {
    protected var ledsizePtr = 0
    protected var brightnessPtr = 0

    init {
        builder = build(R.raw.effect_led_vertex, R.raw.effect_led_fragment, null)
    }

    override fun assignPtrs() {
        super.assignPtrs()
        ledsizePtr = GLES20.glGetUniformLocation(programId, "u_led_size")
        brightnessPtr = GLES20.glGetUniformLocation(programId, "u_brightness")
    }

    /**
     * Imposta un attributo uniforme di tipo float
     *
     * @param attributo
     */
    fun setLedSize(value: Float) {
        GLES20.glUniform1f(ledsizePtr, value)
        checkGlError("Shader (id=$programId) setLedSize")
    }

    /**
     * Imposta un attributo uniforme di tipo float
     *
     * @param attributo
     */
    fun setBrightness(value: Float) {
        GLES20.glUniform1f(brightnessPtr, value)
        checkGlError("Shader (id=$programId) setBrightness")
    }
}