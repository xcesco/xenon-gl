package com.abubusoft.xenon.mesh.tiledmaps.orthogonal;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController;

/**
 * @author Francesco Benincasa
 * 
 */
public class OrthogonalMapController extends AbstractMapController implements MapController {

	/**
	 * <p>
	 * Costuttore. Effettua tutte le operazioni necessarie per farlo funzionare.
	 * 
	 * </p>
	 * 
	 * @param mapValue
	 * @param cameraValue
	 */
	public OrthogonalMapController(TiledMap mapValue, Camera cameraValue) {
		super(mapValue, cameraValue);
	}
	
	/**
	 * Muove la posizione della mappa, in pixel.
	 * 
	 * TODO da testare
	 * 
	 * @param x
	 * @param y
	 */
	public void position(float x, float y) {
		map.positionInMap.setCoords(x, y);

		if (map.scrollHorizontalLocked) {
			// se locked, la position non può andare oltre
			if (map.positionInMap.x < 0f)
				map.positionInMap.x = 0f;

			if (map.positionInMap.x > (map.view().mapMaxPositionValueX)) {
				map.positionInMap.x = (map.view().mapMaxPositionValueX);
			}
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
			if (map.positionInMap.y < 0f)
				map.positionInMap.y = 0f;

			if (map.positionInMap.y > map.view().mapMaxPositionValueY) {
				map.positionInMap.y = map.view().mapMaxPositionValueY;
			}
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
