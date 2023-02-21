/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.orthogonal;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.ObjBase;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.kripton.android.Logger;

/**
 * <p>
 * <b>Tutti i metodi di questa classe non sono thread safe.</b>
 * </p>
 * <p>
 * Questa classe fornisce dei metodi d'aiuto per convertire le coordinate fornite in input in coordinate utili per la mappa.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class OrthogonalHelper {

	/**
	 * <p>
	 * variabile di lavoro
	 * </p>
	 */
	private final static Point2 workResult = new Point2();

	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Dato un oggetto, ottiene le coordinate di un oggetto rispetto alla finestra. Queste coordinate possono essere utilizzate direttamente in una matrice di trasformazione, per
	 * visualizzare su schermo l'oggetto.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloMapCenter1.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * 
	 * <pre>
	 *      screenX =   posX - ( positionInMap.x + windowCenter.x - tileWidth)
	 *      screenY = - posY + ( positionInMap.y + windowCenter.y - tileHeight)
	 * </pre>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param obj
	 *            oggetto su cui si lavora
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static Point2 translateInScreenCoords(TiledMap tiledMap, ObjBase obj) {
		workResult.setCoords((obj.x + obj.width * 0.5f) + tiledMap.tileWidth - (Math.round(tiledMap.positionInMap.x) + tiledMap.view().windowCenter.x), -(obj.y + obj.height * 0.5f) - tiledMap.tileHeight + (Math.round(tiledMap.positionInMap.y) + tiledMap.view().windowCenter.y));
		return workResult;
	}
	
	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Dato un oggetto, ottiene le coordinate di un oggetto rispetto alla finestra. Queste coordinate possono essere utilizzate direttamente in una matrice di trasformazione, per
	 * visualizzare su schermo l'oggetto. Le coordinate screen sono state già riportate alle giuste proporzioni. Questo metodo quindi non si preoccupa di 
	 * moltiplicarle per il fattore di conversione da schermo, dato che è stato già fatto.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Box2D.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * 
	 * <pre>
	 *      screenX =   posX + positionInMap.x
	 *      screenY =   posY + positionInMap.y
	 * </pre>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param obj
	 *            oggetto su cui si lavora
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static Point2 translateScreenCoordsToTiledMap(TiledMap tiledMap, float screenX, float screenY) {
		workResult.setCoords(screenX+Math.round(tiledMap.positionInMap.x) , screenY+Math.round(tiledMap.positionInMap.y));
		Logger.info("Convert screen (%s, %s) to map (%s, %s)", screenX, screenY,workResult.x, workResult.y );
		return workResult;
	}
	
	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Date le coordinate prese dallo schermo, recuperiamo l'id della tile che riesede a quelle coordinate.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Box2D.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * 
	 * <pre>
	 *      screenX =   posX + positionInMap.x
	 *      screenY =   posY + positionInMap.y
	 * </pre>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param obj
	 *            oggetto su cui si lavora
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static int convertRawScreenToTileId(TiledMap tiledMap, float screenX, float screenY) {
		int x=(int) (screenX+Math.round(tiledMap.positionInMap.x));
		int y=(int) (screenY+Math.round(tiledMap.positionInMap.y));
		// dobbiamo evitare arrotondamenti tra x e y, quindi convertiamo tutto in int separatamente
		int res=(int)(y/tiledMap.tileHeight*tiledMap.tileColumns)+(int)(x/tiledMap.tileWidth);
		
		Logger.info("Convert screen (%s, %s) to mapId (%s)", screenX, screenY,res );
		
		return res;
	}
	
	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Date le coordinate prese dallo schermo, recuperiamo l'id della tile che riesede a quelle coordinate.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Box2D.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * 
	 * <pre>
	 *      screenX =   posX + positionInMap.x
	 *      screenY =   posY + positionInMap.y
	 * </pre>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param obj
	 *            oggetto su cui si lavora
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static int translateMapCoordsToTileId(TiledMap tiledMap, float mapX, float mapY) {
		/*int x=(int) (Math.round(mapX));
		int y=(int) (Math.round(mapY));*/
		int x=(int) mapX;
		int y=(int) mapY;
		int res=y/tiledMap.tileHeight*tiledMap.tileColumns+x/tiledMap.tileWidth;
		
		Logger.info("Convert map (%s, %s) to mapId (%s)", x, y,res );
		return res;
	}

	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Dato un oggetto, ottiene le coordinate di un oggetto rispetto alla finestra. Queste coordinate possono essere utilizzate direttamente in una matrice di trasformazione, per
	 * visualizzare su schermo l'oggetto.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Screen.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * <pre>
	 *      screenX =   posX - ( positionInMap.x + windowCenter.x - tileWidth)
	 *      screenY = - posY + ( positionInMap.y + windowCenter.y - tileHeight)
	 * </pre>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param posx
	 *            startX coordinate in formato tiledMap
	 * @param posy
	 *            startY coordinate in formato tiledMap
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static Point2 translateInScreenCoords(TiledMap tiledMap, float posx, float posy) {
		workResult.setCoords(posx - (Math.round(tiledMap.positionInMap.x) + tiledMap.view().windowCenter.x - tiledMap.tileWidth), -posy + (Math.round(tiledMap.positionInMap.y) + tiledMap.view().windowCenter.y - tiledMap.tileHeight));

		//workResult.integer();
		
		return workResult;
	}

	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Dato un oggetto, ottiene le coordinate rispetto allo schermo di un oggetto partendo dalle sue coordinate fisiche (box2d).
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Box2D.jpg"/>
	 * 
	 * <p>
	 * Poi passiamo al sistema screen.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Screen.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema physic. Il sistema di riferimento finale è quello dello schermo.
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param posx
	 *            startX coord rispetto al mapCenter
	 * @param posy
	 *            startY coord rispetto al mapCenter
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static Point2 translatePhysicToScreenCoords(TiledMap tiledMap, float posx, float posy) {
		workResult.setCoords(posx + Math.round(tiledMap.mapCenter.x), -posy + Math.round(tiledMap.mapCenter.y));

		// assert: workResult ora è in sistema di riferimento tiledMap
		return translateInScreenCoords(tiledMap, workResult.x, workResult.y);
	}

	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Dato un oggetto, ottiene le coordinate di un oggetto rispetto al sistema di coordinate tileMap, che corrisponde allo spigolo in alto a sx della mappa.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Box2D.jpg"/>
	 * 
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa. </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param posx
	 *            startX coordinate in formato tiledMap
	 * @param posy
	 *            startY coordinate in formato tiledMap
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static Point2 translatePhysicToMapCoords(TiledMap tiledMap, float posx, float posy) {
		workResult.setCoords(posx + tiledMap.mapCenter.x, -posy + tiledMap.mapCenter.y);

		// TODO controllo su size.. da 0 a mapWidth

		return workResult;
	}

	/**
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * <p>
	 * Dato un oggetto, ottiene le coordinate di un oggetto rispetto al sistema di coordinate box2D, che corrisponde al mapCenter.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloObj2Box2D.jpg"/>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa su cui operare
	 * @param posx
	 *            startX coordinate in formato tiledMap
	 * @param posy
	 *            startY coordinate in formato tiledMap
	 * @return coordinate del punto nel sistema di riferimento della windows.
	 */
	public static Point2 translateInPhysicCoords(TiledMap tiledMap, float posx, float posy) {
		workResult.setCoords(posx - tiledMap.mapCenter.x, -posy + tiledMap.mapCenter.y);

		return workResult;
	}

	/**
	 * <p>
	 * Dato un oggetto, una matrice, ed una tiledMap, questo metodo provvede ad immettere nella matrice i valori di traslazione.
	 * </p>
	 * 
	 * <p>
	 * Il sistema di riferimento iniziale è il sistema tiledMap con centro in alto a sx della mappa.
	 * </p>
	 * 
	 * <img src="doc-files/calcoloMapCenter1.jpg"/>
	 * 
	 * <p>
	 * e
	 * </p>
	 * 
	 * <img src="doc-files/calcoloMapCenter2.jpg"/>
	 * 
	 * @see #positionInMap(TiledMap, TileObject)
	 * 
	 * @param matrix
	 * @param tiledMap
	 * @param obj
	 */
	public static void translateInScreenCoords(Matrix4x4 matrix, TiledMap tiledMap, ObjBase obj) {
		Point2 point = translateInScreenCoords(tiledMap, obj);
		matrix.translate(point.x, point.y, 0f);
	}
}
