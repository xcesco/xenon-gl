package com.abubusoft.xenon.interpolations

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
object InterpolationExponentialInOut : Interpolation {

    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        val percentage = pSecondsElapsed / pDuration
        return if (percentage < 0.5f) {
            0.5f * InterpolationExponentialIn.Companion.getValue(2 * percentage)
        } else {
            0.5f + 0.5f * InterpolationExponentialOut.getValue(percentage * 2 - 1)
        }
    }

}