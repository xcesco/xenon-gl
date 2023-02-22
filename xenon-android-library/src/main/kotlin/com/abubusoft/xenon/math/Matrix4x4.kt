package com.abubusoft.xenon.math

import android.opengl.Matrix
import com.abubusoft.xenon.engine.SharedData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *
 *
 * Matrice per le trasformazioni in opengl. Opengl usa il sistema right handed.
 *
 * [vedi wiki Matrix4x4](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4)
 *
 * @author Francesco Benincasa
 */
class Matrix4x4 : SharedData {
    /**
     *
     *
     * Tipo di rotazione. Ne esiste una per ogni asse.
     *
     * [vedi wiki.](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#rotationtype)
     *
     */
    enum class RotationType {
        /**
         *
         *
         * Rotazione attorno asse X
         *
         */
        ROTATION_X,

        /**
         *
         *
         * Rotazione attorno asse Y
         *
         */
        ROTATION_Y,

        /**
         *
         *
         * Rotazione attorno asse Z
         *
         */
        ROTATION_Z
    }

    /**
     * matrice
     */
    protected var matrix = FloatArray(16)

    /**
     * buffer nativo per importare le matrici negli shader
     */
    private var matrixFloatBuffer: FloatBuffer? = null

    /**
     * matrice di appoggio per le moltiplicazioni
     */
    private val tempMultiplyMatrix = FloatArray(16)

    /**
     * Aggiorna il float buffer e lo restituisce come valore di ritorno del
     *
     * @return floatbuffer
     */
    fun asFloatBuffer(): FloatBuffer? {
        if (matrixFloatBuffer == null) {
            matrixFloatBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }
        if (!isLocked) {
            // nel caso in cui non sia posizionato all'inizio lo posizioniamo manualmente
            // if (matrixFloatBuffer.position()!=0) matrixFloatBuffer.position(0);
            matrixFloatBuffer!!.put(matrix).position(0)
        }
        return matrixFloatBuffer
    }
    /**
     * Verifica stato di lock
     */
    /**
     *
     *
     * Se true il floatBuffer non viene aggiornato quando si usa il metodo asFloatBuffer.
     *
     */
    var isLocked = false
        protected set

    /**
     * Blocca il contenuto del float buffer
     */
    fun lock() {
        // effetuaimo l'unlock, copiamo i dati nel float buffer e rimettiamo il lock
        isLocked = false
        asFloatBuffer()
        isLocked = true
    }

    /**
     * sblocca tutto
     */
    fun unlock() {
        // effetuaimo l'unlock, copiamo i dati nel float buffer e rimettiamo il lock
        isLocked = false
    }

    /**
     * Crea una matrice per il frustum associato.
     *
     * <a href="https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#buildfrustummatrix">Vedi wiki</a>
     *
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param nearZ
     * @param farZ
     */
    private fun buildFrustumMatrix(left: Float, right: Float, bottom: Float, top: Float, nearZ: Float, farZ: Float) {
        require(left != right) { "left == right" }
        require(top != bottom) { "top == bottom" }
        require(nearZ != farZ) { "near == far" }
        require(nearZ > 0.0f) { "near <= 0.0f" }
        require(farZ > 0.0f) { "far <= 0.0f" }
        val r_width = 1.0f / (right - left)
        val r_height = 1.0f / (top - bottom)
        val r_depth = 1.0f / (nearZ - farZ)
        val x = 2.0f * (nearZ * r_width)
        val y = 2.0f * (nearZ * r_height)
        val A = (right + left) * r_width
        val B = (top + bottom) * r_height
        val C = (farZ + nearZ) * r_depth
        val D = 2.0f * (farZ * nearZ * r_depth)
        matrix[0] = x
        matrix[5] = y
        matrix[8] = A
        matrix[9] = B
        matrix[10] = C
        matrix[14] = D
        matrix[11] = -1.0f
        matrix[1] = 0.0f
        matrix[2] = 0.0f
        matrix[3] = 0.0f
        matrix[4] = 0.0f
        matrix[6] = 0.0f
        matrix[7] = 0.0f
        matrix[12] = 0.0f
        matrix[13] = 0.0f
        matrix[15] = 0.0f

        // multiply(frust, matrix);
    }

    /**
     * Crea una matrice d'identità.
     */
    fun buildIdentityMatrix() {
        buildIdentityMatrix(matrix)
        // buildIdentityMatrix(tempMatrix);
        // buildIdentityMatrix(tempMultiplyMatrix);
    }

    /**
     * Crea una copia della matrice data come parametro.
     */
    fun build(origin: Matrix4x4) {
        for (i in matrix.indices) {
            matrix[i] = origin.matrix[i]
        }
    }

