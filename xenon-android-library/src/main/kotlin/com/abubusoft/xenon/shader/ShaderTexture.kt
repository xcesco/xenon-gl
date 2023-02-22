package com.abubusoft.xenon.shader

import com.abubusoft.xenon.R

class ShaderTexture : Shader() {
    init {
        builder = ShaderBuilder.Companion.build(R.raw.shader_texture_vertex, R.raw.shader_texture_fragment, ArgonShaderOptions.Companion.build())
    }
}