package com.abubusoft.xenon.mesh

/**
 * Rappresenta una mattonella che tipicamente viene utilizzata all'interno delle tiledMap.
 *
 * @author Francesco Benincasa
 */
class MeshTile : MeshSprite() {
    /**
     * ( * , startY ) indice della colonna del tile
     */
    var tileRowIndex = 0

    /**
     * ( startX , * ) indice della riga del tile
     */
    var tileColumnIndex = 0

    /**
     * altezza della tile in pixel
     */
    var tileHeight = 0f

    /**
     * larghezza della tile in pixel
     */
    var tileWidth = 0f

    /**
     * offset X del tile.
     */
    var drawOffsetX = 0

    /**
     * offset Y del tile.
     */
    var drawOffsetY = 0

    companion object {
        private const val serialVersionUID = 902361471602149778L
    }
}