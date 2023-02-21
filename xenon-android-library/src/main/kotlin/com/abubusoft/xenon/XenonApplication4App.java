/**
 * 
 */
package com.abubusoft.xenon;

import android.app.Activity;

/**
 * 
 * @author Francesco Benincasa
 * @param <E>
 * 
 */

public interface XenonApplication4App extends XenonApplication<Xenon4App> {

	/**
	 * evento scatenato quando viene messo in pausa il servizio o l'activity
	 */
	public void onPause(Activity currentActivity);

	/**
	 * evento scatenato quando viene riavviata l'applicazione o l'activity
	 */
	public void onResume(Activity currentActivity);

}
