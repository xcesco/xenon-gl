/**
 *
 */
package com.abubusoft.xenon.texture

import android.graphics.*
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap
import com.abubusoft.xenon.math.SizeI2
import com.abubusoft.xenon.math.XenonMath.isGreater
import com.abubusoft.xenon.math.XenonMath.isLess

/**
 * wrapped
 * @author Francesco Benincasa
 */
object BitmapResizer {
    var backgroundColor = Color.parseColor("#00000000")

    /**
     * Effettua il resize delle immagini. wrapped
     * @param source
     * @param size
     * @param aspectXY
     * rapporto tra x e y (width / height)
     * @param effectiveSize
     * @return
     */
    fun resizeBitmap(source: Bitmap, size: TextureSizeType, aspectXY: Double, effectiveSize: SizeI2?): Bitmap {
        val Ri = source.width.toFloat() / source.height
        var temp: Bitmap?
        val resizedBitmap: Bitmap
        temp = if (isLess(Ri, aspectXY.toFloat())) {
            // dobbiamo far aumentare il rapporto tra w / h --> DEVO aumentare w
            // (non posso aumentare h):
            // DI QUANTO? IN PERCENTUALE RI/RA
            cropBitmapInHeight(source, (Ri / aspectXY).toFloat())
        } else if (isGreater(Ri, aspectXY.toFloat())) {
            cropBitmapInWidth(source, (aspectXY / Ri).toFloat())
        } else {
            source
        }
        resizedBitmap = resizeBitmapHeight(temp, size, aspectXY, effectiveSize)

        // TODO recycle
        if (source != temp && temp != source && !temp.isRecycled) temp.recycle()
        temp = null
        return resizedBitmap
    }

