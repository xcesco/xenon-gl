package com.abubusoft.xenon.android.surfaceview16;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.abubusoft.xenon.opengl.XenonEGL;

import android.opengl.GLSurfaceView.EGLConfigChooser;

/**
 * An interface for choosing an EGLConfig configuration from a list of potential configurations.
 * <p>
 * This interface must be implemented by clients wishing to call {@link ArgonGLSurfaceView16#setEGLConfigChooser(EGLConfigChooser)}
 */
public interface ArgonConfigChooser16 {
	/**
	 * Choose a configuration from the list. Implementors typically implement this method by calling {@link EGL10#eglChooseConfig} and iterating through the results. Please consult the EGL specification available from The Khronos Group
	 * to learn how to call eglChooseConfig.
	 * 
	 * @param egl
	 *            the EGL10 for the current display.
	 * @param display
	 *            the current display.
	 * @return the chosen configuration.
	 */
	EGLConfig chooseConfig(EGL10 egl, EGLDisplay display);

	void findBestMatch(XenonEGL xenonEGL);
	
	int getPixelFormat();
}