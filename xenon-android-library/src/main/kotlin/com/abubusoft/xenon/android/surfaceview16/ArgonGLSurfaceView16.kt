/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abubusoft.xenon.android.surfaceview16

import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLExt
import android.opengl.GLDebugHelper
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.AsyncOperationManager
import com.abubusoft.xenon.opengl.XenonGLConfigChooser
import com.abubusoft.xenon.opengl.XenonGLRenderer
import com.abubusoft.xenon.settings.XenonSettings
import java.io.Writer
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10

/**
 * An implementation of SurfaceView that uses the dedicated surface for displaying OpenGL rendering.
 *
 *
 * A GLSurfaceView provides the following features:
 *
 *
 *
 *  * Manages a surface, which is a special piece of memory that can be composited into the Android view system.
 *  * Manages an EGL display, which enables OpenGL to render into a surface.
 *  * Accepts a user-provided Renderer object that does the actual rendering.
 *  * Renders on a dedicated thread to decouple rendering performance from the UI thread.
 *  * Supports both on-demand and continuous rendering.
 *  * Optionally wraps, traces, and/or error-checks the renderer's OpenGL calls.
 *
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 *
 *
 * For more information about how to use OpenGL, read the [OpenGL]({@docRoot}guide/topics/graphics/opengl.html) developer guide.
 *
</div> *
 *
 * <h3>Using GLSurfaceView</h3>
 *
 *
 * Typically you use GLSurfaceView by subclassing it and overriding one or more of the View system input event methods. If your application does not need to override event methods then GLSurfaceView can be used as-is. For the most part
 * GLSurfaceView behavior is customized by calling "set" methods rather than by subclassing. For example, unlike a regular View, drawing is delegated to a separate Renderer object which is registered with the GLSurfaceView using the
 * [.setRenderer] call.
 *
 *
 * <h3>Initializing GLSurfaceView</h3>
 * All you have to do to initialize a GLSurfaceView is call [.setRenderer]. However, if desired, you can modify the default behavior of GLSurfaceView by calling one or more of these methods before calling setRenderer:
 *
 *  * [.setDebugFlags]
 *  * [.setEGLConfigChooser]
 *  * [.setEGLConfigChooser]
 *  * [.setEGLConfigChooser]
 *  * [.setGLWrapper]
 *
 *
 *
 * <h4>Specifying the android.view.Surface</h4>
 * By default GLSurfaceView will create a PixelFormat.RGB_888 format surface. If a translucent surface is required, call getHolder().setFormat(PixelFormat.TRANSLUCENT). The exact format of a TRANSLUCENT surface is device dependent, but it
 * will be a 32-bit-per-pixel surface with 8 bits per component.
 *
 *
 * <h4>Choosing an EGL Configuration</h4>
 * A given Android device may support multiple EGLConfig rendering configurations. The available configurations may differ in how may channels of data are present, as well as how many bits are allocated to each channel. Therefore, the first
 * thing GLSurfaceView has to do when starting to render is choose what EGLConfig to use.
 *
 *
 * By default GLSurfaceView chooses a EGLConfig that has an RGB_888 pixel format, with at least a 16-bit depth buffer and no stencil.
 *
 *
 * If you would prefer a different EGLConfig you can override the default behavior by calling one of the setEGLConfigChooser methods.
 *
 *
 * <h4>Debug Behavior</h4>
 * You can optionally modify the behavior of GLSurfaceView by calling one or more of the debugging methods [.setDebugFlags], and [.setGLWrapper]. These methods may be called before and/or after setRenderer, but typically
 * they are called before setRenderer so that they take effect immediately.
 *
 *
 * <h4>Setting a Renderer</h4>
 * Finally, you must call [.setRenderer] to register a [Renderer]. The renderer is responsible for doing the actual OpenGL rendering.
 *
 *
 * <h3>Rendering Mode</h3>
 * Once the renderer is set, you can control whether the renderer draws continuously or on-demand by calling [.setRenderMode]. The default is continuous rendering.
 *
 *
 * <h3>Activity Life-cycle</h3>
 * A GLSurfaceView must be notified when the activity is paused and resumed. GLSurfaceView clients are required to call [.onPause] when the activity pauses and [.onResume] when the activity resumes. These calls allow
 * GLSurfaceView to pause and resume the rendering thread, and also allow GLSurfaceView to release and recreate the OpenGL display.
 *
 *
 * <h3>Handling events</h3>
 *
 *
 * To handle an event you will typically subclass GLSurfaceView and override the appropriate method, just as you would with any other View. However, when handling the event, you may need to communicate with the Renderer object that's
 * running in the rendering thread. You can do this using any standard Java cross-thread communication mechanism. In addition, one relatively easy way to communicate with your renderer is to call [.queueEvent]. For example:
 *
 * <pre class="prettyprint">
 * class MyGLSurfaceView extends GLSurfaceView {
 *
 * private MyRenderer mMyRenderer;
 *
 * public void start() {
 * mMyRenderer = ...;
 * setRenderer(mMyRenderer);
 * }
 *
 * public boolean onKeyDown(int keyCode, KeyEvent event) {
 * if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
 * queueEvent(new Runnable() {
 * // This method will be called on the rendering
 * // thread:
 * public void run() {
 * mMyRenderer.handleDpadCenter();
 * }
 * });
 * return true;
 * }
 * return super.onKeyDown(keyCode, event);
 * }
 * }
</pre> *
 *
 */
