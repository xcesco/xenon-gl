package com.abubusoft.xenon.misc;

import android.os.SystemClock;

/**
 * Classe di utilità per il touchTimer
 * @author Francesco Benincasa
 *
 */
public abstract class Clock {
	/**
	 * Get time in millseconds, since device stopped.
	 * 
	 * @return
	 * 		time in millseconds
	 */
	public static long now()
	{
		return SystemClock.elapsedRealtime();
	}
}
