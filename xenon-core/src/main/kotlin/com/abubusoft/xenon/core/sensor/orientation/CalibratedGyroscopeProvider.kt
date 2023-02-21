package com.abubusoft.xenon.core.sensor.orientation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors
import com.abubusoft.xenon.core.sensor.orientation.math.Quaternion

/**
 * The orientation provider that delivers the relative orientation from the [ Gyroscope][Sensor.TYPE_GYROSCOPE]. This sensor does not deliver an absolute orientation (with respect to magnetic north and gravity) but
 * only a relative measurement starting from the point where it started.
 *
 * @author Alexander Pacha
 */
@AttachedSensors(Sensor.TYPE_GYROSCOPE)
object CalibratedGyroscopeProvider : OrientationProvider() {
    /**
     * The quaternion that stores the difference that is obtained by the gyroscope.
     * Basically it contains a rotational difference encoded into a quaternion.
     *
     * To obtain the absolute orientation one must add this into an initial position by
     * multiplying it with another quaternion
     */
    private val deltaQuaternion = Quaternion()

    /**
     * The time-stamp being used to record the time when the last gyroscope event occurred.
     */
    private var timestamp: Long = 0

    /**
     * Value giving the total velocity of the gyroscope (will be high, when the device is moving fast and low when
     * the device is standing still). This is usually a value between 0 and 10 for normal motion. Heavy shaking can
     * increase it to about 25. Keep in mind, that these values are time-depended, so changing the sampling rate of
     * the sensor will affect this value!
     */
    private var gyroscopeRotationVelocity = 0.0
    override fun onSensorChanged(event: SensorEvent) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {

            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0L) {
                val dT = (event.timestamp - timestamp) * NS2S
                // Axis of the rotation sample, not normalized yet.
                var axisX = event.values[0]
                var axisY = event.values[1]
                var axisZ = event.values[2]

                // Calculate the angular speed of the sample
                gyroscopeRotationVelocity = Math.sqrt((axisX * axisX + axisY * axisY + axisZ * axisZ).toDouble())

                // Normalize the rotation vector if it's big enough to get the axis
                if (gyroscopeRotationVelocity > EPSILON) {
                    axisX /= gyroscopeRotationVelocity.toFloat()
                    axisY /= gyroscopeRotationVelocity.toFloat()
                    axisZ /= gyroscopeRotationVelocity.toFloat()
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                val thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f
                val sinThetaOverTwo = Math.sin(thetaOverTwo)
                val cosThetaOverTwo = Math.cos(thetaOverTwo)
                deltaQuaternion.x = (sinThetaOverTwo * axisX).toFloat()
                deltaQuaternion.y = (sinThetaOverTwo * axisY).toFloat()
                deltaQuaternion.z = (sinThetaOverTwo * axisZ).toFloat()
                deltaQuaternion.w = -cosThetaOverTwo.toFloat()

                // Matrix rendering in CubeRenderer does not seem to have this problem.
                synchronized(syncToken) {
                    // Move current gyro orientation if gyroscope should be used
                    deltaQuaternion.multiplyByQuat(currentOrientationQuaternion, currentOrientationQuaternion)
                }
                val correctedQuat = currentOrientationQuaternion.clone()
                // We inverted w in the deltaQuaternion, because currentOrientationQuaternion required it.
                // Before converting it back to matrix representation, we need to revert this process
                correctedQuat.w(-correctedQuat.w())
                synchronized(syncToken) {
                    // Set the rotation matrix as well to have both representations
                    SensorManager.getRotationMatrixFromVector(
                        currentOrientationRotationMatrix.matrix,
                        correctedQuat.ToArray()
                    )
                }
            }
            timestamp = event.timestamp
        }
        update()
    }


    /**
     * Constant specifying the factor between a Nano-second and a second
     */
    private const val NS2S = 1.0f / 1000000000.0f

    /**
     * This is a filter-threshold for discarding Gyroscope measurements that are below a certain level and
     * potentially are only noise and not real motion. Values from the gyroscope are usually between 0 (stop) and
     * 10 (rapid rotation), so 0.1 seems to be a reasonable threshold to filter noise (usually smaller than 0.1) and
     * real motion (usually > 0.1). Note that there is a chance of missing real motion, if the use is turning the
     * device really slowly, so this value has to find a balance between accepting noise (threshold = 0) and missing
     * slow user-action (threshold > 0.5). 0.1 seems to work fine for most applications.
     *
     */
    private const val EPSILON = 0.1

}