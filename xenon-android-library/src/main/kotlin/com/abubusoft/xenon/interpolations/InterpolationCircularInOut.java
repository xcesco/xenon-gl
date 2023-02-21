package com.abubusoft.xenon.interpolations;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationCircularInOut implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationCircularInOut INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationCircularInOut() {
	}

	public static InterpolationCircularInOut getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationCircularInOut();
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
			return 0.5f * InterpolationCircularIn.getValue(2 * percentage);
		} else {
			return 0.5f + 0.5f * InterpolationCircularOut.getValue(percentage * 2 - 1);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
