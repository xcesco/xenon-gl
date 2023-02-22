package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

public class ScanlinesShader extends Shader {

	public ScanlinesShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_scanlines_vertex, R.raw.effect_scanlines_fragment, null);
	}

}

