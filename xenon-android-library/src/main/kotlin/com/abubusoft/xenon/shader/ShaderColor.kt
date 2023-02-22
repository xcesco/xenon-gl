package com.abubusoft.xenon.shader

import com.abubusoft.xenon.R

/**
 * Shader che semplicemente applica ai vertici i colori definiti.
 *
 * @author Francesco Benincasa
 */
class ShaderColor : Shader() {
    /**
     * Costruisce una tile
     *
     * @param context
     * @param options
     */
    init {
        builder = ShaderBuilder.Companion.build(
            R.raw.shader_color_vertex,
            R.raw.shader_color_fragment,
            ArgonShaderOptions.Companion.build().name("simmpleColored").numberOfTextures(0).numberOfUniformAttributes(0)
        )
    }
}