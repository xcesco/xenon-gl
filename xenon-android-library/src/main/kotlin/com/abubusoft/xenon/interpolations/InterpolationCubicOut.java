package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationCubicOut implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationCubicOut INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationCubicOut() {

	}

	public static InterpolationCubicOut getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationCubicOut();
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
		return InterpolationCubicOut.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		final float t = pPercentage - 1;
		return 1 + (t * t * t);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
