package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;
import com.abubusoft.xenon.R;

public class ToonShader extends Shader {

	public ToonShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_toon_vertex, R.raw.effect_toon_fragment, null);
	}

}

