package com.abubusoft.xenon.engine;

import java.util.concurrent.locks.ReentrantLock;

import com.abubusoft.xenon.XenonApplication4OpenGL;
import com.abubusoft.xenon.UpdateTaskListener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * <p>
 * Si occupa di gestire le operazioni opengl async in un secondo contesto. Nel caso in cui non sia possibile a causa del device o da impostazioni dell'applicazione, questo manager consente di effettuare le stesse operazioni in modo
 * sincrono, bloccando e quindi introducendo dei lag, ma almeno l'operazione non viene impedita.
 * </p>
 * 
 * <p>
 * This class holds the shared context magic and is based on informations gathered from the following sources:
 * </p>
 * <ul>
 * <li><a href="http://www.khronos.org/message_boards/showthread.php/9029-Loading-textures-in-a-background-thread-on-Android">Loading-textures-in-a-background-thread-on-Android</link></li>
 * <li><a href="http://www.khronos.org/message_boards/showthread.php/5843-Texture-Sharing">5843-Texture-Sharing</a></li>
 * <li><a href="http://stackoverflow.com/questions/14062803/why-is-eglmakecurrent-failing-with-egl-bad-match">why-is-eglmakecurrent-failing-with-egl-bad-match</a></li>
 * </ul>
 * 
 * @author Francesco Benincasa
 * 
 */
public class UpdateManager {

	public interface UpdateTask {
		void execute();
	}

	protected ReentrantLock lock = new ReentrantLock();

	protected UpdateThread thread;

	public class UpdateThread extends Thread {
		public UpdateThread() {
			super("UpdateThread");
		}

		public Handler handler;

		public UpdateTaskListener listener;

		@SuppressLint("HandlerLeak")
		public void run() {
			Looper.prepare();

			handler = new Handler() {
				public void handleMessage(Message msg) {

					lock.lock();
					try {
						// process input
						TouchManager.instance().processMessages();

						// process logic
						listener.onFramePrepare(Phase.LOGIC, enlapsedTime, speedAdapter);

						// sleep(2000);
						// Logger.debug("UpdateThread on-message %s ", Thread.currentThread().getName());
						// updated.set(true);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			};

			Looper.loop();
		}
	}

	public boolean isReady() {
		lock.lock();
		try {
			// Logger.debug("UpdateThread isReady ");
			return true;
		} finally {
			lock.unlock();
		}
	}

	public void init(XenonApplication4OpenGL listener) {
		lock.lock();
		try {
			if (thread == null) {
				thread = new UpdateThread();
				thread.listener = listener;
				thread.start();
			} else {
				thread.listener = listener;
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * <p>
	 * istanza
	 * </p>
	 */
	private static UpdateManager instance = new UpdateManager();

	/**
	 * <p>
	 * Singleton
	 * </p>
	 * 
	 * @return singleton del manager
	 */
	public static UpdateManager instance() {
		return instance;
	}

	public UpdateManager() {
	}

	protected long enlapsedTime;

	protected float speedAdapter;

	public void onFramePrepare(Phase phase, long enlapsedTimeValue, float speedAdapterValue) {
		enlapsedTime = enlapsedTimeValue;
		speedAdapter = speedAdapterValue;
		thread.handler.sendEmptyMessage(phase.ordinal());
	}
}
