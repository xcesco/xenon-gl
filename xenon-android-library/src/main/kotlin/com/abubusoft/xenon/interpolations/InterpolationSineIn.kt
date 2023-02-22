package com.abubusoft.xenon.interpolations

import com.abubusoft.xenon.math.XenonMath

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
object InterpolationSineIn : Interpolation {
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return getValue(pSecondsElapsed / pDuration)
    }

    private fun getValue(pPercentage: Float): Float {
        return (-Math.cos((pPercentage * XenonMath.PI_HALF).toDouble()) + 1).toFloat()
    }
}