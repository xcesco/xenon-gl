package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.R
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class ToonShader : Shader() {
    init {
        builder = build(R.raw.effect_toon_vertex, R.raw.effect_toon_fragment, null)
    }
}