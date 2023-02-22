package com.abubusoft.xenon.animations.events

import com.abubusoft.xenon.animations.KeyFrame

interface EventFrameListener<K : KeyFrame?> {
    /**
     * Evento relativo al cambio del key frame. Siamo all'inizio del frame.
     *
     * @param currentFrame
     * key frame attualmente usato.
     */
    fun onFrameBegin(currentFrame: K)

    /**
     * fine frame
     * @param currentFrame
     */
    fun onFrameEnd(currentFrame: K)
}