/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.misc.NormalizedTimer;
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer;
import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * @author Francesco Benincasa
 * 
 */
@UseShader(WaveShader.class)
public class WaveEffect extends AbstractEffect<WaveShader> {

	public float waveAmount;

	public float waveDistortion;

	/**
	 * Speed. Maggiore è la velocità e più le onde si muoveranno velocemente.
	 * da 1 .. +n.
	 */
	public float waveSpeed;

	public WaveEffect() {
		timer = new NormalizedTimer(TypeNormalizedTimer.REPEAT_FOREVER);
		timer.setMaxTime((long) (4f * NormalizedTimer.SECOND_IN_MILLISECONDS));
		
		timer.start();
		
		waveAmount = 20f;
		waveDistortion = 0.005f;
		waveSpeed = 4.f;
	}

	public NormalizedTimer timer;

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.render.AbstractEffect#updateShader(com.abubusoft.xenon.shader.Shader, long, float)
	 */
	@Override
	protected void updateShader(WaveShader shader, long enlapsedTime, float speedAdapter) {
		shader.setWaveAmount(this.waveAmount);
		shader.setWaveDistortion(this.waveDistortion);
		shader.setWaveSpeed(this.waveSpeed);
		
		timer.update(enlapsedTime);
		shader.setTime(2f * XenonMath.PI * (timer.getNormalizedEnlapsedTime()));
		
	}
}
