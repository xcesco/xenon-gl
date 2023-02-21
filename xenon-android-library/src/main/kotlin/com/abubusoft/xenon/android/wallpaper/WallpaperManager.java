package com.abubusoft.xenon.android.wallpaper;

/**
 * <p>
 * Gestore del wallpaper. Attualmente gestisce l'offset dello schermo.
 * </p>
 * 
 * <pre>
 * 
 * onFrameDraw()
 * {
 *    [..]
 * 
 *    if (!ArgonGL.screenInfo.isLandscapeMode() &amp;&amp; 
 *      WallpaperManager.instance().isScreenOffsetChanged()) {
 *          float o = screen.width * (1f - ArgonGL.screenInfo.aspectRatio) * 0.5f;
 *          float xl = o * 2f;
 * 
 *          Logger.info(&quot;---&gt; Offset %s &quot;, WallpaperManager.instance().getScreenOffset());
 *          camera.positionOnXYPlaneTo(-o + xl * WallpaperManager.instance().getScreenOffset(), 0f);
 * 
 *          // calcola matrici
 *          // matrix screen
 *          matrixModelview.buildIdentityMatrix();
 *          matrixModelview.translate(0, -config.textureSize.height * 0.5f, -this.screenZDistance);
 *          screenMatrixModelViewProjection.unlock();
 *          screenMatrixModelViewProjection.multiply(camera.info.projection4CameraMatrix, matrixModelview);
 *          screenMatrixModelViewProjection.lock();
 *   }
 *   
 *   [..]
 * </pre>
 * 
 * @author Francesco Benincasa
 * 
 */
public class WallpaperManager {

	private static final WallpaperManager instance = new WallpaperManager();

	private WallpaperManager() {
		currentScreenOffset = 0f;
		lastScreenOffset = -1f;
	}

	private float currentScreenOffset;

	private float lastScreenOffset;

	private float tempValue;

	public static WallpaperManager instance() {
		return instance;
	}

	/**
	 * <p>
	 * Metodo da invocare nell'onDraw dell'applicazione per vedere se l'offset dello screen è cambiato. Questo metodo aggiorna anche il valore precedente dell'offset, dandogli il
	 * valore corrente. Questo implica il fatto che la volta successiva che viene invocato questo metodo, se non ci sono cambiamenti nell'offset questo metodo restituisce
	 * <b>true</b>.
	 * </p>
	 * 
	 * @return <b>true</b> se l'offset è cambiato.
	 */
	public boolean isScreenOffsetChanged() {
		tempValue = lastScreenOffset;
		lastScreenOffset = currentScreenOffset;

		return tempValue != currentScreenOffset;

	}

	/**
	 * <p>
	 * Imposta l'offset dello schermo attuale.
	 * </p>
	 * 
	 * @param value
	 *            valore dell'offset dello schermo da [0, 1]
	 */
	public void setScreenOffset(float value) {
		currentScreenOffset = value;
	}

	/**
	 * <p>
	 * Valore dell'offset dello schermo da [0, 1]
	 * </p>
	 * 
	 * @return valore dell'offset dello schermo da [0, 1]
	 */
	public float getScreenOffset() {
		return currentScreenOffset;
	}
}
