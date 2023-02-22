package com.abubusoft.xenon.mesh.tiledmaps

/**
 * Layer che fa parte di una animazione.
 *
 * @author Francesco Benincasa
 */
class TileAnimationFrame(
    /**
     * frame associato
     */
    val layer: Layer,
    /**
     * durata del frame in millisecondi
     */
    var duration: Long
)