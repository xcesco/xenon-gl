package com.abubusoft.xenon.mesh.tiledmaps

/**
 * Indica la posizione all'interno della tilemap.
 *
 * @author Francesco Benincasa
 */
enum class TiledMapPositionType {
    /**
     * in alto a sinistra
     */
    LEFT_TOP,

    /**
     * a sinistra in mezzo
     */
    LEFT_CENTER,

    /**
     * in basso a sinistra
     */
    LEFT_BOTTOM,

    /**
     * in mezzo in alto
     */
    MIDDLE_TOP,

    /**
     * nel centro
     */
    MIDDLE_CENTER,

    /**
     * in mezzo in basso
     */
    MIDDLE_BOTTOM,

    /**
     * a destra in alto
     */
    RIGHT_TOP,

    /**
     * a destra in mezzo
     */
    RIGHT_CENTER,

    /**
     * a destra in basso
     */
    RIGHT_BOTTOM
}