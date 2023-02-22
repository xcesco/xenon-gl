package com.abubusoft.xenon

import com.abubusoft.xenon.engine.Phase

interface UpdateTaskListener {
    /**
     *
     *
     * Prepara il frame in termini di spostamenti, collision detection etc etc.
     *
     *
     * @param enlapsedTime
     * tempo trascorso dal frame precendente
     * @param speedAdapter
     * dato una velocità espressa pixel/secondo, questo fattore di moltiplicazione deve essere moltiplicato per la velocità per avere la relativa velocità per il disegno
     * per quel frame.
     */
    fun onFramePrepare(phase: Phase?, enlapsedTime: Long, speedAdapter: Float)
}