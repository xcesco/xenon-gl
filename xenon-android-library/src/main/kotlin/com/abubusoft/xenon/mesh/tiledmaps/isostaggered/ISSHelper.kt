package com.abubusoft.xenon.mesh.tiledmaps.isostaggered

import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.modifiers.AttributeQuadModifier.setVertexAttributes2
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier.setVertexCoords
import com.abubusoft.xenon.vbo.AttributeBuffer
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.VertexBuffer

object ISSHelper {
    val workPoint = Point2()
    val workPoint2 = Point2()

    /**
     *
     *
     * Converte le coordinate da schermo raw a (centered) window. Ricordiamo che il termine raw indica che il sistema di riferimento è in alto a sinistra. Sia lo screen che le coordinate window sono raw.
     *
     *
     *
     *
     *
     * I passaggi sono due, da raw screen a raw window a centered window.
     *
     *
     *
     *
     *
     * <img src="./doc-files/convertRawScreen2CenteredWindow.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param controller
     * @param screenX
     * @param screenY
     * @return coordinate del punto nel sistema di riferimentod della centered window
     */
    fun convertRawScreen2CenteredWindow(controller: ISSMapController, screenX: Float, screenY: Float): Point2 {
        // da screen a window
        workPoint.x = screenX * controller.screenToTiledMapFactor
        workPoint.y = screenY * controller.screenToTiledMapFactor

        // da raw screen a centered window
        workPoint.x = workPoint.x - controller.map.view().windowCenter.x
        workPoint.y = -workPoint.y + controller.map.view().windowCenter.y
        return workPoint
    }

    /**
     *
     *
     * Converte le coordinate da schermo raw a (iso) window. Ricordiamo che il termine raw indica che il sistema di riferimento è in alto a sinistra. La iso (centered) window invece è la window vista dalla prospettiva della mappa
     * isometrica, quindi con gli assi ruotati e a 120°.
     *
     *
     *
     *
     *
     * I passaggi sono due, da raw screen a raw window a centered window.
     *
     *
     *
     *
     *
     * <img src="./doc-files/convertRawScreen2IsoWindow.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param controller
     * @param screenX
     * @param screenY
     * @return coordinate del punto nel sistema di riferimento della iso (centered) window
     */
    fun convertRawScreen2IsoWindow(controller: ISSMapController, screenX: Float, screenY: Float): Point2 {
        // da raw screen a raw window
        workPoint.x = screenX * controller.screenToTiledMapFactor
        workPoint.y = screenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - controller.map.view().windowCenter.x
        workPoint.y = -workPoint.y + controller.map.view().windowCenter.y

        // da centered window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y
        return workPoint2
    }

    /**
     *
     *
     * Converte le coordinate da iso window a centered window. Ricordiamo che la centered window ha il sistema di coordinate standard, e la iso (centered) window invece è la window vista dalla prospettiva della mappa isometrica, quindi con
     * gli assi ruotati e a 120°.
     *
     *
     *
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param isoX
     * @param isoY
     * @return coordinate del punto nel sistema di riferimento della centered window
     */
    fun convertIsoMapOffset2ScreenOffset(isoX: Float, isoY: Float): Point2 {
        //TODO questo viene usato per l'offset
        // da map a iso window (consideriamo che hanno lo stesso punto d'origine)
        // isoX=isoX;
        var isoY = isoY
        isoY = -isoY

        // versione fixata
        // ora convertiamo in centered window con y in alto
        // da iso window a centered window
        workPoint.x = (+isoX + isoY) / 2
        workPoint.y = (-isoX + isoY) / 4
        //workPoint.x=isoX;
        //workPoint.y=isoY;

        // per essere applicato al sistema di visualizzazione dobbiamo trasformare gli offset in scale * 2
        // questo perchè sul sistema della mappa
        workPoint.mul(2f)
        return workPoint
    }

    fun convertIsoWindow2RawScreen(controller: ISSMapController, isoX: Float, isoY: Float): Point2 {
        // da iso window a centered window
        workPoint.x = (+isoX + isoY) / 2
        workPoint.y = (-isoX + isoY) / 4

        // da centered window a raw screen a distanza window
        workPoint.x = workPoint.x + controller.map.view().windowCenter.x
        workPoint.y = workPoint.y - controller.map.view().windowCenter.y

        // da distanzacentered window a raw screen
        workPoint.y = -workPoint.y
        workPoint.x = workPoint.x / controller.screenToTiledMapFactor
        workPoint.y = workPoint.y / controller.screenToTiledMapFactor
        return workPoint
    }

