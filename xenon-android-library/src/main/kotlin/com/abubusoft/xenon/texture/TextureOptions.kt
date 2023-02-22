/**
 *
 */
package com.abubusoft.xenon.texture

/**
 * @author Francesco Benincasa
 */
class TextureOptions {
    /**
     * nome della texture
     */
    var name: String? = "[undefined]"

    /**
     * dimensioni finali della texture
     */
    var textureSize: TextureSizeType? = null

    /**
     * tipo di filtro. Di default è quello da usare negli atlas, il NEAREST
     */
    var textureFilter: TextureFilterType? = null

    /**
     * formato degli elementi che compongono la texture: unsigned byte o float
     */
    var textureInternalFormat: TextureInternalFormatType? = null

    /**
     * Aspect ratio della texture, ovvero permette di selezionare quanta parte della texture considerare come buona.
     */
    var aspectRatio = 0.0

    /**
     * trasformazione da applicare all'immagine prima di trasformarla in texture.
     */
    var transformation: BitmapTransformation? = null

    /**
     * se true, abilita il salvataggio della texture su file temporaneo
     */
    var debugTextureOnFile = false

    /**
     * opacità 0 - 1f
     */
    var opacity = 0f

    /**
     * indica se ripetere la texture, sia in startX che in startY
     */
    var textureRepeat: TextureRepeatType? = null

    /**
     * Fluent interface per textureRepeat. true/false. Wrappa TextureWrap.ClampToEdge, TextureWrap.ClampToEdge
     *
     * @param textureRepeat
     * @return
     */
    fun textureRepeat(value: TextureRepeatType?): TextureOptions {
        textureRepeat = value
        return this
    }

    /**
     * Fluent interface per opacity. 0 - 1f
     *
     * @param opacity
     * @return
     */
    fun opacity(value: Float): TextureOptions {
        opacity = value
        return this
    }

    /**
     * Fluent interface per textureSize.
     *
     * @param textureSizeValue
     * @return
     * this
     */
    fun size(value: TextureSizeType?): TextureOptions {
        textureSize = value
        return this
    }

    /**
     * Fluent interface per aspectRatio.
     *
     * @param aspectRatioValue
     * @return
     * this
     */
    fun aspectRatio(value: TextureAspectRatioType): TextureOptions {
        aspectRatio = value.aspectXY
        return this
    }

    /**
     * Fluent interface per aspectRatio.
     *
     * @param aspectRatioValue
     * @return
     * this
     */
    fun aspectRatio(value: Double): TextureOptions {
        aspectRatio = value
        return this
    }

    /**
     * Fluent interface per debugTextureOnFile.
     *
     * @param value
     * @return
     * this
     */
    fun debugTextureOnFile(value: Boolean): TextureOptions {
        debugTextureOnFile = value
        return this
    }

    /**
     * Fluent interface per transformation.
     *
     * @param aspectRatioValue
     * @return
     * this
     */
    fun transformation(value: BitmapTransformation?): TextureOptions {
        transformation = value
        return this
    }

    /**
     * Tipo di formato interno della texture: unsigned byte o float
     *
     * @param value
     * @return
     */
    fun textureInternalFormat(value: TextureInternalFormatType?): TextureOptions {
        textureInternalFormat = value
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
    fun copy(src: TextureOptions): TextureOptions {
        val nuovo = build()
        nuovo.aspectRatio = src.aspectRatio
        nuovo.debugTextureOnFile = src.debugTextureOnFile
        nuovo.opacity = src.opacity
        nuovo.textureFilter = src.textureFilter
        nuovo.textureRepeat = src.textureRepeat
        nuovo.textureSize = src.textureSize
        nuovo.transformation = src.transformation
        return nuovo
    }

    /**
     * Fluent interface per textureFilter
     *
     * @param value
     * @return
     */
    fun textureFilter(value: TextureFilterType?): TextureOptions {
        textureFilter = value
        return this
    }

    /**
     * Fluent interface per name
     *
     * @param value
     * @return
     */
    fun name(value: String?): TextureOptions {
        name = value
        return this
    }

    companion object {
        /**
         * Build delle opzioni. configurazione di default:
         *
         *
         *  * **textureSize**: [TextureSize.SIZE_UNBOUND]
         *  * **aspectRatio**: [TypeAspectRatio.RATIO1_1]
         *  * **opacity**: 1.0f
         *  * **textureRepeat**: [TextureRepeatType.NO_REPEAT]
         *  * **textureFilter**: [TextureFilterType.NEAREST]
         *  * **asyncLoad**: false
         *  * **debugTextureOnFile**: false
         *
         *
         * @return
         */
        fun build(): TextureOptions {
            // configurazione di default
            return TextureOptions()
                .size(TextureSizeType.SIZE_UNBOUND)
                .aspectRatio(TextureAspectRatioType.RATIO1_1)
                .opacity(1.0f).textureRepeat(TextureRepeatType.NO_REPEAT)
                .debugTextureOnFile(false)
                .textureFilter(TextureFilterType.NEAREST)
                .textureInternalFormat(TextureInternalFormatType.UNSIGNED_BYTE)
        }
    }
}