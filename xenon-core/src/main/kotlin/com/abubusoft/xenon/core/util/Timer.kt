package com.abubusoft.xenon.core.util

import android.os.SystemClock

/**
 * Classe di utilità per monitorare il tempo
 * @author Francesco Benincasa
 */
class Timer {
    /**
     * tempo iniziale
     */
    private var time: Long = 0

    /**
     * inizio timer
     */
    fun start() {
        time = now()
    }

    /**
     * fine
     * @return
     */
    fun end(): Long {
        return now() - time
    }

    companion object {
        /**
         * tempo attuale in millisecondi.
         * @return
         */
        fun now(): Long {
            return SystemClock.elapsedRealtime()
        }
    }
}