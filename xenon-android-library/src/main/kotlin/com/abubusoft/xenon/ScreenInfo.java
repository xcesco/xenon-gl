package com.abubusoft.xenon;

import com.abubusoft.xenon.android.ScreenDensityClassType;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.core.util.ResourceUtility;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Questa classe espone proprietà in sola lettura relativa allo schermo del dispositivo.
 * 
 * @author Francesco Benincasa
 * 
 */
public class ScreenInfo {

	/**
	 * larghezza dello schermo
	 */
	public int width;

	/**
	 * altezza dello schermo
	 */
	public int height;

	/**
	 * indica se lo schermo è in landscape mode
	 */
	public boolean landscapeMode;

	public boolean isLandscapeMode() {
		return landscapeMode;
	}

	public boolean isPortraitMode() {
		return !landscapeMode;
	}

	/**
	 * aspect ratio dello schermo
	 */
	public float aspectRatio;

	/**
	 * fattore di correzione X in base all'aspect ratio
	 */
	public float correctionX;

	/**
	 * fattore di correzione Y in base all'aspect ratio
	 */
	public float correctionY;

	/**
	 * <p>
	 * classe di densità dello schermo.
	 * </p>
	 */
	public ScreenDensityClassType densityClass;

	/**
	 * Densità reale dello schermo in dpi (dot per inc).
	 */
	public int densityDpi;

	/**
	 * <p>
	 * Scaling factor. Non usata
	 * </p>
	 */
	public float scaleFactor;

	/**
	 * risoluzione dello schermo
	 */
	public ScreenResolutionType resolution;

	/**
	 * <p>
	 * Recupera le info per lo schermo.
	 * </p>
	 * 
	 * @param context
	 *            contesto da usare
	 * 
	 * @return informazioni sullo screen
	 */
	public static ScreenInfo build(Context context) {
		return build(context, new ScreenInfo());
	}

	/**
	 * <p>
	 * Recupera le info per lo schermo.
	 * </p>
	 * 
	 * @param context
	 *            contesto da usare
	 * @param screenInfo
	 *            oggetto da usare per recuperare le informazioni sullo schermo
	 * 
	 * 
	 * @return lo stesso oggetto passato come argomento con i parametri relativi allo schermo
	 */
	public static ScreenInfo build(Context context, ScreenInfo screenInfo) {
		WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			} catch (Exception ignored) {
			}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			} catch (Exception ignored) {
			}

		screenInfo.densityClass = ScreenDensityClassType.UNKNOWN;
		screenInfo.densityDpi = metrics.densityDpi;

		screenInfo.scaleFactor = metrics.density;

		screenInfo.updateDimensions(widthPixels, heightPixels);
		screenInfo.resolution = ScreenResolutionType.findMatch(screenInfo.width, screenInfo.height);

		return screenInfo;
	}

	/**
	 * Aggiorna le dimensioni dello screenInfo:
	 * <ul>
	 * <li>width: larghezza dello schermo</li>
	 * <li>height: altezza dello schermo</li>
	 * <li>aspectRatio: rapporto width/height</li>
	 * <li>landscapeMode: se true indica che la larghezza è superiore all'altezza</li>
	 * <li>correctionX: </li>
	 * <li>correctionY</li>
	 * </ul>
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void updateDimensions(int screenWidth, int screenHeight) {
		// registriamo il cam
		// impostiamo sempre e comunque lo screenInfo
		width = screenWidth;
		height = screenHeight;
		aspectRatio = (float) screenWidth / screenHeight;
		landscapeMode = XenonGL.screenInfo.aspectRatio > 1f;
		
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
*/
		
		
		if (landscapeMode) {
			// width > height
			
			// 
			correctionX = XenonGL.screenInfo.aspectRatio;
			correctionY = 1.0f;
		} else {
			// width < height
			correctionX = 1.0f;
			correctionY = 1.0f / XenonGL.screenInfo.aspectRatio;
		}

	}

}
