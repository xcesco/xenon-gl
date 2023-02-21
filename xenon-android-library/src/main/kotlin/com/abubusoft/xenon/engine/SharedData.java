/**
 * 
 */
package com.abubusoft.xenon.engine;

/**
 * <p>Rappresenta un dato condiviso tra la fase LOGIC e la fase
 * RENDER. Nello specifico, copia i dati di render</p>
 * 
 * @author Francesco Benincasa
 *
 */
public interface SharedData {

	/**
	 * passa i dati dallo phase LOGIC alla fase RENDER
	 */
	public void update();
}
