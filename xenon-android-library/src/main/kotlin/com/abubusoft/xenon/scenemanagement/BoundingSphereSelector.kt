/**
 *
 */
package com.abubusoft.xenon.scenemanagement

import com.abubusoft.xenon.entity.Entity
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.math.Sphere

/**
 * Permette di selezionare un oggetto in base al fatto che il punto di selezione sia dentro la sfera di collisione
 * dell'entità
 *
 * @author Francesco Benincasa
 */
class BoundingSphereSelector<E : Entity<*>?> {
    var bounding: Sphere
    var selectorPoint: Point3? = null

    init {
        bounding = Sphere()
    }

    fun setSelectorPoint(selector: Point3?) {
        selectorPoint = selector
    }

    fun isSelected(entity: E): Boolean {
        bounding[entity!!.position] = entity.boundingRadius
        return if (bounding.intersect(selectorPoint)) {
            true
        } else false
    }
}