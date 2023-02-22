/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps

/**
 * @author Francesco Benincasa
 */
interface MovementListener {
    /**
     * Sollevato quando si effettua lo scroll della mappa. Viene eseguito un evento per lo scroll orizzontale ed uno per lo scroll verticale.
     * Vieen
     * @param direction
     * @param area
     */
    fun onAreaChange(direction: ScrollDirectionType?, area: Int)

    /**
     * Sollevato quando si sposta la posizione della mappa.
     * @param startX
     * @param startY
     */
    fun onPosition(x: Float, y: Float)
}