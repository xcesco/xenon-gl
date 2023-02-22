/**
 *
 */
package com.abubusoft.xenon

import android.app.Activity
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.XenonWallpaper

/**
 * Manager argon per applicazioni tradizionali
 *
 * @author Francesco Benincasa
 */
class Xenon4App : Xenon4BaseImpl<XenonApplication4App>() {
    /**
     * Da eseguire durante la creazione dell'activity.
     *
     * @param activity
     * activity creata
     * @throws Exception
     */
    @Throws(Exception::class)
    fun onActivityCreated(activity: Activity?) {
        Logger.info("onActivityCreated")
    }

    /**
     * Creazione del servizio ed impostazione del renderer
     *
     * @param service
     * service creato
     * @throws Exception
     */
    @Throws(Exception::class)
    fun onServiceCreated(service: XenonWallpaper?) {
        Logger.info("onServiceCreated")
    }

    /**
     * Chiude un'activity
     *
     * @param activity
     * activity da chiudere
     */
    fun onDestroy(activity: Activity) {
        application!!.onDestroy(activity)
    }

    /**
     * Metodo invocato quando viene eseguito il programma, sia esso mediante
     * activity, sia mediante servizio.
     *
     * Al suo interno:
     *
     *  1. impostiamo il context application
     *  1. leggiamo i settings
     *  1. crea l'istanza di applicazione
     *  1. definiamo l'application manager
     *  1. imposta il context (this)
     *  1. configura il log
     *  1. configura le impostazioni di argon
     *  1. configura le impostazioni applicative
     *  1. registra il listener del cambio preferences
     *  1. avvia l'applicazione
     *  1. se è la prima volta, viene eseguito il metodo onAfterStartupFirstTime
     *
     *
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
    fun onConfigReset() {
        application!!.onConfigReset()
    }

    companion object {
        private const val serialVersionUID = -4852533683873441094L
    }
}