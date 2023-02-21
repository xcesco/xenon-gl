package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

import android.opengl.GLES20;

public class LedShader extends Shader {

	public LedShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_led_vertex, R.raw.effect_led_fragment, null);
	}
	
	protected int ledsizePtr;
	protected int brightnessPtr;
	
	@Override
	protected void assignPtrs() {
		super.assignPtrs();

		ledsizePtr = GLES20.glGetUniformLocation(programId, "u_led_size");
		brightnessPtr = GLES20.glGetUniformLocation(programId, "u_brightness");
	}
	
	/**
	 * Imposta un attributo uniforme di tipo float
	 * 
	 * @param attributo
	 */
	public void setLedSize(float value) {
		GLES20.glUniform1f(ledsizePtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setLedSize");
	}
	
	/**
	 * Imposta un attributo uniforme di tipo float
	 * 
	 * @param attributo
	 */
	public void setBrightness(float value) {
		GLES20.glUniform1f(brightnessPtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setBrightness");
	}

}

