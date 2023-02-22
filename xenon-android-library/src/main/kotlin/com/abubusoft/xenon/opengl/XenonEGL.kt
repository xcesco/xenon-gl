/*
 * Copyright 2013 Google Inc. All rights reserved.
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
package com.abubusoft.xenon.opengl

import android.graphics.SurfaceTexture
import android.view.Surface
import com.abubusoft.kripton.android.Logger
import javax.microedition.khronos.egl.*

/**
 * Core EGL state (display, context, config).
 *
 *
 * The EGLContext must only be attached to one thread at a time.  This class is not thread-safe.
 */
class XenonEGL @JvmOverloads constructor(sharedContext: EGLContext? = null, flags: Int = 0) {
    /**
     * @return the argonGLDisplay
     */
    var argonGLDisplay = EGL10.EGL_NO_DISPLAY
        private set

    /**
     * @return the argonGLContext
     */
    var argonGLContext = EGL10.EGL_NO_CONTEXT
        private set
    private var mEGLConfig: EGLConfig? = null

    /**
     * Returns the GLES version this context is configured for (currently 2 or 3).
     */
    var glVersion = -1

    /**
     * @return the xenonEGL
     */
    val argonEGL: EGL10
    /**
     * Prepares EGL display and context.
     *
     *
     * @param sharedContext The context to share, or null if sharing is not desired.
     * @param flags Configuration bit flags, e.g. FLAG_RECORDABLE.
     */
    /**
     * Prepares EGL display and context.
     *
     *
     * Equivalent to EglCore(null, 0).
     */
    init {
        var sharedContext = sharedContext
        argonEGL = EGLContext.getEGL() as EGL10
        if (argonGLDisplay !== EGL10.EGL_NO_DISPLAY) {
            throw RuntimeException("EGL already set up")
        }
        if (sharedContext == null) {
            sharedContext = EGL10.EGL_NO_CONTEXT
        }
        argonGLDisplay = argonEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        if (argonGLDisplay === EGL10.EGL_NO_DISPLAY) {
            throw RuntimeException("unable to get EGL14 display")
        }
        val version = IntArray(2)
        if (!argonEGL.eglInitialize(argonGLDisplay, version)) {
            argonGLDisplay = null
            throw RuntimeException("unable to initialize EGL14")
        }

        // Try to get a GLES3 context, if requested.
        if (flags and FLAG_TRY_GLES3 != 0) {
            //Log.d(TAG, "Trying GLES 3");
            val config = getConfig(flags, 3)
            if (config != null) {
                val attrib3_list = intArrayOf(
                    EGL_CONTEXT_CLIENT_VERSION, 3,
                    EGL10.EGL_NONE
                )
                val context = argonEGL.eglCreateContext(
                    argonGLDisplay, config, sharedContext,
                    attrib3_list
                )
                if (argonEGL.eglGetError() == EGL10.EGL_SUCCESS) {
                    //Log.d(TAG, "Got GLES 3 config");
                    mEGLConfig = config
                    argonGLContext = context
                    glVersion = 3
                }
            }
        }
        if (argonGLContext === EGL10.EGL_NO_CONTEXT) {  // GLES 2 only, or GLES 3 attempt failed
            //Log.d(TAG, "Trying GLES 2");
            val config = getConfig(flags, 2) ?: throw RuntimeException("Unable to find a suitable EGLConfig")
            val attrib2_list = intArrayOf(
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
            )
            val context = argonEGL.eglCreateContext(
                argonGLDisplay, config, sharedContext,
                attrib2_list
            )
            checkEglError("eglCreateContext")
            mEGLConfig = config
            argonGLContext = context
            glVersion = 2
        }

        // Confirm with query.
        val values = IntArray(1)
        argonEGL.eglQueryContext(
            argonGLDisplay, argonGLContext, EGL_CONTEXT_CLIENT_VERSION,
            values
        )
        Logger.info("EGLContext created, client version " + values[0])
    }

