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

package com.abubusoft.xenon.android.surfaceview17;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.xenon.opengl.XenonGLRenderer;
import com.abubusoft.xenon.opengl.AsyncOperationManager;
import com.abubusoft.xenon.settings.XenonSettings;
import com.abubusoft.kripton.android.Logger;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * GLSurfaceView using EGl14 instead of EGL10
 *
 * @author Perraco Labs (August-2015)
 * @repository https://github.com/perracolabs/GLSurfaceViewEGL14
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ArgonGLSurfaceView17 extends ArgonGLView implements SurfaceHolder.Callback {

	private class DefaultContextFactory implements EGLContextFactory {
		private final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

		public DefaultContextFactory() {
			// Empty
		}

		@Override
		public EGLContext createContext(final EGLDisplay display, final EGLConfig config) {
			final int[] attrib_list = { this.EGL_CONTEXT_CLIENT_VERSION, ArgonGLSurfaceView17.this.mEGLContextClientVersion, EGL14.EGL_NONE };

			EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, ArgonGLSurfaceView17.this.mEGLContextClientVersion != 0 ? attrib_list : null, 0);
			
			XenonSettings settings= XenonBeanContext.getBean(XenonBeanType.XENON_SETTINGS);
			
			if (settings.openGL.asyncMode) {
				AsyncOperationManager.instance().init(context, display, config);
			} else {
				AsyncOperationManager.instance().init();
			}
			
			return context;
		}

		@Override
		public void destroyContext(final EGLDisplay display, final EGLContext context) {
			// distrugge il context secondario, usato per le operazioni
			// async
			XenonSettings settings= XenonBeanContext.getBean(XenonBeanType.XENON_SETTINGS);
			
			
			if (settings.openGL.asyncMode) {
				if (!AsyncOperationManager.instance().destroy()) {
					Logger.error("display: texture loader context: " + context);
					Logger.info("tid=" + Thread.currentThread().getId());
					Logger.error("eglDestroyContex %s", EGL14.eglGetError());
				}
			}

			
			if (EGL14.eglDestroyContext(display, context) == false) {
				Logger.error("display:" + display + " context: " + context);

				if (ArgonGLSurfaceView17.LOG_THREADS) {
					Logger.info("tid=" + Long.toString(Thread.currentThread().getId()));
				}

				EglHelper.throwEglException("eglDestroyContex", EGL14.eglGetError());
			}
			EGL14.eglReleaseThread();
		}
	}

	private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
		public DefaultWindowSurfaceFactory() {
			// Empty
		}

		@Override
		public EGLSurface createWindowSurface(final EGLDisplay display, final EGLConfig config, final Object nativeWindow) {
			EGLSurface result = null;

			try {
				final int[] surfaceAttribs = { EGL14.EGL_NONE };
				result = EGL14.eglCreateWindowSurface(display, config, nativeWindow, surfaceAttribs, 0);
			} catch (Throwable ex) {
				// This exception indicates that the surface flinger surface
				// is not valid. This can happen if the surface flinger surface has
				// been torn down, but the application has not yet been
				// notified via SurfaceHolder.Callback.surfaceDestroyed.
				// In theory the application should be notified first,
				// but in practice sometimes it is not. See b/4588890
				Logger.error("eglCreateWindowSurface call failed", ex);
			} finally {
				if (result == null) {
					try {
						// Hack to avoid pegged CPU bug
						Thread.sleep(10);
					} catch (InterruptedException t) {
						Logger.error("CPU was pegged");
					}
				}
			}

			return result;
		}

		@Override
		public void destroySurface(final EGLDisplay display, final EGLSurface surface) {
			if (EGL14.eglDestroySurface(display, surface) == false) {
				Logger.error("eglDestroySurface Failed");
			}
		}
	}
	/**
	 * An interface for customizing the eglCreateContext and eglDestroyContext calls.
	 * <p>
	 * This interface must be implemented by clients wishing to call {@link ArgonGLSurfaceView17#setEGLContextFactory(EGLContextFactory)}
	 */
	public interface EGLContextFactory {
		/**
		 * @param display
		 *            EGL Display
		 * @param eglConfig
		 *            EGL Configuration
		 * @return EGL Context
		 */
		EGLContext createContext(EGLDisplay display, EGLConfig eglConfig);

		/**
		 * @param egl
		 *            EGL object
		 * @param display
		 *            EGL Display
		 * @param context
		 *            EGL Context
		 */
		void destroyContext(EGLDisplay display, EGLContext context);
	}
	/**
	 * An EGL helper class.
	 */

	private static class EglHelper {
		protected static HashMap<String, Integer> cachedErrors = null;

		private static void cacheError(final String errorMessage) {
			try {
				if (EglHelper.cachedErrors == null) {
					EglHelper.cachedErrors = new HashMap<>();
				}

				int errorCount = 0;

				if (EglHelper.cachedErrors.containsKey(errorMessage) == true) {
					errorCount = EglHelper.cachedErrors.get(errorMessage).intValue();
					errorCount++;
				}

				if (EglHelper.cachedErrors.size() < 100) {
					EglHelper.cachedErrors.put(errorMessage, Integer.valueOf(errorCount));
				}
			} catch (Exception ex) {
				Logger.error("Failed to cache error.", ex);
			}
		}

		public static String formatEglError(final String function, final int error) {
			return function + " failed: " + ArgonGLSurfaceView17.getErrorString(error);
		}

		public static void logEglErrorAsWarning(final String function, final int error) {
			final String errorMessage = EglHelper.formatEglError(function, error);
			Logger.error(errorMessage);

			EglHelper.cacheError(errorMessage);
		}

		private static void throwEglException(final String function) {
			EglHelper.throwEglException(function, EGL14.eglGetError());
		}

		public static void throwEglException(final String function, final int error) {
			String message = EglHelper.formatEglError(function, error);

			if (ArgonGLSurfaceView17.LOG_THREADS) {
				Logger.error("EGL Exception. tid=" + Long.toString(Thread.currentThread().getId()) + " Error: " + message);
			}

			throw new RuntimeException(message);
		}

		EGLConfig mEglConfig;

		EGLContext mEglContext;

		EGLDisplay mEglDisplay;

		EGLSurface mEglSurface;

		private final WeakReference<ArgonGLSurfaceView17> mGLViewWeakRef;

		public EglHelper(final WeakReference<ArgonGLSurfaceView17> instanceWeakRef) {
			this.mGLViewWeakRef = instanceWeakRef;
		}

		/**
		 * Create an egl surface for the current SurfaceHolder surface. If a surface already exists, destroy it before creating the new surface.
		 *
		 * @return true if the surface was created successfully.
		 */
		public boolean createSurface() {
			if (ArgonGLSurfaceView17.LOG_EGL) {
				Logger.warn("tid=" + Long.toString(Thread.currentThread().getId()));
			}

			if (this.mEglDisplay == null) {
				throw new RuntimeException("eglDisplay not initialized");
			}

			if (this.mEglConfig == null) {
				throw new RuntimeException("mEglConfig not initialized");
			}

			// The window size has changed, so we need to create a new surface.

			this.destroySurfaceImp();

			// Create an EGL surface we can render into.

			ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

			if (view != null) {
				this.mEglSurface = view.mEGLWindowSurfaceFactory.createWindowSurface(this.mEglDisplay, this.mEglConfig, view.getHolder());
			} else {
				this.mEglSurface = null;
			}

			if (this.mEglSurface == null || this.mEglSurface == EGL14.EGL_NO_SURFACE) {
				int error = EGL14.eglGetError();

				if (error == EGL14.EGL_BAD_NATIVE_WINDOW) {
					Logger.error("createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
				}

				return false;
			}

			// Before we can issue GL commands, we need to make sure the context is current and bound to a surface.

			final boolean status = this.makeCurrent();

			return status;
		}

		public void destroySurface() {
			if (ArgonGLSurfaceView17.LOG_EGL) {
				Logger.warn("Destroying surface. tid=" + Long.toString(Thread.currentThread().getId()));
			}

			this.destroySurfaceImp();
		}

		private void destroySurfaceImp() {
			if (this.mEglSurface != null && this.mEglSurface != EGL14.EGL_NO_SURFACE) {
				EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
				ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

				if (view != null) {
					view.mEGLWindowSurfaceFactory.destroySurface(this.mEglDisplay, this.mEglSurface);
				}

				this.mEglSurface = null;
			}
		}
		/**
		 * xcesco: esco e distruggo il display
		 */
		public void disconnectDisplay() {
			if (mEglDisplay != null) {
				Logger.info("disconnectDisplay %s", mEglDisplay);
				// xcesco: non rimuoviamo il display	
				// rilasciamo tutte le informazioni OPENGL associate al thrread.
				EGL14.eglReleaseThread();
				EGL14.eglTerminate(this.mEglDisplay);
				mEglDisplay = null;
			}
		}
		public void finish() {
			if (ArgonGLSurfaceView17.LOG_EGL) {
				Logger.warn("Finishing. tid=" + Long.toString(Thread.currentThread().getId()));
			}

			if (this.mEglContext != null) {
				final ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

				if (view != null) {
					view.mEGLContextFactory.destroyContext(this.mEglDisplay, this.mEglContext);
				}

				this.mEglContext = null;
			}

			if (this.mEglDisplay != null ) {
				// xcesco: non rimuoviamo il display
				//EGL14.eglTerminate(this.mEglDisplay);
				//this.mEglDisplay = null;
			}
		}
		public boolean makeCurrent() {
			if (this.mEglDisplay == null || this.mEglSurface == null || this.mEglContext == null) {
				return false;
			}

			if (EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext) == false) {
				if (EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext) == false) {
					if (EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext) == false) {
						final int errorCode = EGL14.eglGetError();

						// Could not make the context current, probably because the underlying SurfaceView surface has been destroyed.
						EglHelper.logEglErrorAsWarning("eglMakeCurrent", errorCode);
						return false;
					}
				}
			}

			return true;
		}
		/**
		 * Initialize EGL for a given configuration specification
		 */
		public void start() {
			if (ArgonGLSurfaceView17.LOG_EGL) {
				Logger.warn("start() tid=".concat(Long.toString(Thread.currentThread().getId())));
			}

			/*
			 * Get to the default display.
			 */
			if (mEglDisplay == null) {
				this.mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

				if (this.mEglDisplay == EGL14.EGL_NO_DISPLAY) {
					throw new RuntimeException("eglGetDisplay failed");
				}

				/*
				 * We can now initialize EGL for that display
				 */
				int[] version = new int[2];
				if (EGL14.eglInitialize(this.mEglDisplay, version, 0, version, 1) == false) {
					throw new RuntimeException("eglInitialize failed");
				}
			}

			ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

			if (view == null) {
				this.mEglConfig = null;
				this.mEglContext = null;
			} else {
				//this.mEglConfig = EGL14Config.chooseConfig(this.mEglDisplay, view.mRecordable);
				this.mEglConfig =ConfigChooserHelper17.listConfig(mEglDisplay)[0];
				

				/*
				 * Create an EGL context. We want to do this as rarely as we can, because an EGL context is a somewhat heavy object.
				 */
				this.mEglContext = view.mEGLContextFactory.createContext(this.mEglDisplay, this.mEglConfig);
			}

			if (this.mEglContext == null || this.mEglContext == EGL14.EGL_NO_CONTEXT) {
				this.mEglContext = null;
				EglHelper.throwEglException("createContext");
			}

			if (ArgonGLSurfaceView17.LOG_EGL) {
				Logger.warn("createContext " + this.mEglContext + " tid=" + Long.toString(Thread.currentThread().getId()));
			}

			this.mEglSurface = null;
		}

		/**
		 * Display the current render surface.
		 *
		 * @return the EGL error code from eglSwapBuffers.
		 */
		public int swap() {
			if (this.mEglDisplay == null) {
				final int error = EGL14.eglGetError();
				return error != 0 ? error : EGL14.EGL_BAD_DISPLAY;
			}

			if (this.mEglSurface == null) {
				final int error = EGL14.eglGetError();
				return error != 0 ? error : EGL14.EGL_BAD_SURFACE;
			}

			if (EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface) == false) {
				return EGL14.eglGetError();
			}

			return EGL14.EGL_SUCCESS;
		}
	}
	/**
	 * An interface for customizing the eglCreateWindowSurface and eglDestroySurface calls.
	 * <p>
	 * This interface must be implemented by clients wishing to call {@link ArgonGLSurfaceView17#setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory)}
	 */
	public interface EGLWindowSurfaceFactory {
		/**
		 * @param egl
		 *            EGL object
		 * @param display
		 *            EGL Display
		 * @param config
		 *            EGL Configuration
		 * @param nativeWindow
		 *            Native window
		 * @return EGL Context or null if the surface cannot be constructed
		 */
		EGLSurface createWindowSurface(EGLDisplay display, EGLConfig config, Object nativeWindow);

		/**
		 * @param egl
		 *            EGL object
		 * @param display
		 *            EGL Display
		 * @param surface
		 *            Surface to be destroyed
		 */
		void destroySurface(EGLDisplay display, EGLSurface surface);
	}
	/**
	 * A generic GL Thread. Takes care of initializing EGL and GL. Delegates to a IRendererEGL14 instance to do the actual drawing. Can be configured to render continuously or on request.
	 *
	 * All potentially blocking synchronization is done through the sGLThreadManager object. This avoids multiple-lock ordering issues.
	 *
	 */
	class GLThread extends Thread {

		private EglHelper mEglHelper;

		public final ArrayList<Runnable> mEventQueue = new ArrayList<>();

		protected boolean mExited;

		private boolean mFinishedCreatingEglSurface;

		/**
		 * Set once at thread construction time, nulled out when the parent view is garbage called. This weak reference allows the GLSurfaceViewEGL14 to be garbage collected while the GLThread is still alive.
		 */
		private final WeakReference<ArgonGLSurfaceView17> mGLViewWeakRef;

		private boolean mHasSurface;

		private boolean mHaveEglContext;

		private boolean mHaveEglSurface;

		private int mHeight;

		private boolean mPaused;

		private boolean mRenderComplete;

		private int mRenderMode;

		private boolean mRequestPaused;

		private boolean mRequestRender;

		// Once the thread is started, all accesses to the following member
		// variables are protected by the sGLThreadManager monitor
		private boolean mShouldExit;

		private boolean mShouldReleaseEglContext;

		private boolean mSizeChanged = true;

		private boolean mSurfaceIsBad;

		private boolean mWaitingForSurface;

		private int mWidth;
		GLThread(final WeakReference<ArgonGLSurfaceView17> instanceWeakRef) {
			super();
			this.mWidth = 0;
			this.mHeight = 0;
			this.mRequestRender = true;
			this.mRenderMode = XenonGLRenderer.RENDERMODE_CONTINUOUSLY;
			this.mGLViewWeakRef = instanceWeakRef;
		}
		public boolean ableToDraw() {
			return this.mHaveEglContext && this.mHaveEglSurface && this.readyToDraw();
		}
		/**
		 * xcesco: disconnette il display al momento di uscire
		 */
		private void disconnectDisplay() {
			mEglHelper.disconnectDisplay();
		}
		public int getRenderMode() {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				return this.mRenderMode;
			}
		}
		private void guardedRun() throws InterruptedException {
			this.mEglHelper = new EglHelper(this.mGLViewWeakRef);
			this.mHaveEglContext = false;
			this.mHaveEglSurface = false;

			try {
				boolean createEglContext = false;
				boolean createEglSurface = false;
				boolean createGlInterface = false;
				boolean lostEglContext = false;
				boolean sizeChanged = false;
				boolean wantRenderNotification = false;
				boolean doRenderNotification = false;
				boolean askedToReleaseEglContext = false;
				int w = 0;
				int h = 0;
				Runnable event = null;

				while (true) {
					synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
						while (true) {
							if (this.mShouldExit == true) {
								return;
							}

							if (this.mEventQueue.isEmpty() == false) {
								event = this.mEventQueue.remove(0);
								break;
							}

							// Update the pause state.
							boolean pausing = false;

							if (this.mPaused != this.mRequestPaused) {
								pausing = this.mRequestPaused;
								this.mPaused = this.mRequestPaused;
								ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

								if (ArgonGLSurfaceView17.LOG_PAUSE_RESUME) {
									Logger.info("mPaused is now " + Boolean.toString(this.mPaused) + ". tid=" + Long.toString(this.getId()));
								}
							}

							// Do we need to give up the EGL context?
							if (this.mShouldReleaseEglContext == true) {
								if (ArgonGLSurfaceView17.LOG_SURFACE) {
									Logger.info("Releasing EGL context because asked to tid=" + Long.toString(this.getId()));
								}

								this.stopEglSurfaceLocked();
								this.stopEglContextLocked();
								this.mShouldReleaseEglContext = false;
								askedToReleaseEglContext = true;
							}

							// Have we lost the EGL context?
							if (lostEglContext == true) {
								this.stopEglSurfaceLocked();
								this.stopEglContextLocked();
								lostEglContext = false;
							}

							// When pausing, release the EGL surface:
							if (pausing == true && this.mHaveEglSurface == true) {
								if (ArgonGLSurfaceView17.LOG_SURFACE) {
									Logger.info("Releasing EGL surface because paused tid=" + Long.toString(this.getId()));
								}

								this.stopEglSurfaceLocked();
							}

							// When pausing, optionally release the EGL Context:
							if (pausing == true && this.mHaveEglContext == true) {
								ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();
								boolean preserveEglContextOnPause = view == null ? false : view.mPreserveEGLContextOnPause;

								if (preserveEglContextOnPause == false || ArgonGLSurfaceView17.sGLThreadManager.shouldReleaseEGLContextWhenPausing()) {
									this.stopEglContextLocked();

									if (ArgonGLSurfaceView17.LOG_SURFACE) {
										Logger.info("Releasing EGL context because paused tid=" + Long.toString(this.getId()));
									}
								}
							}

							// Have we lost the SurfaceView surface?
							if ((this.mHasSurface == false) && (this.mWaitingForSurface == false)) {
								if (ArgonGLSurfaceView17.LOG_SURFACE) {
									Logger.info( "Noticed surfaceView surface lost tid=" + Long.toString(this.getId()));
								}

								if (this.mHaveEglSurface == true) {
									this.stopEglSurfaceLocked();
								}

								this.mWaitingForSurface = true;
								this.mSurfaceIsBad = false;
								ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
							}

							// Have we acquired the surface view surface?
							if (this.mHasSurface == true && this.mWaitingForSurface == true) {
								if (ArgonGLSurfaceView17.LOG_SURFACE) {
									Logger.info("Noticed surfaceView surface acquired tid=" + Long.toString(this.getId()));
								}

								this.mWaitingForSurface = false;
								ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
							}

							if (doRenderNotification == true) {
								if (ArgonGLSurfaceView17.LOG_SURFACE) {
									Logger.info("Sending render notification tid=" + Long.toString(this.getId()));
								}

								wantRenderNotification = false;
								doRenderNotification = false;
								this.mRenderComplete = true;
								ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
							}

							// Ready to draw?
							if (this.readyToDraw() == true) {

								// If we don't have an EGL context, try to acquire one.
								if (this.mHaveEglContext == false) {
									if (askedToReleaseEglContext == true) {
										askedToReleaseEglContext = false;
									} else if (ArgonGLSurfaceView17.sGLThreadManager.tryAcquireEglContextLocked(this)) {
										try {
											this.mEglHelper.start();
										} catch (RuntimeException t) {
											ArgonGLSurfaceView17.sGLThreadManager.releaseEglContextLocked(this);
											throw t;
										}

										this.mHaveEglContext = true;
										createEglContext = true;

										ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
									}
								}

								if (this.mHaveEglContext == true && this.mHaveEglSurface == false) {
									this.mHaveEglSurface = true;
									createEglSurface = true;
									createGlInterface = true;
									sizeChanged = true;
								}

								if (this.mHaveEglSurface == true) {
									if (this.mSizeChanged == true) {
										sizeChanged = true;
										w = this.mWidth;
										h = this.mHeight;
										wantRenderNotification = true;

										if (ArgonGLSurfaceView17.LOG_SURFACE) {
											Logger.info("Noticing that we want render notification tid=" + Long.toString(this.getId()));
										}

										// Destroy and recreate the EGL surface.
										createEglSurface = true;

										this.mSizeChanged = false;
									}

									this.mRequestRender = false;
									ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
									break;
								}
							}

							// By design, this is the only place in a GLThread thread where we wait().
							if (ArgonGLSurfaceView17.LOG_THREADS_WAIT) {
								Logger.info(
										"waiting tid=" + Long.toString(this.getId()) + " mHaveEglContext: " + Boolean.toString(this.mHaveEglContext) + " mHaveEglSurface: " + Boolean.toString(this.mHaveEglSurface)
												+ " mFinishedCreatingEglSurface: " + Boolean.toString(this.mFinishedCreatingEglSurface) + " mPaused: " + Boolean.toString(this.mPaused) + " mHasSurface: " + Boolean.toString(this.mHasSurface)
												+ " mSurfaceIsBad: " + Boolean.toString(this.mSurfaceIsBad) + " mWaitingForSurface: " + Boolean.toString(this.mWaitingForSurface) + " mWidth: " + Integer.toString(this.mWidth) + " mHeight: "
												+ Integer.toString(this.mHeight) + " mRequestRender: " + Boolean.toString(this.mRequestRender) + " mRenderMode: " + Integer.toString(this.mRenderMode));
							}

							ArgonGLSurfaceView17.sGLThreadManager.wait();
						}
					} // end of synchronized(sGLThreadManager)

					if (event != null) {
						event.run();
						event = null;
						continue;
					}

					if (createEglSurface == true) {
						if (ArgonGLSurfaceView17.LOG_SURFACE) {
							Logger.warn("egl createSurface");
						}

						if (this.mEglHelper.createSurface() == true) {
							synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
								this.mFinishedCreatingEglSurface = true;
								ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
							}
						} else {
							synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
								this.mFinishedCreatingEglSurface = false;
								this.mSurfaceIsBad = false;
								ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
							}

							continue;
						}

						createEglSurface = false;
					}

					if (createGlInterface == true) {
						createGlInterface = false;
					}

					if (createEglContext == true) {
						if (ArgonGLSurfaceView17.LOG_RENDERER) {
							Logger.warn("onSurfaceCreated");
						}

						ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

						if (view != null) {
							view.mRenderer.onSurfaceCreated();
						}

						createEglContext = false;
					}

					if (sizeChanged == true) {
						if (ArgonGLSurfaceView17.LOG_RENDERER) {
							Logger.warn("onSurfaceChanged(" + Integer.toString(w) + ", " + Integer.toString(h) + ")");
						}

						ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

						if (view != null) {
							view.mRenderer.onSurfaceChanged(w, h);
						}

						sizeChanged = false;
					}

					if (ArgonGLSurfaceView17.LOG_RENDERER_DRAW_FRAME) {
						Logger.warn("onDrawFrame tid=" + Long.toString(this.getId()));
					}
					{
						ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

						if (view != null) {
							view.mRenderer.onDrawFrame();
						}
					}

					int swapError = this.mEglHelper.swap();
					// Thread.sleep(2);

					switch (swapError) {
					case EGL14.EGL_SUCCESS:
						break;

					case EGL14.EGL_CONTEXT_LOST:

						if (ArgonGLSurfaceView17.LOG_SURFACE) {
							Logger.info("egl context lost tid=" + Long.toString(this.getId()));
						}

						lostEglContext = true;
						break;

					default:

						// Other errors typically mean that the current surface is bad,
						// probably because the SurfaceView surface has been destroyed,
						// but we haven't been notified yet.
						// Log the error to help developers understand why rendering stopped.
						EglHelper.logEglErrorAsWarning("eglSwapBuffers", swapError);

						synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
							this.mSurfaceIsBad = true;
							ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
						}
						break;
					}

					if (wantRenderNotification == true) {
						doRenderNotification = true;
					}
				}
			} finally {
				/*
				 * clean-up everything...
				 */
				synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
					this.stopEglSurfaceLocked();
					this.stopEglContextLocked();

					if (destroyDisplayOnExit) {
						disconnectDisplay();
					}
				}
			}
		}
		public void onPause() {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				if (ArgonGLSurfaceView17.LOG_PAUSE_RESUME) {
					Logger.info("onPause tid=" + Long.toString(this.getId()));
				}

				this.mRequestPaused = true;
				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

				while ((this.mExited == false) && (this.mPaused == false)) {
					if (ArgonGLSurfaceView17.LOG_PAUSE_RESUME) {
						Logger.info("onPause waiting for mPaused.");
					}

					try {
						ArgonGLSurfaceView17.sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		public void onResume() {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				if (ArgonGLSurfaceView17.LOG_PAUSE_RESUME) {
					Logger.info("onResume tid=" + Long.toString(this.getId()));
				}

				this.mRequestPaused = false;
				this.mRequestRender = true;
				this.mRenderComplete = false;
				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

				while ((this.mExited == false) && this.mPaused == true && (this.mRenderComplete == false)) {
					if (ArgonGLSurfaceView17.LOG_PAUSE_RESUME) {
						Logger.info("onResume waiting for !mPaused.");
					}

					try {
						ArgonGLSurfaceView17.sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		public void onWindowResize(final int w, final int h) {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				this.mWidth = w;
				this.mHeight = h;
				this.mSizeChanged = true;
				this.mRequestRender = true;
				this.mRenderComplete = false;

				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

				// Wait for thread to react to resize and render a frame
				while (this.mExited == false && this.mPaused == false && this.mRenderComplete == false && this.ableToDraw() == true) {
					if (ArgonGLSurfaceView17.LOG_SURFACE) {
						Logger.info("onWindowResize waiting for render complete from tid=" + Long.toString(this.getId()));
					}

					try {
						ArgonGLSurfaceView17.sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		/**
		 * Queue an "event" to be run on the GL rendering thread.
		 *
		 * @param runnable
		 *            the runnable to be run on the GL rendering thread.
		 */
		public void queueEvent(final Runnable runnable) {
			if (runnable == null) {
				throw new IllegalArgumentException("'runnable' must not be null");
			}

			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				this.mEventQueue.add(runnable);
				Logger.debug("Queued events: ".concat(Integer.toString(this.mEventQueue.size())));

				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
			}
		}
		private boolean readyToDraw() {
			return (!this.mPaused) && this.mHasSurface && (!this.mSurfaceIsBad) && (this.mWidth > 0) && (this.mHeight > 0) && (this.mRequestRender || (this.mRenderMode == XenonGLRenderer.RENDERMODE_CONTINUOUSLY));
		}
		public void requestExitAndWait() {
			// don't call this from GLThread thread or it is a guaranteed deadlock!
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				this.mShouldExit = true;
				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

				while (this.mExited == false) {
					try {
						ArgonGLSurfaceView17.sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		public void requestReleaseEglContextLocked() {
			this.mShouldReleaseEglContext = true;
			ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
		}
		public void requestRender() {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				if (this.mRequestPaused == false) {
					this.mRequestRender = true;
					ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
				}
			}
		}
		@Override
		public void run() {
			this.setName("GLThread " + Long.toString(this.getId()));

			if (ArgonGLSurfaceView17.LOG_THREADS) {
				Logger.info("starting tid=" + Long.toString(this.getId()));
			}

			try {
				this.guardedRun();
			} catch (InterruptedException e) {
				// fall thru and exit normally
			} finally {
				ArgonGLSurfaceView17.sGLThreadManager.threadExiting(this);
			}
		}
		public void setRenderMode(final int renderMode) {
			if (!((XenonGLRenderer.RENDERMODE_WHEN_DIRTY <= renderMode) && (renderMode <= XenonGLRenderer.RENDERMODE_CONTINUOUSLY))) {
				throw new IllegalArgumentException("renderMode");
			}
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				this.mRenderMode = renderMode;
				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();
			}
		}
		/*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
		private void stopEglContextLocked() {
			final ArgonGLSurfaceView17 view = this.mGLViewWeakRef.get();

			if (view != null && view.mRenderer != null) {
				view.mRenderer.onDestroy();
			}

			if (this.mHaveEglContext) {
				this.mEglHelper.finish();
				this.mHaveEglContext = false;
				ArgonGLSurfaceView17.sGLThreadManager.releaseEglContextLocked(this);
			}
		}
		/*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
		private void stopEglSurfaceLocked() {
			if (this.mHaveEglSurface) {
				this.mHaveEglSurface = false;
				this.mEglHelper.destroySurface();
			}
		}

		// End of member variables protected by the sGLThreadManager monitor.

		public void surfaceCreated() {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				if (ArgonGLSurfaceView17.LOG_THREADS) {
					Logger.info("surfaceCreated tid=" + Long.toString(this.getId()));
				}

				this.mHasSurface = true;
				this.mFinishedCreatingEglSurface = false;
				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

				while ((this.mWaitingForSurface) && (!this.mFinishedCreatingEglSurface) && (!this.mExited)) {
					try {
						ArgonGLSurfaceView17.sGLThreadManager.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void surfaceDestroyed() {
			synchronized (ArgonGLSurfaceView17.sGLThreadManager) {
				if (ArgonGLSurfaceView17.LOG_THREADS) {
					Logger.info("surfaceDestroyed tid=" + Long.toString(this.getId()));
				}

				this.mHasSurface = false;
				ArgonGLSurfaceView17.sGLThreadManager.notifyAll();

				while ((this.mWaitingForSurface == false) && (this.mExited == false)) {
					try {
						ArgonGLSurfaceView17.sGLThreadManager.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}
	private static class GLThreadManager {
		private GLThread mEglOwner;

		/**
		 * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides support for hardware-accelerated views, therefore multiple EGL contexts are supported on all Android 3.0+ EGL drivers.
		 */
		private boolean mLimitedGLESContexts;

		public GLThreadManager() {
			Logger.info("GLThreadManager instance created");
		}

		/*
		 * Releases the EGL context. Requires that we are already in the sGLThreadManager monitor when this is called.
		 */
		public void releaseEglContextLocked(final GLThread thread) {
			if (this.mEglOwner == thread) {
				this.mEglOwner = null;
			}

			this.notifyAll();
		}

		public synchronized boolean shouldReleaseEGLContextWhenPausing() {
			// Release the EGL context when pausing even if
			// the hardware supports multiple EGL contexts.
			// Otherwise the device could run out of EGL contexts.
			return this.mLimitedGLESContexts;
		}

		public synchronized void threadExiting(final GLThread thread) {
			if (ArgonGLSurfaceView17.LOG_THREADS) {
				Logger.info("Exiting tid=" + Long.toString(thread.getId()));
			}

			thread.mExited = true;

			if (this.mEglOwner == thread) {
				this.mEglOwner = null;
			}

			this.notifyAll();
		}
		/*
		 * Tries once to acquire the right to use an EGL context. Does not block. Requires that we are already in the sGLThreadManager monitor when this is called.
		 * 
		 * @return true if the right to use an EGL context was acquired.
		 */
		public boolean tryAcquireEglContextLocked(final GLThread thread) {
			if (this.mEglOwner == thread || this.mEglOwner == null) {
				this.mEglOwner = thread;
				this.notifyAll();
			}

			return true;
		}
	}

	/**
	 * Check glError() after every GL call and throw an exception if glError indicates that an error has occurred. This can be used to help track down which OpenGL ES call is causing an error.
	 *
	 * see getDebugFlags, setDebugFlags
	 */
	private final static int DEBUG_CHECK_GL_ERROR = 1;

	/**
	 * Log GL calls to the system log at "verbose" level with tag "GLSurfaceViewEGL14".
	 *
	 * see #getDebugFlags, setDebugFlags
	 */
	private final static int DEBUG_LOG_GL_CALLS = 2;

	private final static boolean LOG_ATTACH_DETACH = false;

	private final static boolean LOG_EGL = false;

	private final static boolean LOG_PAUSE_RESUME = false;
	private final static boolean LOG_RENDERER = false;

	private final static boolean LOG_RENDERER_DRAW_FRAME = false;

	private final static boolean LOG_SURFACE = false;

	private final static boolean LOG_THREADS = false;

	private final static boolean LOG_THREADS_WAIT = false;

	protected static final GLThreadManager sGLThreadManager = new GLThreadManager();

	/**
	 * Returns any cached error as a log
	 *
	 * @return Error log
	 */
	public static String getCachedErrorsLog() {
		String log = "<NO ERRORS>";

		try {
			if (EglHelper.cachedErrors != null && EglHelper.cachedErrors.size() > 0) {
				log = "";

				for (Map.Entry<String, Integer> entry : EglHelper.cachedErrors.entrySet()) {
					log = log.concat("\n>>").concat(entry.getKey()).concat(" (").concat(entry.getValue().toString()).concat(")");

					if (log.length() > 300) {
						log = log.concat("<<<<<Log Too Large>>>>>");
						break;
					}
				}
			}
		} catch (Exception ex) {
			Logger.error("Failed to cache error.", ex);
			log = "Failed to cache error.";
		}

		return log;
	}

	/**
	 * Gets a GL Error string
	 *
	 * @param error
	 *            Error to be resolve
	 * @return Resolved error string
	 */
	protected static String getErrorString(final int error) {
		Thread.dumpStack();

		switch (error) {
		case EGL14.EGL_SUCCESS:
			return "EGL_SUCCESS";
		case EGL14.EGL_NOT_INITIALIZED:
			return "EGL_NOT_INITIALIZED";
		case EGL14.EGL_BAD_ACCESS:
			return "EGL_BAD_ACCESS";
		case EGL14.EGL_BAD_ALLOC:
			return "EGL_BAD_ALLOC";
		case EGL14.EGL_BAD_ATTRIBUTE:
			return "EGL_BAD_ATTRIBUTE";
		case EGL14.EGL_BAD_CONFIG:
			return "EGL_BAD_CONFIG";
		case EGL14.EGL_BAD_CONTEXT:
			return "EGL_BAD_CONTEXT";
		case EGL14.EGL_BAD_CURRENT_SURFACE:
			return "EGL_BAD_CURRENT_SURFACE";
		case EGL14.EGL_BAD_DISPLAY:
			return "EGL_BAD_DISPLAY";
		case EGL14.EGL_BAD_MATCH:
			return "EGL_BAD_MATCH";
		case EGL14.EGL_BAD_NATIVE_PIXMAP:
			return "EGL_BAD_NATIVE_PIXMAP";
		case EGL14.EGL_BAD_NATIVE_WINDOW:
			return "EGL_BAD_NATIVE_WINDOW";
		case EGL14.EGL_BAD_PARAMETER:
			return "EGL_BAD_PARAMETER";
		case EGL14.EGL_BAD_SURFACE:
			return "EGL_BAD_SURFACE";
		case EGL14.EGL_CONTEXT_LOST:
			return "EGL_CONTEXT_LOST";
		default:
			return "0x" + Integer.toHexString(error);
		}
	}

	/**
	 * serve per il nexus 9 (e mipad)
	 */
	protected boolean destroyDisplayOnExit=false;

	protected int mDebugFlags;

	private boolean mDetached;

	//protected EGL14ConfigChooser mEGLConfigChooser;

	protected int mEGLContextClientVersion;
	
	protected EGLContextFactory mEGLContextFactory;

	protected EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;

	private GLThread mGLThread;

	protected boolean mPreserveEGLContextOnPause;

	protected boolean mRecordable;

	private final WeakReference<ArgonGLSurfaceView17> mThisWeakRef = new WeakReference<>(this);

	/**
	 * Standard View constructor. In order to render something, you must call {@link #setRenderer} to register a renderer.
	 *
	 * @param context
	 *            Context used for operations
	 */
	public ArgonGLSurfaceView17(final Context context) {
		super(context);

		this.setWillNotDraw(false);

		this.init(context);
	}

	/**
	 * Standard View constructor. In order to render something, you must call {@link #setRenderer} to register a renderer.
	 *
	 * @param context
	 *            Context used for operations
	 * @param attrs
	 *            Attributes
	 */
	public ArgonGLSurfaceView17(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.setWillNotDraw(false);

		this.init(context);
	}

	private void checkRenderThreadState() {
		if (this.mGLThread != null) {
			throw new IllegalStateException("setRenderer has already been called for this instance.");
		}
	}

	/**
	 * @see Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			if (this.mGLThread != null) {
				// GLThread may still be running if this view was never attached to a window.
				this.mGLThread.requestExitAndWait();
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * Get the current value of the debug flags.
	 *
	 * @return the current value of the debug flags.
	 */
	public int getDebugFlags() {
		return this.mDebugFlags;
	}

	// ----------------------------------------------------------------------

	/**
	 * @return true if the EGL context will be preserved when paused
	 */
	public boolean getPreserveEGLContextOnPause() {
		return this.mPreserveEGLContextOnPause;
	}

	/**
	 * Get the current rendering mode. May be called from any thread. Must not be called before a renderer has been set.
	 *
	 * @return the current rendering mode. see RENDERMODE_CONTINUOUSLY see RENDERMODE_WHEN_DIRTY
	 */
	public int getRenderMode() {
		return this.mGLThread.getRenderMode();
	}

	/**
	 * Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed
	 */
	private void hookCallbacks() {
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);
	}

	private void init(final Context context) {
		// Request an 2.0 OpenGL ES compatible context
		this.setEGLContextClientVersion(2);

		if ((context.getApplicationContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			this.setDebugFlags(ArgonGLSurfaceView17.DEBUG_LOG_GL_CALLS | ArgonGLSurfaceView17.DEBUG_CHECK_GL_ERROR);
		}

		this.hookCallbacks();
	}

	/**
	 * This method is used as part of the View class and is not normally called or sub-classed by clients of Control.
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (ArgonGLSurfaceView17.LOG_ATTACH_DETACH) {
			Logger.debug("onAttachedToWindow reattach: ".concat(Boolean.toString(this.mDetached)));
		}

		if (this.mDetached && (this.mRenderer != null)) {
			int renderMode = XenonGLRenderer.RENDERMODE_CONTINUOUSLY;

			if (this.mGLThread != null) {
				renderMode = this.mGLThread.getRenderMode();
			}

			this.mGLThread = new GLThread(this.mThisWeakRef);

			if (renderMode != XenonGLRenderer.RENDERMODE_CONTINUOUSLY) {
				this.mGLThread.setRenderMode(renderMode);
			}

			this.mGLThread.start();
		}

		this.mDetached = false;
	}

	/**
	 * This method is used as part of the View class and is not normally called or sub-classed by clients of the Control. Must not be called before a renderer has been set.
	 */
	@Override
	protected void onDetachedFromWindow() {
		if (ArgonGLSurfaceView17.LOG_ATTACH_DETACH) {
			Logger.debug( "Detaching from window.");
		}

		if (this.mGLThread != null) {
			this.mGLThread.requestExitAndWait();
		}

		this.mDetached = true;
		super.onDetachedFromWindow();
	}

	/**
	 * Inform the view that the activity is paused. The owner of this view must call this method when the activity is paused. Calling this method will pause the rendering thread. Must not be called before a renderer has been set.
	 */
	public void onPause() {
		if (this.mGLThread == null || this.mRenderer == null) {
			return;
		}

		this.mGLThread.onPause();
		mRenderer.onPause();
	}

	/**
	 * Inform the view that the activity is resumed. The owner of this view must call this method when the activity is resumed. Calling this method will recreate the OpenGL display and resume the rendering thread. Must not be called before
	 * a renderer has been set.
	 */
	public void onResume() {
		if (this.mGLThread == null || this.mRenderer == null) {
			return;
		}

		//this.hookCallbacks();
		this.mGLThread.onResume();
		mRenderer.onResume();
	}
	
	/**
	 * Queue a runnable to be run on the GL rendering thread.
	 * <p>
	 * This can be used to communicate with the Renderer on the rendering thread.
	 * <p>
	 * Must not be called before a renderer has been set.
	 *
	 * @param runnable
	 *            The runnable to be run on the GL rendering thread.
	 */
	public void queueEvent(final Runnable runnable) {
		this.mGLThread.queueEvent(runnable);
	}

	/**
	 * Request that the renderer render a frame. This method is typically used when the render mode has been set to RENDERMODE_WHEN_DIRTY, so that frames are only rendered on demand. May be called from any thread. Must not be called before
	 * a renderer has been set.
	 */
	public void requestRender() {
		this.mGLThread.requestRender();
	}

	/**
	 * Set the debug flags to a new value. The value is constructed by OR-together zero or more of the DEBUG_CHECK_* constants. The debug flags take effect whenever a surface is created. The default value is zero.
	 *
	 * @param debugFlags
	 *            the new debug flags see DEBUG_CHECK_GL_ERROR see DEBUG_LOG_GL_CALLS
	 */
	public void setDebugFlags(final int debugFlags) {
		this.mDebugFlags = debugFlags;
	}

	/**
	 * Inform the default EGLContextFactory and default EGLConfigChooser which EGLContext client version to pick.
	 * <p>
	 * Use this method to create an OpenGL ES 2.0-compatible context. Example:
	 *
	 * <pre class="prettyprint">
	 * public MyView(Context context) {
	 * 	super(context);
	 * 	setEGLContextClientVersion(2); // Pick an OpenGL ES 2.0 context.
	 * 	setRenderer(new MyRenderer());
	 * }
	 * </pre>
	 * <p>
	 * Note: Activities which require OpenGL ES 2.0 should indicate this by setting @lt;uses-feature android:glEsVersion="0x00020000" /> in the activity's AndroidManifest.xml file.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(RendererEGL14)} is called.
	 * <p>
	 *
	 * @param version
	 *            The EGLContext client version to choose. Use 2 for OpenGL ES 2.0
	 */
	public void setEGLContextClientVersion(final int version) {
		this.checkRenderThreadState();
		this.mEGLContextClientVersion = version;
	}
	/**
	 * Install a custom EGLContextFactory.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(RendererEGL14)} is called.
	 * <p>
	 * If this method is not called, then by default a context will be created with no shared context and with a null attribute list.
	 *
	 * @param factory
	 *            Factory context
	 */
	public void setEGLContextFactory(final EGLContextFactory factory) {
		this.checkRenderThreadState();
		this.mEGLContextFactory = factory;
	}
	/**
	 * Install a custom EGLWindowSurfaceFactory.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(RendererEGL14)} is called.
	 * <p>
	 * If this method is not called, then by default a window surface will be created with a null attribute list.
	 *
	 * @param factory
	 *            Factory context
	 */
	public void setEGLWindowSurfaceFactory(final EGLWindowSurfaceFactory factory) {
		this.checkRenderThreadState();
		this.mEGLWindowSurfaceFactory = factory;
	}
	/**
	 * Control whether the EGL context is preserved when the GLSurfaceViewEGL14 is paused and resumed.
	 * <p>
	 * If set to true, then the EGL context may be preserved when the GLSurfaceViewEGL14 is paused. Whether the EGL context is actually preserved or not depends upon whether the Android device that the program is running on can support an
	 * arbitrary number of EGL contexts or not. Devices that can only support a limited number of EGL contexts must release the EGL context in order to allow multiple applications to share the GPU.
	 * <p>
	 * If set to false, the EGL context will be released when the GLSurfaceViewEGL14 is paused, and recreated when the GLSurfaceViewEGL14 is resumed.
	 * <p>
	 *
	 * The default is false.
	 *
	 * @param preserveOnPause
	 *            preserve the EGL context when paused
	 */
	public void setPreserveEGLContextOnPause(final boolean preserveOnPause) {
		this.mPreserveEGLContextOnPause = preserveOnPause;
	}
	/**
	 * Sets the surface to use the EGL_RECORDABLE_ANDROID flag
	 * <p>
	 * To take effect must be called before than setRenderer()
	 *
	 * @param recordable
	 *            True to set the recordable flag
	 */
	public void setRecordable(final boolean recordable) {
		this.mRecordable = recordable;
		Logger.info("Updated recordable flag. State: ".concat(Boolean.toString(recordable)));
	}
	/**
	 * Set the renderer associated with this view. Also starts the thread that will call the renderer, which in turn causes the rendering to start.
	 * <p>
	 * This method should be called once and only once in the life-cycle of a GLSurfaceViewEGL14. The following GLSurfaceViewEGL14 methods can only be called <em>after</em> setRenderer is called:
	 * <ul>
	 * <li>{@link #getRenderMode()}
	 * <li>{@link #onPause()}
	 * <li>{@link #onResume()}
	 * <li>{@link #queueEvent(Runnable)}
	 * <li>{@link #requestRender()}
	 * <li>{@link #setRenderMode(int)}
	 * </ul>
	 *
	 * @param renderer
	 *            the renderer to use to perform OpenGL drawing
	 */
	public void setRenderer(XenonGLRenderer renderer) {
		this.checkRenderThreadState();

		if (this.mEGLContextFactory == null) {
			this.mEGLContextFactory = new DefaultContextFactory();
		}

		if (this.mEGLWindowSurfaceFactory == null) {
			this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
		}

		//if (this.mEGLConfigChooser==null)
		{
			//this.mEGLConfigChooser=new EGL14DefaultConfigChooser();
		}
		
		this.mRenderer = renderer;
		this.mGLThread = new GLThread(this.mThisWeakRef);
		this.mGLThread.start();
	}
	/**
	 * Set the rendering mode. When renderMode is RENDERMODE_CONTINUOUSLY, the renderer is called repeatedly to re-render the scene. When renderMode is RENDERMODE_WHEN_DIRTY, the renderer only rendered when the surface is created, or when
	 * {@link #requestRender} is called. Defaults to RENDERMODE_CONTINUOUSLY.
	 * <p>
	 * Using RENDERMODE_WHEN_DIRTY can improve battery life and overall system performance by allowing the GPU and CPU to idle when the view does not need to be updated.
	 * <p>
	 * This method can only be called after {@link #setRenderer(RendererEGL14)}
	 *
	 * @param renderMode
	 *            one of the RENDERMODE_X constants see RENDERMODE_CONTINUOUSLY see RENDERMODE_WHEN_DIRTY
	 */
	public void setRenderMode(final int renderMode) {
		this.mGLThread.setRenderMode(renderMode);
	}
	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceViewEGL14.
	 */
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int w, final int h) {
		this.mGLThread.onWindowResize(w, h);
	}
	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceViewEGL14.
	 */
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		this.mGLThread.surfaceCreated();
	}
	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceViewEGL14.
	 */
	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		// Surface will be destroyed when we return
		this.mGLThread.surfaceDestroyed();
	}

}
