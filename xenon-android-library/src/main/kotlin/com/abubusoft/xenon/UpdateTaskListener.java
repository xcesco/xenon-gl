package com.abubusoft.xenon;

import com.abubusoft.xenon.engine.Phase;

public interface UpdateTaskListener {

	/**
	 * <p>
	 * Prepara il frame in termini di spostamenti, collision detection etc etc.
	 * </p>
	 * 
	 * @param enlapsedTime
	 *            tempo trascorso dal frame precendente
	 * @param speedAdapter
	 *            dato una velocità espressa pixel/secondo, questo fattore di moltiplicazione deve essere moltiplicato per la velocità per avere la relativa velocità per il disegno
	 *            per quel frame.
	 */
	void onFramePrepare(Phase phase, long enlapsedTime, float speedAdapter);

}