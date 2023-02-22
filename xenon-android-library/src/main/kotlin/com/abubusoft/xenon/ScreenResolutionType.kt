package com.abubusoft.xenon

import com.abubusoft.xenon.math.XenonMath

/**
 * Inquadra la risoluzione dello schermo in base alla tabella trovata [qua](http://www.androidworld.it/il-dizionario-di-android/android-dalla-a-alla-z-cosa-sono-fhd-4k-hd-qhd-e-qhd/)>
 *
 * @author Francesco Benincasa
 */
enum class ScreenResolutionType(var width: Int, var height: Int) {
    /**
     * 320 x 240 pixel
     */
    QVGA(320, 240),

    /**
     * 480 x 320 pixel
     */
    HVGA(480, 320),

    /**
     * 800 x 480 pixel
     */
    WVGA(800, 480),

    /**
     * 854 x 480 pixel
     */
    FWVGA(854, 480),

    /**
     * 960 x 540 pixel (ovvero un quarto del Full HD)
     */
    qHD(960, 540),

    /**
     * 1024 x 600 pixel (risoluzione solitamente destinata ai tablet)
     */
    WSVGA(1024, 600),

    /**
     * 1024 x 768 pixel (formato utilizzato per i tablet in 4:3)
     */
    XGA(1024, 768),

    /**
     * 1280 x 720 pixel (difficilmente però utilizzata sui tablet in quanto in 16:9)
     */
    HD(1280, 720),

    /**
     * 1280 x 800 pixel (preferita alla HD sui tablet, perché in 16:10)
     */
    WXGA(1280, 800),

    /**
     * 1920 x 1080 pixel (ovvero il Full HD)
     */
    FHD(1920, 1080),

    /**
     * 1920 x 1200 pixel (variante in 16:10 del FHD, solitamente utilizzata sui tablet)
     */
    WUXGA(1920, 1200),

    /**
     * 2K: 2560 x 1440 pixel (il doppio dei pixel del Full HD, la metà del 4K)
     */
    QHD(2560, 1440),

    /**
     * 2560 x 1600 pixel (risoluzione in 16:10, destinata ai tablet)
     */
    WQXGA(2560, 1600),

    /**
     * 4K: 3840 x 2160 pixel (quattro volte il Full HD, chiamata anche Ultra HD)
     */
    UHD(3840, 2160),

    /**
     * 3840 x 2400 pixel (Ultra HD in 16:10 per tablet)
     */
    WQUXGA(3840, 2400),

    /**
     * 4K, 4096 x 2560 pixel
     */
    U_4K(4096, 2560),

    /**
     * FUHD / 8K: 7680 x 4320 pixel
     */
    FUHD(7680, 4320),

    /**
     * QUHD / 16K: 15360 x 8640 pixel
     */
    QUHD(15360, 8640);

    companion object {
        /**
         * Restituisce il best match della risoluzione dello schermo
         *
         * @param screen
         * screen info associato
         * @return
         * ScreenResolutionType dello schermo o null.
         */
        fun findMatch(width: Int, height: Int): ScreenResolutionType? {
            // mettiamolo in landscape
            val widthCurrent = XenonMath.max(width, height)
            val heightCurrent = XenonMath.min(width, height)
            for (item in values()) {
                if (widthCurrent <= item.width && heightCurrent <= item.height) {
                    return item
                }
            }
            return null
        }
    }
}