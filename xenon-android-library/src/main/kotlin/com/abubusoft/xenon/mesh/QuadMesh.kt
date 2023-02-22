/**
 * 
 */
package com.abubusoft.xenon.mesh;

import com.abubusoft.kripton.annotation.BindType;

/**
 * <p>
 * Mesh basate su quadrati, e non triangoli. Comunque di sotto ci sono triangoli.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@BindType
public class QuadMesh extends Mesh {

	private static final long serialVersionUID = 4361946217970924460L;
	
	/**
	 * <p>Numero di vertici presenti in quad (rettangolo) i cui vertici sono indicizzati.</p>
	 */
	public static final int VERTEX_IN_INDEXED_QUAD = 4;

	/**
	 * Definiamo costruttore con scope package, in modo da non poter essere definito senza l'apposita factory
	 */
	QuadMesh() {
		// di default è su base triangolare. Qua impostiamo il fatto che 
		// la mesh si basa su quadrati.
		type = MeshType.QUAD_BASED;
	}
}
