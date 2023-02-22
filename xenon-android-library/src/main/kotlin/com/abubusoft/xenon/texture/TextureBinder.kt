package com.abubusoft.xenon.texture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.XenonLogger.debug
import com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap
import com.abubusoft.xenon.core.graphic.BitmapUtility.adjustOpacity
import com.abubusoft.xenon.core.graphic.BitmapUtility.loadImageFromAssets
import com.abubusoft.xenon.core.graphic.SampledBitmapFactory.decodeBitmap
import com.abubusoft.xenon.core.util.IOUtility.saveTempPngFile
import com.abubusoft.xenon.math.SizeI2
import com.abubusoft.xenon.math.XenonMath.isEquals
import com.abubusoft.xenon.opengl.AsyncOperationManager
import com.abubusoft.xenon.opengl.AsyncOperationManager.AsyncTextureInfoLoader
import com.abubusoft.xenon.opengl.AsyncOperationManager.load
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.opengl.XenonGLExtension
import com.abubusoft.xenon.texture.BitmapResizer.resizeBitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Permette di convertire una bitmap in una texture. La bitmap viene recuperata da un file, da una risorsa o passata direttamente. Il sistema si preoccupa di effettuare delle
 * operazioni di preparazione prima di renderla.
 *
 *
 * Da file:
 *
 *  *
 *
 *
 *
 * Da risorsa:
 *
 *  *
 *
 *
 *
 * Da bitmap:
 *
 *  *
 *
 *
 * @author Francesco Benincasa
 */
object TextureBinder {
    /**
     * effettua il bind di una bitmap. wrapped
     *
     * @param textureBindingId idx della texture
     * @param source           bitmap source
     * @param options          opzioni startX la generazione della texture
     * @return
     */
    private fun bindBitmapInternal(textureBindingId: Int, context: Context?, source: Bitmap?, options: TextureOptions?): TextureDimension {
        // prendiamo eventualmente le opzioni di default
        var options = options
        options = options ?: TextureOptions.Companion.build()

        // salva la texture iniziale su file
        if (options.debugTextureOnFile) {
            val fileName = saveTempPngFile(context!!, "debugTexture" + textureBindingId + "Initial", source!!)
            Logger.debug("Saved texture %s", fileName)
        }
        var transformedSource: Bitmap?
        val result: TextureDimension
        transformedSource = source
        if (!isEquals(options.opacity, 1.0f)) {
            // se variamo l'opacità allora dobbiamo mutare
            transformedSource = wrapBitmap(adjustOpacity(source!!, options.opacity))
        }

        // effettuiamo transform se esiste
        if (options.transformation != null) {
            val transformedSource1: Bitmap
            transformedSource1 = wrapBitmap(options.transformation!!.transform(transformedSource!!))

            // azzeriamo la bitmap iniziale dopo averla trasformata
            if (transformedSource != transformedSource1 && !transformedSource.isRecycled) {
                transformedSource.recycle()
            }
            transformedSource = transformedSource1
        }
        result = if (options.textureSize == TextureSizeType.SIZE_UNBOUND) {
            bindTextureInternal(textureBindingId, context, transformedSource, options)
        } else {
            bindResizedBitmapInternal(textureBindingId, context, transformedSource, options)
        }

        // se creiamo bitmap di intermezzo, allora ricicliamo
        if (transformedSource != source && !transformedSource!!.isRecycled) {
            transformedSource.recycle()
        }
        return result
    }

    /**
     * Effettua il bind facendo prima un resize dell'immagine.
     *
     * @param textureId
     * @param source
     * @return dimensioni della texture
     */
    private fun bindResizedBitmapInternal(textureId: Int, context: Context?, source: Bitmap?, options: TextureOptions?): TextureDimension {
        val effectiveSize = SizeI2()

        // effettua il resize dell'immagine e mette in effectiveSize le
        // dimensioni dell'immagine
        var finalBitmap: Bitmap? = resizeBitmap(source!!, options!!.textureSize!!, options.aspectRatio, effectiveSize)
        bindTextureInternal(textureId, context, finalBitmap, options)
        val ret = TextureDimension(
            effectiveSize.width,
            effectiveSize.height,
            effectiveSize.width.toFloat() / options.textureSize!!.width,
            effectiveSize.height.toFloat() / options.textureSize!!.height,
            options.textureSize!!
        )

        // una volta fatto il binding rilasciamo la risorsa
        if (!finalBitmap!!.isRecycled) finalBitmap.recycle()
        finalBitmap = null
        return ret
    }

