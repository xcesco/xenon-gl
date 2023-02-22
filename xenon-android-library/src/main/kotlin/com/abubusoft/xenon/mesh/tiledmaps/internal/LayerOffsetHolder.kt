package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.math.Point2

/**
 * Rappresenta per un layer, il suo punto di riferimento per quel che riguarda il primo tile da disegnare
 *
 * @author xcesco
 */
class LayerOffsetHolder {
    var tileIndexX = 0
    var tileIndexY = 0
    var screenOffsetX = 0
    var screenOffsetY = 0
    fun setOffset(offsetPoint: Point2) {
        screenOffsetX = offsetPoint.x.toInt()
        screenOffsetY = offsetPoint.y.toInt()
    }

    fun setOffset(x: Int, y: Int) {
        screenOffsetX = x
        screenOffsetY = y
    }
}