package com.abubusoft.xenon.math;

import java.util.Random;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.camera.CameraInfo;
import com.abubusoft.xenon.camera.CameraManager;

import android.opengl.GLU;

/**
 * Classe di utilità per funzioni matematiche.
 *
 * @author xcesco
 */
public class XenonMath {

    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.

    static private final int SIN_MASK = ~(-1 << SIN_BITS);

    static private final int SIN_COUNT = SIN_MASK + 1;

    /**
     * costante di moltiplicazione per convertire un angolo misurato in gradi
     * nell'equivalente in radianti.
     * <p>
     * radianti = gradi * PI / 180
     */
    public static final float DEGREES_TO_RADIANS_FACTOR = (float) (Math.PI / 180.0);
    static private final float degToIndex = SIN_COUNT / 360.0f;

    final static float MAX_ABSOLUTE_ERROR = 0.00001f;
    final static float MAX_RELATIVE_ERROR = 0.01f;

    public static final float PI = (float) (Math.PI);

    /**
     * PI / 2
     */
    public static final float PI_HALF = (float) (Math.PI / 2.0);

    /**
     * PI * 2
     */
    public static final float PI_TWICE = (float) (Math.PI * 2.0);

    /**
     * fattore di conversione per convertire un radiante in gradi
     * <p>
     * gradi = radianti * 180 / PI
     */
    public static final float RADIANS_DEGREES_FACTOR = (float) (180.0 / Math.PI);

    static private final float radToIndex = SIN_COUNT / PI_TWICE;

    private static final int SGN_MASK_FLOAT = 0x80000000;

