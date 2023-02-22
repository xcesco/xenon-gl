package com.abubusoft.xenon.mesh;

/**
 * Rappresenta una mattonella che tipicamente viene utilizzata all'interno delle tiledMap.
 * 
 * @author Francesco Benincasa
 * 
 */
public class MeshTile extends MeshSprite {
	
	private static final long serialVersionUID = 902361471602149778L;

	/**
	 * ( * , startY ) indice della colonna del tile
	 */
	public int tileRowIndex;

	/**
	 * ( startX , * ) indice della riga del tile
	 */
	public int tileColumnIndex;

	/**
	 * altezza della tile in pixel
	 */
	public float tileHeight;

	/**
	 * larghezza della tile in pixel
	 */
	public float tileWidth;

	/**
	 * offset X del tile.
	 */
	public int drawOffsetX;
	
	/**
	 * offset Y del tile.
	 */
	public int drawOffsetY;


}
