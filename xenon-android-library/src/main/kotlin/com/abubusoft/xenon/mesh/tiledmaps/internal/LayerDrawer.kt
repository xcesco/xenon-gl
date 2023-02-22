package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.shader.ShaderTiledMap

interface LayerDrawer {
    /**
     *
     *
     * Firma del metodo per disegnare il layer. Il layer può traslare in modo
     * costante, senza interruzione di continuità. Questo grazie ai due offset,
     * che consentono di spostare le tile di un numero di pixel anche minore
     * rispetto alla dimensione delle tile.
     *
     *
     * @param shader
     * shader da utilizzare
     * @param enlapsedTime
     * tempo in ms trascorso dall'ultimo frame
     * @param startLayerColumn
     * colonna del layer dal quale si inizia a disegnare
     * @param startLayerRow
     * riga del layer dal quale si inizia a disegnare
     * @param offsetX
     * offset startX in termini di pixel. Rappresenta lo spiazzamento
     * inferiore alla larghezza di una tile che bisogna applicare al
     * layer per la traslazione continua.
     * @param offsetY
     * offset startY in termini di pixel. Rappresenta lo spiazzamento
     * inferiore all'altezza di una tile che bisogna applicare al
     * layer per la traslazione continua.
     * @param modelview
     * è la matrice mvp usata per disegnare l'intera mappa in mezzo
     * allo schermo.
     */
    fun drawLayer(shader: ShaderTiledMap?, enlapsedTime: Long, startLayerColumn: Int, startLayerRow: Int, offsetX: Int, offsetY: Int, modelview: Matrix4x4?)
}