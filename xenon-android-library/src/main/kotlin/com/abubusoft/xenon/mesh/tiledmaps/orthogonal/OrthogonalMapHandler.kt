/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.orthogonal;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.ScreenInfo;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier;
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapOptions;
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractMapHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerOffsetHolder;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.BufferManager;
import com.abubusoft.xenon.vbo.VertexBuffer;
import com.abubusoft.xenon.core.Uncryptable;

/**
 * drawer per le mappe ortogonali. Per il momento usa il codice nell'abstract map handler
 * 
 * @author xcesco
 *
 */
@Uncryptable
public class OrthogonalMapHandler extends AbstractMapHandler<OrthogonalMapController> {

	public OrthogonalMapHandler(TiledMap map) {
		super(map);

		map.mapWidth = map.tileColumns * map.tileWidth;
		map.mapHeight = map.tileRows * map.tileHeight;
	}

	@Override
	public void onBuildView(TiledMapView view, Camera camera, TiledMapOptions options) {
		ScreenInfo screenInfo = XenonGL.screenInfo;
		// impostiamo metodo di riempimento dello schermo
		view.windowDimension = 0;
		switch (options.fillScreenType) {
		case FILL_HEIGHT:
			view.windowDimension = map.mapHeight * screenInfo.correctionX;
			break;
		case FILL_WIDTH:
			view.windowDimension = map.mapWidth * screenInfo.correctionY;
			break;
		case FILL_CUSTOM_HEIGHT:
			view.windowDimension = options.visibleTiles * map.tileHeight;
			break;
		case FILL_CUSTOM_WIDTH:
			view.windowDimension = options.visibleTiles * map.tileWidth;
			break;
		}
		view.windowDimension *= options.visiblePercentage;
		view.distanceFromViewer = XenonMath.zDistanceForSquare(camera, view.windowDimension);

		view.windowWidth = (int) (view.windowDimension / screenInfo.correctionY);
		view.windowHeight = (int) (view.windowDimension / screenInfo.correctionX);

		view.mapMaxPositionValueX = map.mapWidth - map.view().windowWidth;
		view.mapMaxPositionValueY = map.mapHeight - map.view().windowHeight;

		int windowRemainderX = view.windowWidth % map.tileWidth;
		int windowRemainderY = view.windowHeight % map.tileHeight;

		// +2 per i bordi, +1 se la divisione contiene un resto
		view.windowTileColumns = (int) (view.windowWidth / map.tileWidth) + (windowRemainderX == 0 ? 0 : 1) + 2;
		view.windowTileRows = (int) (view.windowHeight / map.tileHeight) + (windowRemainderY == 0 ? 0 : 1) + 2;

		// calcoliamo il centro dello schermo, considerando che abbiamo 2
		// elementi in più per lato (quindi dobbiamo
		// togliere una dimensione per parte).
		view.windowCenter.x = (view.windowWidth / 2f) + map.tileWidth;
		view.windowCenter.y = (view.windowHeight / 2f) + map.tileHeight;

		// creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
		// i layer che hanno come dimensione delle tile le stesse di default.

		// dato che si parte da 0,0, quando si userà la matrice di proiezione bisognerà aggiustare l'origine (la camera di default
		// ce l'ha in mezzo allo schermo ma qua la si parte a disegnare da in alto a sx).
		float sceneX = 0;
		float sceneY = 0;
		view.windowVerticesBuffer = BufferManager.instance().createVertexBuffer(view.windowTileColumns * view.windowTileRows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC);
		for (int i = 0; i < view.windowTileRows; i++) {
			for (int j = 0; j < view.windowTileColumns; j++) {
				sceneX = j * map.tileWidth;
				sceneY = i * map.tileHeight;

				VertexQuadModifier.setVertexCoords(view.windowVerticesBuffer, i * view.windowTileColumns + j, sceneX, -sceneY, map.tileWidth, map.tileHeight, false);
			}
		}
		// lo impostiamo una volta per tutte, tanto non verrà mai cambiato
		view.windowVerticesBuffer.update();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler#convertScroll(com.abubusoft.xenon.math.Point2, float, float)
	 */
	@Override
	public void convertRawWindow2MapWindow(Point2 scrollInMap, float scrollXInScreen, float scrollYInScreen) {
		scrollInMap.x = scrollXInScreen;
		scrollInMap.y = scrollYInScreen;
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrthogonalMapController buildMapController(TiledMap map, Camera cameraValue) {
		controller=new OrthogonalMapController(map, cameraValue);
		return controller;
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrthogonalTiledLayerHandler buildTiledLayerHandler(TiledLayer layer) {
		return new OrthogonalTiledLayerHandler(layer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrthogonalImageLayerHandler buildImageLayerHandler(ImageLayer layer) {
		return new OrthogonalImageLayerHandler(layer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrthogonalObjectLayerHandler buildObjectLayerHandler(ObjectLayer layer) {
		return new OrthogonalObjectLayerHandler(layer);
	}

	@Override
	public void convertMap2ViewLayer(LayerOffsetHolder offsetHolder, int mapX, int mapY) {
		// http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
		offsetHolder.tileIndexX = mapX / map.tileWidth;
		offsetHolder.tileIndexY = mapY / map.tileHeight;
		
		offsetHolder.screenOffsetX = mapX % map.tileWidth;
		offsetHolder.screenOffsetY = mapY % map.tileHeight;
	}

}
