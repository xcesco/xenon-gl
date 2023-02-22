package com.abubusoft.xenon

import android.app.Activity
import android.content.Context

interface XenonApplication<E: Xenon> {
    /**
     * Context dell'applicazione
     *
     * @return
     * context dell'applicazione
     */
    fun context(): Context

    /**
     * Viene eseguito solo la prima volta che lancio l'applicazione
     */
    fun onAfterStartupFirstTime()

    /**
     * Viene eseguita la prima volta che eseguo questa vestione
     * dell'applicazione
     */
    fun onAfterStartupFirstTimeForThisVersion()

    /**
     * Evento che viene scatenato nella fase di avvio, nel caso in cui la
     * configurazione venga creata o resettata.
     */
    fun onConfigReset()

    /**
     * Quando l'applicazione chiude
     */
    fun onDestroy(activity: Activity?)

    /**
     * startup dell'applicazione. In questo metodo abbiamo già definito le varie
     * configurazioni ma non abbiamo ancora creato la view e quindi non abbiamo
     * ancora il contesto opengl definito
     */
    fun onStartup()

    /**
     * Imposta argon
     *
     * @param argonValue
     */
    fun setArgon(argonValue: Xenon)
}