    /**
     * Riduce in larghezza (CROPPANDO IN CENTRO) un'immagine. Data la
     * percentuale espressa in 0 - 1.
     *
     * wrapped
     *
     * @param bmpSrc
     * @param incPercentage
     * @return
     */
    fun cropBitmapInWidth(bmpSrc: Bitmap, incPercentage: Float): Bitmap {
        val offset = Math.ceil(((1.0f - incPercentage) * bmpSrc.width / 2.0f).toDouble()).toInt()
        val conf = Bitmap.Config.ARGB_8888 // see other conf types
        val desctBmp = wrapBitmap(Bitmap.createBitmap(bmpSrc.width - offset * 2, bmpSrc.height, conf))

        // Logger.error("ADATTO IMMAGINE : inc %s -- w,h = (%s, %s",incPercentage,desctBmp.getWidth(),desctBmp.getHeight());
        val src = Rect(0 + offset, 0, bmpSrc.width - offset, bmpSrc.height)
        val dest = Rect(0, 0, desctBmp.width, desctBmp.height)
        val wideBmpCanvas: Canvas
        wideBmpCanvas = Canvas(desctBmp)
        wideBmpCanvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor))
        wideBmpCanvas.drawBitmap(bmpSrc, src, dest, null)
        return desctBmp
    }

    /**
     * Riduce in altezza (CROPPANDO IN CENTRO) un'immagine. Data la percentuale
     * espressa in 0 - 1.
     *
     * wrapped
     *
     * @param bmpSrc
     * @param incPercentage
     * @return
     */
    fun cropBitmapInHeight(bmpSrc: Bitmap, incPercentage: Float): Bitmap {
        val offset = Math.ceil(((1.0f - incPercentage) * bmpSrc.height / 2.0f).toDouble()).toInt()
        val conf = Bitmap.Config.ARGB_8888 // see other conf types
        val desctBmp = wrapBitmap(Bitmap.createBitmap(bmpSrc.width, bmpSrc.height - offset * 2, conf))

        // Logger.error("ADATTO IMMAGINE : inc %s -- w,h = (%s, %s",incPercentage,desctBmp.getWidth(),desctBmp.getHeight());
        val src = Rect(0, 0 + offset, bmpSrc.width, bmpSrc.height - offset)
        val dest = Rect(0, 0, desctBmp.width, desctBmp.height)
        val wideBmpCanvas: Canvas
        wideBmpCanvas = Canvas(desctBmp)
        wideBmpCanvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor))
        wideBmpCanvas.drawBitmap(bmpSrc, src, dest, null)
        return desctBmp
    }

    /**
     * Prende la bitmap, e la adatta facendo in modo che H siano uguali.
     *
     * wrapped
     *
     * @param bmpSrc
     * bitmap in ingresso
     * @param textureSize
     * dimensioni della texture
     * @param aspectRatio
     * rapporto tra width e height della bitmap.
     * @param effectiveSize
     * se impostato, alla fine del metodo contiene le dimensioni effettivamente utilizzate della bitmap
     * @return
     * nuova bitmap
     */
    fun resizeBitmapHeight(bmpSrc: Bitmap?, textureSize: TextureSizeType, aspectRatio: Double, effectiveSize: SizeI2?): Bitmap {
        val initialW = bmpSrc!!.width
        val initialH = bmpSrc.height
        val desideredHeight = textureSize.height / aspectRatio
        val scaleHeight = desideredHeight.toFloat() / initialH
        val matrix = Matrix()
        matrix.postScale(scaleHeight, scaleHeight)
        var resizedBitmap: Bitmap? = wrapBitmap(Bitmap.createBitmap(bmpSrc, 0, 0, initialW, initialH, matrix, false))
        val Iw = resizedBitmap!!.width.toFloat()
        val Ih = resizedBitmap.height.toFloat()
        Logger.error("Resized bitmap " + resizedBitmap.width + " startX " + resizedBitmap.height)
        val conf = Bitmap.Config.ARGB_8888 // see other conf types
        val desctBmp = wrapBitmap(Bitmap.createBitmap(textureSize.width, textureSize.height, conf))
        Logger.error("desctBmp bitmap " + desctBmp.width + " startX " + desctBmp.height)
        var offsetX = 0
        var offsetY = 0
        if (isGreater(Iw / Ih, aspectRatio.toFloat())) {
            // abbiamo più w che h
            offsetX = ((Iw - textureSize.width) / 2.0).toInt()
        } else {
            offsetY = ((Ih - textureSize.height) / 2.0).toInt()
        }
        val src: Rect
        val dest: Rect

        // Logger.error("Offset " + screenOffsetX + "," + screenOffsetY);
        offsetX = if (offsetX > 0) offsetX else 0
        offsetY = if (offsetY > 0) offsetY else 0
        // Logger.error("Offset Fixed " + screenOffsetX + "," + screenOffsetY);
        src = Rect(0 + offsetX, 0 + offsetY, resizedBitmap.width - offsetX, resizedBitmap.height - offsetY)
        // Logger.error("rect " + resizedBitmap.getWidth() + " startX " +
        // resizedBitmap.getHeight()); 

        //TODO da verificare: deve essere spostato in basso
        // consideriamo sempre come sia un quadrato
        dest = Rect(0, 0, textureSize.width, (textureSize.width / aspectRatio).toInt())

        // impostiamo in effetti quando è grande la bitmap nella texture
        if (effectiveSize != null) {
            // memorizziamo fino a dove arriva la bitmap reale
            effectiveSize.width = resizedBitmap.width
            effectiveSize.height = resizedBitmap.height
        }
        val wideBmpCanvas: Canvas
        wideBmpCanvas = Canvas(desctBmp)
        wideBmpCanvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor))
        wideBmpCanvas.drawBitmap(resizedBitmap, src, dest, null)

        // TODO recycle bitmap
        // svuotiamo bitmap
        if (resizedBitmap != bmpSrc && resizedBitmap.isRecycled) resizedBitmap.recycle()
        resizedBitmap = null
        return desctBmp
    }
}