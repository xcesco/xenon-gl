/**
 * 
 */
package com.abubusoft.xenon;

import com.abubusoft.xenon.android.XenonActivitySplash;

/**
 * <p>
 * Contiene il task da eseguire durante la visualizzazione dello splash screen.
 * </p>
 * 
 * @author Cesco
 * 
 */
public interface XenonStartupTask {

	/**
	 * <p>
	 * effettua il task.
	 * </p>
	 * 
	 * @param splashActivity
	 */
	void doTask(XenonActivitySplash splashActivity);

}