    /**
     * Define a viewing transformation in terms of an eye point, a center of view, and an up vector.
     *
     * @param eyeX
     * eye point X
     * @param eyeY
     * eye point Y
     * @param eyeZ
     * eye point Z
     * @param centerX
     * center of view X
     * @param centerY
     * center of view Y
     * @param centerZ
     * center of view Z
     * @param upX
     * up vector X
     * @param upY
     * up vector Y
     * @param upZ
     * up vector Z
     */
    fun buildLookAtMatrix(eyeX: Float, eyeY: Float, eyeZ: Float, centerX: Float, centerY: Float, centerZ: Float, upX: Float, upY: Float, upZ: Float) {

        // See the OpenGL GLUT documentation for gluLookAt for a description
        // of the algorithm. We implement it in a straightforward way:
        var fx = centerX - eyeX
        var fy = centerY - eyeY
        var fz = centerZ - eyeZ

        // Normalize f
        val rlf = 1.0f / Matrix.length(fx, fy, fz)
        fx *= rlf
        fy *= rlf
        fz *= rlf

        // compute s = f startX up (startX means "cross product")
        var sx = fy * upZ - fz * upY
        var sy = fz * upX - fx * upZ
        var sz = fx * upY - fy * upX

        // and normalize s
        val rls = 1.0f / Matrix.length(sx, sy, sz)
        sx *= rls
        sy *= rls
        sz *= rls

        // compute u = s startX f
        val ux = sy * fz - sz * fy
        val uy = sz * fx - sx * fz
        val uz = sx * fy - sy * fx
        matrix[0] = sx
        matrix[1] = ux
        matrix[2] = -fx
        matrix[3] = 0.0f
        matrix[4] = sy
        matrix[5] = uy
        matrix[6] = -fy
        matrix[7] = 0.0f
        matrix[8] = sz
        matrix[9] = uz
        matrix[10] = -fz
        matrix[11] = 0.0f
        matrix[12] = 0.0f
        matrix[13] = 0.0f
        matrix[14] = 0.0f
        matrix[15] = 1.0f
        translate(1f, -eyeX, -eyeY, -eyeZ)
    }

    /**
     * Costruisce una matrice di proiezione ortogonale
     *
     *
     * <a href="https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#buildfrustummatrix">Vedi wiki</a>
     *
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param nearZ
     * @param farZ
     */
    fun buildOrthoProjectionMatrix(left: Float, right: Float, bottom: Float, top: Float, nearZ: Float, farZ: Float) {
        val deltaX = right - left
        val deltaY = top - bottom
        val deltaZ = farZ - nearZ
        buildIdentityMatrix()
        if (deltaX == 0.0f || deltaY == 0.0f || deltaZ == 0.0f) return
        matrix[0 * 4 + 0] = 2.0f / deltaX
        matrix[1 * 4 + 1] = 2.0f / deltaY
        matrix[2 * 4 + 2] = -2.0f / deltaZ
        matrix[3 * 4 + 0] = -(right + left) / deltaX
        matrix[3 * 4 + 1] = -(top + bottom) / deltaY
        matrix[3 * 4 + 2] = -(nearZ + farZ) / deltaZ

        // multiply(tempMatrix, matrix);
    }

    /**
     *
     *
     * Costruire una matrice di proiezione.
     *
     * [Vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#buildperspectiveprojectionmatrix)
     *
     * @param fieldOfView
     * @param aspect
     * @param nearZ
     * @param farZ
     */
    fun buildPerspectiveProjectionMatrix(fieldOfView: Float, aspect: Float, nearZ: Float, farZ: Float) {
        val frustumW: Float
        var frustumH: Float
        frustumH = Math.tan((fieldOfView * XenonMath.DEGREES_TO_RADIANS_FACTOR).toDouble()).toFloat() * nearZ
        if (aspect <= 1.0f) {
            frustumW = frustumH * aspect
        } else {
            frustumW = frustumH
            frustumH = frustumH / aspect
        }

        // Matrix.frustumM(m, offset, left, right, bottom, top, near, far)
        buildIdentityMatrix()
        buildFrustumMatrix(-frustumW, frustumW, -frustumH, frustumH, nearZ, farZ)
    }

    /**
     *
     *
     * Imposta la matrice come matrice di rotazione. L'angolo è espresso in gradi.
     *
     *
     * [Vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#buildrotationmatrix)
     *
     * @see RotationType
     *
     *
     * @param rotation
     * tipo di rotazione
     * @param angle
     * angolo di rotazione in gradi.
     */
    fun buildRotationMatrix(rotation: RotationType, angle: Float) {
        buildRotationMatrix(matrix, rotation, angle)
    }

