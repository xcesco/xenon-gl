package com.abubusoft.xenon.core.sensor.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.view.Display
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.core.sensor.OrientationInputConfig
import com.abubusoft.xenon.core.sensor.OrientationInputListener
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors
import com.abubusoft.xenon.core.sensor.internal.InputSensorProvider

@AttachedSensors(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD)
class CompassProvider : InputSensorProvider {
    /**
     * elenco degli id dei sensori associati
     */
    protected var attachedTypeSensors = HashSet<Int>()
    private val mGravity = FloatArray(3)
    private val mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var pitch = 0f
    private var roll = 0f
    private val display: Display? = null
    protected fun lowPass(input: FloatArray, output: FloatArray) {
        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
    }//ex.printStackTrace();

    /**
     * Recupera la rotazione del display
     *
     * @return
     */
    val displayRotation: Int
        get() {
            var result = 1
            try {
                result = display!!.rotation
            } catch (ex: Exception) {
                //ex.printStackTrace();
            }
            return result
        }
    var averageHeading = AngleLowpassFilter()
    var averagePitch = AngleLowpassFilter()
    var averageRoll = AngleLowpassFilter()
    var orientation = FloatArray(3)
    var found1 = false
    var found2 = false
    var R = FloatArray(9)
    var I = FloatArray(9)
    var currentTime: Long = 0
    var lastTime: Long = 0
    private var config: OrientationInputConfig? = null
    private var listener: OrientationInputListener? = null

    init {
        val annotations = this.javaClass.getAnnotation(AttachedSensors::class.java)
        if (annotations == null || annotations.value.isEmpty()) {
            throw XenonRuntimeException("No sensors is defined for detector $javaClass")
        }
        val sensors: IntArray = annotations.value
        for (i in sensors.indices) {
            attachedTypeSensors.add(sensors[i])
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                found1 = true
                lowPass(event.values.clone(), mGravity)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                found2 = true
                lowPass(event.values.clone(), mGeomagnetic)
            }
        }
        if (found1 != found2) return
        val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
        if (success) {
            found1 = false
            found2 = false
            //Logger.info("Eseguo per %s",event.sensor.getType());
            SensorManager.getOrientation(R, orientation)
            // Log.d(TAG, "azimuth (rad): " + azimuth);
            averageHeading.add(orientation[0])
            averagePitch.add(orientation[1])
            averageRoll.add(orientation[2])

            // se non è trascorso il tempo minimo (il delta) andiamo avanti.
            currentTime = System.currentTimeMillis()
            if (currentTime - lastTime < config!!.deltaTime) return
            lastTime = currentTime
            azimuth = Math.toDegrees(averageHeading.average().toDouble()).toFloat() // orientation
            azimuth = (azimuth + 360) % 360
            pitch = Math.toDegrees(averagePitch.average().toDouble()).toFloat() // orientation
            roll = Math.toDegrees(averageRoll.average().toDouble()).toFloat() // orientation
            update(azimuth, pitch, roll)
        }
    }

    /**
     * aggiunta listener per i sensori di orientamento
     *
     * @param configValue
     * @param listenerValue
     */
    fun setListener(configValue: OrientationInputConfig?, listenerValue: OrientationInputListener?) {
        config = configValue
        listener = listenerValue
    }

    fun clearListener() {
        listener = null
    }

    /**
     * aggiorna i listener
     *
     * @param currentAzimuth
     * @param currentPitch
     * @param currentRoll
     */
    fun update(currentAzimuth: Float, currentPitch: Float, currentRoll: Float) {
        if (listener == null) return
        config!!.currentAzimuth = currentAzimuth.toDouble()
        config!!.currentPitch = currentPitch.toDouble()
        config!!.currentRoll = currentRoll.toDouble()
        // Logger.info("onSensorChanged update %s",i);
        val somethingIsChanged =
            Math.abs(config!!.currentAzimuth - config!!.oldAzimuth) > config!!.noiseNearZero || Math.abs(config!!.currentRoll - config!!.oldRoll) > config!!.noiseNearZero || Math.abs(
                config!!.currentPitch - config!!.oldPitch
            ) > config!!.noiseNearZero

        // notifichiamo evento se dobbiamo farlo sempre o se la notifica è
        // abilitata per i cambiamenti e qualcosa
        // effettivamente è cambiato
        if (config!!.event == OrientationInputConfig.EventType.NOTIFY_ALWAYS || somethingIsChanged && config!!.event == OrientationInputConfig.EventType.NOTIFY_CHANGES) {
            listener!!.update(
                config!!.currentAzimuth,
                config!!.currentPitch,
                config!!.currentRoll,
                config!!.currentAzimuth - config!!.oldAzimuth,
                config!!.currentRoll - config!!.oldRoll,
                config!!.currentPitch - config!!.oldPitch,
                somethingIsChanged
            )
            config!!.oldAzimuth = config!!.currentAzimuth
            config!!.oldRoll = config!!.currentRoll
            config!!.oldPitch = config!!.currentPitch
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun getAttachedTypeofSensors(): HashSet<Int> {
        return attachedTypeSensors
    }

    companion object {
        var instance: CompassProvider? = null
        fun instance(): CompassProvider? {
            if (instance == null) {
                instance = CompassProvider()
            }
            return instance
        }

        private const val ALPHA = 0.2f
    }
}