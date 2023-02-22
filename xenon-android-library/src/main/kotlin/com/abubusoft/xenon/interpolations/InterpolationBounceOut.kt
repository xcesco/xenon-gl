package com.abubusoft.xenon.interpolations

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Gil
 * @author Nicolas Gramlich
 * @since 16:52:11 - 26.07.2010
 */
object InterpolationBounceOut : Interpolation {
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    override fun getPercentage(pSecondsElapsed: Float, pDuration: Float): Float {
        return getValue(pSecondsElapsed / pDuration)
    }

    fun getValue(pPercentage: Float): Float {
        return if (pPercentage < 1f / 2.75f) {
            7.5625f * pPercentage * pPercentage
        } else if (pPercentage < 2f / 2.75f) {
            val t = pPercentage - 1.5f / 2.75f
            7.5625f * t * t + 0.75f
        } else if (pPercentage < 2.5f / 2.75f) {
            val t = pPercentage - 2.25f / 2.75f
            7.5625f * t * t + 0.9375f
        } else {
            val t = pPercentage - 2.625f / 2.75f
            7.5625f * t * t + 0.984375f
        }
    }
}