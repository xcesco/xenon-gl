package com.abubusoft.xenon.math

import android.opengl.GLU
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.camera.CameraManager
import java.util.*

/**
 * Classe di utilità per funzioni matematiche.
 *
 * @author xcesco
 */
object XenonMath {
    private const val SIN_BITS = 14 // 16KB. Adjust for accuracy.
    private const val SIN_MASK = (-1 shl SIN_BITS).inv()
    private const val SIN_COUNT = SIN_MASK + 1

    /**
     * costante di moltiplicazione per convertire un angolo misurato in gradi
     * nell'equivalente in radianti.
     *
     *
     * radianti = gradi * PI / 180
     */
    const val DEGREES_TO_RADIANS_FACTOR = (Math.PI / 180.0).toFloat()
    private const val degToIndex = SIN_COUNT / 360.0f
    const val MAX_ABSOLUTE_ERROR = 0.00001f
    const val MAX_RELATIVE_ERROR = 0.01f
    const val PI = Math.PI.toFloat()

    /**
     * PI / 2
     */
    const val PI_HALF = (Math.PI / 2.0).toFloat()

    /**
     * PI * 2
     */
    const val PI_TWICE = (Math.PI * 2.0).toFloat()

    /**
     * fattore di conversione per convertire un radiante in gradi
     *
     *
     * gradi = radianti * 180 / PI
     */
    const val RADIANS_DEGREES_FACTOR = (180.0 / Math.PI).toFloat()
    private const val radToIndex = SIN_COUNT / PI_TWICE
    private const val SGN_MASK_FLOAT = -0x80000000

    /**
     *
     *
     * Restituisce valore assoluto di un numero.
     *
     *
     * @param value in ingresso
     * @return valore assoluto
     */
    @JvmStatic
    fun abs(value: Float): Float {
        return if (value < 0) -value else value
    }

    /**
     *
     *
     * Verifica se value rientra nell'intervallo chiuso min - max. Se è < min
     * allora = min. Se > max allora = max. Se il valore si trova
     * nell'intervallo, non viene alternato.
     *
     *
     *
     * <pre>
     * min(max(startX, minVal), maxVal)
    </pre> *
     *
     * @param value valore da limitare
     * @param min   limite inferiore
     * @param max   limite superiore
     * @return numero compreso tra [min, max]
     */
    @JvmStatic
    fun clamp(value: Float, min: Float, max: Float): Float {
        var value = value
        return if ((if (value > max) max.also { value = it } else value) < min) min else value
        //return Math.max(min, Math.min(max, value));
    }

    /**
     *
     *
     * Verifica se value rientra nell'intervallo chiuso min - max. Se è < min
     * allora = min. Se > max allora = max. Se il valore si trova
     * nell'intervallo, non viene alternato.
     *
     *
     *
     *
     *
     * Metodo che lavora con integer.
     *
     *
     *
     * <pre>
     * min(max(startX, minVal), maxVal)
    </pre> *
     *
     * @param value valore da limitare
     * @param min   limite inferiore
     * @param max   limite superiore
     * @return numero compreso tra [min, max]
     */
    @JvmStatic
    fun clampI(value: Int, min: Int, max: Int): Int {
        var value = value
        return if ((if (value > max) max.also { value = it } else value) < min) min else value
        //return Math.max(min, Math.min(max, value));
    }