    /**
     *
     *
     * Carica una bitmap da un file presente nell'assets.
     *
     *
     * @param context
     * @return
     */
    fun bindTextureFromAssetsFile(texture: Texture, context: Context?, fileNameValue: String?, optionsValue: TextureOptions?, loaderOptions: TextureReplaceOptions?): TextureInfo? {
        return if (!loaderOptions!!.asyncLoad) {
            var finalBitmap = loadImageFromAssets(context!!, fileNameValue)
            val dimensionValue = bindBitmapInternal(texture.bindingId, context, finalBitmap, optionsValue)

            // una volta fatto il binding rilasciamo la risorsa
            if (!finalBitmap!!.isRecycled) finalBitmap.recycle()
            finalBitmap = null

            // impostiamo le info
            val result = TextureInfo(TextureInfo.TextureLoadType.ASSET_TEXTURE)
            result.resourceContext = context
            result.setFileName(fileNameValue)
            result.options = optionsValue
            result.dimension = dimensionValue
            result
        } else {
            AsyncOperationManager.instance().load(texture, object : AsyncTextureInfoLoader {
                override fun load(texture: Texture): TextureInfo? {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromAssetsFile(texture, context, fileNameValue, optionsValue, loaderOptions.copy().asyncLoad(false))
                }
            }, loaderOptions.asyncLoaderListener)
            null
        }
    }

    /**
     *
     *
     * Effettua il bind di una bitmap. wrapped.
     *
     *
     *
     *
     * **Non può essere effettuato in modo ansincrono anche se impostato, dato che la bitmap è già presente.**
     *
     *
     * @param source        bitmap source
     * @param optionsValue  opzioni startX la generazione della texture
     * @return
     */
    fun bindTextureFromBitmap(texture: Texture, context: Context?, source: Bitmap?, optionsValue: TextureOptions?, loaderOptions: TextureReplaceOptions?): TextureInfo? {
        return if (!loaderOptions!!.asyncLoad) {
            val dimensionValue = bindBitmapInternal(texture.bindingId, context, source, optionsValue)

            // impostiamo le info
            val result = TextureInfo(TextureInfo.TextureLoadType.BITMAP_TEXTURE)
            result.options = optionsValue
            result.dimension = dimensionValue
            if (TextureManager.Companion.instance().isTexturesReloadable()) {
                result.setFileName(saveTempPngFile(context!!, "texture_", source!!))
            }
            result
        } else {
            AsyncOperationManager.instance().load(texture, object : AsyncTextureInfoLoader {
                override fun load(texture: Texture): TextureInfo? {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromBitmap(texture, context, source, optionsValue, loaderOptions.copy().asyncLoad(false))
                }
            }, loaderOptions.asyncLoaderListener)
            null
        }
    }

    /**
     * Ricarica un'immagine in una texture. wrapped
     *
     * @param url
     */
    fun bindTextureFromFile(texture: Texture, context: Context?, url: String?, optionsValue: TextureOptions?, loaderOptions: TextureReplaceOptions?): TextureInfo? {
        return if (!loaderOptions!!.asyncLoad) {

            // carica un'immagine le cui dimensioni siano più vicine possibili
            // alle
            // dimensioni della texture richiesta.
            var image: Bitmap?
            image = if (optionsValue!!.textureSize == TextureSizeType.SIZE_UNBOUND) {
                // non abbiamo posto alcun limite alla texture
                wrapBitmap(BitmapFactory.decodeFile(url))
            } else {
                // carichiamo immagine facendo cmq un resize
                decodeBitmap(url, optionsValue.textureSize!!.width, optionsValue.textureSize!!.height, null)
            }
            val dimensionValue = bindBitmapInternal(texture.bindingId, context, image, optionsValue)

            // impostiamo le info
            val result = TextureInfo(TextureInfo.TextureLoadType.FILE_TEXTURE)
            result.resourceContext = null
            result.setFileName(url)
            result.options = optionsValue
            result.dimension = dimensionValue
            if (!image!!.isRecycled) image.recycle()
            image = null
            result
        } else {
            AsyncOperationManager.instance().load(texture, object : AsyncTextureInfoLoader {
                override fun load(texture: Texture): TextureInfo? {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromFile(texture, context, url, optionsValue, loaderOptions.copy().asyncLoad(false))
                }
            }, loaderOptions.asyncLoaderListener)
            null
        }
    }

