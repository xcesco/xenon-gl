package com.abubusoft.xenon.interpolations

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
object InterpolationExponentialIn : Interpolation {

    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return getValue(pSecondsElapsed / pDuration)
    }

    fun getValue(pPercentage: Float): Float {
        return (if (pPercentage == 0f) 0 else Math.pow(2.0, (10 * (pPercentage - 1)).toDouble()) - 0.001f).toFloat()
    }
}