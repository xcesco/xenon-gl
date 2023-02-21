/**
 * 
 */
package com.abubusoft.xenon.math;

import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.engine.SharedData;
import com.abubusoft.xenon.math.Vector3;

/**
 * @author Francesco Benincasa
 *
 */
public class SharedVector3 implements SharedData {
	
	Vector3 values[]=new Vector3[2];

	@Override
	public void update() {
		values[0].copyInto(values[1]);
	}
	
	public Vector3 get(Phase phase)
	{
		return values[phase.ordinal()];
	}

	public Vector3 get()
	{
		return values[Phase.LOGIC.ordinal()];
	}
}
