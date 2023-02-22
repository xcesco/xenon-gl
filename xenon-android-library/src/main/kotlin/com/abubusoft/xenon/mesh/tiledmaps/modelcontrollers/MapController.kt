package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapPositionType

interface MapController {
    val matrixModelViewProjection: Matrix4x4

    /**
     *
     *
     * Effettua lo scroll della tilemap, partendo da uno scroll lato schermo.
     *
     *
     * @param screenDistanceX
     * @param screenDistanceY
     */
    fun scrollFromScreen(screenDistanceX: Float, screenDistanceY: Float)

    /**
     *
     *
     * Effettua lo scroll della tilemap con uno spostamento calcolto rispetto allo schermo. Le distanze vengono quindi modificate.
     *
     *
     * @param distanceX
     * @param distanceY
     */
    fun scroll(distanceX: Float, distanceY: Float)

    /**
     *
     *
     * Effettua lo spostamento della mappa.
     *
     *
     *
     *
     * Le coordinate sono espresse con il sistema di riferimento degli oggetti, ovvero quello che ha come origine il punto in alto a sinistra della mappa (con startY verso il
     * basso).
     *
     *
     * @param x
     * @param y
     * @param positionType
     */
    fun position(x: Float, y: Float, positionType: TiledMapPositionType?)

    /**
     * Converte un punto dello schermo nelle coordinate
     * @param screenX
     * @param screenY
     *
     * @return
     */
    fun touch(screenX: Float, screenY: Float): Point2

    /**
     *
     *
     * Effettua lo spostamento della mappa.
     *
     *
     *
     *
     * Le coordinate sono espresse con il sistema di riferimento dello schermo, quindi le coordinate devo essere convertite.
     *
     *
     * @param screenX
     * @param screenY
     */
    fun positionFromScreen(screenX: Float, screenY: Float, positionType: TiledMapPositionType?)
    fun zoom(value: Float)
    fun position(x: Float, y: Float)
}