package com.abubusoft.xenon;

import com.abubusoft.xenon.settings.XenonSettings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <p>Serve a marcare i modi con i quali l'applicazione può essere lanciata.</p>
 * 
 * @author Francesco Benincasa
 * 
 */
public interface Xenon {

	/**
	 * <p>Da avviare in fase di avvio dell'applicazione</p>
	 * 
	 * @param contextValue
	 * @param settingsValue
	 */
	void init(Context contextValue, XenonSettings settingsValue);

	/**
	 * <p>Recupera l'application context</p>
	 * 
	 * @return
	 */
	Context context();
}
