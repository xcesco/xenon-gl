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
object InterpolationSineInOut : Interpolation {
    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        val percentage = pSecondsElapsed / pDuration
        return (-0.5f * (Math.cos((percentage * XenonMath.PI).toDouble()) - 1)).toFloat()
    }
}