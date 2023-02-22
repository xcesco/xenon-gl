package com.abubusoft.xenon.core.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.SparseArray
import com.abubusoft.xenon.core.sensor.OrientationInputConfig.ProviderType
import com.abubusoft.xenon.core.sensor.compass.CompassProvider
import com.abubusoft.xenon.core.sensor.internal.InputSensorProvider
import com.abubusoft.xenon.core.sensor.internal.ShakeInputDetector
import com.abubusoft.xenon.core.sensor.orientation.*

/**
 *
 *
 * Gestore dei vari tipi di sensori che il framework supporta.
 *
 * <h2>Bussola</h2>
 *
 *
 * Consente di capire come è orientato il dispositivo. Necessita di autorizzazioni particolari all'interno del manifest.
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
 * </pre>
 *
 * @author Francesco Benincasa
 *
 * <a href="https://github.com/xcesco/xenon-gl/wiki/Sensors">vedi wiki</a>
 */
object ElioSensorManager : SensorEventListener {
    /**
     * Stati del manager
     *
     * @author xcesco
     */
    enum class StatusType {
        STARTED, STOPPED
    }

    /**
     * Stati dei sensori gestiti
     *
     * @author xcesco
     */
    enum class SensorStatusType {
        REGISTERED, UNREGISTERED
    }

    /**
     * indica se il manager gestisce l'orientamento del device
     */
    private var deviceOrientation: Boolean

    /**
     * indica se il manager gestisce lo shake del device
     */
    private var deviceShake: Boolean
    /**
     * getter dello stato del maanger
     *
     * @return stato del manager
     */
    /**
     * stato dell'input manager
     *
     */
    var status: StatusType
        private set

    /**
     * Se non sono stati già registrati, provvede a registrare i listener.
     */
    private fun registryListener() {
        var sensor: Sensor?
        // definiamo sensori e detector associati
        for (i in 0 until sensorsToProviders.size()) {
            sensor = sensorManager!!.getDefaultSensor(sensorsToProviders.keyAt(i))
            sensorManager!!.registerListener(this, sensor, config!!.delay!!.value)
        }
    }

    /**
     * Se non sono stati già unregistrati, provvede a unregistrare i listener
     */
    private fun unregistryListener() {

        // assert: ora abbiamo creato la lista di sensori a cui ci vogliamo
        // agganciare
        var sensor: Sensor?
        for (i in 0 until sensorsToProviders.size()) {
            sensor = sensorManager!!.getDefaultSensor(sensorsToProviders.keyAt(i))
            sensorManager!!.unregisterListener(this, sensor)
        }
    }

    /**
     * a prescindere dallo stato in cui si trova, lo mettiamo in stato di stop, cancelliamo tutti gli stati.
     */
    fun reset() {
        // intanto blocchiamo eventualmente tutto
        stopMeasure()
        providers.clear()
        deviceOrientation = false
        deviceShake = false
        deviceCompass = false
    }

    /**
     * insieme degli eventi
     */
    private val sensorsToProviders: SparseArray<ArrayList<InputSensorProvider>> = SparseArray()
    private var initDone = false

    /**
     * detector di input relativi ai sensori agganciati
     */
    private val providers: HashSet<InputSensorProvider>

    /**
     * configurazione del manager
     */
    var config: ElioSensorConfig? = null

    /**
     * gestore dei sensori
     */
    var sensorManager: SensorManager? = null
    private var deviceCompass = false

    /**
     * Inizializzazione del sistema. Cancella tutti i listener e provider eventualmente definiti in precedenza.
     *
     * @param context
     * @param configValue
     * configurazione di base del manager
     * @return true se l'inizializzazione è stata effettuata, false se era stata già effettuata in precedenza.
     */
    fun init(context: Context, configValue: ElioSensorConfig?): Boolean {
        if (initDone) {
            stopMeasure()
        }
        config = configValue
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        initDone = true
        deviceOrientation = false
        deviceShake = false
        deviceCompass = false
        return true
    }

