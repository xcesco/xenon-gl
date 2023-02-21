package com.abubusoft.xenon.core.sensor.internal

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Pair
import com.abubusoft.xenon.core.sensor.ShakeInputConfig
import com.abubusoft.xenon.core.sensor.ShakeInputListener

@AttachedSensors(Sensor.TYPE_ACCELEROMETER)
class ShakeInputDetector : AbstractSensorInputDetector() {
    /**
     * Arrays to store gravity
     */
    private val gravity = floatArrayOf(0.0f, 0.0f, 0.0f)

    /**
     * linear acceleration values
     */
    private val linearAcceleration = floatArrayOf(0.0f, 0.0f, 0.0f)
    var i = 0
    var n = 0


    override fun onSensorChanged(event: SensorEvent) {
        // This method will be called event the accelerometer detects a change.

        // Call a helper method that wraps code from the Android developer site
        setCurrentAcceleration(event)

        // Get the max linear acceleration in any direction
        val maxLinearAcceleration = maxCurrentLinearAcceleration
        var listener: ShakeInputListener
        var config: ShakeInputConfig
        i = 0
        while (i < n) {
            config = listeners[i].first
            listener = listeners[i].second
            // Check if the acceleration is greater than our minimum threshold
            if (maxLinearAcceleration > config.minShakeAcceleration) {
                val now = System.currentTimeMillis()

                // Set the startTime if it was reset to zero
                if (config.startTime == 0L) {
                    config.startTime = now
                }
                val elapsedTime = now - config.startTime

                // Check if we're still in the shake window we defined
                if (elapsedTime > config.maxShakeDuration) {
                    // Too much time has passed. Start over!
                    resetShakeDetection(config)
                } else {
                    // Keep track of all the movements
                    config.moveCount++

                    // Check if enough movements have been made to qualify as a
                    // shake
                    if (config.moveCount > config.minMovements) {
                        // It's a shake! Notify the listener.
                        listener.onShake()

                        // Reset for the next one!
                        resetShakeDetection(config)
                    }
                }
            }
            i++
        }
    }

    private fun setCurrentAcceleration(event: SensorEvent) {
        /*
		 * BEGIN SECTION from Android developer site. This code accounts for
		 * gravity using a high-pass filter
		 */

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate
        val alpha = 0.8f

        // Gravity components of x, y, and z acceleration
        gravity[X] = alpha * gravity[X] + (1 - alpha) * event.values[X]
        gravity[Y] = alpha * gravity[Y] + (1 - alpha) * event.values[Y]
        gravity[Z] = alpha * gravity[Z] + (1 - alpha) * event.values[Z]

        // Linear acceleration along the x, y, and z axes (gravity effects
        // removed)
        linearAcceleration[X] = event.values[X] - gravity[X]
        linearAcceleration[Y] = event.values[Y] - gravity[Y]
        linearAcceleration[Z] = event.values[Z] - gravity[Z]

        /*
		 * END SECTION from Android developer site
		 */
    }
    // Start by setting the value to the x value

    // Check if the y value is greater

    // Check if the z value is greater

    // Return the greatest value
    private val maxCurrentLinearAcceleration: Float
        private get() {
            // Start by setting the value to the x value
            var maxLinearAcceleration = linearAcceleration[X]

            // Check if the y value is greater
            if (linearAcceleration[Y] > maxLinearAcceleration) {
                maxLinearAcceleration = linearAcceleration[Y]
            }

            // Check if the z value is greater
            if (linearAcceleration[Z] > maxLinearAcceleration) {
                maxLinearAcceleration = linearAcceleration[Z]
            }

            // Return the greatest value
            return maxLinearAcceleration
        }

    private fun resetShakeDetection(config: ShakeInputConfig?) {
        config!!.startTime = 0
        config.moveCount = 0
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    private val listeners = ArrayList<Pair<ShakeInputConfig, ShakeInputListener>>()
    fun clearListeners() {
        listeners.clear()
    }

    fun addListener(configValue: ShakeInputConfig, listenerValue: ShakeInputListener) {
        listeners.add(Pair(configValue, listenerValue))
        n = listeners.size
        resetShakeDetection(configValue)
    }

    companion object {
        // Indexes for x, y, and z values
        private const val X = 0
        private const val Y = 1
        private const val Z = 2
        fun instance(): ShakeInputDetector {
            return instance
        }

        private val instance = ShakeInputDetector()
    }
}