/**
 * 
 */
package com.abubusoft.xenon.scenemanagement;

import com.abubusoft.xenon.entity.Entity;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.math.Sphere;
import com.abubusoft.xenon.mesh.MeshTile;

/**
 * Permette di selezionare un oggetto in base al fatto che il punto di selezione
 * sia dentro la sfera di collisione dell'entità
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressWarnings("rawtypes")
public class BoundingBox2DSelector<E extends Entity> {
	Sphere bounding;
	Point3 selectorPoint;
	Point3 position;
	MeshTile sprite;
	float halfWidth;
	float halfHeight;

	public BoundingBox2DSelector() {
		bounding = new Sphere();
	}

	public void setSelectorPoint(Point3 selector) {
		selectorPoint = selector;
	}

	/**
	 * Indica se il punto di touch è in proiezione su un entity registrato
	 * @param entity
	 * @return
	 */
	public boolean isSelected(E entity) {
		position = entity.position;

		halfWidth = entity.mesh.boundingBox.width / 2f;
		halfHeight = entity.mesh.boundingBox.height / 2f;

		//
		if (
				((position.x - halfWidth) <= selectorPoint.x) && 
				((position.x + halfWidth) >= selectorPoint.x) && 
				((position.y - halfHeight) <= selectorPoint.y) && 
				((position.y + halfHeight) >= selectorPoint.y)) {
			return true;
		}

		return false;
	}
}