    /**
     * Definisce una matrice che effettua lo scaling.
     *
     * Per ogni dimensione definiamo il fattore di scala.
     *
     * <a href="https://github.com/xcesco/xenon-gl/wiki/Matrix4x4#scaling">vedi wiki</a>
     *
     * @param sx
     * @param sy
     * @param sz
     */
    fun buildScaleMatrix(sx: Float, sy: Float, sz: Float) {
        buildIdentityMatrix()
        matrix[0 * 4 + 0] *= sx
        matrix[1 * 4 + 1] *= sy
        matrix[2 * 4 + 2] *= sz
    }

    /**
     * Moltiplica la matrice attuale per una matrice di scala
     *
     * @param sx
     * @param sy
     * @param sz
     */
    fun scale(sx: Float, sy: Float, sz: Float) {
        Matrix.scaleM(matrix, 0, sx, sy, sz)
    }

    /**
     * Moltiplica la matrice attuale per una matrice di scala. Se 1 lascia tutto inalterato
     *
     * @param scale
     */
    fun scale(scale: Float) {
        Matrix.scaleM(matrix, 0, scale, scale, scale)
    }

    /**
     * Imposta la matrice come matrice di traslazione. Viene prima creata una matrice identità e poi vengono impostati i parametri di traslazione.
     *
     * @param tx
     * fattore di traslazione startX
     * @param ty
     * fattore di traslazione startY
     * @param tz
     * fattore di traslazione z
     */
    fun buildTranslationMatrix(tx: Float, ty: Float, tz: Float) {
        buildTranslationMatrix(1f, tx, ty, tz)
    }

    /**
     *
     *
     * Imposta la matrice come matrice di traslazione. Viene prima creata una matrice identità e poi vengono impostati i parametri di traslazione.
     *
     *
     * <pre>
     * * * * tx
     * * * * ty
     * * * * tz
     * * * * 1
    </pre> *
     *
     * @param module
     * traslazione
     * @param tx
     * fattore di traslazione startX
     * @param ty
     * fattore di traslazione startY
     * @param tz
     * fattore di traslazione z
     */
    fun buildTranslationMatrix(module: Float, tx: Float, ty: Float, tz: Float) {
        buildIdentityMatrix()
        Matrix.translateM(matrix, 0, module * tx, module * ty, module * tz)
    }

    fun get(): FloatArray {
        return matrix
    }

    /**
     * Moltiplica questa matrice con quella passata come argomento.
     *
     * `this = this * matrixB`
     *
     * Viene usata la matrice temporanea tempMultiplyMatrix
     *
     * @param matrixB
     */
    fun multiply(matrixB: Matrix4x4) {
        multiply(this, this, matrixB)
    }

    /**
     *
     *
     * Effettua la moltiplicazione della matrice per un vettore
     *
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#multiply)
     *
     * @param vectorInput
     * @param vectorOutput
     */
    fun multiply(vectorInput: Vector3, vectorOutput: Vector3) {
        vectorOutput.x = vectorInput.x * matrix[0] + vectorInput.y * matrix[4] + vectorInput.z * matrix[8] + matrix[12]
        vectorOutput.y = vectorInput.x * matrix[1] + vectorInput.y * matrix[5] + vectorInput.z * matrix[9] + matrix[13]
        vectorOutput.z = vectorInput.x * matrix[2] + vectorInput.y * matrix[6] + vectorInput.z * matrix[10] + matrix[14]
    }

    /**
     * Effettua la moltiplicazione della matrice per un vettore. Il vettore di float deve essere di dimensioni 3.
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#multiply)
     *
     * @param vectorInput
     * @param vectorOutput
     */
    fun multiply(vectorInput: FloatArray, vectorOutput: FloatArray) {
        vectorOutput[0] = vectorInput[0] * matrix[0] + vectorInput[1] * matrix[4] + vectorInput[2] * matrix[8] + matrix[12]
        vectorOutput[1] = vectorInput[1] * matrix[1] + vectorInput[1] * matrix[5] + vectorInput[2] * matrix[9] + matrix[13]
        vectorOutput[2] = vectorInput[2] * matrix[2] + vectorInput[1] * matrix[6] + vectorInput[2] * matrix[10] + matrix[14]
    }

    /**
     * Effettua la moltiplicazione della matrice per un vettore
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#multiply)
     *
     * @param vectorInput
     */
    fun multiply(vectorInput: Vector3): Vector3 {
        val vectorOutput = Vector3()
        multiply(vectorInput, vectorOutput)
        return vectorOutput
    }

