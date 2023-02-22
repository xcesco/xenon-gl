package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.R
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class GreyscaleShader : Shader() {
    init {
        builder = build(R.raw.effect_greyscale_vertex, R.raw.effect_greyscale_fragment, null)
    }
}