package com.abubusoft.xenon.android.listener

import android.view.MotionEvent
import android.view.ScaleGestureDetector

/**
 *
 * Gestore delle gesture.
 *
 * @author Francesco Benincasa
 */
open class XenonGestureListenerImpl : XenonGestureListener {
    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    /* (non-Javadoc)
	 * @see android.view.ScaleGestureDetector.OnScaleGestureListener#onScale(android.view.ScaleGestureDetector)
	 */
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        return false
    }

    /* (non-Javadoc)
	 * @see android.view.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin(android.view.ScaleGestureDetector)
	 */
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        // deve tornare true per iniziare a considerare l'eventuale zoom
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        // lasciato vuoto
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.android.listener.XenonGestureListener#onUp(android.view.MotionEvent)
	 */
    override fun onUp(e: MotionEvent?) {}
}