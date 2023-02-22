/**
 * 
 */
package com.abubusoft.xenon.settings;

import com.abubusoft.xenon.core.Uncryptable;


/**
 * <p>Factory dell'applicazione. Fornisce le informazioni di base quali ad
 * esempio la classe dell'applicazione e le altre configurazioni.</p>
 * 
 * <p>Deve essere realizzata dall'activity o dal service.</p>
 * 
 * @author Francesco Benincasa
 *
 */
@Uncryptable
public interface XenonSettingsFactory {
	
	/**
	 * <p>Recupera le informazioni relative ai settings. Da questi verrà eventualmente
	 * recuperata anche la classe dell'applicazione.</p>
	 * 
	 * @return
	 * 		XenonSettings
	 */
	XenonSettings buildSettings();

}
