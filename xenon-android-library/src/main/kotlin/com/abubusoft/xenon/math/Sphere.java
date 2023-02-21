package com.abubusoft.xenon.math;

public class Sphere {
	/**
	 * Imposta le coordinate del centro della sfera.
	 * 
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param radius
	 */
	public Sphere(float centerX, float centerY, float centerZ, float radius) {
		this();

		this.center.x = centerX;
		this.center.y = centerY;
		this.center.z = centerZ;

		this.radius = radius;
	}

	public Sphere() {
		center = new Point3();
	}

	public final Point3 center;

	public float radius;

	/**
	 * Indica se i due cerchi si intersecano.
	 * 
	 * Se la distanza tra i due centri è minore o uguale alla somma dei due
	 * raggi, allora si intersecano necessariamente.
	 * 
	 * @param sphere2
	 * @return true se collidono, false altrimenti
	 */
	public boolean intersect(Sphere sphere2) {
		float distance = center.distance(sphere2.center);

		return (radius + sphere2.radius) >= distance;
	}

	/**
	 * @param point
	 * @return
	 * 		true se i due oggetti collidono
	 */
	public boolean intersect(Point3 point) {
		float distance2 = center.distance2(point);

		return distance2 < (radius * radius);
	}

	/**
	 * Imposta il centro ed il raggio
	 * 
	 * @param centerValue
	 * @param radiusValue
	 */
	public void set(Point3 centerValue, float radiusValue) {
		center.x = centerValue.x;
		center.y = centerValue.y;
		center.z = centerValue.z;
		radius = radiusValue;
	}

	public Sphere copy() {
		return new Sphere(center.x, center.y, center.z, radius);
	}

	public void copyInto(Sphere destination) {
		destination.center.x=center.x;
		destination.center.y=center.y;
		destination.center.z=center.z;
		
		destination.radius=radius;

	}

}
