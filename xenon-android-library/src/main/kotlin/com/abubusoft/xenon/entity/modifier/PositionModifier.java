/**
 * 
 */
package com.abubusoft.xenon.entity.modifier;

import com.abubusoft.xenon.entity.BaseEntity;

/**
 * Modificatore di posizione per le entità.
 * 
 * @author Francesco Benincasa
 *
 */
public class PositionModifier extends StaticModifier {
	
	public static void moveBy(BaseEntity entity, float xOffset, float yOffset, float zOffset)
	{
		entity.position.add(xOffset, yOffset, zOffset);
	}
	
	public static void moveBy(BaseEntity entity, float xOffset, float yOffset)
	{
		entity.position.add(xOffset, yOffset, 0);
	}
	
	public static void moveTo(BaseEntity entity, float x, float y)
	{
		entity.position.setCoords(x, y, 0);
	}
	
	public static void moveTo(BaseEntity entity, float x, float y, float z)
	{
		entity.position.setCoords(x, y, z);
	}
	
}
