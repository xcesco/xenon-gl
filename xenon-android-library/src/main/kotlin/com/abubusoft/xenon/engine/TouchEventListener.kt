/**
 *
 */
package com.abubusoft.xenon.engine

/**
 * @author Francesco Benincasa
 */
interface TouchEventListener {
    /**
     *
     * Evento relativo al touch
     *
     * @param type
     * @param x
     * @param y
     */
    fun onTouch(type: TouchType?, x: Float, y: Float)
}