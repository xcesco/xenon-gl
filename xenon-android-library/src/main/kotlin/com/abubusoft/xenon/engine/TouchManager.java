/**
 * 
 */
package com.abubusoft.xenon.engine;

import java.util.concurrent.locks.ReentrantLock;

import com.abubusoft.xenon.core.collections.SmartQueue;
import com.abubusoft.kripton.android.Logger;

/**
 * Questo gestore di input funziona così: nel thread per la ricezione dell'input richiede di inserire il metodo per la creazione dei messaggi da inserire nella coda di input. Nella creazione della scena invece viene registrato un listener
 * per la ricezione degli eventi.
 * 
 * Un esempio di classe listener:
 * 
 * <pre>
 * public class TiledTest02GestureListener extends ArgonGestureListenerImpl {
 * 
 *     ...
 * 		
 * 	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
 * 		TouchManager.instance().sendMessage(TouchType.SCROLL, distanceX, distanceY);
 * 
 * 		return true;
 * 	}
 * 	
 * 	
 * 	public boolean onDoubleTap(MotionEvent e) {
 * 		TouchManager.instance().sendMessage(TouchType.DOUBLE_TAP, e.getX(), e.getY());
 * 
 * 		return true;
 * 	}
 * 
 * 	
 * 	public void onUp(MotionEvent e) {
 * 		TouchManager.instance().sendMessage(TouchType.UP, e.getX(), e.getY());
 * 	}
 * 
 *     ...
 * }
 * </pre>
 * 
 * 
 * Un esempio di classe application:
 * 
 * <pre>
 * public class TiledApplication {
 * 
 *  ...
 *  
 * 	public void onSceneCreate(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
 * 		...
 * 		TouchManager.instance().setListener(this);
 * 		...
 * 	}
 * 	
 * 
 * 	public void onTouch(TouchType type, float x, float y) {
 * 		Logger.info("onTouch %s - THREAD %s", type, Thread.currentThread().getName());
 * 		switch (type) {
 * 		case SCROLL:
 * 			mapController.scrollFromScreen(x, y);
 * 			break;
 * 		case DOUBLE_TAP:
 * 			Point2 point = mapController.touch(x, y);
 * 			objPlayerController.clearActions();
 * 			objPlayerController.navigateTo(point.x, point.y);
 * 
 * 			break;
 * 		}
 * 
 * 	}
 * 
 *  ...
 *  
 *  }
 * </pre>
 * 
 * @author Francesco Benincasa
 * 
 */
public class TouchManager {

	final ReentrantLock lock = new ReentrantLock();

	private SmartQueue<TouchMessage> queue = new SmartQueue<>();

	private TouchMessagePool pool = new TouchMessagePool();

	private static final TouchManager instance = new TouchManager();

	public static TouchManager instance() {
		return instance;
	}

	public void sendMessage(TouchType type, float x, float y) {
		final ReentrantLock lock = this.lock;
		lock.lock();

		try {
			TouchMessage msg = pool.createPooledObject();

			msg.type = type;
			msg.x = x;
			msg.y = y;

			//Logger.info("creo %s - %s", type, Thread.currentThread().getName());

			queue.add(msg);
		} finally {
			lock.unlock();
		}
	}

	public void setListener(TouchEventListener value) {
		listener = value;
	}

	private TouchEventListener listener;

	public void processMessages() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			TouchMessage msg;

			while (!queue.isEmpty()) {
				msg = queue.pop();
				listener.onTouch(msg.type, msg.x, msg.y);
				pool.freeObject(msg);
			}
		} finally {
			lock.unlock();
		}
	}

}
