package com.abubusoft.xenon.core.sensor.internal

import android.hardware.SensorEventListener

/**
 * Marcatore per le classi detector agganciate ai sensori.
 *
 * @author Francesco Benincasa
 */
interface InputSensorProvider : SensorEventListener {
    /**
     * Recupera il set di sensori agganciati al detector
     * @return
     * elenco dei sensori collegati al detector
     */
    fun getAttachedTypeofSensors(): HashSet<Int>
}