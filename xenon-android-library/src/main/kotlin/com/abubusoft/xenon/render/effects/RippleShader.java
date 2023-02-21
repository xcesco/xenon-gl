package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

import android.opengl.GLES20;

public class RippleShader extends Shader {

	public RippleShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_ripple_vertex, R.raw.effect_ripple_fragment, null);
	}

	private int waveAmountPtr;

	private int waveDistortionPtr;

	private int waveSpeedPtr;

	private int touchPtr[];

	private int touchTimePtr[];

	private int touchOnPtr[];

	@Override
	protected void assignPtrs() {
		super.assignPtrs();

		waveAmountPtr = GLES20.glGetUniformLocation(programId, "u_wave_amount");
		waveDistortionPtr = GLES20.glGetUniformLocation(programId, "u_wave_distortion");
		waveSpeedPtr = GLES20.glGetUniformLocation(programId, "u_wave_speed");

		touchPtr = new int[RippleEffect.MAX_TOUCH];
		for (int i = 0; i < touchPtr.length; i++) {
			touchPtr[i] = GLES20.glGetUniformLocation(programId, "u_touch" + i);
		}

		touchTimePtr = new int[RippleEffect.MAX_TOUCH];
		for (int i = 0; i < touchTimePtr.length; i++) {
			touchTimePtr[i] = GLES20.glGetUniformLocation(programId, "u_time" + i);
		}

		touchOnPtr = new int[RippleEffect.MAX_TOUCH];
		for (int i = 0; i < touchOnPtr.length; i++) {
			touchOnPtr[i] = GLES20.glGetUniformLocation(programId, "u_touch_on" + i);
		}
	}

	public void setWaveAmount(float value) {
		GLES20.glUniform1f(waveAmountPtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setWaveAmount");
	}

	public void setWaveDistortion(float value) {
		GLES20.glUniform1f(waveDistortionPtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setWaveDistortion");
	}

	public void setWaveSpeed(float value) {
		GLES20.glUniform1f(waveSpeedPtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setWaveSpeed");
	}

	public void setTouchEnabled(int i, boolean value) {
		GLES20.glUniform1f(touchOnPtr[i], value ? 1.0f : 0.0f);
		XenonGL.checkGlError("Shader (id="+programId+") setTouchEnabled[" + i+"]");
	}

	public void setTouchTime(int i, float value) {
		GLES20.glUniform1f(touchTimePtr[i], value);
		XenonGL.checkGlError("Shader (id="+programId+") setTouchTimer[" + i + "]");
	}

	public void setTouch(int i, Point3 value) {
		if (value == null)
			GLES20.glUniform3f(touchPtr[i], 0f, 0f, 0f);
		else
			GLES20.glUniform3f(touchPtr[i], value.x, value.y, value.z);
		XenonGL.checkGlError("Shader (id="+programId+") setTouch[" + i + "]");
	}

}
