package com.abubusoft.xenon.texture;

/**
 * Indica in termini di pixel ed in termini di coordinate UW normalizzate (il cui range è 0f..1f), le dimensioni
 * massime valide della texture.
 * 
 * Size rappresenta le dimensioni reali della texture.
 * 
 * @author Francesco Benincasa
 *
 */
public class TextureDimension {
	
	public TextureDimension(int widthValue, int heightValue, float normalizedMaxWidthValue, float normalizedMaxHeightValue, TextureSizeType sizeValue)
	{
		this.width=widthValue;
		this.height=heightValue;
		
		this.normalizedMaxWidth=normalizedMaxWidthValue;
		this.normalizedMaxHeight=normalizedMaxHeightValue;
		
		this.size=sizeValue;
	}
	
	/**
	 * dimensioni in pixel della texture
	 */
	public final int width;
	/**
	 * dimensioni in pixel della texture
	 */
	public final int height;
	
	/**
	 * dimensione massima normalizzata tra 0 e 1
	 */
	public final float normalizedMaxWidth;
	
	/**
	 * dimensione massima normalizzata tra 0 e 1
	 */
	public final float normalizedMaxHeight;
	
	/**
	 * dimensione reale della texture
	 */
	public final TextureSizeType size;
}
