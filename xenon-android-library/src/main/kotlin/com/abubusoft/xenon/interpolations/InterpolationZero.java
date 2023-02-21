package com.abubusoft.xenon.interpolations;

/**
 * <p>
 * Restituisce sempre 0
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class InterpolationZero implements Interpolation {
	private static InterpolationZero instance;

	private InterpolationZero() {

	}

	public static InterpolationZero instance() {
		if (instance == null) {
			instance = new InterpolationZero();
		}
		return instance;
	}

	@Override
	public float getPercentage(final float pSecondsElapsed, final float pDuration) {
		return 0;
	}

}
