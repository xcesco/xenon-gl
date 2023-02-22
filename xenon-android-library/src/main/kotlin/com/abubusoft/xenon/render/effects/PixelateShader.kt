package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

import android.opengl.GLES20;

public class PixelateShader extends Shader {

	protected int pixelAmountPtr;

	public PixelateShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_pixelate_vertex, R.raw.effect_pixelate_fragment, null);
	}
	
	@Override
	protected void assignPtrs() {
		super.assignPtrs();
		
		pixelAmountPtr = GLES20.glGetUniformLocation(programId, "u_pixel_amount");				
	}
	
	/**
	 * Imposta le dimensioni dei pixel
	 * 
	 * @param currentFramePercentage
	 */
	public void setPixelAmount(float value) {
		GLES20.glUniform1f(pixelAmountPtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setPixelAmount");
	}

}

