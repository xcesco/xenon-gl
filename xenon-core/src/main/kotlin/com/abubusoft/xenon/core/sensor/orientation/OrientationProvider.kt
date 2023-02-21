/**
 *
 */
package com.abubusoft.xenon.core.sensor.orientation

import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Pair
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.core.sensor.ElioSensorManager
import com.abubusoft.xenon.core.sensor.OrientationInputConfig
import com.abubusoft.xenon.core.sensor.OrientationInputListener
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors
import com.abubusoft.xenon.core.sensor.internal.InputSensorProvider
import com.abubusoft.xenon.core.sensor.orientation.math.EulerAngles
import com.abubusoft.xenon.core.sensor.orientation.math.Matrixf4x4
import com.abubusoft.xenon.core.sensor.orientation.math.Quaternion

/**
 * Classes implementing this interface provide an orientation of the device
 * either by directly accessing hardware, using Android sensor fusion or fusing
 * sensors itself.
 *
 * The orientation can be provided as rotation matrix or quaternion.
 *
 * @author Alexander Pacha
 */
abstract class OrientationProvider : InputSensorProvider {
    /**
     * elenco degli id dei sensori associati
     *
     */
    protected var attachedTypeSensors = HashSet<Int>()

    /**
     * The quaternion that holds the current rotation
     */
    protected val currentOrientationQuaternion: Quaternion = Quaternion()

    /**
     * The matrix that holds the current rotation
     */
    public val currentOrientationRotationMatrix: Matrixf4x4 = Matrixf4x4()
    protected val listeners = ArrayList<Pair<OrientationInputConfig, OrientationInputListener>>()

    /**
     * The list of sensors used by this provider
     */
    protected var sensorList: List<Sensor> = ArrayList()

    /**
     * Sync-token for syncing read/write to sensor-data from sensor manager and
     * fusion algorithm
     */
    protected val syncToken = Any()

    /**
     * aggiunta listener per i sensori di orientamento
     *
     * @param configValue
     * @param listenerValue
     */
    fun addListener(configValue: OrientationInputConfig, listenerValue: OrientationInputListener) {
        listeners.add(Pair(configValue, listenerValue))
        n = listeners.size
    }

    fun clearListeners() {
        listeners.clear()
        n = 0
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.sensor.internal.InputSensorDetector#getAttachedTypeofSensors()
	 */
    override fun getAttachedTypeofSensors(): HashSet<Int> {
        return attachedTypeSensors
    }

    /**
     * @return Returns the current rotation of the device in the Euler-Angles
     */
    val eulerAngles: EulerAngles
        get() {
            synchronized(syncToken) {
                val angles = FloatArray(3)
                SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles)
                return EulerAngles(angles[0], angles[1], angles[2])
            }
        }

    /**
     * @return Returns the current rotation of the device in the quaternion
     * format (vector4f)
     */
    val quaternion: Quaternion
        get() {
            synchronized(syncToken) { return currentOrientationQuaternion.clone() }
        }

    /**
     * @return Returns the current rotation of the device in the rotation matrix
     * format (4x4 matrix)
     */
    val rotationMatrix: Matrixf4x4
        get() {
            synchronized(syncToken) { return currentOrientationRotationMatrix }
        }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not doing anything
    }

    /**
     * Starts the sensor fusion (e.g. when resuming the activity)
     */
    fun start() {
        val sensorManager = ElioSensorManager.instance().sensorManager

        // enable our sensor when the activity is resumed, ask for
        // 10 ms updates.
        for (sensor in sensorList) {
            // enable our sensors when the activity is resumed, ask for
            // 20 ms updates (Sensor_delay_game)
            sensorManager!!.registerListener(
                this, sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    protected var currentAzimuth = 0.0
    protected var currentPitch = 0.0
    protected var currentRoll = 0.0
    var i = 0
    var n = 0
    var tempResult = DoubleArray(3)
    private var item: Pair<OrientationInputConfig, OrientationInputListener>? = null
    //private var currentConfig: OrientationInputConfig
    //private var currentListener: OrientationInputListener? = null

    /**
     * Initialises a new OrientationProvider
     *
     */
    init {
        // Initialise with identity

        // Initialise with identity
        val annotations = this.javaClass.getAnnotation(AttachedSensors::class.java)
        if (annotations == null || annotations.value.size == 0) {
            throw XenonRuntimeException("No sensors is defined for detector $javaClass")
        }
        val sensors: IntArray = annotations.value
        for (i in sensors.indices) {
            attachedTypeSensors.add(sensors[i])
        }
    }

    /**
     * aggiorna i listener
     */
    fun update() {
        if (n == 0) return
        currentOrientationQuaternion.toEulerAngles(tempResult)
        currentAzimuth = Math.toDegrees(tempResult[0])
        currentPitch = Math.toDegrees(tempResult[1])
        currentRoll = Math.toDegrees(tempResult[2])

        var currentConfig: OrientationInputConfig
        var currentListener: OrientationInputListener? = null

        i = 0
        while (i < n) {
            item = listeners[i]
            currentConfig = item!!.first
            currentListener = item!!.second
            currentConfig.currentAzimuth = currentAzimuth
            currentConfig.currentPitch = currentPitch
            currentConfig.currentRoll = currentRoll
            // Logger.info("onSensorChanged update %s",i);
            var somethingIsChanged =
                Math.abs(currentConfig.currentAzimuth - currentConfig.oldAzimuth) > currentConfig.noiseNearZero ||
                    Math.abs(currentConfig.currentRoll - currentConfig.oldRoll) > currentConfig.noiseNearZero ||
                    Math.abs(currentConfig.currentPitch - currentConfig.oldPitch) > currentConfig.noiseNearZero

            // notifichiamo evento se dobbiamo farlo sempre o se la notifica è
            // abilitata per i cambiamenti e qualcosa
            // effettivamente è cambiato
            if (currentConfig.event === OrientationInputConfig.EventType.NOTIFY_ALWAYS || somethingIsChanged && currentConfig.event === OrientationInputConfig.EventType.NOTIFY_CHANGES) {
                currentListener.update(
                    currentConfig.currentAzimuth,
                    currentConfig.currentPitch,
                    currentConfig.currentRoll,
                    currentConfig.currentAzimuth - currentConfig.oldAzimuth,
                    currentConfig.currentRoll - currentConfig.oldRoll,
                    currentConfig.currentPitch - currentConfig.oldPitch,
                    somethingIsChanged
                )

                // se i nuovi valori sono stati registrati allora registriamo anche gli old value
                currentConfig.oldAzimuth = currentConfig.currentAzimuth
                currentConfig.oldRoll = currentConfig.currentRoll
                currentConfig.oldPitch = currentConfig.currentPitch
            }
            i++
        }
    }

    /**
     * Stops the sensor fusion (e.g. when pausing/suspending the activity)
     */
    fun stop() {
        val sensorManager = ElioSensorManager.instance().sensorManager

        // make sure to turn our sensors off when the activity is paused
        for (sensor in sensorList) {
            sensorManager!!.unregisterListener(this, sensor)
        }
    }
}