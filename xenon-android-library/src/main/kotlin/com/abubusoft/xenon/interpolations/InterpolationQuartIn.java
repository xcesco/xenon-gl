package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationQuartIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationQuartIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationQuartIn() {

	}

	public static InterpolationQuartIn getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationQuartIn();
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
		return InterpolationQuartIn.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		return pPercentage * pPercentage * pPercentage * pPercentage;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
