package com.abubusoft.xenon.mesh

import com.abubusoft.xenon.animations.AnimationHandler.value

/**
 *
 *
 * Tipo di mesh: a base di triangoli o a base di quadrati. Questi ultimi richiedono obbligatoriamente l'uso di indici.
 *
 *
 * @author Francesco Benincasa
 */
enum class MeshType {
    /**
     *
     *
     * Gli shape vengono definiti su base triangolare indicizzata, quindi i triangoli possono riusare i vertici tra loro.
     *
     */
    //INDEXED_TRIANGLES_BASED(true),
    /**
     *
     *
     * Gli shape vengono definiti su base triangolare, quindi per definire un quadrato occorrono 6 vertici, dato che ogni triangolo non ha nulla di condiviso con gli altri.
     *
     */
    TRIANGLES_BASED,

    /**
     *
     *
     * Gli shape vengono definiti su base di quadrato. Questo tipo di rappresentazione in realtà è una forzatura, nel senso che si lavora sempre con dei triangoli, con la
     * differenza che si ragiona però a gruppi di 4 vertici per volta e non di 3.
     *
     */
    QUAD_BASED //public final boolean useIndexes;
    /**
     *
     * Usa degli shapeType.
     * @param value
     */
    //	MeshType(boolean value)
    //	{
    //		useIndexes=value;
    //	}
}