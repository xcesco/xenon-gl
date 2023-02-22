/**
 * 
 */
package com.abubusoft.xenon.mesh;

import com.abubusoft.xenon.animations.TextureTimeline;
import com.abubusoft.xenon.entity.GridCell;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.tiledmaps.Tile;
import com.abubusoft.xenon.texture.TextureRegion;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.IndexBuffer;
import com.abubusoft.xenon.vbo.TextureBuffer;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindDisabled;
import com.abubusoft.kripton.annotation.BindType;

/**
 * <p>
 * Mesh atta a contenere l'insieme di tile che devono essere disegnati. Per il fatto che ogni tile può avere delle dimensioni diverse da quelle standard.
 * </p>
 * 
 * <p>
 * Viene definito una dimensione massima dello shape, ma si possono utilizzare anche di meno, grazie all'uso del cursore.
 * </p>
 * 
 * <p>
 * Ogni tile è definito da un quad tile: due triangoli con due vertici in comune.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@BindType
public class MeshGrid extends QuadMesh {

	private static final long serialVersionUID = 673553443788789280L;

	/**
	 * Così non può essere definito se non nella factory
	 */
	MeshGrid() {
		//
		tempVertexSource = new float[3];
		tempVertexDestV2 = new float[3];
		tempVertexDestV1 = new float[3];
		tempVertexDestV3 = new float[3];
	}

	/**
	 * indica la tile corrente. Va da 0 fino al massimo di gridCols*gridRows
	 */
	@Bind
	public int cursor;

	/**
	 * <p>
	 * Imposta al primo tile
	 */
	public void cursorReset() {
		cursor = 0;
	}

	/**
	 * <p>
	 * Sposta il cursore al prossimo tile, quindi +1
	 * </p>
	 */
	public void cursorMove() {
		//
		cursor += 1;
	}

	/**
	 * <p>
	 * Legge il cursore attuale
	 * </p>
	 * 
	 */
	public int cursorRead() {
		return cursor;
	}

	/**
	 * vettore usato per la rototraslazione dei vertici
	 */
	@BindDisabled
	private transient float[] tempVertexSource;

	/**
	 * vettore usato per la rototraslazione dei vertici
	 */
	@BindDisabled
	private transient float[] tempVertexDestV2;

	/**
	 * vettore usato per la rototraslazione dei vertici
	 */
	@BindDisabled
	private transient float[] tempVertexDestV1;

	/**
	 * vettore usato per la rototraslazione dei vertici
	 */
	@BindDisabled
	private transient float[] tempVertexDestV3;

	public static final int TEXTURE_DEFAULT_INDEX = 0;

	/**
	 * variabili temporanee per l'allocazione dei vertici.
	 */
	@BindDisabled
	private transient float v1x, v1y, v2x, v2y, v3x, v3y, v4x, v4y;

	/**
	 * dimensioni di default della tile
	 */
	@Bind
	public int tileWidth;

	/**
	 * dimensioni di default della tile
	 */
	@Bind
	public int tileHeight;

	/**
	 * numero di righe
	 */
	@Bind
	public int gridRows;

	/**
	 * numero di colonne
	 */
	@Bind
	public int gridCols;

	/**
	 * variabile temporanea usata all'interno dei metodi
	 */	
	@BindDisabled
	private transient int basePtr;

	/**
	 * <p>
	 * Applica ai vertici di una cell una matrice di trasformazione. Il sistema di riferimento utilizzato è quello dell'intero entity.
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | 1    4 |
	 * |        |
	 * | 2    3 |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * I triangoli hanno i vertici: 123 e 341
	 * </p>
	 * <p>
	 * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
	 * </p>
	 * 
	 * @param selectedRow
	 * @param selectedColumn
	 * @param leftX
	 * @param topY
	 * @param tile
	 * 
	 */
	/*
	 * public void setVertexCoords(int selectedRow, int selectedColumn, Matrix4x4 matrix, GridCell cell) {
	 * 
	 * // triangle 1: 123 basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr +
	 * OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y]; tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; // mettiamo in
	 * tempVertexDestV1 così da poterlo riusare matrix.multiply(tempVertexSource, tempVertexDestV1); vertices.coords[basePtr + OFFSET_X] = tempVertexDestV1[OFFSET_X];
	 * vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV1[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV1[OFFSET_Z];
	 * 
	 * basePtr += VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr + OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y];
	 * tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; matrix.multiply(tempVertexSource, tempVertexDestV2); vertices.coords[basePtr + OFFSET_X] =
	 * tempVertexDestV2[OFFSET_X]; vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z];
	 * 
	 * basePtr += VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr + OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y];
	 * tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; matrix.multiply(tempVertexSource, tempVertexDestV3); vertices.coords[basePtr + OFFSET_X] =
	 * tempVertexDestV3[OFFSET_X]; vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV3[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV3[OFFSET_Z];
	 * 
	 * // triangle 2: 341
	 * 
	 * basePtr += VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr + OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y];
	 * tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; matrix.multiply(tempVertexSource, tempVertexDestV2); vertices.coords[basePtr + OFFSET_X] =
	 * tempVertexDestV2[OFFSET_X]; vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z];
	 * 
	 * }
	 */

	/**
	 * <p>
	 * Imposta i vertici per un tile (row, col). La z viene ricavata dalle posizioni
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | 1    4 |
	 * |        |
	 * | 2    3 |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * I triangoli hanno i vertici: 123 e 341
	 * </p>
	 * <p>
	 * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
	 * </p>
	 * 
	 * @param selectedRow
	 * @param selectedColumn
	 * @param leftX
	 * @param topY
	 * 
	 */
	public void setVertexCoords(int selectedRow, int selectedColumn, float leftX, float topY, Matrix4x4 matrix, GridCell cell) {
		float TILE_WIDTH_T = cell.width / 2f;
		float TILE_HEIGHT_T = cell.height / 2f;

		float centerX = leftX + TILE_WIDTH_T;
		float centerY = topY - TILE_HEIGHT_T;

		// triangle 1: 123
		basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS;
		tempVertexSource[OFFSET_X] = -TILE_WIDTH_T;
		tempVertexSource[OFFSET_Y] = TILE_HEIGHT_T;
		tempVertexSource[OFFSET_Z] = 0;
		// mettiamo in tempVertexDestV1 così da poterlo riusare
		matrix.multiply(tempVertexSource, tempVertexDestV1);
		vertices.coords[basePtr + OFFSET_X] = tempVertexDestV1[OFFSET_X] + centerX;
		vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV1[OFFSET_Y] + centerY;
		vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV1[OFFSET_Z];

		basePtr += VERTICES_DIMENSIONS;
		tempVertexSource[OFFSET_X] = -TILE_WIDTH_T;
		tempVertexSource[OFFSET_Y] = -TILE_HEIGHT_T;
		tempVertexSource[OFFSET_Z] = 0;
		matrix.multiply(tempVertexSource, tempVertexDestV2);
		vertices.coords[basePtr + OFFSET_X] = tempVertexDestV2[OFFSET_X] + centerX;
		vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y] + centerY;
		vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z];

		basePtr += VERTICES_DIMENSIONS;
		tempVertexSource[OFFSET_X] = TILE_WIDTH_T;
		tempVertexSource[OFFSET_Y] = -TILE_HEIGHT_T;
		tempVertexSource[OFFSET_Z] = 0;
		matrix.multiply(tempVertexSource, tempVertexDestV3);
		vertices.coords[basePtr + OFFSET_X] = tempVertexDestV3[OFFSET_X] + centerX;
		vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV3[OFFSET_Y] + centerY;
		vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV3[OFFSET_Z];

		basePtr += VERTICES_DIMENSIONS;
		tempVertexSource[OFFSET_X] = TILE_WIDTH_T;
		tempVertexSource[OFFSET_Y] = TILE_HEIGHT_T;
		tempVertexSource[OFFSET_Z] = 0;
		matrix.multiply(tempVertexSource, tempVertexDestV2);
		vertices.coords[basePtr + OFFSET_X] = tempVertexDestV2[OFFSET_X] + centerX;
		vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y] + centerY;
		vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z];

	}

	/**
	 * <p>
	 * Usa il cursore per indicare un tile. Questa funzione mette a 0 la z.
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | 1    4 |
	 * |        |
	 * | 2    3 |
	 * +--------+
	 * </pre>
	 * 
	 * <pre>
	 * 1   1(6)---4(5)
	 * |\   \  |
	 * | \   \ |
	 * |  \   \|
	 * 2---3   3 (4)
	 * </pre>
	 * 
	 * <p>
	 * I triangoli hanno i vertici: 123 e 341
	 * </p>
	 * <p>
	 * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
	 * </p>
	 * 
	 * <p>
	 * Il controllo validità del tile si presume essere stato fatto prima di invocare questo metodo.
	 * </p>
	 * 
	 * 
	 */
	public void setVertexCoordsOnCursor(float leftX, float topY, Tile tile) {
		float TILE_WIDTH = tile.width;
		float TILE_HEIGHT = tile.height;

		// gestione delle rotazioni startX, startY e diagonali
		// if (!tile.diagonalFlip) {
		v1x = leftX;
		v1y = topY;

		v2x = leftX;
		v2y = topY - TILE_HEIGHT;

		v3x = leftX + TILE_WIDTH;
		v3y = topY - TILE_HEIGHT;

		v4x = leftX + TILE_WIDTH;
		v4y = topY;

		// triangle 1
		// cursor rappresenta il numero di vertici. Per ogni vertice ci sono
		// 3 coordinate per i vertici
		basePtr = cursor * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = v1x;
		vertices.coords[basePtr + OFFSET_Y] = v1y;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = v2x;
		vertices.coords[basePtr + OFFSET_Y] = v2y;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = v3x;
		vertices.coords[basePtr + OFFSET_Y] = v3y;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = v4x;
		vertices.coords[basePtr + OFFSET_Y] = v4y;
		vertices.coords[basePtr + OFFSET_Z] = 0;

	}

	/**
	 * <p>
	 * Imposta i vertici per un tile (row, col). Questa funzione mette a 0 la z.
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | 1    4 |
	 * |        |
	 * | 2    3 |
	 * +--------+
	 * </pre>
	 * 
	 * <pre>
	 * 1   1(6)---4(5)
	 * |\   \  |
	 * | \   \ |
	 * |  \   \|
	 * 2---3   3 (4)
	 * </pre>
	 * 
	 * <p>
	 * I triangoli hanno i vertici: 123 e 341
	 * </p>
	 * <p>
	 * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
	 * </p>
	 * 
	 * @param selectedRow
	 * @param selectedColumn
	 * @param leftX
	 * @param topY
	 * @param tile
	 * 
	 */
	public void setVertexCoords(int selectedRow, int selectedColumn, float leftX, float topY, Tile tile) {
		if (!Tile.isEmpty(tile)) {
			float TILE_WIDTH = tile.width;
			float TILE_HEIGHT = tile.height;

			v1x = leftX;
			v1y = topY;

			v2x = leftX;
			v2y = topY - TILE_HEIGHT;

			v3x = leftX + TILE_WIDTH;
			v3y = topY - TILE_HEIGHT;

			v4x = leftX + TILE_WIDTH;
			v4y = topY;

			// diagonali gestite da texture
			// gestione delle rotazioni startX, startY e diagonali

			// triangle 1
			basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = v1x;
			vertices.coords[basePtr + OFFSET_Y] = v1y;
			vertices.coords[basePtr + OFFSET_Z] = 0;

			basePtr += VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = v2x;
			vertices.coords[basePtr + OFFSET_Y] = v2y;
			vertices.coords[basePtr + OFFSET_Z] = 0;

			basePtr += VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = v3x;
			vertices.coords[basePtr + OFFSET_Y] = v3y;
			vertices.coords[basePtr + OFFSET_Z] = 0;

			// triangle 2
			basePtr += VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = v4x;
			vertices.coords[basePtr + OFFSET_Y] = v4y;
			vertices.coords[basePtr + OFFSET_Z] = 0;
		} else {
			// triangle 1
			basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = 0;
			vertices.coords[basePtr + OFFSET_Y] = 0;
			vertices.coords[basePtr + OFFSET_Z] = 0;

			basePtr += VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = 0;
			vertices.coords[basePtr + OFFSET_Y] = 0;
			vertices.coords[basePtr + OFFSET_Z] = 0;

			basePtr += VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = 0;
			vertices.coords[basePtr + OFFSET_Y] = 0;
			vertices.coords[basePtr + OFFSET_Z] = 0;

			// triangle 2
			basePtr += VERTICES_DIMENSIONS;
			vertices.coords[basePtr + OFFSET_X] = 0;
			vertices.coords[basePtr + OFFSET_Y] = 0;
			vertices.coords[basePtr + OFFSET_Z] = 0;
		}
	}

	/**
	 * <p>
	 * Imposta i vertici per una grid cell (row, col). Questa funzione mette a 0 la z.
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | 1    4 |
	 * |        |
	 * | 2    3 |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * I triangoli hanno i vertici: 123 e 341
	 * </p>
	 * 
	 * @param selectedRow
	 * @param selectedColumn
	 * @param leftX
	 * @param topY
	 * @param cell
	 */
	public void setVertexCoords(int selectedRow, int selectedColumn, float leftX, float topY, GridCell cell) {

		// triangle 1
		basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX;
		vertices.coords[basePtr + OFFSET_Y] = topY;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX;
		vertices.coords[basePtr + OFFSET_Y] = topY - cell.height;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX + cell.width;
		vertices.coords[basePtr + OFFSET_Y] = topY - cell.height;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		// triangle 2
		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX + cell.width;
		vertices.coords[basePtr + OFFSET_Y] = topY;
		vertices.coords[basePtr + OFFSET_Z] = 0;
	}

	/**
	 * <p>
	 * Imposta i vertici per una grid cell (row, col). Questa funzione mette a 0 la z.
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | 1    4 |
	 * |        |
	 * | 2    3 |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * I triangoli hanno i vertici: 123 e 341. Detto quindi in altro modo, leftX e topY rappresentano le coordinate rispetto al sistema di riferimento dello shape del vertex 1.
	 * </p>
	 * 
	 * @param selectedRow
	 * @param selectedColumn
	 * @param leftX
	 * @param topY
	 * @param widthValue
	 * @param heightValue
	 */
	public void setVertexCoords(int selectedRow, int selectedColumn, float leftX, float topY, int widthValue, int heightValue) {

		// triangle 1
		basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX;
		vertices.coords[basePtr + OFFSET_Y] = topY;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX;
		vertices.coords[basePtr + OFFSET_Y] = topY - heightValue;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX + widthValue;
		vertices.coords[basePtr + OFFSET_Y] = topY - heightValue;
		vertices.coords[basePtr + OFFSET_Z] = 0;

		// triangle 2
		basePtr += VERTICES_DIMENSIONS;
		vertices.coords[basePtr + OFFSET_X] = leftX + widthValue;
		vertices.coords[basePtr + OFFSET_Y] = topY;
		vertices.coords[basePtr + OFFSET_Z] = 0;
	}

	/**
	 * <p>
	 * Imposta le coordinate della texture numero textureIndex.
	 * </p>
	 * 
	 */
	public void setTextureCoords(int row, int col, float textureLowX, float textureHighX, float textureLowY, float textureHighY) {
		setTextureCoords(TEXTURE_DEFAULT_INDEX, row, col, textureLowX, textureHighX, textureLowY, textureHighY);
	}

	/**
	 * <p>
	 * Imposta le coordinate della texture numero textureIndex.
	 * </p>
	 * 
	 */
	public void setTextureCoords(int textureIndex, int row, int col, float textureLowX, float textureHighX, float textureLowY, float textureHighY) {
		basePtr = (row * gridCols + col) * VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS;

		TextureBuffer current = textures[textureIndex];

		// triangle 1
		current.coords[basePtr + OFFSET_X] = textureLowX;
		current.coords[basePtr + OFFSET_Y] = textureLowY;

		basePtr += TEXTURE_DIMENSIONS;
		current.coords[basePtr + OFFSET_X] = textureLowX;
		current.coords[basePtr + OFFSET_Y] = textureHighY;

		basePtr += TEXTURE_DIMENSIONS;
		current.coords[basePtr + OFFSET_X] = textureHighX;
		current.coords[basePtr + OFFSET_Y] = textureHighY;

		basePtr += TEXTURE_DIMENSIONS;
		current.coords[basePtr + OFFSET_X] = textureHighX;
		current.coords[basePtr + OFFSET_Y] = textureLowY;

	}

	/**
	 * Imposta le coordinate in base ad un'animazione
	 * 
	 */
	public void setTextureCoords(int textureIndex, int row, int col, TextureTimeline animator) {
		setTextureCoords(textureIndex, row, col, animator.getHandler().value().textureRegion);
	}
	
	public void setTextureCoords(int textureIndex, int row, int col, TextureRegion region) {
		basePtr = (row * gridCols + col) * VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS;

		float[] currentTexturesCoords = textures[textureIndex].coords;

		// triangle 1
		currentTexturesCoords[basePtr + OFFSET_X] = region.lowX;
		currentTexturesCoords[basePtr + OFFSET_Y] = region.lowY;

		basePtr += TEXTURE_DIMENSIONS;
		currentTexturesCoords[basePtr + OFFSET_X] = region.lowX;
		currentTexturesCoords[basePtr + OFFSET_Y] = region.highY;

		basePtr += TEXTURE_DIMENSIONS;
		currentTexturesCoords[basePtr + OFFSET_X] = region.highX;
		currentTexturesCoords[basePtr + OFFSET_Y] = region.highY;

		basePtr += TEXTURE_DIMENSIONS;
		currentTexturesCoords[basePtr + OFFSET_X] = region.highX;
		currentTexturesCoords[basePtr + OFFSET_Y] = region.lowY;
	}

	public void setTextureCoords(int row, int col, Tile tile) {
		setTextureCoords(TEXTURE_DEFAULT_INDEX, row, col, tile);
	}

	public void setTextureCoords(int row, int col, GridCell cell) {
		setTextureCoords(TEXTURE_DEFAULT_INDEX, row, col, cell);
	}

	public void setTextureCoords(int textureIndex, int row, int col, GridCell cell) {
		basePtr = (row * gridCols + col) * VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS;

		float[] currentTexturesCoords = textures[textureIndex].coords;

		// triangle 1
		currentTexturesCoords[basePtr + OFFSET_X] = cell.textureLowX;
		currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureLowY;

		basePtr += TEXTURE_DIMENSIONS;
		currentTexturesCoords[basePtr + OFFSET_X] = cell.textureLowX;
		currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureHighY;

		basePtr += TEXTURE_DIMENSIONS;
		currentTexturesCoords[basePtr + OFFSET_X] = cell.textureHighX;
		currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureHighY;

		basePtr += TEXTURE_DIMENSIONS;
		currentTexturesCoords[basePtr + OFFSET_X] = cell.textureHighX;
		currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureLowY;
	}

	/**
	 * <p>
	 * Imposta le coordinate texture della tile attualmente sotto il cursore.
	 * </p>
	 * 
	 * <pre>
	 * 
	 *      2--- 3       4
	 *      |   /       /|
	 *      |  /       / |
	 *      | /       /  |
	 *      |/       /   |
	 *      1       6----5
	 * 
	 * </pre>
	 * 
	 */
	public void setTextureCoordsOnCursor(Tile tile) {
		setTextureCoordsOnCursor(TEXTURE_DEFAULT_INDEX, tile);
	}

	/**
	 * <p>
	 * Imposta le coordinate texture della tile attualmente sotto il cursore.
	 * </p>
	 * 
	 * <pre>
	 * 
	 *      2--- 3       4
	 *      |   /       /|
	 *      |  /       / |
	 *      | /       /  |
	 *      |/       /   |
	 *      1       6----5
	 * 
	 * </pre>
	 * 
	 * <p>
	 * Il controllo validità del tile si presume essere stato fatto prima di invocare questo metodo.
	 * </p>
	 * 
	 */
	public void setTextureCoordsOnCursor(int textureIndex, Tile tile) {
		// cursor rappresenta il numero di vertici. Per ogni vertice ci sono 2
		// coordinate per le texture
		basePtr = cursor * VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS;

		TextureBuffer current = textures[textureIndex];

		if (!tile.diagonalFlip) {
			current.coords[basePtr + OFFSET_X] = tile.lowX;
			current.coords[basePtr + OFFSET_Y] = tile.lowY;
		} else {
			current.coords[basePtr + OFFSET_X] = tile.highX;
			current.coords[basePtr + OFFSET_Y] = tile.highY;
		}

		basePtr += TEXTURE_DIMENSIONS;
		current.coords[basePtr + OFFSET_X] = tile.lowX;
		current.coords[basePtr + OFFSET_Y] = tile.highY;

		if (!tile.diagonalFlip) {
			basePtr += TEXTURE_DIMENSIONS;
			current.coords[basePtr + OFFSET_X] = tile.highX;
			current.coords[basePtr + OFFSET_Y] = tile.highY;
		} else {
			basePtr += TEXTURE_DIMENSIONS;
			current.coords[basePtr + OFFSET_X] = tile.lowX;
			current.coords[basePtr + OFFSET_Y] = tile.lowY;
		}

		basePtr += TEXTURE_DIMENSIONS;
		current.coords[basePtr + OFFSET_X] = tile.highX;
		current.coords[basePtr + OFFSET_Y] = tile.lowY;

	}

	/**
	 * <pre>
	 * 
	 *      2--- 3       4
	 *      |   /       /|
	 *      |  /       / |
	 *      | /       /  |
	 *      |/       /   |
	 *      1       6----5
	 * 
	 * </pre>
	 * 
	 * @param textureIndex
	 * @param row
	 * @param col
	 * @param tile
	 */
	public void setTextureCoords(int textureIndex, int row, int col, Tile tile) {
		basePtr = (row * gridCols + col) * VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS;

		float[] coords = textures[textureIndex].coords;

		if (!Tile.isEmpty(tile)) {

			// triangle 1
			if (!tile.diagonalFlip) {
				coords[basePtr + OFFSET_X] = tile.lowX;
				coords[basePtr + OFFSET_Y] = tile.lowY;
			} else {
				coords[basePtr + OFFSET_X] = tile.highX;
				coords[basePtr + OFFSET_Y] = tile.highY;
			}

			basePtr += TEXTURE_DIMENSIONS;
			coords[basePtr + OFFSET_X] = tile.lowX;
			coords[basePtr + OFFSET_Y] = tile.highY;

			if (!tile.diagonalFlip) {
				basePtr += TEXTURE_DIMENSIONS;
				coords[basePtr + OFFSET_X] = tile.highX;
				coords[basePtr + OFFSET_Y] = tile.highY;
			} else {
				basePtr += TEXTURE_DIMENSIONS;
				coords[basePtr + OFFSET_X] = tile.lowX;
				coords[basePtr + OFFSET_Y] = tile.lowY;
			}

			basePtr += TEXTURE_DIMENSIONS;
			coords[basePtr + OFFSET_X] = tile.highX;
			coords[basePtr + OFFSET_Y] = tile.lowY;

		} else {
			// triangle 1
			coords[basePtr + OFFSET_X] = 0;
			coords[basePtr + OFFSET_Y] = 0;

			basePtr += TEXTURE_DIMENSIONS;
			coords[basePtr + OFFSET_X] = 0;
			coords[basePtr + OFFSET_Y] = 0;

			basePtr += TEXTURE_DIMENSIONS;
			coords[basePtr + OFFSET_X] = 0;
			coords[basePtr + OFFSET_Y] = 0;

			// triangle 2
			basePtr += TEXTURE_DIMENSIONS;
			coords[basePtr + OFFSET_X] = 0;
			coords[basePtr + OFFSET_Y] = 0;
		}
	}

	/**
	 * <p>
	 * Effettua l'aggiornamento di tutti i buffer che non sono di tipo STATIC, limitandosi però ai vertici definiti mediante il cursore
	 * </p>
	 */
	public void updateBuffersOnCursor() {
		int size = cursor;
		// gli indici non devono essere aggiornati.

		// vertici
		// vertices.put(verticesCoords, 0, size * VERTICES_DIMENSIONS).position(0);
		if (vertices.allocation != BufferAllocationType.STATIC) {
			vertices.update(size * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS);
		}

		for (int i = 0; i < textures.length; i++) {
			if (textures[i].allocation != BufferAllocationType.STATIC) {
				textures[i].update(size * VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS);
				// textures[i].put(texturesCoords[i], 0, size * TEXTURE_DIMENSIONS).position(0);
			}
		}

		if (indexes.allocation != BufferAllocationType.STATIC) {
			indexes.update(size * IndexBuffer.INDEX_IN_QUAD_TILE);
		}

	}

}
