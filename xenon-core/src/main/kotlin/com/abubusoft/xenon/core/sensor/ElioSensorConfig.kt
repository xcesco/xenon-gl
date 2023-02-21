package com.abubusoft.xenon.core.sensor

class ElioSensorConfig {
    /**
     * delay con il quale rilevare il cambiamento dei sensori.
     */
    var delay: ElioSensorDelayType? = null

    /**
     * delay con il quale rilevare il cambiamento dei sensori.
     *
     * @param value
     * valore di delay
     * @return
     * this
     */
    fun delay(value: ElioSensorDelayType?): ElioSensorConfig {
        delay = value
        return this
    }

    companion object {
        /**
         * Configurazione di default dell'input:
         *
         *
         *  * delay = DELAY_GAME
         *
         *
         * @return
         * istanza di default della configurazione
         */
        fun build(): ElioSensorConfig {
            return ElioSensorConfig().delay(ElioSensorDelayType.DELAY_GAME)
        }
    }
}