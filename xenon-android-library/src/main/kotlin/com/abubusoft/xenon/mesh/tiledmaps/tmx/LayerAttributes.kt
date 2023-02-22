package com.abubusoft.xenon.mesh.tiledmaps.tmx;

public abstract class LayerAttributes {

	/**
	 * <p>Rendering offset for this layer in pixels. Defaults to 0. (since 0.14).</p>
	 * 
	 * <p>E' un valore da sottrarre alle coordinate delle tile</p>
	 */
	public static final String OFFSET_X = "x";
	
	/**
	 * <p>Rendering offset for this layer in pixels. Defaults to 0. (since 0.14)</p>
	 * 
	 * <p>E' un valore da sottrarre alle coordinate delle tile</p>
	 */
	public static final String OFFSET_Y = "y";

	/**
	 * The width of the layer in tiles. Traditionally required, but as of Tiled Qt always the same as the map width.
	 */
	public static final String WIDTH = "width";

	/**
	 * The height of the layer in tiles. Traditionally required, but as of Tiled Qt always the same as the map height.
	 */
	public static final String HEIGHT = "height";

	/**
	 * The opacity of the layer as a value from 0 to 1. Defaults to 1.
	 */
	public static final String OPACITY = "opacity";

	/**
	 * The name of the layer.
	 */
	public static final String NAME = "name";

}
