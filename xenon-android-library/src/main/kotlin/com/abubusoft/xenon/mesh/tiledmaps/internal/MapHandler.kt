/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.XenonApplication4OpenGL
import com.abubusoft.xenon.camera.Camera

/**
 * Gestore di una tiledmap. Per ogni tipo di mappa, esiste un gestore
 *
 * @author xcesco
 */
interface MapHandler {
    /**
     *
     *
     * Disegna la mappa in base al controller.
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel [XenonApplication4OpenGL.onSceneReady])
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     * @param deltaTime
     */
    fun draw(deltaTime: Long)

    /**
     *
     *
     * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel [XenonApplication4OpenGL.onSceneReady])
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     *
     * @param deltaTime
     * tempo trascorso dall'ultimo draw
     * @param modelViewProjection
     * matrice MVP
     */
    fun draw(deltaTime: Long, modelViewProjection: Matrix4x4?)

    /**
     * Effettua il build della view. La sua implentazione cambia in base al tipo di orientamento della mappa.
     *
     * @param view
     * @param camera
     * @param options
     */
    fun onBuildView(view: TiledMapView?, camera: Camera?, options: TiledMapOptions?)

    /**
     * Converte lo scroll sullo schermo in scroll lato mappa, il valore viene salvato in mapValue
     *
     * @param mapValue
     * punto nel sistema di coordinate della mappa, quello della griglia di celle per intenderci.
     * @param screenX
     * ascissa sullo schermo
     * @param screenY
     * ordinata sullo schermo
     */
    fun convertRawWindow2MapWindow(mapPoint: Point2?, viewX: Float, viewY: Float)

    /**
     * Converte un punto da mappa a schermo (view).
     *
     * @param offsetHolder
     * punto nel sistema di coordinate della mappa, quello della griglia di celle per intenderci.
     * @param mapX
     * ascissa sulla view
     * @param mapY
     * ordinata sulla view
     */
    fun convertMap2ViewLayer(offsetHolder: LayerOffsetHolder?, mapX: Int, mapY: Int)

    /**
     * Costruisce controller per la mappa in questione
     *
     * @param tiledMap
     * @param cameraValue
     * @return
     * controller
     */
    fun <E : MapController?> buildMapController(tiledMap: TiledMap?, cameraValue: Camera?): E

    /**
     * Costruisce il gestore dei tiled layer
     *
     * @param layer
     * @return
     * gestore del tiled layer
     */
    fun <E : TiledLayerHandler?> buildTiledLayerHandler(layer: TiledLayer?): E

    /**
     * Costruisce il gestore degli object layer
     *
     * @param layer
     * @return
     * gestore del object layer
     */
    fun <E : ObjectLayerHandler?> buildObjectLayerHandler(layer: ObjectLayer?): E

    /**
     * Costruisce il gestore degli image layer
     *
     * @param layer
     * @return
     * gestore del image layer
     */
    fun <E : ImageLayerHandler?> buildImageLayerHandler(layer: ImageLayer?): E
}