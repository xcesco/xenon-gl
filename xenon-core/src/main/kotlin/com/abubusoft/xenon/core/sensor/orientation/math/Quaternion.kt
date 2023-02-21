package com.abubusoft.xenon.core.sensor.orientation.math

/**
 * The Quaternion class. A Quaternion is a four-dimensional vector that is used to represent rotations of a rigid body
 * in the 3D space. It is very similar to a rotation vector; it contains an angle, encoded into the w component
 * and three components to describe the rotation-axis (encoded into x, y, z).
 *
 *
 *
 * Quaternions allow for elegant descriptions of 3D rotations, interpolations as well as extrapolations and compared to
 * Euler angles, they don't suffer from gimbal lock. Interpolations between two Quaternions are called SLERP (Spherical
 * Linear Interpolation).
 *
 *
 *
 *
 * This class also contains the representation of the same rotation as a Quaternion and 4x4-Rotation-Matrix.
 *
 *
 * @author Leigh Beattie, Alexander Pacha
 */
class Quaternion : Vector4f() {
    /**
     * Rotation matrix that contains the same rotation as the Quaternion in a 4x4 homogenised rotation matrix.
     * Remember that for performance reasons, this matrix is only updated, when it is accessed and not on every change
     * of the quaternion-values.
     */
    private val matrix: Matrixf4x4 = Matrixf4x4()

    /**
     * This variable is used to synchronise the rotation matrix with the current quaternion values. If someone has
     * changed the
     * quaternion numbers then the matrix will need to be updated. To save on processing we only really want to update
     * the matrix when someone wants to fetch it, instead of whenever someone sets a quaternion value.
     */
    //var dirty = false

    fun clone(): Quaternion {
        val clone = Quaternion()
        clone.copyVec4(this)
        return clone
    }

    /**
     * Normalise this Quaternion into a unity Quaternion.
     */
    fun normalise() {
        dirty = true
        val mag = Math.sqrt(
            (points[3] * points[3] + points[0] * points[0] + points[1] * points[1] + (points[2]
                    * points[2])).toDouble()
        ).toFloat()
        points[3] = points[3] / mag
        points[0] = points[0] / mag
        points[1] = points[1] / mag
        points[2] = points[2] / mag
    }

    override fun normalize() {
        normalise()
    }

    /**
     * Copies the values from the given quaternion to this one
     *
     * @param quat The quaternion to copy from
     */
    fun set(quat: Quaternion) {
        dirty = true
        copyVec4(quat)
    }

    /**
     * Multiply this quaternion by the input quaternion and store the result in the out quaternion
     *
     * @param input
     * @param output
     */
    fun multiplyByQuat(input: Quaternion, output: Quaternion) {
        val inputCopy = Vector4f()
        if (input !== output) {
            output.points[3] = points[3] * input.points[3] - points[0] * input.points[0] - points[1] * input.points[1] - (points[2]
                    * input.points[2]) //w = w1w2 - x1x2 - y1y2 - z1z2
            output.points[0] = (points[3] * input.points[0] + points[0] * input.points[3] + points[1] * input.points[2] - points[2]
                    * input.points[1]) //x = w1x2 + x1w2 + y1z2 - z1y2
            output.points[1] = (points[3] * input.points[1] + points[1] * input.points[3] + points[2] * input.points[0] - points[0]
                    * input.points[2]) //y = w1y2 + y1w2 + z1x2 - x1z2
            output.points[2] = (points[3] * input.points[2] + points[2] * input.points[3] + points[0] * input.points[1] - points[1]
                    * input.points[0]) //z = w1z2 + z1w2 + x1y2 - y1x2
        } else {
            inputCopy.points[0] = input.points[0]
            inputCopy.points[1] = input.points[1]
            inputCopy.points[2] = input.points[2]
            inputCopy.points[3] = input.points[3]
            output.points[3] = points[3] * inputCopy.points[3] - points[0] * inputCopy.points[0] - (points[1]
                    * inputCopy.points[1]) - points[2] * inputCopy.points[2] //w = w1w2 - x1x2 - y1y2 - z1z2
            output.points[0] = points[3] * inputCopy.points[0] + points[0] * inputCopy.points[3] + (points[1]
                    * inputCopy.points[2]) - points[2] * inputCopy.points[1] //x = w1x2 + x1w2 + y1z2 - z1y2
            output.points[1] = points[3] * inputCopy.points[1] + points[1] * inputCopy.points[3] + (points[2]
                    * inputCopy.points[0]) - points[0] * inputCopy.points[2] //y = w1y2 + y1w2 + z1x2 - x1z2
            output.points[2] = points[3] * inputCopy.points[2] + points[2] * inputCopy.points[3] + (points[0]
                    * inputCopy.points[1]) - points[1] * inputCopy.points[0] //z = w1z2 + z1w2 + x1y2 - y1x2
        }
    }

