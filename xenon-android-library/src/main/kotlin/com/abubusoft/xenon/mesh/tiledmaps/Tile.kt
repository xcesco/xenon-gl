package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.texture.TextureRegion

/**
 *
 *
 * Rappresenta la singola tile
 *
 *
 * @author Francesco Benincasa
 */
@BindType
class Tile
/**
 *
 *
 * Costruttore
 *
 *
 * @param gid
 * @param column
 * @param row
 * @param atlasColumn
 * @param atlasRow
 * @param width
 * @param height
 */ @JvmOverloads constructor(
    /**
     * values del tile
     */
    var gid: Int = 0,
    /**
     * colonna nel layer
     */
    var layerColumn: Int = 0,
    /**
     * riga nel layer
     */
    var layerRow: Int = 0,
    /**
     * colonna nel tileset
     */
    var atlasColumn: Int = -1,
    /**
     * riga nel tileset
     */
    var atlasRow: Int = -1,
    /**
     * larghezza
     */
    var width: Int = 0,
    /**
     * altezza del tile
     */
    var height: Int = 0,
    /**
     * offsetX del tile
     */
    var drawOffsetX: Int = 0,
    /**
     * screenOffsetY del tile
     */
    var drawOffsetY: Int = 0
) : TextureRegion() {
    /**
     * index di [TiledMap.textureList] della texture associata.
     */
    var textureSelector: Byte = 0

    /**
     * Indica che la tile prima di essere usata deve essere ruotata in orizzontale
     */
    var horizontalFlip = false

    /**
     * Indica che la tile prima di essere usata deve essere ruotata in verticale
     */
    var verticalFlip = false

    /**
     * Indica che la tile prima di essere usata deve essere subire l'inversione tra asse startX e startY della texture
     */
    var diagonalFlip = false
    /**
     *
     *
     * Costruttore
     *
     *
     * @param gid
     * @param layerColumn
     * @param layerRow
     * @param atlasColumn
     * @param atlasRow
     * @param width
     * @param height
     */
    /**
     * Imposta le coordinate della texture.
     *
     * @param xl
     * @param xh
     * @param yl
     * @param yh
     */
    fun setTextureCoordinate(xl: Float, xh: Float, yl: Float, yh: Float) {
        lowX = xl
        highX = xh
        lowY = yl
        highY = yh
    }

    /**
     * Imposta le dimensioni del tile
     *
     * @param tileWidth
     * @param tileHeight
     */
    fun setDimensions(tileWidth: Int, tileHeight: Int) {
        height = tileHeight
        width = tileWidth
    }

    /**
     * Imposta la posizione del tile nel layer
     *
     * @param row
     * @param col
     */
    fun setLayerPosition(row: Int, col: Int) {
        layerRow = row
        layerColumn = col
    }

    fun setAtlas(column: Int, row: Int) {
        atlasColumn = column
        atlasRow = row
    }

    fun setEmpty() {
        gid = 0
    }

    companion object {
        fun getEmptyTile(column: Int, row: Int): Tile {
            return Tile(0, column, row, -1, -1, 0, 0, 0, 0)
        }

        /**
         * Indica se la tile è vuota o meno. Una tile è vuota se è nulla o se ha id == 0.
         *
         * @param tile
         * @return
         */
        fun isEmpty(tile: Tile?): Boolean {
            return if (tile == null) true else tile.gid == 0
        }
    }
}