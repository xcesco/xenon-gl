package com.abubusoft.xenon.android.wallpaper

/**
 *
 *
 * Gestore del wallpaper. Attualmente gestisce l'offset dello schermo.
 *
 *
 * <pre>
 *
 * onFrameDraw()
 * {
 * [..]
 *
 * if (!ArgonGL.screenInfo.isLandscapeMode() &amp;&amp;
 * WallpaperManager.instance().isScreenOffsetChanged()) {
 * float o = screen.width * (1f - ArgonGL.screenInfo.aspectRatio) * 0.5f;
 * float xl = o * 2f;
 *
 * Logger.info(&quot;---&gt; Offset %s &quot;, WallpaperManager.instance().getScreenOffset());
 * camera.positionOnXYPlaneTo(-o + xl * WallpaperManager.instance().getScreenOffset(), 0f);
 *
 * // calcola matrici
 * // matrix screen
 * matrixModelview.buildIdentityMatrix();
 * matrixModelview.translate(0, -config.textureSize.height * 0.5f, -this.screenZDistance);
 * screenMatrixModelViewProjection.unlock();
 * screenMatrixModelViewProjection.multiply(camera.info.projection4CameraMatrix, matrixModelview);
 * screenMatrixModelViewProjection.lock();
 * }
 *
 * [..]
</pre> *
 *
 * @author Francesco Benincasa
 */
class WallpaperManager private constructor() {
    /**
     *
     *
     * Valore dell'offset dello schermo da [0, 1]
     *
     *
     * @return valore dell'offset dello schermo da [0, 1]
     */
    /**
     *
     *
     * Imposta l'offset dello schermo attuale.
     *
     *
     * @param value
     * valore dell'offset dello schermo da [0, 1]
     */
    var screenOffset = 0f
    private var lastScreenOffset: Float
    private var tempValue = 0f

    init {
        lastScreenOffset = -1f
    }

    /**
     *
     *
     * Metodo da invocare nell'onDraw dell'applicazione per vedere se l'offset dello screen è cambiato. Questo metodo aggiorna anche il valore precedente dell'offset, dandogli il
     * valore corrente. Questo implica il fatto che la volta successiva che viene invocato questo metodo, se non ci sono cambiamenti nell'offset questo metodo restituisce
     * **true**.
     *
     *
     * @return **true** se l'offset è cambiato.
     */
    val isScreenOffsetChanged: Boolean
        get() {
            tempValue = lastScreenOffset
            lastScreenOffset = screenOffset
            return tempValue != screenOffset
        }

    companion object {
        private val instance = WallpaperManager()
        fun instance(): WallpaperManager {
            return instance
        }
    }
}