/**
 *
 */
package com.abubusoft.xenon

import android.app.Activity

/**
 *
 * @author Francesco Benincasa
 * @param <E>
</E> */
interface XenonApplication4App : XenonApplication<Xenon4App> {
    /**
     * evento scatenato quando viene messo in pausa il servizio o l'activity
     */
    fun onPause(currentActivity: Activity?)

    /**
     * evento scatenato quando viene riavviata l'applicazione o l'activity
     */
    fun onResume(currentActivity: Activity?)
}