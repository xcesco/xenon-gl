package com.abubusoft.xenon.shader;

import com.abubusoft.xenon.R;

/**
 * Shader che semplicemente applica ai vertici i colori definiti.
 * 
 * @author Francesco Benincasa
 *
 */
public class ShaderColor extends Shader {
	/**
	 * Costruisce una tile
	 * 
	 * @param context
	 * @param options
	 */
	public ShaderColor() {
		builder=ShaderBuilder.build(R.raw.shader_color_vertex, R.raw.shader_color_fragment, ArgonShaderOptions.build().name("simmpleColored").numberOfTextures(0).numberOfUniformAttributes(0));	
	}
	
	


}
