package com.abubusoft.xenon.entity.modifier

import com.abubusoft.xenon.entity.BaseEntity

/**
 * Modificatore per la rotazione di un'entità.
 *
 * @author Francesco Benincasa
 */
object RotationModifier : DynamicModifier() {
    /**
     * Definisce l'angolo di rotazione per i vari assi per un entità.
     *
     * @param entity
     * @param xAngle
     * @param yAngle
     * @param zAngle
     */
    fun rotateBy(entity: BaseEntity, xAngle: Float, yAngle: Float, zAngle: Float) {
        entity.rotationAngles.setCoords(xAngle, yAngle, zAngle)
    }
}