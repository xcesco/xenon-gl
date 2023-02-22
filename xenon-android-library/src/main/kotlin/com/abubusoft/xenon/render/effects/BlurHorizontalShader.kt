package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.R
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder

class BlurHorizontalShader : Shader() {
    init {
        builder = ShaderBuilder.build(R.raw.effect_blur_horizontal_vertex, R.raw.effect_blur_horizontal_fragment, null)
    }
}