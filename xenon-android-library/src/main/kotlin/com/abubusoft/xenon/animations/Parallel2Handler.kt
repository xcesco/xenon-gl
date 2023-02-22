package com.abubusoft.xenon.animations

import com.abubusoft.xenon.animations.events.EventFrameListener

/**
 *
 *
 * Gestisce più animazioni contemporaneamente.
 *
 *
 *
 * Gli handler devono venire definiti a livello di costruttore!.
 *
 *
 *  * aggiunge gli handler
 *  * imposta l'animazione
 *
 *
 *
 *
 *
 *
 * @author Francesco Benincasa
 */
abstract class Parallel2Handler<K0 : KeyFrame?, K1 : KeyFrame?> : AnimationHandler<K0?>() {
    /**
     * Sequenza di frame
     */
    protected var frames1: ArrayList<K1>? = null
    protected var handler0: AnimationHandler<K0>? = null
    protected var handler1: AnimationHandler<K1>? = null
    override fun setFrameListener(listener: EventFrameListener<K0?>?) {
        handler0!!.setFrameListener(listener)
    }

    fun setFrameListener1(listener: EventFrameListener<K1?>?) {
        handler1!!.setFrameListener(listener)
    }

    override fun update(enlapsedTimeValue: Long): K0? {
        var allStopped = true

        // calcoliamo i valori e li mettiamo in temp
        handler0!!.update(enlapsedTimeValue)
        handler1!!.update(enlapsedTimeValue)
        allStopped = handler0!!.status == StatusType.STOPPED && handler1!!.status == StatusType.STOPPED

        // se tutti sono terminati, allora è terminato
        if (allStopped) status = StatusType.STOPPED
        return handler0!!.value()
    }

    /**
     * Restituisce il valore 1
     *
     * @return
     */
    override fun value(): K0? {
        return handler0!!.value()
    }

    /**
     * Restituisce il valore 1
     *
     * @return
     */
    fun value1(): K1? {
        return handler1!!.value()
    }

    override fun reset() {
        handler0!!.reset()
        handler1!!.reset()
        status = StatusType.STOPPED
    }

    override fun playFromStart() {
        handler0!!.playFromStart()
        handler1!!.playFromStart()
        status = StatusType.RUNNING
    }

    override fun play() {
        handler0!!.play()
        handler1!!.play()
        status = StatusType.RUNNING
    }

    override fun stop() {
        handler0!!.stop()
        handler1!!.stop()
        status = StatusType.STOPPED
    }

    override fun oneMoreTime() {
        handler0!!.oneMoreTime()
        handler1!!.oneMoreTime()
    }

    override fun pause() {
        handler0!!.pause()
        handler1!!.pause()
        status = StatusType.PAUSED
    }

    override val isFinished: Boolean
        get() = status == StatusType.STOPPED

    override fun duration(): Long {
        var duration: Long = 0
        duration = Math.max(duration, handler0!!.duration())
        duration = Math.max(duration, handler1!!.duration())
        return duration
    }

    override fun value(current: K0?, enlapsedTime: Long, next: K0?): K0? {
        // TODO Auto-generated method stub
        return null
    }

    override fun buildFrame(): K0? {
        // non serve
        return null
    }

    override fun set(value: Animation<K0?>?) {
        val valueA = value as Parallel2Animation<K0?, K1>?
        animation = null
        copyFrom(value)
        inc = 1 // animation.rate > 0 ? 1 : -1;
        status = StatusType.STOPPED
        enlapsedTime = 0
        cycleCount = 0

        // frame di appoggio per il calcolo del frame corrente
        temp = null
        val anim = Animation(valueA!!.frames)
        handler0!!.set(anim)
        val anim1 = Animation(valueA.frames1)
        handler1!!.set(anim1)
    }
}