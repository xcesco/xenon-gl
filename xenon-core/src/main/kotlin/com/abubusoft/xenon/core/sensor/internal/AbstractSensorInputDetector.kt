/**
 *
 */
package com.abubusoft.xenon.core.sensor.internal

import android.hardware.SensorEventListener
import com.abubusoft.xenon.core.XenonRuntimeException

/**
 *
 * Classe astratta per la gestione dei sensori. Questa classe deve gestire sia i sensori hardware
 * lato android, sia i listener lato Elio, in modo da ottenere dei listener sulle informazioni che
 * in effetti servono.
 *
 * @author Francesco Benincasa
 */
abstract class AbstractSensorInputDetector protected constructor() : SensorEventListener, InputSensorProvider {
    /**
     * elenco degli id dei sensori associati
     *
     */
    protected var attachedTypeSensors = HashSet<Int>()

    init {
        val annotations = this.javaClass.getAnnotation(AttachedSensors::class.java)
        if (annotations == null || annotations.value.size == 0) {
            throw XenonRuntimeException("No sensors is defined for detector $javaClass")
        }
        val sensors: IntArray = annotations.value
        for (i in sensors.indices) {
            attachedTypeSensors.add(sensors[i])
        }
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.sensor.internal.InputSensorDetector#getAttachedTypeofSensors()
	 */
    override fun getAttachedTypeofSensors(): HashSet<Int> {
        return attachedTypeSensors
    }

    /**
     * Se true indica che il detector è interessato al tipo di sensore.
     *
     * @param sensorType
     *
     * @return
     * true se l'evento è interessante per questo detector
     */
    fun isInterestIn(sensorType: Int): Boolean {
        return attachedTypeSensors.contains(sensorType)
    }
}