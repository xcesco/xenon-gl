package com.abubusoft.xenon.entity;

import java.lang.reflect.Array;

import com.abubusoft.xenon.entity.BaseEntity;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.texture.TextureReference;
import com.abubusoft.kripton.android.Logger;

/**
 * Space partiniong.
 * 
 * @author Francesco Benincasa
 * 
 */
public class GridEntity<E extends GridCell> extends BaseEntity {

	private static final long serialVersionUID = 158695967345870381L;

	/**
	 * Tipo di cella da istanziare
	 */
	private Class<E> cellClazz;

	/**
	 * le colonne pari sono messe più in basso di 1/2 tile
	 */
	public boolean oddColumnsLower;

	public E[] items;

	/**
	 * margine orizzontale della finestra
	 */
	public int windowHorizontalMargin;

	/**
	 * distanza tra il centro di un tile ed il successivo
	 */
	public int windowVerticalMargin;

	/**
	 * larghezza della tile
	 */
	public int tileWidth;

	public int tileEffectiveWidth;

	/**
	 * altezza della tile
	 */
	public int tileHeight;

	public int tileEffectiveHeight;

	/**
	 * 
	 */
	public float worldMinX;

	/**
	 * 
	 */
	public float worldMaxX;

	/**
	 * 
	 */
	public float worldMinY;

	/**
	 * 
	 */
	public float worldMaxY;

	/**
	 * centro della tile in basso a sinistra
	 */
	public Point2 initialTilePosition;

	/**
	 * <p>
	 * centro della window
	 * </p>
	 * 
	 * 
	 * <pre>
	 * +------+---------------+
	 * |      |               |
	 * |  W   |               |
	 * |      |               |
	 * +------+---------------+
	 * </pre>
	 * 
	 */
	public Point2 windowCenterInitial;

	/**
	 * world width
	 */
	public int worldWidth;

	/**
	 * world height
	 */
	public int worldHeight;

	/**
	 * window width
	 */
	public int windowWidth;

	/**
	 * offset massimo in larghezza con il quale la finestra si può spostare.
	 */
	public int windowMaxWitdhOffset;

	/**
	 * window height
	 */
	public int windowHeight;

	/**
	 * colonne visibili
	 */
	public int windowCols;

	/**
	 * indica se lo schermo è portrait
	 */
	public boolean windowLandscape;

	public float windowScaleFactor;

	public Point2 windowCenter;

	/**
	 * righe visibili
	 */
	public int windowRows;

	private int INFINITE_VALUE = 1000000000;

	public int gridRows;

	public int gridCols;

	// private Point3 oldPosition;

	private E[] windowsItems;

	private float lastOffsetY;

	private float lastOffsetX;

	/**
	 * indica se la finestra di visualizzazione è stata spostata.
	 */
	public boolean windowMoved;

	public GridEntity() {
		windowCenter = new Point2();
		windowCenterInitial = new Point2();
	}

	/**
	 * Definisce rows e cols
	 * 
	 * @param worldSizeX
	 * @param worldSizeY
	 * @param tileSizeValue
	 * @param options
	 */
	private void defineRowsAndCols(int worldSizeX, int worldSizeY, GridOptions options) {
		worldWidth = worldSizeX;
		worldHeight = worldSizeY;

		tileEffectiveWidth = (int) (tileWidth * options.marginHorizontal);
		tileEffectiveHeight = (int) (tileHeight * options.marginVertical);

		// dato che partiamo dal centro delle tile, dobbiamo mettere sempre e
		// comunque un pezzo per riempire
		// l'intera area.
		gridCols = worldWidth / tileEffectiveWidth + 1;
		gridRows = worldHeight / tileEffectiveHeight + 1;

		if (gridCols * tileEffectiveWidth < worldWidth)
			gridCols++;
		if (gridRows * tileEffectiveHeight < worldHeight)
			gridRows++;

		if (options.oddColumnsLower) {
			gridRows += 1;
		}
	}

