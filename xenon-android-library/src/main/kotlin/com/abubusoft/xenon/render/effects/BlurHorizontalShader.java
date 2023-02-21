package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.R;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;

public class BlurHorizontalShader extends Shader {

	public BlurHorizontalShader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_blur_horizontal_vertex, R.raw.effect_blur_horizontal_fragment, null);
	}

}

