package com.abubusoft.xenon.core.sensor.orientation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors

/**
 * The orientation provider that delivers the current orientation from the [ Accelerometer][Sensor.TYPE_ACCELEROMETER] and [Compass][Sensor.TYPE_MAGNETIC_FIELD].
 *
 * @author Alexander Pacha
 */
@AttachedSensors(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD)
object AccelerometerCompassProvider : OrientationProvider() {
    /**
     * Compass values
     */
    private var magnitudeValues: FloatArray? = FloatArray(3)

    /**
     * Accelerometer values
     */
    private var accelerometerValues: FloatArray? = FloatArray(3)
    override fun onSensorChanged(event: SensorEvent) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> magnitudeValues = event.values.clone()
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = event.values.clone()
        }
        if (magnitudeValues != null && accelerometerValues != null) {
            val i = FloatArray(16)

            // Fuse accelerometer with compass
            SensorManager.getRotationMatrix(
                currentOrientationRotationMatrix.matrix, i, accelerometerValues,
                magnitudeValues
            )
            // Transform rotation matrix to quaternion
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix)
        }
        update()
    }
}