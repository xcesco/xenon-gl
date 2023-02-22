package com.abubusoft.xenon.math

import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.math.XenonMath.max
import com.abubusoft.xenon.math.XenonMath.min

@BindType
class Vector3 : Point3 {
    constructor() {}
    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun add(b: Vector3) {
        x += b.x
        y += b.y
        z += b.z
    }

    /**
     * Computes the cross product between two [Vector3] objects and and sets a new [Vector3] to the result.
     *
     * @param v2
     * [Vector3] The second [Vector3] to cross.
     * @return
     * [Vector3] The computed cross product.
     */
    fun crossProduct(v2: Vector3): Vector3 {
        return Vector3(y * v2.z - z * v2.y, z * v2.x - x * v2.z, x * v2.y - y * v2.x)
    }

    /**
     * Return the dot product of this vector with the input vector
     *
     * @param v2
     * vector 2
     * @return
     * Float value representing the scalar of the dot product operation
     */
    fun dotProduct(v2: Vector3): Float {
        return x * v2.x + y * v2.y + z * v2.z
    }

    fun unit(): Vector3 {
        normalize()
        return this
    }

    /**
     * If you need to get the length of a vector then use this function.
     *
     * @return The length of the vector
     */
    fun length(): Float {
        return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
    override fun copy(): Vector3? {
        return Vector3(x, y, z)
    } /*
	 * Vector.randomDirection = function() { return Vector.fromAngles(Math.random() * Math.PI * 2, Math.asin(Math.random() * 2 - 1)); }; Vector.min = function(a, b) { return new Vector(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z,
	 * b.z)); }; Vector.max = function(a, b) { return new Vector(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z)); }; Vector.lerp = function(a, b, fraction) { return b.subtract(a).multiply(fraction).add(a); }; Vector.fromArray =
	 * function(a) { return new Vector(a[0], a[1], a[2]); };
	 */

    companion object {
        private const val serialVersionUID = -906580073899163617L
        fun negative(a: Vector3, result: Vector3): Vector3 {
            result.x = -a.x
            result.y = -a.y
            result.z = -a.z
            return result
        }

        fun add(a: Vector3, b: Vector3, result: Vector3): Vector3 {
            result.x = a.x + b.x
            result.y = a.y + b.y
            result.z = a.z + b.z
            return result
        }

        fun add(a: Vector3, b: Vector3): Vector3 {
            return Vector3(a.x + b.x, a.y + b.y, a.z + b.z)
        }

        fun add(a: Vector3, b: Float, result: Vector3): Vector3 {
            result.x = a.x + b
            result.y = a.y + b
            result.z = a.z + b
            return result
        }

        fun subtract(a: Vector3, b: Vector3, result: Vector3): Vector3 {
            result.x = a.x - b.x
            result.y = a.y - b.y
            result.z = a.z - b.z
            return result
        }

        fun subtract(a: Vector3, b: Float, result: Vector3): Vector3 {
            result.x = a.x - b
            result.y = a.y - b
            result.z = a.z - b
            return result
        }

        fun subtract(a: Vector3, b: Vector3): Vector3 {
            return Vector3(a.x - b.x, a.y - b.y, a.z - b.z)
        }

        fun multiply(a: Vector3, b: Vector3, result: Vector3): Vector3 {
            result.x = a.x * b.x
            result.y = a.y * b.y
            result.z = a.z * b.z
            return result
        }

        fun multiply(a: Vector3, b: Float, result: Vector3): Vector3 {
            result.x = a.x * b
            result.y = a.y * b
            result.z = a.z * b
            return result
        }

        fun divide(a: Vector3, b: Vector3, result: Vector3): Vector3 {
            result.x = a.x / b.x
            result.y = a.y / b.y
            result.z = a.z / b.z
            return result
        }

        fun divide(a: Vector3, b: Float, result: Vector3): Vector3 {
            result.x = a.x / b
            result.y = a.y / b
            result.z = a.z / b
            return result
        }

        /**
         * Computes the cross product between two [Vector3] objects and and sets a new [Vector3] to the result.
         *
         * @param v1
         * [Vector3] The first [Vector3] to cross.
         * @param v2
         * [Vector3] The second [Vector3] to cross.
         * @param result
         * [Vector3] The computed cross product.
         */
        fun crossProduct(v1: Vector3, v2: Vector3, result: Vector3) {
            result.x = v1.y * v2.z - v1.z * v2.y
            result.y = v1.z * v2.x - v1.x * v2.z
            result.z = v1.x * v2.y - v1.y * v2.x
        }

        /**
         * Computes the cross product between two [Vector3] objects and and sets a new [Vector3] to the result.
         *
         * @param v1
         * [Vector3] The first [Vector3] to cross.
         * @param v2
         * [Vector3] The second [Vector3] to cross.
         * @return
         * [Vector3] The computed cross product.
         */
        fun crossProduct(v1: Vector3, v2: Vector3): Vector3 {
            return Vector3(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x)
        }

        /**
         * Return the dot product of this vector with the input vector
         *
         * @param v1
         * vector 1
         * @param v2
         * vector 2
         * @return
         * Float value representing the scalar of the dot product operation
         */
        fun dotProduct(v1: Vector3, v2: Vector3): Float {
            return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
        }

        /*
	 * public static Vector3 crossAndCreate(Vector3 v1, Vector3 v2) { return new Vector3(v2.y * v1.z - v2.z * v1.y, v2.z * v1.x - v2.x * v1.z, v2.x * v1.y - v2.y * v1.x); }
	 */
        fun unit(a: Vector3, result: Vector3): Vector3 {
            a.copyInto(result)
            result.normalize()
            return result
        }

        fun fromAngles(theta: Float, phi: Float, result: Vector3) {
            result.setCoords(
                (Math.cos(theta.toDouble()) * Math.cos(phi.toDouble())).toFloat(),
                Math.sin(phi.toDouble()).toFloat(),
                (Math.sin(theta.toDouble()) * Math.cos(phi.toDouble())).toFloat()
            )
        }

        fun randomDirection(result: Vector3) {
            fromAngles((Math.random() * Math.PI * 2).toFloat(), Math.asin(Math.random() * 2 - 1).toFloat(), result)
        }

        fun min(a: Vector3, b: Vector3, result: Vector3): Vector3 {
            result.x = min(a.x, b.x)
            result.y = min(a.y, b.y)
            result.z = min(a.z, b.z)
            return result
        }

        fun max(a: Vector3, b: Vector3, result: Vector3): Vector3 {
            result.x = max(a.x, b.x)
            result.y = max(a.y, b.y)
            result.z = max(a.z, b.z)
            return result
        }

        /**
         * linear interpolation tra a e b. Il risultato viene messo in result
         *
         * @param a
         * @param b
         * @param fraction
         * @param result
         * @return
         * risultato dell'interpolazione lineare
         */
        fun lerp(a: Vector3, b: Vector3, fraction: Float, result: Vector3): Vector3 {
            subtract(b, a, result)
            multiply(result, fraction, result)
            add(result, a, result)
            return result
        }

        /**
         * Alternativa al new
         *
         * @param x
         * @param y
         * @param z
         * @return nuovo vector con i valori impostati
         */
        operator fun set(x: Float, y: Float, z: Float): Vector3 {
            return Vector3(x, y, z)
        }
    }
}