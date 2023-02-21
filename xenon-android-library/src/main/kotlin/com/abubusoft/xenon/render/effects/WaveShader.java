package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

import android.opengl.GLES20;

public class WaveShader extends Shader {

	public WaveShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_wave_vertex, R.raw.effect_wave_fragment, null);
	}

	private int waveAmountPtr;
	
	private int waveDistortionPtr;

	private int waveSpeedPtr;

	@Override
	protected void assignPtrs() {
		super.assignPtrs();
		
		waveAmountPtr = GLES20.glGetUniformLocation(programId, "u_wave_amount");
		waveDistortionPtr = GLES20.glGetUniformLocation(programId, "u_wave_distortion");
		waveSpeedPtr= GLES20.glGetUniformLocation(programId, "u_wave_speed");
		
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
		XenonGL.checkGlError("Shader (id="+programId+")  setWaveSpeed");
	}
}