    /**
     * Carica un'immagine da una risorsa id e la carica in opengl nella dimensione specificata. wrapped
     *
     * @param context
     * @param resourceIdx
     * @return dimensioni reali della texture
     */
    fun bindTextureFromResourceId(texture: Texture, context: Context?, resourceIdx: Int, optionsValue: TextureOptions?, loaderOptions: TextureReplaceOptions?): TextureInfo {
        return if (!loaderOptions!!.asyncLoad) {
            // carica un'immagine le cui dimensioni siano più vicine possibili
            // alle
            // dimensioni della texture richiesta.
            var image: Bitmap?
            image = if (optionsValue!!.textureSize == TextureSizeType.SIZE_UNBOUND) {
                // non abbiamo posto alcun limite alla texture
                wrapBitmap(BitmapFactory.decodeResource(context!!.resources, resourceIdx))
            } else {
                // carichiamo immagine facendo cmq un resize
                decodeBitmap(context!!.resources, resourceIdx, optionsValue.textureSize!!.width, optionsValue.textureSize!!.height, null)
            }
            val dimensionValue = bindBitmapInternal(texture.bindingId, context, image, optionsValue)

            // impostiamo le info
            val result = TextureInfo(TextureInfo.TextureLoadType.RESOURCE_TEXTURE)
            result.resourceContext = context
            result.setResourceId(resourceIdx)
            result.setFileName(null)
            result.options = optionsValue
            result.dimension = dimensionValue
            if (!image.isRecycled) image.recycle()
            image = null
            result
        } else {
            AsyncOperationManager.instance().load(texture, object : AsyncTextureInfoLoader {
                override fun load(texture: Texture): TextureInfo? {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromResourceId(texture, context, resourceIdx, optionsValue, loaderOptions.copy().asyncLoad(false))
                }
            }, loaderOptions.asyncLoaderListener)
        }
    }

    /**
     * Carica una risorsa mediante una stringa che contiene il suo id, da un contesto da definire. wrapped
     *
     * @param context
     * @param imageResourceId
     * @param options
     * @return
     */
    fun bindTextureFromResourceString(
        texture: Texture,
        context: Context,
        imageResourceId: String?,
        options: TextureOptions?,
        loaderOptions: TextureReplaceOptions?
    ): TextureInfo {
        var resourceId: Int
        val imageName: String
        var resourceName: String
        imageName = if (imageResourceId!!.lastIndexOf('.') > 0) {
            imageResourceId.substring(imageResourceId.lastIndexOf('/') + 1, imageResourceId.lastIndexOf('.'))
        } else {
            imageResourceId.substring(imageResourceId.lastIndexOf('/') + 1)
        }
        resourceName = "drawable/$imageName"
        resourceId = context.resources.getIdentifier(resourceName, null, context.packageName)

        // alternativamente proviamo a caricarlo da raw
        if (resourceId == 0) {
            resourceName = "raw/$imageName"
            resourceId = context.resources.getIdentifier(resourceName, null, context.packageName)
        }
        return bindTextureFromResourceId(texture, context, resourceId, options, loaderOptions)
    }