    /**
     * Dato un punto definito sullo schermo, questo metodo provvede a
     * convertirlo in punto nello spazio 3D avente come Z = -zDistance rispetto
     * alla telecamera.
     *
     * Il sistema di coordinate dello schermo è quello di default, in alto a
     * sinistra.
     *
     * Questo metodo è da usare negli eventi di input o dove c'è un potenziale
     * multithread, dato che utilizza un array interno che viene istanziato ogni
     * volta
     *
     * @param camera    camera da usare
     * @param screenX   larghezza screen
     * @param screenY   altezza screen
     * @param zDistance distanza dalla telecamera
     * @return worldPoint con z=1 rispetto alla telecamera
     */
    @JvmStatic
    fun convertViewToWorld(camera: Camera, screenX: Float, screenY: Float, zDistance: Float): Point3 {
        val pointInPlanes = FloatArray(16)
        var temp: Float
        GLU.gluUnProject(
            screenX, camera.info.viewport[3] - screenY, 0f, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
            camera.info.viewport, 0, pointInPlanes, 0
        )
        // fix
        if (pointInPlanes[3] != 0f) {
            temp = 1f / pointInPlanes[3]
            pointInPlanes[0] = pointInPlanes[0] * temp
            pointInPlanes[1] = pointInPlanes[1] * temp
            pointInPlanes[2] = pointInPlanes[2] * temp
        }
        val point0 = Point3.set(pointInPlanes[0], pointInPlanes[1], pointInPlanes[2])
        GLU.gluUnProject(
            screenX, camera.info.viewport[3] - screenY, 1f, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
            camera.info.viewport, 0, pointInPlanes, 0
        )
        // fix
        if (pointInPlanes[3] != 0f) {
            temp = 1f / pointInPlanes[3]
            pointInPlanes[0] = pointInPlanes[0] * temp
            pointInPlanes[1] = pointInPlanes[1] * temp
            pointInPlanes[2] = pointInPlanes[2] * temp
        }
        // Point3 point1 = Point3.set(pointInPlanes[0], pointInPlanes[1],
        // pointInPlanes[2]);

        // float perc = zDistance / (camera.info.zFar - camera.info.zNear);
        val perc = zDistance * camera.info.zInverseFrustmDepthFactor

        // ray vector
        point0.setCoords(
            point0.x + perc * (pointInPlanes[0] - point0.x), point0.y + perc * (pointInPlanes[1] - point0.y), point0.z + perc
                    * (pointInPlanes[2] - point0.z)
        )
        return point0
    }

    /**
     * [.convertViewToWorld]
     */
    @JvmStatic
    fun convertViewToWorld(screenX: Float, screenY: Float, zDistance: Float): Point3 {
        // usiamo la camera di default.
        return convertViewToWorld(CameraManager.camera, screenX, screenY, zDistance)
    }

    /**
     *
     *
     * **Versione Single Thread per
     * [.convertViewToWorld].**
     *
     */
    @JvmStatic
    fun convertViewToWorldST(camera: Camera, worldPoint: Point3, tempPointInPlanes: FloatArray, screenX: Float, screenY: Float, zDistance: Float): Point3 {
        var temp: Float
        GLU.gluUnProject(
            screenX, camera.info.viewport[3] - screenY, 0f, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
            camera.info.viewport, 0, tempPointInPlanes, 0
        )
        // fix
        if (tempPointInPlanes[3] != 0f) {
            temp = 1f / tempPointInPlanes[3]
            tempPointInPlanes[0] = tempPointInPlanes[0] * temp
            tempPointInPlanes[1] = tempPointInPlanes[1] * temp
            tempPointInPlanes[2] = tempPointInPlanes[2] * temp
        }
        // Point3 point0 = Point3.set(tempPointInPlanes[0],
        // tempPointInPlanes[1], tempPointInPlanes[2]);
        worldPoint.setCoords(tempPointInPlanes[0], tempPointInPlanes[1], tempPointInPlanes[2])
        GLU.gluUnProject(
            screenX, camera.info.viewport[3] - screenY, 1f, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
            camera.info.viewport, 0, tempPointInPlanes, 0
        )
        // fix
        if (tempPointInPlanes[3] != 0f) {
            temp = 1f / tempPointInPlanes[3]
            tempPointInPlanes[0] = tempPointInPlanes[0] * temp
            tempPointInPlanes[1] = tempPointInPlanes[1] * temp
            tempPointInPlanes[2] = tempPointInPlanes[2] * temp
        }

        // float perc = zDistance / (camera.info.zFar - camera.info.zNear);
        val perc = zDistance * camera.info.zInverseFrustmDepthFactor

        // Point3 point1 = Point3.set(tempPointInPlanes[0],
        // tempPointInPlanes[1], tempPointInPlanes[2]);

        // ray vector (tutto con un punto)
        worldPoint.setCoords(
            worldPoint.x + perc * (tempPointInPlanes[0] - worldPoint.x), worldPoint.y + perc * (tempPointInPlanes[1] - worldPoint.y),
            worldPoint.z + perc * (tempPointInPlanes[2] - worldPoint.z)
        )
        return worldPoint
    }

