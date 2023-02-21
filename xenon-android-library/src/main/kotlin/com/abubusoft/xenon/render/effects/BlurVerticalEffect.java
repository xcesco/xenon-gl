/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * 
 * http://www.gamerendering.com/2008/10/11/gaussian-blur-filter-shader/
 * 
 * @author Francesco Benincasa
 *
 */
@UseShader(BlurVerticalShader.class)
public class BlurVerticalEffect extends AbstractEffect<BlurVerticalShader> {

	@Override
	protected void updateShader(BlurVerticalShader shader, long enlapsedTime, float speedAdapter) {
		// TODO Auto-generated method stub
		
	}
		
}