    /**
     *
     *
     * Effettua il binding opengl e restituisce le dimensioni della texture.
     *
     *
     *
     *
     *
     * OpenGL has two parameters that can be set:
     *
     *
     *
     *
     *
     * GL_TEXTURE_MIN_FILTER GL_TEXTURE_MAG_FILTER These correspond to the minification and magnification described earlier. GL_TEXTURE_MIN_FILTER accepts the following options:
     *
     *
     *
     *
     *  * GL_NEAREST GL_LINEAR
     *  * GL_NEAREST_MIPMAP_NEAREST
     *  * GL_NEAREST_MIPMAP_LINEAR
     *  * GL_LINEAR_MIPMAP_NEAREST
     *  * GL_LINEAR_MIPMAP_LINEAR
     *  * GL_TEXTURE_MAG_FILTER
     *
     *
     *
     *
     *
     * Accepts the following options:
     *
     *
     *
     *
     *  * GL_NEAREST GL_LINEAR GL_NEAREST corresponds to nearest-neighbour rendering, GL_LINEAR corresponds to bilinear filtering,
     *  * GL_LINEAR_MIPMAP_NEAREST corresponds to bilinear filtering with mipmaps
     *  * GL_LINEAR_MIPMAP_LINEAR corresponds to trilinear filtering.
     *
     *
     *
     * Vedi [http://www.learnopengles.com/](http://www.learnopengles.com/android-lesson-six-an-introduction-to-texture-filtering/)
     *
     * @param textureId
     * @param source
     * @param options
     * @return dimensioni della texture ovvero quanto della texture può essere considerata come buona.
     */
    private fun bindTextureInternal(textureId: Int, context: Context?, source: Bitmap?, options: TextureOptions?): TextureDimension {

        // salva la texture generata su file
        if (options!!.debugTextureOnFile) {
            val fileName = saveTempPngFile(context!!, "debugTexture" + textureId + "Final", source!!)
            debug("Saved texture %s", fileName)
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        // impostiamo
        // GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, source, 0);
        if (options.textureInternalFormat == TextureInternalFormatType.FLOAT) {
            val buffer: ByteBuffer
            if (XenonGLExtension.TEXTURE_FLOAT.isPresent) {
                buffer = ByteBuffer.allocateDirect(source!!.width * source.height * 4 * 4).order(ByteOrder.nativeOrder())
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source.width, source.height, 0, GLES20.GL_RGBA, TextureInternalFormatType.FLOAT.value, buffer)
                checkGlError("glTexImage2D TEXTURE_FLOAT")
                Logger.debug("Create texture TEXTURE_FLOAT %s %s", source.width, source.height)
            } else if (XenonGLExtension.TEXTURE_HALF_FLOAT.isPresent) {
                // lavoriamo con gli half float
                buffer = ByteBuffer.allocateDirect(source!!.width * source.height * 4 * 2).order(ByteOrder.nativeOrder())
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source.width, source.height, 0, GLES20.GL_RGBA, XenonGL.GL_HALF_FLOAT_OES, buffer)
                checkGlError("glTexImage2D TEXTURE_HALF_FLOAT")
                Logger.debug("Create texture TEXTURE_HALF_FLOAT %s %s", source.width, source.height)
            } else {
                val msg = "Unable to create texture of float or half_float! (No suitable opengl extensions founded!)"
                Logger.fatal(msg)
                throw RuntimeException(msg)
            }

            // GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source, TextureInternalFormatType.FLOAT.value, 0);
        } else {
            // GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source.getWidth(), source.getHeight(), 0, GLES20.GL_RGBA, TextureInternalFormatType.FLOAT.value, pixels)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source, TextureInternalFormatType.UNSIGNED_BYTE.value, 0)
        }
        if (options.textureFilter!!.generateMipmap) {
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
        }
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, options.textureFilter!!.minifier)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, options.textureFilter!!.magnifier)

        // come da http://www.khronos.org/opengles/sdk/docs/man/xhtml/glTexParameter.xml
        // il wrap può essere di tre tipi:
        // GL_CLAMP_TO_EDGE, GL_MIRRORED_REPEAT, or GL_REPEAT
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, options.textureRepeat!!.value)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, options.textureRepeat!!.value)
        return TextureDimension(source!!.width, source.height, 1.0f, 1.0f, TextureSizeType.SIZE_UNBOUND)
    }
}