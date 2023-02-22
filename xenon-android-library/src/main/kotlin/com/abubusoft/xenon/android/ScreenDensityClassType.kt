package com.abubusoft.xenon.android

/**
 * Tipologie di densità.
 *
 * @author Francesco Benincasa
 */
enum class ScreenDensityClassType(
    /**
     * fattore di scala per la classe. Non dovrebbe venire usato
     */
    val scaleFactor: Float,
    /**
     * densità in dpi
     */
    val density: Int
) {
    UNKNOWN(0, 0),

    /**
     * low density
     */
    LDPI(0.75f, 120),

    /**
     * medium density
     */
    MDPI(1f, 160),

    /**
     * high density
     */
    HDPI(1.5f, 240),

    /**
     * extra high density
     */
    XHDPI(2, 320),

    /**
     * extra extra high density
     */
    XXHDPI(3, 480),

    /**
     * extra extra extra high density
     */
    XXXHDPI(4, 640);

    companion object {
        /**
         *
         * A partire da un valore di scala, prova a recuperare la relativa classe dello screen density.
         *
         * @param scaleValue
         * valore di scala
         * @return
         * classe di appartenenza dello screen density
         */
        fun valueFromScale(scaleValue: Float): ScreenDensityClassType {
            var screenDensity = UNKNOWN
            for (item in values()) {
                if (scaleValue >= item.scaleFactor) {
                    screenDensity = item
                }
            }
            return screenDensity
        }
    }
}