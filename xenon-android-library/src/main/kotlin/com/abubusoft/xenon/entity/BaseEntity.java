/**
 * 
 */
package com.abubusoft.xenon.entity;

import java.io.Serializable;

import com.abubusoft.xenon.math.Point3;

/**
 * <p>E' la base di tutte le entità. Contiene tutto quello che definisce di base
 * un'entità astratta.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public class BaseEntity implements Serializable {
	
	/**
	 * Costruttore di default
	 */
	public BaseEntity() {
		position = new Point3();		
		rotationAngles = new Point3();
		scale = 1.0f;
		
		oldPosition = new Point3();
		oldRotationAngles = new Point3();
	}

	private static final long serialVersionUID = -2432547140052127109L;

	/**
	 * centro del sistema di riferimento dell'entità
	 */
	public final Point3 position;

	/**
	 * angoli di rotazione espressi in gradi
	 */
	public final Point3 rotationAngles;
	

	/**
	 * posizione di salvataggio
	 */
	public final Point3 oldPosition;

	/**
	 * rotazione di salvataggio
	 */
	public final Point3 oldRotationAngles;
	
	/**
	 * fattore di scala
	 */
	public float scale;
	
	/**
	 * salva i valori attuali di posizione e rotazione in oldPosition e oldRotation
	 */
	public void savePosition() {
		position.copyInto(oldPosition);
		rotationAngles.copyInto(oldRotationAngles);
	}

	/**
	 * Ripristina i vecchi valore di posizione e rotazione
	 */
	public void restorePosition() {
		oldPosition.copyInto(position);
		oldRotationAngles.copyInto(rotationAngles);
	}
	

	/**
	 * Copia l'oggetto
	 * 
	 * @return
	 */
	public BaseEntity copy() {
		BaseEntity copy=null;
		try {
			// creiamo nuova istanza
			copy = getClass().newInstance();
			
			position.copyInto(copy.position);
			rotationAngles.copyInto(copy.rotationAngles);
			copy.scale=scale;
			
			oldPosition.copyInto(copy.oldPosition);
			oldRotationAngles.copyInto(copy.oldRotationAngles);									
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
	public void copyInto(BaseEntity destination) {
		position.copyInto(destination.position);
		rotationAngles.copyInto(destination.rotationAngles);
		
		oldPosition.copyInto(destination.oldPosition);
		oldRotationAngles.copyInto(destination.oldRotationAngles);
		
		destination.scale=scale;
	}

}
