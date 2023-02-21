/**
 * 
 */
package com.abubusoft.xenon;

import com.abubusoft.kripton.android.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Francesco Benincasa
 * 
 */
public class ScreenStatus extends BroadcastReceiver {

	public static boolean screenOn = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			// do whatever you need to do here
			screenOn = false;
			
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			// and do whatever you need to do here
			screenOn = true;
		}
		
		Logger.debug("SCREEN "+screenOn);

	}

}
