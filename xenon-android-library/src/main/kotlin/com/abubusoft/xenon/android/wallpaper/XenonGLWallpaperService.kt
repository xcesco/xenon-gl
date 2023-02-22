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
package com.abubusoft.xenon.android.wallpaper

import android.content.Context
import android.os.Bundle
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.WindowManager
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.Xenon4OpenGL
import com.abubusoft.xenon.android.XenonWallpaper
import com.abubusoft.xenon.android.listener.XenonGestureDetector
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.XenonGLConfigChooser
import com.abubusoft.xenon.opengl.XenonGLDefaultRenderer
import com.abubusoft.xenon.opengl.XenonGLRenderer
import java.util.concurrent.locks.ReentrantLock

open class XenonGLWallpaperService : WallpaperService(), XenonWallpaper {
    inner class WallpaperView(context: Context?) : ArgonGLSurfaceView16(context) {
        init {
            // questo consente di mantenere il display
            destroyDisplayOnExit = false
        }

        /** Getter for SurfaceHolder object, surface holder is required to restore gl context in GLSurfaceView  */
        override fun getHolder(): SurfaceHolder {
            Logger.debug(" > AndroidLiveWallpaperService - getSurfaceHolder()")
            synchronized(sync) { return if (linkedEngine == null) null else linkedEngine!!.surfaceHolder }
        }

        fun onDestroy() {
            onDetachedFromWindow()
        }
    }

