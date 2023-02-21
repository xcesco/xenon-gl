/**
 * 
 */
package com.abubusoft.xenon.engine;

/**
 * @author Francesco Benincasa
 *
 */
public interface TouchEventListener {

	/**
	 * <p>Evento relativo al touch</p>
	 * 
	 * @param type
	 * @param x
	 * @param y
	 */
	void onTouch(TouchType type, float x, float y);
}
