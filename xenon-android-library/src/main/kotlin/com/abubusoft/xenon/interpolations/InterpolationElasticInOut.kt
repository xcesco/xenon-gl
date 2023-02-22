package com.abubusoft.xenon.interpolations

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
object InterpolationElasticInOut : Interpolation {

    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        val percentage = pSecondsElapsed / pDuration
        return if (percentage < 0.5f) {
            0.5f * InterpolationElasticIn.Companion.getValue(2 * pSecondsElapsed, pDuration, 2 * percentage)
        } else {
            0.5f + 0.5f * InterpolationElasticOut.getValue(pSecondsElapsed * 2 - pDuration, pDuration, percentage * 2 - 1)
        }
    }
}