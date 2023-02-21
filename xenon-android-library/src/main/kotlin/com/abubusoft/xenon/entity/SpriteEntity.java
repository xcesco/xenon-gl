/**
 * 
 */
package com.abubusoft.xenon.entity;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.mesh.MeshSprite;

/**
 * Entità associata ad uno sprite.
 * 
 * @author Francesco Benincasa
 * 
 */
public class SpriteEntity extends Entity<MeshSprite> {

	private static final long serialVersionUID = -7603400179112407353L;

	@Override
	public float getBoundingRadius() {
		return XenonMath.max(mesh.boundingBox.width, mesh.boundingBox.height)/2.0f;
	}
	
	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
	@Override
	public SpriteEntity copy() {
		SpriteEntity copy=null;
		try {
			// cloniamo
			copy = getClass().newInstance();
			copyInto(copy);						
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return copy;
	}

	/**
	 * Effettua la copia nell'oggetto destinazione
	 * 
	 * @param destination
	 */
	public void copyInto(SpriteEntity destination) {
		super.copyInto(destination);	
	}
	

}
