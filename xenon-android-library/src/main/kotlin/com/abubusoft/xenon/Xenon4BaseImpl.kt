package com.abubusoft.xenon

import android.content.Context
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.context.XenonBeanContext.createInstance
import com.abubusoft.xenon.context.XenonBeanContext.setBean
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.settings.XenonSettings
import java.io.Serializable

/**
 * Manager di base per le varie configurazioni.
 *
 * @author Francesco Benincasa
 */
abstract class Xenon4BaseImpl : Xenon, Serializable {

    override fun context(): Context {
        return context
    }

    /**
     * contesto android
     */
    protected lateinit var context: Context

    /**
     * applicazione da eseguire
     */
    lateinit var application: XenonApplication<*>

    /**
     * indica se l'applicazione è stata caricata almeno una volta. (Serve sull'on startup degli action)
     */
    protected var firstTime = true

    /**
     * configurazione della libreria
     */
    lateinit var settings: XenonSettings

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.Xenon#init(android.content.Context, com.abubusoft.xenon.settings.XenonSettings, android.content.SharedPreferences)
	 */
    override fun init(contextValue: Context, settingsValue: XenonSettings) {
        // 1 - impostiamo il context e le preference
        // (http://android-developers.blogspot.de/2009/01/avoiding-memory-leaks.html)
        context = contextValue
        settings = settingsValue

        // 2 - instanziamo config
        try {
            // impostiamo la configurazione una config di default
//			if (settings.application.configClazz == null) {
//				Logger.warn("No configClass was specifed, we use the default class %s", ArgonConfig.class);
//				//settings.application.configClazz = ArgonConfig.class.getName();
//			}
            val configClazz = settings.application.configClazz

            // se ci sono dipendenze con il pack manager lo istanziamo
//			if (ConfigBase.hashPackManagerDependencies(configClazz)) {
//				PackManager.instance().init(context);
//			}
//			// carichiamo la configurazione di default
//			ConfigBase config = (ConfigBase) Class.forName(configClazz).newInstance();
//			config.readPreferences(contextValue);

//			XenonBeanContext.setBean(XenonBeanType.CONFIG, config);
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw RuntimeException(e)
        }

        // 2 - creiamo application (recuperato da settings) e valorizza eventuali Inject
        application = try {
            createInstance(Class.forName(settings.application.clazz.trim { it <= ' ' })) as E
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }

        // 3 - imposta le injection
        setBean(XenonBeanType.APPLICATION, application)

        // 4 - impostiamo il context ed avvia l'argon application
        application.setArgon(this)
        application.onStartup()
    }

    companion object {
        private const val serialVersionUID = -299952463307885714L
    }
}