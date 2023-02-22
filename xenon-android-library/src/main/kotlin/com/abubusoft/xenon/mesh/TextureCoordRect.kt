/**
 * 
 */
package com.abubusoft.xenon.mesh;

/**
 * Permette di definire un rettangolo nello spazio delle texture, che va da a [0 .. 1]
 * 
 * @author Francesco Benincasa
 * 
 */
public class TextureCoordRect {
	
	public float x;
	public float y;
	
	public float width;
	public float height;

	private TextureCoordRect(float x, float y, float width, float height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}

	/**
	 * A partire dal centro (0.5, 0.5) definisce un rettangolo di dimensioni specificate.
	 * 
	 * @param width da 0 a 1
	 * @param height da 0 a 1
	 * @return
	 */
	public static TextureCoordRect buildFromCenter(float width, float height)
	{
		TextureCoordRect rect=new TextureCoordRect(0.5f - width * 0.5f, 0.5f - height * 0.5f, width, height);
		
		return rect;
	}
	
	/**
	 * A partire dal centro (0.5, 0.5) definisce un rettangolo di dimensioni specificate.
	 * 
	 * @param width da 0 a 1
	 * @param height da 0 a 1
	 * @return
	 */
	public static TextureCoordRect buildFromTopLeft(float width, float height)
	{
		TextureCoordRect rect=new TextureCoordRect(0, 0, width, height);
		
		return rect;
	}
	
	/**
	 * Posto il top left (origine) del sistema a (0, 0), definisce il range in termini di larghezza ed altezza
	 * 
	 * @param width da 0 a 1
	 * @param height da 0 a 1
	 * @return
	 */
	public static TextureCoordRect buildFromOrigin(float width, float height)
	{
		TextureCoordRect rect=new TextureCoordRect(0, 0, width, height);
		
		return rect;
	}
	
	/**
	 * A partire dal top left definisce un rettangolo di dimensioni specificate.
	 * 
	 * @param startx
	 * @param starty
	 * @param width
	 * @param height
	 * @return
	 */
	public static TextureCoordRect buildFromTopLeft(float startx, float starty, float width, float height)
	{
		TextureCoordRect rect=new TextureCoordRect(startx, starty, width, height);
		
		return rect;
	}
	
	/**
	 * Range [0 - 1] [0 - 1]
	 * 
	 * @param width da 0 a 1
	 * @param height da 0 a 1
	 * @return
	 */
	public static TextureCoordRect build()
	{
		TextureCoordRect rect=new TextureCoordRect(0, 0, 1f, 1f);
		
		return rect;
	}
}