    /**
     * Multiply this quaternion by the input quaternion and store the result in the out quaternion
     *
     * @param input
     * @param output
     */
    var bufferQuaternion: Quaternion? = null

    /**
     * Creates a new Quaternion object and initialises it with the identity Quaternion
     */
    init {
        loadIdentityQuat()
    }

    fun multiplyByQuat(input: Quaternion) {
        if (bufferQuaternion == null) {
            bufferQuaternion = Quaternion()
        }
        dirty = true
        bufferQuaternion!!.copyVec4(this)
        multiplyByQuat(input, bufferQuaternion!!)
        copyVec4(bufferQuaternion!!)
    }

    /**
     * Multiplies this Quaternion with a scalar
     *
     * @param scalar the value that the vector should be multiplied with
     */
    override fun multiplyByScalar(scalar: Float) {
        dirty = true
        multiplyByScalar(scalar)
    }

    /**
     * Add a quaternion to this quaternion
     *
     * @param input The quaternion that you want to add to this one
     */
    fun addQuat(input: Quaternion) {
        dirty = true
        addQuat(input, this)
    }

    /**
     * Add this quaternion and another quaternion together and store the result in the output quaternion
     *
     * @param input The quaternion you want added to this quaternion
     * @param output The quaternion you want to store the output in.
     */
    fun addQuat(input: Quaternion, output: Quaternion) {
        output.x = x + input.x
        output.y = y + input.y
        output.z = z + input.z
        output.w = w + input.w
    }

    /**
     * Subtract a quaternion to this quaternion
     *
     * @param input The quaternion that you want to subtracted from this one
     */
    fun subQuat(input: Quaternion) {
        dirty = true
        subQuat(input, this)
    }

    /**
     * Subtract another quaternion from this quaternion and store the result in the output quaternion
     *
     * @param input The quaternion you want subtracted from this quaternion
     * @param output The quaternion you want to store the output in.
     */
    fun subQuat(input: Quaternion, output: Quaternion) {
        output.x = x - input.x
        output.y = y - input.y
        output.z = z - input.z
        output.w = w - input.w
    }

    /**
     * Converts this Quaternion into the Rotation-Matrix representation which can be accessed by
     * [getMatrix4x4][Quaternion.getMatrix4x4]
     */
    private fun convertQuatToMatrix() {
        val x = points[0]
        val y = points[1]
        val z = points[2]
        val w = points[3]
        matrix.setX0(1 - 2 * (y * y) - 2 * (z * z)) //1 - 2y2 - 2z2
        matrix.setX1(2 * (x * y) + 2 * (w * z)) // 2xy - 2wz
        matrix.setX2(2 * (x * z) - 2 * (w * y)) //2xz + 2wy
        matrix.setX3(0f)
        matrix.setY0(2 * (x * y) - 2 * (w * z)) //2xy + 2wz
        matrix.setY1(1 - 2 * (x * x) - 2 * (z * z)) //1 - 2x2 - 2z2
        matrix.setY2(2 * (y * z) + 2 * (w * x)) // 2yz + 2wx
        matrix.setY3(0f)
        matrix.setZ0(2 * (x * z) + 2 * (w * y)) //2xz + 2wy
        matrix.setZ1(2 * (y * z) - 2 * (w * x)) //2yz - 2wx
        matrix.setZ2(1 - 2 * (x * x) - 2 * (y * y)) //1 - 2x2 - 2y2
        matrix.setZ3(0f)
        matrix.setW0(0f)
        matrix.setW1(0f)
        matrix.setW2(0f)
        matrix.setW3(1f)
    }

