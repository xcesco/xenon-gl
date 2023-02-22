/**
 * 
 */
package com.abubusoft.xenon.math;

import static com.abubusoft.xenon.math.XenonMath.power2;
import static com.abubusoft.xenon.math.XenonMath.sqrt;
import static com.abubusoft.xenon.math.XenonMath.abs;

import java.io.Serializable;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

/**
 * Punto in uno spazio cartesiano tridimensionale, con la caratteristica di essere persistente mediante Kripton.
 * 
 * @author Francesco Benincasa
 *
 */
@BindType
public class Point3 implements Serializable {

	private static final long serialVersionUID = 4754358686291704165L;


	public Point3()
	{
		
	}
	
	public Point3(float x, float y, float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
		
	}
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float x;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float y;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float z;
		
	
	public void add(float x1, float y1, float z1)
	{
		x+=x1;
		y+=y1;
		z+=z1;
	}
	
	public void add(Point3 value)
	{
		x+=value.x;
		y+=value.y;
		z+=value.z;
	}
		
	/**
	 * Definisce le coordinate di un punto
	 * @param newX
	 * @param newY
	 * @param newZ
	 */
	public void setCoords(float newX, float newY, float newZ)
	{
		x=newX;
		y=newY;
		z=newZ;
	}
	

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copyInto(java.lang.Object)
	 */
	public void copyInto(Point3 dest)
	{
		dest.x=x;
		dest.y=y;
		dest.z=z;
	}
	
	public void normalize()
	{
		float denomin=1/ XenonMath.abs(XenonMath.sqrt(x*x+y*y+z*z));
		
		x*=denomin;
		y*=denomin;
		z*=denomin;
	}
	

	/**
	 * Calcola distanza tra due punti
	 * 
	 * @param point2
	 * @return
	 * 		distanza
	 */
	public float distance(Point3 point2)
	{
		return sqrt(power2(point2.x-x))+(power2(point2.y-y)+(power2(point2.z-z)));
	}
	
	/**
	 * Calcola la distanza al quadrato tra due punti (più veloce rispetto a {@link #distance(Point3)})
	 * 
	 * @param point2
	 * @return
	 * 		distanza
	 */
	public float distance2(Point3 point2)
	{
		return abs(power2(point2.x-x))+(power2(point2.y-y)+(power2(point2.z-z)));
	}
	

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
	public Point3 copy()
	{
		return new Point3(x,y,z);
	}

	/**
	 * Alternativa al new
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * 		nuovo punto 
	 */
	public static Point3 set(float x, float y, float z) {
		return new Point3(x, y, z);
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
		result = prime * result + Float.floatToIntBits(z);
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
		Point3 other = (Point3) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

}
