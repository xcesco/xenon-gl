/**
 *
 */
package com.abubusoft.xenon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abubusoft.kripton.android.Logger

/**
 * @author Francesco Benincasa
 */
class ScreenStatus : BroadcastReceiver() {
    /*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            // do whatever you need to do here
            screenOn = false
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            // and do whatever you need to do here
            screenOn = true
        }
        Logger.debug("SCREEN " + screenOn)
    }

    companion object {
        var screenOn = true
    }
}