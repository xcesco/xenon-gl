package com.abubusoft.xenon.core.graphic

import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_ALPHA
import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_BLUE
import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_GREEN
import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_HUE
import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_RED
import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_SAT
import com.abubusoft.xenon.core.graphic.ColorRangeMasks.MASK_VALUE

enum class ColorRangeType
/**
 * @param maskValue
 */(
    /**
     * mask
     */
    val mask: Int
) {
    /**
     * Range solo per alpha
     */
    ALPHA(MASK_ALPHA),

    /**
     * Cambiano tutti i componenti ARGB
     */
    RGB(MASK_RED or MASK_BLUE or MASK_GREEN or MASK_ALPHA),

    /**
     * Cambia solo il componente RGB RED
     */
    RGB_RED(MASK_RED),

    /**
     * Cambia il componente RGB green
     */
    RGB_GREEN(MASK_GREEN),

    /**
     * Cambia il componente RGB blue
     */
    RGB_BLUE(MASK_BLUE),

    /**
     * Cambia le componenti HSV
     */
    HSV(MASK_ALPHA or MASK_HUE or MASK_SAT or MASK_VALUE),

    /**
     * Cambia la componente HUE HSV
     */
    HSV_HUE(MASK_HUE),

    /**
     * Cambia la componente SAT
     */
    HSV_SAT(MASK_SAT),

    /**
     * Cambia la componente value HSV
     */
    HSV_VALUE(MASK_VALUE);

    /**
     * @return
     */
    val isWorkingOnRGB: Boolean
        get() = mask and (MASK_RED or MASK_GREEN or MASK_BLUE) > 0

    /**
     * @return
     */
    val isWorkingOnHSV: Boolean
        get() = mask and (MASK_HUE or MASK_SAT or MASK_VALUE) > 0

    /**
     * @return
     */
    val isWorkingOnAlphaChannel: Boolean
        get() = mask and MASK_ALPHA > 0

    /**
     * Verifica se è abilitato una determinata variazione
     *
     * @param maskValue
     * @return
     */
    fun isEnable(maskValue: Int): Boolean {
        return mask and maskValue > 0
    }
}