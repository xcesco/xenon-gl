package com.abubusoft.xenon.interpolations;


import com.abubusoft.xenon.math.XenonMath;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
public class InterpolationSineIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationSineIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationSineIn() {

	}

	public static InterpolationSineIn getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationSineIn();
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
		return InterpolationSineIn.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		return (float) (-Math.cos(pPercentage * XenonMath.PI_HALF) + 1);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
