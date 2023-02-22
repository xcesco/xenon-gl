package com.abubusoft.xenon.engine

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.abubusoft.xenon.UpdateTaskListener
import com.abubusoft.xenon.XenonApplication4OpenGL
import java.util.concurrent.locks.ReentrantLock

/**
 *
 *
 * Si occupa di gestire le operazioni opengl async in un secondo contesto. Nel caso in cui non sia possibile a causa del device o da impostazioni dell'applicazione, questo manager consente di effettuare le stesse operazioni in modo
 * sincrono, bloccando e quindi introducendo dei lag, ma almeno l'operazione non viene impedita.
 *
 *
 *
 *
 * This class holds the shared context magic and is based on informations gathered from the following sources:
 *
 *
 *  * [Loading-textures-in-a-background-thread-on-Android](http://www.khronos.org/message_boards/showthread.php/9029-Loading-textures-in-a-background-thread-on-Android)
 *  * [5843-Texture-Sharing](http://www.khronos.org/message_boards/showthread.php/5843-Texture-Sharing)
 *  * [why-is-eglmakecurrent-failing-with-egl-bad-match](http://stackoverflow.com/questions/14062803/why-is-eglmakecurrent-failing-with-egl-bad-match)
 *
 *
 * @author Francesco Benincasa
 */
class UpdateManager {
    interface UpdateTask {
        fun execute()
    }

    protected var lock = ReentrantLock()
    protected var thread: UpdateThread? = null

    inner class UpdateThread : Thread("UpdateThread") {
        var handler: Handler? = null
        var listener: UpdateTaskListener? = null
        @SuppressLint("HandlerLeak")
        override fun run() {
            Looper.prepare()
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    lock.lock()
                    try {
                        // process input
                        TouchManager.Companion.instance().processMessages()

                        // process logic
                        listener!!.onFramePrepare(Phase.LOGIC, enlapsedTime, speedAdapter)

                        // sleep(2000);
                        // Logger.debug("UpdateThread on-message %s ", Thread.currentThread().getName());
                        // updated.set(true);
                        // } catch (InterruptedException e) {
                        // e.printStackTrace();
                    } finally {
                        lock.unlock()
                    }
                }
            }
            Looper.loop()
        }
    }

    // Logger.debug("UpdateThread isReady ");
    val isReady: Boolean
        get() {
            lock.lock()
            return try {
                // Logger.debug("UpdateThread isReady ");
                true
            } finally {
                lock.unlock()
            }
        }

    fun init(listener: XenonApplication4OpenGL?) {
        lock.lock()
        try {
            if (thread == null) {
                thread = UpdateThread()
                thread!!.listener = listener
                thread!!.start()
            } else {
                thread!!.listener = listener
            }
        } finally {
            lock.unlock()
        }
    }

    protected var enlapsedTime: Long = 0
    protected var speedAdapter = 0f
    fun onFramePrepare(phase: Phase, enlapsedTimeValue: Long, speedAdapterValue: Float) {
        enlapsedTime = enlapsedTimeValue
        speedAdapter = speedAdapterValue
        thread!!.handler!!.sendEmptyMessage(phase.ordinal)
    }

    companion object {
        /**
         *
         *
         * istanza
         *
         */
        private val instance = UpdateManager()

        /**
         *
         *
         * Singleton
         *
         *
         * @return singleton del manager
         */
        fun instance(): UpdateManager {
            return instance
        }
    }
}