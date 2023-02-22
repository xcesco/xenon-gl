package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.R
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderBuilder.Companion.build

class EdgeDetect2Shader : Shader() {
    init {
        builder = build(R.raw.effect_edge_detect2_vertex, R.raw.effect_edge_detect2_fragment, null)
    }
}