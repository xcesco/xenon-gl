package com.abubusoft.xenon.core.sensor.orientation.math

import java.io.Serializable

/**
 * Representation of a four-dimensional float-vector
 */
open class Vector4f : Renderable, Serializable {
    /** The points.  */
    protected var points = floatArrayOf(0f, 0f, 0f, 0f)

    /**
     * Instantiates a new vector4f.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param w the w
     */
    constructor(x: Float, y: Float, z: Float, w: Float) {
        points[0] = x
        points[1] = y
        points[2] = z
        points[3] = w
    }

    /**
     * Instantiates a new vector4f.
     */
    constructor() {
        points[0] = 0f
        points[1] = 0f
        points[2] = 0f
        points[3] = 0f
    }

    constructor(vector3f: Vector3f, w: Float) {
        points[0] = vector3f.x()
        points[1] = vector3f.y()
        points[2] = vector3f.z()
        points[3] = w
    }

    /**
     * To array.
     *
     * @return the float[]
     */
    fun ToArray(): FloatArray {
        return points
    }

    fun copyVec4(vec: Vector4f) {
        points[0] = vec.points[0]
        points[1] = vec.points[1]
        points[2] = vec.points[2]
        points[3] = vec.points[3]
    }

    /**
     * Adds the.
     *
     * @param vector the vector
     */
    fun add(vector: Vector4f) {
        points[0] += vector.points[0]
        points[1] += vector.points[1]
        points[2] += vector.points[2]
        points[3] += vector.points[3]
    }

    fun add(vector: Vector3f, w: Float) {
        points[0] += vector.x()
        points[1] += vector.y()
        points[2] += vector.z()
        points[3] += w
    }

    fun subtract(vector: Vector4f) {
        points[0] -= vector.points[0]
        points[1] -= vector.points[1]
        points[2] -= vector.points[2]
        points[3] -= vector.points[3]
    }

    fun subtract(vector: Vector4f, output: Vector4f) {
        output.setXYZW(
            points[0] - vector.points[0], points[1] - vector.points[1], points[2]
                    - vector.points[2], points[3] - vector.points[3]
        )
    }

    fun subdivide(vector: Vector4f) {
        points[0] /= vector.points[0]
        points[1] /= vector.points[1]
        points[2] /= vector.points[2]
        points[3] /= vector.points[3]
    }

    /**
     * Multiply by scalar.
     *
     * @param scalar the scalar
     */
    open fun multiplyByScalar(scalar: Float) {
        points[0] *= scalar
        points[1] *= scalar
        points[2] *= scalar
        points[3] *= scalar
    }

    fun dotProduct(input: Vector4f): Float {
        return points[0] * input.points[0] + points[1] * input.points[1] + points[2] * input.points[2] + points[3] * input.points[3]
    }

    /**
     * Linear interpolation between two vectors storing the result in the output variable.
     *
     * @param input
     * @param output
     * @param t
     */
    fun lerp(input: Vector4f, output: Vector4f, t: Float) {
        output.points[0] = points[0] * (1.0f * t) + input.points[0] * t
        output.points[1] = points[1] * (1.0f * t) + input.points[1] * t
        output.points[2] = points[2] * (1.0f * t) + input.points[2] * t
        output.points[3] = points[3] * (1.0f * t) + input.points[3] * t
    }

    /**
     * Normalize.
     */
    open fun normalize() {
        if (points[3] == 0f) return
        points[0] /= points[3]
        points[1] /= points[3]
        points[2] /= points[3]
        val a = Math.sqrt(
            (points[0] * points[0] + points[1] * points[1] + (points[2]
                    * points[2])).toDouble()
        )
        points[0] = (points[0] / a).toFloat()
        points[1] = (points[1] / a).toFloat()
        points[2] = (points[2] / a).toFloat()
    }
    /**
     * Gets the x.
     *
     * @return the x
     */
    /**
     * Sets the x.
     *
     * @param x the new x
     */
    var x: Float
        get() = points[0]
        set(x) {
            points[0] = x
        }
    /**
     * Gets the y.
     *
     * @return the y
     */
    /**
     * Sets the y.
     *
     * @param y the new y
     */
    var y: Float
        get() = points[1]
        set(y) {
            points[1] = y
        }
    /**
     * Gets the z.
     *
     * @return the z
     */
    /**
     * Sets the z.
     *
     * @param z the new z
     */
    var z: Float
        get() = points[2]
        set(z) {
            points[2] = z
        }
    /**
     * Gets the w.
     *
     * @return the w
     */
    /**
     * Sets the w.
     *
     * @param w the new w
     */
    var w: Float
        get() = points[3]
        set(w) {
            points[3] = w
        }

    fun x(): Float {
        return points[0]
    }

    fun y(): Float {
        return points[1]
    }

    fun z(): Float {
        return points[2]
    }

    fun w(): Float {
        return points[3]
    }

    fun x(x: Float) {
        points[0] = x
    }

    fun y(y: Float) {
        points[1] = y
    }

    fun z(z: Float) {
        points[2] = z
    }

    fun w(w: Float) {
        points[3] = w
    }

    fun setXYZW(x: Float, y: Float, z: Float, w: Float) {
        points[0] = x
        points[1] = y
        points[2] = z
        points[3] = w
    }

    /**
     * Compare this vector4f to the supplied one
     *
     * @param rhs True if they match, false other wise.
     * @return
     */
    fun compareTo(rhs: Vector4f): Boolean {
        var ret = false
        if (points[0] == rhs.points[0] && points[1] == rhs.points[1] && points[2] == rhs.points[2] && points[3] == rhs.points[3]) ret = true
        return ret
    }

    /**
     * Copies the data from the supplied vec3 into this vec4 plus the supplied w.
     *
     * @param input The x y z values to copy in.
     * @param w The extra w element to copy in
     */
    fun copyFromV3f(input: Vector3f, w: Float) {
        points[0] = input.x()
        points[1] = input.y()
        points[2] = input.z()
        points[3] = w
    }

    override fun toString(): String {
        return "X:" + points[0] + " Y:" + points[1] + " Z:" + points[2] + " W:" + points[3]
    }

    companion object {
        /**
         * ID for Serialisation
         */
        private const val serialVersionUID = 1L
    }
}