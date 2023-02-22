package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.HashMap;

/**
 * <p>
 * Rappresenta un'entità che ha delle proprietà che normalmente sono stringhe,
 * ma possono essere anche interpretate in altro modo: long, float, etc.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class PropertiesCollector {

	/**
	 * proprietà dell'oggetto
	 */
	public HashMap<String, String> properties = new HashMap<>();

	/**
	 * <p>
	 * Aggiunge una proprioetà.
	 * </p>
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}

	/**
	 * <p>
	 * Recupera una proprietà come long. Se non esiste, viene usato il valore di
	 * default.
	 * </p>
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getPropertyAsLong(String key, long defaultValue) {
		String value = properties.get(key);

		if (value != null) {
			return Long.parseLong(value);
		} else
			return defaultValue;
	}

	/**
	 * <p>
	 * Recupera una proprietà come long. Se non esiste, viene usato il valore di
	 * default.
	 * </p>
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public float getPropertyAsFloat(String key, float defaultValue) {
		String value = properties.get(key);

		if (value != null) {
			return Float.parseFloat(value);
		} else
			return defaultValue;
	}

	/**
	 * <p>
	 * Recupera una proprietà come boolean. Se non esiste, viene usato il valore
	 * di default.
	 * </p>
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getPropertyAsBool(String key, boolean defaultValue) {
		String value = properties.get(key);

		if (value != null) {
			return Boolean.parseBoolean(value);
		} else
			return defaultValue;
	}

	/**
	 * <p>
	 * Recupera una proprietà mediante il suo nome.
	 * </p>
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		String value = properties.get(key);

		if (value != null) {
			return value;
		} else
			return defaultValue;
	}
}
