package com.abubusoft.xenon.interpolations;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationElasticInOut implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationElasticInOut INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationElasticInOut() {

	}

	public static InterpolationElasticInOut getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationElasticInOut();
		}
		return INSTANCE;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public float getPercentage(final float pSecondsElapsed, final float pDuration) {
		final float percentage = pSecondsElapsed / pDuration;

		if(percentage < 0.5f) {
			return 0.5f * InterpolationElasticIn.getValue(2 * pSecondsElapsed, pDuration, 2 * percentage);
		} else {
			return 0.5f + 0.5f * InterpolationElasticOut.getValue(pSecondsElapsed * 2 - pDuration, pDuration, percentage * 2 - 1);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
