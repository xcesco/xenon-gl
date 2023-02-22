package com.abubusoft.xenon.android.surfaceview16

import com.abubusoft.xenon.opengl.XenonEGL
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * An interface for choosing an EGLConfig configuration from a list of potential configurations.
 *
 *
 * This interface must be implemented by clients wishing to call [ArgonGLSurfaceView16.setEGLConfigChooser]
 */
interface ArgonConfigChooser16 {
    /**
     * Choose a configuration from the list. Implementors typically implement this method by calling [EGL10.eglChooseConfig] and iterating through the results. Please consult the EGL specification available from The Khronos Group
     * to learn how to call eglChooseConfig.
     *
     * @param egl
     * the EGL10 for the current display.
     * @param display
     * the current display.
     * @return the chosen configuration.
     */
    fun chooseConfig(egl: EGL10?, display: EGLDisplay?): EGLConfig?
    fun findBestMatch(xenonEGL: XenonEGL)
    val pixelFormat: Int
}