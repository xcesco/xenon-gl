package com.abubusoft.xenon.core.sensor.orientation.math

import android.util.Log
import com.abubusoft.xenon.core.sensor.orientation.math.Matrix.setIdentityM

/**
 * The Class Matrixf4x4.
 *
 * Internal the matrix is structured as
 *
 * [ x0 , y0 , z0 , w0 ] [ x1 , y1 , z1 , w1 ] [ x2 , y2 , z2 , w2 ] [ x3 , y3 , z3 , w3 ]
 *
 * it is recommend that when setting the matrix values individually that you use the set{x,#} methods, where 'x' is
 * either x, y, z or w and # is either 0, 1, 2 or 3, setY1 for example. The reason you should use these functions is
 * because it will map directly to that part of the matrix regardless of whether or not the internal matrix is column
 * major or not. If the matrix is either or length 9 or 16 it will be able to determine if it can set the value or not.
 * If the matrix is of size 9 but you set say w2, the value will not be set and the set method will return without any
 * error.
 *
 */
class Matrixf4x4 {
    /**
     * Find out if the stored matrix is column major
     *
     * @return
     */
    /**
     * Set whether the internal data is col major by passing true, or false for a row major matrix. The matrix is column
     * major by default.
     *
     * @param colMajor
     */
    var isColumnMajor = true
    var isMatrixValid = false
        private set

    /** The matrix.  */
    var matrix: FloatArray

    /**
     * Instantiates a new matrixf4x4. The Matrix is assumed to be Column major, however you can change this by using the
     * setColumnMajor function to false and it will operate like a row major matrix.
     */
    init {
        // The matrix is defined as float[column][row]
        matrix = FloatArray(16)
        setIdentityM(matrix, 0)
        isMatrixValid = true
    }

    fun size(): Int {
        return matrix.size
    }

    /**
     * Sets the matrix from a float[16] array. If the matrix you set isn't 16 long then the matrix will be set as
     * invalid.
     *
     * @param elements the new matrix
     */
    fun setMatrixElements(elements: FloatArray) {
        this.matrix = elements
        if (elements.size == 16 || elements.size == 9) isMatrixValid = true else {
            isMatrixValid = false
            Log.e("matrix", "Matrix set is invalid, size is " + elements.size + " expected 9 or 16")
        }
    }

    fun copyMatrixValues(otherElements: FloatArray) {
        if (matrix.size != otherElements.size) {
            Log.e("matrix", "Matrix set is invalid, size is " + otherElements.size + " expected 9 or 16")
        }
        for (i in otherElements.indices) {
            matrix[i] = otherElements[i]
        }
    }

    /**
     * Multiply the given vector by this matrix. This should only be used if the matrix is of size 16 (use the
     * matrix.size() method).
     *
     * @param vector A vector of length 4.
     */
    fun multiplyVector4fByMatrix(vector: Vector4f) {
        if (isMatrixValid && matrix.size == 16) {
            var x = 0f
            var y = 0f
            var z = 0f
            var w = 0f
            val vectorArray = vector.ToArray()
            if (isColumnMajor) {
                for (i in 0..3) {
                    val k = i * 4
                    x += matrix[k + 0] * vectorArray[i]
                    y += matrix[k + 1] * vectorArray[i]
                    z += matrix[k + 2] * vectorArray[i]
                    w += matrix[k + 3] * vectorArray[i]
                }
            } else {
                for (i in 0..3) {
                    x += matrix[0 + i] * vectorArray[i]
                    y += matrix[4 + i] * vectorArray[i]
                    z += matrix[8 + i] * vectorArray[i]
                    w += matrix[12 + i] * vectorArray[i]
                }
            }
            vector.x = x
            vector.y = y
            vector.z = z
            vector.w = w
        } else Log.e("matrix", "Matrix is invalid, is " + matrix.size + " long, this equation expects a 16 value matrix")
    }

    /**
     * Multiply the given vector by this matrix. This should only be used if the matrix is of size 9 (use the
     * matrix.size() method).
     *
     * @param vector A vector of length 3.
     */
    fun multiplyVector3fByMatrix(vector: Vector3f) {
        if (isMatrixValid && matrix.size == 9) {
            var x = 0f
            var y = 0f
            var z = 0f
            val vectorArray = vector.toArray()
            if (!isColumnMajor) {
                for (i in 0..2) {
                    val k = i * 3
                    x += matrix[k + 0] * vectorArray[i]
                    y += matrix[k + 1] * vectorArray[i]
                    z += matrix[k + 2] * vectorArray[i]
                }
            } else {
                for (i in 0..2) {
                    x += matrix[0 + i] * vectorArray[i]
                    y += matrix[3 + i] * vectorArray[i]
                    z += matrix[6 + i] * vectorArray[i]
                }
            }
            vector.x = x
            vector.y = y
            vector.z = z
        } else Log.e(
            "matrix", "Matrix is invalid, is " + matrix.size
                    + " long, this function expects the internal matrix to be of size 9"
        )
    }

