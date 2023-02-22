/**
 *
 */
package com.abubusoft.xenon.math

import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.math.XenonMath.abs
import com.abubusoft.xenon.math.XenonMath.power2
import com.abubusoft.xenon.math.XenonMath.sqrt

/**
 * @author Francesco Benincasa
 */
@BindType
class Point2 {
    constructor() {}

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + java.lang.Float.floatToIntBits(x)
        result = prime * result + java.lang.Float.floatToIntBits(y)
        return result
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Point2
        if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other.x)) return false
        return if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y)) false else true
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    /**
     *
     *
     * coordinata sull'asse delle ascisse.
     *
     */
    var x = 0f

    /**
     *
     *
     * coordinata sull'asse delle ordinate.
     *
     */
    var y = 0f
    fun distance(point2: Point2): Float {
        return sqrt(power2(point2.x - x)) + power2(point2.y - y)
    }

    fun distance2(point2: Point2): Float {
        return abs(power2(point2.x - x)) + power2(point2.y - y)
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
    fun copy(): Point2 {
        return Point2(x, y)
    }

    /**
     * Imposta le coordinate del punto
     *
     * @param valueX
     * @param valueY
     */
    fun setCoords(valueX: Float, valueY: Float) {
        x = valueX
        y = valueY
    }

    /**
     * Aggiunge alle coordinate del punto
     *
     * @param valueX
     * @param valueY
     */
    fun addCoords(valueX: Float, valueY: Float) {
        x += valueX
        y += valueY
    }

    /**
     * Copia il punto in un altro punto
     *
     * @param destination
     */
    fun copyInto(destination: Point2) {
        destination.x = x
        destination.y = y
    }

    /**
     * Imposta le coordinate del punto con le coordinate dell'altro punto passato come argomento
     *
     * @param source
     */
    fun set(source: Point2) {
        x = source.x
        y = source.y
    }

    /**
     *
     *
     * Aggiunge le coordinate punto passato come parametro.
     *
     *
     * @param value
     */
    fun add(value: Point2) {
        x += value.x
        y += value.y
    }

    /**
     *
     *
     * Aggiunge ad entrambe le coordinate il valore passato come argomento.
     *
     *
     * @param value
     */
    fun add(value: Float) {
        x += value
        y += value
    }

    /**
     *
     *
     * Divide le coordinate per il valore passato come argomento
     *
     *
     * @param value
     */
    operator fun div(value: Float) {
        x /= value
        y /= value
    }

    /**
     *
     *
     * Divide le coordinate per il valore passato come argomento e memorizza il resto.
     *
     *
     * @param value
     */
    fun mod(value: Float) {
        x = x % value
        y = y % value
    }

    fun mul(value: Float) {
        x *= value
        y *= value
    }

    /**
     * Converte le coordinate di questo punto in integer
     *
     * @return
     */
    /*
	 * public Point2 integer() { startX=Math.round(startX); startY=Math.round(startY);
	 * 
	 * return this; }
	 */
    companion object {
        /**
         * Alternativa al new
         *
         * @param x
         * @param y
         * @return nuovo punto
         */
        operator fun set(x: Float, y: Float): Point2 {
            return Point2(x, y)
        }
    }
}