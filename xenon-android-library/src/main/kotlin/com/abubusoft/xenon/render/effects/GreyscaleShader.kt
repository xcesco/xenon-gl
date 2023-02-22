package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

public class GreyscaleShader extends Shader {

	public GreyscaleShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_greyscale_vertex, R.raw.effect_greyscale_fragment, null);
	}

}

