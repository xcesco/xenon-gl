package com.abubusoft.xenon

import android.content.Context
import com.abubusoft.xenon.settings.XenonSettings

/**
 *
 * Serve a marcare i modi con i quali l'applicazione può essere lanciata.
 *
 * @author Francesco Benincasa
 */
interface Xenon {
    /**
     *
     * Da avviare in fase di avvio dell'applicazione
     *
     * @param contextValue
     * @param settingsValue
     */
    fun init(contextValue: Context, settingsValue: XenonSettings)

    /**
     *
     * Recupera l'application context
     *
     * @return
     */
    fun context(): Context
}