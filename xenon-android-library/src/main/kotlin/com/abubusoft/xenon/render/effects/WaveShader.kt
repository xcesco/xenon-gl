package com.abubusoft.xenon.render.effects

import android.opengl.GLES20
import com.abubusoft.xenon.R
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class WaveShader : Shader() {
    private var waveAmountPtr = 0
    private var waveDistortionPtr = 0
    private var waveSpeedPtr = 0

    init {
        builder = build(R.raw.effect_wave_vertex, R.raw.effect_wave_fragment, null)
    }

    override fun assignPtrs() {
        super.assignPtrs()
        waveAmountPtr = GLES20.glGetUniformLocation(programId, "u_wave_amount")
        waveDistortionPtr = GLES20.glGetUniformLocation(programId, "u_wave_distortion")
        waveSpeedPtr = GLES20.glGetUniformLocation(programId, "u_wave_speed")
    }

    fun setWaveAmount(value: Float) {
        GLES20.glUniform1f(waveAmountPtr, value)
        checkGlError("Shader (id=$programId) setWaveAmount")
    }

    fun setWaveDistortion(value: Float) {
        GLES20.glUniform1f(waveDistortionPtr, value)
        checkGlError("Shader (id=$programId) setWaveDistortion")
    }

    fun setWaveSpeed(value: Float) {
        GLES20.glUniform1f(waveSpeedPtr, value)
        checkGlError("Shader (id=$programId)  setWaveSpeed")
    }
}