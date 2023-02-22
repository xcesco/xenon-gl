package com.abubusoft.xenon.animations.events

import com.abubusoft.xenon.animations.KeyFrame

interface EventAnimationListener<K : KeyFrame?> {
    /**
     * avvio animazione
     */
    fun onAnimationStart()

    /**
     * termina animazione
     *
     * @param currentFrame
     */
    fun onAnimationStop(currentFrame: K)

    /**
     * metto in pausa
     */
    fun onAnimationPause(currentFrame: K, enlapsedTime: Long)

    /**
     * resume dell'animazione
     */
    fun onAnimationResume(currentFrame: K, enlapsedTime: Long)
}