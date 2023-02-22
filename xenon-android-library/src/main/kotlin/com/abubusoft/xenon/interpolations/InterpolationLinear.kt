package com.abubusoft.xenon.interpolations

/**
 *
 *
 * Interpolazione lineare.
 *
 *
 * @author Francesco Benincasa
 */
object InterpolationLinear : Interpolation {
    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return pSecondsElapsed / pDuration
    }

}