    /**
     * Moltiplica A * B mettendo il risultato nella matrice corrente.
     *
     * `this = A * B`
     *
     * Viene utilizzata una tempMultiplyMatrix dato che le due matrici potrebbero essere questa matrice.
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#multiply)
     *
     * @param m1
     * @param m2
     */
    fun multiply(m1: Matrix4x4, m2: Matrix4x4) {
        multiply(tempMultiplyMatrix, m1.matrix, m2.matrix)
        System.arraycopy(tempMultiplyMatrix, 0, matrix, 0, 16)
    }

    /**
     * Moltiplica la matrice attualmente in corso (M) per la matrice di rotazione (R) i cui parametri sono stati passati a questo metodo.
     *
     * `M'= M startX R`
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#rotate)
     *
     * @see RotationType
     *
     * @param rotation
     * tipo di rotazione
     *
     * @param angle
     * angolo di rotazione in gradi
     */
    fun rotate(rotation: RotationType, angle: Float) {
        rotate(matrix, rotation, angle)
    }

    fun set(value: FloatArray) {
        matrix = value
    }

    /**
     * Trasla la matrice attuale.
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#translate)
     *
     * @param tx
     * fattore di traslazione sull'asse startX
     * @param ty
     * fattore di traslazione sull'asse startY
     * @param tz
     * fattore di traslazione sull'asse z
     */
    fun translate(tx: Float, ty: Float, tz: Float) {
        translate(1f, tx, ty, tz)
    }

    /**
     * Trasla la matrice attuale.
     *
     * [vedi wiki](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#translate)
     *
     * Per traslare solo lungo un asse, basta impostare il modulo ad 1 e mettere il rispettivo fattore alla traslazione desiderata. Si è scelta questa soluzione in quanto è la più flessibile.
     *
     * @param module
     * traslazione
     * @param tx
     * fattore di traslazione sull'asse startX
     * @param ty
     * fattore di traslazione sull'asse startY
     * @param tz
     * fattore di traslazione sull'asse z
     */
    fun translate(module: Float, tx: Float, ty: Float, tz: Float) {
        Matrix.translateM(matrix, 0, module * tx, module * ty, module * tz)
    }

    /**
     * Passa dal buffer usato nella fase LOGIC al floatbuffer usato nella fase RENDER.
     */
    override fun update() {
        val temp = isLocked
        isLocked = false
        asFloatBuffer()
        isLocked = temp
    }

