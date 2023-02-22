/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.abubusoft.xenon.animations

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.animations.KeyFrame
import com.abubusoft.xenon.animations.TextureTimeline
import com.abubusoft.xenon.core.collections.SmartQueue

/**
 * A `Timeline` can be used to define a free from animation of any [javafx.beans.value.WritableValue], e.g. all [JavaFX Properties][javafx.beans.property.Property]
 * .
 *
 *
 * A `Timeline`, defined by one or more [KeyFrame]s, processes individual `KeyFrame` sequentially, in the order specified by `KeyFrame.time`. The animated
 * properties, defined as key values in `KeyFrame.values`, are interpolated to/from the targeted key values at the specified time of the `KeyFrame` to `Timeline`
 * 's initial position, depends on `Timeline`'s direction.
 *
 *
 * `Timeline` processes individual `KeyFrame` at or after specified time interval elapsed, it does not guarantee the timing when `KeyFrame` is processed.
 *
 *
 * The [.cycleDurationProperty] will be set to the largest time value of Timeline's keyFrames.
 *
 *
 * If a `KeyFrame` is not provided for the `time==0s` instant, one will be synthesized using the target values that are current at the time [.play] or
 * [.playFromStart] is called.
 *
 *
 * It is not possible to change the `keyFrames` of a running `Timeline`. If the value of `keyFrames` is changed for a running `Timeline`, it has to be
 * stopped and started again to pick up the new value.
 *
 * @see TextureTimeline
 *
 * @see KeyFrame
 *
 * @see KeyValue
 *
 *
 * @since JavaFX 2.0
 */
/**
 * @author Francesco Benincasa
 *
 * @param <V>
 * @param <A>
 * @param <M>
</M></A></V> */
open class Timeline<A : Animation<K>?, K : KeyFrame?, H : AnimationHandler<K>?> {
    /**
     * Avvia l'animazione
     *
     * @param name
     * @param enlapsedTimeValue
     * @return
     */
    fun play() {
        // immettiamo animation e la avviamo
        if (channel.size() == 0) {
            // assert: non abbiamo alcun elemento
            Logger.error("No animation to play on timeline %s!", id)
        } else {
            handler!!.set(channel.pop())
            handler!!.play()
        }
    }
    /**
     *
     *
     * Aggiunge un elemento alla coda.
     *
     *
     * @param animationId
     */
    /**
     *
     *
     * Aggiunge un elemento alla coda.
     *
     *
     * @param animationId
     */
    @JvmOverloads
    fun add(animation: A, forceRun: Boolean = false) {
        channel.add(animation)
        if (forceRun && handler!!.status == AnimationHandler.StatusType.STOPPED) {
            // se è bloccato, lo facciamo partire
            play()
        }
    }

    /**
     *
     */
    fun oneMoreTime() {
        if (handler != null && handler!!.animation != null) {
            handler!!.oneMoreTime()
        } else {
            Logger.warn("No oneMoreTime, because no animation is associated to timeline.")
        }
    }

    /**
     * rimuove tutti gli elementi accodati. Se un'animazione è in esecuzione, viene mantenuta
     *
     */
    fun removeQueue() {
        channel.clear()
    }

    /**
     * facciamo in modo che l'attuale animazione, se in loop, venga terminata (con il suo scorrere normale). Se in stato di running viene posto il loop dell'handler a false.
     *
     * Se l'animazione invece finirà di suo, allora non viene fatto niente.
     */
    fun forceNext() {
        if (handler!!.status == AnimationHandler.StatusType.RUNNING) {
            handler.loop = false
        }
    }

    fun setHandler(value: H) {
        handler = value
    }

    /**
     * indice dell'animator
     */
    var index = 0
    val isAnimationFinished: Boolean
        get() = handler!!.isFinished

    /**
     * Durata dell'animazione. Usa il cursor
     *
     * @return
     */
    fun duration(): Long {
        var duration: Long = 0
        channel.cursorReset()
        while (channel.cursorHasNext()) {
            duration += channel.cursorValue()!!.duration()
            channel.cursorNext()
        }
        return duration
    }

    /**
     *
     *
     * Nome dell'animazione corrente.
     *
     *
     * @return
     */
    val animationName: String?
        get() = handler.getAnimationName()
    /**
     * @return the handler
     */
    /**
     * handler dell'animazione
     */
    var handler: H? = null
        protected set

    /**
     * id
     */
    var id: String? = null

    /**
     *
     *
     * Blocca l'animazione.
     *
     */
    fun stop() {
        handler!!.stop()
    }

    /**
     *
     *
     * Sequenza delle animazioni da eseguire.
     *
     */
    protected var channel: SmartQueue<A>
    /**
     *
     *
     * Se l'animazione corrente è diversa da quella selezionata, la sostituisce e cancella tutte le altre animazioni che sono state accodate.
     *
     *
     * @param animationId
     *
     * @return
     */
    /*
	 * public boolean replaceWith(A animation) { return replaceWith(animation, true); }
	 */
    /**
     *
     *
     * Sostituisce l'animazione corrente con quella il cui nome è passato come parametro. Il secondo parametro indica se cancellare o meno la coda di animazioni. Se l'animazione
     * corrente corrisponde già a quella selezionata, non fa nulla.
     *
     *
     * @param animationId
     * @return
     */
    /*
	 * public boolean replaceWith(String animationId, boolean clearQueue) { // se lo abbiamo già come animazione corrente, lo lasciamo in pace. // questo per evitare che in caso di
	 * animazione infinita, l'animazione venga interrotta. if (!animationId.equals(animation.name)) { start(animationId); }
	 * 
	 * if (clearQueue) channel.clear();
	 * 
	 * return false; }
	 */
    /**
     *
     *
     * Costruttore con l'animazione da far partire subito.
     *
     *
     * @param timelineName
     * nome della timeline
     * @param animationName
     * animazione da far partir subito
     * @param manager
     * manager delle animazioni
     */
    init {
        channel = SmartQueue(16)
    }

    /**
     * Restituisce il valore corrente dell'animazione.
     *
     * @return valore corrente
     */
    fun value(): K {
        return handler!!.value()
    }

    /**
     *
     *
     * Aggiorna lo stato dell'animazione.
     *
     *
     * @param enlapsedTime
     * tempo trascorso dall'inizio dell'animazione
     * @return
     */
    fun update(enlapsedTime: Long): K {
        val value: K
        var exceedTime: Long
        value = handler!!.update(enlapsedTime)

        // andiamo avanti finchè abbiamo esaurito il tempo
        while (handler!!.isFinished && channel.size() > 0) {
            exceedTime = handler!!.remaingTime
            handler!!.set(channel.pop())
            handler!!.play()
            handler!!.update(exceedTime)
        }
        return value
    }

    val isFinished: Boolean
        get() = handler!!.isFinished
    val isAnimationPlaying: Boolean
        get() = handler!!.isPlaying
}