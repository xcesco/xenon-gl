package com.abubusoft.xenon.core.sensor.orientation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors

/**
 * The orientation provider that delivers the current orientation from the [Gravity][Sensor.TYPE_GRAVITY] and [Compass][Sensor.TYPE_MAGNETIC_FIELD].
 *
 * @author Alexander Pacha
 */
@AttachedSensors(Sensor.TYPE_GRAVITY, Sensor.TYPE_MAGNETIC_FIELD)
object GravityCompassProvider : OrientationProvider() {
    /**
     * Compass values
     */
    private var magnitudeValues: FloatArray? = FloatArray(3)

    /**
     * Gravity values
     */
    private var gravityValues: FloatArray? = FloatArray(3)
    override fun onSensorChanged(event: SensorEvent) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> magnitudeValues = event.values.clone()
            Sensor.TYPE_GRAVITY -> gravityValues = event.values.clone()
        }
        if (magnitudeValues != null && gravityValues != null) {
            val i = FloatArray(16)

            // Fuse gravity-sensor (virtual sensor) with compass
            SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, i, gravityValues, magnitudeValues)
            // Transform rotation matrix to quaternion
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix)
        }
        update()
    }
}