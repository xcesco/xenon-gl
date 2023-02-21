package com.abubusoft.xenon.entity.modifier;

import com.abubusoft.xenon.entity.BaseEntity;

/**
 * Modificatore per la rotazione di un'entità.
 * 
 * @author Francesco Benincasa
 *
 */
public class RotationModifier extends DynamicModifier {
	/**
	 * Definisce l'angolo di rotazione per i vari assi per un entità.
	 * 
	 * @param entity
	 * @param xAngle
	 * @param yAngle
	 * @param zAngle
	 */
	public static void rotateBy(BaseEntity entity, float xAngle, float yAngle, float zAngle)
	{
		entity.rotationAngles.setCoords(xAngle, yAngle, zAngle);
	}
	
}
