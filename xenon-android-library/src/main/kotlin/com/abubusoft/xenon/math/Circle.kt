/**
 * 
 */
package com.abubusoft.xenon.math;

/**
 * @author Francesco Benincasa
 *
 */
public class Circle {
	
	public Circle()
	{
		center=new Point2();
	}

	public Circle(float x, float y, float radius) {
		center=new Point2();
		center.x=x;
		center.y=y;
		
		this.radius=radius;
	}

	public final Point2 center;
	
	/**
	 * raggio del cerchio
	 */
	public float radius;
	
	/**
	 * Indica se i due cerchi si intersecano.
	 * 
	 * Se la distanza tra i due centri è minore o uguale alla somma dei due raggi, allora si
	 * intersecano necessariamente.
	 * 
	 * @param circle2
	 * @return
	 * 		true se collidono, false altrimenti
	 */
	public boolean intersect(Circle circle2)
	{
		float distance=center.distance(circle2.center);

		return (radius+circle2.radius)>=distance;
	}
	
	/**
	 * intersezione con il point
	 * 
	 * @param point
	 * @return
	 */
	public boolean intersect(Point2 point)
	{
		float distance2=center.distance2(point);
		
		return distance2<(radius*radius);
	}
	
	/**
	 * Impostiamo il valore del cerchio
	 * 
	 * @param centerValue
	 * @param radiusValue
	 */
	public void set(Point2 centerValue, float radiusValue)
	{
		center.x=centerValue.x;
		center.y=centerValue.y;
		
		radius=radiusValue;
	}
	
	/**
	 * Imposta il centro ignorando la z.
	 * @param centerValue
	 * @param radiusValue
	 */
	public void set(Point3 centerValue, float radiusValue)
	{
		center.x=centerValue.x;
		center.y=centerValue.y;
		
		radius=radiusValue;
	}

	public Circle copy() {
		return new Circle(center.x, center.y, radius);
	}

	public void copyInto(Circle destination) {
		center.copyInto(destination.center);
		destination.radius=radius;		
	}
	
}
