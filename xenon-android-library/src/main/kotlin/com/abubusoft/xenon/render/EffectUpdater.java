package com.abubusoft.xenon.render;

import com.abubusoft.xenon.shader.Shader;

/**
 * <p></p>
 * @author Francesco Benincasa
 *
 * @param <P>
 * @param <E>
 */
public interface EffectUpdater<P extends AbstractEffect<E>, E extends Shader> {

	/**
	 * @param effect
	 * @param shader
	 * @param enlapsedTime
	 * @param speedAdapter
	 */
	public void update(P effect, E shader, long enlapsedTime, float speedAdapter);
}
