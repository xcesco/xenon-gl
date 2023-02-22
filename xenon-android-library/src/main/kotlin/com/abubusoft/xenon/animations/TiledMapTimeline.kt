/**
 *
 */
package com.abubusoft.xenon.animations

import com.abubusoft.xenon.animations.events.EventFrameListener

/**
 * @author Francesco Benincasa
 */
class TiledMapTimeline : Timeline<TiledMapAnimation?, TranslationFrame?, TiledMapAnimationHandler?>() {
    init {
        handler = TiledMapAnimationHandler()
    }

    /**
     * Imposta il listener relativo allo spostamento.
     * @param value
     */
    fun setOnMoveEventListener(value: EventFrameListener<TranslationFrame?>?) {
        handler!!.setFrameListener(value)
    }
}