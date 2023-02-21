/**
 *
 */
package com.abubusoft.xenon.core.graphic

import android.content.Context
import android.graphics.*
import com.abubusoft.xenon.core.graphic.ColorUtil.splitInComponent
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 * @author Francesco Benincasa
 */
object BitmapUtility {
    /**
     * Prende l'immagine source e crea un'altra bitmap di dimensioni cropWitdh e cropHeight, partendo da cropLeft,cropTop dalla bitmap source.
     *
     * @param source
     * @param cropLeft
     * @param cropTop
     * @param cropWidth
     * @param cropHeight
     * @return
     */
    fun cropBitmap(source: Bitmap, cropLeft: Int, cropTop: Int, cropWidth: Int, cropHeight: Int): Bitmap {
        val destBitmap = Bitmap.createBitmap(cropWidth, cropHeight, Bitmap.Config.ARGB_8888)
        BitmapManager.wrap(destBitmap)
        val c = Canvas(destBitmap)
        c.drawBitmap(source, cropLeft.toFloat(), cropTop.toFloat(), null)
        return destBitmap
    }

    /**
     * Prende l'immagine rgbBitmap e vi applica la bitmap maskBitmap per filtrare i pixel. Se nella seconda maschera un pixel è nero, indica che il relativo pixel in rgbBitmap è
     * trasparente. Se è bianco, allora il relativo pixel in rgbBitmap è valido.
     *
     * [Vedi wiki](https://github.com/xcesco/xenon-gl/wiki/BitmapUtility#bitmaputilitycompositewithmaskbitmap-rgbbitmap-bitmap-maskbitmap)
     *
     * @param maskBitmap
     * @param rgbBitmap
     *
     * @return
     */
    fun compositeWithMask(rgbBitmap: Bitmap, maskBitmap: Bitmap): Bitmap {
        val width = rgbBitmap.width
        val height = rgbBitmap.height
        check(!(width != maskBitmap.width || height != maskBitmap.height)) { "image size mismatch!" }
        val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        BitmapManager.wrap(destBitmap)
        val pixels = IntArray(width)
        val alpha = IntArray(width)
        for (y in 0 until height) {
            rgbBitmap.getPixels(pixels, 0, width, 0, y, width, 1)
            maskBitmap.getPixels(alpha, 0, width, 0, y, width, 1)
            for (x in 0 until width) {
                // Replace the alpha channel with the r value from the bitmap.
                pixels[x] = pixels[x] and 0x00FFFFFF or (alpha[x] shl 8 and -0x1000000)
            }
            destBitmap.setPixels(pixels, 0, width, 0, y, width, 1)
        }
        return destBitmap
    }

    /**
     * Prende il colore color e vi applica la bitmap maskBitmap per filtrare i pixel. Se nella seconda maschera un pixel è nero, indica che il relativo pixel in rgbBitmap è
     * trasparente. Se è bianco, allora il relativo pixel in rgbBitmap è valido.
     *
     * Rispetto al metodo con due bitmap, questo è molto più efficiente se vogliamo semplicemente applicare una maschera ad una bitmap basata su un colore.
     *
     * [Vedi wiki](https://github.com/xcesco/xenon-gl/wiki/BitmapUtility#bitmaputilitycompositewithmaskbitmap-rgbbitmap-bitmap-maskbitmap)
     *
     * @param rgbColor
     * @param maskBitmap
     * @param mask
     * @return
     */
    fun compositeWithMask(rgbColor: Int, maskBitmap: Bitmap, mask: MaskType): Bitmap {
        val width = maskBitmap.width
        val height = maskBitmap.height
        val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        BitmapManager.wrap(destBitmap)
        val pixels = IntArray(width)
        val alpha = IntArray(width)
        if (mask.value > 0) {
            for (y in 0 until height) {
                maskBitmap.getPixels(alpha, 0, width, 0, y, width, 1)
                for (x in 0 until width) {
                    // Replace the alpha channel with the r value from the bitmap.
                    pixels[x] = rgbColor and 0x00FFFFFF or (alpha[x] shl mask.value and -0x1000000)
                }
                destBitmap.setPixels(pixels, 0, width, 0, y, width, 1)
            }
        } else {
            for (y in 0 until height) {
                maskBitmap.getPixels(alpha, 0, width, 0, y, width, 1)
                for (x in 0 until width) {
                    // Replace the alpha channel with the r value from the bitmap.
                    pixels[x] = rgbColor and 0x00FFFFFF or (alpha[x] and -0x1000000)
                }
                destBitmap.setPixels(pixels, 0, width, 0, y, width, 1)
            }
        }
        return destBitmap
    }

