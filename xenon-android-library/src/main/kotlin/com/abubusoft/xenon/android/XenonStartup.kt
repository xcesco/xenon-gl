package com.abubusoft.xenon.android

import android.app.Application
import com.abubusoft.kripton.android.KriptonLibrary
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.XenonLibrary

/**
 *
 *
 * Rappresenta l'application di base di tutte le applicazioni che si basano su argon.
 *
 *
 * @author Francesco Benincasa
 */
class XenonStartup : Application() {
    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
    override fun onCreate() {
        super.onCreate()
        KriptonLibrary.init(this)
        XenonLibrary.init(this)
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onLowMemory()
	 */
    override fun onLowMemory() {
        super.onLowMemory()
        Logger.info("XenonStartup - onLowMemory")
    }
}