package com.abubusoft.xenon

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import com.abubusoft.xenon.android.ScreenDensityClassType
import com.abubusoft.xenon.opengl.XenonGL

/**
 * Questa classe espone proprietà in sola lettura relativa allo schermo del dispositivo.
 *
 * @author Francesco Benincasa
 */
class ScreenInfo {
    /**
     * larghezza dello schermo
     */
    var width = 0

    /**
     * altezza dello schermo
     */
    var height = 0

    /**
     * indica se lo schermo è in landscape mode
     */
    var isLandscapeMode = false
    val isPortraitMode: Boolean
        get() = !isLandscapeMode

    /**
     * aspect ratio dello schermo
     */
    var aspectRatio = 0f

    /**
     * fattore di correzione X in base all'aspect ratio
     */
    var correctionX = 0f

    /**
     * fattore di correzione Y in base all'aspect ratio
     */
    var correctionY = 0f

    /**
     *
     *
     * classe di densità dello schermo.
     *
     */
    var densityClass: ScreenDensityClassType? = null

    /**
     * Densità reale dello schermo in dpi (dot per inc).
     */
    var densityDpi = 0

    /**
     *
     *
     * Scaling factor. Non usata
     *
     */
    var scaleFactor = 0f

    /**
     * risoluzione dello schermo
     */
    var resolution: ScreenResolutionType? = null

    /**
     * Aggiorna le dimensioni dello screenInfo:
     *
     *  * width: larghezza dello schermo
     *  * height: altezza dello schermo
     *  * aspectRatio: rapporto width/height
     *  * landscapeMode: se true indica che la larghezza è superiore all'altezza
     *  * correctionX:
     *  * correctionY
     *
     *
     * @param screenWidth
     * @param screenHeight
     */
    fun updateDimensions(screenWidth: Int, screenHeight: Int) {
        // registriamo il cam
        // impostiamo sempre e comunque lo screenInfo
        width = screenWidth
        height = screenHeight
        aspectRatio = screenWidth.toFloat() / screenHeight
        isLandscapeMode = XenonGL.screenInfo.aspectRatio > 1f

        // il codice nella build era questo.
/*
		
		// metrics.
		screenInfo.width = widthPixels;
		screenInfo.height = heightPixels;

		screenInfo.aspectRatio = (float) ((1.0 * screenInfo.width) / screenInfo.height);
		screenInfo.landscapeMode = false;
		if (screenInfo.width > screenInfo.height) {
			screenInfo.landscapeMode = true;
		}
		screenInfo.correctionX = screenInfo.aspectRatio;
		screenInfo.correctionY = 1;
*/if (isLandscapeMode) {
            // width > height

            // 
            correctionX = XenonGL.screenInfo.aspectRatio
            correctionY = 1.0f
        } else {
            // width < height
            correctionX = 1.0f
            correctionY = 1.0f / XenonGL.screenInfo.aspectRatio
        }
    }

    companion object {
        /**
         *
         *
         * Recupera le info per lo schermo.
         *
         *
         * @param context
         * contesto da usare
         * @param screenInfo
         * oggetto da usare per recuperare le informazioni sullo schermo
         *
         *
         * @return lo stesso oggetto passato come argomento con i parametri relativi allo schermo
         */
        /**
         *
         *
         * Recupera le info per lo schermo.
         *
         *
         * @param context
         * contesto da usare
         *
         * @return informazioni sullo screen
         */
        @JvmOverloads
        fun build(context: Context, screenInfo: ScreenInfo = ScreenInfo()): ScreenInfo {
            val w = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = w.defaultDisplay
            val metrics = DisplayMetrics()
            d.getMetrics(metrics)
            // since SDK_INT = 1;
            var widthPixels = metrics.widthPixels
            var heightPixels = metrics.heightPixels
            // includes window decorations (statusbar bar/menu bar)
            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) try {
                widthPixels = Display::class.java.getMethod("getRawWidth").invoke(d) as Int
                heightPixels = Display::class.java.getMethod("getRawHeight").invoke(d) as Int
            } catch (ignored: Exception) {
            }
            // includes window decorations (statusbar bar/menu bar)
            if (Build.VERSION.SDK_INT >= 17) try {
                val realSize = Point()
                Display::class.java.getMethod("getRealSize", Point::class.java).invoke(d, realSize)
                widthPixels = realSize.x
                heightPixels = realSize.y
            } catch (ignored: Exception) {
            }
            screenInfo.densityClass = ScreenDensityClassType.UNKNOWN
            screenInfo.densityDpi = metrics.densityDpi
            screenInfo.scaleFactor = metrics.density
            screenInfo.updateDimensions(widthPixels, heightPixels)
            screenInfo.resolution = ScreenResolutionType.Companion.findMatch(screenInfo.width, screenInfo.height)
            return screenInfo
        }
    }
}