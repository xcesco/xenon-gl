/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.ScrollDirectionType;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapPositionType;
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalHelper;
import com.abubusoft.xenon.opengl.XenonGL;

/**
 * @author xcesco
 *
 */
public abstract class AbstractMapController implements MapController {
	
	public AbstractMapController(TiledMap mapValue, Camera cameraValue) {
		map = mapValue;
		camera = cameraValue;
		
		matrix = new Matrix4x4();
		matrixModelViewProjection = new Matrix4x4();
		
		matrix.buildIdentityMatrix();
		matrix.translate(0, 0, -map.view().distanceFromViewer);

		matrixModelViewProjection.multiply(camera.info.projection4CameraMatrix, matrix);

		float screenSize = XenonMath.max(XenonGL.screenInfo.width, XenonGL.screenInfo.height);
		float windowOfTileSize = map.view().windowDimension;

		screenToTiledMapFactor = windowOfTileSize / screenSize;
		
		workScroll=new Point2();
	}
	
	/**
	 * serve a gestire lo scroll sulla mappa
	 */
	protected Point2 workScroll;
	
	/**
	 * 
	 */
	public final TiledMap map;

	/**
	 * 
	 */
	protected Matrix4x4 matrix;

	/**
	 * 
	 */
	public Matrix4x4 matrixModelViewProjection;

	/**
	 * <p>Camera di riferimento.</p>
	 */
	protected Camera camera;

	/**
	 * <p>
	 * Fattore di riduzione dei movimenti. Lo schermo è di una dimensione, la window sul tile di un altra. se non viene applicato questo fattore di riduzione, i movimenti applicati
	 * alla window avrebbero sarebbero dimensionati rispetto alle dimensioni dello schermo.
	 * </p>
	 */
	public float screenToTiledMapFactor;
	
	public Matrix4x4 getMatrixModelViewProjection()
	{
		return matrixModelViewProjection;
	}

	/**
	 * <p>
	 * Effettua lo scroll della tilemap, partendo da uno scroll lato schermo.
	 * </p>
	 * 
	 * @param rawScreenX
	 * @param rawScreenY
	 */
	public void scrollFromScreen(float rawScreenX, float rawScreenY) {
		scroll(rawScreenX *screenToTiledMapFactor, rawScreenY  * screenToTiledMapFactor);
	}
	
	/**
	 * Muove la posizione della mappa, nel sistema di coordinate della mappa
	 * 
	 * @param rawWindowX
	 * @param rawWindowY
	 */
	public void scroll(float rawWindowX, float rawWindowY) {

		// eliminiamo spostamenti troppo piccoli
		if (XenonMath.isEquals(rawWindowX, 0f)) {
			rawWindowX = 0f;
		}

		if (XenonMath.isEquals(rawWindowY, 0f)) {
			rawWindowY = 0f;
		}

		map.handler.convertRawWindow2MapWindow(workScroll, rawWindowX, rawWindowY);

		// eliminiamo spostamenti troppo piccoli
		if (XenonMath.isEquals(workScroll.x, 0f)) {
			workScroll.x = 0f;
		}

		if (XenonMath.isEquals(workScroll.y, 0f)) {
			workScroll.y = 0f;
		}

		map.positionInMap.addCoords(workScroll.x, workScroll.y);

		if (map.scrollHorizontalLocked) {
			map.positionInMap.x= XenonMath.clamp(map.positionInMap.x, 0f, map.view().mapMaxPositionValueX);
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
					map.movementEventListener.onAreaChange(ScrollDirectionType.HORIZONTAL_SCROLL, map.scrollHorizontalCurrentArea);
					map.scrollHorizontalPreviousArea = map.scrollHorizontalCurrentArea;
				}
			}
		}

		if (map.scrollVerticalLocked) {
			// se locked, la position non può andare oltre
			map.positionInMap.y= XenonMath.clamp(map.positionInMap.y, 0f, map.view().mapMaxPositionValueY);
			
		} else {
			if (map.positionInMap.y < 0f)
				map.positionInMap.y += map.mapHeight;

			if (map.positionInMap.y > map.mapHeight)
				map.positionInMap.y -= map.mapHeight;

			if (map.movementEventListener != null) {
				map.scrollVerticalCurrentArea = (int) (map.positionInMap.y * map.listenerOptions.verticalAreaInvSize);

				if (map.scrollVerticalPreviousArea != map.scrollVerticalCurrentArea) {
					map.movementEventListener.onAreaChange(ScrollDirectionType.VERTICAL_SCROLL, map.scrollVerticalCurrentArea);
					map.scrollVerticalCurrentArea = map.scrollVerticalPreviousArea;
				}
			}
		}
		
		//Logger.info("--> position (%s, %s) , -- scroll (%s, %s) ", map.positionInMap.x, map.positionInMap.y, workScroll.x, workScroll.y);

		for (int i = 0; i < map.layers.size(); i++) {
			map.layers.get(i).scroll(workScroll.x, workScroll.y);
		}

		// in caso di spostamento e di listener!=null mandiamo evento
		if (map.movementEventListener != null) {
			map.movementEventListener.onPosition(map.positionInMap.x, map.positionInMap.y);
		}
	}

	/**
	 * <p>
	 * Effettua lo spostamento della mappa.
	 * </p>
	 * 
	 * <p>
	 * Le coordinate sono espresse con il sistema di riferimento degli oggetti, ovvero quello che ha come origine il punto in alto a sinistra della mappa (con startY verso il
	 * basso).
	 * </p>
	 * 
	 * @param x
	 * @param y
	 * @param positionType
	 */
	public void position(float x, float y, TiledMapPositionType positionType) {
		switch (positionType) {
		case LEFT_TOP:
			position(x, y);
			break;
		case LEFT_CENTER:
			position(x, y + map.tileHeight - map.view().windowHeight * .5f);
			break;
		case LEFT_BOTTOM:
			position(x, y + map.tileHeight - map.view().windowHeight);
			break;
		case MIDDLE_CENTER:
			position(x - map.view().windowWidth * .5f + map.tileWidth, y + map.tileHeight - map.view().windowHeight * .5f);
			break;
		default:
		}
	}

	/**
	 * Converte un punto dello schermo nelle coordinate 
	 * @param screenX
	 * @param screenY
	 * 
	 * @return
	 * 		punto con coordinate sulla mappa
	 */
	public Point2 touch(float screenX, float screenY) {		
		Point2 point = OrthogonalHelper.translateScreenCoordsToTiledMap(map, screenX * screenToTiledMapFactor, screenY * screenToTiledMapFactor);

		return point;
	}

	/**
	 * <p>
	 * Effettua lo spostamento della mappa.
	 * </p>
	 * 
	 * <p>
	 * Le coordinate sono espresse con il sistema di riferimento dello schermo, quindi le coordinate devo essere convertite.
	 * </p>
	 * 
	 * @param screenX
	 * @param screenY
	 */
	public void positionFromScreen(float screenX, float screenY, TiledMapPositionType positionType) {		
		position((int) (screenX * screenToTiledMapFactor), (int) (screenY * screenToTiledMapFactor), positionType);
	}

	public void zoom(float value) {
	}
}
