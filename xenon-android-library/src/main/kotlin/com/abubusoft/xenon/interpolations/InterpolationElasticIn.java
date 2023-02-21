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
public class InterpolationElasticIn implements Interpolation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static InterpolationElasticIn INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private InterpolationElasticIn() {

	}

	public static InterpolationElasticIn getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InterpolationElasticIn();
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
		return InterpolationElasticIn.getValue(pSecondsElapsed, pDuration, pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pSecondsElapsed, final float pDuration, final float pPercentage) {
		if(pSecondsElapsed == 0) {
			return 0;
		}
		if(pSecondsElapsed == pDuration) {
			return 1;
		}

		final float p = pDuration * 0.3f;
		final float s = p / 4;

		final float t = pPercentage - 1;
		return (float) (-Math.pow(2, 10 * t) * Math.sin((t * pDuration - s) * -XenonMath.PI_TWICE / p));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
