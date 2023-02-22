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
object InterpolationSineOut : Interpolation {

    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return getValue(pSecondsElapsed / pDuration)
    }

    fun getValue(pPercentage: Float): Float {
        return Math.sin((pPercentage * XenonMath.PI_HALF).toDouble()).toFloat()
    }
}