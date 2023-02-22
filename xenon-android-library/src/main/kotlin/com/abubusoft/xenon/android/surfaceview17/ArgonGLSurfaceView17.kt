/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.abubusoft.xenon.android.surfaceview17

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ApplicationInfo
import android.opengl.*
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.AsyncOperationManager
import com.abubusoft.xenon.opengl.XenonGLRenderer
import com.abubusoft.xenon.settings.XenonSettings
import java.lang.ref.WeakReference

/**
 * GLSurfaceView using EGl14 instead of EGL10
 *
 * @author Perraco Labs (August-2015)
 * @repository https://github.com/perracolabs/GLSurfaceViewEGL14
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class ArgonGLSurfaceView17 : ArgonGLView, SurfaceHolder.Callback {
    private inner class DefaultContextFactory : EGLContextFactory {
        private val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        override fun createContext(display: EGLDisplay?, config: EGLConfig?): EGLContext {
            val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion, EGL14.EGL_NONE)
            val context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, if (mEGLContextClientVersion != 0) attrib_list else null, 0)
            val settings = XenonBeanContext.getBean<XenonSettings>(XenonBeanType.XENON_SETTINGS)
            if (settings.openGL.asyncMode) {
                AsyncOperationManager.instance().init(context, display, config)
            } else {
                AsyncOperationManager.instance().init()
            }
            return context
        }

        override fun destroyContext(display: EGLDisplay?, context: EGLContext?) {
            // distrugge il context secondario, usato per le operazioni
            // async
            val settings = XenonBeanContext.getBean<XenonSettings>(XenonBeanType.XENON_SETTINGS)
            if (settings.openGL.asyncMode) {
                if (!AsyncOperationManager.instance().destroy()) {
                    Logger.error("display: texture loader context: $context")
                    Logger.info("tid=" + Thread.currentThread().id)
                    Logger.error("eglDestroyContex %s", EGL14.eglGetError())
                }
            }
            if (EGL14.eglDestroyContext(display, context) == false) {
                Logger.error("display:$display context: $context")
                if (LOG_THREADS) {
                    Logger.info("tid=" + java.lang.Long.toString(Thread.currentThread().id))
                }
                EglHelper.throwEglException("eglDestroyContex", EGL14.eglGetError())
            }
            EGL14.eglReleaseThread()
        }
    }

    private class DefaultWindowSurfaceFactory : EGLWindowSurfaceFactory {
        override fun createWindowSurface(display: EGLDisplay?, config: EGLConfig?, nativeWindow: Any?): EGLSurface? {
            var result: EGLSurface? = null
            try {
                val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)
                result = EGL14.eglCreateWindowSurface(display, config, nativeWindow, surfaceAttribs, 0)
            } catch (ex: Throwable) {
                // This exception indicates that the surface flinger surface
                // is not valid. This can happen if the surface flinger surface has
                // been torn down, but the application has not yet been
                // notified via SurfaceHolder.Callback.surfaceDestroyed.
                // In theory the application should be notified first,
                // but in practice sometimes it is not. See b/4588890
                Logger.error("eglCreateWindowSurface call failed", ex)
            } finally {
                if (result == null) {
                    try {
                        // Hack to avoid pegged CPU bug
                        Thread.sleep(10)
                    } catch (t: InterruptedException) {
                        Logger.error("CPU was pegged")
                    }
                }
            }
            return result
        }

        override fun destroySurface(display: EGLDisplay?, surface: EGLSurface?) {
            if (EGL14.eglDestroySurface(display, surface) == false) {
                Logger.error("eglDestroySurface Failed")
            }
        }
    }

    /**
     * An interface for customizing the eglCreateContext and eglDestroyContext calls.
     *
     *
     * This interface must be implemented by clients wishing to call [ArgonGLSurfaceView17.setEGLContextFactory]
     */
    interface EGLContextFactory {
        /**
         * @param display
         * EGL Display
         * @param eglConfig
         * EGL Configuration
         * @return EGL Context
         */
        fun createContext(display: EGLDisplay?, eglConfig: EGLConfig?): EGLContext

        /**
         * @param egl
         * EGL object
         * @param display
         * EGL Display
         * @param context
         * EGL Context
         */
        fun destroyContext(display: EGLDisplay?, context: EGLContext?)
    }

    /**
     * An EGL helper class.
     */
    private class EglHelper(private val mGLViewWeakRef: WeakReference<ArgonGLSurfaceView17>) {
        var mEglConfig: EGLConfig? = null
        var mEglContext: EGLContext? = null
        var mEglDisplay: EGLDisplay? = null
        var mEglSurface: EGLSurface? = null

        /**
         * Create an egl surface for the current SurfaceHolder surface. If a surface already exists, destroy it before creating the new surface.
         *
         * @return true if the surface was created successfully.
         */
        fun createSurface(): Boolean {
            if (LOG_EGL) {
                Logger.warn("tid=" + java.lang.Long.toString(Thread.currentThread().id))
            }
            if (mEglDisplay == null) {
                throw RuntimeException("eglDisplay not initialized")
            }
            if (mEglConfig == null) {
                throw RuntimeException("mEglConfig not initialized")
            }

            // The window size has changed, so we need to create a new surface.
            destroySurfaceImp()

            // Create an EGL surface we can render into.
            val view = mGLViewWeakRef.get()
            if (view != null) {
                mEglSurface = view.mEGLWindowSurfaceFactory!!.createWindowSurface(mEglDisplay, mEglConfig, view.holder)
            } else {
                mEglSurface = null
            }
            if (mEglSurface == null || mEglSurface === EGL14.EGL_NO_SURFACE) {
                val error = EGL14.eglGetError()
                if (error == EGL14.EGL_BAD_NATIVE_WINDOW) {
                    Logger.error("createWindowSurface returned EGL_BAD_NATIVE_WINDOW.")
                }
                return false
            }

            // Before we can issue GL commands, we need to make sure the context is current and bound to a surface.
            return makeCurrent()
        }

        fun destroySurface() {
            if (LOG_EGL) {
                Logger.warn("Destroying surface. tid=" + java.lang.Long.toString(Thread.currentThread().id))
            }
            destroySurfaceImp()
        }

        private fun destroySurfaceImp() {
            if (mEglSurface != null && mEglSurface !== EGL14.EGL_NO_SURFACE) {
                EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                val view = mGLViewWeakRef.get()
                if (view != null) {
                    view.mEGLWindowSurfaceFactory!!.destroySurface(mEglDisplay, mEglSurface)
                }
                mEglSurface = null
            }
        }

        /**
         * xcesco: esco e distruggo il display
         */
        fun disconnectDisplay() {
            if (mEglDisplay != null) {
                Logger.info("disconnectDisplay %s", mEglDisplay)
                // xcesco: non rimuoviamo il display	
                // rilasciamo tutte le informazioni OPENGL associate al thrread.
                EGL14.eglReleaseThread()
                EGL14.eglTerminate(mEglDisplay)
                mEglDisplay = null
            }
        }

        fun finish() {
            if (LOG_EGL) {
                Logger.warn("Finishing. tid=" + java.lang.Long.toString(Thread.currentThread().id))
            }
            if (mEglContext != null) {
                val view = mGLViewWeakRef.get()
                if (view != null) {
                    view.mEGLContextFactory!!.destroyContext(mEglDisplay, mEglContext)
                }
                mEglContext = null
            }
            if (mEglDisplay != null) {
                // xcesco: non rimuoviamo il display
                //EGL14.eglTerminate(this.mEglDisplay);
                //this.mEglDisplay = null;
            }
        }

        fun makeCurrent(): Boolean {
            if (mEglDisplay == null || mEglSurface == null || mEglContext == null) {
                return false
            }
            if (EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext) == false) {
                if (EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext) == false) {
                    if (EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext) == false) {
                        val errorCode = EGL14.eglGetError()

                        // Could not make the context current, probably because the underlying SurfaceView surface has been destroyed.
                        logEglErrorAsWarning("eglMakeCurrent", errorCode)
                        return false
                    }
                }
            }
            return true
        }

        /**
         * Initialize EGL for a given configuration specification
         */
        fun start() {
            if (LOG_EGL) {
                Logger.warn("start() tid=" + java.lang.Long.toString(Thread.currentThread().id))
            }

            /*
			 * Get to the default display.
			 */if (mEglDisplay == null) {
                mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
                if (mEglDisplay === EGL14.EGL_NO_DISPLAY) {
                    throw RuntimeException("eglGetDisplay failed")
                }

                /*
				 * We can now initialize EGL for that display
				 */
                val version = IntArray(2)
                if (EGL14.eglInitialize(mEglDisplay, version, 0, version, 1) == false) {
                    throw RuntimeException("eglInitialize failed")
                }
            }
            val view = mGLViewWeakRef.get()
            if (view == null) {
                mEglConfig = null
                mEglContext = null
            } else {
                //this.mEglConfig = EGL14Config.chooseConfig(this.mEglDisplay, view.mRecordable);
                mEglConfig = ConfigChooserHelper17.Companion.listConfig(mEglDisplay).get(0)


                /*
				 * Create an EGL context. We want to do this as rarely as we can, because an EGL context is a somewhat heavy object.
				 */mEglContext = view.mEGLContextFactory!!.createContext(mEglDisplay, mEglConfig)
            }
            if (mEglContext == null || mEglContext === EGL14.EGL_NO_CONTEXT) {
                mEglContext = null
                throwEglException("createContext")
            }
            if (LOG_EGL) {
                Logger.warn("createContext " + mEglContext + " tid=" + java.lang.Long.toString(Thread.currentThread().id))
            }
            mEglSurface = null
        }

        /**
         * Display the current render surface.
         *
         * @return the EGL error code from eglSwapBuffers.
         */
        fun swap(): Int {
            if (mEglDisplay == null) {
                val error = EGL14.eglGetError()
                return if (error != 0) error else EGL14.EGL_BAD_DISPLAY
            }
            if (mEglSurface == null) {
                val error = EGL14.eglGetError()
                return if (error != 0) error else EGL14.EGL_BAD_SURFACE
            }
            return if (EGL14.eglSwapBuffers(mEglDisplay, mEglSurface) == false) {
                EGL14.eglGetError()
            } else EGL14.EGL_SUCCESS
        }

        companion object {
            var cachedErrors: HashMap<String, Int>? = null
            private fun cacheError(errorMessage: String) {
                try {
                    if (cachedErrors == null) {
                        cachedErrors = HashMap()
                    }
                    var errorCount = 0
                    if (cachedErrors!!.containsKey(errorMessage) == true) {
                        errorCount = cachedErrors!![errorMessage]!!.toInt()
                        errorCount++
                    }
                    if (cachedErrors!!.size < 100) {
                        cachedErrors!![errorMessage] = Integer.valueOf(errorCount)
                    }
                } catch (ex: Exception) {
                    Logger.error("Failed to cache error.", ex)
                }
            }

            fun formatEglError(function: String, error: Int): String {
                return function + " failed: " + getErrorString(error)
            }

            fun logEglErrorAsWarning(function: String, error: Int) {
                val errorMessage = formatEglError(function, error)
                Logger.error(errorMessage)
                cacheError(errorMessage)
            }

            private fun throwEglException(function: String) {
                throwEglException(function, EGL14.eglGetError())
            }

            fun throwEglException(function: String, error: Int) {
                val message = formatEglError(function, error)
                if (LOG_THREADS) {
                    Logger.error("EGL Exception. tid=" + java.lang.Long.toString(Thread.currentThread().id) + " Error: " + message)
                }
                throw RuntimeException(message)
            }
        }
    }

    /**
     * An interface for customizing the eglCreateWindowSurface and eglDestroySurface calls.
     *
     *
     * This interface must be implemented by clients wishing to call [ArgonGLSurfaceView17.setEGLWindowSurfaceFactory]
     */
    interface EGLWindowSurfaceFactory {
        /**
         * @param egl
         * EGL object
         * @param display
         * EGL Display
         * @param config
         * EGL Configuration
         * @param nativeWindow
         * Native window
         * @return EGL Context or null if the surface cannot be constructed
         */
        fun createWindowSurface(display: EGLDisplay?, config: EGLConfig?, nativeWindow: Any?): EGLSurface?

        /**
         * @param egl
         * EGL object
         * @param display
         * EGL Display
         * @param surface
         * Surface to be destroyed
         */
        fun destroySurface(display: EGLDisplay?, surface: EGLSurface?)
    }

    /**
     * A generic GL Thread. Takes care of initializing EGL and GL. Delegates to a IRendererEGL14 instance to do the actual drawing. Can be configured to render continuously or on request.
     *
     * All potentially blocking synchronization is done through the sGLThreadManager object. This avoids multiple-lock ordering issues.
     *
     */
    internal inner class GLThread(instanceWeakRef: WeakReference<ArgonGLSurfaceView17>) : Thread() {
        private var mEglHelper: EglHelper? = null
        val mEventQueue = ArrayList<Runnable>()
        var mExited = false
        private var mFinishedCreatingEglSurface = false

        /**
         * Set once at thread construction time, nulled out when the parent view is garbage called. This weak reference allows the GLSurfaceViewEGL14 to be garbage collected while the GLThread is still alive.
         */
        private val mGLViewWeakRef: WeakReference<ArgonGLSurfaceView17>
        private var mHasSurface = false
        private var mHaveEglContext = false
        private var mHaveEglSurface = false
        private var mHeight = 0
        private var mPaused = false
        private var mRenderComplete = false
        private var mRenderMode: Int
        private var mRequestPaused = false
        private var mRequestRender = true

        // Once the thread is started, all accesses to the following member
        // variables are protected by the sGLThreadManager monitor
        private var mShouldExit = false
        private var mShouldReleaseEglContext = false
        private var mSizeChanged = true
        private var mSurfaceIsBad = false
        private var mWaitingForSurface = false
        private var mWidth = 0

        init {
            mRenderMode = XenonGLRenderer.RENDERMODE_CONTINUOUSLY
            mGLViewWeakRef = instanceWeakRef
        }

        fun ableToDraw(): Boolean {
            return mHaveEglContext && mHaveEglSurface && readyToDraw()
        }

        /**
         * xcesco: disconnette il display al momento di uscire
         */
        private fun disconnectDisplay() {
            mEglHelper!!.disconnectDisplay()
        }

        var renderMode: Int
            get() {
                synchronized(sGLThreadManager) { return mRenderMode }
            }
            set(renderMode) {
                require(XenonGLRenderer.RENDERMODE_WHEN_DIRTY <= renderMode && renderMode <= XenonGLRenderer.RENDERMODE_CONTINUOUSLY) { "renderMode" }
                synchronized(sGLThreadManager) {
                    mRenderMode = renderMode
                    sGLThreadManager.notifyAll()
                }
            }

        @Throws(InterruptedException::class)
        private fun guardedRun() {
            mEglHelper = EglHelper(mGLViewWeakRef)
            mHaveEglContext = false
            mHaveEglSurface = false
            try {
                var createEglContext = false
                var createEglSurface = false
                var createGlInterface = false
                var lostEglContext = false
                var sizeChanged = false
                var wantRenderNotification = false
                var doRenderNotification = false
                var askedToReleaseEglContext = false
                var w = 0
                var h = 0
                var event: Runnable? = null
                while (true) {
                    synchronized(sGLThreadManager) {
                        while (true) {
                            if (mShouldExit == true) {
                                return
                            }
                            if (mEventQueue.isEmpty() == false) {
                                event = mEventQueue.removeAt(0)
                                break
                            }

                            // Update the pause state.
                            var pausing = false
                            if (mPaused != mRequestPaused) {
                                pausing = mRequestPaused
                                mPaused = mRequestPaused
                                sGLThreadManager.notifyAll()
                                if (LOG_PAUSE_RESUME) {
                                    Logger.info("mPaused is now " + java.lang.Boolean.toString(mPaused) + ". tid=" + java.lang.Long.toString(this.id))
                                }
                            }

                            // Do we need to give up the EGL context?
                            if (mShouldReleaseEglContext == true) {
                                if (LOG_SURFACE) {
                                    Logger.info("Releasing EGL context because asked to tid=" + java.lang.Long.toString(this.id))
                                }
                                stopEglSurfaceLocked()
                                stopEglContextLocked()
                                mShouldReleaseEglContext = false
                                askedToReleaseEglContext = true
                            }

                            // Have we lost the EGL context?
                            if (lostEglContext == true) {
                                stopEglSurfaceLocked()
                                stopEglContextLocked()
                                lostEglContext = false
                            }

                            // When pausing, release the EGL surface:
                            if (pausing == true && mHaveEglSurface == true) {
                                if (LOG_SURFACE) {
                                    Logger.info("Releasing EGL surface because paused tid=" + java.lang.Long.toString(this.id))
                                }
                                stopEglSurfaceLocked()
                            }

                            // When pausing, optionally release the EGL Context:
                            if (pausing == true && mHaveEglContext == true) {
                                val view = mGLViewWeakRef.get()
                                val preserveEglContextOnPause = view?.mPreserveEGLContextOnPause ?: false
                                if (preserveEglContextOnPause == false || sGLThreadManager.shouldReleaseEGLContextWhenPausing()) {
                                    stopEglContextLocked()
                                    if (LOG_SURFACE) {
                                        Logger.info("Releasing EGL context because paused tid=" + java.lang.Long.toString(this.id))
                                    }
                                }
                            }

                            // Have we lost the SurfaceView surface?
                            if (mHasSurface == false && mWaitingForSurface == false) {
                                if (LOG_SURFACE) {
                                    Logger.info("Noticed surfaceView surface lost tid=" + java.lang.Long.toString(this.id))
                                }
                                if (mHaveEglSurface == true) {
                                    stopEglSurfaceLocked()
                                }
                                mWaitingForSurface = true
                                mSurfaceIsBad = false
                                sGLThreadManager.notifyAll()
                            }

                            // Have we acquired the surface view surface?
                            if (mHasSurface == true && mWaitingForSurface == true) {
                                if (LOG_SURFACE) {
                                    Logger.info("Noticed surfaceView surface acquired tid=" + java.lang.Long.toString(this.id))
                                }
                                mWaitingForSurface = false
                                sGLThreadManager.notifyAll()
                            }
                            if (doRenderNotification == true) {
                                if (LOG_SURFACE) {
                                    Logger.info("Sending render notification tid=" + java.lang.Long.toString(this.id))
                                }
                                wantRenderNotification = false
                                doRenderNotification = false
                                mRenderComplete = true
                                sGLThreadManager.notifyAll()
                            }

                            // Ready to draw?
                            if (readyToDraw() == true) {

                                // If we don't have an EGL context, try to acquire one.
                                if (mHaveEglContext == false) {
                                    if (askedToReleaseEglContext == true) {
                                        askedToReleaseEglContext = false
                                    } else if (sGLThreadManager.tryAcquireEglContextLocked(this)) {
                                        try {
                                            mEglHelper!!.start()
                                        } catch (t: RuntimeException) {
                                            sGLThreadManager.releaseEglContextLocked(this)
                                            throw t
                                        }
                                        mHaveEglContext = true
                                        createEglContext = true
                                        sGLThreadManager.notifyAll()
                                    }
                                }
                                if (mHaveEglContext == true && mHaveEglSurface == false) {
                                    mHaveEglSurface = true
                                    createEglSurface = true
                                    createGlInterface = true
                                    sizeChanged = true
                                }
                                if (mHaveEglSurface == true) {
                                    if (mSizeChanged == true) {
                                        sizeChanged = true
                                        w = mWidth
                                        h = mHeight
                                        wantRenderNotification = true
                                        if (LOG_SURFACE) {
                                            Logger.info("Noticing that we want render notification tid=" + java.lang.Long.toString(this.id))
                                        }

                                        // Destroy and recreate the EGL surface.
                                        createEglSurface = true
                                        mSizeChanged = false
                                    }
                                    mRequestRender = false
                                    sGLThreadManager.notifyAll()
                                    break
                                }
                            }

                            // By design, this is the only place in a GLThread thread where we wait().
                            if (LOG_THREADS_WAIT) {
                                Logger.info(
                                    "waiting tid=" + java.lang.Long.toString(this.id) + " mHaveEglContext: " + java.lang.Boolean.toString(mHaveEglContext) + " mHaveEglSurface: " + java.lang.Boolean.toString(
                                        mHaveEglSurface
                                    )
                                            + " mFinishedCreatingEglSurface: " + java.lang.Boolean.toString(mFinishedCreatingEglSurface) + " mPaused: " + java.lang.Boolean.toString(
                                        mPaused
                                    ) + " mHasSurface: " + java.lang.Boolean.toString(
                                        mHasSurface
                                    )
                                            + " mSurfaceIsBad: " + java.lang.Boolean.toString(mSurfaceIsBad) + " mWaitingForSurface: " + java.lang.Boolean.toString(
                                        mWaitingForSurface
                                    ) + " mWidth: " + Integer.toString(
                                        mWidth
                                    ) + " mHeight: "
                                            + Integer.toString(mHeight) + " mRequestRender: " + java.lang.Boolean.toString(mRequestRender) + " mRenderMode: " + Integer.toString(
                                        mRenderMode
                                    )
                                )
                            }
                            sGLThreadManager.wait()
                        }
                    } // end of synchronized(sGLThreadManager)
                    if (event != null) {
                        event!!.run()
                        event = null
                        continue
                    }
                    if (createEglSurface == true) {
                        if (LOG_SURFACE) {
                            Logger.warn("egl createSurface")
                        }
                        if (mEglHelper!!.createSurface() == true) {
                            synchronized(sGLThreadManager) {
                                mFinishedCreatingEglSurface = true
                                sGLThreadManager.notifyAll()
                            }
                        } else {
                            synchronized(sGLThreadManager) {
                                mFinishedCreatingEglSurface = false
                                mSurfaceIsBad = false
                                sGLThreadManager.notifyAll()
                            }
                            continue
                        }
                        createEglSurface = false
                    }
                    if (createGlInterface == true) {
                        createGlInterface = false
                    }
                    if (createEglContext == true) {
                        if (LOG_RENDERER) {
                            Logger.warn("onSurfaceCreated")
                        }
                        val view = mGLViewWeakRef.get()
                        view?.mRenderer?.onSurfaceCreated()
                        createEglContext = false
                    }
                    if (sizeChanged == true) {
                        if (LOG_RENDERER) {
                            Logger.warn("onSurfaceChanged(" + Integer.toString(w) + ", " + Integer.toString(h) + ")")
                        }
                        val view = mGLViewWeakRef.get()
                        view?.mRenderer?.onSurfaceChanged(w, h)
                        sizeChanged = false
                    }
                    if (LOG_RENDERER_DRAW_FRAME) {
                        Logger.warn("onDrawFrame tid=" + java.lang.Long.toString(this.id))
                    }
                    run {
                        val view = this.mGLViewWeakRef.get()
                        view?.mRenderer?.onDrawFrame()
                    }
                    val swapError = mEglHelper!!.swap()
                    when (swapError) {
                        EGL14.EGL_SUCCESS -> {}
                        EGL14.EGL_CONTEXT_LOST -> {
                            if (LOG_SURFACE) {
                                Logger.info("egl context lost tid=" + java.lang.Long.toString(this.id))
                            }
                            lostEglContext = true
                        }
                        else -> {

                            // Other errors typically mean that the current surface is bad,
                            // probably because the SurfaceView surface has been destroyed,
                            // but we haven't been notified yet.
                            // Log the error to help developers understand why rendering stopped.
                            EglHelper.logEglErrorAsWarning("eglSwapBuffers", swapError)
                            synchronized(sGLThreadManager) {
                                mSurfaceIsBad = true
                                sGLThreadManager.notifyAll()
                            }
                        }
                    }
                    if (wantRenderNotification == true) {
                        doRenderNotification = true
                    }
                }
            } finally {
                /*
				 * clean-up everything...
				 */
                synchronized(sGLThreadManager) {
                    stopEglSurfaceLocked()
                    stopEglContextLocked()
                    if (destroyDisplayOnExit) {
                        disconnectDisplay()
                    }
                }
            }
        }

        fun onPause() {
            synchronized(sGLThreadManager) {
                if (LOG_PAUSE_RESUME) {
                    Logger.info("onPause tid=" + java.lang.Long.toString(this.id))
                }
                mRequestPaused = true
                sGLThreadManager.notifyAll()
                while (mExited == false && mPaused == false) {
                    if (LOG_PAUSE_RESUME) {
                        Logger.info("onPause waiting for mPaused.")
                    }
                    try {
                        sGLThreadManager.wait()
                    } catch (ex: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        fun onResume() {
            synchronized(sGLThreadManager) {
                if (LOG_PAUSE_RESUME) {
                    Logger.info("onResume tid=" + java.lang.Long.toString(this.id))
                }
                mRequestPaused = false
                mRequestRender = true
                mRenderComplete = false
                sGLThreadManager.notifyAll()
                while (mExited == false && mPaused == true && mRenderComplete == false) {
                    if (LOG_PAUSE_RESUME) {
                        Logger.info("onResume waiting for !mPaused.")
                    }
                    try {
                        sGLThreadManager.wait()
                    } catch (ex: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        fun onWindowResize(w: Int, h: Int) {
            synchronized(sGLThreadManager) {
                mWidth = w
                mHeight = h
                mSizeChanged = true
                mRequestRender = true
                mRenderComplete = false
                sGLThreadManager.notifyAll()

                // Wait for thread to react to resize and render a frame
                while (mExited == false && mPaused == false && mRenderComplete == false && ableToDraw() == true) {
                    if (LOG_SURFACE) {
                        Logger.info("onWindowResize waiting for render complete from tid=" + java.lang.Long.toString(this.id))
                    }
                    try {
                        sGLThreadManager.wait()
                    } catch (ex: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        /**
         * Queue an "event" to be run on the GL rendering thread.
         *
         * @param runnable
         * the runnable to be run on the GL rendering thread.
         */
        fun queueEvent(runnable: Runnable?) {
            requireNotNull(runnable) { "'runnable' must not be null" }
            synchronized(sGLThreadManager) {
                mEventQueue.add(runnable)
                Logger.debug("Queued events: " + Integer.toString(mEventQueue.size))
                sGLThreadManager.notifyAll()
            }
        }

        private fun readyToDraw(): Boolean {
            return !mPaused && mHasSurface && !mSurfaceIsBad && mWidth > 0 && mHeight > 0 && (mRequestRender || mRenderMode) == XenonGLRenderer.RENDERMODE_CONTINUOUSLY
        }

        fun requestExitAndWait() {
            // don't call this from GLThread thread or it is a guaranteed deadlock!
            synchronized(sGLThreadManager) {
                mShouldExit = true
                sGLThreadManager.notifyAll()
                while (mExited == false) {
                    try {
                        sGLThreadManager.wait()
                    } catch (ex: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        fun requestReleaseEglContextLocked() {
            mShouldReleaseEglContext = true
            sGLThreadManager.notifyAll()
        }

        fun requestRender() {
            synchronized(sGLThreadManager) {
                if (mRequestPaused == false) {
                    mRequestRender = true
                    sGLThreadManager.notifyAll()
                }
            }
        }

        override fun run() {
            this.name = "GLThread " + java.lang.Long.toString(this.id)
            if (LOG_THREADS) {
                Logger.info("starting tid=" + java.lang.Long.toString(this.id))
            }
            try {
                guardedRun()
            } catch (e: InterruptedException) {
                // fall thru and exit normally
            } finally {
                sGLThreadManager.threadExiting(this)
            }
        }

        /*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
        private fun stopEglContextLocked() {
            val view = mGLViewWeakRef.get()
            if (view != null && view.mRenderer != null) {
                view.mRenderer.onDestroy()
            }
            if (mHaveEglContext) {
                mEglHelper!!.finish()
                mHaveEglContext = false
                sGLThreadManager.releaseEglContextLocked(this)
            }
        }

        /*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
        private fun stopEglSurfaceLocked() {
            if (mHaveEglSurface) {
                mHaveEglSurface = false
                mEglHelper!!.destroySurface()
            }
        }

        // End of member variables protected by the sGLThreadManager monitor.
        fun surfaceCreated() {
            synchronized(sGLThreadManager) {
                if (LOG_THREADS) {
                    Logger.info("surfaceCreated tid=" + java.lang.Long.toString(this.id))
                }
                mHasSurface = true
                mFinishedCreatingEglSurface = false
                sGLThreadManager.notifyAll()
                while (mWaitingForSurface && !mFinishedCreatingEglSurface && !mExited) {
                    try {
                        sGLThreadManager.wait()
                    } catch (e: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }

        fun surfaceDestroyed() {
            synchronized(sGLThreadManager) {
                if (LOG_THREADS) {
                    Logger.info("surfaceDestroyed tid=" + java.lang.Long.toString(this.id))
                }
                mHasSurface = false
                sGLThreadManager.notifyAll()
                while (mWaitingForSurface == false && mExited == false) {
                    try {
                        sGLThreadManager.wait()
                    } catch (e: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }
    }

    class GLThreadManager {
        private var mEglOwner: GLThread? = null

        /**
         * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides support for hardware-accelerated views, therefore multiple EGL contexts are supported on all Android 3.0+ EGL drivers.
         */
        private val mLimitedGLESContexts = false

        init {
            Logger.info("GLThreadManager instance created")
        }

        /*
		 * Releases the EGL context. Requires that we are already in the sGLThreadManager monitor when this is called.
		 */
        fun releaseEglContextLocked(thread: GLThread) {
            if (mEglOwner === thread) {
                mEglOwner = null
            }
            this.notifyAll()
        }

        @Synchronized
        fun shouldReleaseEGLContextWhenPausing(): Boolean {
            // Release the EGL context when pausing even if
            // the hardware supports multiple EGL contexts.
            // Otherwise the device could run out of EGL contexts.
            return mLimitedGLESContexts
        }

        @Synchronized
        fun threadExiting(thread: GLThread) {
            if (LOG_THREADS) {
                Logger.info("Exiting tid=" + java.lang.Long.toString(thread.id))
            }
            thread.mExited = true
            if (mEglOwner === thread) {
                mEglOwner = null
            }
            this.notifyAll()
        }

        /*
		 * Tries once to acquire the right to use an EGL context. Does not block. Requires that we are already in the sGLThreadManager monitor when this is called.
		 * 
		 * @return true if the right to use an EGL context was acquired.
		 */
        fun tryAcquireEglContextLocked(thread: GLThread): Boolean {
            if (mEglOwner === thread || mEglOwner == null) {
                mEglOwner = thread
                this.notifyAll()
            }
            return true
        }
    }

    /**
     * serve per il nexus 9 (e mipad)
     */
    protected var destroyDisplayOnExit = false
    protected var mDebugFlags = 0
    private var mDetached = false

    //protected EGL14ConfigChooser mEGLConfigChooser;
    protected var mEGLContextClientVersion = 0
    protected var mEGLContextFactory: EGLContextFactory? = null
    protected var mEGLWindowSurfaceFactory: EGLWindowSurfaceFactory? = null
    private var mGLThread: GLThread? = null
    protected var mPreserveEGLContextOnPause = false
    protected var mRecordable = false
    private val mThisWeakRef = WeakReference(this)

    /**
     * Standard View constructor. In order to render something, you must call [.setRenderer] to register a renderer.
     *
     * @param context
     * Context used for operations
     */
    constructor(context: Context) : super(context) {
        setWillNotDraw(false)
        init(context)
    }

    /**
     * Standard View constructor. In order to render something, you must call [.setRenderer] to register a renderer.
     *
     * @param context
     * Context used for operations
     * @param attrs
     * Attributes
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
        init(context)
    }

    private fun checkRenderThreadState() {
        check(mGLThread == null) { "setRenderer has already been called for this instance." }
    }

    /**
     * @see Object.finalize
     */
    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            if (mGLThread != null) {
                // GLThread may still be running if this view was never attached to a window.
                mGLThread!!.requestExitAndWait()
            }
        } finally {
            super.finalize()
        }
    }

    /**
     * Get the current value of the debug flags.
     *
     * @return the current value of the debug flags.
     */
    fun getDebugFlags(): Int {
        return mDebugFlags
    }
    // ----------------------------------------------------------------------
    /**
     * @return true if the EGL context will be preserved when paused
     */
    fun getPreserveEGLContextOnPause(): Boolean {
        return mPreserveEGLContextOnPause
    }
    /**
     * Get the current rendering mode. May be called from any thread. Must not be called before a renderer has been set.
     *
     * @return the current rendering mode. see RENDERMODE_CONTINUOUSLY see RENDERMODE_WHEN_DIRTY
     */
    /**
     * Set the rendering mode. When renderMode is RENDERMODE_CONTINUOUSLY, the renderer is called repeatedly to re-render the scene. When renderMode is RENDERMODE_WHEN_DIRTY, the renderer only rendered when the surface is created, or when
     * [.requestRender] is called. Defaults to RENDERMODE_CONTINUOUSLY.
     *
     *
     * Using RENDERMODE_WHEN_DIRTY can improve battery life and overall system performance by allowing the GPU and CPU to idle when the view does not need to be updated.
     *
     *
     * This method can only be called after [.setRenderer]
     *
     * @param renderMode
     * one of the RENDERMODE_X constants see RENDERMODE_CONTINUOUSLY see RENDERMODE_WHEN_DIRTY
     */
    var renderMode: Int
        get() = mGLThread!!.renderMode
        set(renderMode) {
            mGLThread!!.renderMode = renderMode
        }

    /**
     * Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed
     */
    private fun hookCallbacks() {
        val holder = this.holder
        holder.addCallback(this)
    }

    private fun init(context: Context) {
        // Request an 2.0 OpenGL ES compatible context
        setEGLContextClientVersion(2)
        if (ApplicationInfo.FLAG_DEBUGGABLE.let {
                context.applicationContext.applicationInfo.flags = context.applicationContext.applicationInfo.flags and it; context.applicationContext.applicationInfo.flags
            } != 0) {
            setDebugFlags(DEBUG_LOG_GL_CALLS or DEBUG_CHECK_GL_ERROR)
        }
        hookCallbacks()
    }

    /**
     * This method is used as part of the View class and is not normally called or sub-classed by clients of Control.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (LOG_ATTACH_DETACH) {
            Logger.debug("onAttachedToWindow reattach: " + java.lang.Boolean.toString(mDetached))
        }
        if (mDetached && this.mRenderer != null) {
            var renderMode = XenonGLRenderer.RENDERMODE_CONTINUOUSLY
            if (mGLThread != null) {
                renderMode = mGLThread!!.renderMode
            }
            mGLThread = GLThread(mThisWeakRef)
            if (renderMode != XenonGLRenderer.RENDERMODE_CONTINUOUSLY) {
                mGLThread!!.renderMode = renderMode
            }
            mGLThread!!.start()
        }
        mDetached = false
    }

    /**
     * This method is used as part of the View class and is not normally called or sub-classed by clients of the Control. Must not be called before a renderer has been set.
     */
    override fun onDetachedFromWindow() {
        if (LOG_ATTACH_DETACH) {
            Logger.debug("Detaching from window.")
        }
        if (mGLThread != null) {
            mGLThread!!.requestExitAndWait()
        }
        mDetached = true
        super.onDetachedFromWindow()
    }

    /**
     * Inform the view that the activity is paused. The owner of this view must call this method when the activity is paused. Calling this method will pause the rendering thread. Must not be called before a renderer has been set.
     */
    override fun onPause() {
        if (mGLThread == null || this.mRenderer == null) {
            return
        }
        mGLThread!!.onPause()
        mRenderer.onPause()
    }

    /**
     * Inform the view that the activity is resumed. The owner of this view must call this method when the activity is resumed. Calling this method will recreate the OpenGL display and resume the rendering thread. Must not be called before
     * a renderer has been set.
     */
    override fun onResume() {
        if (mGLThread == null || this.mRenderer == null) {
            return
        }

        //this.hookCallbacks();
        mGLThread!!.onResume()
        mRenderer.onResume()
    }

    /**
     * Queue a runnable to be run on the GL rendering thread.
     *
     *
     * This can be used to communicate with the Renderer on the rendering thread.
     *
     *
     * Must not be called before a renderer has been set.
     *
     * @param runnable
     * The runnable to be run on the GL rendering thread.
     */
    fun queueEvent(runnable: Runnable?) {
        mGLThread!!.queueEvent(runnable)
    }

    /**
     * Request that the renderer render a frame. This method is typically used when the render mode has been set to RENDERMODE_WHEN_DIRTY, so that frames are only rendered on demand. May be called from any thread. Must not be called before
     * a renderer has been set.
     */
    fun requestRender() {
        mGLThread!!.requestRender()
    }

    /**
     * Set the debug flags to a new value. The value is constructed by OR-together zero or more of the DEBUG_CHECK_* constants. The debug flags take effect whenever a surface is created. The default value is zero.
     *
     * @param debugFlags
     * the new debug flags see DEBUG_CHECK_GL_ERROR see DEBUG_LOG_GL_CALLS
     */
    override fun setDebugFlags(debugFlags: Int) {
        mDebugFlags = debugFlags
    }

    /**
     * Inform the default EGLContextFactory and default EGLConfigChooser which EGLContext client version to pick.
     *
     *
     * Use this method to create an OpenGL ES 2.0-compatible context. Example:
     *
     * <pre class="prettyprint">
     * public MyView(Context context) {
     * super(context);
     * setEGLContextClientVersion(2); // Pick an OpenGL ES 2.0 context.
     * setRenderer(new MyRenderer());
     * }
    </pre> *
     *
     *
     * Note: Activities which require OpenGL ES 2.0 should indicate this by setting @lt;uses-feature android:glEsVersion="0x00020000" /> in the activity's AndroidManifest.xml file.
     *
     *
     * If this method is called, it must be called before [.setRenderer] is called.
     *
     *
     *
     * @param version
     * The EGLContext client version to choose. Use 2 for OpenGL ES 2.0
     */
    override fun setEGLContextClientVersion(version: Int) {
        checkRenderThreadState()
        mEGLContextClientVersion = version
    }

    /**
     * Install a custom EGLContextFactory.
     *
     *
     * If this method is called, it must be called before [.setRenderer] is called.
     *
     *
     * If this method is not called, then by default a context will be created with no shared context and with a null attribute list.
     *
     * @param factory
     * Factory context
     */
    fun setEGLContextFactory(factory: EGLContextFactory?) {
        checkRenderThreadState()
        mEGLContextFactory = factory
    }

    /**
     * Install a custom EGLWindowSurfaceFactory.
     *
     *
     * If this method is called, it must be called before [.setRenderer] is called.
     *
     *
     * If this method is not called, then by default a window surface will be created with a null attribute list.
     *
     * @param factory
     * Factory context
     */
    fun setEGLWindowSurfaceFactory(factory: EGLWindowSurfaceFactory?) {
        checkRenderThreadState()
        mEGLWindowSurfaceFactory = factory
    }

    /**
     * Control whether the EGL context is preserved when the GLSurfaceViewEGL14 is paused and resumed.
     *
     *
     * If set to true, then the EGL context may be preserved when the GLSurfaceViewEGL14 is paused. Whether the EGL context is actually preserved or not depends upon whether the Android device that the program is running on can support an
     * arbitrary number of EGL contexts or not. Devices that can only support a limited number of EGL contexts must release the EGL context in order to allow multiple applications to share the GPU.
     *
     *
     * If set to false, the EGL context will be released when the GLSurfaceViewEGL14 is paused, and recreated when the GLSurfaceViewEGL14 is resumed.
     *
     *
     *
     * The default is false.
     *
     * @param preserveOnPause
     * preserve the EGL context when paused
     */
    override fun setPreserveEGLContextOnPause(preserveOnPause: Boolean) {
        mPreserveEGLContextOnPause = preserveOnPause
    }

    /**
     * Sets the surface to use the EGL_RECORDABLE_ANDROID flag
     *
     *
     * To take effect must be called before than setRenderer()
     *
     * @param recordable
     * True to set the recordable flag
     */
    fun setRecordable(recordable: Boolean) {
        mRecordable = recordable
        Logger.info("Updated recordable flag. State: " + java.lang.Boolean.toString(recordable))
    }

    /**
     * Set the renderer associated with this view. Also starts the thread that will call the renderer, which in turn causes the rendering to start.
     *
     *
     * This method should be called once and only once in the life-cycle of a GLSurfaceViewEGL14. The following GLSurfaceViewEGL14 methods can only be called *after* setRenderer is called:
     *
     *  * [.getRenderMode]
     *  * [.onPause]
     *  * [.onResume]
     *  * [.queueEvent]
     *  * [.requestRender]
     *  * [.setRenderMode]
     *
     *
     * @param renderer
     * the renderer to use to perform OpenGL drawing
     */
    override fun setRenderer(renderer: XenonGLRenderer) {
        checkRenderThreadState()
        if (mEGLContextFactory == null) {
            mEGLContextFactory = DefaultContextFactory()
        }
        if (mEGLWindowSurfaceFactory == null) {
            mEGLWindowSurfaceFactory = DefaultWindowSurfaceFactory()
        }

        //if (this.mEGLConfigChooser==null)
        run {}
        this.mRenderer = renderer
        mGLThread = GLThread(mThisWeakRef)
        mGLThread!!.start()
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceViewEGL14.
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        mGLThread!!.onWindowResize(w, h)
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceViewEGL14.
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        mGLThread!!.surfaceCreated()
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceViewEGL14.
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Surface will be destroyed when we return
        mGLThread!!.surfaceDestroyed()
    }

    companion object {
        /**
         * Check glError() after every GL call and throw an exception if glError indicates that an error has occurred. This can be used to help track down which OpenGL ES call is causing an error.
         *
         * see getDebugFlags, setDebugFlags
         */
        private const val DEBUG_CHECK_GL_ERROR = 1

        /**
         * Log GL calls to the system log at "verbose" level with tag "GLSurfaceViewEGL14".
         *
         * see #getDebugFlags, setDebugFlags
         */
        private const val DEBUG_LOG_GL_CALLS = 2
        private const val LOG_ATTACH_DETACH = false
        private const val LOG_EGL = false
        private const val LOG_PAUSE_RESUME = false
        private const val LOG_RENDERER = false
        private const val LOG_RENDERER_DRAW_FRAME = false
        private const val LOG_SURFACE = false
        private const val LOG_THREADS = false
        private const val LOG_THREADS_WAIT = false
        protected val sGLThreadManager = GLThreadManager()

        /**
         * Returns any cached error as a log
         *
         * @return Error log
         */
        val cachedErrorsLog: String
            get() {
                var log = "<NO ERRORS>"
                try {
                    if (EglHelper.cachedErrors != null && EglHelper.cachedErrors!!.size > 0) {
                        log = ""
                        for ((key, value) in EglHelper.cachedErrors!!) {
                            log = """$log
>>$key ($value)"""
                            if (log.length > 300) {
                                log = "$log<<<<<Log Too Large>>>>>"
                                break
                            }
                        }
                    }
                } catch (ex: Exception) {
                    Logger.error("Failed to cache error.", ex)
                    log = "Failed to cache error."
                }
                return log
            }

        /**
         * Gets a GL Error string
         *
         * @param error
         * Error to be resolve
         * @return Resolved error string
         */
        protected fun getErrorString(error: Int): String {
            Thread.dumpStack()
            return when (error) {
                EGL14.EGL_SUCCESS -> "EGL_SUCCESS"
                EGL14.EGL_NOT_INITIALIZED -> "EGL_NOT_INITIALIZED"
                EGL14.EGL_BAD_ACCESS -> "EGL_BAD_ACCESS"
                EGL14.EGL_BAD_ALLOC -> "EGL_BAD_ALLOC"
                EGL14.EGL_BAD_ATTRIBUTE -> "EGL_BAD_ATTRIBUTE"
                EGL14.EGL_BAD_CONFIG -> "EGL_BAD_CONFIG"
                EGL14.EGL_BAD_CONTEXT -> "EGL_BAD_CONTEXT"
                EGL14.EGL_BAD_CURRENT_SURFACE -> "EGL_BAD_CURRENT_SURFACE"
                EGL14.EGL_BAD_DISPLAY -> "EGL_BAD_DISPLAY"
                EGL14.EGL_BAD_MATCH -> "EGL_BAD_MATCH"
                EGL14.EGL_BAD_NATIVE_PIXMAP -> "EGL_BAD_NATIVE_PIXMAP"
                EGL14.EGL_BAD_NATIVE_WINDOW -> "EGL_BAD_NATIVE_WINDOW"
                EGL14.EGL_BAD_PARAMETER -> "EGL_BAD_PARAMETER"
                EGL14.EGL_BAD_SURFACE -> "EGL_BAD_SURFACE"
                EGL14.EGL_CONTEXT_LOST -> "EGL_CONTEXT_LOST"
                else -> "0x" + Integer.toHexString(error)
            }
        }
    }
}