/**
 *
 */
package com.abubusoft.xenon.animations

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindXml

/**
 * @author Francesco Benincasa
 */
abstract class Parallel2Animation<K0 : KeyFrame?, K1 : KeyFrame?> : Animation<K0>() {
    /**
     * Sequenza di frame
     */
    @Bind("frames1")
    @BindXml(elementTag = "frame")
    var frames1: ArrayList<K1?>?

    init {
        frames1 = ArrayList()
    }

    /**
     *
     *
     * Aggiunge un keyframe. Se il nome non esiste, viene aggiunto di default con la regola `keyframe + i` dove i è va da 0 a n (numero di frame).
     *
     *
     * @param frame
     * frame da aggiungere
     * @return
     * indice del frame appena inserito
     */
    fun add1(frame: K1): Int {
        // se non abbiamo un nome, lo aggiungiamo di default
        if (frame!!.name == null) {
            frame.name = "keyframe1-" + frames!!.size
        }
        frames1!!.add(frame)
        return frames1!!.size - 1
    }

    /**
     * misura la durata. Da tenere in considerazione che questa non ha alcun senso se il flag loop è impostato a true, dato che l'animazione andrà avanti all'infinito.
     *
     * @return
     */
    override fun duration(): Long {
        var maxDuration: Long = 0
        run {

            // frame
            var duration: Long = 0
            val n = frames!!.size
            for (i in 0 until n) {
                duration += frames!![i]!!.duration
            }
            maxDuration = Math.max(maxDuration, (duration * rate).toLong())
        }
        run {

            // frame 1
            var duration: Long = 0
            val n = frames1!!.size
            for (i in 0 until n) {
                duration += frames1!![i]!!.duration
            }
            maxDuration = Math.max(maxDuration, (duration * rate).toLong())
        }
        return maxDuration
    }

    fun getFrame1(index: Int): K1? {
        return frames1!![index]
    }

    fun setAnimation(value: Animation<K0>) {
        frames = value.frames
    }

    fun setAnimation1(value: Animation<K1>) {
        frames1 = value.frames
    }

    companion object {
        /**
         * numero di animazioni
         */
        const val NUMBER_OF_ANIMATIONS = 2
    }
}