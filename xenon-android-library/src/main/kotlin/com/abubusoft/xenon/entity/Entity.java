/**
 * 
 */
package com.abubusoft.xenon.entity;


import com.abubusoft.xenon.mesh.Mesh;

/**
 * @author Francesco Benincasa
 * 
 */
public class Entity<E extends Mesh> extends BaseEntity {

	private static final long serialVersionUID = 3212120492925326047L;

	public Entity(E shapeValue) {
		this();
		mesh = shapeValue;
	}

	/**
	 * Costruttore di default
	 */
	public Entity() {
		super();
		visible = true;
		collidable = true;
	}

	/**
	 * indicase se l'entità è visibile
	 */
	public boolean visible;

	/**
	 * indica se l'entità è soggetta a collisioni
	 */
	public boolean collidable;

	/**
	 * mesh associato all'entity
	 */
	public E mesh;

	/**
	 * Raggio del cerchio di contenimento
	 * 
	 * @return
	 */
	public float getBoundingRadius() {
		return mesh.boundingSphereRadius;
	}

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Entity<E> copy() {
		Entity<E> copy=null;
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
	public void copyInto(Entity<E> destination) {
		super.copyInto(destination);
		destination.collidable=collidable;
		destination.mesh=mesh;
		destination.visible=visible;
	}

}
