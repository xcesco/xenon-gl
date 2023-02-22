package com.abubusoft.xenon.animations

import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindTypeVariables

@BindTypeVariables("K0,K", "K1")
@BindType
class TiledMapAnimation : Parallel2Animation<TranslationFrame?, TextureKeyFrame?>() {
    /**
     * Consente con un unico metodo di definire l'animazione in termini di texture e di spostamento da eseguire.
     *
     *
     * Il tempo viene definito dall'animazione.
     *
     * @param x
     * @param y
     * @param animation
     */
    fun setAnimation(x: Float, y: Float, animation: String?) {
        val animation1: TextureAnimation = TextureAnimationManager.Companion.instance().getAnimation(animation!!)!!
        val animation0 = Translation()
        animation0.setInterval(TranslationFrame.Companion.build(animation1.duration()), TranslationFrame.Companion.build(x, y, 0f, 0))
        setAnimation(animation0)
        setAnimation1(animation1)
    }
}