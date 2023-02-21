package com.abubusoft.xenon.entity;

public class GridOptions {

	/**
	 * <p>Definisce la percentuale della dimensione in orizzontale della tile da utilizzare come spaziatore 
	 * tra un centro della tile e quello della colonna successivo</p> 
	 */
	public float marginHorizontal;
	
	public float marginVertical;
	
	public boolean oddColumnsLower;

	/**
	 * fattore di scala della window. Nel caso di landscape sarà > 1
	 */
	public float windowScaleFactor;
	
	public static GridOptions build()
	{
		return (new GridOptions()).marginHorizontal(1f).marginVertical(1f).oddColumnsLower(false).windowScaleFactor(1f);
	}

	public GridOptions oddColumnsLower(boolean value) {
		oddColumnsLower=value;
		return this;
	}

	public GridOptions marginVertical(float value) {
		marginVertical=value;
		return this;
	}

	public GridOptions marginHorizontal(float value) {
		marginHorizontal=value;
		return this;
	}
	
	public GridOptions windowScaleFactor(float value) {
		windowScaleFactor=value;
		return this;
	}
	
}
