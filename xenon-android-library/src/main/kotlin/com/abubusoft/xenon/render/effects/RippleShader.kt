package com.abubusoft.xenon.render.effects

import android.opengl.GLES20
import com.abubusoft.xenon.R
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class RippleShader : Shader() {
    private var waveAmountPtr = 0
    private var waveDistortionPtr = 0
    private var waveSpeedPtr = 0
    private var touchPtr: IntArray
    private var touchTimePtr: IntArray
    private var touchOnPtr: IntArray

    init {
        builder = build(R.raw.effect_ripple_vertex, R.raw.effect_ripple_fragment, null)
    }

    override fun assignPtrs() {
        super.assignPtrs()
        waveAmountPtr = GLES20.glGetUniformLocation(programId, "u_wave_amount")
        waveDistortionPtr = GLES20.glGetUniformLocation(programId, "u_wave_distortion")
        waveSpeedPtr = GLES20.glGetUniformLocation(programId, "u_wave_speed")
        touchPtr = IntArray(RippleEffect.Companion.MAX_TOUCH)
        for (i in touchPtr.indices) {
            touchPtr[i] = GLES20.glGetUniformLocation(programId, "u_touch$i")
        }
        touchTimePtr = IntArray(RippleEffect.Companion.MAX_TOUCH)
        for (i in touchTimePtr.indices) {
            touchTimePtr[i] = GLES20.glGetUniformLocation(programId, "u_time$i")
        }
        touchOnPtr = IntArray(RippleEffect.Companion.MAX_TOUCH)
        for (i in touchOnPtr.indices) {
            touchOnPtr[i] = GLES20.glGetUniformLocation(programId, "u_touch_on$i")
        }
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
        checkGlError("Shader (id=$programId) setWaveSpeed")
    }

    fun setTouchEnabled(i: Int, value: Boolean) {
        GLES20.glUniform1f(touchOnPtr[i], if (value) 1.0f else 0.0f)
        checkGlError("Shader (id=$programId) setTouchEnabled[$i]")
    }

    fun setTouchTime(i: Int, value: Float) {
        GLES20.glUniform1f(touchTimePtr[i], value)
        checkGlError("Shader (id=$programId) setTouchTimer[$i]")
    }

    fun setTouch(i: Int, value: Point3?) {
        if (value == null) GLES20.glUniform3f(touchPtr[i], 0f, 0f, 0f) else GLES20.glUniform3f(touchPtr[i], value.x, value.y, value.z)
        checkGlError("Shader (id=$programId) setTouch[$i]")
    }
}