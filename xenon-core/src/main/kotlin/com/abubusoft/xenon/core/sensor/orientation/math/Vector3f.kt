package com.abubusoft.xenon.core.sensor.orientation.math

/**
 * 3-dimensional vector with conventient getters and setters. Additionally this class is serializable and
 */
class Vector3f : Renderable {
    /**
     * A float array was chosen instead of individual variables due to performance concerns. Converting the points into
     * an array at run time can cause slowness so instead we use one array and extract the individual variables with get
     * methods.
     */
    protected var points = FloatArray(3)

    /**
     * Initialises the vector with the given values
     *
     * @param x the x-component
     * @param y the y-component
     * @param z the z-component
     */
    constructor(x: Float, y: Float, z: Float) {
        points[0] = x
        points[1] = y
        points[2] = z
    }

    /**
     * Initialises all components of this vector with the given same value.
     *
     * @param value Initialisation value for all components
     */
    constructor(value: Float) {
        points[0] = value
        points[1] = value
        points[2] = value
    }

    /**
     * Instantiates a new vector3f.
     */
    constructor() {}

    /**
     * Copy constructor
     */
    constructor(vector: Vector3f) {
        points[0] = vector.points[0]
        points[1] = vector.points[1]
        points[2] = vector.points[2]
    }

    /**
     * Initialises this vector from a 4-dimensional vector. If the fourth component is not zero, a normalisation of all
     * components will be performed.
     *
     * @param vector The 4-dimensional vector that should be used for initialisation
     */
    constructor(vector: Vector4f) {
        if (vector.w() != 0f) {
            points[0] = vector.x() / vector.w()
            points[1] = vector.y() / vector.w()
            points[2] = vector.z() / vector.w()
        } else {
            points[0] = vector.x()
            points[1] = vector.y()
            points[2] = vector.z()
        }
    }

    /**
     * Returns this vector as float-array.
     *
     * @return the float[]
     */
    fun toArray(): FloatArray {
        return points
    }

    /**
     * Adds a vector to this vector
     *
     * @param summand the vector that should be added component-wise
     */
    fun add(summand: Vector3f) {
        points[0] += summand.points[0]
        points[1] += summand.points[1]
        points[2] += summand.points[2]
    }

    /**
     * Adds the value to all components of this vector
     *
     * @param summand The value that should be added to all components
     */
    fun add(summand: Float) {
        points[0] += summand
        points[1] += summand
        points[2] += summand
    }

    /**
     *
     * @param subtrahend
     */
    fun subtract(subtrahend: Vector3f) {
        points[0] -= subtrahend.points[0]
        points[1] -= subtrahend.points[1]
        points[2] -= subtrahend.points[2]
    }

    /**
     * Multiply by scalar.
     *
     * @param scalar the scalar
     */
    fun multiplyByScalar(scalar: Float) {
        points[0] *= scalar
        points[1] *= scalar
        points[2] *= scalar
    }

    /**
     * Normalize.
     */
    fun normalize() {
        val a = Math.sqrt((points[0] * points[0] + points[1] * points[1] + points[2] * points[2]).toDouble())
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
     * Functions for convenience
     */
    fun x(): Float {
        return points[0]
    }

    fun y(): Float {
        return points[1]
    }

    fun z(): Float {
        return points[2]
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

    fun setXYZ(x: Float, y: Float, z: Float) {
        points[0] = x
        points[1] = y
        points[2] = z
    }

    /**
     * Return the dot product of this vector with the input vector
     *
     * @param inputVec The vector you want to do the dot product with against this vector.
     * @return Float value representing the scalar of the dot product operation
     */
    fun dotProduct(inputVec: Vector3f): Float {
        return points[0] * inputVec.points[0] + points[1] * inputVec.points[1] + points[2] * inputVec.points[2]
    }

    /**
     * Get the cross product of this vector and another vector. The result will be stored in the output vector.
     *
     * @param inputVec The vector you want to get the dot product of against this vector.
     * @param outputVec The vector to store the result in.
     */
    fun crossProduct(inputVec: Vector3f, outputVec: Vector3f) {
        outputVec.x = points[1] * inputVec.points[2] - points[2] * inputVec.points[1]
        outputVec.y = points[2] * inputVec.points[0] - points[0] * inputVec.points[2]
        outputVec.z = points[0] * inputVec.points[1] - points[1] * inputVec.points[0]
    }

    fun crossProduct(`in`: Vector3f): Vector3f {
        val out = Vector3f()
        crossProduct(`in`, out)
        return out
    }

    /**
     * If you need to get the length of a vector then use this function.
     *
     * @return The length of the vector
     */
    val length: Float
        get() = Math.sqrt((points[0] * points[0] + points[1] * points[1] + points[2] * points[2]).toDouble()).toFloat()

    override fun toString(): String {
        return "X:" + points[0] + " Y:" + points[1] + " Z:" + points[2]
    }

    /**
     * Clone the input vector so that this vector has the same values.
     *
     * @param source The vector you want to clone.
     */
    fun clone(source: Vector3f) {
        // this.points[0] = source.points[0];
        // this.points[1] = source.points[1];
        // this.points[2] = source.points[2];
        System.arraycopy(source.points, 0, points, 0, 3)
    }

    /**
     * Clone the input vector so that this vector has the same values.
     *
     * @param source The vector you want to clone.
     */
    fun clone(source: FloatArray?) {
        // this.points[0] = source[0];
        // this.points[1] = source[1];
        // this.points[2] = source[2];
        System.arraycopy(source, 0, points, 0, 3)
    }

    companion object {
        /**
         * ID for serialisation
         */
        private const val serialVersionUID = -4565578579900616220L
    }
}