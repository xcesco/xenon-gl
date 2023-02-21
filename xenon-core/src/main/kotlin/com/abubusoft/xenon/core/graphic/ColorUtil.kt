/**
 *
 */
package com.abubusoft.xenon.core.graphic

import android.graphics.Bitmap
import android.graphics.Color

/**
 * @author Francesco Benincasa
 */
object ColorUtil {
    /**
     *
     *
     * Suddivide un colore nei suoi componenti.
     *
     *
     *
     *  * 0 - Alpha
     *  * 1 - Red
     *  * 2 - Green
     *  * 3 - Blue
     *
     *
     * @param color
     * colore in input
     * @param argb
     * array nel quale mettere le componenti argb del colore
     */
    fun splitInComponent(color: Int, argb: IntArray) {
        argb[0] = color shr 24 and 0xFF
        argb[1] = color shr 16 and 0xFF
        argb[2] = color shr 8 and 0xFF
        argb[3] = color and 0xFF
    }

    fun transformColor(fraction: Float, startColor: Int, endColor: Int): Int {
        val startInt = startColor
        val startA = startInt shr 24 and 0xff
        val startR = startInt shr 16 and 0xff
        val startG = startInt shr 8 and 0xff
        val startB = startInt and 0xff
        val endInt = endColor
        val endA = endInt shr 24 and 0xff
        val endR = endInt shr 16 and 0xff
        val endG = endInt shr 8 and 0xff
        val endB = endInt and 0xff
        return ((startA + (fraction * (endA - startA)).toInt() shl 24) or (startR + (fraction * (endR - startR)).toInt() shl 16) or (startG + (fraction * (endG - startG)).toInt() shl 8)
                or (startB + (fraction * (endB - startB)).toInt()))
    }

    /**
     *
     *
     * Modifica l'alpha channel in percentuale rispetto a quello del colore
     * iniziale.
     *
     *
     * @param color
     * colore iniziale
     * @param fraction
     * da 0f a 1f
     * @return
     */
    fun transformAlpha(color: Int, fraction: Float): Int {
        val colorA = color shr 24 and 0xff
        val colorR = color shr 16 and 0xff
        val colorG = color shr 8 and 0xff
        val colorB = color and 0xff
        return ((fraction * colorA).toInt() shl 24 or (colorR shl 16) or (colorG shl 8) or colorB)
    }

    /**
     *
     *
     * Dato un colore, modifica in percentuale la luminosità.
     *
     *
     * @param color
     * @param factor
     * @return
     */
    fun luminosity(color: Int, factor: Float): Int {
        var color = color
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= factor // value component
        color = Color.HSVToColor(hsv)
        return color
    }

    fun luminosity(color: Int, factor: Float, hsv: FloatArray): Int {
        var color = color
        Color.colorToHSV(color, hsv)
        hsv[2] *= factor // value component
        color = Color.HSVToColor(hsv)
        return color
    }

    /**
     *
     *
     * Data una bitmap, calcola il colore medio
     *
     *
     * @param pic
     * @return
     */
    fun averageARGB(pic: Bitmap): Int {
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        B = 0
        G = B
        R = G
        A = R
        val width = pic.width
        val height = pic.height
        val size = width * height
        var buffer: IntArray? = IntArray(size)
        pic.getPixels(buffer, 0, width, 0, 0, width, height)
        // int pixelColor;
        for (c in buffer!!) {
            A += Color.alpha(c)
            R += Color.red(c)
            G += Color.green(c)
            B += Color.blue(c)
        }

        /*
		 * for (int x = 0; x < width; ++x) { for (int y = 0; y < height; ++y) {
		 * pixelColor = buffer; pic.getPixel(x, y); A +=
		 * Color.alpha(pixelColor); R += Color.red(pixelColor); G +=
		 * Color.green(pixelColor); B += Color.blue(pixelColor); } }
		 */A /= size
        R /= size
        G /= size
        B /= size
        buffer = null
        return Color.argb(A, R, G, B)
    }
}