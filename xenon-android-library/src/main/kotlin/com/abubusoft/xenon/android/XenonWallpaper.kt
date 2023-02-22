package com.abubusoft.xenon.android

import android.content.Context
import com.abubusoft.xenon.android.listener.XenonGestureDetector
import com.abubusoft.xenon.opengl.XenonGLRenderer
import com.abubusoft.xenon.opengl.XenonGLRendererBuilder

/**
 * Interfaccia che i vari wallpaper engine devono implementare per poter essere usati
 * in argon.
 *
 * @author Francesco Benincasa
 */
interface XenonWallpaper : XenonGLRendererBuilder {
    /**
     * Imposta il renderer
     *
     * @param renderer
     * rendere da utilizzare
     */
    fun setRenderer(renderer: XenonGLRenderer?)

    /**
     * Fornisce il context dell'applicazione
     *
     * @return
     * context dell'applicazione
     */
    val applicationContext: Context?

    /**
     * Imposta il gesture detector
     *
     * @param gestureDetectorValue
     */
    fun setGestureDetector(gestureDetectorValue: XenonGestureDetector?)

    /**
     * Evento invocato quando l'applicazione si sta chiudendo
     */
    fun onDestroy()
}