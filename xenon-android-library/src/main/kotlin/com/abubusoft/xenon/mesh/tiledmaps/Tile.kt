package com.abubusoft.xenon.mesh.tiledmaps;

import com.abubusoft.xenon.texture.TextureRegion;

import com.abubusoft.kripton.annotation.BindType;

/**
 * <p>
 * Rappresenta la singola tile
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@BindType
public class Tile extends TextureRegion {

	/**
	 * values del tile
	 */
	public int gid;

	/**
	 * colonna nel tileset
	 */
	public int atlasColumn;

	/**
	 * riga nel tileset
	 */
	public int atlasRow;

	/**
	 * colonna nel layer
	 */
	public int layerColumn;

	/**
	 * riga nel layer
	 */
	public int layerRow;

	/**
	 * larghezza
	 */
	public int width;

	/**
	 * altezza del tile
	 */
	public int height;

	/**
	 * offsetX del tile
	 */
	public int drawOffsetX;

	/**
	 * screenOffsetY del tile
	 */
	public int drawOffsetY;

	/**
	 * index di {@link TiledMap#textureList} della texture associata.
	 */
	public byte textureSelector;

	/**
	 * Indica che la tile prima di essere usata deve essere ruotata in orizzontale
	 */
	public boolean horizontalFlip;

	/**
	 * Indica che la tile prima di essere usata deve essere ruotata in verticale
	 */
	public boolean verticalFlip;

	/**
	 * Indica che la tile prima di essere usata deve essere subire l'inversione tra asse startX e startY della texture
	 */
	public boolean diagonalFlip;

	public Tile() {
		this(0, 0, 0, -1, -1, 0, 0,0,0);
	}

	/**
	 * <p>
	 * Costruttore
	 * </p>
	 * 
	 * @param gid
	 * @param column
	 * @param row
	 * @param atlasColumn
	 * @param atlasRow
	 * @param width
	 * @param height
	 */
	public Tile(int gid, int column, int row, int atlasColumn, int atlasRow, int width, int height) {
		this(gid, column, row, atlasColumn, atlasRow, width, height, 0, 0);
	}

	/**
	 * <p>
	 * Costruttore
	 * </p>
	 * 
	 * @param gid
	 * @param column
	 * @param row
	 * @param atlasColumn
	 * @param atlasRow
	 * @param width
	 * @param height
	 */
	public Tile(int gid, int column, int row, int atlasColumn, int atlasRow, int width, int height, int drawOffsetX, int drawOffsetY) {
		this.gid = gid;
		this.layerColumn = column;
		this.layerRow = row;
		this.atlasColumn = atlasColumn;
		this.atlasRow = atlasRow;
		this.width = width;
		this.height = height;

		this.drawOffsetX = drawOffsetX;
		this.drawOffsetY = drawOffsetY;
	}

	public static Tile getEmptyTile(int column, int row) {
		return new Tile(0, column, row, -1, -1, 0, 0, 0, 0);
	}

	/**
	 * Imposta le coordinate della texture.
	 * 
	 * @param xl
	 * @param xh
	 * @param yl
	 * @param yh
	 */
	public void setTextureCoordinate(float xl, float xh, float yl, float yh) {
		lowX = xl;
		highX = xh;

		lowY = yl;
		highY = yh;
	}

	/**
	 * Imposta le dimensioni del tile
	 * 
	 * @param tileWidth
	 * @param tileHeight
	 */
	public void setDimensions(int tileWidth, int tileHeight) {
		height = tileHeight;
		width = tileWidth;
	}

	/**
	 * Imposta la posizione del tile nel layer
	 * 
	 * @param row
	 * @param col
	 */
	public void setLayerPosition(int row, int col) {
		layerRow = row;
		layerColumn = col;
	}

	/**
	 * Indica se la tile è vuota o meno. Una tile è vuota se è nulla o se ha id == 0.
	 * 
	 * @param tile
	 * @return
	 */
	public static boolean isEmpty(Tile tile) {
		if (tile == null)
			return true;
		return tile.gid == 0;
	}

	public void setAtlas(int column, int row) {
		this.atlasColumn = column;
		this.atlasRow = row;
	}

	public void setEmpty() {
		gid = 0;
	}
}