    /**
     * [.convertViewToWorld]
     */
    fun convertViewToWorldST(worldPoint: Point3, tempPointInPlanes: FloatArray, screenX: Float, screenY: Float, zDistance: Float): Point3 {
        // usiamo la camera di default.
        return convertViewToWorldST(CameraManager.camera, worldPoint, tempPointInPlanes, screenX, screenY, zDistance)
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    fun cos(radians: Float): Float {
        return Sin.table[((radians + PI / 2) * radToIndex).toInt() and SIN_MASK]
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    fun cosDeg(degrees: Float): Float {
        return Sin.table[((degrees + 90) * degToIndex).toInt() and SIN_MASK]
    }

    /**
     * Dato un numero intero, trova la potenza di 2 più vicina.
     *
     * @param n
     * @return
     */
    fun findNearestPowerOf2(n: Int): Int {
        val higher = findNextPowerOf2(n)
        val lower = higher / 2

        // se la distanza tra lower e n è minore rispetto
        // alla distanza tra higher ed n, allora optiamo per lower
        return if (n - lower <= higher - n) {
            lower
        } else higher
    }

    /**
     * Trova il prossimo numero potenza di 10, appena superiore al numero
     *
     * @param n
     * @return
     */
    fun findNextPowerOf10(n: Int): Int {
        var n = n
        var x = 1
        while (n > 0) {
            n /= 10
            x *= 10
        }
        return x
    }

    /**
     * Trova il prossimo numero intero potenza di 2, appena superiore al numero
     * dato
     *
     * @param n
     * @return
     */
    fun findNextPowerOf2(n: Int): Int {
        var n = n
        var x = 1
        while (n > 0) {
            n /= 2
            x *= 2
        }
        return x
    }

    /**
     *
     *
     * Compara due float come [qui](http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm)
     *
     *
     * @param a
     * @param b
     * @return
     */
    fun isEquals(a: Float, b: Float): Boolean {
        if (java.lang.Float.isNaN(a) || java.lang.Float.isNaN(b)) return false
        if (abs(a - b) < MAX_ABSOLUTE_ERROR) return true
        val relativeError: Float
        relativeError = if (abs(b) > abs(a)) abs((a - b) / b) else abs((a - b) / a)
        return if (relativeError <= MAX_RELATIVE_ERROR) true else false
    }

    /**
     * startX >= startY
     *
     * @param startX
     * @param startY
     * @return
     */
    fun isGreatEquals(x: Float, y: Float): Boolean {
        val maxUlps = 5
        var xInt = java.lang.Float.floatToIntBits(x)
        var yInt = java.lang.Float.floatToIntBits(y)
        if (xInt < 0) xInt = SGN_MASK_FLOAT - xInt
        if (yInt < 0) yInt = SGN_MASK_FLOAT - yInt
        val isGreater = xInt - yInt >= maxUlps
        return isGreater && !java.lang.Float.isNaN(x) && !java.lang.Float.isNaN(y)
    }

    /**
     * startX > startY
     *
     * @param startX
     * @param startY
     * @return
     */
    fun isGreater(x: Float, y: Float): Boolean {
        val maxUlps = 5
        var xInt = java.lang.Float.floatToIntBits(x)
        var yInt = java.lang.Float.floatToIntBits(y)
        if (xInt < 0) xInt = SGN_MASK_FLOAT - xInt
        if (yInt < 0) yInt = SGN_MASK_FLOAT - yInt
        val isGreater = xInt - yInt > maxUlps
        return isGreater && !java.lang.Float.isNaN(x) && !java.lang.Float.isNaN(y)
    }

    /**
     * startX < startY
     *
     * @param startX
     * @param startY
     * @return
     */
    fun isLess(x: Float, y: Float): Boolean {
        val maxUlps = 5
        var xInt = java.lang.Float.floatToIntBits(x)
        var yInt = java.lang.Float.floatToIntBits(y)
        if (xInt < 0) xInt = SGN_MASK_FLOAT - xInt
        if (yInt < 0) yInt = SGN_MASK_FLOAT - yInt
        val isGreater = yInt - xInt > maxUlps
        return isGreater && !java.lang.Float.isNaN(x) && !java.lang.Float.isNaN(y)
    }

    /**
     * startX <= startY
     *
     * @param startX
     * @param startY
     * @return
     */
    fun isLessEquals(x: Float, y: Float): Boolean {
        val maxUlps = 5
        var xInt = java.lang.Float.floatToIntBits(x)
        var yInt = java.lang.Float.floatToIntBits(y)
        if (xInt < 0) xInt = SGN_MASK_FLOAT - xInt
        if (yInt < 0) yInt = SGN_MASK_FLOAT - yInt
        val isGreater = yInt - xInt >= maxUlps
        return isGreater && !java.lang.Float.isNaN(x) && !java.lang.Float.isNaN(y)
    }

    /**
     * Restituisce true se il numero passato come argomento è una potenza di 2.
     *
     *
     * Vedi
     * http://stackoverflow.com/questions/600293/how-to-check-if-a-number-is
     * -a-power-of-2
     *
     *
     *
     *
     * First and foremost the bitwise binary & operator from MSDN definition:
     *
     *
     *
     * Binary & operators are predefined for the integral types and bool. For
     * integral types, & computes the logical bitwise AND of its operands. For
     * bool operands, & computes the logical AND of its operands; that is, the
     * result is true if and only if both its operands are true.
     *
     *
     *
     * Now let's take a look at how this all plays out:
     *
     *
     *
     * The function returns boolean (true / false) and accepts one incoming
     * parameter of allocation unsigned long (startX, in this case). Let us for
     * the sake of simplicity assume that someone has passed the value 4 and
     * called the function like so:
     *
     * `bool b = IsPowerOfTwo(4)`
     *
     *
     * Now we replace each occurrence of startX with 4:
     *
     *
     *
     * <pre>
     * return (4 != 0) &amp;&amp; ((4 &amp; (4 - 1)) == 0);
    </pre> *
     *
     *
     * Well we already know that 4 != 0 evals to true, so far so good. But what
     * about:
     *
     *
     *
     * <pre>
     * ((4 &amp; (4 - 1)) == 0)
    </pre> *
     *
     *
     * This translates to this of course:
     *
     *
     *
     * <pre>
     * ((4 &amp; 3) == 0)
    </pre> *
     *
     *
     * But what exactly is 4&3? The binary representation of 4 is 100 and the
     * binary representation of 3 is 011 (remember the & takes the binary
     * representation of these numbers. So we have:
     *
     *
     *
     * <pre>
     * 100 = 4
     * 011 = 3
    </pre> *
     *
     *
     * Imagine these values being stacked up much like elementary addition. The
     * & operator says that if both values are equal to 1 then the result is 1,
     * otherwise it is 0. So 1 & 1 = 1, 1 & 0 = 0, 0 & 0 = 0, and 0 & 1 = 0. So
     * we do the math:
     *
     *
     *
     * <pre>
     * 100
     * 011
     * ----
     * 000
    </pre> *
     *
     *
     * The result is simply 0. So we go back and look at what our return
     * statement now translates to:
     *
     *
     *
     * <pre>
     * return (4 != 0) &amp;&amp; ((4 &amp; 3) == 0);
    </pre> *
     *
     *
     * Which translates now to:
     *
     *
     *
     * <pre>
     * return true &amp;&amp; (0 == 0);
     * return true &amp;&amp; true;
    </pre> *
     *
     *
     * We all know that true && true is simply true, and this shows that for our
     * example, 4 is a power of 2.
     *
     *
     * @param n
     * @return
     */
    fun isPowerOfTwo(n: Int): Boolean {
        return n != 0 && n and n - 1 == 0
    }

    /**
     * Massimo valore
     *
     * @param a
     * @param b
     * @return
     */
    fun max(a: Float, b: Float): Float {
        return if (a > b) a else b
    }

    /**
     * Massimo valore
     *
     * @param a
     * @param b
     * @return
     */
    fun max(a: Int, b: Int): Int {
        return if (a > b) a else b
    }

    /**
     * Minimo valore
     *
     * @param a
     * @param b
     * @return
     */
    fun min(a: Float, b: Float): Float {
        return if (a < b) a else b
    }

    /**
     * Returns the largest (closest to positive infinity)
     * `int` value that is less than or equal to the algebraic quotient.
     * There is one special case, if the dividend is the
     * [Integer.MIN_VALUE] and the divisor is `-1`,
     * then integer overflow occurs and
     * the result is equal to the `Integer.MIN_VALUE`.
     *
     *
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     *
     *  * If the signs of the arguments are the same, the results of
     * `floorDiv` and the `/` operator are the same.  <br></br>
     * For example, `floorDiv(4, 3) == 1` and `(4 / 3) == 1`.
     *  * If the signs of the arguments are different,  the quotient is negative and
     * `floorDiv` returns the integer less than or equal to the quotient
     * and the `/` operator returns the integer closest to zero.<br></br>
     * For example, `floorDiv(-4, 3) == -2`,
     * whereas `(-4 / 3) == -1`.
     *
     *
     *
     *
     *
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * `int` value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor `y` is zero
     * @since 1.8
     */
    fun floorDiv(x: Int, y: Int): Int {
        var r = x / y
        // if the signs are different and modulo not zero, round down
        if (x xor y < 0 && r * y != x) {
            r--
        }
        return r
    }

    /**
     * Minimo valore
     *
     * @param a
     * @param b
     * @return
     */
    fun min(a: Int, b: Int): Int {
        return if (a < b) a else b
    }

    /**
     * Potenza di due
     *
     * @param value
     * @return
     */
    @JvmStatic
    fun power2(value: Float): Float {
        return value * value
    }

    @JvmStatic
    fun power2I(value: Int): Int {
        return value * value
    }

    /**
     * Converte in radianti un angolo espresso in gradi. Si può utilizzare
     * questo metodo oppure di può direttamente moltiplicare i gradi per la
     * costante [.DEGREES_TO_RADIANS_FACTOR].
     *
     * @param angleInDegree
     * @return
     */
    fun radians(angleInDegree: Float): Float {
        return angleInDegree * DEGREES_TO_RADIANS_FACTOR
    }

    /**
     *
     *
     * Restituisce il segno del numero: -1 o 1.
     *
     *
     * @param value
     * @return
     */
    fun sign(value: Float): Float {
        return if (value < 0f) -1f else 1f
    }

    /**
     *
     *
     * Restituisce il segno del numero: -1 o 1.
     *
     *
     * @param value
     * @return
     */
    fun sign(value: Int): Int {
        return if (value < 0) -1 else 1
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    fun sin(radians: Float): Float {
        return Sin.table[(radians * radToIndex).toInt() and SIN_MASK]
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    fun sinDeg(degrees: Float): Float {
        return Sin.table[(degrees * degToIndex).toInt() and SIN_MASK]
    }

    /**
     * Radice quadrata
     *
     * @param value
     * @return
     */
    fun sqrt(value: Float): Float {
        return Math.sqrt(value.toDouble()).toFloat()
    }

    /**
     * Calcola la distanza al quadrato di un punto dall'origine del suo sistema
     * di riferimento. Viene calcolato al quadrato per questioni di performace
     *
     * @param startX
     * @param startY
     * @param z
     * @return
     */
    fun squareDistanceFromOrigin(x: Float, y: Float, z: Float): Float {
        return x * x + y * y + z * z
    }

    /**
     * Dato un quadrato nello spazio 3D, perfettamente perpendicolare rispetto
     * alla telecamera, questa funzione permette di calcolare l'esatta distanza
     * dalla telecamera affinchè tale quadrato abbia un lato che vada a
     * coincidere con un lato dello schermo.
     *
     *
     * Quale sia il lato dello schermo, quello più o quello più piccolo, dipende
     * dalla configurazione della proiezione.
     *
     *
     * Questa funzione riceve in ingresso diversi parametri globali
     *
     *
     * [CameraInfo]
     *
     * @param squareWidth
     * @return
     */
    fun zDistanceForSquare(camera: Camera, squareWidth: Float): Float {
        val angle = (camera.info.fieldOfView * DEGREES_TO_RADIANS_FACTOR * 0.5f).toDouble()
        val zFactor = (1.0 / Math.tan(angle)).toFloat() * 0.5f

        // final float ZFACTOR = 1.732f / 2.0f;
        // Scene.distanceZ =squareWidth * ZFACTOR;

        // zFactor per la metà della larghezza
        return zFactor * squareWidth
    }

    // ---
    var random: Random = RandomXS128()

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (inclusive).
     */
    fun random(range: Int): Int {
        return random.nextInt(range + 1)
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    fun random(start: Int, end: Int): Int {
        return start + random.nextInt(end - start + 1)
    }

    /**
     * Returns a random boolean value.
     */
    fun randomBoolean(): Boolean {
        return random.nextBoolean()
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified
     * value.
     */
    fun randomBoolean(chance: Float): Boolean {
        return random() < chance
    }

    /**
     * Returns random number between 0.0 (inclusive) and 1.0 (exclusive).
     */
    fun random(): Float {
        return random.nextFloat()
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (exclusive).
     */
    fun random(range: Float): Float {
        return random.nextFloat() * range
    }

    /**
     * Returns a random number between start (inclusive) and end (exclusive).
     */
    fun random(start: Float, end: Float): Float {
        return start + random.nextFloat() * (end - start)
    }

    /**
     * Returns -1 or 1, randomly.
     */
    fun randomSign(): Int {
        return 1 or (random.nextInt() shr 31)
    } // ---

    private object Sin {
        val table = FloatArray(SIN_COUNT)

        init {
            for (i in 0 until SIN_COUNT) table[i] = Math.sin(((i + 0.5f) / SIN_COUNT * PI_TWICE).toDouble()).toFloat()
            var i = 0
            while (i < 360) {
                table[(i * degToIndex).toInt() and SIN_MASK] = Math.sin((i * DEGREES_TO_RADIANS_FACTOR).toDouble()).toFloat()
                i += 90
            }
        }
    }
}