package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.R;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderBuilder;

public class EdgeDetect2Shader extends Shader {

	public EdgeDetect2Shader() {
		super();

		builder = ShaderBuilder.build(R.raw.effect_edge_detect2_vertex, R.raw.effect_edge_detect2_fragment, null);
	}

}

