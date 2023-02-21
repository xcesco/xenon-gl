package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationCubicInOut implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationCubicInOut instance;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationCubicInOut() {

	}

	public static InterpolationCubicInOut instance() {
		if(instance == null) {
			instance = new InterpolationCubicInOut();
		}
		return instance;
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
			return 0.5f * InterpolationCubicIn.getValue(2 * percentage);
		} else {
			return 0.5f + 0.5f * InterpolationCubicOut.getValue(percentage * 2 - 1);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
