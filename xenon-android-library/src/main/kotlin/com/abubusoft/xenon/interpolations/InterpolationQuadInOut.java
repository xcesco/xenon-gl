package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationQuadInOut implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationQuadInOut INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationQuadInOut() {

	}

	public static InterpolationQuadInOut getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationQuadInOut();
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
			return 0.5f * InterpolationQuadIn.getValue(2 * percentage);
		} else {
			return 0.5f + 0.5f * InterpolationQuadOut.getValue(percentage * 2 - 1);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
