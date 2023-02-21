package com.abubusoft.xenon.shader;

import com.abubusoft.xenon.R;
 
public class ShaderTexture extends Shader {
	public ShaderTexture() {
		
		builder=ShaderBuilder.build(R.raw.shader_texture_vertex, R.raw.shader_texture_fragment, ArgonShaderOptions.build());
	}
	
}
