package com.abubusoft.xenon.animations.events;

import com.abubusoft.xenon.animations.KeyFrame;

public interface EventAnimationListener<K extends KeyFrame> {

	/**
	 * avvio animazione
	 */
	public void onAnimationStart();
	
	/**
	 * termina animazione
	 * 
	 * @param currentFrame
	 */
	public void onAnimationStop(K currentFrame);
	

	/**
	 * metto in pausa
	 */
	void onAnimationPause(K currentFrame, long enlapsedTime);

	/**
	 * resume dell'animazione
	 */
	void onAnimationResume(K currentFrame, long enlapsedTime);
	
}
