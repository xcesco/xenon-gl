package com.abubusoft.xenon

import android.app.Activity
import android.content.Context
import com.abubusoft.kripton.android.Logger

/**
 * @author Francesco Benincasa
 */
abstract class XenonApplicationImpl<E: Xenon> : XenonApplication {
    /**
     * Xenon manager for wallpaper
     */
    lateinit var argon: E

    override fun setArgon(argonValue: E) {
        argon = argonValue
    }

    /* (non-Javadoc)
     * @see com.abubusoft.xenon.XenonApplication4App#context()
     */
    override fun context(): Context {
        return argon.context()
    }

    /*
     * (non-Javadoc)
     *
     * @see com.abubusoft.xenon.XenonApplication4App#onAfterStartupFirstTime()
     */
    override fun onAfterStartupFirstTime() {
        Logger.info("onAfterStartupFirstTime")
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.abubusoft.xenon.XenonApplication4App#onAfterStartupFirstTimeForThisVersion()
     */
    override fun onAfterStartupFirstTimeForThisVersion() {
        Logger.info("onAfterStartupFirstTimeForThisVersion")
    }

    override fun onDestroy(activity: Activity?) {
        // non faccio niente
    }
}