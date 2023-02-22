package com.abubusoft.xenon.texture

import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.media.MediaPlayer

/**
 * Opzioni per la creazione di una texture esterna.
 *
 * @author xcesco
 */
class ExternalTextureOptions {
    /**
     * nome della texture
     */
    var name = "[ExternalTexture noname]"

    /**
     * associato alla camera
     */
    var onFrameAvailableListener: OnFrameAvailableListener? = null

    /**
     * media player
     */
    var mediaPlayer: MediaPlayer? = null

    /**
     * dimensioni finali della texture
     */
    var textureSize: TextureSizeType? = null

    /**
     * Rapporto tra width e height della texture da considerare come valido.
     * Permette di selezionare quanta parte della texture considerare come
     * buona.
     */
    var aspectRatio = 0.0

    /**
     * Fluent interface per textureSize.
     *
     * @param textureSizeValue
     * @return
     */
    fun size(value: TextureSizeType?): ExternalTextureOptions {
        textureSize = value
        return this
    }

    /**
     * Fluent interface per aspectRatio.
     *
     * @param aspectRatioValue
     * @return
     */
    fun aspectRatio(value: TextureAspectRatioType): ExternalTextureOptions {
        aspectRatio = value.aspectXY
        return this
    }

    /**
     * Fluent interface per aspectRatio.
     *
     * @param aspectRatioValue
     * @return this
     */
    fun aspectRatio(value: Double): ExternalTextureOptions {
        aspectRatio = value
        return this
    }

    /**
     *
     *
     * Crea una copia della configurazione
     *
     *
     * @return
     */
    fun copy(src: ExternalTextureOptions): ExternalTextureOptions {
        val nuovo = build()
        nuovo.aspectRatio = src.aspectRatio
        nuovo.textureSize = src.textureSize
        nuovo.mediaPlayer = src.mediaPlayer
        nuovo.onFrameAvailableListener = src.onFrameAvailableListener
        return nuovo
    }

    /**
     * Fluent interface per name
     *
     * @param value
     * @return
     */
    fun name(value: String): ExternalTextureOptions {
        name = value
        return this
    }

    fun mediaPlayer(value: MediaPlayer?): ExternalTextureOptions {
        mediaPlayer = value
        return this
    }

    fun onFrameAvailableListener(value: OnFrameAvailableListener?): ExternalTextureOptions {
        onFrameAvailableListener = value
        return this
    }

    fun toTextureOptions(): TextureOptions {
        val options: TextureOptions = TextureOptions.Companion.build()
        options.aspectRatio = aspectRatio
        options.name = name
        options.textureSize = textureSize
        return options
    }

    companion object {
        fun build(): ExternalTextureOptions {
            return ExternalTextureOptions().size(TextureSizeType.SIZE_UNBOUND).aspectRatio(TextureAspectRatioType.RATIO1_1)
        }
    }
}