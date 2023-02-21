package com.abubusoft.xenon;

import com.abubusoft.kripton.android.Logger;

import android.app.Activity;
import android.content.Context;

/**
 * @author Francesco Benincasa
 */
public abstract class XenonApplicationImpl<E extends Xenon> implements XenonApplication<E> {

    /**
     * Xenon manager for wallpaper
     */
    public E argon;

    @Override
    public void setArgon(E argonValue) {
        argon = argonValue;
    }

    /* (non-Javadoc)
     * @see com.abubusoft.xenon.XenonApplication4App#context()
     */
    @Override
    public Context context() {
        return argon.context();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.abubusoft.xenon.XenonApplication4App#onAfterStartupFirstTime()
     */
    @Override
    public void onAfterStartupFirstTime() {
        Logger.info("onAfterStartupFirstTime");

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.abubusoft.xenon.XenonApplication4App#onAfterStartupFirstTimeForThisVersion()
     */
    @Override
    public void onAfterStartupFirstTimeForThisVersion() {
        Logger.info("onAfterStartupFirstTimeForThisVersion");
    }

    public void onDestroy(Activity activity) {
        // non faccio niente
    }

}
