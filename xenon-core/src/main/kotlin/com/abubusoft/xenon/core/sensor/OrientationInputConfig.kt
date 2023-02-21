package com.abubusoft.xenon.core.sensor

/**
 *
 *
 * Configurazione dell'orientamento. Gli angoli sono espressi in gradi
 *
 *
 * <pre>
 * ^ Roll (z)
 * |
 * +---------+
 * |         |
 * | Heading |
 * |   (x)   |
 * |    x    | ---> Pitch (y)
 * |         |
 * +---------+
 *
</pre> *
 *
 * @author Francesco Benincasa
 */
class OrientationInputConfig private constructor() {
    /**
     * Tipo di notifiche
     *
     * @author Francesco Benincasa
     */
    enum class EventType {
        /**
         * indica di notificare sempre e comunque tutti gli eventi
         */
        NOTIFY_ALWAYS,

        /**
         * indica di notificare solo le modifiche ai valori
         */
        NOTIFY_CHANGES
    }

    enum class ProviderType {
        ACCELEROMETER_COMPASS, CALIBRATE_COMPASS, GRAVITY_COMPASS, IMPROVED_ORIENTATION_SENSOR1, IMPROVED_ORIENTATION_SENSOR2, ROTATION_VECTOR
    }

    /**
     * limite in abs dell'azimouth
     */
    var azimouthLimit = 0.0
    var currentAzimuth = 0.0
    var currentPitch = 0.0
    var currentRoll = 0.0
    var deltaTime: Long = 0

    /**
     * indica quando notificare l'evento.
     *
     */
    var event: EventType? = null

    /**
     * delta in gradi in abs dentro il quale posso considerare la misurazione come 0
     */
    var noiseNearZero = 0.0

    /**
     * ultimo azimuth rilevato per questo config
     */
    var oldAzimuth = 0.0

    /**
     * ultimo pitch rilevato per questo config
     */
    var oldPitch = 0.0

    /**
     * ultimo roll rilevato per questo config
     */
    var oldRoll = 0.0

    /**
     * limite in abs in gradi del pitch
     */
    var pitchLimit = 0.0
    var provider: ProviderType? = null

    /**
     * limite in abs in gradi del roll
     */
    var rollLimit = 0.0

    /**
     * costruttore
     */
    init {
        restoreDefaultValue()
    }

    /**
     * <pre>
     * ^ Roll (z)
     * |
     * +---------+
     * |         |
     * | Heading |
     * |   (x)   |
     * |    x    | ---> Pitch (y)
     * |         |
     * +---------+
     *
    </pre> *
     *
     * @param value
     * @return    this
     */
    fun azimouthLimit(value: Double): OrientationInputConfig {
        azimouthLimit = value
        return this
    }

    /**
     * indica quando notificare l'evento.
     *
     * @param value
     * quando notificare l'evento
     * @return
     * this
     */
    fun event(value: EventType?): OrientationInputConfig {
        event = value
        return this
    }

    /**
     * <pre>
     * ^ Roll (z)
     * |
     * +---------+
     * |         |
     * | Heading |
     * |   (x)   |
     * |    x    | ---> Pitch (y)
     * |         |
     * +---------+
     *
    </pre> *
     *
     * @param value
     * @return this
     */
    fun pitchLimit(value: Double): OrientationInputConfig {
        pitchLimit = value
        return this
    }

    /**
     * @param value
     * @return this
     */
    fun provider(value: ProviderType?): OrientationInputConfig {
        provider = value
        return this
    }

    /**
     * imposta i valori di default
     */
    private fun restoreDefaultValue() {
        noiseNearZero = DEFAULT_NOISE_NEAR_ZERO

        // vuol dire che non lo rileviamo
        azimouthLimit = NO_LIMIT

        // limite 20°
        rollLimit = DEFAULT_LIMIT

        // limite 20°
        pitchLimit = DEFAULT_LIMIT

        // notifica solo i cambiamenti
        event = EventType.NOTIFY_CHANGES

        // ogni 250 ms
        deltaTime = 150

        // tipo di provider
        provider = ProviderType.IMPROVED_ORIENTATION_SENSOR1
    }

    /**
     * <pre>
     * ^ Roll (z)
     * |
     * +---------+
     * |         |
     * | Heading |
     * |   (x)   |
     * |    x    | ---> Pitch (y)
     * |         |
     * +---------+
     *
    </pre> *
     *
     * @param value
     * @return this
     */
    fun rollLimit(value: Double): OrientationInputConfig {
        rollLimit = value
        return this
    }

    /**
     * Differenza sotto la quale due misurazioni sono considerate uguali. Di default
     * il suo valore è [.DEFAULT_NOISE_NEAR_ZERO]
     *
     * @param value
     * @return
     * builder
     */
    fun noiseNearZero(value: Double): OrientationInputConfig {
        noiseNearZero = value
        return this
    }

    /**
     * tempo minimo tra una misurazione ed un'altra
     *
     * @param value
     * @return
     * builder
     */
    fun deltaTime(value: Long): OrientationInputConfig {
        deltaTime = value
        return this
    }

    companion object {
        /**
         * limite di default (minimo)
         */
        const val DEFAULT_LIMIT = 20.0

        /**
         * valore di default in gradi per il quale consideriamo la misurazione 0
         */
        const val DEFAULT_NOISE_NEAR_ZERO = 1.0
        const val NO_LIMIT = 360.0

        /**
         *
         *
         * Configurazione di default.
         *
         *
         *
         * <dl>
         * <dt>delta</dt>
         * <dd>delta minimo in gradi misurabile = 1.0</dd>
         * <dt>refreshCounter</dt>
         * <dd>ogni quante rilevazioni prendere quella buona= 1</dd>
         * <dt>pitchLimit</dt>
         * <dd>limite in abs in gradi del pitch = 20.0</dd>
         * <dt>rollLimit</dt>
         * <dd>limite in abs in gradi del roll = 20.0</dd>
         * <dt>azimouthLimit</dt>
         * <dd>limite in abs dell'azimouth = 360.0</dd>
         * <dt>noiseNearZero</dt>
         * <dd>delta in gradi in abs dentro il quale posso considerare la misurazione come 0= 3.0</dd>
         * <dt>intValues</dt>
         * <dd>se true indica che i valori devono essere a scalini = true</dd>
         * <dt>event</dt>
         * <dd>tipo di evento da gestire = [EventType.NOTIFY_CHANGES] (segnala solo i cambiamenti).</dd>
        </dl> *
         *
         *
         */
        fun build(): OrientationInputConfig {
            return OrientationInputConfig()
        }
    }
}