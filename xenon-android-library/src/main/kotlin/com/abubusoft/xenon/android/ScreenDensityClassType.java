package com.abubusoft.xenon.android;

/**
 * Tipologie di densità.
 * 
 * @author Francesco Benincasa
 *
 */
public enum ScreenDensityClassType {
	UNKNOWN(0,0),
	/**
	 * low density 
	 */
	LDPI(0.75f,120),
	/**
	 * medium density 
	 */
	MDPI(1f,160),
	/**
	 * high density 
	 */
	HDPI(1.5f,240),
	/**
	 * extra high density
	 */
	XHDPI(2,320),
	/**
	 * extra extra high density 
	 */
	XXHDPI(3,480),
	/**
	 * extra extra extra high density
	 */
	XXXHDPI(4,640);
	
	
	/**
	 * fattore di scala per la classe. Non dovrebbe venire usato
	 */
	public final float scaleFactor;
	
	/**
	 * densità in dpi
	 */
	public final int density;
	
	private ScreenDensityClassType(float scaleValue, int densityValue)
	{
		scaleFactor=scaleValue;
		density=densityValue;
	}
	
	/**
	 * <p>A partire da un valore di scala, prova a recuperare la relativa classe dello screen density.</p> 
	 * 
	 * @param scaleValue
	 * 		valore di scala
	 * @return
	 * 		classe di appartenenza dello screen density
	 */
	public static ScreenDensityClassType valueFromScale(float scaleValue)
	{
		ScreenDensityClassType screenDensity=UNKNOWN;
		
		for (ScreenDensityClassType item: values())
		{
			if (scaleValue>=item.scaleFactor)
			{
				screenDensity=item;
			}
		}
		
		return screenDensity;
	}
	
}
