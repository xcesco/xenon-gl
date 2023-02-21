package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * Layer che fa parte di una animazione.
 * 
 * @author Francesco Benincasa
 * 
 */
public class TileAnimationFrame {

	public TileAnimationFrame(Layer layerValue, long durationValue) {
		layer = layerValue;
		duration = durationValue;
	}

	/**
	 * durata del frame in millisecondi
	 */
	public long duration;

	/**
	 * frame associato
	 */
	public final Layer layer;

}