    companion object {
        /**
         * Imposta una matrice come matrice identità.
         *
         * @param m
         * matrice
         */
        protected fun buildIdentityMatrix(m: FloatArray) {
            for (i in 0..15) {
                m[i] = 0f
            }
            var i = 0
            while (i < 16) {
                m[i] = 1.0f
                i += 5
            }
        }

        /**
         * Imposta la matrice `m` come matrice di rotazione. L'angolo è espresso in gradi.
         *
         * @param matrix
         * matrice su cui operare
         * @param angle
         * angolo di rotazione
         * @param rotation
         * tipo di rotazione
         */
        protected fun buildRotationMatrix(matrix: FloatArray, rotation: RotationType, angle: Float) {
            buildIdentityMatrix(matrix)
            rotate(matrix, rotation, angle)
        }

        /**
         * Computes the length of a vector
         *
         * @param x
         * startX coordinate of a vector
         * @param y
         * startY coordinate of a vector
         * @param z
         * z coordinate of a vector
         * @return the length of a vector
         */
        fun length(x: Float, y: Float, z: Float): Float {
            return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        }

        /**
         *
         *
         * Moltiplica la matrice sourceA per sourceB e mette il contenuto in desMatrix.
         *
         *
         * `destMatrix = sourceA * sourceB`
         *
         * @param destMatrix
         * @param sourceA
         * @param sourceB
         */
        fun multiply(destMatrix: Matrix4x4, sourceA: Matrix4x4, sourceB: Matrix4x4) {
            multiply(destMatrix.tempMultiplyMatrix, sourceA.matrix, sourceB.matrix)
            System.arraycopy(destMatrix.tempMultiplyMatrix, 0, destMatrix.matrix, 0, 16)
        }

        /**
         * Moltiplica la matrice attualmente in corso (M) per la matrice di rotazione (R) i cui parametri sono stati passati a questo metodo. Gli angoli sono espressi in gradi.
         *
         * `M'= M startX R`
         *
         * [Vedi wiki.](https://github.com/xcesco/xenon-gl/wiki/Matrix4x4/_edit#rotatefloat-matrix-rotationtype-rotation-float-angle)
         *
         * @see RotationType
         *
         *
         * @param rotation
         * tipo di rotazione
         *
         * @param angle
         * angolo di rotazione in gradi.
         */
        fun rotate(matrix: FloatArray, rotation: RotationType, angle: Float) {
            // gli angoli rimangono in gradi.
            when (rotation) {
                RotationType.ROTATION_X -> Matrix.rotateM(matrix, 0, angle, 1f, 0f, 0f)
                RotationType.ROTATION_Y ->            // rotazione startY
                    Matrix.rotateM(matrix, 0, angle, 0f, 1f, 0f)
                RotationType.ROTATION_Z ->            // rotazione z
                    Matrix.rotateM(matrix, 0, angle, 0f, 0f, 1f)
            }
        }

        /**
         * Moltiplica due matrici. Usa una matrice temporanea per evitare che un uso della matrice interna di questa classe come parametro della funzioni generi problemi.
         *
         * `result = m1 * m2`
         *
         * @param m1
         * @param m2
         * @param result
         */
        private fun multiply(result: FloatArray, m1: FloatArray, m2: FloatArray) {
            // Matrix.multiplyMM(result, 0, m1, 0, m2, 0);
            result[0 + 0] = m1[0 + 0] * m2[0] + m1[0 + 4] * m2[1] + m1[0 + 8] * m2[2] + m1[0 + 12] * m2[3]
            result[0 + 4] = m1[0 + 0] * m2[4 + 0] + m1[0 + 4] * m2[4 + 1] + m1[0 + 8] * m2[4 + 2] + m1[0 + 12] * m2[4 + 3]
            result[0 + 8] = m1[0 + 0] * m2[8 + 0] + m1[0 + 4] * m2[8 + 1] + m1[0 + 8] * m2[8 + 2] + m1[0 + 12] * m2[8 + 3]
            result[0 + 12] = m1[0 + 0] * m2[12 + 0] + m1[0 + 4] * m2[12 + 1] + m1[0 + 8] * m2[12 + 2] + m1[0 + 12] * m2[12 + 3]
            result[1 + 0] = m1[1 + 0] * m2[0] + m1[1 + 4] * m2[1] + m1[1 + 8] * m2[2] + m1[1 + 12] * m2[3]
            result[1 + 4] = m1[1 + 0] * m2[4 + 0] + m1[1 + 4] * m2[4 + 1] + m1[1 + 8] * m2[4 + 2] + m1[1 + 12] * m2[4 + 3]
            result[1 + 8] = m1[1 + 0] * m2[8 + 0] + m1[1 + 4] * m2[8 + 1] + m1[1 + 8] * m2[8 + 2] + m1[1 + 12] * m2[8 + 3]
            result[1 + 12] = m1[1 + 0] * m2[12 + 0] + m1[1 + 4] * m2[12 + 1] + m1[1 + 8] * m2[12 + 2] + m1[1 + 12] * m2[12 + 3]
            result[2 + 0] = m1[2 + 0] * m2[0] + m1[2 + 4] * m2[1] + m1[2 + 8] * m2[2] + m1[2 + 12] * m2[3]
            result[2 + 4] = m1[2 + 0] * m2[4 + 0] + m1[2 + 4] * m2[4 + 1] + m1[2 + 8] * m2[4 + 2] + m1[2 + 12] * m2[4 + 3]
            result[2 + 8] = m1[2 + 0] * m2[8 + 0] + m1[2 + 4] * m2[8 + 1] + m1[2 + 8] * m2[8 + 2] + m1[2 + 12] * m2[8 + 3]
            result[2 + 12] = m1[2 + 0] * m2[12 + 0] + m1[2 + 4] * m2[12 + 1] + m1[2 + 8] * m2[12 + 2] + m1[2 + 12] * m2[12 + 3]
            result[3 + 0] = m1[3 + 0] * m2[0] + m1[3 + 4] * m2[1] + m1[3 + 8] * m2[2] + m1[3 + 12] * m2[3]
            result[3 + 4] = m1[3 + 0] * m2[4 + 0] + m1[3 + 4] * m2[4 + 1] + m1[3 + 8] * m2[4 + 2] + m1[3 + 12] * m2[4 + 3]
            result[3 + 8] = m1[3 + 0] * m2[8 + 0] + m1[3 + 4] * m2[8 + 1] + m1[3 + 8] * m2[8 + 2] + m1[3 + 12] * m2[8 + 3]
            result[3 + 12] = m1[3 + 0] * m2[12 + 0] + m1[3 + 4] * m2[12 + 1] + m1[3 + 8] * m2[12 + 2] + m1[3 + 12] * m2[12 + 3]
        }
    }
}