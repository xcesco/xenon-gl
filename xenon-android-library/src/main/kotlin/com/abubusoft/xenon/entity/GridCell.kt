package com.abubusoft.xenon.entity

class GridCell : BaseEntity() {
    var textureLowX = 0f
    var textureLowY = 0f
    var textureHighY = 0f
    var textureHighX = 0f
    var width = 0
    var height = 0

    /**
     * Imposta le coordinate della texture.
     *
     * @param xl
     * @param xh
     * @param yl
     * @param yh
     */
    fun setTextureCoordinate(xl: Float, xh: Float, yl: Float, yh: Float) {
        textureLowX = xl
        textureHighX = xh
        textureLowY = yl
        textureHighY = yh
    }

    /**
     * Imposta le dimensioni del tile
     * @param tileWidth
     * @param tileHeight
     */
    fun setDimensions(tileWidth: Int, tileHeight: Int) {
        height = tileHeight
        width = tileWidth
    }

    companion object {
        private const val serialVersionUID = -5483129550120552425L
    }
}