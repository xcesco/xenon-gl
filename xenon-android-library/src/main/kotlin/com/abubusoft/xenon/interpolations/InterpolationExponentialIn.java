package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationExponentialIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationExponentialIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationExponentialIn() {

	}

	public static InterpolationExponentialIn getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationExponentialIn();
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
		return InterpolationExponentialIn.getValue(pSecondsElapsed / pDuration);

	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		return (float) ((pPercentage == 0) ? 0 : Math.pow(2, 10 * (pPercentage - 1)) - 0.001f);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
