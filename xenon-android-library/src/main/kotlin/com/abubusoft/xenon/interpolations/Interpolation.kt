package com.abubusoft.xenon.interpolations

/**
 * Funzione atta a calcolare la percentuale
 * @author Francesco Benincasa
 */
interface Interpolation {
    /**
     * Restituisce la percentuale di tempo trascorso in base alla funzione implementata.
     *
     * @param enlapsedTime
     * tempo trascorso in secondi
     * @param duration
     * tempo massimo in secondi
     * @return
     * percentuale da 0 a 1
     */
    fun getPercentage(enlapsedTime: Float, duration: Float): Float
}