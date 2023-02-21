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

package com.abubusoft.xenon.opengl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import com.abubusoft.kripton.android.Logger;

import android.graphics.SurfaceTexture;
import android.view.Surface;

/**
 * Core EGL state (display, context, config).
 * <p>
 * The EGLContext must only be attached to one thread at a time.  This class is not thread-safe.
 */
public final class XenonEGL {
	
	public static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    /**
     * Constructor flag: surface must be recordable.  This discourages EGL from using a
     * pixel format that cannot be converted efficiently to something usable by the video
     * encoder.
     */
    public static final int FLAG_RECORDABLE = 0x01;

    /**
     * Constructor flag: ask for GLES3, fall back to GLES2 if not available.  Without this
     * flag, GLES2 is used.
     */
    public static final int FLAG_TRY_GLES3 = 0x02;

    // Android-specific extension.
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private EGLDisplay argonGLDisplay = EGL10.EGL_NO_DISPLAY;
    /**
	 * @return the argonGLDisplay
	 */
	public EGLDisplay getArgonGLDisplay() {
		return argonGLDisplay;
	}

	/**
	 * @return the argonGLContext
	 */
	public EGLContext getArgonGLContext() {
		return argonGLContext;
	}

	private EGLContext argonGLContext = EGL10.EGL_NO_CONTEXT;
    private EGLConfig mEGLConfig = null;
    private int mGlVersion = -1;
    
    private EGL10 argonEGL;


    /**
	 * @return the xenonEGL
	 */
	public EGL10 getArgonEGL() {
		return argonEGL;
	}

	/**
     * Prepares EGL display and context.
     * <p>
     * Equivalent to EglCore(null, 0).
     */
    public XenonEGL() {
        this(null, 0);
    }

    /**
     * Prepares EGL display and context.
     * <p>
     * @param sharedContext The context to share, or null if sharing is not desired.
     * @param flags Configuration bit flags, e.g. FLAG_RECORDABLE.
     */
    public XenonEGL(EGLContext sharedContext, int flags) {
    	this.argonEGL=(EGL10) EGLContext.getEGL();
    	
        if (argonGLDisplay != EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }

        if (sharedContext == null) {
            sharedContext = EGL10.EGL_NO_CONTEXT;
        }

        argonGLDisplay = argonEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (argonGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] version = new int[2];
        if (!argonEGL.eglInitialize(argonGLDisplay, version)) {
            argonGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }

        // Try to get a GLES3 context, if requested.
        if ((flags & FLAG_TRY_GLES3) != 0) {
            //Log.d(TAG, "Trying GLES 3");
            EGLConfig config = getConfig(flags, 3);
            if (config != null) {
                int[] attrib3_list = {
                        EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL10.EGL_NONE
                };
                EGLContext context = argonEGL.eglCreateContext(argonGLDisplay, config, sharedContext,
                        attrib3_list);

                if (argonEGL.eglGetError() == EGL10.EGL_SUCCESS) {
                    //Log.d(TAG, "Got GLES 3 config");
                    mEGLConfig = config;
                    argonGLContext = context;
                    mGlVersion = 3;
                }
            }
        }
        if (argonGLContext == EGL10.EGL_NO_CONTEXT) {  // GLES 2 only, or GLES 3 attempt failed
            //Log.d(TAG, "Trying GLES 2");
            EGLConfig config = getConfig(flags, 2);
            if (config == null) {
                throw new RuntimeException("Unable to find a suitable EGLConfig");
            }
            int[] attrib2_list = {
            		EGL_CONTEXT_CLIENT_VERSION, 2,
            		EGL10.EGL_NONE
            };
            EGLContext context = argonEGL.eglCreateContext(argonGLDisplay, config, sharedContext,
                    attrib2_list);
            checkEglError("eglCreateContext");
            mEGLConfig = config;
            argonGLContext = context;
            mGlVersion = 2;
        }

        // Confirm with query.
        int[] values = new int[1];
        argonEGL.eglQueryContext(argonGLDisplay, argonGLContext, EGL_CONTEXT_CLIENT_VERSION,
                values);
        Logger.info("EGLContext created, client version " + values[0]);
    }

