/**
 * 
 */
package com.abubusoft.xenon.math;

import static com.abubusoft.xenon.math.XenonMath.power2;
import static com.abubusoft.xenon.math.XenonMath.sqrt;
import static com.abubusoft.xenon.math.XenonMath.abs;

import com.abubusoft.kripton.annotation.BindType;

/**
 * @author Francesco Benincasa
 *
 */
@BindType
public class Point2 {

	public Point2() {

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point2 other = (Point2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	public Point2(float x, float y) {
		this.x = x;
		this.y = y;

	}

	/**
	 * <p>
	 * coordinata sull'asse delle ascisse.
	 * </p>
	 */
	public float x;

	/**
	 * <p>
	 * coordinata sull'asse delle ordinate.
	 * </p>
	 */
	public float y;

	public float distance(Point2 point2) {
		return sqrt(power2(point2.x - x)) + (power2(point2.y - y));
	}

	public float distance2(Point2 point2) {
		return abs(power2(point2.x - x)) + (power2(point2.y - y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
	public Point2 copy() {
		return new Point2(x, y);
	}

	/**
	 * Alternativa al new
	 * 
	 * @param x
	 * @param y
	 * @return nuovo punto
	 */
	public static Point2 set(float x, float y) {
		return new Point2(x, y);
	}

	/**
	 * Imposta le coordinate del punto
	 * 
	 * @param valueX
	 * @param valueY
	 */
	public void setCoords(float valueX, float valueY) {
		this.x = valueX;
		this.y = valueY;
	}

	/**
	 * Aggiunge alle coordinate del punto
	 * 
	 * @param valueX
	 * @param valueY
	 */
	public void addCoords(float valueX, float valueY) {
		this.x += valueX;
		this.y += valueY;
	}

	/**
	 * Copia il punto in un altro punto
	 * 
	 * @param destination
	 */
	public void copyInto(Point2 destination) {
		destination.x = x;
		destination.y = y;
	}

	/**
	 * Imposta le coordinate del punto con le coordinate dell'altro punto passato come argomento
	 * 
	 * @param source
	 */
	public void set(Point2 source) {
		this.x = source.x;
		this.y = source.y;
	}

	/**
	 * <p>
	 * Aggiunge le coordinate punto passato come parametro.
	 * </p>
	 * 
	 * @param value
	 */
	public void add(Point2 value) {
		this.x += value.x;
		this.y += value.y;
	}

	/**
	 * <p>
	 * Aggiunge ad entrambe le coordinate il valore passato come argomento.
	 * </p>
	 * 
	 * @param value
	 */
	public void add(float value) {
		this.x += value;
		this.y += value;
	}

	/**
	 * <p>
	 * Divide le coordinate per il valore passato come argomento
	 * </p>
	 * 
	 * @param value
	 */
	public void div(float value) {
		this.x /= value;
		this.y /= value;
	}

	/**
	 * <p>
	 * Divide le coordinate per il valore passato come argomento e memorizza il resto.
	 * </p>
	 * 
	 * @param value
	 */
	public void mod(float value) {
		this.x = this.x % value;
		this.y = this.y % value;
	}

	public void mul(float value) {
		this.x *= value;
		this.y *= value;
	}

	/**
	 * Converte le coordinate di questo punto in integer
	 * 
	 * @return
	 */
	/*
	 * public Point2 integer() { startX=Math.round(startX); startY=Math.round(startY);
	 * 
	 * return this; }
	 */
}