open class ArgonGLSurfaceView16 : ArgonGLView, SurfaceHolder.Callback {
    private abstract inner class BaseConfigChooser(configSpec: IntArray) : ArgonConfigChooser16 {
        protected var mConfigSpec: IntArray

        init {
            mConfigSpec = filterConfigSpec(configSpec)
        }

        override fun chooseConfig(egl: EGL10?, display: EGLDisplay?): EGLConfig? {
            val num_config = IntArray(1)

            //recupera il numero di configurazioni ammesse dal device
            require(egl!!.eglChooseConfig(display, mConfigSpec, null, 0, num_config)) { "eglChooseConfig failed" }
            val numConfigs = num_config[0]
            require(numConfigs > 0) { "No configs match configSpec" }
            val configs = arrayOfNulls<EGLConfig>(numConfigs)
            require(egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs, num_config)) { "eglChooseConfig#2 failed" }
            return chooseConfig(egl, display, configs) ?: throw IllegalArgumentException("No config chosen")
        }

        abstract fun chooseConfig(egl: EGL10?, display: EGLDisplay?, configs: Array<EGLConfig?>?): EGLConfig?
        private fun filterConfigSpec(configSpec: IntArray): IntArray {
            if (mEGLContextClientVersion != 2 && mEGLContextClientVersion != 3) {
                return configSpec
            }
            /*
			 * We know none of the subclasses define EGL_RENDERABLE_TYPE. And we know the configSpec is well formed.
			 */
            val len = configSpec.size
            val newConfigSpec = IntArray(len + 2)
            System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1)
            newConfigSpec[len - 1] = EGL10.EGL_RENDERABLE_TYPE
            if (mEGLContextClientVersion == 2) {
                newConfigSpec[len] = EGL14.EGL_OPENGL_ES2_BIT /* EGL_OPENGL_ES2_BIT */
            } else {
                newConfigSpec[len] = EGLExt.EGL_OPENGL_ES3_BIT_KHR /* EGL_OPENGL_ES3_BIT_KHR */
            }
            newConfigSpec[len + 1] = EGL10.EGL_NONE
            return newConfigSpec
        }
    }

    private inner class DefaultContextFactory : EGLContextFactory {
        var EGL_CONTEXT_CLIENT_VERSION = 0x3098
        override fun createContext(egl: EGL10?, display: EGLDisplay?, config: EGLConfig?): EGLContext {
            val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion, EGL10.EGL_NONE)
            val context = egl!!.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attrib_list)
            contextCounter.addAndGet(1)
            val settings = XenonBeanContext.getBean<XenonSettings>(XenonBeanType.XENON_SETTINGS)
            if (settings.openGL.asyncMode) {
                AsyncOperationManager.instance().init(egl, context, display, config)
            } else {
                AsyncOperationManager.instance().init()
            }
            Logger.warn("DefaultContextFactory > createContext %s , [contexts #%s, surfaces #%s]", context, contextCounter.get(), surfaceCounter.get())
            return context
        }

        override fun destroyContext(egl: EGL10?, display: EGLDisplay?, context: EGLContext) {
            val settings = XenonBeanContext.getBean<XenonSettings>(XenonBeanType.XENON_SETTINGS)

            // distrugge il context secondario, usato per le operazioni
            // async
            if (settings.openGL.asyncMode) {
                if (!AsyncOperationManager.instance().destroy(egl)) {
                    Logger.error("display: texture loader context: $context")
                    Logger.info("tid=" + Thread.currentThread().id)
                    Logger.error("eglDestroyContex %s", egl!!.eglGetError())
                }
            }
            if (!egl!!.eglDestroyContext(display, context)) {
                Logger.error("DefaultContextFactory, display:$display context: $context")
                if (LOG_THREADS) {
                    Logger.info("DefaultContextFactory tid=" + Thread.currentThread().id)
                }
                EglHelper.throwEglException("eglDestroyContex", egl.eglGetError())
            }
            contextCounter.addAndGet(-1)
            Logger.warn("DefaultContextFactory > destroyContext %s [contexts #%s, surfaces #%s]", context, contextCounter.get(), surfaceCounter.get())
        }
    }

    private inner class DefaultWindowSurfaceFactory : EGLWindowSurfaceFactory {
        override fun createWindowSurface(egl: EGL10, display: EGLDisplay?, config: EGLConfig?, nativeWindow: Any?): EGLSurface? {
            var surface: EGLSurface? = null
            try {
                surface = egl.eglCreateWindowSurface(display, config, nativeWindow, null)
                surfaceCounter.addAndGet(1)
                Logger.warn("DefaultWindowSurfaceFactory > eglCreateWindowSurface %s [contexts #%s, surfaces #%s]", surface, contextCounter.get(), surfaceCounter.get())
            } catch (e: IllegalArgumentException) {
                // This exception indicates that the surface flinger surface
                // is not valid. This can happen if the surface flinger surface has
                // been torn down, but the application has not yet been
                // notified via SurfaceHolder.Callback.surfaceDestroyed.
                // In theory the application should be notified first,
                // but in practice sometimes it is not. See b/4588890
                Logger.error("eglCreateWindowSurface %s", e)
            }
            return surface
        }

        override fun destroySurface(egl: EGL10?, display: EGLDisplay?, surface: EGLSurface?) {
            egl!!.eglDestroySurface(display, surface)
            surfaceCounter.addAndGet(-1)
            Logger.warn("DefaultWindowSurfaceFactory > eglDestroySurface %s [contexts #%s, surfaces #%s]", surface, contextCounter.get(), surfaceCounter.get())
        }
    }

    /**
     * An interface for customizing the eglCreateContext and eglDestroyContext calls.
     *
     *
     * This interface must be implemented by clients wishing to call [ArgonGLSurfaceView16.setEGLContextFactory]
     */
    interface EGLContextFactory {
        fun createContext(egl: EGL10?, display: EGLDisplay?, eglConfig: EGLConfig?): EGLContext
        fun destroyContext(egl: EGL10?, display: EGLDisplay?, context: EGLContext)
    }

    /**
     * An EGL helper class.
     */
    private class EglHelper(private val mGLSurfaceViewWeakRef: WeakReference<ArgonGLSurfaceView16>) {
        var mEgl: EGL10? = null
        var mEglConfig: EGLConfig? = null
        var mEglContext: EGLContext? = null
        var mEglDisplay: EGLDisplay? = null
        var mEglSurface: EGLSurface? = null

        /**
         * Create a GL object for the current EGL context.
         *
         * @return
         */
        fun createGL(): GL? {
            var gl = mEglContext!!.gl
            val view = mGLSurfaceViewWeakRef.get()
            if (view != null) {
                if (view.mGLWrapper != null) {
                    gl = view.mGLWrapper!!.wrap(gl)
                }
                if (view.mDebugFlags and (DEBUG_CHECK_GL_ERROR or DEBUG_LOG_GL_CALLS) != 0) {
                    var configFlags = 0
                    var log: Writer? = null
                    if (view.mDebugFlags and DEBUG_CHECK_GL_ERROR != 0) {
                        configFlags = configFlags or GLDebugHelper.CONFIG_CHECK_GL_ERROR
                    }
                    if (view.mDebugFlags and DEBUG_LOG_GL_CALLS != 0) {
                        log = LogWriter()
                    }
                    gl = GLDebugHelper.wrap(gl, configFlags, log)
                }
            }
            return gl
        }

        /**
         * Create an egl surface for the current SurfaceHolder surface. If a surface already exists, destroy it before creating the new surface.
         *
         * @return true if the surface was created successfully.
         */
        fun createSurface(): Boolean {
            if (LOG_EGL) {
                Logger.warn("EglHelper, createSurface()  tid=" + Thread.currentThread().id)
            }
            /*
			 * Check preconditions.
			 */if (mEgl == null) {
                throw RuntimeException("egl not initialized")
            }
            if (mEglDisplay == null) {
                throw RuntimeException("eglDisplay not initialized")
            }
            if (mEglConfig == null) {
                throw RuntimeException("mEglConfig not initialized")
            }

            /*
			 * The window size has changed, so we need to create a new surface.
			 */destroySurfaceImp()

            /*
			 * Create an EGL surface we can render into.
			 */
            val view = mGLSurfaceViewWeakRef.get()
            mEglSurface = if (view != null) {
                view.mEGLWindowSurfaceFactory!!.createWindowSurface(mEgl!!, mEglDisplay, mEglConfig, view.holder)
            } else {
                null
            }
            if (mEglSurface == null || mEglSurface === EGL10.EGL_NO_SURFACE) {
                val error = mEgl!!.eglGetError()
                if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                    Logger.error("EglHelper, createWindowSurface returned EGL_BAD_NATIVE_WINDOW.")
                }
                return false
            }

            /*
			 * Before we can issue GL commands, we need to make sure the context is current and bound to a surface.
			 */if (!mEgl!!.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
                /*
				 * Could not make the context current, probably because the underlying SurfaceView surface has been destroyed.
				 */
                logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", mEgl!!.eglGetError())
                return false
            }
            Logger.info("EglHelper, createWindowSurface eglMakeCurrent.")
            return true
        }

        fun destroySurface() {
            if (LOG_EGL) {
                Logger.warn("EglHelper, destroySurface()  tid=" + Thread.currentThread().id)
            }
            destroySurfaceImp()
        }

        private fun destroySurfaceImp() {
            if (mEglSurface != null && mEglSurface !== EGL10.EGL_NO_SURFACE) {
                mEgl!!.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
                val view = mGLSurfaceViewWeakRef.get()
                if (view != null) {
                    view.mEGLWindowSurfaceFactory!!.destroySurface(mEgl, mEglDisplay, mEglSurface)
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
                mEgl!!.eglTerminate(mEglDisplay)
                mEglDisplay = null
            }
        }

        fun finish() {
            if (LOG_EGL) {
                Logger.warn("EglHelper, finish() tid=" + Thread.currentThread().id)
            }
            if (mEglContext != null) {
                val view = mGLSurfaceViewWeakRef.get()
                if (view != null) {
                    view.mEGLContextFactory!!.destroyContext(mEgl, mEglDisplay, mEglContext!!)
                }
                mEglContext = null
            }
            if (mEglDisplay != null) {
                // xcesco: non rimuoviamo il display
                //mEgl.eglTerminate(mEglDisplay);
                //mEglDisplay = null;
            }
        }

        /**
         * Initialize EGL for a given configuration spec.
         *
         * @param configSpec
         */
        fun start() {
            if (LOG_EGL) {
                Logger.warn("EglHelper, start() tid=%s", Thread.currentThread().id)
            }
            /*
			 * Get an EGL instance
			 */
            // V1
            mEgl = EGLContext.getEGL() as EGL10

            /*
			 * xcesco: Get to the default display. 
			 */if (mEglDisplay == null) {
                mEglDisplay = mEgl!!.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
                Logger.info("mEglDisplay INITIALIZATED %s", mEglDisplay)
                if (mEglDisplay === EGL10.EGL_NO_DISPLAY) {
                    throw RuntimeException("eglGetDisplay failed")
                }

                /*
				 * We can now initialize EGL for that display
				 */
                val version = IntArray(2)
                if (!mEgl!!.eglInitialize(mEglDisplay, version)) {
                    Logger.warn("eglInitialize failed - %s %s", version[0], version[1])
                }
            } else {
                Logger.info("mEglDisplay RESUED %s", mEglDisplay)
            }
            val view = mGLSurfaceViewWeakRef.get()
            if (view == null) {
                mEglConfig = null
                mEglContext = null
            } else {
                mEglConfig = view.mEGLConfigChooser!!.chooseConfig(mEgl, mEglDisplay)

                /*
				 * Create an EGL context. We want to do this as rarely as we can, because an EGL context is a somewhat heavy object.
				 */mEglContext = view.mEGLContextFactory!!.createContext(mEgl, mEglDisplay, mEglConfig)
            }
            if (mEglContext == null || mEglContext === EGL10.EGL_NO_CONTEXT) {
                mEglContext = null
                throwEglException("createContext")
            }
            if (LOG_EGL) {
                Logger.warn("EglHelper, createContext " + mEglContext + " tid=" + Thread.currentThread().id)
            }
            mEglSurface = null
        }

        /**
         * Display the current render surface.
         *
         * @return the EGL error code from eglSwapBuffers.
         */
        fun swap(): Int {
            return if (!mEgl!!.eglSwapBuffers(mEglDisplay, mEglSurface)) {
                mEgl!!.eglGetError()
            } else EGL10.EGL_SUCCESS
        }

        private fun throwEglException(function: String) {
            throwEglException(function, mEgl!!.eglGetError())
        }

        companion object {
            fun formatEglError(function: String, error: Int): String {
                return "$function failed: $error" // + EGLLogWrapper.getErrorString(error);
            }

            fun logEglErrorAsWarning(tag: String?, function: String, error: Int) {
                Logger.warn(formatEglError(function, error))
            }

            fun throwEglException(function: String, error: Int) {
                val message = formatEglError(function, error)
                if (LOG_THREADS) {
                    Logger.error("EglHelper, throwEglException tid=" + Thread.currentThread().id + " " + message)
                }
                throw RuntimeException(message)
            }
        }
    }

    /**
     * An interface for customizing the eglCreateWindowSurface and eglDestroySurface calls.
     *
     *
     * This interface must be implemented by clients wishing to call [ArgonGLSurfaceView16.setEGLWindowSurfaceFactory]
     */
    interface EGLWindowSurfaceFactory {
        /**
         * @return null if the surface cannot be constructed.
         */
        fun createWindowSurface(egl: EGL10, display: EGLDisplay?, config: EGLConfig?, nativeWindow: Any?): EGLSurface?
        fun destroySurface(egl: EGL10?, display: EGLDisplay?, surface: EGLSurface?)
    }

    /**
     * A generic GL Thread. Takes care of initializing EGL and GL. Delegates to a Renderer instance to do the actual drawing. Can be configured to render continuously or on request.
     *
     * All potentially blocking synchronization is done through the sGLThreadManager object. This avoids multiple-lock ordering issues.
     *
     */
    internal inner class GLThread(glSurfaceViewWeakRef: WeakReference<ArgonGLSurfaceView16>) : Thread() {
        private var mEglHelper: EglHelper? = null
        private val mEventQueue = ArrayList<Runnable>()
        val mExited = false
        private var mFinishedCreatingEglSurface = false

        /**
         * Set once at thread construction time, nulled out when the parent view is garbage called. This weak reference allows the GLSurfaceView to be garbage collected while the GLThread is still alive.
         */
        private val mGLSurfaceViewWeakRef: WeakReference<ArgonGLSurfaceView16>
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
            mRenderMode = RENDERMODE_CONTINUOUSLY
            mGLSurfaceViewWeakRef = glSurfaceViewWeakRef
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
                require(RENDERMODE_WHEN_DIRTY <= renderMode && renderMode <= RENDERMODE_CONTINUOUSLY) { "renderMode" }
                synchronized(sGLThreadManager) {
                    mRenderMode = renderMode
                    sGLThreadManager.notifyAll()
                }
            }

        @Throws(InterruptedException::class)
        private fun guardedRun() {
            mEglHelper = EglHelper(mGLSurfaceViewWeakRef)
            mHaveEglContext = false
            mHaveEglSurface = false
            try {
                var gl: GL10? = null
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
                            if (mShouldExit) {
                                return
                            }
                            if (!mEventQueue.isEmpty()) {
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
                                    Logger.info("GLThread, mPaused is now $mPaused tid=$id")
                                }
                            }

                            // Do we need to give up the EGL context?
                            if (mShouldReleaseEglContext) {
                                if (LOG_SURFACE) {
                                    Logger.info("GLThread, releasing EGL context because asked to tid=$id")
                                }
                                stopEglSurfaceLocked()
                                stopEglContextLocked()
                                mShouldReleaseEglContext = false
                                askedToReleaseEglContext = true
                            }

                            // Have we lost the EGL context?
                            if (lostEglContext) {
                                stopEglSurfaceLocked()
                                stopEglContextLocked()
                                lostEglContext = false
                            }

                            // When pausing, release the EGL surface:
                            if (pausing && mHaveEglSurface) {
                                if (LOG_SURFACE) {
                                    Logger.info("GLThread, releasing EGL surface because paused tid=$id")
                                }
                                stopEglSurfaceLocked()
                            }

                            // When pausing, optionally release the EGL Context:
                            if (pausing && mHaveEglContext) {
                                val view = mGLSurfaceViewWeakRef.get()
                                val preserveEglContextOnPause = view?.mPreserveEGLContextOnPause ?: false
                                if (!preserveEglContextOnPause || sGLThreadManager.shouldReleaseEGLContextWhenPausing()) {
                                    stopEglContextLocked()
                                    if (LOG_SURFACE) {
                                        Logger.info("GLThread, releasing EGL context because paused tid=$id")
                                    }
                                }
                            }

                            // When pausing, optionally terminate EGL:
                            if (pausing) {
                                if (sGLThreadManager.shouldTerminateEGLWhenPausing()) {
                                    mEglHelper!!.finish()
                                    if (LOG_SURFACE) {
                                        Logger.info("GLThread, terminating EGL because paused tid=$id")
                                    }
                                }
                            }

                            // Have we lost the SurfaceView surface?
                            if (!mHasSurface && !mWaitingForSurface) {
                                if (LOG_SURFACE) {
                                    Logger.info("GLThread, noticed surfaceView surface lost tid=$id")
                                }
                                if (mHaveEglSurface) {
                                    stopEglSurfaceLocked()
                                }
                                mWaitingForSurface = true
                                mSurfaceIsBad = false
                                sGLThreadManager.notifyAll()
                            }

                            // Have we acquired the surface view surface?
                            if (mHasSurface && mWaitingForSurface) {
                                if (LOG_SURFACE) {
                                    Logger.info("GLThread, noticed surfaceView surface acquired tid=$id")
                                }
                                mWaitingForSurface = false
                                sGLThreadManager.notifyAll()
                            }
                            if (doRenderNotification) {
                                if (LOG_SURFACE) {
                                    Logger.info("GLThread, sending render notification tid=$id")
                                }
                                wantRenderNotification = false
                                doRenderNotification = false
                                mRenderComplete = true
                                sGLThreadManager.notifyAll()
                            }

                            // Ready to draw?
                            if (readyToDraw()) {

                                // If we don't have an EGL context, try to acquire one.
                                if (!mHaveEglContext) {
                                    if (askedToReleaseEglContext) {
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
                                if (mHaveEglContext && !mHaveEglSurface) {
                                    mHaveEglSurface = true
                                    createEglSurface = true
                                    createGlInterface = true
                                    sizeChanged = true
                                }
                                if (mHaveEglSurface) {
                                    if (mSizeChanged) {
                                        sizeChanged = true
                                        w = mWidth
                                        h = mHeight
                                        wantRenderNotification = true
                                        if (LOG_SURFACE) {
                                            Logger.info("GLThread, noticing that we want render notification tid=$id")
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
                            if (LOG_THREADS) {
                                Logger.info(
                                    "GLThread, waiting tid=" + id + " mHaveEglContext: " + mHaveEglContext + " mHaveEglSurface: " + mHaveEglSurface + " mFinishedCreatingEglSurface: " + mFinishedCreatingEglSurface
                                            + " mPaused: " + mPaused + " mHasSurface: " + mHasSurface + " mSurfaceIsBad: " + mSurfaceIsBad + " mWaitingForSurface: " + mWaitingForSurface + " mWidth: " + mWidth + " mHeight: " + mHeight
                                            + " mRequestRender: " + mRequestRender + " mRenderMode: " + mRenderMode
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
                    if (createEglSurface) {
                        if (LOG_SURFACE) {
                            Logger.warn("GLThread, egl createSurface")
                        }
                        if (mEglHelper!!.createSurface()) {
                            synchronized(sGLThreadManager) {
                                mFinishedCreatingEglSurface = true
                                sGLThreadManager.notifyAll()
                            }
                        } else {
                            synchronized(sGLThreadManager) {
                                mFinishedCreatingEglSurface = true
                                mSurfaceIsBad = true
                                sGLThreadManager.notifyAll()
                            }
                            continue
                        }
                        createEglSurface = false
                    }
                    if (createGlInterface) {
                        gl = mEglHelper!!.createGL() as GL10?
                        sGLThreadManager.checkGLDriver(gl)
                        createGlInterface = false
                    }
                    if (createEglContext) {
                        if (LOG_RENDERER) {
                            Logger.warn("GLThread, onSurfaceCreated")
                        }
                        val view = mGLSurfaceViewWeakRef.get()
                        if (view != null) {
                            try {
                                // Logger.info("onSurfaceCreated");
                                view.mRenderer.onSurfaceCreated()
                            } finally {
                                // Trace.traceEnd(Trace.TRACE_TAG_VIEW);
                            }
                        }
                        createEglContext = false
                    }
                    if (sizeChanged) {
                        if (LOG_RENDERER) {
                            Logger.warn("GLThread, onSurfaceChanged($w, $h)")
                        }
                        val view = mGLSurfaceViewWeakRef.get()
                        if (view != null) {
                            try {
                                // Trace.traceBegin(Trace.TRACE_TAG_VIEW, "onSurfaceChanged");
                                view.mRenderer.onSurfaceChanged(w, h)
                            } finally {
                                // Trace.traceEnd(Trace.TRACE_TAG_VIEW);
                            }
                        }
                        sizeChanged = false
                    }
                    if (LOG_RENDERER_DRAW_FRAME) {
                        Logger.warn("GLThread, onDrawFrame tid=$id")
                    }
                    run {
                        val view = mGLSurfaceViewWeakRef.get()
                        if (view != null) {
                            try {
                                // Trace.traceBegin(Trace.TRACE_TAG_VIEW, "onDrawFrame");
                                view.mRenderer.onDrawFrame()
                            } finally {
                                // Trace.traceEnd(Trace.TRACE_TAG_VIEW);
                            }
                        }
                    }
                    val swapError = mEglHelper!!.swap()
                    when (swapError) {
                        EGL10.EGL_SUCCESS -> {}
                        EGL11.EGL_CONTEXT_LOST -> {
                            if (LOG_SURFACE) {
                                Logger.info("GLThread, egl context lost tid=$id")
                            }
                            lostEglContext = true
                        }
                        else -> {
                            // Other errors typically mean that the current surface is bad,
                            // probably because the SurfaceView surface has been destroyed,
                            // but we haven't been notified yet.
                            // Log the error to help developers understand why rendering stopped.
                            EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError)
                            synchronized(sGLThreadManager) {
                                mSurfaceIsBad = true
                                sGLThreadManager.notifyAll()
                            }
                        }
                    }
                    if (wantRenderNotification) {
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
                    Logger.info("GLThread, onPause tid=$id")
                }
                mRequestPaused = true
                sGLThreadManager.notifyAll()
                while (!mExited && !mPaused) {
                    if (LOG_PAUSE_RESUME) {
                        Logger.info("Main Thread, onPause waiting for mPaused.")
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
                    Logger.info("GLThread, onResume tid=$id")
                }
                mRequestPaused = false
                mRequestRender = true
                mRenderComplete = false
                sGLThreadManager.notifyAll()
                while (!mExited && mPaused && !mRenderComplete) {
                    if (LOG_PAUSE_RESUME) {
                        Logger.info("Main Thread, onResume waiting for !mPaused.")
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
                while (!mExited && !mPaused && !mRenderComplete && ableToDraw()) {
                    if (LOG_SURFACE) {
                        Logger.info("Main Thread, onWindowResize waiting for render complete from tid=$id")
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
         * @param r
         * the runnable to be run on the GL rendering thread.
         */
        fun queueEvent(r: Runnable?) {
            requireNotNull(r) { "r must not be null" }
            synchronized(sGLThreadManager) {
                mEventQueue.add(r)
                sGLThreadManager.notifyAll()
            }
        }

        private fun readyToDraw(): Boolean {
            return !mPaused && mHasSurface && !mSurfaceIsBad && mWidth > 0 && mHeight > 0 && (mRequestRender || mRenderMode) == RENDERMODE_CONTINUOUSLY
        }

        fun requestExitAndWait() {
            // don't call this from GLThread thread or it is a guaranteed
            // deadlock!
            synchronized(sGLThreadManager) {
                mShouldExit = true
                sGLThreadManager.notifyAll()
                while (!mExited) {
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
                mRequestRender = true
                sGLThreadManager.notifyAll()
            }
        }

        override fun run() {
            name = "GLThread $id"
            if (LOG_THREADS) {
                Logger.warn("GLThread starting tid=$id")
            }
            try {
                guardedRun()
            } catch (e: InterruptedException) {
                // fall thru and exit normally
                Logger.fatal(e.message)
                e.printStackTrace()
            } finally {
                sGLThreadManager.threadExiting(this)
            }
        }

        /*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
        private fun stopEglContextLocked() {
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
                    Logger.info("GLThread, surfaceCreated tid=$id")
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
                    Logger.info("GLThread, surfaceDestroyed tid=$id")
                }
                mHasSurface = false
                sGLThreadManager.notifyAll()
                while (!mWaitingForSurface && !mExited) {
                    try {
                        sGLThreadManager.wait()
                    } catch (e: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            }
        }
    }

    private class GLThreadManager {
        private var mEglOwner: GLThread? = null
        private var mGLESDriverCheckComplete = false
        private var mGLESVersion = 0

        /**
         * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides support for hardware-accelerated views, therefore multiple EGL contexts are supported on all Android 3.0+ EGL drivers.
         */
        private var mGLESVersionCheckComplete = false
        private var mLimitedGLESContexts = false
        private var mMultipleGLESContextsAllowed = false
        @Synchronized
        fun checkGLDriver(gl: GL10?) {
            if (!mGLESDriverCheckComplete) {
                checkGLESVersion()
                val renderer = gl!!.glGetString(GL10.GL_RENDERER)
                if (mGLESVersion < kGLES_20) {
                    mMultipleGLESContextsAllowed = !renderer.startsWith(kMSM7K_RENDERER_PREFIX)
                    notifyAll()
                }
                mLimitedGLESContexts = !mMultipleGLESContextsAllowed
                if (LOG_SURFACE) {
                    Logger.warn("checkGLDriver renderer = \"$renderer\" multipleContextsAllowed = $mMultipleGLESContextsAllowed mLimitedGLESContexts = $mLimitedGLESContexts")
                }
                mGLESDriverCheckComplete = true
            }
        }

        private fun checkGLESVersion() {
            if (!mGLESVersionCheckComplete) {
                /*
				 * mGLESVersion = SystemProperties.getInt( "ro.opengles.version", ConfigurationInfo.GL_ES_VERSION_UNDEFINED); if (mGLESVersion >= kGLES_20) { mMultipleGLESContextsAllowed = true; }
				 */
                mGLESVersion = kGLES_20
                mMultipleGLESContextsAllowed = true
                if (LOG_SURFACE) {
                    Logger.warn("checkGLESVersion mGLESVersion = $mGLESVersion mMultipleGLESContextsAllowed = $mMultipleGLESContextsAllowed")
                }
                mGLESVersionCheckComplete = true
            }
        }

        /*
		 * Releases the EGL context. Requires that we are already in the sGLThreadManager monitor when this is called.
		 */
        fun releaseEglContextLocked(thread: GLThread) {
            if (mEglOwner === thread) {
                mEglOwner = null
            }
            notifyAll()
        }

        @Synchronized
        fun shouldReleaseEGLContextWhenPausing(): Boolean {
            // Release the EGL context when pausing even if
            // the hardware supports multiple EGL contexts.
            // Otherwise the device could run out of EGL contexts.
            return mLimitedGLESContexts
        }

        @Synchronized
        fun shouldTerminateEGLWhenPausing(): Boolean {
            checkGLESVersion()
            return !mMultipleGLESContextsAllowed
        }

        @Synchronized
        fun threadExiting(thread: GLThread) {
            if (LOG_THREADS) {
                Logger.info("GLThread exiting tid=" + thread.id)
            }
            thread.mExited = true
            if (mEglOwner === thread) {
                mEglOwner = null
            }
            notifyAll()
        }

        /*
		 * Tries once to acquire the right to use an EGL context. Does not block. Requires that we are already in the sGLThreadManager monitor when this is called.
		 * 
		 * @return true if the right to use an EGL context was acquired.
		 */
        fun tryAcquireEglContextLocked(thread: GLThread): Boolean {
            if (mEglOwner === thread || mEglOwner == null) {
                mEglOwner = thread
                notifyAll()
                return true
            }
            checkGLESVersion()
            if (mMultipleGLESContextsAllowed) {
                return true
            }
            // Notify the owning thread that it should release the context.
            // TODO: implement a fairness policy. Currently
            // if the owning thread is drawing continuously it will just
            // reacquire the EGL context.
            if (mEglOwner != null) {
                mEglOwner!!.requestReleaseEglContextLocked()
            }
            return false
        }

        companion object {
            // private static String TAG = "GLThreadManager";
            private const val kGLES_20 = 0x20000
            private const val kMSM7K_RENDERER_PREFIX = "Q3Dimension MSM7500 "
        }
    }

    /**
     * An interface used to wrap a GL interface.
     *
     *
     * Typically used for implementing debugging and tracing on top of the default GL interface. You would typically use this by creating your own class that implemented all the GL methods by delegating to another GL instance. Then you
     * could add your own behavior before or after calling the delegate. All the GLWrapper would do was instantiate and return the wrapper GL instance:
     *
     * <pre class="prettyprint">
     * class MyGLWrapper implements GLWrapper {
     * GL wrap(GL gl) {
     * return new MyGLImplementation(gl);
     * }
     * static class MyGLImplementation implements GL,GL10,GL11,... {
     * ...
     * }
     * }
    </pre> *
     *
     * @see .setGLWrapper
     */
    interface GLWrapper {
        /**
         * Wraps a gl interface in another gl interface.
         *
         * @param gl
         * a GL interface that is to be wrapped.
         * @return either the input argument or another GL object that wraps the input argument.
         */
        fun wrap(gl: GL?): GL?
    }

    internal class LogWriter : Writer() {
        private val mBuilder = StringBuilder()
        override fun close() {
            flushBuilder()
        }

        override fun flush() {
            flushBuilder()
        }

        private fun flushBuilder() {
            if (mBuilder.length > 0) {
                Logger.verbose("Main Thread,GLSurfaceView $mBuilder")
                mBuilder.delete(0, mBuilder.length)
            }
        }

        override fun write(buf: CharArray, offset: Int, count: Int) {
            for (i in 0 until count) {
                val c = buf[offset + i]
                if (c == '\n') {
                    flushBuilder()
                } else {
                    mBuilder.append(c)
                }
            }
        }
    }

    // private final GLThreadManager sGLThreadManager = new GLThreadManager();
    var contextCounter = AtomicInteger()

    /**
     * xcesco: per un uso normale, all'uscita del thread che si occupa del contesto GL dovremmo
     * distruggere il display. Mettendo a false questa variabile il display viene preservato e di
     * conseguenza il display viene mantenuto. Questo evita sul MIPAD (tegra K1) di avere un BAD_DISPLAY
     * la seconda volta (ma solo la seconda) che uso la view, ad esempio ruotando il display mentre
     * si è in modalità preview.
     */
    protected var destroyDisplayOnExit = true
    private var mDebugFlags = 0
    private var mDetached = false
    private var mEGLConfigChooser: ArgonConfigChooser16? = null
    private var mEGLContextClientVersion = 0
    private var mEGLContextFactory: EGLContextFactory? = null
    private var mEGLWindowSurfaceFactory: EGLWindowSurfaceFactory? = null
    private var mGLThread: GLThread? = null
    private var mGLWrapper: GLWrapper? = null
    private var mPreserveEGLContextOnPause = false
    private val mThisWeakRef = WeakReference(this)

    // ----------------------------------------------------------------------
    var surfaceCounter = AtomicInteger()

    /**
     * Standard View constructor. In order to render something, you must call [.setRenderer] to register a renderer.
     */
    constructor(context: Context) : super(context) {
        init()
    }

    /**
     * Standard View constructor. In order to render something, you must call [.setRenderer] to register a renderer.
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun checkRenderThreadState() {
        check(mGLThread == null) { "setRenderer has already been called for this instance." }
    }

    @Throws(Throwable::class)
    protected open fun finalize() {
        try {
            if (mGLThread != null) {
                // GLThread may still be running if this view was never
                // attached to a window.
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

    /**
     * @return true if the EGL context will be preserved when paused
     */
    fun getPreserveEGLContextOnPause(): Boolean {
        return mPreserveEGLContextOnPause
    }
    /**
     * Get the current rendering mode. May be called from any thread. Must not be called before a renderer has been set.
     *
     * @return the current rendering mode.
     * @see .RENDERMODE_CONTINUOUSLY
     *
     * @see .RENDERMODE_WHEN_DIRTY
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
     * one of the RENDERMODE_X constants
     * @see .RENDERMODE_CONTINUOUSLY
     *
     * @see .RENDERMODE_WHEN_DIRTY
     */
    var renderMode: Int
        get() = mGLThread!!.renderMode
        set(renderMode) {
            mGLThread!!.renderMode = renderMode
        }

    private fun init() {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed
        val holder = holder
        holder.addCallback(this)
        // setFormat is done by SurfaceView in SDK 2.3 and newer. Uncomment
        // this statement if back-porting to 2.2 or older:
        //holder.setFormat(PixelFormat.RGB_565);
        //
        // setType is not needed for SDK 2.0 or newer. Uncomment this
        // statement if back-porting this code to older SDKs.
        //holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
    }

    /**
     * This method is used as part of the View class and is not normally called or subclassed by clients of GLSurfaceView.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (LOG_ATTACH_DETACH) {
            Logger.debug("onAttachedToWindow reattach =$mDetached")
        }
        if (mDetached && mRenderer != null) {
            var renderMode = RENDERMODE_CONTINUOUSLY
            if (mGLThread != null) {
                renderMode = mGLThread!!.renderMode
            }
            mGLThread = GLThread(mThisWeakRef)
            if (renderMode != RENDERMODE_CONTINUOUSLY) {
                mGLThread!!.renderMode = renderMode
            }
            mGLThread!!.start()
        }
        mDetached = false
    }

    override fun onDetachedFromWindow() {
        if (LOG_ATTACH_DETACH) {
            Logger.debug("onDetachedFromWindow")
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
        mGLThread!!.onPause()
        mRenderer.onPause()
    }

    /**
     * Inform the view that the activity is resumed. The owner of this view must call this method when the activity is resumed. Calling this method will recreate the OpenGL display and resume the rendering thread. Must not be called before
     * a renderer has been set.
     */
    override fun onResume() {
        mGLThread!!.onResume()
        mRenderer.onResume()
    }

    /**
     * Queue a runnable to be run on the GL rendering thread. This can be used to communicate with the Renderer on the rendering thread. Must not be called before a renderer has been set.
     *
     * @param r
     * the runnable to be run on the GL rendering thread.
     */
    fun queueEvent(r: Runnable?) {
        mGLThread!!.queueEvent(r)
    }

    /**
     * Request that the renderer render a frame. This method is typically used when the render mode has been set to [.RENDERMODE_WHEN_DIRTY], so that frames are only rendered on demand. May be called from any thread. Must not be
     * called before a renderer has been set.
     */
    fun requestRender() {
        mGLThread!!.requestRender()
    }

    /**
     * Set the debug flags to a new value. The value is constructed by OR-together zero or more of the DEBUG_CHECK_* constants. The debug flags take effect whenever a surface is created. The default value is zero.
     *
     * @param debugFlags
     * the new debug flags
     * @see .DEBUG_CHECK_GL_ERROR
     *
     * @see .DEBUG_LOG_GL_CALLS
     */
    override fun setDebugFlags(debugFlags: Int) {
        mDebugFlags = debugFlags
    }

    /**
     * Install a custom EGLConfigChooser.
     *
     *
     * If this method is called, it must be called before [.setRenderer] is called.
     *
     *
     * If no setEGLConfigChooser method is called, then by default the view will choose an EGLConfig that is compatible with the current android.view.Surface, with a depth buffer depth of at least 16 bits.
     *
     * @param configChooser
     */
    fun setEGLConfigChooser(configChooser: ArgonConfigChooser16?) {
        checkRenderThreadState()
        mEGLConfigChooser = configChooser
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
     * This method only affects the behavior of the default EGLContexFactory and the default EGLConfigChooser. If [.setEGLContextFactory] has been called, then the supplied EGLContextFactory is responsible for
     * creating an OpenGL ES 2.0-compatible context. If [.setEGLConfigChooser] has been called, then the supplied EGLConfigChooser is responsible for choosing an OpenGL ES 2.0-compatible config.
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
     */
    fun setEGLWindowSurfaceFactory(factory: EGLWindowSurfaceFactory?) {
        checkRenderThreadState()
        mEGLWindowSurfaceFactory = factory
    }

    /**
     * Set the glWrapper. If the glWrapper is not null, its [GLWrapper.wrap] method is called whenever a surface is created. A GLWrapper can be used to wrap the GL object that's passed to the renderer. Wrapping a GL object enables
     * examining and modifying the behavior of the GL calls made by the renderer.
     *
     *
     * Wrapping is typically used for debugging purposes.
     *
     *
     * The default value is null.
     *
     * @param glWrapper
     * the new GLWrapper
     */
    fun setGLWrapper(glWrapper: GLWrapper?) {
        mGLWrapper = glWrapper
    }

    /**
     * Control whether the EGL context is preserved when the GLSurfaceView is paused and resumed.
     *
     *
     * If set to true, then the EGL context may be preserved when the GLSurfaceView is paused. Whether the EGL context is actually preserved or not depends upon whether the Android device that the program is running on can support an
     * arbitrary number of EGL contexts or not. Devices that can only support a limited number of EGL contexts must release the EGL context in order to allow multiple applications to share the GPU.
     *
     *
     * If set to false, the EGL context will be released when the GLSurfaceView is paused, and recreated when the GLSurfaceView is resumed.
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
     * Set the renderer associated with this view. Also starts the thread that will call the renderer, which in turn causes the rendering to start.
     *
     *
     * This method should be called once and only once in the life-cycle of a GLSurfaceView.
     *
     *
     * The following GLSurfaceView methods can only be called *before* setRenderer is called:
     *
     *  * [.setEGLConfigChooser]
     *  * [.setEGLConfigChooser]
     *  * [.setEGLConfigChooser]
     *
     *
     *
     * The following GLSurfaceView methods can only be called *after* setRenderer is called:
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
     * the renderer to use to perform OpenGL drawing.
     */
    override fun setRenderer(renderer: XenonGLRenderer) {
        checkRenderThreadState()
        if (mEGLConfigChooser == null) {
            mEGLConfigChooser = XenonGLConfigChooser.build()
            //			mEGLConfigChooser = new SimpleEGLConfigChooser(false);
        }
        if (mEGLContextFactory == null) {
            mEGLContextFactory = DefaultContextFactory()
        }
        if (mEGLWindowSurfaceFactory == null) {
            mEGLWindowSurfaceFactory = DefaultWindowSurfaceFactory()
        }
        mRenderer = renderer
        mGLThread = GLThread(mThisWeakRef)
        mGLThread!!.start()
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceView.
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        mGLThread!!.onWindowResize(w, h)
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceView.
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        mGLThread!!.surfaceCreated()
    }

    /**
     * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceView.
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Surface will be destroyed when we return
        mGLThread!!.surfaceDestroyed()
    }

    companion object {
        /**
         * Check glError() after every GL call and throw an exception if glError indicates that an error has occurred. This can be used to help track down which OpenGL ES call is causing an error.
         *
         * @see .getDebugFlags
         *
         * @see .setDebugFlags
         */
        const val DEBUG_CHECK_GL_ERROR = 1

        /**
         * Log GL calls to the system log at "verbose" level with tag "GLSurfaceView".
         *
         * @see .getDebugFlags
         *
         * @see .setDebugFlags
         */
        const val DEBUG_LOG_GL_CALLS = 2
        private const val LOG_ATTACH_DETACH = true
        private const val LOG_EGL = true
        private const val LOG_PAUSE_RESUME = true
        private const val LOG_RENDERER = true
        private const val LOG_RENDERER_DRAW_FRAME = false
        private const val LOG_SURFACE = true
        private const val LOG_THREADS = true

        /**
         * The renderer is called continuously to re-render the scene.
         *
         * @see .getRenderMode
         * @see .setRenderMode
         */
        const val RENDERMODE_CONTINUOUSLY = 1

        /**
         * The renderer only renders when the surface is created, or when [.requestRender] is called.
         *
         * @see .getRenderMode
         * @see .setRenderMode
         * @see .requestRender
         */
        const val RENDERMODE_WHEN_DIRTY = 0
        private val sGLThreadManager = GLThreadManager()
    }
}