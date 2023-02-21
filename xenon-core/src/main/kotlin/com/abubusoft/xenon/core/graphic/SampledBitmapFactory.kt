package com.abubusoft.xenon.core.graphic

import android.content.res.Resources
import android.graphics.*
import com.abubusoft.xenon.core.graphic.BitmapManager.Companion.wrapBitmap
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Carica le bitmap in un formato più comodo e di ridotte dimensioni, giusto per
 * evitare problemi con la memoria.
 *
 * Wrapped
 *
 * <pre>
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html}
</pre> *
 *
 * @author Francesco Benincasa
 */
object SampledBitmapFactory {
    private fun scaleWidth(source: Bitmap?, newWidth: Int, newHeight: Int, scalePosition: ScalePositionType): Bitmap {
        val sourceWidth = source!!.width
        val sourceHeight = source.height

        // Compute the scaling factors to fit the new height and width,
        // respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        val xScale = newWidth.toFloat() / sourceWidth

        // Now get the size of the source bitmap event scaled
        val scaledWidth = xScale * sourceWidth
        val scaledHeight = xScale * sourceHeight

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        val left = 0f
        var top = 0f
        when (scalePosition) {
            ScalePositionType.FIT_WIDTH_TOP -> top = 0f
            ScalePositionType.FIT_WIDTH_CENTER -> top = (newHeight - scaledHeight) / 2
            ScalePositionType.FIT_WIDTH_BOTTOM -> top = newHeight - scaledHeight
            else -> {}
        }

        // The target rectangle for the new, scaled version of the source bitmap
        // will now be
        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val reducedImage: Bitmap = wrapBitmap(Bitmap.createScaledBitmap(source, scaledWidth.toInt(), scaledHeight.toInt(), false))
        val dest: Bitmap = wrapBitmap(Bitmap.createBitmap(newWidth, newHeight, source.config))
        val canvas = Canvas(dest)
        canvas.drawBitmap(reducedImage, null, targetRect, null)
        reducedImage.recycle()
        return dest
    }

