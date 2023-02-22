/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.XenonApplication4OpenGL;
import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapOptions;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController;

/**
 * Gestore di una tiledmap. Per ogni tipo di mappa, esiste un gestore
 * 
 * @author xcesco
 *
 */
public interface MapHandler {

	/**
	 * <p>
	 * Disegna la mappa in base al controller.
	 * </p>
	 * 
	 * <p>
	 * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel {@link XenonApplication4OpenGL#onSceneReady(android.content.SharedPreferences, boolean, boolean, boolean)})
	 * </p>
	 * 
	 * <pre>
	 * GLES20.glEnable(GLES20.GL_BLEND);
	 * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	 * </pre>
	 * 
	 * @param deltaTime
	 */
	void draw(long deltaTime);

	/**
	 * <p>
	 * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
	 * </p>
	 * 
	 * <p>
	 * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel {@link XenonApplication4OpenGL#onSceneReady(android.content.SharedPreferences, boolean, boolean, boolean)})
	 * </p>
	 * 
	 * <pre>
	 * GLES20.glEnable(GLES20.GL_BLEND);
	 * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	 * </pre>
	 * 
	 * 
	 * @param deltaTime
	 *            tempo trascorso dall'ultimo draw
	 * @param modelViewProjection
	 *            matrice MVP
	 */
	public void draw(long deltaTime, Matrix4x4 modelViewProjection);

	/**
	 * Effettua il build della view. La sua implentazione cambia in base al tipo di orientamento della mappa.
	 * 
	 * @param view
	 * @param camera
	 * @param options
	 */
	void onBuildView(TiledMapView view, Camera camera, TiledMapOptions options);

	/**
	 * Converte lo scroll sullo schermo in scroll lato mappa, il valore viene salvato in mapValue
	 * 
	 * @param mapValue
	 * 			punto nel sistema di coordinate della mappa, quello della griglia di celle per intenderci.
	 * @param screenX
	 * 			ascissa sullo schermo
	 * @param screenY
	 * 			ordinata sullo schermo
	 */
	void convertRawWindow2MapWindow(Point2 mapPoint, float viewX, float viewY);
	
	/**
	 * Converte un punto da mappa a schermo (view).
	 * 
	 * @param offsetHolder
	 * 			punto nel sistema di coordinate della mappa, quello della griglia di celle per intenderci.
	 * @param mapX
	 * 			ascissa sulla view
	 * @param mapY
	 * 			ordinata sulla view
	 */
	void convertMap2ViewLayer(LayerOffsetHolder offsetHolder, int mapX, int mapY);

	/**
	 * Costruisce controller per la mappa in questione
	 * 
	 * @param tiledMap
	 * @param cameraValue
	 * @return
	 * 		controller
	 */
	<E extends MapController> E buildMapController(TiledMap tiledMap, Camera cameraValue);
	
	/**
	 * Costruisce il gestore dei tiled layer
	 * 
	 * @param layer
	 * @return
	 * 		gestore del tiled layer
	 */
	<E extends TiledLayerHandler> E buildTiledLayerHandler(TiledLayer layer);

	/**
	 * Costruisce il gestore degli object layer
	 * 
	 * @param layer
	 * @return
	 * 		gestore del object layer
	 */
	<E extends ObjectLayerHandler> E buildObjectLayerHandler(ObjectLayer layer);
	
	/**
	 * Costruisce il gestore degli image layer
	 * 
	 * @param layer
	 * @return
	 * 		gestore del image layer
	 */
	<E extends ImageLayerHandler> E buildImageLayerHandler(ImageLayer layer);

}