    /**
     * Get an axis angle representation of this quaternion.
     *
     * @param output Vector4f axis angle.
     */
    fun toAxisAngle(output: Vector4f) {
        if (w > 1) {
            normalise() // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
        }
        val angle = 2 * Math.toDegrees(Math.acos(w.toDouble())).toFloat()
        val x: Float
        val y: Float
        val z: Float
        val s = Math.sqrt((1 - w * w).toDouble()).toFloat() // assuming quaternion normalised then w is less than 1, so term always positive.
        if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
            // if s close to zero then direction of axis not important
            x = points[0] // if it is important that axis is normalised then replace with x=1; y=z=0;
            y = points[1]
            z = points[2]
        } else {
            x = points[0] / s // normalise axis
            y = points[1] / s
            z = points[2] / s
        }
        output.points[0] = x
        output.points[1] = y
        output.points[2] = z
        output.points[3] = angle
    }

    /**
     * Returns the heading, attitude and bank of this quaternion as euler angles in the double array respectively
     *
     * @return An array of size 3 containing the euler angles for this quaternion
     */
    fun toEulerAngles(): DoubleArray {
        val ret = DoubleArray(3)
        ret[0] = Math.atan2(
            (2 * points[1] * w - 2 * points[0] * points[2]).toDouble(), (1 - 2 * (points[1] * points[1]) - (2
                    * (points[2] * points[2]))).toDouble()
        ) // atan2(2*qy*qw-2*qx*qz , 1 - 2*qy2 - 2*qz2)
        ret[1] = Math.asin((2 * points[0] * points[1] + 2 * points[2] * w).toDouble()) // asin(2*qx*qy + 2*qz*qw) 
        ret[2] = Math.atan2(
            (2 * points[0] * w - 2 * points[1] * points[2]).toDouble(), (1 - 2 * (points[0] * points[0]) - (2
                    * (points[2] * points[2]))).toDouble()
        ) // atan2(2*qx*qw-2*qy*qz , 1 - 2*qx2 - 2*qz2)
        return ret
    }

    fun toEulerAngles(result: DoubleArray) {
        result[0] = Math.atan2(
            (2 * points[1] * w - 2 * points[0] * points[2]).toDouble(), (1 - 2 * (points[1] * points[1]) - (2
                    * (points[2] * points[2]))).toDouble()
        ) // atan2(2*qy*qw-2*qx*qz , 1 - 2*qy2 - 2*qz2)
        result[1] = Math.asin((2 * points[0] * points[1] + 2 * points[2] * w).toDouble()) // asin(2*qx*qy + 2*qz*qw) 
        result[2] = Math.atan2(
            (2 * points[0] * w - 2 * points[1] * points[2]).toDouble(), (1 - 2 * (points[0] * points[0]) - (2
                    * (points[2] * points[2]))).toDouble()
        ) // atan2(2*qx*qw-2*qy*qz , 1 - 2*qx2 - 2*qz2)
    }

    /**
     * Sets the quaternion to an identity quaternion of 0,0,0,1.
     */
    fun loadIdentityQuat() {
        dirty = true
        x = 0f
        y = 0f
        z = 0f
        w = 1f
    }

    override fun toString(): String {
        return "{X: $x, Y:$y, Z:$z, W:$w}"
    }

    /**
     * This is an internal method used to build a quaternion from a rotation matrix and then sets the current quaternion
     * from that matrix.
     *
     */
    private fun generateQuaternionFromMatrix() {
        val qx: Float
        val qy: Float
        val qz: Float
        val qw: Float
        val mat = matrix.matrix
        var indices: IntArray? = null
        indices = if (matrix.size() == 16) {
            if (matrix.isColumnMajor) {
                Matrixf4x4.matIndCol16_3x3
            } else {
                Matrixf4x4.matIndRow16_3x3
            }
        } else {
            if (matrix.isColumnMajor) {
                Matrixf4x4.matIndCol9_3x3
            } else {
                Matrixf4x4.matIndRow9_3x3
            }
        }
        val m00 = indices[0]
        val m01 = indices[1]
        val m02 = indices[2]
        val m10 = indices[3]
        val m11 = indices[4]
        val m12 = indices[5]
        val m20 = indices[6]
        val m21 = indices[7]
        val m22 = indices[8]
        val tr = mat[m00] + mat[m11] + mat[m22]
        if (tr > 0) {
            val s = Math.sqrt(tr + 1.0).toFloat() * 2 // S=4*qw 
            qw = 0.25f * s
            qx = (mat[m21] - mat[m12]) / s
            qy = (mat[m02] - mat[m20]) / s
            qz = (mat[m10] - mat[m01]) / s
        } else if ((mat[m00] > mat[m11]) and (mat[m00] > mat[m22])) {
            val s = Math.sqrt(1.0 + mat[m00] - mat[m11] - mat[m22]).toFloat() * 2 // S=4*qx 
            qw = (mat[m21] - mat[m12]) / s
            qx = 0.25f * s
            qy = (mat[m01] + mat[m10]) / s
            qz = (mat[m02] + mat[m20]) / s
        } else if (mat[m11] > mat[m22]) {
            val s = Math.sqrt(1.0 + mat[m11] - mat[m00] - mat[m22]).toFloat() * 2 // S=4*qy
            qw = (mat[m02] - mat[m20]) / s
            qx = (mat[m01] + mat[m10]) / s
            qy = 0.25f * s
            qz = (mat[m12] + mat[m21]) / s
        } else {
            val s = Math.sqrt(1.0 + mat[m22] - mat[m00] - mat[m11]).toFloat() * 2 // S=4*qz
            qw = (mat[m10] - mat[m01]) / s
            qx = (mat[m02] + mat[m20]) / s
            qy = (mat[m12] + mat[m21]) / s
            qz = 0.25f * s
        }
        x = qx
        y = qy
        z = qz
        w = qw
    }

    /**
     * You can set the values for this quaternion based off a rotation matrix. If the matrix you supply is not a
     * rotation matrix this will fail. You MUST provide a 4x4 matrix.
     *
     * @param matrix A column major rotation matrix
     */
    fun setColumnMajor(matrix: FloatArray) {
        this.matrix.setMatrixElements(matrix)
        this.matrix.isColumnMajor = true
        generateQuaternionFromMatrix()
    }

    /**
     * You can set the values for this quaternion based off a rotation matrix. If the matrix you supply is not a
     * rotation matrix this will fail.
     *
     * @param matrix A column major rotation matrix
     */
    fun setRowMajor(matrix: FloatArray) {
        this.matrix.setMatrixElements(matrix)
        this.matrix.isColumnMajor = false
        generateQuaternionFromMatrix()
    }

    /**
     * Set this quaternion from axis angle values. All rotations are in degrees.
     *
     * @param azimuth The rotation around the z axis
     * @param pitch The rotation around the y axis
     * @param roll The rotation around the x axis
     */
    fun setEulerAngle(azimuth: Float, pitch: Float, roll: Float) {
        val heading = Math.toRadians(roll.toDouble())
        val attitude = Math.toRadians(pitch.toDouble())
        val bank = Math.toRadians(azimuth.toDouble())
        val c1 = Math.cos(heading / 2)
        val s1 = Math.sin(heading / 2)
        val c2 = Math.cos(attitude / 2)
        val s2 = Math.sin(attitude / 2)
        val c3 = Math.cos(bank / 2)
        val s3 = Math.sin(bank / 2)
        val c1c2 = c1 * c2
        val s1s2 = s1 * s2
        w = (c1c2 * c3 - s1s2 * s3).toFloat()
        x = (c1c2 * s3 + s1s2 * c3).toFloat()
        y = (s1 * c2 * c3 + c1 * s2 * s3).toFloat()
        z = (c1 * s2 * c3 - s1 * c2 * s3).toFloat()
        dirty = true
    }

    /**
     * Rotation is in degrees. Set this quaternion from the supplied axis angle.
     *
     * @param vec The vector of rotation
     * @param rot The angle of rotation around that vector in degrees.
     */
    fun setAxisAngle(vec: Vector3f, rot: Float) {
        val s = Math.sin(Math.toRadians((rot / 2).toDouble()))
        x = vec.x * s.toFloat()
        y = vec.y * s.toFloat()
        z = vec.z * s.toFloat()
        w = Math.cos(Math.toRadians((rot / 2).toDouble())).toFloat()
        dirty = true
    }

    fun setAxisAngleRad(vec: Vector3f, rot: Double) {
        val s = rot / 2
        x = vec.x * s.toFloat()
        y = vec.y * s.toFloat()
        z = vec.z * s.toFloat()
        w = rot.toFloat() / 2
        dirty = true
    }//toMatrixColMajor();

    /**
     * @return Returns this Quaternion in the Rotation Matrix representation
     */
    val matrix4x4: Matrixf4x4
        get() {
            if (dirty) {
                convertQuatToMatrix()
                dirty = false
            }
            return matrix
        }

    fun copyFromVec3(vec: Vector3f, w: Float) {
        copyFromV3f(vec, w)
    }

    /**
     * Get a linear interpolation between this quaternion and the input quaternion, storing the result in the output
     * quaternion.
     *
     * @param input The quaternion to be slerped with this quaternion.
     * @param output The quaternion to store the result in.
     * @param t The ratio between the two quaternions where 0 <= t <= 1.0 . Increase value of t will bring rotation
     * closer to the input quaternion.
     */
    fun slerp(input: Quaternion, output: Quaternion, t: Float) {
        // Calculate angle between them.
        //double cosHalftheta = this.dotProduct(input);
        var bufferQuat: Quaternion? = null
        var cosHalftheta = dotProduct(input)
        if (cosHalftheta < 0) {
            bufferQuat = Quaternion()
            cosHalftheta = -cosHalftheta
            bufferQuat.points[0] = -input.points[0]
            bufferQuat.points[1] = -input.points[1]
            bufferQuat.points[2] = -input.points[2]
            bufferQuat.points[3] = -input.points[3]
        } else {
            bufferQuat = input
        }
        /**
         * if(dot < 0.95f){
         * double angle = Math.acos(dot);
         * double ratioA = Math.sin((1 - t) * angle);
         * double ratioB = Math.sin(t * angle);
         * double divisor = Math.sin(angle);
         *
         * //Calculate Quaternion
         * output.setW((float)((this.getW() * ratioA + input.getW() * ratioB)/divisor));
         * output.setX((float)((this.getX() * ratioA + input.getX() * ratioB)/divisor));
         * output.setY((float)((this.getY() * ratioA + input.getY() * ratioB)/divisor));
         * output.setZ((float)((this.getZ() * ratioA + input.getZ() * ratioB)/divisor));
         * }
         * else{
         * lerp(input, output, t);
         * }
         */
        // if qa=qb or qa=-qb then theta = 0 and we can return qa
        if (Math.abs(cosHalftheta) >= 1.0) {
            output.points[0] = points[0]
            output.points[1] = points[1]
            output.points[2] = points[2]
            output.points[3] = points[3]
        } else {
            val sinHalfTheta = Math.sqrt(1.0 - cosHalftheta * cosHalftheta)
            // if theta = 180 degrees then result is not fully defined
            // we could rotate around any axis normal to qa or qb
            //if(Math.abs(sinHalfTheta) < 0.001){
            //output.setW(this.getW() * 0.5f + input.getW() * 0.5f);
            //output.setX(this.getX() * 0.5f + input.getX() * 0.5f);
            //output.setY(this.getY() * 0.5f + input.getY() * 0.5f);
            //output.setZ(this.getZ() * 0.5f + input.getZ() * 0.5f);
            //  lerp(bufferQuat, output, t);
            //}
            //else{
            val halfTheta = Math.acos(cosHalftheta.toDouble())
            val ratioA = Math.sin((1 - t) * halfTheta) / sinHalfTheta
            val ratioB = Math.sin(t * halfTheta) / sinHalfTheta

            //Calculate Quaternion
            output.points[3] = (points[3] * ratioA + bufferQuat.points[3] * ratioB).toFloat()
            output.points[0] = (points[0] * ratioA + bufferQuat.points[0] * ratioB).toFloat()
            output.points[1] = (points[1] * ratioA + bufferQuat.points[1] * ratioB).toFloat()
            output.points[2] = (points[2] * ratioA + bufferQuat.points[2] * ratioB).toFloat()

            //}
        }
    }

    companion object {
        /**
         * A randomly generated UID to make the Quaternion object serialisable.
         */
        private const val serialVersionUID = -7148812599404359073L
    }
}