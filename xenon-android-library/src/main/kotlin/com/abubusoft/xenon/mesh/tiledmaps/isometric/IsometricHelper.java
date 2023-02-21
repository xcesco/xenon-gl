package com.abubusoft.xenon.mesh.tiledmaps.isometric;

import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.modifiers.AttributeQuadModifier;
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier;
import com.abubusoft.xenon.vbo.AttributeBuffer;
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.BufferManager;
import com.abubusoft.xenon.vbo.VertexBuffer;

/**
 * <p>
 * Funzioni di utilità per le mappe isometriche a diamante.
 * </p>
 * 
 * @author xcesco
 *
 */
public abstract class IsometricHelper {

	static final Point2 workPoint = new Point2();

	static final Point2 workPoint2 = new Point2();

	/**
	 * <p>
	 * Converte le coordinate da schermo raw a (centered) window. Ricordiamo che il termine raw indica che il sistema di riferimento è in alto a sinistra. Sia lo screen che le coordinate window sono raw.
	 * </p>
	 * 
	 * <p>
	 * I passaggi sono due, da raw screen a raw window a centered window.
	 * </p>
	 * 
	 * 
	 * <img src="./doc-files/convertRawScreen2CenteredWindow.png"/>
	 * 
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param controller
	 * @param screenX
	 * @param screenY
	 * @return coordinate del punto nel sistema di riferimentod della centered window
	 */
	public static Point2 convertRawScreen2CenteredWindow(IsometricMapController controller, float screenX, float screenY) {
		// da screen a window
		workPoint.x = screenX * controller.screenToTiledMapFactor;
		workPoint.y = screenY * controller.screenToTiledMapFactor;

		// da raw screen a window
		workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth);
		workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight);

		return workPoint;
	}

	/**
	 * <p>
	 * Converte le coordinate da schermo raw a (iso) window. Ricordiamo che il termine raw indica che il sistema di riferimento è in alto a sinistra. La iso (centered) window invece è la window vista dalla prospettiva della mappa
	 * isometrica, quindi con gli assi ruotati e a 120°.
	 * </p>
	 * 
	 * <p>
	 * I passaggi sono due, da raw screen a raw window a centered window.
	 * </p>
	 * 
	 * 
	 * <img src="./doc-files/convertRawScreen2IsoWindow.png"/>
	 * 
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param controller
	 * @param screenX
	 * @param screenY
	 * @return coordinate del punto nel sistema di riferimento della iso (centered) window
	 */
	public static Point2 convertRawScreen2IsoWindow(IsometricMapController controller, float screenX, float screenY) {
		// da raw screen a raw window
		workPoint.x = screenX * controller.screenToTiledMapFactor;
		workPoint.y = screenY * controller.screenToTiledMapFactor;

		// da raw screen a window
		workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth);
		workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight);

		// da centered window a iso window
		workPoint2.x = workPoint.x - 2f * workPoint.y;
		workPoint2.y = workPoint.x + 2f * workPoint.y;

		return workPoint2;
	}

	/**
	 * <p>
	 * Converte le coordinate da iso window a centered window. Ricordiamo che la centered window ha il sistema di coordinate standard, e la iso (centered) window invece è la window vista dalla prospettiva della mappa isometrica, quindi con
	 * gli assi ruotati e a 120°.
	 * </p>
	 * 
	 * 
	 * <img src="./doc-files/convertRawWindow2IsoMap.png"/>
	 * 
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param isoX
	 * @param isoY
	 * @return coordinate del punto nel sistema di riferimento della centered window
	 */
	public static Point2 convertIsoMapOffset2ScreenOffset(float isoX, float isoY) {
		// TODO questo viene usato per l'offset
		// da map a iso window (consideriamo che hanno lo stesso punto d'origine)
		// isoX=isoX;
		isoY = -isoY;

		// versione fixata
		// ora convertiamo in centered window con y in alto
		// da iso window a centered window
		workPoint.x = (+isoX + isoY) / 2;
		workPoint.y = (-isoX + isoY) / 4;

		// per essere applicato al sistema di visualizzazione dobbiamo trasformare gli offset in scale * 2
		// questo perchè sul sistema della mappa
		workPoint.mul(2f);

		return workPoint;
	}

	/**
	 * <p>
	 * Da Iso Window a Raw Screen.
	 * </p>
	 * <img src="./doc-files/convertRawWindow2IsoMap.png"/>
	 * 
	 * <p>
	 * Le specifiche di trasformazione. Quelle che ci interessano vanno da C' a A'.
	 * </p>
	 * 
	 * <img src="./doc-files/coordinateTransformationInverse.png"/>
	 * 
	 * @param controller
	 * @param isoX
	 * @param isoY
	 * @return coordinate del punto riferite allo schermo
	 */
	public static Point2 convertIsoWindow2RawScreen(IsometricMapController controller, float isoX, float isoY) {
		// da iso window a centered window
		workPoint.x = (+isoX + isoY) / 2;
		workPoint.y = (-isoX + isoY) / 4;

		// da centered window a raw screen a distanza window
		workPoint.x = workPoint.x + (controller.map.view().windowCenter.x - controller.map.tileWidth);
		workPoint.y = workPoint.y - (controller.map.view().windowCenter.y - controller.map.tileHeight);
		// da distanzacentered window a raw screen
		workPoint.y = -workPoint.y;

		workPoint.x = workPoint.x / controller.screenToTiledMapFactor;
		workPoint.y = workPoint.y / controller.screenToTiledMapFactor;

		return workPoint;
	}

	/**
	 * <p>
	 * Costruisce un vertex buffer atto a contenere un rombo di tiles.
	 * </p>
	 * 
	 * <img src="./doc-files/Iso2Memory.png"/>
	 * 
	 * <p>
	 * Le righe della mappa sono sulla diagonale a sx, le colonne sulla diagonale a dx.
	 * </p>
	 * 
	 * <p>
	 * Il diamante viene generato considerando che l'origine del sistema è in centro. La punta che viene generata si trova in alto in centro al sisteam di coordinate.
	 * </p>
	 * 
	 * @param rows
	 *            righe del vertex buffer
	 * @param cols
	 *            colonne del vertex buffer
	 * @param stepWidth
	 *            step in larghezza tra un tile ed un altro
	 * @param stepHeight
	 *            step in altezza tra un tile ed un altro
	 * @param tileWidth
	 *            width delle tiles
	 * @param tileHeight
	 *            height delle tiles
	 * @return vertex buffer
	 */
	public static VertexBuffer buildDiamondVertexBuffer(int rows, int cols, float stepWidth, float stepHeight, float tileWidth, float tileHeight) {
		// creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
		// i layer che hanno come dimensione delle tile le stesse di default.
		float sceneX;
		float sceneY;
		VertexBuffer verticesBuffer = BufferManager.instance().createVertexBuffer(cols * rows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC);
		/*
		 * for (int i = 0; i < rows; i++) { for (int j = 0; j < cols; j++) { sceneX = (j - 1) * (stepWidth) - i * (stepWidth); sceneY = (rows - 1 - i) * stepHeight;
		 * 
		 * VertexQuadModifier.setVertexCoords(verticesBuffer, i * cols + j, sceneX, sceneY, tileWidth, tileHeight, false); } }
		 */

		// questo serve a metterlo al centro
		float baseY = (rows) * stepHeight;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				sceneX = (j - i) * stepWidth - stepWidth;
				sceneY = baseY - (j + i) * stepHeight;

				VertexQuadModifier.setVertexCoords(verticesBuffer, i * cols + j, sceneX, sceneY, tileWidth, tileHeight, false);
			}
		}

		return verticesBuffer;
	}

	public static AttributeBuffer buildDiamondOffsetAttributeBuffer(int rows, int cols) {
		// creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
		// i layer che hanno come dimensione delle tile le stesse di default.
		AttributeBuffer attributesBuffer = BufferManager.instance().createAttributeBuffer(cols * rows * VertexBuffer.VERTEX_IN_QUAD_TILE, AttributeDimensionType.DIM_2, BufferAllocationType.STATIC);

		// inizializziamo tutto a 0
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				AttributeQuadModifier.setVertexAttributes2(attributesBuffer, i * cols + j, 0f, 0f, false);
			}
		}

		return attributesBuffer;
	}

	/**
	 * <p>
	 * Converte da raw window a iso map, ovvero il punto
	 * </p>
	 * 
	 * <img src="./doc-files/convertRawWindow2IsoMap.png"/>
	 * 
	 * <p>
	 * Le specifiche di trasformazione. Quelle che ci interessano vanno da B a C.
	 * </p>
	 * 
	 * <img src="./doc-files/coordinateTransformation.png"/>
	 * 
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param controller
	 * @param rawWindowX
	 * @param rawWindowY
	 * @return punto con le coordinate iso window
	 */
	public static Point2 convertCenteredWindow2IsoWindow(IsometricMapController controller, float rawWindowX, float rawWindowY) {

		// da centered window a iso window
		workPoint.x = rawWindowX - 2f * rawWindowY;
		workPoint.y = rawWindowX + 2f * rawWindowY;

		// da iso windows a iso map (y verso il basso)
		workPoint.y = -workPoint.y;

		// usiamo il sistema di riferimento della mappa
		// workPoint.add(controller.map.positionInMap);
		// workPoint.add(((IsometricMapHandler) (controller.map.handler)).isoWindowSize);

		return workPoint;
	}

	/**
	 * <p>
	 * Converte da raw window a iso map, ovvero il punto
	 * </p>
	 * 
	 * <img src="./doc-files/convertRawWindow2IsoMap.png"/>
	 * 
	 * <p>
	 * Le specifiche di trasformazione. Quelle che ci interessano vanno da A a C.
	 * </p>
	 * 
	 * <img src="./doc-files/coordinateTransformation.png"/>
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param controller
	 * @param screenX
	 * @param screenY
	 * @return punto con le coordinate iso window
	 */
	public static Point2 convertRawScreen2IsoMap(IsometricMapController controller, float screenX, float screenY) {
		// da screen a raw window
		workPoint.x = screenX * controller.screenToTiledMapFactor;
		workPoint.y = screenY * controller.screenToTiledMapFactor;

		// da raw screen a centered window
		workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth);
		workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight);

		// da centered window a iso window
		workPoint2.x = workPoint.x - 2f * workPoint.y;
		workPoint2.y = workPoint.x + 2f * workPoint.y;

		// da iso windows a iso map (y verso il basso)
		workPoint2.y = -workPoint2.y;
		// usiamo il sistema di riferimento della mappa
		workPoint2.add(controller.map.positionInMap);
		workPoint2.add(((IsometricMapHandler) (controller.map.handler)).isoWindowSize / 2f);

		// diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
		// workPoint.div(((IsometricMapHandler) (controller.map.handler)).isoTileSize);

		return workPoint2;
	}

	/**
	 * <p>
	 * Converte da raw window a indici delle tile.
	 * </p>
	 * 
	 * <img src="./doc-files/convertRawWindow2IsoMap.png"/>
	 * 
	 * <p>
	 * Le specifiche di trasformazione. Quelle che ci interessano vanno da A a C.
	 * </p>
	 * 
	 * <img src="./doc-files/coordinateTransformation.png"/>
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param controller
	 * @param rawScreenX
	 * @param rawScreenY
	 * @return punto con le coordinate iso window
	 */
	public static Point2 convertRawScreen2IsoTileIndex(IsometricMapController controller, float rawScreenX, float rawScreenY) {
		// da screen a window
		workPoint.x = rawScreenX * controller.screenToTiledMapFactor;
		workPoint.y = rawScreenY * controller.screenToTiledMapFactor;

		// da raw screen a window
		workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth);
		workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight);

		// da centered window a iso window
		workPoint2.x = workPoint.x - 2f * workPoint.y;
		workPoint2.y = workPoint.x + 2f * workPoint.y;

		// da iso windows a iso map (y verso il basso)
		workPoint2.y = -workPoint2.y;

		// usiamo il sistema di riferimento della mappa
		workPoint2.add(controller.map.positionInMap);
		workPoint2.add(((IsometricMapHandler) (controller.map.handler)).isoWindowSize / 2f);

		// diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
		workPoint2.div(((IsometricMapHandler) (controller.map.handler)).isoTileSize);

		return workPoint2;
	}

	/**
	 * <p>
	 * Converte da raw window a indici delle tile.
	 * </p>
	 * 
	 * <img src="./doc-files/convertRawWindow2IsoMap.png"/>
	 * 
	 * <p>
	 * <b>Questo metodo non è thread safe.</b>
	 * </p>
	 * 
	 * @param controller
	 * @param rawScreenX
	 * @param rawScreenY
	 * @return punto con le coordinate iso window
	 */
	public static Point2 convertRawScreen2IsoTileOffset(IsometricMapController controller, float rawScreenX, float rawScreenY) {
		// da screen a window
		workPoint.x = rawScreenX * controller.screenToTiledMapFactor;
		workPoint.y = rawScreenY * controller.screenToTiledMapFactor;

		// da raw screen a window
		workPoint.x = workPoint.x - (controller.map.view().windowCenter.x - controller.map.tileWidth);
		workPoint.y = -workPoint.y + (controller.map.view().windowCenter.y - controller.map.tileHeight);

		// da centered window a iso window
		workPoint2.x = workPoint.x - 2f * workPoint.y;
		workPoint2.y = workPoint.x + 2f * workPoint.y;

		// da iso windows a iso map (y verso il basso)
		workPoint2.y = -workPoint2.y;

		// usiamo il sistema di riferimento della mappa
		workPoint2.add(controller.map.positionInMap);
		workPoint2.add(((IsometricMapHandler) (controller.map.handler)).isoWindowSize / 2f);

		// diviamo le coordinate per la dimensione delle tile, al fine di ottenere gli indici delle tile
		workPoint2.mod(((IsometricMapHandler) (controller.map.handler)).isoTileSize);

		return workPoint2;
	}

}
