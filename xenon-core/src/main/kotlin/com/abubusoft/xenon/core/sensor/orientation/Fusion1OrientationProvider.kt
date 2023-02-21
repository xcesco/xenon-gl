package com.abubusoft.xenon.core.sensor.orientation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors
import com.abubusoft.xenon.core.sensor.orientation.math.Quaternion

/**
 * The orientation provider that delivers the absolute orientation from the [Gyroscope][Sensor.TYPE_GYROSCOPE] and [Android Rotation Vector sensor][Sensor.TYPE_ROTATION_VECTOR].
 *
 * It mainly relies on the gyroscope, but corrects with the Android Rotation Vector which also provides an absolute estimation of current orientation. The correction is a static weight.
 *
 * @author Alexander Pacha
 */
@AttachedSensors(Sensor.TYPE_GYROSCOPE, Sensor.TYPE_ROTATION_VECTOR)
class Fusion1OrientationProvider : OrientationProvider() {
    /**
     * The quaternion that stores the difference that is obtained by the gyroscope. Basically it contains a rotational difference encoded into a quaternion.
     *
     * To obtain the absolute orientation one must add this into an initial position by multiplying it with another quaternion
     */
    private val deltaQuaternion = Quaternion()

    /**
     * The Quaternions that contain the current rotation (Angle and axis in Quaternion format) of the Gyroscope
     */
    private val quaternionGyroscope = Quaternion()

    /**
     * The quaternion that contains the absolute orientation as obtained by the rotationVector sensor.
     */
    private val quaternionRotationVector = Quaternion()

    /**
     * The time-stamp being used to record the time when the last gyroscope event occurred.
     */
    private var timestamp: Long = 0

    /**
     * Value giving the total velocity of the gyroscope (will be high, when the device is moving fast and low when the device is standing still). This is usually a value between 0 and 10 for normal motion. Heavy shaking can increase it to
     * about 25. Keep in mind, that these values are time-depended, so changing the sampling rate of the sensor will affect this value!
     */
    private var gyroscopeRotationVelocity = 0.0

    /**
     * Flag indicating, whether the orientations were initialised from the rotation vector or not. If false, the gyroscope can not be used (since it's only meaningful to calculate differences from an initial state). If true, the gyroscope
     * can be used normally.
     */
    private var positionInitialised = false

    /**
     * Counter that sums the number of consecutive frames, where the rotationVector and the gyroscope were significantly different (and the dot-product was smaller than 0.7). This event can either happen when the angles of the rotation
     * vector explode (e.g. during fast tilting) or when the device was shaken heavily and the gyroscope is now completely off.
     */
    private var panicCounter = 0
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                // Process rotation vector (just safe it)
                val q = FloatArray(4)
                // Calculate angle. Starting with API_18, Android will provide this value as event.values[3], but if not, we have to calculate it manually.
                SensorManager.getQuaternionFromVector(q, event.values)

