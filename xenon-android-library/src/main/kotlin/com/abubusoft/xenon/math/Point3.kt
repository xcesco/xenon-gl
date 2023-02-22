/**
 *
 */
package com.abubusoft.xenon.math

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType
import com.abubusoft.xenon.math.XenonMath.abs
import com.abubusoft.xenon.math.XenonMath.power2
import com.abubusoft.xenon.math.XenonMath.sqrt
import java.io.Serializable

/**
 * Punto in uno spazio cartesiano tridimensionale, con la caratteristica di essere persistente mediante Kripton.
 *
 * @author Francesco Benincasa
 */
@BindType
open class Point3 : Serializable {
    constructor() {}
    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var x = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var y = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var z = 0f
    fun add(x1: Float, y1: Float, z1: Float) {
        x += x1
        y += y1
        z += z1
    }

    fun add(value: Point3) {
        x += value.x
        y += value.y
        z += value.z
    }

    /**
     * Definisce le coordinate di un punto
     * @param newX
     * @param newY
     * @param newZ
     */
    fun setCoords(newX: Float, newY: Float, newZ: Float) {
        x = newX
        y = newY
        z = newZ
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copyInto(java.lang.Object)
	 */
    fun copyInto(dest: Point3) {
        dest.x = x
        dest.y = y
        dest.z = z
    }

    fun normalize() {
        val denomin = 1 / abs(sqrt(x * x + y * y + z * z))
        x *= denomin
        y *= denomin
        z *= denomin
    }

    /**
     * Calcola distanza tra due punti
     *
     * @param point2
     * @return
     * distanza
     */
    fun distance(point2: Point3): Float {
        return sqrt(power2(point2.x - x)) + (power2(point2.y - y) + power2(point2.z - z))
    }

    /**
     * Calcola la distanza al quadrato tra due punti (più veloce rispetto a [.distance])
     *
     * @param point2
     * @return
     * distanza
     */
    fun distance2(point2: Point3): Float {
        return abs(power2(point2.x - x)) + (power2(point2.y - y) + power2(point2.z - z))
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
    open fun copy(): Point3? {
        return Point3(x, y, z)
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + java.lang.Float.floatToIntBits(x)
        result = prime * result + java.lang.Float.floatToIntBits(y)
        result = prime * result + java.lang.Float.floatToIntBits(z)
        return result
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Point3
        if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other.x)) return false
        if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y)) return false
        return if (java.lang.Float.floatToIntBits(z) != java.lang.Float.floatToIntBits(other.z)) false else true
    }

    companion object {
        private const val serialVersionUID = 4754358686291704165L

        /**
         * Alternativa al new
         *
         * @param x
         * @param y
         * @param z
         * @return
         * nuovo punto
         */
        operator fun set(x: Float, y: Float, z: Float): Point3 {
            return Point3(x, y, z)
        }
    }
}