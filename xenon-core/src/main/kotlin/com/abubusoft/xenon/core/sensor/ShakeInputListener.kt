package com.abubusoft.xenon.core.sensor

import com.abubusoft.xenon.core.sensor.internal.InputListener

/**
 * @author Francesco Benincasa
 */
interface ShakeInputListener : InputListener {
    /**
     * generato quando il device viene shakerato
     */
    fun onShake()
}