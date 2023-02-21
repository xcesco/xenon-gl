/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * 
 * https://github.com/yulu/GLtext/blob/master/res/raw/edge_detect_fragment_shader.glsl
 * 
 * @author Francesco Benincasa
 *
 */
@UseShader(ToonShader.class)
public class ToonEffect extends AbstractEffect<ToonShader> {

	@Override
	protected void updateShader(ToonShader shader, long enlapsedTime, float speedAdapter) {
		// TODO Auto-generated method stub
		
	}
		
}
