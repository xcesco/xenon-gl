/**
 *
 */
package com.abubusoft.xenon.entity

import com.abubusoft.xenon.math.Point3
import java.io.Serializable

/**
 *
 * E' la base di tutte le entità. Contiene tutto quello che definisce di base
 * un'entità astratta.
 *
 * @author Francesco Benincasa
 */
open class BaseEntity : Serializable {
    /**
     * centro del sistema di riferimento dell'entità
     */
    val position: Point3

    /**
     * angoli di rotazione espressi in gradi
     */
    val rotationAngles: Point3

    /**
     * posizione di salvataggio
     */
    val oldPosition: Point3

    /**
     * rotazione di salvataggio
     */
    val oldRotationAngles: Point3

    /**
     * fattore di scala
     */
    var scale: Float

    /**
     * Costruttore di default
     */
    init {
        position = Point3()
        rotationAngles = Point3()
        scale = 1.0f
        oldPosition = Point3()
        oldRotationAngles = Point3()
    }

    /**
     * salva i valori attuali di posizione e rotazione in oldPosition e oldRotation
     */
    fun savePosition() {
        position.copyInto(oldPosition)
        rotationAngles.copyInto(oldRotationAngles)
    }

    /**
     * Ripristina i vecchi valore di posizione e rotazione
     */
    fun restorePosition() {
        oldPosition.copyInto(position)
        oldRotationAngles.copyInto(rotationAngles)
    }

    /**
     * Copia l'oggetto
     *
     * @return
     */
    open fun copy(): BaseEntity? {
        var copy: BaseEntity? = null
        try {
            // creiamo nuova istanza
            copy = javaClass.newInstance()
            position.copyInto(copy.position)
            rotationAngles.copyInto(copy.rotationAngles)
            copy.scale = scale
            oldPosition.copyInto(copy.oldPosition)
            oldRotationAngles.copyInto(copy.oldRotationAngles)
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
    fun copyInto(destination: BaseEntity?) {
        position.copyInto(destination!!.position)
        rotationAngles.copyInto(destination.rotationAngles)
        oldPosition.copyInto(destination.oldPosition)
        oldRotationAngles.copyInto(destination.oldRotationAngles)
        destination.scale = scale
    }

    companion object {
        private const val serialVersionUID = -2432547140052127109L
    }
}