package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapPositionType;

public interface MapController {

	Matrix4x4 getMatrixModelViewProjection();
	
	/**
	 * <p>
	 * Effettua lo scroll della tilemap, partendo da uno scroll lato schermo.
	 * </p>
	 * 
	 * @param screenDistanceX
	 * @param screenDistanceY
	 */
	public void scrollFromScreen(float screenDistanceX, float screenDistanceY);

	/**
	 * <p>
	 * Effettua lo scroll della tilemap con uno spostamento calcolto rispetto allo schermo. Le distanze vengono quindi modificate.
	 * </p>
	 * 
	 * @param distanceX
	 * @param distanceY
	 */
	public void scroll(float distanceX, float distanceY);

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
	public void position(float x, float y, TiledMapPositionType positionType);

	/**
	 * Converte un punto dello schermo nelle coordinate 
	 * @param screenX
	 * @param screenY
	 * 
	 * @return
	 */
	public Point2 touch(float screenX, float screenY);
	
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
	public void positionFromScreen(float screenX, float screenY, TiledMapPositionType positionType);

	public void zoom(float value);
	
	public void position(float x, float y);
}
