/**
 *
 */
package com.abubusoft.xenon.texture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap
import com.abubusoft.xenon.core.graphic.BitmapUtility.adjustOpacity
import com.abubusoft.xenon.core.graphic.SampledBitmapFactory.decodeBitmap
import com.abubusoft.xenon.math.SizeI2
import com.abubusoft.xenon.opengl.AsyncOperationManager
import com.abubusoft.xenon.opengl.AsyncOperationManager.AsyncTextureInfoLoader
import com.abubusoft.xenon.texture.BitmapResizer.resizeBitmap
import com.abubusoft.xenon.texture.TextureInfo.TextureType

/**
 * A differenza del TextureBinder, le immagini non vengono elaborate, vengono prese così come sono.
 *
 * @author Francesco Benincasa
 */
object CubeTextureBinder {
    /**
     * Carica una cube texture (6 immagini) dalle risorse id e la carica in opengl nella dimensione specificata. wrapped
     *
     * @param textureIndex
     * @param context
     * @param resourceIdx
     * @param textureSize
     *
     * @return dimensioni reali della texture
     */
    @JvmStatic
    fun bindTextureFromResourceId(
        texture: CubeTexture?,
        context: Context,
        textureBindingId: Int,
        upperX: Int,
        lowerX: Int,
        upperY: Int,
        lowerY: Int,
        upperZ: Int,
        lowerZ: Int,
        options: TextureOptions,
        loaderOptions: TextureReplaceOptions
    ): TextureInfo? {
        return if (!loaderOptions.asyncLoad) {
            // al textureIndex associamo una texture di tipo cube map
            GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureBindingId)
            var dimension: TextureDimension? = null
            val resourceIdxArray = intArrayOf(upperX, lowerX, upperY, lowerY, upperZ, lowerZ)
            val mapTexture = intArrayOf(
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
            )

            // carichiamo per tutti i lati un'immagine
            for (i in 0..5) {
                var image: Bitmap?
                image = if (options.textureSize == TextureSizeType.SIZE_UNBOUND) {
                    // non abbiamo posto alcun limite alla texture
                    wrapBitmap(BitmapFactory.decodeResource(context.resources, resourceIdxArray[i]))
                } else {
                    // carichiamo immagine facendo cmq un resize
                    decodeBitmap(context.resources, resourceIdxArray[i], options.textureSize.width, options.textureSize.height, null)
                }
                dimension = bindTextureFromBitmapInternal(textureBindingId, mapTexture[i], image, options)
                if (!image.isRecycled) image.recycle()
                image = null
            }
            if (options.textureFilter.generateMipmap) {
                GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_CUBE_MAP)
            }
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, options.textureFilter.minifier.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, options.textureFilter.magnifier.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, options.textureRepeat.value.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, options.textureRepeat.value.toFloat())

            // impostiamo le info
            val result = TextureInfo(TextureInfo.TextureLoadType.RESOURCE_TEXTURE, TextureType.TEXTURE2D_CUBIC)
            result.resourceContext = context
            for (i in 0..5) {
                result.setResourceId(i, resourceIdxArray[i])
            }
            for (i in 0..5) {
                result.setFileName(i, null)
            }
            result.options = options
            result.dimension = dimension
            result
        } else {
            AsyncOperationManager.load(texture!!, object : AsyncTextureInfoLoader {
                override fun load(texture: Texture): TextureInfo? {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromResourceId(
                        texture as CubeTexture?,
                        context,
                        textureBindingId,
                        upperX,
                        lowerX,
                        upperY,
                        lowerY,
                        upperZ,
                        lowerZ,
                        options,
                        loaderOptions.copy().asyncLoad(false)
                    )
                }
            }, loaderOptions.asyncLoaderListener)
        }
    }

    /**
     * effettua il bind di una bitmap. wrapped
     *
     * @param textureBindindId
     * idx della texture
     * @param source
     * bitmap source
     * @param options
     * opzioni startX la generazione della texture
     * @return
     */
    private fun bindTextureFromBitmapInternal(textureBindindId: Int, mapId: Int, source: Bitmap, options: TextureOptions): TextureDimension {
        // prendiamo eventualmente le opzioni di default
        var options: TextureOptions? = options
        options = options ?: TextureOptions.build()
        var transformedSource: Bitmap
        val result: TextureDimension
        transformedSource = source
        if (options!!.opacity != 1.0f) {
            // se variamo l'opacità allora dobbiamo mutare
            transformedSource = wrapBitmap(adjustOpacity(source, options.opacity))
        }

        // effettuiamo transform se esiste
        if (options.transformation != null) {
            val transformedSource1: Bitmap
            transformedSource1 = wrapBitmap(options.transformation.transform(transformedSource))

            // azzeriamo la bitmap iniziale dopo averla trasformata
            if (transformedSource != transformedSource1 && !transformedSource.isRecycled) {
                transformedSource.recycle()
            }
            transformedSource = transformedSource1
        }
        result = if (options.textureSize == TextureSizeType.SIZE_UNBOUND) {
            bindSideTexture(mapId, transformedSource, options)
        } else {
            bindResizedSideTexture(mapId, transformedSource, options)
        }

        // se creiamo bitmap di intermezzo, allora ricicliamo
        if (transformedSource != source && !transformedSource.isRecycled) {
            transformedSource.recycle()
        }
        return result
    }

    /**
     * Mappa un lato della texture
     *
     * @param targetId
     * @param source
     * @param options
     * @return
     */
    private fun bindSideTexture(targetId: Int, source: Bitmap?, options: TextureOptions?): TextureDimension {
        GLUtils.texImage2D(targetId, 0, source, 0)
        return TextureDimension(source!!.width, source.height, 1.0f, 1.0f, TextureSizeType.SIZE_UNBOUND)
    }

    /**
     * Effettua il bind facendo prima un resize dell'immagine.
     *
     * @param textureIndex
     * @param source
     * @param size
     * @param aspectRatio
     *
     * @return dimensioni della texture
     */
    private fun bindResizedSideTexture(mapId: Int, source: Bitmap, options: TextureOptions?): TextureDimension {
        val effectiveSize = SizeI2()

        // effettua il resize dell'immagine e mette in effectiveSize le
        // dimensioni dell'immagine
        var finalBitmap: Bitmap? = resizeBitmap(source, options!!.textureSize, options.aspectRatio, effectiveSize)
        bindSideTexture(mapId, finalBitmap, options)
        val ret = TextureDimension(
            effectiveSize.width,
            effectiveSize.height,
            effectiveSize.width.toFloat() / options.textureSize.width,
            effectiveSize.height.toFloat() / options.textureSize.height,
            options.textureSize
        )

        // una volta fatto il binding rilasciamo la risorsa
        if (!finalBitmap!!.isRecycled) finalBitmap.recycle()
        finalBitmap = null
        return ret
    }
}