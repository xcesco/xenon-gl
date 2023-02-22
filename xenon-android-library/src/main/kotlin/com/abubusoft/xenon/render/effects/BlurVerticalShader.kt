package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.R
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class BlurVerticalShader : Shader() {
    init {
        builder = build(R.raw.effect_blur_vertical_vertex, R.raw.effect_blur_vertical_fragment, null)
    }
}