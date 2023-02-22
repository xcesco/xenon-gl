/**
 * 
 */
package com.abubusoft.xenon.math;

import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.engine.SharedData;
import com.abubusoft.xenon.math.Point3;

/**
 * @author Francesco Benincasa
 *
 */
public class SharedPoint3 implements SharedData {
	
	Point3 values[]=new Point3[2];

	@Override
	public void update() {
		values[0].copyInto(values[1]);
	}
	
	public Point3 get(Phase phase)
	{
		return values[phase.ordinal()];
	}
	
	public Point3 get()
	{
		return values[Phase.LOGIC.ordinal()];
	}

}
