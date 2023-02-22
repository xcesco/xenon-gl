/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * 
 * @author Francesco Benincasa
 *
 */
@UseShader(PixelateShader.class)
public class PixelateEffect extends AbstractEffect<PixelateShader> {
	
	public PixelateEffect()
	{
		pixelAmount=200f;
	}

	public float pixelAmount;
	
	@Override
	protected void updateShader(PixelateShader shader, long enlapsedTime, float speedAdapter) {
		shader.setPixelAmount(pixelAmount);
		
	}
		
}
