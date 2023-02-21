package com.abubusoft.xenon.android.listener

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector.OnScaleGestureListener

/**
 * Interfaccia per la gestione delle gesture
 *
 * @author Francesco Benincasa
 */
interface XenonGestureListener : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, OnScaleGestureListener {
    /**
     *
     *
     * Eseguito quando viene rilasciato. Non restituisce nulla, dato che è trasparente rispetto a tutti gli altri eventi.
     *
     *
     * @param e
     */
    fun onUp(e: MotionEvent?)
}