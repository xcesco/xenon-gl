/**
 * 
 */
package com.abubusoft.xenon.animations;

import com.abubusoft.xenon.math.Vector3;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;

/**
 * @author Francesco Benincasa
 *
 */
@BindType
public class TranslationFrame extends KeyFrame {
	
	public static TranslationFrame build(long duration)
	{
		return build(0f, 0f, 0f, duration);
	}

	public static TranslationFrame build(float x, float y, float z, long duration)
	{
		TranslationFrame frame = new TranslationFrame();
		
		frame.translation.setCoords(x, y, z);
		frame.duration=duration;		
		
		return frame;
	}
	
	@Bind
	public Vector3 translation=new Vector3();

	public TranslationFrame() {
		// è di tipo continuo, quindi l'interpolazione è per lo meno lineare
		//val.interpolation=InterpolationLinear.instance();
	}

}
