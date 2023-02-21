package com.abubusoft.xenon.android.surfaceview

/**
 * A simple class keeping track of the mean of a stream of values within a certain window. the WindowedMean will only return a value in case enough data has been sampled. After enough data has been sampled the oldest sample will be replaced
 * by the newest in case a new sample is added.
 *
 * @author badlogicgames@gmail.com
 */
class WindowedMean(window_size: Int) {
    var values: FloatArray
    var added_values = 0
    var last_value = 0
    var mean = 0f
    var dirty = true

    /**
     * constructor, window_size specifies the number of samples we will continuously get the mean and variance from. the class will only return meaning full values if at least window_size values have been added.
     *
     * @param window_size
     * size of the sample window
     */
    init {
        values = FloatArray(window_size)
    }

    /** @return whether the value returned will be meaningful
     */
    fun hasEnoughData(): Boolean {
        return added_values >= values.size
    }

    /** clears this WindowedMean. The class will only return meaningful values after enough data has been added again.  */
    fun clear() {
        added_values = 0
        last_value = 0
        for (i in values.indices) values[i] = 0f
        dirty = true
    }

    /**
     * adds a new sample to this mean. In case the window is full the oldest value will be replaced by this new value.
     *
     * @param value
     * The value to add
     */
    fun addValue(value: Float) {
        if (added_values < values.size) added_values++
        values[last_value++] = value
        if (last_value > values.size - 1) last_value = 0
        dirty = true
    }

    /**
     * returns the mean of the samples added to this instance. Only returns meaningful results when at least window_size samples as specified in the constructor have been added.
     *
     * @return the mean
     */
    fun getMean(): Float {
        return if (hasEnoughData()) {
            if (dirty == true) {
                var mean = 0f
                for (i in values.indices) mean += values[i]
                this.mean = mean / values.size
                dirty = false
            }
            mean
        } else 0.0f
    }

    /** @return the oldest value in the window
     */
    val oldest: Float
        get() = if (last_value == values.size - 1) values[0] else values[last_value + 1]

    /** @return the value last added
     */
    val latest: Float
        get() = values[if (last_value - 1 == -1) values.size - 1 else last_value - 1]

    /** @return The standard deviation
     */
    fun standardDeviation(): Float {
        if (!hasEnoughData()) return 0.0f
        val mean = getMean()
        var sum = 0f
        for (i in values.indices) {
            sum += (values[i] - mean) * (values[i] - mean)
        }
        return Math.sqrt((sum / values.size).toDouble()).toFloat()
    }

    fun getWindowSize(): Int {
        return values.size
    }
}