package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.shader.ShaderTiledMap;

public interface LayerDrawer {

	/**
	 * <p>
	 * Firma del metodo per disegnare il layer. Il layer può traslare in modo
	 * costante, senza interruzione di continuità. Questo grazie ai due offset,
	 * che consentono di spostare le tile di un numero di pixel anche minore
	 * rispetto alla dimensione delle tile.
	 * </p>
	 * 
	 * @param shader
	 *            shader da utilizzare
	 * @param enlapsedTime
	 *            tempo in ms trascorso dall'ultimo frame
	 * @param startLayerColumn
	 *            colonna del layer dal quale si inizia a disegnare
	 * @param startLayerRow
	 *            riga del layer dal quale si inizia a disegnare
	 * @param offsetX
	 *            offset startX in termini di pixel. Rappresenta lo spiazzamento
	 *            inferiore alla larghezza di una tile che bisogna applicare al
	 *            layer per la traslazione continua.
	 * @param offsetY
	 *            offset startY in termini di pixel. Rappresenta lo spiazzamento
	 *            inferiore all'altezza di una tile che bisogna applicare al
	 *            layer per la traslazione continua.
	 * @param modelview
	 *            è la matrice mvp usata per disegnare l'intera mappa in mezzo
	 *            allo schermo.
	 */
	void drawLayer(ShaderTiledMap shader, long enlapsedTime,  int startLayerColumn, int startLayerRow, int offsetX, int offsetY, Matrix4x4 modelview);
}
