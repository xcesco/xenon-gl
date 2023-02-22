package com.abubusoft.xenon.render

import com.abubusoft.xenon.texture.RenderedTextureOptions
import com.abubusoft.xenon.texture.TextureSizeType

/**
 *
 *
 * Opzioni per la creazione una render
 *
 * @author Francesco Benincasa
 */
class RenderPipelineOptions private constructor() {
    /**
     * Se true indica che le texture e la mesh utilizzata è fatta apposta per entrare completamente nello schermo
     * Se false la mesh e le texture vengono usate semplicemente come un quadrato.
     */
    var optimized = false

    /**
     * dimensioni del viewport
     */
    var viewportDimensions: TextureSizeType? = null

    /**
     * opzioni della texture per il rendering
     */
    var renderTextureOptions: RenderedTextureOptions? = null
    fun renderTextureOptions(value: RenderedTextureOptions?): RenderPipelineOptions {
        renderTextureOptions = value
        return this
    }

    fun dimension(value: TextureSizeType?): RenderPipelineOptions {
        viewportDimensions = value
        return this
    }

    fun optimized(value: Boolean): RenderPipelineOptions {
        optimized = value
        return this
    }

    companion object {
        /**
         * dimensioni standard del viewport
         */
        var VIEWPORT_DIMENSION_NORMAL = TextureSizeType.SIZE_512x512
        var VIEWPORT_DIMENSION_BIG = TextureSizeType.SIZE_1024x1024
        var VIEWPORT_DIMENSION_HD = TextureSizeType.SIZE_2048x2048
        fun build(): RenderPipelineOptions {
            return RenderPipelineOptions().dimension(VIEWPORT_DIMENSION_NORMAL).renderTextureOptions(RenderedTextureOptions.build()).optimized(true)
        }
    }
}