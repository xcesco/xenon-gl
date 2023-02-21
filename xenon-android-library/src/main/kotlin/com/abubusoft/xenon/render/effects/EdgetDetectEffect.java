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
@UseShader(EdgeDetectShader.class)
public class EdgetDetectEffect extends AbstractEffect<EdgeDetectShader> {

	@Override
	protected void updateShader(EdgeDetectShader shader, long enlapsedTime, float speedAdapter) {
		// TODO Auto-generated method stub
		
	}
		
}
