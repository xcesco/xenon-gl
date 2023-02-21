package com.abubusoft.xenon.math;

/**
 * Rappresenta un rettangolo allineato con gli assi.
 * 
 * @author Francesco Benincasa
 *
 */
public class AlignedRectangle2D  {

	/**
	 * centro del rettangolo
	 */
	public final Point2 center;
	
	/**
	 * altezza
	 */
	public float height;
	
	/**
	 * larghezza
	 */
	public float width;
	
	
	public AlignedRectangle2D(float centerX, float centerY, float widthValue, float heightValue) {
		center=new Point2(centerX, centerY);
		width=widthValue;
		height=heightValue;
	}

	/**
	 * Verifica se un cerchio interseca con il rettangolo
	 * 
	 * @param circle
	 * @return
	 */
	public boolean intersect(Circle circle)
	{
		if ((Math.abs(circle.center.x - center.x) <= (width/2 + circle.radius))  && 
		(Math.abs(circle.center.y - center.y) <= (height/2 + circle.radius))) return true;
		
		return false;
	}

	/**
	 * @return
	 */
	public AlignedRectangle2D copy() {
		return new AlignedRectangle2D(center.x, center.y, this.width, this.height);
	}

	/**
	 * @param destination
	 */
	public void copyInto(AlignedRectangle2D destination) {
		center.copyInto(destination.center);
		
		destination.width=width;
		destination.height=height;		
	}
}
