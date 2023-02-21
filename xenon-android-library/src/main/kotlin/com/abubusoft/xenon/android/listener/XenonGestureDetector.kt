/**
 *
 */
package com.abubusoft.xenon.android.listener

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.Xenon4OpenGL

/**
 * @author Francesco Benincasa
 */
class XenonGestureDetector(xenonGL: Xenon4OpenGL, gestureListener: XenonGestureListener) {
    /**
     * gestore delle gesture
     */
    var gestureDetector: GestureDetector

    /**
     * gestore dello scale
     */
    var scaleGestureDetector: ScaleGestureDetector
    var listener: XenonGestureListener
    var xenon4OpenGL: Xenon4OpenGL

    init {
        xenon4OpenGL = xenonGL
        gestureDetector = GestureDetector(xenonGL.context(), gestureListener)
        scaleGestureDetector = ScaleGestureDetector(xenonGL.context(), gestureListener)
        listener = gestureListener
    }

    /**
     *
     *
     * Evento associato al touch
     *
     *
     * @param event evento in input
     * @return true se l'evento è stato consumato.
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        if (!xenon4OpenGL.isSceneReady()) {
            Logger.debug("Scene is not ready, skip touch event")
            return false
        }
        val action: Int = event.getActionMasked()
        // evento up
        if (action == MotionEvent.ACTION_UP) {
            listener.onUp(event)
        }

        // come da
        // http://stackoverflow.com/questions/15309743/use-scalegesturedetector-with-gesturedetector
        var result: Boolean = scaleGestureDetector.onTouchEvent(event)
        // result is always true here, so I need another way to check for a
        // detected scaling gesture
        result = scaleGestureDetector.isInProgress()
        val isScaling = result
        if (!isScaling) {
            // if no scaling is performed check for other gestures (fling, long
            // tab, etc.)
            result = gestureDetector.onTouchEvent(event)
        }
        return result
    }
}