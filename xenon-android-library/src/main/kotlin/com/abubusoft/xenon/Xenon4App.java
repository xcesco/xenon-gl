/**
 * 
 */
package com.abubusoft.xenon;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.android.XenonWallpaper;


import android.app.Activity;

/**
 * Manager argon per applicazioni tradizionali
 * 
 * @author Francesco Benincasa
 * 
 */
public class Xenon4App extends Xenon4BaseImpl<XenonApplication4App> {

	private static final long serialVersionUID = -4852533683873441094L;


	/**
	 * Da eseguire durante la creazione dell'activity.
	 * 
	 * @param activity
	 * 		activity creata
	 * @throws Exception
	 */
	public void onActivityCreated(Activity activity) throws Exception {
		Logger.info("onActivityCreated");
	}

	/**
	 * Creazione del servizio ed impostazione del renderer
	 * 
	 * @param service
	 * 		service creato
	 * @throws Exception
	 */
	public void onServiceCreated(XenonWallpaper service) throws Exception {
		Logger.info("onServiceCreated");
	}

	/**
	 * Chiude un'activity
	 * 
	 * @param activity
	 * 		activity da chiudere
	 */
	public void onDestroy(Activity activity) {
		application.onDestroy(activity);
	}

	/**
	 * Metodo invocato quando viene eseguito il programma, sia esso mediante
	 * activity, sia mediante servizio.
	 * 
	 * Al suo interno:
	 * <ol>
	 * <li>impostiamo il context application</li>
	 * <li>leggiamo i settings</li>
	 * <li>crea l'istanza di applicazione</li>
	 * <li>definiamo l'application manager</li>
	 * <li>imposta il context (this)</li>
	 * <li>configura il log</li>
	 * <li>configura le impostazioni di argon</li>
	 * <li>configura le impostazioni applicative</li>
	 * <li>registra il listener del cambio preferences</li>
	 * <li>avvia l'applicazione</li>
	 * <li>se è la prima volta, viene eseguito il metodo onAfterStartupFirstTime
	 * </li>
	 * </ol>
	 * 
	 * Xenon rileva se è la prima volta che viene eseguita l'applicazione
	 * semplicemente vedendo se nelle preference esiste un booleano.
	 * 
	 * @param applicationFactory
	 * @param activity
	 * @param argonService
	 * @param argonView
	 * @throws Exception
	 */
	/*public void init(ArgonActivity4App activity, XenonWallpaper service) throws Exception {
		// impostiamo l'application
		if (firstTime) {

			// 2 - l'application manager è stato già configurato
			ApplicationManager am = ApplicationManager.getInstance();

			// 3 - ricaviamo la configurazione
			settings = (XenonSettings) am.attributes.get(ApplicationManager.SETTINGS_KEY);

			// 4 - creiamo application (recuperato da settings)
			application = (XenonApplication4App) Class.forName(settings.application.clazz).newInstance();

			// 5 - impostiamo il context
			application.setArgon(this);

			preferences = am.getApplicationPreferences();

			// cancelliamo la parte relativa ai file xml che rendono persistente
			// la configurazione
			if (settings.application.resetConfig) {
				// pulisce la configurazione di sistema
				ApplicationManager.getInstance().resetSystemPreferences();
				ApplicationManager.getInstance().resetApplicationPreferences();
			}
			// facciamo referenziare il configstorage dell'app manager.
			// il config storage viene messo qua sotto perchè il
			// resetApplicationPreferences ne
			// verifica la presenza ed in caso positivo provvede anche a
			// cancellare il config. (Ci sono
			// gli oggetti di tipo pack che richiedono ancora inizializzazione).
			am.configStorage = application;

			// avviamo l'applicazione
			ApplicationInfo info = am.info;
			if (info.isFirstTime()) {
				Logger.info("First time for application %s", info.name);
				application.onAfterStartupFirstTime();
			}

			if (info.isFirstTimeForThiVersion()) {
				Logger.info("First time for application %s version %s", info.name, info.version);
				application.onAfterStartupFirstTimeForThisVersion();
			}

			// rileva la risoluzione dello schermo
			ScreenInfo.build(context, screenInfo);
			Logger.info("Screen resolution %s startX %s - Density %s", screenInfo.width, screenInfo.height, screenInfo.densityClass);

			application.init();

			// la configurazione è stata resettata (devo metterla qua per
			// evitare che la config non sia correttamente inizializzata
			if (ApplicationManager.getInstance().isConfigReset()) {
				application.onConfigReset();
				ApplicationManager.getInstance().setConfigReset(false);
			}

		} else {
			Logger.info("Activity or service partial restart");
		}

		// stiamo lanciando il programma come activity
		if (activity != null) {
			// inizializzazione iniziale dell'activity
			onActivityCreated(activity);

		} else if (service != null) {
			// tutto il resto per il servizio viene eseguito dal servizio
			// stesso.
			onServiceCreated(service);

		}

		firstTime = false;
	}*/



	
	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.Xenon#onConfigReset()
	 */
	public void onConfigReset()
	{
		application.onConfigReset();
	}

}
