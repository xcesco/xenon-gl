/**
 * 
 */
package com.abubusoft.xenon.scenemanagement;

import com.abubusoft.xenon.entity.Entity;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.math.Sphere;

/**
 * Permette di selezionare un oggetto in base al fatto che il punto di selezione sia dentro la sfera di collisione
 * dell'entità
 * 
 * @author Francesco Benincasa
 *
 */
@SuppressWarnings("rawtypes")
public class BoundingSphereSelector<E extends Entity> {
	Sphere bounding;
	Point3 selectorPoint;
	
	public BoundingSphereSelector()
	{
		bounding = new Sphere();
	}
	
	public void setSelectorPoint(Point3 selector)
	{
		selectorPoint=selector;	
	}
	
	public boolean isSelected(E entity)
	{
		bounding.set(entity.position, entity.getBoundingRadius());
		
		if (bounding.intersect(selectorPoint)) {
			return true;
		}
		
		return false;
	}
}
