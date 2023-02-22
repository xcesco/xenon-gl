/**
 *
 */
package com.abubusoft.xenon.engine

/**
 *
 * Rappresenta un dato condiviso tra la fase LOGIC e la fase
 * RENDER. Nello specifico, copia i dati di render
 *
 * @author Francesco Benincasa
 */
interface SharedData {
    /**
     * passa i dati dallo phase LOGIC alla fase RENDER
     */
    fun update()
}