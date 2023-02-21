package com.abubusoft.xenon.context;

import com.abubusoft.xenon.settings.XenonSettings;

/**
 * <p>
 * Sono i valori che possono essere trasmessi mediante reflection
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public enum XenonBeanType {

	/**
	 * application
	 */
	APPLICATION,

	/**
	 * 
	 */
	XENON,

	/**
	 * <p>
	 * Impostazioni caricate da file xml. il bean è di tipo
	 * {@link XenonSettings}
	 * </p>
	 * 
	 */
	XENON_SETTINGS,

	/**
	 * android context (application context)
	 */
	CONTEXT,

	/**
	 * configurazione dell'applicazione. Deve derivare da ConfigBase
	 */
	CONFIG;

	/**
	 * istanza di classe da associare
	 */
	public Object value;
}
