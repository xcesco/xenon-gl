package com.abubusoft.xenon.core.graphic.animation

import android.graphics.drawable.AnimationDrawable

/**
 * Animazione di uno sprite sottorma di animationdrawable. Serve a gestire le animazioni.
 *
 * @author Francesco Benincasa
 */
class BitmapAnimation(val name: String) {

    /**
     * animazioni
     */
    var frames: AnimationDrawable

    init {
        frames = AnimationDrawable()
    }
}