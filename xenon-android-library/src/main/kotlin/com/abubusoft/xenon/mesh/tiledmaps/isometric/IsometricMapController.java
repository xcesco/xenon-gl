package com.abubusoft.xenon.mesh.tiledmaps.isometric;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController;
import com.abubusoft.kripton.android.Logger;

public class IsometricMapController extends AbstractMapController {

	public IsometricMapController(TiledMap mapValue, Camera cameraValue) {
		super(mapValue, cameraValue);

		// this.screenToTiledMapFactor=1/map.view().distanceFromViewer;
	}

	/**
	 * Converte un punto dello schermo nelle coordinate
	 * 
	 * @param screenX
	 * @param screenY
	 * 
	 * @return punto con coordinate sulla mappa
	 */
	public Point2 touch(float screenX, float screenY) {
		{
						
			Logger.info("Isometric map position (%s , %s), window size = %s, tile size = %s", map.positionInMap.x, map.positionInMap.y, ((IsometricMapHandler)(map.handler)).isoWindowSize, ((IsometricMapHandler)(map.handler)).isoTileSize);
		}
		
		{
			workScroll.set(IsometricHelper.convertRawScreen2CenteredWindow(this, screenX, screenY));			
			Logger.info("Convert raw screen (%s , %s) to window (%s , %s)", screenX, screenY, workScroll.x, workScroll.y);
		}
		
		{
			workScroll.set(IsometricHelper.convertRawScreen2IsoMap(this, screenX, screenY));			
			Logger.info("Convert raw screen (%s , %s) to map (%s , %s)", screenX, screenY, workScroll.x, workScroll.y);
		}
		
		{
			workScroll.set(IsometricHelper.convertRawScreen2IsoTileIndex(this, screenX, screenY));			
			Logger.info("Convert raw screen (%s , %s) to tile (%s , %s)", screenX, screenY, workScroll.x, workScroll.y);
		}
		
		{
			workScroll.set(IsometricHelper.convertRawScreen2IsoTileOffset(this, screenX, screenY));			
			Logger.info("Convert raw screen (%s , %s) to tile offset (%s , %s)", screenX, screenY, workScroll.x, workScroll.y);
		}

		// x verifica
		{
			workScroll.set(IsometricHelper.convertRawScreen2IsoWindow(this, screenX, screenY));
			Logger.info("Convert raw screen (%s , %s) to iso (%s , %s)", screenX, screenY, workScroll.x, workScroll.y);
			workScroll.set(IsometricHelper.convertIsoWindow2RawScreen(this, workScroll.x, workScroll.y));
			Logger.info("Check raw screen (%s , %s) to calculated (%s , %s)", screenX, screenY, workScroll.x, workScroll.y);
		}
		
		

		
		Logger.info("----ss--------------------");
		/*
		 * scroll.x=scroll.x-(this.map.view().windowCenter.x-map.tileWidth); scroll.y=(this.map.view().windowCenter.y- map.tileHeight)-scroll.y;
		 * 
		 * // versione 2 scroll.x = -(scroll.y -scroll.x / 2); scroll.y = (scroll.y + scroll.x / 2);
		 * 
		 * scroll.x= scroll.x+(map.positionInMap.x+(map.view().windowTileColumns*map.tileWidth*0.5f)); scroll.y=-scroll.y+(map.positionInMap.y+map.view().windowTileRows*map.tileHeight*0.5f);
		 * 
		 * Logger.info("Convert screen (%s , %s) to (%s , %s)", screenX, screenY, scroll.x, scroll.y); Logger.info("Convert screen (%s , %s) to tile (row: %s , col: %s)", screenX, screenY, (int)scroll.y/map.tileWidth,
		 * (int)scroll.x/map.tileHeight);
		 * 
		 * //TODO per il momento non funziona molto bene //map.handler.convertScreen2Map(scroll, screenX* screenToTiledMapFactor, screenY* screenToTiledMapFactor);
		 */
		return workScroll;
	}

	@Override
	public void position(float x, float y) {
		map.positionInMap.setCoords(x, y);

		if (map.scrollHorizontalLocked) {
			// se locked, la position non può andare oltre
			map.positionInMap.x = XenonMath.clamp(map.positionInMap.x, 0, map.view().mapMaxPositionValueX);
		} else {
			// la posizione nella mappa deve essere sempre circoscritta alle
			// dimensioni della mappa stessa, senza mai andare oltre
			if (map.positionInMap.x < 0f) {
				map.positionInMap.x += map.mapWidth;
			}

			if (map.positionInMap.x > map.mapWidth) {
				map.positionInMap.x -= map.mapWidth;
			}

			if (map.movementEventListener != null) {
				map.scrollHorizontalCurrentArea = (int) (map.positionInMap.x * map.listenerOptions.horizontalAreaInvSize);

				if (map.scrollHorizontalPreviousArea != map.scrollHorizontalCurrentArea) {
					map.scrollHorizontalPreviousArea = map.scrollHorizontalCurrentArea;
				}
			}
		}

		if (map.scrollVerticalLocked) {
			// se locked, la position non può andare oltre
			map.positionInMap.y = XenonMath.clamp(map.positionInMap.y, 0, map.view().mapMaxPositionValueY);
		} else {
			if (map.positionInMap.y < 0f)
				map.positionInMap.y += map.mapHeight;

			if (map.positionInMap.y > map.mapHeight)
				map.positionInMap.y -= map.mapHeight;

			if (map.movementEventListener != null) {
				map.scrollVerticalCurrentArea = (int) (map.positionInMap.x * map.listenerOptions.verticalAreaInvSize);

				if (map.scrollVerticalPreviousArea != map.scrollVerticalCurrentArea) {
					map.scrollVerticalCurrentArea = map.scrollVerticalPreviousArea;
				}
			}
		}

		for (int i = 0; i < map.layers.size(); i++) {
			map.layers.get(i).position(map.positionInMap.x, map.positionInMap.y);
		}

		// in caso di spostamento e di listener!=null mandiamo evento
		if (map.movementEventListener != null) {
			map.movementEventListener.onPosition(map.positionInMap.x, map.positionInMap.y);
		}

		map.resetScrollAreas();

	}

}
