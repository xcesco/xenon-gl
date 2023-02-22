package com.abubusoft.xenon.math

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import java.io.Serializable

/**
 * Dimensioni 3d
 *
 * @author Francesco Benincasa
 */
@BindType
class Dimension3 : Serializable {
    /**
     * larghezza
     */
    @Bind
    var width = 0f

    /**
     * altezza
     */
    @Bind
    var height = 0f

    /**
     * profondità
     */
    @Bind
    var depth = 0f

    /**
     * Imposta in un'unica volta i tre valori
     *
     * @param width
     * @param height
     * @param depth
     */
    operator fun set(width: Float, height: Float, depth: Float) {
        this.width = width
        this.height = height
        this.depth = depth
    }

    companion object {
        private const val serialVersionUID = 3425624648092503466L
    }
}