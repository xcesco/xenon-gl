package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.math.Point2;

/**
 * Rappresenta per un layer, il suo punto di riferimento per quel che riguarda il primo tile da disegnare
 * 
 * @author xcesco
 *
 */
public class LayerOffsetHolder {
	public int tileIndexX;
	public int tileIndexY;

	public int screenOffsetX;
	public int screenOffsetY;

	public void setOffset(Point2 offsetPoint) {
		screenOffsetX = (int) offsetPoint.x;
		screenOffsetY = (int) offsetPoint.y;
	}

	public void setOffset(int x, int y) {
		screenOffsetX = x;
		screenOffsetY = y;
	}

}