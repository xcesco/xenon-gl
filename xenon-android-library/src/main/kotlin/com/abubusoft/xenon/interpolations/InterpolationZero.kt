package com.abubusoft.xenon.interpolations

/**
 *
 *
 * Restituisce sempre 0
 *
 *
 * @author Francesco Benincasa
 */
object InterpolationZero : Interpolation {
    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return 0f
    }
}