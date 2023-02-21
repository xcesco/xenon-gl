/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * @author Francesco Benincasa
 *
 */
@UseShader(GreyscaleShader.class)
public class GreyscaleEffect extends AbstractEffect<GreyscaleShader> {

	@Override
	protected void updateShader(GreyscaleShader shader, long enlapsedTime, float speedAdapter) {
		// TODO Auto-generated method stub
		
	}
		
}
