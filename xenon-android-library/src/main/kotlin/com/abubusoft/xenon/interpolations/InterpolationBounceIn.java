package com.abubusoft.xenon.interpolations;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationBounceIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationBounceIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationBounceIn() {

	}

	public static InterpolationBounceIn getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationBounceIn();
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
		return InterpolationBounceIn.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		// TODO Inline?
		return 1 - InterpolationBounceOut.getValue(1 - pPercentage);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
