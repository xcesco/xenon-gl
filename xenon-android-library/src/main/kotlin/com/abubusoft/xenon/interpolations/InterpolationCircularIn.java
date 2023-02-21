package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationCircularIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationCircularIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationCircularIn() {

	}

	public static InterpolationCircularIn getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationCircularIn();
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
		return InterpolationCircularIn.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		return (float) -(Math.sqrt(1 - pPercentage * pPercentage) - 1.0f);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
