package com.abubusoft.xenon.animations

/**
 *
 *
 * Definiamo come **animation** la sequenza di frame caratterizzati da una durata. Ci possono essere
 * diversi tipi di animazioni.
 *
 *
 *
 *
 * Le animazioni possono essere accodate, stando attenti al fatto che le animazioni a loop non finiranno mai.
 *
 *
 *
 * @author Francesco Benincasa
 * @param <F>
</F> */
class TextureTimeline : Timeline<TextureAnimation?, TextureKeyFrame?, TextureAnimationHandler?>() {
    init {
        handler = TextureAnimationHandler()
    }
}