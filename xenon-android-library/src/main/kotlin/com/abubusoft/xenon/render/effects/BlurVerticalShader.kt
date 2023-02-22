package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.R;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;

public class BlurVerticalShader extends Shader {

	public BlurVerticalShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_blur_vertical_vertex, R.raw.effect_blur_vertical_fragment, null);
	}

}

