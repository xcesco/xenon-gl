package com.abubusoft.xenon.math

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.abubusoft.xenon.texture.TextureSizeType

/**
 * Genera una texture i cui valori sono in realtà una tabella di lookup bidimensionale
 *
 * @author Francesco Benincasa
 */
object LUTFactory {
    /**
     * dato un valore da 0 a 1, viene convertito da 0 a 255. viene utilizzato un check di range, quindi rientrerà sempre tra 0 a 255.
     *
     * @param value
     * @return
     */
    private fun comp(value: Double): Int {
        var value = value
        value = Math.min(Math.max(value, 0.0), 1.0) * 255f
        return value.toInt()
    }

    fun createSinLUT(size: TextureSizeType): Bitmap {
        return createLUT(size, object : OnLUTCreateValueListener {
            /* (non-Javadoc)
       * @see com.abubusoft.xenon.math.LUTFactory.OnLUTCreateValueListener#onCreate(com.abubusoft.xenon.math.LUTFactory.ARGB, double, double)
       */
            override fun onCreate(argb: ARGB, x: Double, y: Double) {
                argb.r = Math.sin(XenonMath.PI_HALF * x)
            }
        })
    }

    /**
     *
     *
     * Crea una bitmap contenente una LUT (look up table). Ogni componente può ospitare dei valori da 0 .. 1
     *
     * @param size
     * @param listener
     * @return
     */
    fun createLUT(size: TextureSizeType, listener: OnLUTCreateValueListener): Bitmap {
        val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var color: Int
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        val argb = ARGB()
        for (j in 0 until size.height) {
            for (i in 0 until size.width) {
                argb.clear(0f)
                listener.onCreate(argb, i.toDouble() / size.height, j.toDouble() / size.width)

                // salva nei componenti rg [1..0], [0.. -1]
                color = Color.argb(comp(argb.a), comp(argb.r), comp(argb.g), comp(argb.b))
                paint.color = color
                canvas.drawPoint(i.toFloat(), j.toFloat(), paint)
            }
        }
        return bitmap
    }

    class ARGB {
        var a = 0.0
        var r = 0.0
        var g = 0.0
        var b = 0.0
        fun clear(value: Float) {
            a = value.toDouble()
            r = value.toDouble()
            g = value.toDouble()
            b = value.toDouble()
        }
    }

    interface OnLUTCreateValueListener {
        /**
         * Crea una componente, avendo come input le coordinate della texture, normalizzati, da 0 a 1.
         *
         * @param argb ARGB
         * @param x    [0 .. 1]
         * @param y    [0 .. 1]
         */
        fun onCreate(argb: ARGB, x: Double, y: Double)
    }
}