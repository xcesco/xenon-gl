package com.abubusoft.xenon.core.sensor

/**
 *
 * Configurazione relativa allo shake
 *
 * @author Francesco Benincasa
 */
class ShakeInputConfig {
    /**
     * Minimum acceleration needed to count as a shake movement
     */
    var minShakeAcceleration = 5

    /**
     * Minimum number of movements to register a shake
     */
    var minMovements = 2

    /**
     * Maximum time (in milliseconds) for the whole shake to occur
     */
    var maxShakeDuration: Long = 500
    var startTime: Long = 0
    var moveCount = 0

    /**
     * @param value
     * @return
     * this
     */
    fun minShakeAcceleration(value: Int): ShakeInputConfig {
        minShakeAcceleration = value
        return this
    }

    /**
     * @param value
     * @return
     * this
     */
    fun minMovements(value: Int): ShakeInputConfig {
        minMovements = value
        return this
    }

    /**
     * @param value
     * @return
     * this
     */
    fun maxDuration(value: Long): ShakeInputConfig {
        maxShakeDuration = value
        return this
    }

    companion object {
        fun build(): ShakeInputConfig {
            return ShakeInputConfig()
        }
    }
}