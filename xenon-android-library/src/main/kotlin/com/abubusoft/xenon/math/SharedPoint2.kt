/**
 * 
 */
package com.abubusoft.xenon.math;

import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.engine.SharedData;
import com.abubusoft.xenon.math.Point2;

/**
 * @author Francesco Benincasa
 *
 */
public class SharedPoint2 implements SharedData {
	
	Point2 values[]=new Point2[2];

	@Override
	public void update() {
		values[0].copyInto(values[1]);
	}
	
	public Point2 get(Phase phase)
	{
		return values[phase.ordinal()];
	}
	
	public Point2 get()
	{
		return values[Phase.LOGIC.ordinal()];
	}

}
