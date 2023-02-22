/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.orthogonal

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.tiledmaps.ObjBase
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap

/**
 *
 *
 * **Tutti i metodi di questa classe non sono thread safe.**
 *
 *
 *
 * Questa classe fornisce dei metodi d'aiuto per convertire le coordinate fornite in input in coordinate utili per la mappa.
 *
 *
 * @author Francesco Benincasa
 */
object OrthogonalHelper {
    /**
     *
     *
     * variabile di lavoro
     *
     */
    private val workResult = Point2()

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Dato un oggetto, ottiene le coordinate di un oggetto rispetto alla finestra. Queste coordinate possono essere utilizzate direttamente in una matrice di trasformazione, per
     * visualizzare su schermo l'oggetto.
     *
     *
     * <img src="doc-files/calcoloMapCenter1.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     *
     * <pre>
     * screenX =   posX - ( positionInMap.x + windowCenter.x - tileWidth)
     * screenY = - posY + ( positionInMap.y + windowCenter.y - tileHeight)
    </pre> *
     *
     *
     *
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param obj
     * oggetto su cui si lavora
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translateInScreenCoords(tiledMap: TiledMap, obj: ObjBase): Point2 {
        workResult.setCoords(
            obj.x + obj.width * 0.5f + tiledMap.tileWidth - (Math.round(tiledMap.positionInMap.x) + tiledMap.view().windowCenter.x),
            -(obj.y + obj.height * 0.5f) - tiledMap.tileHeight + (Math.round(tiledMap.positionInMap.y) + tiledMap.view().windowCenter.y)
        )
        return workResult
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Dato un oggetto, ottiene le coordinate di un oggetto rispetto alla finestra. Queste coordinate possono essere utilizzate direttamente in una matrice di trasformazione, per
     * visualizzare su schermo l'oggetto. Le coordinate screen sono state già riportate alle giuste proporzioni. Questo metodo quindi non si preoccupa di
     * moltiplicarle per il fattore di conversione da schermo, dato che è stato già fatto.
     *
     *
     * <img src="doc-files/calcoloObj2Box2D.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     *
     * <pre>
     * screenX =   posX + positionInMap.x
     * screenY =   posY + positionInMap.y
    </pre> *
     *
     *
     *
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param obj
     * oggetto su cui si lavora
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translateScreenCoordsToTiledMap(tiledMap: TiledMap, screenX: Float, screenY: Float): Point2 {
        workResult.setCoords(screenX + Math.round(tiledMap.positionInMap.x), screenY + Math.round(tiledMap.positionInMap.y))
        Logger.info("Convert screen (%s, %s) to map (%s, %s)", screenX, screenY, workResult.x, workResult.y)
        return workResult
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Date le coordinate prese dallo schermo, recuperiamo l'id della tile che riesede a quelle coordinate.
     *
     *
     * <img src="doc-files/calcoloObj2Box2D.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     *
     * <pre>
     * screenX =   posX + positionInMap.x
     * screenY =   posY + positionInMap.y
    </pre> *
     *
     *
     *
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param obj
     * oggetto su cui si lavora
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun convertRawScreenToTileId(tiledMap: TiledMap, screenX: Float, screenY: Float): Int {
        val x = (screenX + Math.round(tiledMap.positionInMap.x)).toInt()
        val y = (screenY + Math.round(tiledMap.positionInMap.y)).toInt()
        // dobbiamo evitare arrotondamenti tra x e y, quindi convertiamo tutto in int separatamente
        val res = (y / tiledMap.tileHeight * tiledMap.tileColumns) + (x / tiledMap.tileWidth)
        Logger.info("Convert screen (%s, %s) to mapId (%s)", screenX, screenY, res)
        return res
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Date le coordinate prese dallo schermo, recuperiamo l'id della tile che riesede a quelle coordinate.
     *
     *
     * <img src="doc-files/calcoloObj2Box2D.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     *
     * <pre>
     * screenX =   posX + positionInMap.x
     * screenY =   posY + positionInMap.y
    </pre> *
     *
     *
     *
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param obj
     * oggetto su cui si lavora
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translateMapCoordsToTileId(tiledMap: TiledMap, mapX: Float, mapY: Float): Int {
        /*int x=(int) (Math.round(mapX));
		int y=(int) (Math.round(mapY));*/
        val x = mapX.toInt()
        val y = mapY.toInt()
        val res = y / tiledMap.tileHeight * tiledMap.tileColumns + x / tiledMap.tileWidth
        Logger.info("Convert map (%s, %s) to mapId (%s)", x, y, res)
        return res
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Dato un oggetto, ottiene le coordinate di un oggetto rispetto alla finestra. Queste coordinate possono essere utilizzate direttamente in una matrice di trasformazione, per
     * visualizzare su schermo l'oggetto.
     *
     *
     * <img src="doc-files/calcoloObj2Screen.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     * <pre>
     * screenX =   posX - ( positionInMap.x + windowCenter.x - tileWidth)
     * screenY = - posY + ( positionInMap.y + windowCenter.y - tileHeight)
    </pre> *
     *
     *
     *
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param posx
     * startX coordinate in formato tiledMap
     * @param posy
     * startY coordinate in formato tiledMap
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translateInScreenCoords(tiledMap: TiledMap, posx: Float, posy: Float): Point2 {
        workResult.setCoords(
            posx - (Math.round(tiledMap.positionInMap.x) + tiledMap.view().windowCenter.x - tiledMap.tileWidth),
            -posy + (Math.round(tiledMap.positionInMap.y) + tiledMap.view().windowCenter.y - tiledMap.tileHeight)
        )

        //workResult.integer();
        return workResult
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Dato un oggetto, ottiene le coordinate rispetto allo schermo di un oggetto partendo dalle sue coordinate fisiche (box2d).
     *
     *
     * <img src="doc-files/calcoloObj2Box2D.jpg"></img>
     *
     *
     *
     * Poi passiamo al sistema screen.
     *
     *
     * <img src="doc-files/calcoloObj2Screen.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema physic. Il sistema di riferimento finale è quello dello schermo.
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param posx
     * startX coord rispetto al mapCenter
     * @param posy
     * startY coord rispetto al mapCenter
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translatePhysicToScreenCoords(tiledMap: TiledMap, posx: Float, posy: Float): Point2 {
        workResult.setCoords(posx + Math.round(tiledMap.mapCenter.x), -posy + Math.round(tiledMap.mapCenter.y))

        // assert: workResult ora è in sistema di riferimento tiledMap
        return translateInScreenCoords(tiledMap, workResult.x, workResult.y)
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Dato un oggetto, ottiene le coordinate di un oggetto rispetto al sistema di coordinate tileMap, che corrisponde allo spigolo in alto a sx della mappa.
     *
     *
     * <img src="doc-files/calcoloObj2Box2D.jpg"></img>
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     * @param tiledMap
     * mappa su cui operare
     * @param posx
     * startX coordinate in formato tiledMap
     * @param posy
     * startY coordinate in formato tiledMap
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translatePhysicToMapCoords(tiledMap: TiledMap, posx: Float, posy: Float): Point2 {
        workResult.setCoords(posx + tiledMap.mapCenter.x, -posy + tiledMap.mapCenter.y)

        // TODO controllo su size.. da 0 a mapWidth
        return workResult
    }

    /**
     *
     *
     * **Questo metodo non è thread safe.**
     *
     *
     *
     *
     * Dato un oggetto, ottiene le coordinate di un oggetto rispetto al sistema di coordinate box2D, che corrisponde al mapCenter.
     *
     *
     * <img src="doc-files/calcoloObj2Box2D.jpg"></img>
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     * @param tiledMap
     * mappa su cui operare
     * @param posx
     * startX coordinate in formato tiledMap
     * @param posy
     * startY coordinate in formato tiledMap
     * @return coordinate del punto nel sistema di riferimento della windows.
     */
    fun translateInPhysicCoords(tiledMap: TiledMap, posx: Float, posy: Float): Point2 {
        workResult.setCoords(posx - tiledMap.mapCenter.x, -posy + tiledMap.mapCenter.y)
        return workResult
    }

    /**
     *
     *
     * Dato un oggetto, una matrice, ed una tiledMap, questo metodo provvede ad immettere nella matrice i valori di traslazione.
     *
     *
     *
     *
     * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
     *
     *
     * <img src="doc-files/calcoloMapCenter1.jpg"></img>
     *
     *
     *
     * e
     *
     *
     * <img src="doc-files/calcoloMapCenter2.jpg"></img>
     *
     * @see .positionInMap
     * @param matrix
     * @param tiledMap
     * @param obj
     */
    fun translateInScreenCoords(matrix: Matrix4x4, tiledMap: TiledMap, obj: ObjBase) {
        val point = translateInScreenCoords(tiledMap, obj)
        matrix.translate(point.x, point.y, 0f)
    }
}