    /**
     * Finds a suitable EGLConfig.
     *
     * @param flags Bit flags from constructor.
     * @param version Must be 2 or 3.
     */
    private fun getConfig(flags: Int, version: Int): EGLConfig? {
        var renderableType = XenonGL.EGL_OPENGL_ES2_BIT
        if (version >= 3) {
            renderableType = XenonGL.EGL_OPENGL_ES3_BIT
        }

        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.
        val attribList = intArrayOf(
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,  //EGL14.EGL_DEPTH_SIZE, 16,
            //EGL14.EGL_STENCIL_SIZE, 8,
            EGL10.EGL_RENDERABLE_TYPE, renderableType,
            EGL10.EGL_NONE, 0,  // placeholder for recordable [@-3]
            EGL10.EGL_NONE
        )
        if (flags and FLAG_RECORDABLE != 0) {
            attribList[attribList.size - 3] = EGL_RECORDABLE_ANDROID
            attribList[attribList.size - 2] = 1
        }
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!argonEGL.eglChooseConfig(
                argonGLDisplay, attribList, configs, configs.size,
                numConfigs
            )
        ) {
            Logger.warn("unable to find RGB8888 / $version EGLConfig")
            return null
        }
        return configs[0]
    }

    /**
     * Discards all resources held by this class, notably the EGL context.  This must be
     * called from the thread where the context was created.
     *
     *
     * On completion, no context will be current.
     */
    fun release() {
        if (argonGLDisplay !== EGL10.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            argonEGL.eglMakeCurrent(
                argonGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
            argonEGL.eglDestroyContext(argonGLDisplay, argonGLContext)
            //EGL10.eglReleaseThread();
            argonEGL.eglTerminate(argonGLDisplay)
        }
        argonGLDisplay = EGL10.EGL_NO_DISPLAY
        argonGLContext = EGL10.EGL_NO_CONTEXT
        mEGLConfig = null
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            if (argonGLDisplay !== EGL10.EGL_NO_DISPLAY) {
                // We're limited here -- finalizers don't run on the thread that holds
                // the EGL state, so if a surface or context is still current on another
                // thread we can't fully release it here.  Exceptions thrown from here
                // are quietly discarded.  Complain in the log file.
                Logger.warn("WARNING: EglCore was not explicitly released -- state may be leaked")
                release()
            }
        } finally {
            super.finalize()
        }
    }

    /**
     * Destroys the specified surface.  Note the EGLSurface won't actually be destroyed if it's
     * still current in a context.
     */
    fun releaseSurface(eglSurface: EGLSurface?) {
        argonEGL.eglDestroySurface(argonGLDisplay, eglSurface)
    }

    /**
     * Creates an EGL surface associated with a Surface.
     *
     *
     * If this is destined for MediaCodec, the EGLConfig should have the "recordable" attribute.
     */
    fun createWindowSurface(surface: Any): EGLSurface {
        if (surface !is Surface && surface !is SurfaceTexture) {
            throw RuntimeException("invalid surface: $surface")
        }

        // Create a window surface, and attach it to the Surface we received.
        val surfaceAttribs = intArrayOf(
            EGL10.EGL_NONE
        )
        val eglSurface = argonEGL.eglCreateWindowSurface(
            argonGLDisplay, mEGLConfig, surface,
            surfaceAttribs
        )
        checkEglError("eglCreateWindowSurface")
        if (eglSurface == null) {
            throw RuntimeException("surface was null")
        }
        return eglSurface
    }

    /**
     * Creates an EGL surface associated with an offscreen buffer.
     */
    fun createOffscreenSurface(width: Int, height: Int): EGLSurface {
        val surfaceAttribs = intArrayOf(
            EGL10.EGL_WIDTH, width,
            EGL10.EGL_HEIGHT, height,
            EGL10.EGL_NONE
        )
        val eglSurface = argonEGL.eglCreatePbufferSurface(
            argonGLDisplay, mEGLConfig,
            surfaceAttribs
        )
        checkEglError("eglCreatePbufferSurface")
        if (eglSurface == null) {
            throw RuntimeException("surface was null")
        }
        return eglSurface
    }

    /**
     * Makes our EGL context current, using the supplied surface for both "draw" and "read".
     */
    fun makeCurrent(eglSurface: EGLSurface?) {
        if (argonGLDisplay === EGL10.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
            Logger.debug("NOTE: makeCurrent w/o display")
        }
        if (!argonEGL.eglMakeCurrent(argonGLDisplay, eglSurface, eglSurface, argonGLContext)) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    /**
     * Makes our EGL context current, using the supplied "draw" and "read" surfaces.
     */
    fun makeCurrent(drawSurface: EGLSurface?, readSurface: EGLSurface?) {
        if (argonGLDisplay === EGL10.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
            Logger.debug("NOTE: makeCurrent w/o display")
        }
        if (!argonEGL.eglMakeCurrent(argonGLDisplay, drawSurface, readSurface, argonGLContext)) {
            throw RuntimeException("eglMakeCurrent(draw,read) failed")
        }
    }

    /**
     * Makes no context current.
     */
    fun makeNothingCurrent() {
        if (!argonEGL.eglMakeCurrent(
                argonGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            )
        ) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    fun swapBuffers(eglSurface: EGLSurface?): Boolean {
        return argonEGL.eglSwapBuffers(argonGLDisplay, eglSurface)
    }

    /**
     * Returns true if our context and the specified surface are current.
     */
    fun isCurrent(eglSurface: EGLSurface?): Boolean {
        return argonGLContext == argonEGL.eglGetCurrentContext() && eglSurface == argonEGL.eglGetCurrentSurface(EGL10.EGL_DRAW)
    }

    /**
     * Performs a simple surface query.
     */
    fun querySurface(eglSurface: EGLSurface?, what: Int): Int {
        val value = IntArray(1)
        argonEGL.eglQuerySurface(argonGLDisplay, eglSurface, what, value)
        return value[0]
    }

    /**
     * Queries a string value.
     */
    fun queryString(what: Int): String {
        return argonEGL.eglQueryString(argonGLDisplay, what)
    }

    /**
     * Writes the current display, context, and surface to the log.
     */
    fun logCurrent(msg: String) {
        val display: EGLDisplay
        val context: EGLContext
        val surface: EGLSurface
        display = argonEGL.eglGetCurrentDisplay()
        context = argonEGL.eglGetCurrentContext()
        surface = argonEGL.eglGetCurrentSurface(EGL10.EGL_DRAW)
        Logger.info(
            "Current EGL (" + msg + "): display=" + display + ", context=" + context +
                    ", surface=" + surface
        )
    }

    /**
     * Checks for EGL errors.  Throws an exception if an error has been raised.
     */
    private fun checkEglError(msg: String) {
        var error: Int
        if (argonEGL.eglGetError().also { error = it } != EGL10.EGL_SUCCESS) {
            checkGlError(msg)
            throw RuntimeException("$msg: EGL error: %s")
        }
    }

    companion object {
        const val EGL_CONTEXT_CLIENT_VERSION = 0x3098

        /**
         * Constructor flag: surface must be recordable.  This discourages EGL from using a
         * pixel format that cannot be converted efficiently to something usable by the video
         * encoder.
         */
        const val FLAG_RECORDABLE = 0x01

        /**
         * Constructor flag: ask for GLES3, fall back to GLES2 if not available.  Without this
         * flag, GLES2 is used.
         */
        const val FLAG_TRY_GLES3 = 0x02

        // Android-specific extension.
        private const val EGL_RECORDABLE_ANDROID = 0x3142
    }
}