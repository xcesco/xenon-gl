/**
 *
 */
package com.abubusoft.xenon.scenemanagement

import com.abubusoft.xenon.entity.Entity
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.math.Sphere
import com.abubusoft.xenon.mesh.MeshTile

/**
 * Permette di selezionare un oggetto in base al fatto che il punto di selezione
 * sia dentro la sfera di collisione dell'entità
 *
 * @author Francesco Benincasa
 */
class BoundingBox2DSelector<E : Entity<*>?> {
    var bounding: Sphere
    var selectorPoint: Point3? = null
    var position: Point3? = null
    var sprite: MeshTile? = null
    var halfWidth = 0f
    var halfHeight = 0f

    init {
        bounding = Sphere()
    }

    fun setSelectorPoint(selector: Point3?) {
        selectorPoint = selector
    }

    /**
     * Indica se il punto di touch è in proiezione su un entity registrato
     * @param entity
     * @return
     */
    fun isSelected(entity: E): Boolean {
        position = entity!!.position
        halfWidth = entity.mesh!!.boundingBox.width / 2f
        halfHeight = entity.mesh!!.boundingBox.height / 2f

        //
        return if (position!!.x - halfWidth <= selectorPoint!!.x && position!!.x + halfWidth >= selectorPoint!!.x && position!!.y - halfHeight <= selectorPoint!!.y && position!!.y + halfHeight >= selectorPoint!!.y) {
            true
        } else false
    }
}