	/**
	 * Definisce le coordinate delle varie tiles.
	 * 
	 * @param worldSizeX
	 * @param worldSizeY
	 * @param tileSizeValue
	 * @param options
	 */
	private void defineCoords(int worldSizeX, int worldSizeY, TextureReference textureRef, GridOptions options) {

		worldMinX = INFINITE_VALUE;
		worldMaxX = -INFINITE_VALUE;

		worldMinY = INFINITE_VALUE;
		worldMaxY = -INFINITE_VALUE;

		// Delta tra una tiles ed un altra
		Point3 position;

		float texY1;
		float texX1;

		float texY2;
		float texX2;

		int offsetX;
		int offsetY;

		initialTilePosition = new Point2();

		// ci posizioniamo sull'elemento in basso a sx
		// questa è la posizione del centro del primo tiles, quindi dobbiamo
		// spostarci della metà delle dimensioni
		// del tile
		float posX = (-worldSizeX) / 2.0f;
		float posY = (-worldSizeY) / 2.0f;

		// aspectRatio del world==1
		/*
		 * if (screenWidth/screenHeight==1 && oddColumnsLower) {
		 * posX-=tileEffectiveWidth*0.5f; }
		 */

		float cx, cy;

		// posizione in basso a sinistra della tile iniziale
		// questo viene fatto a prescindere dall'eventuale
		// offset delle colonne pari.
		// memorizziamo le coordinate del vertice del primo box più in basso
		initialTilePosition.setCoords(posX - tileEffectiveWidth * 0.5f, posY - tileEffectiveHeight * 0.5f);

		// l'incremento è quello legato alle reali dimensioni delle tiles
		offsetX = tileEffectiveWidth;
		offsetY = tileEffectiveHeight;

		try {
			cx = posX;
			for (int j = 0; j < gridCols; j++) {

				cy = posY;
				if (options.oddColumnsLower && (j % 2 == 1)) {
					cy -= tileHeight * 0.5f;
				}

				// resettiamo startX
				for (int i = 0; i < gridRows; i++) {
					// Posizione del tile
					position = new Point3(cx, cy, 0);// options.distanceFromViewer);

					// cx,cy rappresenta il centro della tile, lo 0.5 serve in
					// quanto
					// il sistema di coordinate ha centro in mezzo, quello delle
					// texture
					// in alto a sx
					// texX1 = (cx - tileWidth / 2f) / worldSizeX+0.5f;
					// texX2 = (cx + tileWidth / 2f) / worldSizeX+0.5f;
					// aspectRatio del world==1
					/*
					 * if (screenWidth/screenHeight==1 && oddColumnsLower) { texX1
					 * = (cx - tileWidth*0.25f) / worldSizeX+0.5f; texX2 = (cx +
					 * tileWidth*0.75f) / worldSizeX+0.5f; } else
					 */
					{
						texX1 = (cx) / worldSizeX + 0.5f;
						texX2 = (cx + tileWidth) / worldSizeX + 0.5f;
					}

					texY1 = -(cy + tileHeight / 2f) / worldSizeY + 0.5f;
					texY2 = -(cy - tileHeight / 2f) / worldSizeY + 0.5f;

					if (textureRef != null) {
						texY1 *= textureRef.get().info.dimension.normalizedMaxHeight;
						texY2 *= textureRef.get().info.dimension.normalizedMaxHeight;
					}

					E entity = cellClazz.newInstance();
					position.copyInto(entity.position);

					// deve essere >0, altrimenti viene considerato nullo
					entity.setTextureCoordinate(texX1, texX2, texY1, texY2);
					entity.setDimensions(tileWidth, tileHeight);

					items[gridRows * j + i] = entity;

					cy += offsetY;
				}
				cx += offsetX;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger.fatal(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Imposta le dimensioni delta griglia.
	 * </p>
	 * 
	 * @param rows
	 * @param cols
	 */
	@SuppressWarnings("unchecked")
	public void buildGrid(int worldSizeX, int worldSizeY, int windowWidthValue, int windowHeightValue, int tileWidthValue, int tileHeightValue, TextureReference textureRef, Class<E> cellClazzValue, GridOptions options) {
		cellClazz = cellClazzValue;

		// impostiamo le dimensioni delle tiles
		tileWidth = tileWidthValue;
		tileHeight = tileHeightValue;

		// rileviamo se siamo in landscape o portrait
		windowLandscape = windowWidthValue > windowHeightValue ? true : false;
		windowScaleFactor = options.windowScaleFactor;

		windowHorizontalMargin = (int) ((1f - options.marginHorizontal) * tileWidth * 0.25f);
		windowVerticalMargin = (int) ((1f - options.marginVertical) * tileHeight * 0.25f);

		// a causa di alcune situazioni limite, dobbiamo necessariamente
		// aumentare il margine orizzontale
		// in caso di landscape.
		/*
		 * if (windowLandscape) { windowHorizontalMargin*=2; }
		 */

		// senza movimento precedente è stata creato.
		windowMoved = true;

		oddColumnsLower = options.oddColumnsLower;

		// calcoliamo
		defineRowsAndCols(worldSizeX, worldSizeY, options);

		// impostiamo i parametri
		items = (E[]) Array.newInstance(cellClazz, gridRows * gridCols);

		// calcoliamo le coordinate
		defineCoords(worldSizeX, worldSizeY, textureRef, options);

		// costruiamo la finestra
		buildVisibleWindow(windowWidthValue, windowHeightValue);
	}

	/**
	 * <p>
	 * Imposta le dimensioni della finestra in base alle coordinate già
	 * trasformate rispetto al camera.
	 * </p>
	 * 
	 * @param windowWidthValue
	 * @param windowHeightValue
	 */
	@SuppressWarnings("unchecked")
	private void buildVisibleWindow(int windowWidthValue, int windowHeightValue) {
		lastOffsetX = -INFINITE_VALUE;
		lastOffsetY = -INFINITE_VALUE;

		windowWidth = (int) (windowWidthValue * windowScaleFactor);
		windowHeight = (int) (windowHeightValue * windowScaleFactor);

		// dobbiamo calcolare quanti tile sono visibili
		// windowCols = (int) (Math.floor(windowWidth / (tileWidth * (1f - (1f -
		// marginHorizontal) / 2f))) + 1);
		// windowRows = (int) (Math.floor(windowHeight / (tileHeight * (1f - (1f
		// - marginVertical) / 2f))) + 1);
		windowCols = (int) ((windowWidth / tileEffectiveWidth) + 2f) + 2;
		if (windowLandscape) {
			windowCols += 2;
		}

		windowRows = (int) ((windowHeight / tileEffectiveHeight) + 2f) + 1;

		windowCols = Math.min(gridCols, windowCols);
		windowRows = Math.min(gridRows, windowRows);

		// calcoliamo il centro iniziale
		// inizialmente l'origine del mondo coincide con il windowCenter.
		// per fare questo dobbiamo fare una trasformazione invertita:
		// da World -> WindowGrid
		// TODO portait
		// windowCenter.setCoords((-screenWidth+windowWidth-tileEffectiveWidth)*0.5f+windowHorizontalMargin,0f);
		// if (windowLandscape) {
		// dobbiamo diminuire width offset e l'inizio in quanto zdistance in
		// realtà dovrebbe essere
		//
		// windowCenter.setCoords((-screenWidth + windowWidth
		// -tileEffectiveWidth) * 0.5f, 0f);
		// windowMaxWitdhOffset = (int) (screenWidth - windowWidth - 3.2f *
		// windowHorizontalMargin);

		// } else {
		windowCenter.setCoords((-worldWidth + windowWidth - tileEffectiveWidth) * 0.5f + windowHorizontalMargin, 0f);
		// windowCenter.setCoords((-screenWidth + windowWidth -
		// tileEffectiveWidth) * 0.5f + windowHorizontalMargin, 0f);
		windowMaxWitdhOffset = (int) (worldWidth - windowWidth - 3.2f * windowHorizontalMargin);
		// }
		// windowCenter.setCoords((-screenWidth+windowWidth+tileEffectiveWidth)*0.5f+windowHorizontalMargin,0f);
		// windowCenter.setCoords((-screenWidth+windowWidth)*0.5f+windowHorizontalMargin,0f);
		windowCenter.copyInto(windowCenterInitial);

		// offset width massimo che si può spostare. Togliamo eventualmente il
		// margine orizzontale, 3 volte (sx e dx)
		// TODO portait
		// windowMaxWitdhOffset=(int)
		// (screenWidth-windowWidth-3.2f*windowHorizontalMargin);
		// windowMaxWitdhOffset=(int)
		// (screenWidth-windowWidth-3.2f*windowHorizontalMargin-tileEffectiveWidth);

		windowsItems = (E[]) Array.newInstance(cellClazz, windowCols * windowRows);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public E getCell(int row, int col) {
		return items[gridRows * col + row];
	}

	public E getVisibleCell(int row, int col) {
		return items[windowRows * col + row];
	}

	/**
	 * Recupera l'elemento
	 * 
	 * @param currentWindowX
	 * @param currentWindowY
	 * 
	 * @return
	 */
	public E getTouchedCell(float currentWindowX, float currentWindowY) {
		int index;

		// la collochiamo rispetto al sistema di riferimento della tile iniziale
		// initialTilePosition contiene le coordinate del centro della tile più
		// in basso
		// float currentTouchedPositionX = currentWindowX -
		// initialTilePosition.x +tileEffectiveWidth * 0.5f;
		// float currentTouchedPositionY = currentWindowY -
		// initialTilePosition.y + tileEffectiveHeight * 0.5f;

		// float currentTouchedPositionX = (currentWindowX +
		// windowCenter.x)-initialTilePosition.x -tileEffectiveWidth * 0.5f;
		// float currentTouchedPositionY = (currentWindowY +
		// windowCenter.y)-initialTilePosition.y - tileEffectiveHeight * 0.5f;
		float currentTouchedPositionX = (currentWindowX + windowCenter.x) - initialTilePosition.x;
		float currentTouchedPositionY = (currentWindowY + windowCenter.y) - initialTilePosition.y;

		// float currentTouchedPositionX = currentWindowX +tileEffectiveWidth *
		// 0.5f;
		// float currentTouchedPositionY = currentWindowY + tileEffectiveHeight
		// * 0.5f;

		// dobbiamo calcolare quanti tile sono visibili
		// aggiungiamo tileEffectiveWidth*0.5f per andare sicuramente oltre il
		// centro della tile.
		int currentCol = (int) (currentTouchedPositionX / tileEffectiveWidth);
		int currentRow = (int) (currentTouchedPositionY / tileEffectiveHeight);

		/*
		 * if (oddColumnsLower && (currentCol % 2 == 0)) { currentWindowY -=
		 * tileEffectiveHeight * 0.5f; }
		 */

		currentCol = currentCol < 0 ? 0 : currentCol;
		currentRow = currentRow < 0 ? 0 : currentRow;

		int touchedRow[] = {
				// colonna precedente
				currentRow - 1, currentCol - 1, currentRow, currentCol - 1, currentRow + 1, currentCol - 1,
				// colonna selezionata
				currentRow - 1, currentCol, currentRow, currentCol, currentRow + 1, currentCol,
				// colonna successiva
				currentRow - 1, currentCol + 1, currentRow, currentCol + 1, currentRow + 1, currentCol + 1 
				};
		int c, r;

		
		if ((tileEffectiveWidth / tileWidth) < 0.51f && windowLandscape) {
			Logger.debug("METODO  Landscape");
			// in caso di tile ristretto in larghezza e landscape, andiamo inditro
			for (int i = touchedRow.length - 2; i >= 0; i -= 2) {
				r = touchedRow[i];
				c = touchedRow[i + 1];

				if (isValid(r, c)) {
					// elemento centrale
					index = c * gridRows + r;
					if (isTouched(items[index], currentWindowX + windowCenter.x, currentWindowY + windowCenter.y)) {
						Logger.debug("---OK");
						return items[index];
					} else {
						Logger.error("NONE");
					}
				}
			}

		} else {
			Logger.debug("METODO  Portrait");
			for (int i = 0; i < touchedRow.length; i += 2) {
				r = touchedRow[i];
				c = touchedRow[i + 1];

				if (isValid(r, c)) {
					// elemento centrale
					index = c * gridRows + r;
					if (isTouched(items[index], currentWindowX + windowCenter.x, currentWindowY + windowCenter.y)) {
						Logger.debug("---OK");
						return items[index];
					} else {
						Logger.error("NONE");
					}
				}
			}

		}
		return null;
	}

	private final static float FACTOR = 0.5f;

	private boolean isValid(int row, int col) {
		return row >= 0 && row < gridRows && col >= 0 && col < gridCols;
	}

	private boolean isTouched(GridCell entity, float currentX, float currentY) {
		Logger.debug("Range X %s < %s < %s = %s", (entity.position.x - tileEffectiveWidth * FACTOR), currentX, (entity.position.x + tileEffectiveWidth * FACTOR),
				(currentX >= (entity.position.x - tileEffectiveWidth * FACTOR) && currentX <= (entity.position.x + tileEffectiveWidth * FACTOR)));
		Logger.debug("Range Y %s < %s < %s = %s", (entity.position.y - tileEffectiveHeight * FACTOR), currentY, (entity.position.y + tileEffectiveHeight * FACTOR),
				(currentY >= (entity.position.y - tileEffectiveHeight * FACTOR) && currentY <= (entity.position.y + tileEffectiveHeight * FACTOR)));
		if (currentX >= (entity.position.x - tileEffectiveWidth * FACTOR) && currentX <= (entity.position.x + tileEffectiveWidth * FACTOR)) {
			if (currentY >= (entity.position.y - tileEffectiveHeight * FACTOR) && currentY <= (entity.position.y + tileEffectiveHeight * FACTOR)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * @param camera
	 * @return
	 */
	public E[] scrollWindowTo(float offsetX, float offsetY) {

		if (offsetX == lastOffsetX && offsetY == lastOffsetY) {
			windowMoved = false;
			return windowsItems;
		}
		lastOffsetX = offsetX;
		lastOffsetY = offsetY;

		windowMoved = true;

		if (offsetX > windowMaxWitdhOffset) {

			offsetX = windowMaxWitdhOffset;
		}

		windowCenter.setCoords(windowCenterInitial.x + offsetX, windowCenterInitial.y + offsetY);

		// dobbiamo calcolare quanti tile sono visibili
		// prendiamo
		int currentCol = (int) ((windowCenter.x + (worldWidth * 0.5f)) / tileEffectiveWidth);
		int currentRow = (int) ((windowCenter.y + (worldHeight * 0.5f)) / tileEffectiveHeight);

		// primi elementi, si parte cmq da 0 come minimo
		int firstCol = currentCol - windowCols / 2;
		firstCol = firstCol < 0 ? 0 : firstCol;

		int firstRow = currentRow - windowRows / 2;
		firstRow = firstRow < 0 ? 0 : firstRow;

		// con questo controllo impediamo che la window vada oltre la
		// definizione
		// della grid
		if ((firstCol + windowCols) > gridCols) {
			firstCol = gridCols - windowCols;
		}

		int curCol = firstCol;
		int curRow = firstRow;

		// ricordiamoci che si parte dall'angolo in basso a sx della griglia, si
		// prosegue in verticale
		// e poi ci si sposta da dx
		for (int i = 0; i < windowCols; i++) {
			curRow = firstRow;
			for (int j = 0; j < windowRows; j++) {
				windowsItems[i * windowRows + j] = items[curCol * gridRows + curRow];
				curRow++;
			}
			curCol++;
		}

		return windowsItems;
	}
}
