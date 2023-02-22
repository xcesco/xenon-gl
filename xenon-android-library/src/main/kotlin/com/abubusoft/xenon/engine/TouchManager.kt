/**
 *
 */
package com.abubusoft.xenon.engine

import com.abubusoft.xenon.core.collections.SmartQueue
import java.util.concurrent.locks.ReentrantLock

/**
 * Questo gestore di input funziona così: nel thread per la ricezione dell'input richiede di inserire il metodo per la creazione dei messaggi da inserire nella coda di input. Nella creazione della scena invece viene registrato un listener
 * per la ricezione degli eventi.
 *
 * Un esempio di classe listener:
 *
 * <pre>
 * public class TiledTest02GestureListener extends ArgonGestureListenerImpl {
 *
 * ...
 *
 * public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
 * TouchManager.instance().sendMessage(TouchType.SCROLL, distanceX, distanceY);
 *
 * return true;
 * }
 *
 *
 * public boolean onDoubleTap(MotionEvent e) {
 * TouchManager.instance().sendMessage(TouchType.DOUBLE_TAP, e.getX(), e.getY());
 *
 * return true;
 * }
 *
 *
 * public void onUp(MotionEvent e) {
 * TouchManager.instance().sendMessage(TouchType.UP, e.getX(), e.getY());
 * }
 *
 * ...
 * }
</pre> *
 *
 *
 * Un esempio di classe application:
 *
 * <pre>
 * public class TiledApplication {
 *
 * ...
 *
 * public void onSceneCreate(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
 * ...
 * TouchManager.instance().setListener(this);
 * ...
 * }
 *
 *
 * public void onTouch(TouchType type, float x, float y) {
 * Logger.info("onTouch %s - THREAD %s", type, Thread.currentThread().getName());
 * switch (type) {
 * case SCROLL:
 * mapController.scrollFromScreen(x, y);
 * break;
 * case DOUBLE_TAP:
 * Point2 point = mapController.touch(x, y);
 * objPlayerController.clearActions();
 * objPlayerController.navigateTo(point.x, point.y);
 *
 * break;
 * }
 *
 * }
 *
 * ...
 *
 * }
</pre> *
 *
 * @author Francesco Benincasa
 */
class TouchManager {
    val lock = ReentrantLock()
    private val queue = SmartQueue<TouchMessage?>()
    private val pool = TouchMessagePool()
    fun sendMessage(type: TouchType?, x: Float, y: Float) {
        val lock = lock
        lock.lock()
        try {
            val msg = pool.createPooledObject()
            msg!!.type = type
            msg.x = x
            msg.y = y

            //Logger.info("creo %s - %s", type, Thread.currentThread().getName());
            queue.add(msg)
        } finally {
            lock.unlock()
        }
    }

    fun setListener(value: TouchEventListener?) {
        listener = value
    }

    private var listener: TouchEventListener? = null
    fun processMessages() {
        val lock = lock
        lock.lock()
        try {
            var msg: TouchMessage?
            while (!queue.isEmpty) {
                msg = queue.pop()
                listener!!.onTouch(msg!!.type, msg.x, msg.y)
                pool.freeObject(msg)
            }
        } finally {
            lock.unlock()
        }
    }

    companion object {
        private val instance = TouchManager()
        fun instance(): TouchManager {
            return instance
        }
    }
}