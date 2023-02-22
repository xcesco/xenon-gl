package com.abubusoft.xenon.texture;

/**
 * Tipo di dimensioni delle texture supportate.
 * 
 * @author Francesco Benincasa
 *
 */
public enum TextureSizeType
{
	/**
	 * le dimensioni della texture non sono specificate, dipende 
	 * dall'immagine usata come texture
	 */
	SIZE_UNBOUND(0,0),
	SIZE_32x32(32,32),
	SIZE_128x128(128, 128),
	SIZE_128x256(128, 256),	
	SIZE_256x128(256,128),
	SIZE_256x256(256, 256),
	SIZE_256x512(256, 512),	
	SIZE_512x256(512, 256),
	SIZE_512x512(512, 512),
	SIZE_1024x2(1024, 2),
	SIZE_1024x512(1024, 512),
	SIZE_1024x1024(1024, 1024),
	SIZE_2048x2048(2048, 2048);
	
	/**
	 * larghezza della texture
	 */
	public final int width;
	
	/**
	 * altezza della texture
	 */
	public final int height;
	
	/**
	 * @param w
	 * @param h
	 */
	private TextureSizeType(int w, int h)
	{
		this.width=w;
		this.height=h;
	}
}