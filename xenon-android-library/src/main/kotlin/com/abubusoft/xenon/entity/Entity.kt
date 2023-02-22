/**
 *
 */
package com.abubusoft.xenon.entity

import com.abubusoft.xenon.mesh.Mesh

/**
 * @author Francesco Benincasa
 */
open class Entity<E : Mesh?>
/**
 * Costruttore di default
 */() : BaseEntity() {
    constructor(shapeValue: E) : this() {
        mesh = shapeValue
    }

    /**
     * indicase se l'entità è visibile
     */
    var visible = true

    /**
     * indica se l'entità è soggetta a collisioni
     */
    var collidable = true

    /**
     * mesh associato all'entity
     */
    var mesh: E? = null

    /**
     * Raggio del cerchio di contenimento
     *
     * @return
     */
    open val boundingRadius: Float
        get() = mesh!!.boundingSphereRadius

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
    override fun copy(): Entity<E?>? {
        var copy: Entity<E?>? = null
        try {
            // cloniamo
            copy = javaClass.newInstance()
            copyInto(copy)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return copy
    }

    /**
     * Effettua la copia nell'oggetto destinazione
     *
     * @param destination
     */
    fun copyInto(destination: Entity<E?>?) {
        super.copyInto(destination)
        destination!!.collidable = collidable
        destination.mesh = mesh
        destination.visible = visible
    }

    companion object {
        private const val serialVersionUID = 3212120492925326047L
    }
}