    /**
     *
     *
     * Abilita la rilevazione dei sensori relativi all'orientamento del dispositivo.
     * Di seguito riportiamo i movimenti ed i relativi assi di rotazione
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
     * @param config
     * @param listener
     */
    fun enableOrientation(config: OrientationInputConfig, listener: OrientationInputListener) {
        if (deviceOrientation) return
        var provider: OrientationProvider? = null
        provider = when (config.provider) {
            ProviderType.ACCELEROMETER_COMPASS -> AccelerometerCompassProvider
            ProviderType.CALIBRATE_COMPASS -> CalibratedGyroscopeProvider
            ProviderType.GRAVITY_COMPASS -> GravityCompassProvider
            ProviderType.IMPROVED_ORIENTATION_SENSOR2 -> Fusion2OrientationProvider
            ProviderType.ROTATION_VECTOR -> RotationVectorProvider
            ProviderType.IMPROVED_ORIENTATION_SENSOR1 -> Fusion1OrientationProvider
        }
        if (!providers.contains(provider)) {
            providers.add(provider)
            deviceOrientation = true
            provider.addListener(config, listener)
        }
    }

    fun enableCompass(config: OrientationInputConfig?, listener: OrientationInputListener?) {
        val detector = CompassProvider.instance()
        detector!!.setListener(config, listener)
        // aggiungiamo il detector associato
        if (!deviceCompass) providers.add(detector)
        deviceCompass = true
    }

    fun enableShake(config: ShakeInputConfig, listener: ShakeInputListener) {
        val detector = ShakeInputDetector.instance()
        detector.addListener(config, listener)
        // aggiungiamo il detector associato
        if (!deviceShake) providers.add(detector)
        deviceShake = true
    }

    /**
     * Da invocare quando vogliamo bloccare tutti
     */
    fun onPause() {
        if (status == StatusType.STARTED) {
            unregistryListener()
        }
    }

    /**
     * Blocca la misurazione
     */
    fun stopMeasure() {
        status = StatusType.STOPPED
        unregistryListener()
        sensorsToProviders.clear()
        providers.clear()
    }

    /**
     * Inizia la misurazione
     */
    fun startMeasure() {
        if (status == StatusType.STARTED) return
        if (providers.size == 0) {
        } else {
            // puliamo sensori
            sensorsToProviders.clear()
            var sensorTypes: HashSet<Int>
            var currentList: ArrayList<InputSensorProvider>
            // prendiamo tutti i detector di sensori
            for (item in providers) {
                sensorTypes = item.getAttachedTypeofSensors()
                // registriamoli con i vari sensori sensorsToDetectors
                for (sensorType in sensorTypes) {
                    if (sensorsToProviders.indexOfKey(sensorType) < 0) {
                        sensorsToProviders.append(sensorType, ArrayList())
                    }
                    currentList = sensorsToProviders[sensorType]
                    currentList.add(item)
                }
            }
            status = StatusType.STARTED
            registryListener()
        }
    }

    fun onResume() {
        if (status == StatusType.STARTED) {
            registryListener()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        sensorIndex = sensorsToProviders.indexOfKey(sensor.type)
        if (sensorIndex >= 0) {
            val currentDetectors = sensorsToProviders.valueAt(sensorIndex)
            for (i in currentDetectors.indices) {
                currentDetectors.get(i)!!.onAccuracyChanged(sensor, accuracy)
            }
        }
    }

    var sensorIndex = 0

    /**
     * costruttore
     */
    init {
        providers = HashSet()
        status = StatusType.STOPPED
        deviceOrientation = false
        deviceShake = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        sensorIndex = sensorsToProviders.indexOfKey(event.sensor.type)
        if (sensorIndex >= 0) {
            val currentDetectors = sensorsToProviders.valueAt(sensorIndex)
            for (i in currentDetectors.indices) {
                currentDetectors.get(i).onSensorChanged(event)
            }
        }
    }
}