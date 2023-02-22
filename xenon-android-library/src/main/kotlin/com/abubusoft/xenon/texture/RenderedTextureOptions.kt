/**
 *
 */
package com.abubusoft.xenon.texture

/**
 * @author Francesco Benincasa
 */
class RenderedTextureOptions {
    var depthBuffer = false
    var renderFactor = 0f
    fun depthBuffer(value: Boolean): RenderedTextureOptions {
        depthBuffer = value
        return this
    }

    fun renderFactor(value: Float): RenderedTextureOptions {
        renderFactor = value
        return this
    }

    var name = "[RenderedTexture noname]"

    /**
     * Tipo di formato interno della texture: unsigned byte o float
     *
     */
    var textureInternalFormat: TextureInternalFormatType? = null
    fun name(value: String): RenderedTextureOptions {
        name = value
        return this
    }

    /**
     * Tipo di formato interno della texture: unsigned byte o float
     *
     * @param value
     * @return
     */
    fun textureInternalFormat(value: TextureInternalFormatType?): RenderedTextureOptions {
        textureInternalFormat = value
        return this
    }

    companion object {
        /**
         *
         *
         * Valori predefiniti:
         *
         * <dl>
         * <dt>name</dt>
         * <dd>[undefined]</dd>
         * <dt>depthBuffer</dt>
         * <dd>true</dd>
         * <dt>textureInternalFormat</dt>
         * <dd>TextureInternalFormatType.UNSIGNED_BYTE</dd>
         * <dt>renderFactor</dt>
         * <dd>1.0</dd>
        </dl> *
         *
         * @return
         * opzioni
         */
        fun build(): RenderedTextureOptions {
            return RenderedTextureOptions().depthBuffer(true).textureInternalFormat(TextureInternalFormatType.UNSIGNED_BYTE).renderFactor(1f)
        }
    }
}