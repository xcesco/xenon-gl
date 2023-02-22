/**
 *
 */
package com.abubusoft.xenon.animations

import com.abubusoft.xenon.animations.events.EventFrameListener

/**
 * Rappresenta l'handler delle animazioni su tiledmap che comprendono sia lo spostamento che l'animazione di uno sprite.
 *
 * @author Francesco Benincasa
 */
class TiledMapAnimationHandler : Parallel2Handler<TranslationFrame?, TextureKeyFrame?>() {
    init {
        handler0 = TranslationHandler()
        handler1 = TextureAnimationHandler()
    }

    /**
     * Imposta il listener relativo allo spostamento.
     * @param value
     */
    fun setOnMoveEventListener(value: EventFrameListener<TranslationFrame?>?) {
        handler0!!.setFrameListener(value)
    }
}