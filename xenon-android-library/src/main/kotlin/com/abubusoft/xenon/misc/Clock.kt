package com.abubusoft.xenon.misc

import android.os.SystemClock

/**
 * Classe di utilità per il touchTimer
 * @author Francesco Benincasa
 */
object Clock {
    /**
     * Get time in millseconds, since device stopped.
     *
     * @return
     * time in millseconds
     */
    fun now(): Long {
        return SystemClock.elapsedRealtime()
    }
}