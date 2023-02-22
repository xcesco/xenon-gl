/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps

/**
 * @author Francesco Benincasa
 */
class ObjClass : ObjBase() {
    /**
     * layer di appartenenza dello shape
     */
    var shapeLayer: TiledLayer? = null

    /**
     * prima colonna da considerare per lo shape
     */
    var shapeColBegin = 0
    var shapeColSize = 0
    var shapeRowBegin = 0
    var shapeRowSize = 0

    /**
     * pixel da considerare in orizzontale per collocare lo shape rispetto alla posizione delle tile
     */
    var shapeColOffset = 0

    /**
     * pixel da considerare in verticale per collocare lo shape rispetto alla posizione delle tile
     */
    var shapeRowOffset = 0

    /**
     *
     * Elenco delle parti che compongono l'elemento.
     */
    var parts = ArrayList<ObjDefinition>()

    init {
        category = CategoryType.CLASS
    }
}