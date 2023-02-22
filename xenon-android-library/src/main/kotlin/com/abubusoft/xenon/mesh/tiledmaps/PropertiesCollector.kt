package com.abubusoft.xenon.mesh.tiledmaps

/**
 *
 *
 * Rappresenta un'entità che ha delle proprietà che normalmente sono stringhe,
 * ma possono essere anche interpretate in altro modo: long, float, etc.
 *
 *
 * @author Francesco Benincasa
 */
open class PropertiesCollector {
    /**
     * proprietà dell'oggetto
     */
    var properties = HashMap<String, String>()

    /**
     *
     *
     * Aggiunge una proprioetà.
     *
     *
     * @param key
     * @param value
     */
    fun addProperty(key: String, value: String) {
        properties[key] = value
    }

    /**
     *
     *
     * Recupera una proprietà come long. Se non esiste, viene usato il valore di
     * default.
     *
     *
     * @param key
     * @param defaultValue
     * @return
     */
    fun getPropertyAsLong(key: String, defaultValue: Long): Long {
        val value = properties[key]
        return value?.toLong() ?: defaultValue
    }

    /**
     *
     *
     * Recupera una proprietà come long. Se non esiste, viene usato il valore di
     * default.
     *
     *
     * @param key
     * @param defaultValue
     * @return
     */
    fun getPropertyAsFloat(key: String, defaultValue: Float): Float {
        val value = properties[key]
        return value?.toFloat() ?: defaultValue
    }

    /**
     *
     *
     * Recupera una proprietà come boolean. Se non esiste, viene usato il valore
     * di default.
     *
     *
     * @param key
     * @param defaultValue
     * @return
     */
    fun getPropertyAsBool(key: String, defaultValue: Boolean): Boolean {
        val value = properties[key]
        return if (value != null) {
            java.lang.Boolean.parseBoolean(value)
        } else defaultValue
    }

    /**
     *
     *
     * Recupera una proprietà mediante il suo nome.
     *
     *
     * @param key
     * @param defaultValue
     * @return
     */
    fun getProperty(key: String, defaultValue: String): String {
        val value = properties[key]
        return value ?: defaultValue
    }
}