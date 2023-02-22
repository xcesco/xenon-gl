package com.abubusoft.xenon.settings

import android.content.Context
import com.abubusoft.kripton.KriptonBinder
import com.abubusoft.xenon.core.util.IOUtility.readRawTextFile

/**
 * Reader dei settings dell'applicazione.
 *
 * @author Francesco Benincasa
 */
object ArgonSettingsReader {
    /**
     * Legge un file xml dalla cartella raw e lo trasforma in una classe XenonSettings.
     *
     * @param
     * context
     * @param
     * argonSettingsId
     * @return
     * argonSettings
     */
    fun readFromRawXml(context: Context?, argonSettingsId: Int): XenonSettings? {
        try {
            return KriptonBinder.xmlBind().parse(readRawTextFile(context!!, argonSettingsId), XenonSettings::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}