    /**
     * Multiply matrix4x4 by matrix.
     *
     * @param matrixf the matrixf
     */
    fun multiplyMatrix4x4ByMatrix(matrixf: Matrixf4x4) {
        // TODO implement Strassen Algorithm in place of this slower naive one.
        if (isMatrixValid && matrixf.isMatrixValid) {
            val bufferMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            val matrix = matrixf.matrix
            /**
             * for(int i = 0; i < 4; i++){ for(int j = 0; j < 4; j++){
             *
             * int k = i * 4; bufferMatrix[0 + j] += this.matrix[k + j] * matrix[0 * 4 + i]; bufferMatrix[1 * 4 + j] +=
             * this.matrix[k + j] * matrix[1 * 4 + i]; bufferMatrix[2 * 4 + j] += this.matrix[k + j] * matrix[2 * 4 +
             * i]; bufferMatrix[3 * 4 + j] += this.matrix[k + j] * matrix[3 * 4 + i]; } }
             */
            multiplyMatrix(matrix, 0, bufferMatrix, 0)
            matrixf.setMatrixElements(bufferMatrix)
        } else Log.e(
            "matrix", "Matrix is invalid, internal is " + matrix.size + " long" + " , input matrix is "
                    + matrixf.matrix.size + " long"
        )
    }

    fun multiplyMatrix(input: FloatArray, inputOffset: Int, output: FloatArray, outputOffset: Int) {
        for (i in 0..3) {
            for (j in 0..3) {
                val k = i * 4
                output[outputOffset + 0 + j] += matrix[k + j] * input[inputOffset + 0 * 4 + i]
                output[outputOffset + 1 * 4 + j] += matrix[k + j] * input[inputOffset + 1 * 4 + i]
                output[outputOffset + 2 * 4 + j] += matrix[k + j] * input[inputOffset + 2 * 4 + i]
                output[outputOffset + 3 * 4 + j] += matrix[k + j] * input[inputOffset + 3 * 4 + i]
            }
        }
    }

    /**
     * This will rearrange the internal structure of the matrix. Be careful though as this is an expensive operation.
     */
    fun transpose() {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                val newMatrix = FloatArray(16)
                for (i in 0..3) {
                    val k = i * 4
                    newMatrix[k] = matrix[i]
                    newMatrix[k + 1] = matrix[4 + i]
                    newMatrix[k + 2] = matrix[8 + i]
                    newMatrix[k + 3] = matrix[12 + i]
                }
                matrix = newMatrix
            } else {
                val newMatrix = FloatArray(9)
                for (i in 0..2) {
                    val k = i * 3
                    newMatrix[k] = matrix[i]
                    newMatrix[k + 1] = matrix[3 + i]
                    newMatrix[k + 2] = matrix[6 + i]
                }
                matrix = newMatrix
            }
        }
    }

    fun setX0(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[0]] = value else matrix[matIndRow16_3x3[0]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[0]] = value else matrix[matIndRow9_3x3[0]] = value
            }
        }
    }

    fun setX1(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[1]] = value else matrix[matIndRow16_3x3[1]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[1]] = value else matrix[matIndRow9_3x3[1]] = value
            }
        }
    }

    fun setX2(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[2]] = value else matrix[matIndRow16_3x3[2]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[2]] = value else matrix[matIndRow9_3x3[2]] = value
            }
        }
    }

    fun setY0(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[3]] = value else matrix[matIndRow16_3x3[3]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[3]] = value else matrix[matIndRow9_3x3[3]] = value
            }
        }
    }

    fun setY1(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[4]] = value else matrix[matIndRow16_3x3[4]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[4]] = value else matrix[matIndRow9_3x3[4]] = value
            }
        }
    }

    fun setY2(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[5]] = value else matrix[matIndRow16_3x3[5]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[5]] = value else matrix[matIndRow9_3x3[5]] = value
            }
        }
    }

    fun setZ0(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[6]] = value else matrix[matIndRow16_3x3[6]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[6]] = value else matrix[matIndRow9_3x3[6]] = value
            }
        }
    }

    fun setZ1(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[7]] = value else matrix[matIndRow16_3x3[7]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[7]] = value else matrix[matIndRow9_3x3[7]] = value
            }
        }
    }

    fun setZ2(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_3x3[8]] = value else matrix[matIndRow16_3x3[8]] = value
            } else {
                if (isColumnMajor) matrix[matIndCol9_3x3[8]] = value else matrix[matIndRow9_3x3[8]] = value
            }
        }
    }

    fun setX3(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[3]] = value else matrix[matIndRow16_4x4[3]] = value
            }
        }
    }

    fun setY3(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[7]] = value else matrix[matIndRow16_4x4[7]] = value
            }
        }
    }

    fun setZ3(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[11]] = value else matrix[matIndRow16_4x4[11]] = value
            }
        }
    }

    fun setW0(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[12]] = value else matrix[matIndRow16_4x4[12]] = value
            }
        }
    }

    fun setW1(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[13]] = value else matrix[matIndRow16_4x4[13]] = value
            }
        }
    }

    fun setW2(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[14]] = value else matrix[matIndRow16_4x4[14]] = value
            }
        }
    }

    fun setW3(value: Float) {
        if (isMatrixValid) {
            if (matrix.size == 16) {
                if (isColumnMajor) matrix[matIndCol16_4x4[15]] = value else matrix[matIndRow16_4x4[15]] = value
            }
        }
    }

    companion object {
        val matIndCol9_3x3 = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        val matIndCol16_3x3 = intArrayOf(0, 1, 2, 4, 5, 6, 8, 9, 10)
        val matIndRow9_3x3 = intArrayOf(0, 3, 6, 1, 4, 7, 3, 5, 8)
        val matIndRow16_3x3 = intArrayOf(0, 4, 8, 1, 5, 9, 2, 6, 10)
        val matIndCol16_4x4 = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val matIndRow16_4x4 = intArrayOf(0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15)
    }
}