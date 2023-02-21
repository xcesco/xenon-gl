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

package com.abubusoft.xenon.android.wallpaper;

import java.util.concurrent.locks.ReentrantLock;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.Xenon4OpenGL;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.android.XenonWallpaper;
import com.abubusoft.xenon.android.listener.XenonGestureDetector;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16;
import com.abubusoft.xenon.opengl.XenonGLConfigChooser;
import com.abubusoft.xenon.opengl.XenonGLDefaultRenderer;
import com.abubusoft.xenon.opengl.XenonGLRenderer;

import android.content.Context;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class XenonGLWallpaperService extends WallpaperService implements XenonWallpaper {

	public class WallpaperView extends ArgonGLSurfaceView16 {
		public WallpaperView(Context context) {
			super(context);
			// questo consente di mantenere il display
			destroyDisplayOnExit = false;
		}

		/** Getter for SurfaceHolder object, surface holder is required to restore gl context in GLSurfaceView */
		public SurfaceHolder getHolder() {
			Logger.debug(" > AndroidLiveWallpaperService - getSurfaceHolder()");

			synchronized (sync) {
				if (linkedEngine == null)
					return null;
				else
					return linkedEngine.getSurfaceHolder();
			}
		}

		public void onDestroy() {
			this.onDetachedFromWindow();
		}
	}

	// engine - begin

	public class ArgonGLEngine extends Engine {

		protected boolean engineIsVisible = false;

		// destination format of surface when this engine is active (updated in onSurfaceChanged)
		protected int engineFormat;
		protected int engineWidth;
		protected int engineHeight;

		public ArgonGLEngine() {
			Logger.debug(" > ArgonGLEngine() " + hashCode());
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			Logger.debug(" > ArgonGLEngine - onCreate() " + hashCode() + " running: " + engines + ", linked: " + (linkedEngine == this) + ", thread: " + Thread.currentThread().toString());

			super.onCreate(surfaceHolder);

		}

		// ---
		/**
		 * Called before surface holder callbacks (ex for GLSurfaceView)! This is called immediately after the surface is first created. Implementations of this should start up whatever rendering code they desire. Note that only one thread
		 * can ever draw into a Surface, so you should not draw into the Surface here if your normal rendering will be in another thread.
		 */
		@Override
		public void onSurfaceCreated(final SurfaceHolder holder) {
			surfaceLock.lock();
			engines++;
			setLinkedEngine(this);

			Logger.debug(" > ArgonGLEngine - onSurfaceCreated() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine == this));

			super.onSurfaceCreated(holder);

			if (engines == 1) {
				// safeguard: recover attributes that could suffered by unexpected surfaceDestroy event
				visibleEngines = 0;
			}

			if (engines == 1 && argon == null) {
				viewFormat = 0; // must be initialized with zeroes
				viewWidth = 0;
				viewHeight = 0;

				argon = XenonBeanContext.getBean(XenonBeanType.XENON);
				argon.onServiceCreated(XenonGLWallpaperService.this);

				view = new WallpaperView(XenonGLWallpaperService.this);
				renderer = createRenderer();
				view.setDebugFlags(ArgonGLSurfaceView16.DEBUG_CHECK_GL_ERROR | ArgonGLSurfaceView16.DEBUG_LOG_GL_CALLS);
				view.setEGLContextClientVersion(2);
				view.setPreserveEGLContextOnPause(true);
				
				view.getHolder().setFormat(XenonGLConfigChooser.build().getPixelFormat());
				view.setRenderer(renderer);
				
				view.onPause();

				view.setRenderMode(ArgonGLSurfaceView16.RENDERMODE_CONTINUOUSLY);
			}

			this.getSurfaceHolder().removeCallback(view); // we are going to call this events manually

			// inherit format from shared surface view
			engineFormat = viewFormat;
			engineWidth = viewWidth;
			engineHeight = viewHeight;

			if (engines == 1) {
				Logger.debug(" > ArgonGLEngine - onSurfaceCreated() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine == this));
				view.surfaceCreated(holder);
			} else {
				// this combination of methods is described in AndroidWallpaperEngine.onResume
				Logger.debug(" > ArgonGLEngine - onSurfaceCreated() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine == this));
				view.surfaceDestroyed(holder);
				notifySurfaceChanged(engineFormat, engineWidth, engineHeight, false);
				view.surfaceCreated(holder);
			}

			notifyPreviewState();
			// notifyOffsetsChanged();
			/*
			 * if (!view.is Gdx.graphics.isContinuousRendering()) { view.requestRender(); }
			 */
		}

		/**
		 * This is called immediately after any structural changes (format or size) have been made to the surface. You should at this point update the imagery in the surface. This method is always called at least once, after
		 * surfaceCreated(SurfaceHolder).
		 */
		@Override
		public void onSurfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
			Logger.debug(" > ArgonGLEngine - onSurfaceChanged() isPreview: " + isPreview() + ", " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine == this) + ", sufcace valid: "
					+ getSurfaceHolder().getSurface().isValid());

			super.onSurfaceChanged(holder, format, width, height);

			notifySurfaceChanged(format, width, height, true);

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
		 *            if false, surface view will be notified only if currently contains expired information
		 */
		private void notifySurfaceChanged(final int format, final int width, final int height, boolean forceUpdate) {
			if (!forceUpdate && format == viewFormat && width == viewWidth && height == viewHeight) {
				// skip if didn't changed
				Logger.debug(" > surface is current, skipping surfaceChanged event");
			} else {
				// update engine desired surface format
				engineFormat = format;
				engineWidth = width;
				engineHeight = height;

				// update surface view if engine is linked with it already
				if (linkedEngine == this) {
					viewFormat = engineFormat;
					viewWidth = engineWidth;
					viewHeight = engineHeight;
					view.surfaceChanged(this.getSurfaceHolder(), viewFormat, viewWidth, viewHeight);
				} else {
					Logger.debug(" > engine is not active, skipping surfaceChanged event");
				}
			}
		}

		/**
		 * Called to inform you of the wallpaper becoming visible or hidden. It is very important that a wallpaper only use CPU while it is visible..
		 */
		@Override
		public void onVisibilityChanged(final boolean visible) {
			boolean reportedVisible = isVisible();

			Logger.debug(" > ArgonGLEngine - onVisibilityChanged(paramVisible: " + visible + " reportedVisible: " + reportedVisible + ") " + hashCode() + ", sufcace valid: " + getSurfaceHolder().getSurface().isValid());
			super.onVisibilityChanged(visible);

			// Android WallpaperService sends fake visibility changed events to force some buggy live wallpapers to shut down after
			// onSurfaceChanged when they aren't visible, it can cause problems in current implementation and it is not necessary
			if (reportedVisible == false && visible == true) {
				Logger.debug(" > fake visibilityChanged event! Android WallpaperService likes do that!");
				return;
			}

			notifyVisibilityChanged(visible);
		}

		private void notifyVisibilityChanged(final boolean visible) {
			if (this.engineIsVisible != visible) {
				this.engineIsVisible = visible;

				if (this.engineIsVisible)
					onResume();
				else
					onPause();
			} else {
				Logger.debug(" > visible state is current, skipping visibilityChanged event!");
			}
		}

		public void onResume() {
			visibleEngines++;
			Logger.debug(" > ArgonGLEngine - onResume() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine == this) + ", visible: " + visibleEngines);
			Logger.info("engine resumed");

			if (linkedEngine != null) {
				if (linkedEngine != this) {
					setLinkedEngine(this);
										

					// disconnect surface view from previous window
					view.surfaceDestroyed(this.getSurfaceHolder()); // force gl surface reload, new instance will be created on current
					// surface holder

					// resize surface to match window associated with current engine
					notifySurfaceChanged(engineFormat, engineWidth, engineHeight, false);

					// connect surface view to current engine
					view.surfaceCreated(this.getSurfaceHolder());
				} else {
					// update if surface changed when engine wasn't active
					notifySurfaceChanged(engineFormat, engineWidth, engineHeight, false);
				}

				if (visibleEngines == 1) {
					Logger.info(">*** onResume");
					view.onResume();
					
				}

				notifyPreviewState();
				// notifyOffsetsChanged();
				// if (!Gdx.graphics.isContinuousRendering()) {
				// view. requestRendering();
				// }
			}
		}

		public void onPause() {
			visibleEngines--;
			Logger.debug(" > ArgonGLEngine - onPause() " + hashCode() + ", running: " + engines + ", linked: " + (linkedEngine == this) + ", visible: " + visibleEngines);
			Logger.info("engine paused");

			// this shouldn't never happen, but if it will.. live wallpaper will not be stopped when device will pause and lwp will
			// drain battery.. shortly!
			if (visibleEngines >= engines) {
				Logger.error("wallpaper lifecycle error, counted too many visible engines! repairing..");
				visibleEngines = Math.max(engines - 1, 0);
			}

			if (linkedEngine != null) {
				if (visibleEngines == 0) {
					Logger.info(">*** onPause");
					view.onPause();
					
				}
			}

			Logger.debug(" > ArgonGLEngine - onPause() done!");
		}

		/**
		 * Called after surface holder callbacks (ex for GLSurfaceView)! This is called immediately before a surface is being destroyed. After returning from this call, you should no longer try to access this surface. If you have a
		 * rendering thread that directly accesses the surface, you must ensure that thread is no longer touching the Surface before returning from this function.
		 * 
		 * Attention! In some cases GL context may be shutdown right now! and SurfaceHolder.Surface.isVaild = false
		 */
		@Override
		public void onSurfaceDestroyed(final SurfaceHolder holder) {
			engines--;
			Logger.debug(" > ArgonGLEngine - onSurfaceDestroyed() " + hashCode() + ", running: " + engines + " ,linked: " + (linkedEngine == this) + ", isVisible: " + engineIsVisible);

			// application can be in resumed state at this moment if app surface had been lost just after it was created (wallpaper
			// selected too fast from preview mode etc)
			// it is too late probably - calling on pause causes deadlock
			// notifyVisibilityChanged(false);

			// it is too late to call app.onDispose, just free native resources
			if (engines == 0)
				onDeepPauseApplication();

			// free surface if it belongs to this engine and if it was initialized
			if (linkedEngine == this && view != null) {
				view.surfaceDestroyed(holder);
			}

			// waitingSurfaceChangedEvent = null;
			engineFormat = 0;
			engineWidth = 0;
			engineHeight = 0;

			// safeguard for other engine callbacks
			if (engines == 0)
				linkedEngine = null;

			super.onSurfaceDestroyed(holder);
			surfaceLock.unlock();
		}

		private void onDeepPauseApplication() {
			// TODO Auto-generated method stub

		}

		// end of lifecycle methods ////////////////////////////////////////////////////////

		// input

		@Override
		public Bundle onCommand(final String pAction, final int pX, final int pY, final int pZ, final Bundle pExtras, final boolean pResultRequested) {
			Logger.debug(" > ArgonGLEngine - onCommand(" + pAction + " " + pX + " " + pY + " " + pZ + " " + pExtras + " " + pResultRequested + ")" + ", linked: " + (linkedEngine == this));

			return super.onCommand(pAction, pX, pY, pZ, pExtras, pResultRequested);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (linkedEngine == this) {
				
				// il renderer è sempre ready, ho visto che metterla comunque qua blocca.
				if (gestureDetector != null) {
					gestureDetector.onTouchEvent(event);
				} else {
					Logger.debug("onTouchEvent but surface is not ready");
				}							
			}
		}

		// offsets from last onOffsetsChanged
		boolean offsetsConsumed = true;
		float xOffset = 0.0f;
		float yOffset = 0.0f;
		float xOffsetStep = 0.0f;
		float yOffsetStep = 0.0f;
		int xPixelOffset = 0;
		int yPixelOffset = 0;

		@Override
		public void onOffsetsChanged(final float xOffset, final float yOffset, final float xOffsetStep, final float yOffsetStep, final int xPixelOffset, final int yPixelOffset) {

			// it spawns too frequent on some devices - its annoying!
			// if (DEBUG)
			// Log.d(TAG, " > AndroidWallpaperEngine - onOffsetChanged(" + xOffset + " " + yOffset + " " + xOffsetStep + " "
			// + yOffsetStep + " " + xPixelOffset + " " + yPixelOffset + ") " + hashCode() + ", linkedApp: " + (linkedApp != null));

			this.offsetsConsumed = false;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.xOffsetStep = xOffsetStep;
			this.yOffsetStep = yOffsetStep;
			this.xPixelOffset = xPixelOffset;
			this.yPixelOffset = yPixelOffset;

			// can fail if linkedApp == null, so we repeat it in Engine.onResume
			// notifyOffsetsChanged();
			// if (!view.isContinuousRendering()) {
			// view.re r Gdx.graphics.requestRendering();
			// }

			// super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
			WallpaperManager.instance().setScreenOffset(xOffset);
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

		protected void notifyPreviewState() {
			// notify preview state to app listener
			if (linkedEngine == this) {
				final boolean currentPreviewState = linkedEngine.isPreview();

				synchronized (sync) {
					if (!isPreviewNotified || notifiedPreviewState != currentPreviewState) {
						notifiedPreviewState = currentPreviewState;
						isPreviewNotified = true;
						argon.setPreviewStatusChanged(true);
					}
				}
			}
		}
		// ---

	}

	// engine - end

	protected volatile Xenon4OpenGL argon = null; // can be accessed from GL render thread
	protected WallpaperView view = null;
	protected XenonGLRenderer renderer = null;
	// current format of surface (one GLSurfaceView is shared between all engines)
	protected int viewFormat;
	protected int viewWidth;
	protected int viewHeight;
	protected int engines = 0;
	protected int visibleEngines = 0;
	// engine currently associated with app instance, linked engine serves surface handler for GLSurfaceView
	protected volatile ArgonGLEngine linkedEngine = null; // can be accessed from GL render thread by getSurfaceHolder

	protected void setLinkedEngine(ArgonGLEngine linkedEngine) {
		synchronized (sync) {
			this.linkedEngine = linkedEngine;
		}
	}

	// if preview state notified ever
	protected volatile boolean isPreviewNotified = false;

	// the value of last preview state notified to app listener
	protected volatile boolean notifiedPreviewState = false;

	volatile int[] sync = new int[0];

	ReentrantLock surfaceLock = new ReentrantLock();

	/**
	 * Service is dying, and will not be used again. You have to finish execution off all living threads there or short after there, besides the new wallpaper service wouldn't be able to start.
	 */
	@Override
	public void onDestroy() {
		Logger.debug(" > AndroidLiveWallpaperService - onDestroy() " + hashCode());

		super.onDestroy(); // can call engine.onSurfaceDestroyed, must be before bellow code:

		if (argon != null) {
			argon.onDestroy();
			view.onDestroy();

			argon = null;
			view = null;
		}

	}

	public WindowManager getWindowManager() {
		return (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	}

	@Override
	public Engine onCreateEngine() {
		Logger.debug(">>>> onCreateEngine");
		return new ArgonGLEngine();
	}

	@Override
	public XenonGLRenderer createRenderer() {
		return new XenonGLDefaultRenderer();
	}

	@Override
	public void setRenderer(XenonGLRenderer renderer) {
		this.renderer=renderer;
	}

	@Override
	public void setGestureDetector(XenonGestureDetector gestureDetectorValue) {
		gestureDetector = gestureDetectorValue;

	}
	
	/**
	 * <p>
	 * Gestore delle gesture.
	 * </p>
	 */
	public XenonGestureDetector gestureDetector;
}