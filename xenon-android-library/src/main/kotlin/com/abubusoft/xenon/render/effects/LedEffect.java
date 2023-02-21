/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * @author Francesco Benincasa
 * 
 */
@UseShader(LedShader.class)
public class LedEffect extends AbstractEffect<LedShader> {

	public LedEffect() {
		ledSize = 128.0f;
		brightness = 1.0f;
	}

	public float ledSize = 128.0f;
	public float brightness = 1.0f;

	@Override
	protected void updateShader(LedShader shader, long enlapsedTime, float speedAdapter) {
		shader.setLedSize(ledSize);
		shader.setBrightness(brightness);
	}

}
