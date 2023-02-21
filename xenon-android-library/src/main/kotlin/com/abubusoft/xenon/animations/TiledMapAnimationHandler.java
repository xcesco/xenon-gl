/**
 * 
 */
package com.abubusoft.xenon.animations;

import com.abubusoft.xenon.animations.events.EventFrameListener;

/**
 * Rappresenta l'handler delle animazioni su tiledmap che comprendono sia lo spostamento che l'animazione di uno sprite.
 * 
 * @author Francesco Benincasa
 *
 */
public class TiledMapAnimationHandler extends Parallel2Handler<TranslationFrame, TextureKeyFrame> {

	public TiledMapAnimationHandler()
	{
		handler0=new TranslationHandler();
		handler1=new TextureAnimationHandler();
	}
	

	/**
	 * Imposta il listener relativo allo spostamento.
	 * @param value
	 */
	public void setOnMoveEventListener(EventFrameListener<TranslationFrame> value) {
		handler0.setFrameListener(value);
	}
	
}
