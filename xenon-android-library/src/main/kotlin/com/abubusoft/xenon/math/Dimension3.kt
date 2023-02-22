package com.abubusoft.xenon.math;

import java.io.Serializable;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;

/**
 * Dimensioni 3d
 * 
 * @author Francesco Benincasa
 *
 */
@BindType
public class Dimension3 implements Serializable {
	
	private static final long serialVersionUID = 3425624648092503466L;

	/**
	 * larghezza
	 */
	@Bind
	public float width;
	
	/**
	 * altezza
	 */
	@Bind
	public float height;
	
	/**
	 * profondità
	 */
	@Bind
	public float depth;
	
	/**
	 * Imposta in un'unica volta i tre valori
	 * 
	 * @param width
	 * @param height
	 * @param depth
	 */
	public void set(float width, float height, float depth)
	{
		this.width=width;
		this.height=height;
		this.depth=depth;
	}
}