    /**
     *
     *
     * Prende il colore color e moltiplica ogni pixel inputBitmap per il colore rgbColor. Normalmente la bitmap come secondo argomento è una bitmap in bianco e nero che in seguito
     * viene
     *
     *
     *
     *
     * A differenza del metodo compositeWithMask, questo metodo non applica una maschera, semplicemente modifica i colori della maschera di input.
     *
     *
     *
     *
     * Per ogni componente, compreso l'alpha channel viene applicata la formula
     *
     *
     * <pre>
     * C  = C  * (1.0/256.0) * C
     * N	  1                  2
    </pre> *
     *
     *
     *
     * Dove C1 è il componente del colore rgbColor e C2 è il componente del pixel inputBitmap.
     *
     *
     * @param rgbColor
     * colore da applicare alla bitmap
     * @param inputBitmap
     * bitmap in bianco e nero da elaborare
     * @return result bitmap risultante
     */
    fun colorize(rgbColor: Int, inputBitmap: Bitmap): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        BitmapManager.wrap(destBitmap)
        val pixels = IntArray(width)
        val origin = IntArray(width)
        val currentPixel = IntArray(4)
        val rgbColorComponent = IntArray(4)
        splitInComponent(rgbColor, rgbColorComponent)
        for (y in 0 until height) {
            inputBitmap.getPixels(origin, 0, width, 0, y, width, 1)
            for (x in 0 until width) {
                splitInComponent(origin[x], currentPixel)
                // Replace the alpha channel with the r value from the bitmap.
                pixels[x] = Color.argb(
                    (rgbColorComponent[0] * (currentPixel[0] / 255f)).toInt(),
                    (rgbColorComponent[1] * (currentPixel[1] / 255f)).toInt(),
                    (rgbColorComponent[2] * (currentPixel[2] / 255f)).toInt(),
                    (rgbColorComponent[3] * (currentPixel[3] / 255f)).toInt()
                )
            }
            destBitmap.setPixels(pixels, 0, width, 0, y, width, 1)
        }
        return destBitmap
    }

    /**
     * Recupera una bitmap a partire da una risorsa. Non viene fatto alcun scaling.
     *
     * @param context
     * @param resourceId
     * @return
     */
    fun loadImageFromResource(context: Context, resourceId: Int): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeResource(context.resources, resourceId, opt)
    }

    /**
     * Carica un'immagine da un asset
     *
     * @param context
     * @param fileName
     * @return
     */
    fun loadImageFromAssets(context: Context, fileName: String?): Bitmap? {
        val mngr = context.assets
        // Create an input stream to read from the asset folder
        var `is`: InputStream? = null
        try {
            `is` = mngr.open(fileName!!)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        // Get the texture from the Android resource directory
        // InputStream is =
        // context.getResources().openRawResource(R.drawable.radiocd5);
        var bitmap: Bitmap? = null
        bitmap = try {
            // BitmapFactory is an Android graphics utility for images
            BitmapFactory.decodeStream(`is`)
        } finally {
            // Always clear and close
            try {
                `is`!!.close()
                `is` = null
            } catch (e: IOException) {
            }
        }
        return bitmap
    }

    /**
     * Carica un'immagine da un file
     *
     * @param context
     * @param fileName
     * @return
     */
    fun loadImage(fileName: String?): Bitmap? {
        // Create an input stream to read from the asset folder
        var `is`: InputStream? = null
        try {
            `is` = FileInputStream(fileName) // mngr.open(fileName);
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        // Get the texture from the Android resource directory
        // InputStream is =
        // context.getResources().openRawResource(R.drawable.radiocd5);
        var bitmap: Bitmap? = null
        bitmap = try {
            // BitmapFactory is an Android graphics utility for images
            BitmapFactory.decodeStream(`is`)
        } finally {
            // Always clear and close
            try {
                `is`!!.close()
                `is` = null
            } catch (e: IOException) {
            }
        }
        return bitmap
    }

    /**
     * http://stackoverflow.com/questions/9021450/android-button-with-image-dim- image-event-button-disabled
     *
     * @param bitmap
     * The source bitmap.
     * @param opacity
     * from 0f to 1.0f a value between 0 (completely transparent) and 255 (completely opaque).
     * @param bitmap
     * manager opzionale
     * @return The opacity-adjusted bitmap. If the source bitmap is mutable it will be adjusted and returned, otherwise a new bitmap is created.
     */
    fun adjustOpacity(bitmap: Bitmap, opacityPercentage: Float): Bitmap {
        val opacity = (255f * opacityPercentage).toInt()
        val mutableBitmap = if (bitmap.isMutable) bitmap else bitmap.copy(Bitmap.Config.ARGB_8888, true)
        BitmapManager.wrapBitmap(mutableBitmap)
        val canvas = Canvas(mutableBitmap)
        val colour = opacity and 0xFF shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        return mutableBitmap
    }
}