    /**
     * Finds a suitable EGLConfig.
     *
     * @param flags Bit flags from constructor.
     * @param version Must be 2 or 3.
     */
    private EGLConfig getConfig(int flags, int version) {
        int renderableType = XenonGL.EGL_OPENGL_ES2_BIT;
        if (version >= 3) {
            renderableType = XenonGL.EGL_OPENGL_ES3_BIT;
        }

        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.
        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16,
                //EGL14.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, renderableType,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        if ((flags & FLAG_RECORDABLE) != 0) {
            attribList[attribList.length - 3] = EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 2] = 1;
        }
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!argonEGL.eglChooseConfig(argonGLDisplay, attribList, configs, configs.length,
                numConfigs)) {
            Logger.warn("unable to find RGB8888 / " + version + " EGLConfig");
            return null;
        }
        return configs[0];
    }

    /**
     * Discards all resources held by this class, notably the EGL context.  This must be
     * called from the thread where the context was created.
     * <p>
     * On completion, no context will be current.
     */
    public void release() {
        if (argonGLDisplay != EGL10.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            argonEGL.eglMakeCurrent(argonGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
            		EGL10.EGL_NO_CONTEXT);
            argonEGL.eglDestroyContext(argonGLDisplay, argonGLContext);
            //EGL10.eglReleaseThread();
            argonEGL.eglTerminate(argonGLDisplay);
        }

        argonGLDisplay = EGL10.EGL_NO_DISPLAY;
        argonGLContext = EGL10.EGL_NO_CONTEXT;
        mEGLConfig = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (argonGLDisplay != EGL10.EGL_NO_DISPLAY) {
                // We're limited here -- finalizers don't run on the thread that holds
                // the EGL state, so if a surface or context is still current on another
                // thread we can't fully release it here.  Exceptions thrown from here
                // are quietly discarded.  Complain in the log file.
                Logger.warn("WARNING: EglCore was not explicitly released -- state may be leaked");
                release();
            }
        } finally {
            super.finalize();
        }
    }

    /**
     * Destroys the specified surface.  Note the EGLSurface won't actually be destroyed if it's
     * still current in a context.
     */
    public void releaseSurface(EGLSurface eglSurface) {
        argonEGL.eglDestroySurface(argonGLDisplay, eglSurface);
    }

    /**
     * Creates an EGL surface associated with a Surface.
     * <p>
     * If this is destined for MediaCodec, the EGLConfig should have the "recordable" attribute.
     */
    public EGLSurface createWindowSurface(Object surface) {
        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
            throw new RuntimeException("invalid surface: " + surface);
        }

        // Create a window surface, and attach it to the Surface we received.
        int[] surfaceAttribs = {
                EGL10.EGL_NONE
        };
        EGLSurface eglSurface = argonEGL.eglCreateWindowSurface(argonGLDisplay, mEGLConfig, surface,
                surfaceAttribs);
        checkEglError("eglCreateWindowSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eglSurface;
    }

    /**
     * Creates an EGL surface associated with an offscreen buffer.
     */
    public EGLSurface createOffscreenSurface(int width, int height) {
        int[] surfaceAttribs = {
                EGL10.EGL_WIDTH, width,
                EGL10.EGL_HEIGHT, height,
                EGL10.EGL_NONE
        };
        EGLSurface eglSurface = argonEGL.eglCreatePbufferSurface(argonGLDisplay, mEGLConfig,
                surfaceAttribs);
        checkEglError("eglCreatePbufferSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eglSurface;
    }

    /**
     * Makes our EGL context current, using the supplied surface for both "draw" and "read".
     */
    public void makeCurrent(EGLSurface eglSurface) {
        if (argonGLDisplay == EGL10.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
            Logger.debug("NOTE: makeCurrent w/o display");
        }
        if (!argonEGL.eglMakeCurrent(argonGLDisplay, eglSurface, eglSurface, argonGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    /**
     * Makes our EGL context current, using the supplied "draw" and "read" surfaces.
     */
    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (argonGLDisplay == EGL10.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
        	Logger.debug("NOTE: makeCurrent w/o display");
        }
        if (!argonEGL.eglMakeCurrent(argonGLDisplay, drawSurface, readSurface, argonGLContext)) {
            throw new RuntimeException("eglMakeCurrent(draw,read) failed");
        }
    }

    /**
     * Makes no context current.
     */
    public void makeNothingCurrent() {
        if (!argonEGL.eglMakeCurrent(argonGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
        		EGL10.EGL_NO_CONTEXT)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    public boolean swapBuffers(EGLSurface eglSurface) {
        return argonEGL.eglSwapBuffers(argonGLDisplay, eglSurface);
    }

    /**
     * Returns true if our context and the specified surface are current.
     */
    public boolean isCurrent(EGLSurface eglSurface) {
        return argonGLContext.equals(argonEGL.eglGetCurrentContext()) &&
            eglSurface.equals(argonEGL.eglGetCurrentSurface(EGL10.EGL_DRAW));
    }

    /**
     * Performs a simple surface query.
     */
    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[1];
        argonEGL.eglQuerySurface(argonGLDisplay, eglSurface, what, value);
        return value[0];
    }

    /**
     * Queries a string value.
     */
    public String queryString(int what) {
        return argonEGL.eglQueryString(argonGLDisplay, what);
    }

    /**
     * Returns the GLES version this context is configured for (currently 2 or 3).
     */
    public int getGlVersion() {
        return mGlVersion;
    }

    /**
     * Writes the current display, context, and surface to the log.
     */
    public void logCurrent(String msg) {
        EGLDisplay display;
        EGLContext context;
        EGLSurface surface;

        display = argonEGL.eglGetCurrentDisplay();
        context = argonEGL.eglGetCurrentContext();
        surface = argonEGL.eglGetCurrentSurface(EGL10.EGL_DRAW);
        Logger.info("Current EGL (" + msg + "): display=" + display + ", context=" + context +
                ", surface=" + surface);
    }

    /**
     * Checks for EGL errors.  Throws an exception if an error has been raised.
     */
    private void checkEglError(String msg) {
        int error;
        if ((error = argonEGL.eglGetError()) != EGL10.EGL_SUCCESS) {
        	XenonGL.checkGlError(msg);
            throw new RuntimeException(msg + ": EGL error: %s" );
        }
    }
}