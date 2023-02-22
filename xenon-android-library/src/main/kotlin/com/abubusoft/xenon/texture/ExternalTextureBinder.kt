/**
 *
 */
package com.abubusoft.xenon.texture

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.view.Surface
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.opengl.AsyncOperationManager.AsyncTextureInfoLoader
import com.abubusoft.xenon.opengl.AsyncOperationManager.load
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.opengl.XenonGLExtension
import com.abubusoft.xenon.texture.TextureInfo.TextureType

/**
 * A differenza del TextureBinder, le immagini non vengono elaborate, vengono prese così come sono.
 *
 * @author Francesco Benincasa
 */
object ExternalTextureBinder {
    /**
     * Carica una external texture. Ci sono diversi limiti sulla configurazione delle texture esterne.
     *
     *
     * @return dimensioni reali della texture
     */
    fun bindTexture(texture: ExternalTexture, textureBindingId: Int, options: TextureOptions?, loaderOptions: TextureReplaceOptions?): TextureInfo? {
        return if (!loaderOptions!!.asyncLoad) {
            if (!XenonGLExtension.IMAGE_EXTERNAL.isPresent) {
                val msg = "Unable to create external texture ! (No suitable opengl extensions founded!)"
                Logger.fatal(msg)
                throw RuntimeException(msg)
            }

            // al textureIndex associamo una texture di tipo cube map
            GLES20.glBindTexture(XenonGL.TEXTURE_EXTERNAL_OES, textureBindingId)
            checkGlError("glBindTexture TEXTURE_EXTERNAL_OES")

            // min filter is LINEAR or NEAREST
            GLES20.glTexParameterf(XenonGL.TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
            GLES20.glTexParameterf(XenonGL.TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())

            // CLAMP_TO_EDGE
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())

            // TODO gestire l'aspect ratio
            val dimension = TextureDimension(options!!.textureSize!!.width, options.textureSize!!.height, 1.0f, 1.0f, TextureSizeType.SIZE_UNBOUND)
            setup(texture, textureBindingId)

            // impostiamo le info
            // TODO per il momento facciamo finta che sia di tipo resource
            val result = TextureInfo(TextureInfo.TextureLoadType.RESOURCE_TEXTURE, TextureType.TEXTURE_EXTERNAL)
            result.options = options
            result.dimension = dimension
            result
        } else {
            load(
                texture,
                AsyncTextureInfoLoader { texture1: Texture -> bindTexture(texture1 as ExternalTexture, textureBindingId, options, loaderOptions.copy().asyncLoad(false)) },
                loaderOptions.asyncLoaderListener
            )
        }
    }

    private fun setup(texture: ExternalTexture, textureBindingId: Int) {
        // creiamo una surface associata alla texture
        texture.surface = SurfaceTexture(textureBindingId)

        // controllo su dimensioni di default
        if (texture.options.textureSize!!.width > 0 && texture.options.textureSize!!.height > 0) {
            texture.surface!!.setDefaultBufferSize(texture.options.textureSize!!.width, texture.options.textureSize!!.height)
        } else {
            Logger.warn("No size specified for external texture %s (%s)", texture.name, texture.bindingId)
        }
        texture.surface!!.setOnFrameAvailableListener(texture.options.onFrameAvailableListener)
        if (texture.options.mediaPlayer != null) {
            texture.options.mediaPlayer!!.setSurface(Surface(texture.surface))
        }
    }
}