/**
 *
 */
package com.abubusoft.xenon

import com.abubusoft.xenon.android.XenonActivitySplash

/**
 *
 *
 * Contiene il task da eseguire durante la visualizzazione dello splash screen.
 *
 *
 * @author Cesco
 */
interface XenonStartupTask {
    /**
     *
     *
     * effettua il task.
     *
     *
     * @param splashActivity
     */
    fun doTask(splashActivity: XenonActivitySplash?)
}