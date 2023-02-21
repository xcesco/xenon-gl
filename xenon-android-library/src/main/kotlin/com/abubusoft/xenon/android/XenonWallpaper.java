package com.abubusoft.xenon.android;

import com.abubusoft.xenon.android.listener.XenonGestureDetector;
import com.abubusoft.xenon.opengl.XenonGLRenderer;
import com.abubusoft.xenon.opengl.XenonGLRendererBuilder;

import android.content.Context;

/**
 * Interfaccia che i vari wallpaper engine devono implementare per poter essere usati
 * in argon.
 * 
 * @author Francesco Benincasa
 *
 */
public interface XenonWallpaper extends XenonGLRendererBuilder {
	
	/**
	 * Imposta il renderer
	 * 
	 * @param renderer
	 * 		rendere da utilizzare
	 */
	void setRenderer(XenonGLRenderer renderer);

	/**
	 * Fornisce il context dell'applicazione
	 * 
	 * @return
	 * 		context dell'applicazione
	 */
	Context getApplicationContext();
	
	/**
	 * Imposta il gesture detector
	 * 
	 * @param gestureDetectorValue
	 */
	void setGestureDetector(XenonGestureDetector gestureDetectorValue);
	
	/**
	 * Evento invocato quando l'applicazione si sta chiudendo
	 */
	void onDestroy();
	
}
