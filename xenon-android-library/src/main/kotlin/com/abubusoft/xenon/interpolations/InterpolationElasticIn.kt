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
object InterpolationElasticIn : Interpolation {

    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return getValue(pSecondsElapsed, pDuration, pSecondsElapsed / pDuration)
    }


    fun getValue(pSecondsElapsed: Float, pDuration: Float, pPercentage: Float): Float {
        if (pSecondsElapsed == 0f) {
            return 0f
        }
        if (pSecondsElapsed == pDuration) {
            return 1f
        }
        val p = pDuration * 0.3f
        val s = p / 4
        val t = pPercentage - 1
        return (-Math.pow(2.0, (10 * t).toDouble()) * Math.sin(((t * pDuration - s) * -XenonMath.PI_TWICE / p).toDouble())).toFloat()
    }
}