    static private class Sin {
        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * PI_TWICE);
            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * DEGREES_TO_RADIANS_FACTOR);
        }
    }

    /**
     * <p>
     * Restituisce valore assoluto di un numero.
     * </p>
     *
     * @param value in ingresso
     * @return valore assoluto
     */
    public static float abs(float value) {
        if (value < 0)
            return -value;

        return value;
    }

    /**
     * <p>
     * Verifica se value rientra nell'intervallo chiuso min - max. Se è < min
     * allora = min. Se > max allora = max. Se il valore si trova
     * nell'intervallo, non viene alternato.
     * </p>
     * <p>
     * <pre>
     * min(max(startX, minVal), maxVal)
     * </pre>
     *
     * @param value valore da limitare
     * @param min   limite inferiore
     * @param max   limite superiore
     * @return numero compreso tra [min, max]
     */
    public static float clamp(float value, float min, float max) {
        return (value > max ? value = max : value) < min ? min : value;
        //return Math.max(min, Math.min(max, value));
    }

    /**
     * <p>
     * Verifica se value rientra nell'intervallo chiuso min - max. Se è < min
     * allora = min. Se > max allora = max. Se il valore si trova
     * nell'intervallo, non viene alternato.
     * </p>
     * <p>
     * <p>
     * Metodo che lavora con integer.
     * </p>
     * <p>
     * <pre>
     * min(max(startX, minVal), maxVal)
     * </pre>
     *
     * @param value valore da limitare
     * @param min   limite inferiore
     * @param max   limite superiore
     * @return numero compreso tra [min, max]
     */
    public static int clampI(int value, int min, int max) {
        return (value > max ? value = max : value) < min ? min : value;
        //return Math.max(min, Math.min(max, value));
    }

    /**
     * <p>
     * Dato un punto definito sullo schermo, questo metodo provvede a
     * convertirlo in punto nello spazio 3D avente come Z = -zDistance rispetto
     * alla telecamera.
     * </p>
     * <p>
     * <p>
     * Il sistema di coordinate dello schermo è quello di default, in alto a
     * sinistra.
     * </p>
     * <p>
     * <p>
     * Questo metodo è da usare negli eventi di input o dove c'è un potenziale
     * multithread, dato che utilizza un array interno che viene istanziato ogni
     * volta
     * </p>
     * .
     *
     * @param camera    camera da usare
     * @param screenX   larghezza screen
     * @param screenY   altezza screen
     * @param zDistance distanza dalla telecamera
     * @return worldPoint con z=1 rispetto alla telecamera
     */
    public static Point3 convertViewToWorld(Camera camera, float screenX, float screenY, float zDistance) {
        float[] pointInPlanes = new float[16];
        float temp;

        GLU.gluUnProject(screenX, camera.info.viewport[3] - screenY, 0, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
                camera.info.viewport, 0, pointInPlanes, 0);
        // fix
        if (pointInPlanes[3] != 0) {
            temp = 1f / pointInPlanes[3];
            pointInPlanes[0] = pointInPlanes[0] * temp;
            pointInPlanes[1] = pointInPlanes[1] * temp;
            pointInPlanes[2] = pointInPlanes[2] * temp;
        }
        Point3 point0 = Point3.set(pointInPlanes[0], pointInPlanes[1], pointInPlanes[2]);

        GLU.gluUnProject(screenX, camera.info.viewport[3] - screenY, 1, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
                camera.info.viewport, 0, pointInPlanes, 0);
        // fix
        if (pointInPlanes[3] != 0) {
            temp = 1f / pointInPlanes[3];
            pointInPlanes[0] = pointInPlanes[0] * temp;
            pointInPlanes[1] = pointInPlanes[1] * temp;
            pointInPlanes[2] = pointInPlanes[2] * temp;
        }
        // Point3 point1 = Point3.set(pointInPlanes[0], pointInPlanes[1],
        // pointInPlanes[2]);

        // float perc = zDistance / (camera.info.zFar - camera.info.zNear);
        float perc = zDistance * camera.info.zInverseFrustmDepthFactor;

        // ray vector
        point0.setCoords(point0.x + perc * (pointInPlanes[0] - point0.x), point0.y + perc * (pointInPlanes[1] - point0.y), point0.z + perc
                * (pointInPlanes[2] - point0.z));

        return point0;
    }

    /**
     * {@link #convertViewToWorld(Camera, float, float, float)}
     */
    public static Point3 convertViewToWorld(float screenX, float screenY, float zDistance) {
        // usiamo la camera di default.
        return convertViewToWorld(CameraManager.instance().camera, screenX, screenY, zDistance);
    }

    /**
     * <p>
     * <b>Versione Single Thread per
     * {@link #convertViewToWorld(Camera, float, float, float)}.</b>
     * </p>
     */
    public static Point3 convertViewToWorldST(Camera camera, Point3 worldPoint, float[] tempPointInPlanes, float screenX, float screenY, float zDistance) {
        float temp;

        GLU.gluUnProject(screenX, camera.info.viewport[3] - screenY, 0, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
                camera.info.viewport, 0, tempPointInPlanes, 0);
        // fix
        if (tempPointInPlanes[3] != 0) {
            temp = 1f / tempPointInPlanes[3];
            tempPointInPlanes[0] = tempPointInPlanes[0] * temp;
            tempPointInPlanes[1] = tempPointInPlanes[1] * temp;
            tempPointInPlanes[2] = tempPointInPlanes[2] * temp;
        }
        // Point3 point0 = Point3.set(tempPointInPlanes[0],
        // tempPointInPlanes[1], tempPointInPlanes[2]);
        worldPoint.setCoords(tempPointInPlanes[0], tempPointInPlanes[1], tempPointInPlanes[2]);

        GLU.gluUnProject(screenX, camera.info.viewport[3] - screenY, 1, camera.info.cameraMatrix.get(), 0, camera.info.projectionMatrix.get(), 0,
                camera.info.viewport, 0, tempPointInPlanes, 0);
        // fix
        if (tempPointInPlanes[3] != 0) {
            temp = 1f / tempPointInPlanes[3];
            tempPointInPlanes[0] = tempPointInPlanes[0] * temp;
            tempPointInPlanes[1] = tempPointInPlanes[1] * temp;
            tempPointInPlanes[2] = tempPointInPlanes[2] * temp;
        }

        // float perc = zDistance / (camera.info.zFar - camera.info.zNear);
        float perc = zDistance * camera.info.zInverseFrustmDepthFactor;

        // Point3 point1 = Point3.set(tempPointInPlanes[0],
        // tempPointInPlanes[1], tempPointInPlanes[2]);

        // ray vector (tutto con un punto)
        worldPoint.setCoords(worldPoint.x + perc * (tempPointInPlanes[0] - worldPoint.x), worldPoint.y + perc * (tempPointInPlanes[1] - worldPoint.y),
                worldPoint.z + perc * (tempPointInPlanes[2] - worldPoint.z));

        return worldPoint;
    }

    /**
     * {@link #convertViewToWorld(Camera, float, float, float)}
     */
    public static Point3 convertViewToWorldST(Point3 worldPoint, float[] tempPointInPlanes, float screenX, float screenY, float zDistance) {
        // usiamo la camera di default.
        return convertViewToWorldST(CameraManager.instance().camera, worldPoint, tempPointInPlanes, screenX, screenY, zDistance);
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    static public float cos(float radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    static public float cosDeg(float degrees) {
        return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

    /**
     * Dato un numero intero, trova la potenza di 2 più vicina.
     *
     * @param n
     * @return
     */
    public static final int findNearestPowerOf2(int n) {
        int higher = findNextPowerOf2(n);
        int lower = higher / 2;

        // se la distanza tra lower e n è minore rispetto
        // alla distanza tra higher ed n, allora optiamo per lower
        if ((n - lower) <= (higher - n)) {
            return lower;
        }

        return higher;
    }

    /**
     * Trova il prossimo numero potenza di 10, appena superiore al numero
     *
     * @param n
     * @return
     */
    public static final int findNextPowerOf10(int n) {
        int x = 1;
        while (n > 0) {
            n /= 10;
            x *= 10;
        }
        return x;
    }

    /**
     * Trova il prossimo numero intero potenza di 2, appena superiore al numero
     * dato
     *
     * @param n
     * @return
     */
    public static final int findNextPowerOf2(int n) {
        int x = 1;
        while (n > 0) {
            n /= 2;
            x *= 2;
        }
        return x;
    }

    /**
     * <p>
     * Compara due float come <a href=
     * "http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm"
     * >qui</a>
     * </p>
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isEquals(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b))
            return false;
        if (XenonMath.abs(a - b) < MAX_ABSOLUTE_ERROR)
            return true;
        float relativeError;
        if (XenonMath.abs(b) > XenonMath.abs(a))
            relativeError = XenonMath.abs((a - b) / b);
        else
            relativeError = XenonMath.abs((a - b) / a);
        if (relativeError <= MAX_RELATIVE_ERROR)
            return true;
        return false;
    }

    /**
     * startX >= startY
     *
     * @param startX
     * @param startY
     * @return
     */
    public static boolean isGreatEquals(float x, float y) {
        int maxUlps = 5;

        int xInt = Float.floatToIntBits(x);
        int yInt = Float.floatToIntBits(y);

        if (xInt < 0)
            xInt = SGN_MASK_FLOAT - xInt;

        if (yInt < 0)
            yInt = SGN_MASK_FLOAT - yInt;

        final boolean isGreater = (xInt - yInt) >= maxUlps;

        return isGreater && !Float.isNaN(x) && !Float.isNaN(y);
    }

    /**
     * startX > startY
     *
     * @param startX
     * @param startY
     * @return
     */
    public static boolean isGreater(float x, float y) {
        int maxUlps = 5;

        int xInt = Float.floatToIntBits(x);
        int yInt = Float.floatToIntBits(y);

        if (xInt < 0)
            xInt = SGN_MASK_FLOAT - xInt;

        if (yInt < 0)
            yInt = SGN_MASK_FLOAT - yInt;

        final boolean isGreater = (xInt - yInt) > maxUlps;

        return isGreater && !Float.isNaN(x) && !Float.isNaN(y);
    }

    /**
     * startX < startY
     *
     * @param startX
     * @param startY
     * @return
     */
    public static boolean isLess(float x, float y) {
        int maxUlps = 5;

        int xInt = Float.floatToIntBits(x);
        int yInt = Float.floatToIntBits(y);

        if (xInt < 0)
            xInt = SGN_MASK_FLOAT - xInt;

        if (yInt < 0)
            yInt = SGN_MASK_FLOAT - yInt;

        final boolean isGreater = (yInt - xInt) > maxUlps;

        return isGreater && !Float.isNaN(x) && !Float.isNaN(y);
    }

    /**
     * startX <= startY
     *
     * @param startX
     * @param startY
     * @return
     */
    public static boolean isLessEquals(float x, float y) {
        int maxUlps = 5;

        int xInt = Float.floatToIntBits(x);
        int yInt = Float.floatToIntBits(y);

        if (xInt < 0)
            xInt = SGN_MASK_FLOAT - xInt;

        if (yInt < 0)
            yInt = SGN_MASK_FLOAT - yInt;

        final boolean isGreater = (yInt - xInt) >= maxUlps;

        return isGreater && !Float.isNaN(x) && !Float.isNaN(y);
    }

    /**
     * Restituisce true se il numero passato come argomento è una potenza di 2.
     * <p>
     * Vedi
     * http://stackoverflow.com/questions/600293/how-to-check-if-a-number-is
     * -a-power-of-2
     * <p>
     * <p>
     * First and foremost the bitwise binary & operator from MSDN definition:
     * </p>
     * <p>
     * Binary & operators are predefined for the integral types and bool. For
     * integral types, & computes the logical bitwise AND of its operands. For
     * bool operands, & computes the logical AND of its operands; that is, the
     * result is true if and only if both its operands are true.
     * </p>
     * <p>
     * Now let's take a look at how this all plays out:
     * </p>
     * <p>
     * The function returns boolean (true / false) and accepts one incoming
     * parameter of allocation unsigned long (startX, in this case). Let us for
     * the sake of simplicity assume that someone has passed the value 4 and
     * called the function like so:
     * </p>
     * <code>bool b = IsPowerOfTwo(4)</code>
     * <p>
     * Now we replace each occurrence of startX with 4:
     * </p>
     * <p>
     * <pre>
     * return (4 != 0) &amp;&amp; ((4 &amp; (4 - 1)) == 0);
     * </pre>
     * <p>
     * Well we already know that 4 != 0 evals to true, so far so good. But what
     * about:
     * </p>
     * <p>
     * <pre>
     * ((4 &amp; (4 - 1)) == 0)
     * </pre>
     * <p>
     * This translates to this of course:
     * </p>
     * <p>
     * <pre>
     * ((4 &amp; 3) == 0)
     * </pre>
     * <p>
     * But what exactly is 4&3? The binary representation of 4 is 100 and the
     * binary representation of 3 is 011 (remember the & takes the binary
     * representation of these numbers. So we have:
     * </p>
     * <p>
     * <pre>
     * 100 = 4
     * 011 = 3
     * </pre>
     * <p>
     * Imagine these values being stacked up much like elementary addition. The
     * & operator says that if both values are equal to 1 then the result is 1,
     * otherwise it is 0. So 1 & 1 = 1, 1 & 0 = 0, 0 & 0 = 0, and 0 & 1 = 0. So
     * we do the math:
     * </p>
     * <p>
     * <pre>
     * 100
     * 011
     * ----
     * 000
     * </pre>
     * <p>
     * The result is simply 0. So we go back and look at what our return
     * statement now translates to:
     * </p>
     * <p>
     * <pre>
     * return (4 != 0) &amp;&amp; ((4 &amp; 3) == 0);
     * </pre>
     * <p>
     * Which translates now to:
     * </p>
     * <p>
     * <pre>
     * return true &amp;&amp; (0 == 0);
     * return true &amp;&amp; true;
     * </pre>
     * <p>
     * We all know that true && true is simply true, and this shows that for our
     * example, 4 is a power of 2.
     * </p>
     *
     * @param n
     * @return
     */
    public static final boolean isPowerOfTwo(final int n) {
        return ((n != 0) && (n & (n - 1)) == 0);
    }

    /**
     * Massimo valore
     *
     * @param a
     * @param b
     * @return
     */
    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    /**
     * Massimo valore
     *
     * @param a
     * @param b
     * @return
     */
    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    /**
     * Minimo valore
     *
     * @param a
     * @param b
     * @return
     */
    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code int} value that is less than or equal to the algebraic quotient.
     * There is one special case, if the dividend is the
     * {@linkplain Integer#MIN_VALUE Integer.MIN_VALUE} and the divisor is {@code -1},
     * then integer overflow occurs and
     * the result is equal to the {@code Integer.MIN_VALUE}.
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     * <ul>
     * <li>If the signs of the arguments are the same, the results of
     * {@code floorDiv} and the {@code /} operator are the same.  <br>
     * For example, {@code floorDiv(4, 3) == 1} and {@code (4 / 3) == 1}.</li>
     * <li>If the signs of the arguments are different,  the quotient is negative and
     * {@code floorDiv} returns the integer less than or equal to the quotient
     * and the {@code /} operator returns the integer closest to zero.<br>
     * For example, {@code floorDiv(-4, 3) == -2},
     * whereas {@code (-4 / 3) == -1}.
     * </li>
     * </ul>
     * <p>
     *
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * {@code int} value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @since 1.8
     */
    public static int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    /**
     * Minimo valore
     *
     * @param a
     * @param b
     * @return
     */
    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    /**
     * Potenza di due
     *
     * @param value
     * @return
     */
    public static float power2(float value) {
        return value * value;
    }

    /**
     * Converte in radianti un angolo espresso in gradi. Si può utilizzare
     * questo metodo oppure di può direttamente moltiplicare i gradi per la
     * costante {@link #DEGREES_TO_RADIANS_FACTOR}.
     *
     * @param angleInDegree
     * @return
     */
    public static float radians(float angleInDegree) {
        return (angleInDegree * DEGREES_TO_RADIANS_FACTOR);
    }

    /**
     * <p>
     * Restituisce il segno del numero: -1 o 1.
     * </p>
     *
     * @param value
     * @return
     */
    public static float sign(float value) {
        if (value < 0f)
            return -1f;

        return 1f;
    }

    /**
     * <p>
     * Restituisce il segno del numero: -1 o 1.
     * </p>
     *
     * @param value
     * @return
     */
    public static int sign(int value) {
        if (value < 0)
            return -1;

        return 1;
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    static public float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    static public float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * Radice quadrata
     *
     * @param value
     * @return
     */
    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
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
    public static float squareDistanceFromOrigin(float x, float y, float z) {
        return x * x + y * y + z * z;
    }

    /**
     * Dato un quadrato nello spazio 3D, perfettamente perpendicolare rispetto
     * alla telecamera, questa funzione permette di calcolare l'esatta distanza
     * dalla telecamera affinchè tale quadrato abbia un lato che vada a
     * coincidere con un lato dello schermo.
     * <p>
     * Quale sia il lato dello schermo, quello più o quello più piccolo, dipende
     * dalla configurazione della proiezione.
     * <p>
     * Questa funzione riceve in ingresso diversi parametri globali
     * <p>
     * {@link CameraInfo}
     *
     * @param squareWidth
     * @return
     */
    public static float zDistanceForSquare(Camera camera, float squareWidth) {

        double angle = camera.info.fieldOfView * DEGREES_TO_RADIANS_FACTOR * 0.5f;
        float zFactor = (float) (1.0 / Math.tan(angle)) * 0.5f;

        // final float ZFACTOR = 1.732f / 2.0f;
        // Scene.distanceZ =squareWidth * ZFACTOR;

        // zFactor per la metà della larghezza
        return zFactor * squareWidth;
    }

    // ---

    static public Random random = new RandomXS128();

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (inclusive).
     */
    static public int random(int range) {
        return random.nextInt(range + 1);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    static public int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /**
     * Returns a random boolean value.
     */
    static public boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified
     * value.
     */
    static public boolean randomBoolean(float chance) {
        return random() < chance;
    }

    /**
     * Returns random number between 0.0 (inclusive) and 1.0 (exclusive).
     */
    static public float random() {
        return random.nextFloat();
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (exclusive).
     */
    static public float random(float range) {
        return random.nextFloat() * range;
    }

    /**
     * Returns a random number between start (inclusive) and end (exclusive).
     */
    static public float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    /**
     * Returns -1 or 1, randomly.
     */
    static public int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    // ---
}
