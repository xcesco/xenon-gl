package com.abubusoft.xenon.mesh.tiledmaps.isometric

import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.modifiers.AttributeQuadModifier.setVertexAttributes2
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier.setVertexCoords
import com.abubusoft.xenon.vbo.AttributeBuffer
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 *
 *
 * Funzioni di utilità per le mappe isometriche a diamante.
 *
 *
 * @author xcesco
 */
object IsometricHelper {
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
     * I passaggi sono due, da raw screen a raw window a centered window.
     *
     *
     *
     * <img src="./doc-files/convertRawScreen2CenteredWindow.png"></img>
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
    fun convertRawScreen2CenteredWindow(controller: IsometricMapController, screenX: Float, screenY: Float): Point2 {
        // da screen a window
        workPoint.x = screenX * controller.screenToTiledMapFactor
        workPoint.y = screenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth)
        workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight)
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
     * I passaggi sono due, da raw screen a raw window a centered window.
     *
     *
     *
     * <img src="./doc-files/convertRawScreen2IsoWindow.png"></img>
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
    fun convertRawScreen2IsoWindow(controller: IsometricMapController, screenX: Float, screenY: Float): Point2 {
        // da raw screen a raw window
        workPoint.x = screenX * controller.screenToTiledMapFactor
        workPoint.y = screenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth)
        workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight)

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
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
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
        // TODO questo viene usato per l'offset
        // da map a iso window (consideriamo che hanno lo stesso punto d'origine)
        // isoX=isoX;
        var isoY = isoY
        isoY = -isoY

        // versione fixata
        // ora convertiamo in centered window con y in alto
        // da iso window a centered window
        workPoint.x = (+isoX + isoY) / 2
        workPoint.y = (-isoX + isoY) / 4

        // per essere applicato al sistema di visualizzazione dobbiamo trasformare gli offset in scale * 2
        // questo perchè sul sistema della mappa
        workPoint.mul(2f)
        return workPoint
    }

    /**
     *
     *
     * Da Iso Window a Raw Screen.
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     * Le specifiche di trasformazione. Quelle che ci interessano vanno da C' a A'.
     *
     *
     * <img src="./doc-files/coordinateTransformationInverse.png"></img>
     *
     * @param controller
     * @param isoX
     * @param isoY
     * @return coordinate del punto riferite allo schermo
     */
    fun convertIsoWindow2RawScreen(controller: IsometricMapController, isoX: Float, isoY: Float): Point2 {
        // da iso window a centered window
        workPoint.x = (+isoX + isoY) / 2
        workPoint.y = (-isoX + isoY) / 4

        // da centered window a raw screen a distanza window
        workPoint.x = workPoint.x + (controller.map.view().windowCenter.x - controller.map.tileWidth)
        workPoint.y = workPoint.y - (controller.map.view().windowCenter.y - controller.map.tileHeight)
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
     * <img src="./doc-files/Iso2Memory.png"></img>
     *
     *
     *
     * Le righe della mappa sono sulla diagonale a sx, le colonne sulla diagonale a dx.
     *
     *
     *
     *
     * Il diamante viene generato considerando che l'origine del sistema è in centro. La punta che viene generata si trova in alto in centro al sisteam di coordinate.
     *
     *
     * @param rows
     * righe del vertex buffer
     * @param cols
     * colonne del vertex buffer
     * @param stepWidth
     * step in larghezza tra un tile ed un altro
     * @param stepHeight
     * step in altezza tra un tile ed un altro
     * @param tileWidth
     * width delle tiles
     * @param tileHeight
     * height delle tiles
     * @return vertex buffer
     */
    fun buildDiamondVertexBuffer(rows: Int, cols: Int, stepWidth: Float, stepHeight: Float, tileWidth: Float, tileHeight: Float): VertexBuffer {
        // creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
        // i layer che hanno come dimensione delle tile le stesse di default.
        var sceneX: Float
        var sceneY: Float
        val verticesBuffer: VertexBuffer = BufferManager.instance().createVertexBuffer(cols * rows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC)
        /*
		 * for (int i = 0; i < rows; i++) { for (int j = 0; j < cols; j++) { sceneX = (j - 1) * (stepWidth) - i * (stepWidth); sceneY = (rows - 1 - i) * stepHeight;
		 * 
		 * VertexQuadModifier.setVertexCoords(verticesBuffer, i * cols + j, sceneX, sceneY, tileWidth, tileHeight, false); } }
		 */

        // questo serve a metterlo al centro
        val baseY = rows * stepHeight
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                sceneX = (j - i) * stepWidth - stepWidth
                sceneY = baseY - (j + i) * stepHeight
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
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     * Le specifiche di trasformazione. Quelle che ci interessano vanno da B a C.
     *
     *
     * <img src="./doc-files/coordinateTransformation.png"></img>
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
    fun convertCenteredWindow2IsoWindow(controller: IsometricMapController?, rawWindowX: Float, rawWindowY: Float): Point2 {

        // da centered window a iso window
        workPoint.x = rawWindowX - 2f * rawWindowY
        workPoint.y = rawWindowX + 2f * rawWindowY

        // da iso windows a iso map (y verso il basso)
        workPoint.y = -workPoint.y

        // usiamo il sistema di riferimento della mappa
        // workPoint.add(controller.map.positionInMap);
        // workPoint.add(((IsometricMapHandler) (controller.map.handler)).isoWindowSize);
        return workPoint
    }

    /**
     *
     *
     * Converte da raw window a iso map, ovvero il punto
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     * Le specifiche di trasformazione. Quelle che ci interessano vanno da A a C.
     *
     *
     * <img src="./doc-files/coordinateTransformation.png"></img>
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
    fun convertRawScreen2IsoMap(controller: IsometricMapController, screenX: Float, screenY: Float): Point2 {
        // da screen a raw window
        workPoint.x = screenX * controller.screenToTiledMapFactor
        workPoint.y = screenY * controller.screenToTiledMapFactor

        // da raw screen a centered window
        workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth)
        workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight)

        // da centered window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y

        // da iso windows a iso map (y verso il basso)
        workPoint2.y = -workPoint2.y
        // usiamo il sistema di riferimento della mappa
        workPoint2.add(controller.map.positionInMap)
        workPoint2.add((controller.map.handler as IsometricMapHandler).isoWindowSize / 2f)

        // diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
        // workPoint.div(((IsometricMapHandler) (controller.map.handler)).isoTileSize);
        return workPoint2
    }

    /**
     *
     *
     * Converte da raw window a indici delle tile.
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
     *
     *
     *
     * Le specifiche di trasformazione. Quelle che ci interessano vanno da A a C.
     *
     *
     * <img src="./doc-files/coordinateTransformation.png"></img>
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
    fun convertRawScreen2IsoTileIndex(controller: IsometricMapController, rawScreenX: Float, rawScreenY: Float): Point2 {
        // da screen a window
        workPoint.x = rawScreenX * controller.screenToTiledMapFactor
        workPoint.y = rawScreenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth)
        workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight)

        // da centered window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y

        // da iso windows a iso map (y verso il basso)
        workPoint2.y = -workPoint2.y

        // usiamo il sistema di riferimento della mappa
        workPoint2.add(controller.map.positionInMap)
        workPoint2.add((controller.map.handler as IsometricMapHandler).isoWindowSize / 2f)

        // diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
        workPoint2.div((controller.map.handler as IsometricMapHandler).isoTileSize)
        return workPoint2
    }

    /**
     *
     *
     * Converte da raw window a indici delle tile.
     *
     *
     * <img src="./doc-files/convertRawWindow2IsoMap.png"></img>
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
    fun convertRawScreen2IsoTileOffset(controller: IsometricMapController, rawScreenX: Float, rawScreenY: Float): Point2 {
        // da screen a window
        workPoint.x = rawScreenX * controller.screenToTiledMapFactor
        workPoint.y = rawScreenY * controller.screenToTiledMapFactor

        // da raw screen a window
        workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth)
        workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight)

        // da centered window a iso window
        workPoint2.x = workPoint.x - 2f * workPoint.y
        workPoint2.y = workPoint.x + 2f * workPoint.y

        // da iso windows a iso map (y verso il basso)
        workPoint2.y = -workPoint2.y

        // usiamo il sistema di riferimento della mappa
        workPoint2.add(controller.map.positionInMap)
        workPoint2.add((controller.map.handler as IsometricMapHandler).isoWindowSize / 2f)

        // diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
        workPoint2.mod((controller.map.handler as IsometricMapHandler).isoTileSize)
        return workPoint2
    }
}