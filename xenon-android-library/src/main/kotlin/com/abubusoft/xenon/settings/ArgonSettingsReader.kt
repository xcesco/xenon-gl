package com.abubusoft.xenon.settings;

import com.abubusoft.xenon.core.util.IOUtility;

import com.abubusoft.kripton.KriptonBinder;

import android.content.Context;

/**
 * Reader dei settings dell'applicazione.
 * 
 * @author Francesco Benincasa
 *
 */
public abstract class ArgonSettingsReader {

	/**
	 * Legge un file xml dalla cartella raw e lo trasforma in una classe XenonSettings.
	 * 
	 * @param
	 * 		context
	 * @param
	 * 		argonSettingsId
	 * @return
	 * 		argonSettings
	 */
	public static XenonSettings readFromRawXml(Context context, int argonSettingsId) {
		try {
			XenonSettings settings=KriptonBinder.xmlBind().parse(IOUtility.readRawTextFile(context, argonSettingsId), XenonSettings.class);
			
			return settings;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
