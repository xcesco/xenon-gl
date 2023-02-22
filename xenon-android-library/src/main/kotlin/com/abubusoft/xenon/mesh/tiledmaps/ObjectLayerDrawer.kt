package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.xenon.math.Matrix4x4

/**
 *
 *
 * Drawer relativo ad un layer di un tiledMap.
 *
 *
 * @author Francesco Benincasa
 */
interface ObjectLayerDrawer {
    /**
     *
     *
     * Firma del metodo per disegnare un object layer. Il layer può traslare in
     * modo costante, senza interruzione di continuità. Questo grazie ai due
     * offset, che consentono di spostare le tile di un numero di pixel anche
     * minore rispetto alla dimensione delle tile.
     *
     *
     * @param tiledMap
     * map
     * @param layer
     * layer di oggetti da disegnare
     * @param enlapsedTime
     * tempo in ms trascorso
     * @param modelview
     * è la matrice mvp usata per disegnare l'intera mappa in mezzo
     * allo schermo.
     */
    fun onObjectLayerFrameDraw(tiledMap: TiledMap?, layer: ObjectLayer?, enlapsedTime: Long, modelview: Matrix4x4?)
}