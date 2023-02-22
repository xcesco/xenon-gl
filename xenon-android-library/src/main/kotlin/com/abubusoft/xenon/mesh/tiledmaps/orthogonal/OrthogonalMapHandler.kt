/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.orthogonal

import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.math.XenonMath.zDistanceForSquare
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier.setVertexCoords
import com.abubusoft.xenon.mesh.tiledmaps.*
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractMapHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerOffsetHolder
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 * drawer per le mappe ortogonali. Per il momento usa il codice nell'abstract map handler
 *
 * @author xcesco
 */
@Uncryptable
class OrthogonalMapHandler(map: TiledMap) : AbstractMapHandler<OrthogonalMapController?>(map) {
    init {
        map.mapWidth = map.tileColumns * map.tileWidth
        map.mapHeight = map.tileRows * map.tileHeight
    }

    override fun onBuildView(view: TiledMapView?, camera: Camera?, options: TiledMapOptions?) {
        val screenInfo = XenonGL.screenInfo
        // impostiamo metodo di riempimento dello schermo
        view!!.windowDimension = 0f
        when (options!!.fillScreenType) {
            TiledMapFillScreenType.FILL_HEIGHT -> view.windowDimension = map.mapHeight * screenInfo.correctionX
            TiledMapFillScreenType.FILL_WIDTH -> view.windowDimension = map.mapWidth * screenInfo.correctionY
            TiledMapFillScreenType.FILL_CUSTOM_HEIGHT -> view.windowDimension = options.visibleTiles * map.tileHeight
            TiledMapFillScreenType.FILL_CUSTOM_WIDTH -> view.windowDimension = options.visibleTiles * map.tileWidth
        }
        view.windowDimension *= options.visiblePercentage
        view.distanceFromViewer = zDistanceForSquare(camera!!, view.windowDimension)
        view.windowWidth = (view.windowDimension / screenInfo.correctionY).toInt()
        view.windowHeight = (view.windowDimension / screenInfo.correctionX).toInt()
        view.mapMaxPositionValueX = map.mapWidth - map.view().windowWidth
        view.mapMaxPositionValueY = map.mapHeight - map.view().windowHeight
        val windowRemainderX: Int = view.windowWidth % map.tileWidth
        val windowRemainderY: Int = view.windowHeight % map.tileHeight

        // +2 per i bordi, +1 se la divisione contiene un resto
        view.windowTileColumns = (view.windowWidth / map.tileWidth) as Int + (if (windowRemainderX == 0) 0 else 1) + 2
        view.windowTileRows = (view.windowHeight / map.tileHeight) as Int + (if (windowRemainderY == 0) 0 else 1) + 2

        // calcoliamo il centro dello schermo, considerando che abbiamo 2
        // elementi in più per lato (quindi dobbiamo
        // togliere una dimensione per parte).
        view.windowCenter.x = view.windowWidth / 2f + map.tileWidth
        view.windowCenter.y = view.windowHeight / 2f + map.tileHeight

        // creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
        // i layer che hanno come dimensione delle tile le stesse di default.

        // dato che si parte da 0,0, quando si userà la matrice di proiezione bisognerà aggiustare l'origine (la camera di default
        // ce l'ha in mezzo allo schermo ma qua la si parte a disegnare da in alto a sx).
        var sceneX = 0f
        var sceneY = 0f
        view.windowVerticesBuffer =
            BufferManager.instance().createVertexBuffer(view.windowTileColumns * view.windowTileRows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC)
        for (i in 0 until view.windowTileRows) {
            for (j in 0 until view.windowTileColumns) {
                sceneX = j * map.tileWidth
                sceneY = i * map.tileHeight
                setVertexCoords(view.windowVerticesBuffer!!, i * view.windowTileColumns + j, sceneX, -sceneY, map.tileWidth, map.tileHeight, false)
            }
        }
        // lo impostiamo una volta per tutte, tanto non verrà mai cambiato
        view.windowVerticesBuffer!!.update()
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler#convertScroll(com.abubusoft.xenon.math.Point2, float, float)
	 */
    override fun convertRawWindow2MapWindow(scrollInMap: Point2?, scrollXInScreen: Float, scrollYInScreen: Float) {
        scrollInMap!!.x = scrollXInScreen
        scrollInMap.y = scrollYInScreen
    }

    fun buildMapController(map: TiledMap?, cameraValue: Camera?): OrthogonalMapController {
        controller = OrthogonalMapController(map, cameraValue)
        return controller!!
    }

    fun buildTiledLayerHandler(layer: TiledLayer?): OrthogonalTiledLayerHandler {
        return OrthogonalTiledLayerHandler(layer)
    }

    fun buildImageLayerHandler(layer: ImageLayer?): OrthogonalImageLayerHandler {
        return OrthogonalImageLayerHandler(layer)
    }

    fun buildObjectLayerHandler(layer: ObjectLayer?): OrthogonalObjectLayerHandler {
        return OrthogonalObjectLayerHandler(layer)
    }

    override fun convertMap2ViewLayer(offsetHolder: LayerOffsetHolder?, mapX: Int, mapY: Int) {
        // http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
        offsetHolder!!.tileIndexX = mapX / map.tileWidth
        offsetHolder.tileIndexY = mapY / map.tileHeight
        offsetHolder.screenOffsetX = mapX % map.tileWidth
        offsetHolder.screenOffsetY = mapY % map.tileHeight
    }
}