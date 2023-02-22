/**
 *
 */
package com.abubusoft.xenon.mesh

import com.abubusoft.kripton.annotation.BindType

/**
 *
 *
 * Mesh basate su quadrati, e non triangoli. Comunque di sotto ci sono triangoli.
 *
 *
 * @author Francesco Benincasa
 */
@BindType
open class QuadMesh internal constructor() : Mesh() {
    /**
     * Definiamo costruttore con scope package, in modo da non poter essere definito senza l'apposita factory
     */
    init {
        // di default è su base triangolare. Qua impostiamo il fatto che 
        // la mesh si basa su quadrati.
        type = MeshType.QUAD_BASED
    }

    companion object {
        private const val serialVersionUID = 4361946217970924460L

        /**
         *
         * Numero di vertici presenti in quad (rettangolo) i cui vertici sono indicizzati.
         */
        const val VERTEX_IN_INDEXED_QUAD = 4
    }
}