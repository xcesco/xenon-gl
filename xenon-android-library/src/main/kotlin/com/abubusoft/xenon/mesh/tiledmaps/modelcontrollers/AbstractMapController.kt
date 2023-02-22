/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers

import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.math.XenonMath.clamp
import com.abubusoft.xenon.math.XenonMath.isEquals
import com.abubusoft.xenon.math.XenonMath.max
import com.abubusoft.xenon.mesh.tiledmaps.ScrollDirectionType
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapPositionType
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalHelper
import com.abubusoft.xenon.opengl.XenonGL

/**
 * @author xcesco
 */
abstract class AbstractMapController(
    /**
     *
     */
    val map: TiledMap,
    /**
     *
     * Camera di riferimento.
     */
    protected var camera: Camera
) : MapController {
    /**
     * serve a gestire lo scroll sulla mappa
     */
    protected var workScroll: Point2

    /**
     *
     */
    protected var matrix: Matrix4x4

    /**
     *
     */
    override var matrixModelViewProjection: Matrix4x4

    /**
     *
     *
     * Fattore di riduzione dei movimenti. Lo schermo è di una dimensione, la window sul tile di un altra. se non viene applicato questo fattore di riduzione, i movimenti applicati
     * alla window avrebbero sarebbero dimensionati rispetto alle dimensioni dello schermo.
     *
     */
    var screenToTiledMapFactor: Float

    init {
        matrix = Matrix4x4()
        matrixModelViewProjection = Matrix4x4()
        matrix.buildIdentityMatrix()
        matrix.translate(0f, 0f, -map.view().distanceFromViewer)
        matrixModelViewProjection.multiply(camera.info.projection4CameraMatrix, matrix)
        val screenSize = max(XenonGL.screenInfo.width, XenonGL.screenInfo.height).toFloat()
        val windowOfTileSize = map.view().windowDimension
        screenToTiledMapFactor = windowOfTileSize / screenSize
        workScroll = Point2()
    }

    /**
     *
     *
     * Effettua lo scroll della tilemap, partendo da uno scroll lato schermo.
     *
     *
     * @param rawScreenX
     * @param rawScreenY
     */
    override fun scrollFromScreen(rawScreenX: Float, rawScreenY: Float) {
        scroll(rawScreenX * screenToTiledMapFactor, rawScreenY * screenToTiledMapFactor)
    }

    /**
     * Muove la posizione della mappa, nel sistema di coordinate della mappa
     *
     * @param rawWindowX
     * @param rawWindowY
     */
    override fun scroll(rawWindowX: Float, rawWindowY: Float) {

        // eliminiamo spostamenti troppo piccoli
        var rawWindowX = rawWindowX
        var rawWindowY = rawWindowY
        if (isEquals(rawWindowX, 0f)) {
            rawWindowX = 0f
        }
        if (isEquals(rawWindowY, 0f)) {
            rawWindowY = 0f
        }
        map.handler.convertRawWindow2MapWindow(workScroll, rawWindowX, rawWindowY)

        // eliminiamo spostamenti troppo piccoli
        if (isEquals(workScroll.x, 0f)) {
            workScroll.x = 0f
        }
        if (isEquals(workScroll.y, 0f)) {
            workScroll.y = 0f
        }
        map.positionInMap.addCoords(workScroll.x, workScroll.y)
        if (map.scrollHorizontalLocked) {
            map.positionInMap.x = clamp(map.positionInMap.x, 0f, map.view().mapMaxPositionValueX)
        } else {
            // la posizione nella mappa deve essere sempre circoscritta alle
            // dimensioni della mappa stessa, senza mai andare oltre
            if (map.positionInMap.x < 0f) {
                map.positionInMap.x += map.mapWidth.toFloat()
            }
            if (map.positionInMap.x > map.mapWidth) {
                map.positionInMap.x -= map.mapWidth.toFloat()
            }
            if (map.movementEventListener != null) {
                map.scrollHorizontalCurrentArea = (map.positionInMap.x * map.listenerOptions.horizontalAreaInvSize).toInt()
                if (map.scrollHorizontalPreviousArea != map.scrollHorizontalCurrentArea) {
                    map.movementEventListener.onAreaChange(ScrollDirectionType.HORIZONTAL_SCROLL, map.scrollHorizontalCurrentArea)
                    map.scrollHorizontalPreviousArea = map.scrollHorizontalCurrentArea
                }
            }
        }
        if (map.scrollVerticalLocked) {
            // se locked, la position non può andare oltre
            map.positionInMap.y = clamp(map.positionInMap.y, 0f, map.view().mapMaxPositionValueY)
        } else {
            if (map.positionInMap.y < 0f) map.positionInMap.y += map.mapHeight.toFloat()
            if (map.positionInMap.y > map.mapHeight) map.positionInMap.y -= map.mapHeight.toFloat()
            if (map.movementEventListener != null) {
                map.scrollVerticalCurrentArea = (map.positionInMap.y * map.listenerOptions.verticalAreaInvSize).toInt()
                if (map.scrollVerticalPreviousArea != map.scrollVerticalCurrentArea) {
                    map.movementEventListener.onAreaChange(ScrollDirectionType.VERTICAL_SCROLL, map.scrollVerticalCurrentArea)
                    map.scrollVerticalCurrentArea = map.scrollVerticalPreviousArea
                }
            }
        }

        //Logger.info("--> position (%s, %s) , -- scroll (%s, %s) ", map.positionInMap.x, map.positionInMap.y, workScroll.x, workScroll.y);
        for (i in map.layers.indices) {
            map.layers[i].scroll(workScroll.x, workScroll.y)
        }

        // in caso di spostamento e di listener!=null mandiamo evento
        if (map.movementEventListener != null) {
            map.movementEventListener.onPosition(map.positionInMap.x, map.positionInMap.y)
        }
    }

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
    override fun position(x: Float, y: Float, positionType: TiledMapPositionType?) {
        when (positionType) {
            TiledMapPositionType.LEFT_TOP -> position(x, y)
            TiledMapPositionType.LEFT_CENTER -> position(x, y + map.tileHeight - map.view().windowHeight * .5f)
            TiledMapPositionType.LEFT_BOTTOM -> position(x, y + map.tileHeight - map.view().windowHeight)
            TiledMapPositionType.MIDDLE_CENTER -> position(x - map.view().windowWidth * .5f + map.tileWidth, y + map.tileHeight - map.view().windowHeight * .5f)
            else -> {}
        }
    }

    /**
     * Converte un punto dello schermo nelle coordinate
     * @param screenX
     * @param screenY
     *
     * @return
     * punto con coordinate sulla mappa
     */
    override fun touch(screenX: Float, screenY: Float): Point2 {
        return OrthogonalHelper.translateScreenCoordsToTiledMap(map, screenX * screenToTiledMapFactor, screenY * screenToTiledMapFactor)
    }

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
    override fun positionFromScreen(screenX: Float, screenY: Float, positionType: TiledMapPositionType?) {
        position((screenX * screenToTiledMapFactor).toInt().toFloat(), (screenY * screenToTiledMapFactor).toInt().toFloat(), positionType)
    }

    override fun zoom(value: Float) {}
}