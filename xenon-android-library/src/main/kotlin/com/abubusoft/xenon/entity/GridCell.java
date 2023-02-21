package com.abubusoft.xenon.entity;

import com.abubusoft.xenon.entity.BaseEntity;

public class GridCell extends BaseEntity {

	private static final long serialVersionUID = -5483129550120552425L;

	public float textureLowX;
	
	public float textureLowY;
	
	public float textureHighY;
	
	public float textureHighX;
	
	public int width;
	
	public int height;
	
	/**
	 * Imposta le coordinate della texture.
	 * 
	 * @param xl
	 * @param xh
	 * @param yl
	 * @param yh
	 */
	public void setTextureCoordinate(float xl, float xh, float yl, float yh)
	{
		textureLowX=xl;
		textureHighX=xh;
		
		textureLowY=yl;
		textureHighY=yh;
	}
	
	/**
	 * Imposta le dimensioni del tile
	 * @param tileWidth
	 * @param tileHeight
	 */
	public void setDimensions(int tileWidth, int tileHeight)
	{
		height=tileHeight;
		width=tileWidth;
	}
	
}
