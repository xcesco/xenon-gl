/**
 *
 */
package com.abubusoft.xenon.entity.modifier

import com.abubusoft.xenon.entity.BaseEntity

/**
 * Modificatore di posizione per le entità.
 *
 * @author Francesco Benincasa
 */
object PositionModifier : StaticModifier() {
    fun moveBy(entity: BaseEntity, xOffset: Float, yOffset: Float, zOffset: Float) {
        entity.position.add(xOffset, yOffset, zOffset)
    }

    fun moveBy(entity: BaseEntity, xOffset: Float, yOffset: Float) {
        entity.position.add(xOffset, yOffset, 0f)
    }

    fun moveTo(entity: BaseEntity, x: Float, y: Float) {
        entity.position.setCoords(x, y, 0f)
    }

    fun moveTo(entity: BaseEntity, x: Float, y: Float, z: Float) {
        entity.position.setCoords(x, y, z)
    }
}