                // Store in quaternion
                quaternionRotationVector.setXYZW(q[1], q[2], q[3], -q[0])
                if (!positionInitialised) {
                    // Override
                    quaternionGyroscope.set(quaternionRotationVector)
                    positionInitialised = true
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                // Process Gyroscope and perform fusion

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

                    // Move current gyro orientation
                    deltaQuaternion.multiplyByQuat(quaternionGyroscope, quaternionGyroscope)

                    // Calculate dot-product to calculate whether the two orientation sensors have diverged
                    // (if the dot-product is closer to 0 than to 1), because it should be close to 1 if both are the same.
                    val dotProd = quaternionGyroscope.dotProduct(quaternionRotationVector)

                    // If they have diverged, rely on gyroscope only (this happens on some devices when the rotation vector "jumps").
                    if (Math.abs(dotProd) < OUTLIER_THRESHOLD) {
                        // Increase panic counter
                        if (Math.abs(dotProd) < OUTLIER_PANIC_THRESHOLD) {
                            panicCounter++
                        }

                        // Directly use Gyro
                        setOrientationQuaternionAndMatrix(quaternionGyroscope)
                    } else {
                        // Both are nearly saying the same. Perform normal fusion.

                        // Interpolate with a fixed weight between the two absolute quaternions obtained from gyro and rotation vector sensors
                        // The weight should be quite low, so the rotation vector corrects the gyro only slowly, and the output keeps responsive.
                        val interpolate = Quaternion()
                        quaternionGyroscope.slerp(quaternionRotationVector, interpolate, DIRECT_INTERPOLATION_WEIGHT)

                        // Use the interpolated value between gyro and rotationVector
                        setOrientationQuaternionAndMatrix(interpolate)
                        // Override current gyroscope-orientation
                        quaternionGyroscope.copyVec4(interpolate)

                        // Reset the panic counter because both sensors are saying the same again
                        panicCounter = 0
                    }
                    if (panicCounter > PANIC_THRESHOLD) {
                        Log.d("Rotation Vector", "Panic counter is bigger than threshold; this indicates a Gyroscope failure. Panic reset is imminent.")
                        if (gyroscopeRotationVelocity < 3) {
                            Log.d("Rotation Vector", "Performing Panic-reset. Resetting orientation to rotation-vector value.")

                            // Manually set position to whatever rotation vector says.
                            setOrientationQuaternionAndMatrix(quaternionRotationVector)
                            // Override current gyroscope-orientation with corrected value
                            quaternionGyroscope.copyVec4(quaternionRotationVector)
                            panicCounter = 0
                        } else {
                            Log.d(
                                "Rotation Vector",
                                String.format(
                                    "Panic reset delayed due to ongoing motion (user is still shaking the device). Gyroscope Velocity: %.2f > 3",
                                    gyroscopeRotationVelocity
                                )
                            )
                        }
                    }
                }
                timestamp = event.timestamp
            }
        }
        update()
    }

    /**
     * Sets the output quaternion and matrix with the provided quaternion and synchronises the setting
     *
     * @param quaternion
     * The Quaternion to set (the result of the sensor fusion)
     */
    private fun setOrientationQuaternionAndMatrix(quaternion: Quaternion) {
        val correctedQuat = quaternion.clone()
        // We inverted w in the deltaQuaternion, because currentOrientationQuaternion required it.
        // Before converting it back to matrix representation, we need to revert this process
        correctedQuat.w(-correctedQuat.w())
        synchronized(syncToken) {

            // Use gyro only
            currentOrientationQuaternion.copyVec4(quaternion)

            // Set the rotation matrix as well to have both representations
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, correctedQuat.ToArray())
        }
    }

    companion object {
        /**
         * Constant specifying the factor between a Nano-second and a second
         */
        private const val NS2S = 1.0f / 1000000000.0f

        /**
         * This is a filter-threshold for discarding Gyroscope measurements that are below a certain level and potentially are only noise and not real motion. Values from the gyroscope are usually between 0 (stop) and 10 (rapid rotation), so
         * 0.1 seems to be a reasonable threshold to filter noise (usually smaller than 0.1) and real motion (usually > 0.1). Note that there is a chance of missing real motion, if the use is turning the device really slowly, so this value has
         * to find a balance between accepting noise (threshold = 0) and missing slow user-action (threshold > 0.5). 0.1 seems to work fine for most applications.
         *
         */
        private const val EPSILON = 0.1

        /**
         * This weight determines directly how much the rotation sensor will be used to correct (in Sensor-fusion-scenario 1 - SensorSelection.GyroscopeAndRotationVector). Must be a value between 0 and 1. 0 means that the system entirely relies
         * on the gyroscope, whereas 1 means that the system relies entirely on the rotationVector.
         */
        private const val DIRECT_INTERPOLATION_WEIGHT = 0.005f

        /**
         * The threshold that indicates an outlier of the rotation vector. If the dot-product between the two vectors (gyroscope orientation and rotationVector orientation) falls below this threshold (ideally it should be 1, if they are exactly
         * the same) the system falls back to the gyroscope values only and just ignores the rotation vector.
         *
         * This value should be quite high (> 0.7) to filter even the slightest discrepancies that causes jumps when tiling the device. Possible values are between 0 and 1, where a value close to 1 means that even a very small difference
         * between the two sensors will be treated as outlier, whereas a value close to zero means that the almost any discrepancy between the two sensors is tolerated.
         */
        private const val OUTLIER_THRESHOLD = 0.85f

        /**
         * The threshold that indicates a massive discrepancy between the rotation vector and the gyroscope orientation. If the dot-product between the two vectors (gyroscope orientation and rotationVector orientation) falls below this
         * threshold (ideally it should be 1, if they are exactly the same), the system will start increasing the panic counter (that probably indicates a gyroscope failure).
         *
         * This value should be lower than OUTLIER_THRESHOLD (0.5 - 0.7) to only start increasing the panic counter, when there is a huge discrepancy between the two fused sensors.
         */
        private const val OUTLIER_PANIC_THRESHOLD = 0.65f

        /**
         * The threshold that indicates that a chaos state has been established rather than just a temporary peak in the rotation vector (caused by exploding angled during fast tilting).
         *
         * If the chaosCounter is bigger than this threshold, the current position will be reset to whatever the rotation vector indicates.
         */
        private const val PANIC_THRESHOLD = 60
        var instance: Fusion1OrientationProvider? = null
        fun instance(): Fusion1OrientationProvider? {
            if (instance == null) {
                instance = Fusion1OrientationProvider()
            }
            return instance
        }
    }
}