package com.abubusoft.xenon.interpolations;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationBackIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final float OVERSHOOT_CONSTANT = 1.70158f;

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationBackIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationBackIn() {

	}

	public static InterpolationBackIn getInstance() {
		if(null == INSTANCE) {
			INSTANCE = new InterpolationBackIn();
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
		return InterpolationBackIn.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		return pPercentage * pPercentage * ((OVERSHOOT_CONSTANT + 1) * pPercentage - OVERSHOOT_CONSTANT);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
