/**
 * 
 */
package com.abubusoft.xenon.math;

import static com.abubusoft.xenon.math.XenonMath.power2;
import static com.abubusoft.xenon.math.XenonMath.sqrt;
import static com.abubusoft.xenon.math.XenonMath.abs;

import com.abubusoft.kripton.annotation.BindType;

/**
 * Punto in un sistema di coordinate unicamente intere.
 * @author Francesco Benincasa
 *
 */
@BindType
public class PointI2 {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		PointI2 other = (PointI2) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public PointI2()
	{
		
	}
	
	public PointI2(int x, int y)
	{
		this.x=x;
		this.y=y;
		
	}
	
	public int x;
	public int y;	
	
	public float distance(PointI2 point2)
	{
		return sqrt(power2(point2.x-x))+(power2(point2.y-y));
	}
	
	public float distance2(PointI2 point2)
	{
		return abs(power2(point2.x-x))+(power2(point2.y-y));
	}
	

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
	public PointI2 copy()
	{
		return new PointI2(x,y);
	}

	/**
	 * Alternativa al new
	 * 
	 * @param x
	 * 		x
	 * @param y
	 * 		y
	 * @return
	 * 		nuovo punto con le coordinate impostate
	 */
	public static PointI2 set(int x, int y) {
		return new PointI2(x, y);
	}
	
	/**
	 * Imposta le coordinate del punto
	 * 
	 * @param xValue
	 * @param yValue
	 */
	public void setCoords(int xValue, int yValue)
	{
		this.x=xValue;
		this.y=yValue;
	}
	
	/**
	 * Aggiunge alle coordinate del punto
	 * 
	 * @param xValue
	 * @param yValue
	 */
	public void addCoords(int xValue, int yValue)
	{
		this.x+=xValue;
		this.y+=yValue;
	}

	/**
	 * Copia il punto in un altro punto
	 * 
	 * @param destination
	 */
	public void copyInto(PointI2 destination) {
		destination.x=x;
		destination.y=y;
		
	}
}
