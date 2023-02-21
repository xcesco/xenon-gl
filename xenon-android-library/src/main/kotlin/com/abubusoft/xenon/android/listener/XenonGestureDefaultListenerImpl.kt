package com.abubusoft.xenon.android.listener

import android.view.MotionEvent
import com.abubusoft.kripton.android.Logger

/**
 * Gestore delle gesture
 *
 * @author xcesco
 */
class XenonGestureDefaultListenerImpl : XenonGestureListenerImpl() {
    override fun onDoubleTap(e: MotionEvent): Boolean {
        Logger.debug("onDoubleTap $e")
        return super.onDoubleTap(e)
    }

    override fun onDown(e: MotionEvent): Boolean {
        Logger.debug("onDown $e")
        return super.onDown(e)
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        Logger.debug("onFling $e2")
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onLongPress(e: MotionEvent) {
        Logger.debug("onLongPress$e")
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        Logger.debug("onScroll $e2")
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onShowPress(e: MotionEvent) {
        Logger.debug("onShowPress$e")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Logger.debug("onSingleTapUp$e")
        return super.onSingleTapUp(e)
    }
}