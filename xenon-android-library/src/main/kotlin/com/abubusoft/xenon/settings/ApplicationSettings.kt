package com.abubusoft.xenon.settings

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.android.listener.XenonGestureDefaultListenerImpl

@BindType
class ApplicationSettings {
    /**
     * Classe che implementa la prima activity da invocare dopo lo splash screen
     */
    @Bind("applicationActivityClazz")
    var activityClazz: String? = null

    /**
     * Classe che implementa l'applicazione
     */
    @Bind("applicationClazz")
    var clazz: String? = null

    /**
     * Classe che implementa la configurazione dell'applicazione
     */
    @Bind("applicationConfigClazz")
    var configClazz: String? = null

    /**
     * Se impostato a true, fa resettare tutti i parametri di applicazione e di sistema
     */
    @Bind("applicationResetConfig")
    var resetConfig = false

    /**
     * Classe da istanziare come listener per le gesture
     */
    @Bind("applicationGestureListenerClazz")
    var gestureListenerClazz: String?

    /**
     * Classe da istanziare come gestore delle policy di upgrade di versione
     */
    @Bind("applicationUpgradePolicyClazz")
    var upgradePolicyClazz: String? = null

    /**
     * indica il tempo di visualizzazione dello splash screen
     */
    @Bind("applicationSplashScreenTimeout")
    var splashScreenTimeout = 3000

    /**
     * task da eseguire durante lo splash screen. Se non definito, non fa niente.
     */
    @Bind("applicationStartupTaskClazz")
    var startupTaskClazz: String? = null

    /**
     *
     * Indica il modo di funzionare dell'applicazione.
     */
    @Bind("applicationMode")
    var mode: ModeType? = null

    /**
     * Costruttore
     */
    init {
        gestureListenerClazz = XenonGestureDefaultListenerImpl::class.java.name // "com.abubusoft.xenon.android.listener.XenonGestureDefaultListenerImpl";
        //upgradePolicyClazz=ApplicationUpgradePolicyImpl.class.getName(); //"com.abubusoft.xenon.core.application.ApplicationUpgradePolicyImpl";
    }
}