    /**
     *
     *
     * Costruisce un vertex buffer atto a contenere un rombo di tiles.
     *
     *
     *
     * <img src="./doc-files/Iso2Memory.png"></img>
     *
     *
     *
     *
     * Le righe della mappa sono sulla diagonale a sx, le colonne sulla diagonale a dx.
     *
     *
     *
     *
     *
     * Il diamante viene generato considerando che l'origine del sistema è in centro. La punta che viene generata si trova in alto in centro al sisteam di coordinate.
     *
     *
     * @param windowDimension   righe del vertex buffer
     * @param verticalRowOffset
     * @param rows
     * @param cols              colonne del vertex buffer
     * @param stepWidth         step in larghezza tra un tile ed un altro
     * @param stepHeight        step in altezza tra un tile ed un altro
     * @param tileWidth         width delle tiles
     * @param tileHeight        height delle tiles
     * @return vertex buffer
     */
    fun buildISSVertexBuffer(
        windowDimension: Float,
        verticalRowOffset: Int,
        rows: Int,
        cols: Int,
        stepWidth: Float,
        stepHeight: Float,
        tileWidth: Float,
        tileHeight: Float
    ): VertexBuffer {
        // creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
        // i layer che hanno come dimensione delle tile le stesse di default.
        var sceneX: Float
        var sceneY: Float
        val verticesBuffer: VertexBuffer = BufferManager.instance().createVertexBuffer(cols * rows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC)

        // con questa posizione mettiamo il vertice più in alto del primo quadrante sul veritice left top del box verde
        val baseX = -tileWidth * 0.5f - windowDimension / 2
        val baseY = windowDimension / 2 + verticalRowOffset * tileHeight / 2 // togliamo il row offset /2 (ogni due righe scendiamo di 1)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                sceneX = baseX - stepWidth * (i % 2) * 0.5f + j * stepWidth
                sceneY = baseY - i * stepHeight
                setVertexCoords(verticesBuffer, i * cols + j, sceneX, sceneY, tileWidth, tileHeight, false)
            }
        }
        return verticesBuffer
    }

    fun buildDiamondOffsetAttributeBuffer(rows: Int, cols: Int): AttributeBuffer {
        // creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
        // i layer che hanno come dimensione delle tile le stesse di default.
        val attributesBuffer: AttributeBuffer =
            BufferManager.instance().createAttributeBuffer(cols * rows * VertexBuffer.VERTEX_IN_QUAD_TILE, AttributeDimensionType.DIM_2, BufferAllocationType.STATIC)

        // inizializziamo tutto a 0
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                setVertexAttributes2(attributesBuffer, i * cols + j, 0f, 0f, false)
            }
        }
        return attributesBuffer
    }

    /**
     *
     *
     * Converte da raw window a iso map, ovvero il punto
     *
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param controller
     * @param rawWindowX
     * @param rawWindowY
     * @return punto con le coordinate iso window
     */
    fun convertCenteredWindow2IsoWindow(controller: ISSMapController?, rawWindowX: Float, rawWindowY: Float): Point2 {

        // da centered window a iso window
        workPoint.x = rawWindowX - 2f * rawWindowY
        workPoint.y = rawWindowX + 2f * rawWindowY

        // da iso windows a iso map (y verso il basso)
        workPoint.y = -workPoint.y

        // usiamo il sistema di riferimento della mappa
        //workPoint.add(controller.map.positionInMap);
        //workPoint.add(((IsometricMapHandler) (controller.map.handler)).isoWindowSize);
        return workPoint
    }

    /**
     *
     *
     * Converte da raw window a iso map, ovvero il punto
     *
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param controller
     * @param screenX
     * @param screenY
     * @return punto con le coordinate iso window
     */
    fun convertRawScreen2IsoMap(controller: ISSMapController, screenX: Float, screenY: Float): Point2 {
        // da screen a window
        workPoint.x = screenX * controller.screenToTiledMapFactor
        workPoint.y = screenY * controller.screenToTiledMapFactor

        // da centered window a tiled window
        workPoint.x = workPoint.x + controller.map.view().tileBase.x
        workPoint.y = workPoint.y + controller.map.view().tileBase.y

        // da raw screen a centered window
        workPoint.x = workPoint.x - controller.map.view().tiledWindowWidth * 0.5f
        workPoint.y = -workPoint.y + controller.map.view().tiledWindowHeight * 0.5f

        // da tiled window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y

        // da iso windows a iso map (y verso il basso)
        workPoint2.y = -workPoint2.y

        // usiamo il sistema di riferimento della mappa
        workPoint2.add(controller.map.positionInMap)
        workPoint2.add((controller.map.handler as ISSMapHandler).isoWindowWidth / 2f)

        // diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
        //workPoint.div(((IsometricMapHandler) (controller.map.handler)).isoTileSize);
        return workPoint2
    }

    /**
     *
     *
     * Converte da raw window a indici delle tile.
     *
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param controller
     * @param rawScreenX
     * @param rawScreenY
     * @return punto con le coordinate iso window
     */
    fun convertRawScreen2IsoTileIndex(controller: ISSMapController, rawScreenX: Float, rawScreenY: Float): Point2 {
        // da screen a window
        workPoint.x = rawScreenX * controller.screenToTiledMapFactor
        workPoint.y = rawScreenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - controller.map.view().windowCenter.x
        workPoint.y = -workPoint.y + controller.map.view().windowCenter.y

        // da centered window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y

        // da iso windows a iso map (y verso il basso)
        workPoint2.y = -workPoint2.y

        // usiamo il sistema di riferimento della mappa
        workPoint2.add(controller.map.positionInMap)
        workPoint2.add((controller.map.handler as ISSMapHandler).isoWindowWidth / 2f)

        // diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
        workPoint2.div((controller.map.handler as ISSMapHandler).isoTileSize)
        return workPoint2
    }

    /**
     *
     *
     * Converte da raw window a indici delle tile.
     *
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     * @param controller
     * @param rawScreenX
     * @param rawScreenY
     * @return punto con le coordinate iso window
     */
    fun convertRawScreen2IsoTileOffset(controller: ISSMapController, rawScreenX: Float, rawScreenY: Float): Point2 {
        // da screen a window
        workPoint.x = rawScreenX * controller.screenToTiledMapFactor
        workPoint.y = rawScreenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - controller.map.view().windowCenter.x
        workPoint.y = -workPoint.y + controller.map.view().windowCenter.y

        // da centered window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y

        // da iso windows a iso map (y verso il basso)
        workPoint2.y = -workPoint2.y

        // usiamo il sistema di riferimento della mappa
        workPoint2.add(controller.map.positionInMap)
        workPoint2.add((controller.map.handler as ISSMapHandler).isoWindowWidth / 2f)

        // diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
        workPoint2.mod((controller.map.handler as ISSMapHandler).isoTileSize)
        return workPoint2
    }
}