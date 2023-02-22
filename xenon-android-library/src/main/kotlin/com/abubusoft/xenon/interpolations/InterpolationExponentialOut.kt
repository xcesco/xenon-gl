package com.abubusoft.xenon.interpolations

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
object InterpolationExponentialOut : Interpolation {

    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return getValue(pSecondsElapsed / pDuration)
    }

    fun getValue(pPercentage: Float): Float {
        return if (pPercentage == 1f) 1f else -Math.pow(2.0, (-10 * pPercentage).toDouble()).toFloat() + 1
    }
}