    // engine - begin
    inner class ArgonGLEngine : Engine() {
        protected var engineIsVisible = false

        // destination format of surface when this engine is active (updated in onSurfaceChanged)
        protected var engineFormat = 0
        protected var engineWidth = 0
        protected var engineHeight = 0
        override fun onCreate(surfaceHolder: SurfaceHolder) {
            Logger.debug(
                " > ArgonGLEngine - onCreate() " + hashCode() + " running: " + engines + ", linked: " + (linkedEngine === this) + ", thread: " + Thread.currentThread().toString()
            )
            super.onCreate(surfaceHolder)
        }
        // ---
        /**
         * Called before surface holder callbacks (ex for GLSurfaceView)! This is called immediately after the surface is first created. Implementations of this should start up whatever rendering code they desire. Note that only one thread
         * can ever draw into a Surface, so you should not draw into the Surface here if your normal rendering will be in another thread.
         */
        override fun onSurfaceCreated(holder: SurfaceHolder) {
            surfaceLock.lock()
            engines++
            setLinkedEngine(this)
            Logger.debug(" > ArgonGLEngine - onSurfaceCreated() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine === this))
            super.onSurfaceCreated(holder)
            if (engines == 1) {
                // safeguard: recover attributes that could suffered by unexpected surfaceDestroy event
                visibleEngines = 0
            }
            if (engines == 1 && argon == null) {
                viewFormat = 0 // must be initialized with zeroes
                viewWidth = 0
                viewHeight = 0
                argon = XenonBeanContext.getBean(XenonBeanType.XENON)
                argon.onServiceCreated(this@XenonGLWallpaperService)
                view = WallpaperView(this@XenonGLWallpaperService)
                renderer = createRenderer()
                view!!.setDebugFlags(ArgonGLSurfaceView16.DEBUG_CHECK_GL_ERROR or ArgonGLSurfaceView16.DEBUG_LOG_GL_CALLS)
                view!!.setEGLContextClientVersion(2)
                view!!.setPreserveEGLContextOnPause(true)
                view!!.holder.setFormat(XenonGLConfigChooser.build().pixelFormat)
                view!!.setRenderer(renderer!!)
                view!!.onPause()
                view!!.renderMode = ArgonGLSurfaceView16.RENDERMODE_CONTINUOUSLY
            }
            this.surfaceHolder.removeCallback(view) // we are going to call this events manually

            // inherit format from shared surface view
            engineFormat = viewFormat
            engineWidth = viewWidth
            engineHeight = viewHeight
            if (engines == 1) {
                Logger.debug(" > ArgonGLEngine - onSurfaceCreated() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine === this))
                view!!.surfaceCreated(holder)
            } else {
                // this combination of methods is described in AndroidWallpaperEngine.onResume
                Logger.debug(" > ArgonGLEngine - onSurfaceCreated() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine === this))
                view!!.surfaceDestroyed(holder)
                notifySurfaceChanged(engineFormat, engineWidth, engineHeight, false)
                view!!.surfaceCreated(holder)
            }
            notifyPreviewState()
            // notifyOffsetsChanged();
            /*
			 * if (!view.is Gdx.graphics.isContinuousRendering()) { view.requestRender(); }
			 */
        }

        /**
         * This is called immediately after any structural changes (format or size) have been made to the surface. You should at this point update the imagery in the surface. This method is always called at least once, after
         * surfaceCreated(SurfaceHolder).
         */
        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Logger.debug(
                " > ArgonGLEngine - onSurfaceChanged() isPreview: " + isPreview + ", " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine === this) + ", sufcace valid: "
                        + surfaceHolder.surface.isValid
            )
            super.onSurfaceChanged(holder, format, width, height)
            notifySurfaceChanged(format, width, height, true)

            // it shouldn't be required there (as I understand android.service.wallpaper.WallpaperService impl)
            // notifyPreviewState();
        }

        /**
         * Notifies shared GLSurfaceView about changed surface format.
         *
         * @param format
         * @param width
         * @param height
         * @param forceUpdate
         * if false, surface view will be notified only if currently contains expired information
         */
        private fun notifySurfaceChanged(format: Int, width: Int, height: Int, forceUpdate: Boolean) {
            if (!forceUpdate && format == viewFormat && width == viewWidth && height == viewHeight) {
                // skip if didn't changed
                Logger.debug(" > surface is current, skipping surfaceChanged event")
            } else {
                // update engine desired surface format
                engineFormat = format
                engineWidth = width
                engineHeight = height

                // update surface view if engine is linked with it already
                if (linkedEngine === this) {
                    viewFormat = engineFormat
                    viewWidth = engineWidth
                    viewHeight = engineHeight
                    view!!.surfaceChanged(this.surfaceHolder, viewFormat, viewWidth, viewHeight)
                } else {
                    Logger.debug(" > engine is not active, skipping surfaceChanged event")
                }
            }
        }

        /**
         * Called to inform you of the wallpaper becoming visible or hidden. It is very important that a wallpaper only use CPU while it is visible..
         */
        override fun onVisibilityChanged(visible: Boolean) {
            val reportedVisible = isVisible
            Logger.debug(" > ArgonGLEngine - onVisibilityChanged(paramVisible: " + visible + " reportedVisible: " + reportedVisible + ") " + hashCode() + ", sufcace valid: " + surfaceHolder.surface.isValid)
            super.onVisibilityChanged(visible)

            // Android WallpaperService sends fake visibility changed events to force some buggy live wallpapers to shut down after
            // onSurfaceChanged when they aren't visible, it can cause problems in current implementation and it is not necessary
            if (reportedVisible == false && visible == true) {
                Logger.debug(" > fake visibilityChanged event! Android WallpaperService likes do that!")
                return
            }
            notifyVisibilityChanged(visible)
        }

        private fun notifyVisibilityChanged(visible: Boolean) {
            if (engineIsVisible != visible) {
                engineIsVisible = visible
                if (engineIsVisible) onResume() else onPause()
            } else {
                Logger.debug(" > visible state is current, skipping visibilityChanged event!")
            }
        }

        fun onResume() {
            visibleEngines++
            Logger.debug(" > ArgonGLEngine - onResume() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine === this) + ", visible: " + visibleEngines)
            Logger.info("engine resumed")
            if (linkedEngine != null) {
                if (linkedEngine !== this) {
                    setLinkedEngine(this)


                    // disconnect surface view from previous window
                    view!!.surfaceDestroyed(this.surfaceHolder) // force gl surface reload, new instance will be created on current
                    // surface holder

                    // resize surface to match window associated with current engine
                    notifySurfaceChanged(engineFormat, engineWidth, engineHeight, false)

                    // connect surface view to current engine
                    view!!.surfaceCreated(this.surfaceHolder)
                } else {
                    // update if surface changed when engine wasn't active
                    notifySurfaceChanged(engineFormat, engineWidth, engineHeight, false)
                }
                if (visibleEngines == 1) {
                    Logger.info(">*** onResume")
                    view!!.onResume()
                }
                notifyPreviewState()
                // notifyOffsetsChanged();
                // if (!Gdx.graphics.isContinuousRendering()) {
                // view. requestRendering();
                // }
            }
        }

        fun onPause() {
            visibleEngines--
            Logger.debug(" > ArgonGLEngine - onPause() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine === this) + ", visible: " + visibleEngines)
            Logger.info("engine paused")

            // this shouldn't never happen, but if it will.. live wallpaper will not be stopped when device will pause and lwp will
            // drain battery.. shortly!
            if (visibleEngines >= engines) {
                Logger.error("wallpaper lifecycle error, counted too many visible engines! repairing..")
                visibleEngines = Math.max(engines - 1, 0)
            }
            if (linkedEngine != null) {
                if (visibleEngines == 0) {
                    Logger.info(">*** onPause")
                    view!!.onPause()
                }
            }
            Logger.debug(" > ArgonGLEngine - onPause() done!")
        }

        /**
         * Called after surface holder callbacks (ex for GLSurfaceView)! This is called immediately before a surface is being destroyed. After returning from this call, you should no longer try to access this surface. If you have a
         * rendering thread that directly accesses the surface, you must ensure that thread is no longer touching the Surface before returning from this function.
         *
         * Attention! In some cases GL context may be shutdown right now! and SurfaceHolder.Surface.isVaild = false
         */
        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            engines--
            Logger.debug(" > ArgonGLEngine - onSurfaceDestroyed() " + hashCode() + ", running: " + engines + " ,linked: " + (linkedEngine === this) + ", isVisible: " + engineIsVisible)

            // application can be in resumed state at this moment if app surface had been lost just after it was created (wallpaper
            // selected too fast from preview mode etc)
            // it is too late probably - calling on pause causes deadlock
            // notifyVisibilityChanged(false);

            // it is too late to call app.onDispose, just free native resources
            if (engines == 0) onDeepPauseApplication()

            // free surface if it belongs to this engine and if it was initialized
            if (linkedEngine === this && view != null) {
                view!!.surfaceDestroyed(holder)
            }

            // waitingSurfaceChangedEvent = null;
            engineFormat = 0
            engineWidth = 0
            engineHeight = 0

            // safeguard for other engine callbacks
            if (engines == 0) linkedEngine = null
            super.onSurfaceDestroyed(holder)
            surfaceLock.unlock()
        }

        private fun onDeepPauseApplication() {
            // TODO Auto-generated method stub
        }

        // end of lifecycle methods ////////////////////////////////////////////////////////
        // input
        override fun onCommand(pAction: String, pX: Int, pY: Int, pZ: Int, pExtras: Bundle, pResultRequested: Boolean): Bundle {
            Logger.debug(" > ArgonGLEngine - onCommand(" + pAction + " " + pX + " " + pY + " " + pZ + " " + pExtras + " " + pResultRequested + ")" + ", linked: " + (linkedEngine === this))
            return super.onCommand(pAction, pX, pY, pZ, pExtras, pResultRequested)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (linkedEngine === this) {

                // il renderer è sempre ready, ho visto che metterla comunque qua blocca.
                if (gestureDetector != null) {
                    gestureDetector!!.onTouchEvent(event)
                } else {
                    Logger.debug("onTouchEvent but surface is not ready")
                }
            }
        }

        // offsets from last onOffsetsChanged
        var offsetsConsumed = true
        var xOffset = 0.0f
        var yOffset = 0.0f
        var xOffsetStep = 0.0f
        var yOffsetStep = 0.0f
        var xPixelOffset = 0
        var yPixelOffset = 0

        init {
            Logger.debug(" > ArgonGLEngine() " + hashCode())
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {

            // it spawns too frequent on some devices - its annoying!
            // if (DEBUG)
            // Log.d(TAG, " > AndroidWallpaperEngine - onOffsetChanged(" + xOffset + " " + yOffset + " " + xOffsetStep + " "
            // + yOffsetStep + " " + xPixelOffset + " " + yPixelOffset + ") " + hashCode() + ", linkedApp: " + (linkedApp != null));
            offsetsConsumed = false
            this.xOffset = xOffset
            this.yOffset = yOffset
            this.xOffsetStep = xOffsetStep
            this.yOffsetStep = yOffsetStep
            this.xPixelOffset = xPixelOffset
            this.yPixelOffset = yPixelOffset

            // can fail if linkedApp == null, so we repeat it in Engine.onResume
            // notifyOffsetsChanged();
            // if (!view.isContinuousRendering()) {
            // view.re r Gdx.graphics.requestRendering();
            // }

            // super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            WallpaperManager.Companion.instance().setScreenOffset(xOffset)
        }

        /*
		 * protected void notifyOffsetsChanged() { if (linkedEngine == this) { if (!offsetsConsumed) { // no need for more sophisticated synchronization - offsetsChanged can be called multiple // times and with various patterns on various
		 * devices - user application must be prepared for that offsetsConsumed = true;
		 * 
		 * if (linkedEngine == AndroidWallpaperEngine.this) {
		 * 
		 * }
		 * 
		 * } } }
		 */
        protected fun notifyPreviewState() {
            // notify preview state to app listener
            if (linkedEngine === this) {
                val currentPreviewState = linkedEngine!!.isPreview
                synchronized(sync) {
                    if (!isPreviewNotified || notifiedPreviewState != currentPreviewState) {
                        notifiedPreviewState = currentPreviewState
                        isPreviewNotified = true
                        argon!!.isPreviewStatusChanged = true
                    }
                }
            }
        } // ---
    }

    // engine - end
    @Volatile
    protected var argon: Xenon4OpenGL? = null // can be accessed from GL render thread
    protected var view: WallpaperView? = null
    protected var renderer: XenonGLRenderer? = null

    // current format of surface (one GLSurfaceView is shared between all engines)
    protected var viewFormat = 0
    protected var viewWidth = 0
    protected var viewHeight = 0
    protected var engines = 0
    protected var visibleEngines = 0

    // engine currently associated with app instance, linked engine serves surface handler for GLSurfaceView
    @Volatile
    protected var linkedEngine: ArgonGLEngine? = null // can be accessed from GL render thread by getSurfaceHolder
    protected fun setLinkedEngine(linkedEngine: ArgonGLEngine?) {
        synchronized(sync) { this.linkedEngine = linkedEngine }
    }

    // if preview state notified ever
    @Volatile
    protected var isPreviewNotified = false

    // the value of last preview state notified to app listener
    @Volatile
    protected var notifiedPreviewState = false

    @Volatile
    var sync = IntArray(0)
    var surfaceLock = ReentrantLock()

    /**
     * Service is dying, and will not be used again. You have to finish execution off all living threads there or short after there, besides the new wallpaper service wouldn't be able to start.
     */
    override fun onDestroy() {
        Logger.debug(" > AndroidLiveWallpaperService - onDestroy() " + hashCode())
        super.onDestroy() // can call engine.onSurfaceDestroyed, must be before bellow code:
        if (argon != null) {
            argon!!.onDestroy()
            view!!.onDestroy()
            argon = null
            view = null
        }
    }

    val windowManager: WindowManager
        get() = getSystemService(WINDOW_SERVICE) as WindowManager

    override fun onCreateEngine(): Engine {
        Logger.debug(">>>> onCreateEngine")
        return ArgonGLEngine()
    }

    override fun createRenderer(): XenonGLRenderer {
        return XenonGLDefaultRenderer()
    }

    override fun setRenderer(renderer: XenonGLRenderer) {
        this.renderer = renderer
    }

    override fun setGestureDetector(gestureDetectorValue: XenonGestureDetector) {
        gestureDetector = gestureDetectorValue
    }

    /**
     *
     *
     * Gestore delle gesture.
     *
     */
    var gestureDetector: XenonGestureDetector? = null
}