package com.abubusoft.xenon.interpolations;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationBounceOut implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationBounceOut INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationBounceOut() {

	}

	public static InterpolationBounceOut getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationBounceOut();
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
		return InterpolationBounceOut.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		if(pPercentage < (1f / 2.75f)) {
			return 7.5625f * pPercentage * pPercentage;
		} else if(pPercentage < (2f / 2.75f)) {
			final float t = pPercentage - (1.5f / 2.75f);
			return 7.5625f * t * t + 0.75f;
		} else if(pPercentage < (2.5f / 2.75f)) {
			final float t = pPercentage - (2.25f / 2.75f);
			return 7.5625f * t * t + 0.9375f;
		} else {
			final float t = pPercentage - (2.625f / 2.75f);
			return 7.5625f * t * t + 0.984375f;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
