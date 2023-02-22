/**
 *
 */
package com.abubusoft.xenon.settings

import com.abubusoft.xenon.core.Uncryptable

/**
 *
 * Factory dell'applicazione. Fornisce le informazioni di base quali ad
 * esempio la classe dell'applicazione e le altre configurazioni.
 *
 *
 * Deve essere realizzata dall'activity o dal service.
 *
 * @author Francesco Benincasa
 */
@Uncryptable
interface XenonSettingsFactory {
    /**
     *
     * Recupera le informazioni relative ai settings. Da questi verrà eventualmente
     * recuperata anche la classe dell'applicazione.
     *
     * @return
     * XenonSettings
     */
    fun buildSettings(): XenonSettings?
}