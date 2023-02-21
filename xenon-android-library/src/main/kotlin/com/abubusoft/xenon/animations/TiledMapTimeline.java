/**
 * 
 */
package com.abubusoft.xenon.animations;

import com.abubusoft.xenon.animations.events.EventFrameListener;

/**
 * @author Francesco Benincasa
 *
 */
public class TiledMapTimeline extends Timeline<TiledMapAnimation, TranslationFrame, TiledMapAnimationHandler> {

	public TiledMapTimeline()
	{
		handler=new TiledMapAnimationHandler();
	}
	
	/**
	 * Imposta il listener relativo allo spostamento.
	 * @param value
	 */
	public void setOnMoveEventListener(EventFrameListener<TranslationFrame> value) {
		handler.setFrameListener(value);
	}

	
}