    /**
     * Effettua il crop dell'immagine, partendo dalle dimensioni desiderate.
     *
     * wrapped
     *
     * Vedi
     * http://stackoverflow.com/questions/8112715/how-to-crop-bitmap-center-
     * like-imageview
     *
     * @param source
     * @param newHeight
     * @param newWidth
     * @return
     */
    private fun cropCenter(source: Bitmap?, newWidth: Int, newHeight: Int): Bitmap {
        val sourceWidth = source!!.width
        val sourceHeight = source.height

        // Compute the scaling factors to fit the new height and width,
        // respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        // Now get the size of the source bitmap event scaled
        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        // The target rectangle for the new, scaled version of the source bitmap
        // will now
        // be
        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        // Finally, we create a new bitmap of the specified size and draw our
        // new,
        // scaled bitmap onto it.
        val dest: Bitmap = wrapBitmap(Bitmap.createBitmap(newWidth, newHeight, source.config))
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)
        return dest
    }

    /**
     * Data una risorsa, provvede a creare una bitmap delle dimensioni
     * desiderate.
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun createBitmap(res: Resources?, resId: Int, reqWidth: Int, reqHeight: Int, type: ScalePositionType?): Bitmap? {
        val image1 = decodeBitmap(res, resId, reqWidth, reqHeight, null)
        var image2: Bitmap? = null
        when (type) {
            ScalePositionType.CROP_CENTER -> image2 = wrapBitmap(cropCenter(image1, reqWidth, reqHeight))
            ScalePositionType.FIT_WIDTH_TOP -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            ScalePositionType.FIT_WIDTH_CENTER -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            ScalePositionType.FIT_WIDTH_BOTTOM -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            else -> {}
        }

        // se possibile lo ricicliamo
        if (image1 != image2) {
            image1.recycle()
        }
        return image2
    }

    /**
     * Effettua il resize della bitmap
     *
     * @param sourceBitmap
     * bitmap da modificare
     * @param reqWidth
     * width richiesta
     * @param reqHeight
     * height richiesta
     * @param type
     * tipo di ridimensionamento: CROP_CENTER, FIT_WIDTH_TOP,
     * FIT_WIDTH_CENTER, FIT_WIDTH_BOTTOM:
     * @return immagine modificata
     */
    fun resizeBitmap(sourceBitmap: Bitmap?, reqWidth: Int, reqHeight: Int, type: ScalePositionType?): Bitmap? {
        val image1 = Bitmap.createScaledBitmap(sourceBitmap!!, reqWidth, reqHeight, true)
        var image2: Bitmap? = null
        when (type) {
            ScalePositionType.CROP_CENTER -> image2 = wrapBitmap(cropCenter(image1, reqWidth, reqHeight))
            ScalePositionType.FIT_WIDTH_TOP -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            ScalePositionType.FIT_WIDTH_CENTER -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            ScalePositionType.FIT_WIDTH_BOTTOM -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            else -> {}
        }

        // se possibile lo ricicliamo
        if (image1 != image2) {
            image1.recycle()
        }
        return image2
    }

    /**
     * Dato il nome di file, provvede a creare una bitmap delle dimensioni
     * desiderate.
     *
     * wrapped
     *
     * @param imageFileName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun createBitmap(imageFileName: String?, reqWidth: Int, reqHeight: Int, type: ScalePositionType?): Bitmap? {
        // questa viene wrappata
        val image1 = decodeBitmap(imageFileName, reqWidth, reqHeight, null)
        var image2: Bitmap? = null
        when (type) {
            ScalePositionType.CROP_CENTER -> image2 = wrapBitmap(cropCenter(image1, reqWidth, reqHeight))
            ScalePositionType.FIT_WIDTH_TOP -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            ScalePositionType.FIT_WIDTH_CENTER -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            ScalePositionType.FIT_WIDTH_BOTTOM -> image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type))
            else -> {}
        }

        // wrapBitmap(cropCenter(image1, reqWidth, reqHeight));
        // se possibile lo ricicliamo
        if (image1 != image2) {
            image1!!.recycle()
        }
        return image2
    }

    /**
     * Calcola il fattore di zoom per caricare l'immagine da disco nel miglior
     * modo possibile (dal punto di vista del consumo della memoria).
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    /**
     * Decodifica un'immagine da un contesto riducendone le dimensioni. Non
     * effettua uno scaling, semplicemente tira fuori una bitmap di dimensioni
     * ridotte.
     *
     * Wrapped
     *
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @param effectiveSize
     * **OPZIONALE** serve a memorizzare in output le dimensioni
     * reali dell'immagine
     * @return
     */
    fun decodeBitmap(res: Resources?, resId: Int, reqWidth: Int, reqHeight: Int, effectiveSize: Rect?): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val temp = BitmapFactory.decodeResource(res, resId, options)
        if (effectiveSize != null) {
            effectiveSize.left = 0
            effectiveSize.top = 0
            effectiveSize.bottom = options.outHeight
            effectiveSize.right = options.outWidth
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        // ricicliamo bitmap
        if (temp != null && !temp.isRecycled) temp.recycle()
        return wrapBitmap(BitmapFactory.decodeResource(res, resId, options))
    }

    /**
     * Decodifica un'immagine da un contesto riducendone le dimensioni.
     *
     * Wrapped
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @param effectiveSize
     * **OPZIONALE** serve a memorizzare in output le dimensioni
     * reali dell'immagine
     * @return
     * @throws FileNotFoundException
     */
    fun decodeBitmap(imageFileName: String?, reqWidth: Int, reqHeight: Int, effectiveSize: Rect?): Bitmap? {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val temp = BitmapFactory.decodeFile(imageFileName, options)
        if (effectiveSize != null) {
            effectiveSize.left = 0
            effectiveSize.top = 0
            effectiveSize.bottom = options.outHeight
            effectiveSize.right = options.outWidth
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        // ricicliamo bitmap
        if (temp != null && !temp.isRecycled) temp.recycle()
        var bis: BufferedInputStream? = null
        var bp: Bitmap? = null
        try {
            bis = BufferedInputStream(FileInputStream(imageFileName))

            val bitmap = BitmapFactory.decodeStream(bis, null, options)
            if (bitmap != null) {
                bp = wrapBitmap(bitmap)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bp
    }
}