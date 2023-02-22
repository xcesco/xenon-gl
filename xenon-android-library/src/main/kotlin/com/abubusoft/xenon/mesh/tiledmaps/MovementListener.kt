/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * @author Francesco Benincasa
 *
 */
public interface MovementListener {

	/**
	 * Sollevato quando si effettua lo scroll della mappa. Viene eseguito un evento per lo scroll orizzontale ed uno per lo scroll verticale.
	 * Vieen
	 * @param direction
	 * @param area
	 */
	void onAreaChange(ScrollDirectionType direction, int area);

	/**
	 * Sollevato quando si sposta la posizione della mappa.
	 * @param startX
	 * @param startY
	 */
	void onPosition(float x, float y);
}
