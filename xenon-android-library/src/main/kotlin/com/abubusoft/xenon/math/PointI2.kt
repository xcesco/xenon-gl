/**
 *
 */
package com.abubusoft.xenon.math

import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.math.XenonMath.abs
import com.abubusoft.xenon.math.XenonMath.power2
import com.abubusoft.xenon.math.XenonMath.power2I
import com.abubusoft.xenon.math.XenonMath.sqrt

/**
 * Punto in un sistema di coordinate unicamente intere.
 * @author Francesco Benincasa
 */
@BindType
class PointI2 {
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + x
        result = prime * result + y
        return result
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as PointI2
        if (x != other.x) return false
        return if (y != other.y) false else true
    }

    constructor() {}

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    var x = 0
    var y = 0

    fun distance(point2: PointI2): Float {
        return sqrt((power2I(point2.x - x) + power2I(point2.y - y)).toFloat())
    }

    fun distance2(point2: PointI2): Float {
        return abs((power2I(point2.x - x) + power2I(point2.y - y)).toFloat())
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
    fun copy(): PointI2 {
        return PointI2(x, y)
    }

    /**
     * Imposta le coordinate del punto
     *
     * @param xValue
     * @param yValue
     */
    fun setCoords(xValue: Int, yValue: Int) {
        x = xValue
        y = yValue
    }

    /**
     * Aggiunge alle coordinate del punto
     *
     * @param xValue
     * @param yValue
     */
    fun addCoords(xValue: Int, yValue: Int) {
        x += xValue
        y += yValue
    }

    /**
     * Copia il punto in un altro punto
     *
     * @param destination
     */
    fun copyInto(destination: PointI2) {
        destination.x = x
        destination.y = y
    }

    companion object {
        /**
         * Alternativa al new
         *
         * @param x
         * x
         * @param y
         * y
         * @return
         * nuovo punto con le coordinate impostate
         */
        operator fun set(x: Int, y: Int): PointI2 {
            return PointI2(x, y)
        }
    }
}