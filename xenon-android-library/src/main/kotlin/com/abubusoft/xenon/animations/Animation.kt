package com.abubusoft.xenon.animations

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindXml

/**
 *
 * Rappresenta un'animazione, o per meglio dire la sua definizione. Gli oggetti di questo tipo non vengono utilizzati direttamente per animare le entità, quanto per definire come
 * animarli.
 *
 * @author Francesco Benincasa
 *
 * @param <K>
</K> */
open class Animation<K : KeyFrame?> {
    /**
     * Sequenza di frame
     */
    @Bind(order = 4)
    @BindXml(elementTag = "frame")
    var frames: ArrayList<K>?
    /**
     * Se `true indica
     *
     * @return
    ` */
    /**
     * loop dell'animazione
     */
    @Bind(order = 1)
    var isLoop = false
    /**
     * @return the name
     */
    /**
     * @param name the name to set
     */
    /**
     * nome dell'animazione
     */
    @Bind(order = 0)
    var name: String? = null
    /**
     * @return the rate
     */
    /**
     * Defines the direction/speed at which the AbstractAnimation is expected to be played. The absolute value of rate indicates the speed which the AbstractAnimation is to be played, while the
     * sign of rate indicates the direction. A positive value of rate indicates forward play, a negative value indicates backward play and 0.0 to stop a running AbstractAnimation. Rate 1.0
     * is normal play, 2.0 is 2 time normal, -1.0 is backwards, etc... Inverting the rate of a running AbstractAnimation will cause the AbstractAnimation to reverse direction in place and play
     * back over the portion of the AbstractAnimation that has already elapsed.
     *
     * **DefaultValue: 1.0**
     */
    @Bind(order = 3)
    var rate = 1.0f

    /**
     * costruttore
     */
    constructor() {
        frames = ArrayList()
        rate = 1.0f
        isLoop = false

        // autoReverse=false;
    }
    /**
     *
     *
     * Defines the number of cycles in this animation. The cycleCount may be INDEFINITE for animations that repeat indefinitely, but must otherwise be > 0. It is not possible to
     * change the cycleCount of a running AbstractAnimation. If the value of cycleCount is changed for a running AbstractAnimation, the animation has to be stopped and started again to pick up the
     * new value.
     *
     *
     * **DefaultValue: 1.0**
     */
    //public int cycleCount=1;
    /**
     * costruttore
     */
    constructor(values: ArrayList<K>?) {
        frames = values
        rate = 1.0f
        isLoop = false

        // autoReverse=false;
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
    fun add(frame: K): Int {
        // se non abbiamo un nome, lo aggiungiamo di default
        if (frame!!.name == null) {
            frame.name = "keyframe" + frames!!.size
        }
        frames!!.add(frame)
        return frames!!.size - 1
    }

    /**
     * misura la durata. Da tenere in considerazione che questa non ha alcun senso se il flag loop è impostato a true, dato che l'animazione andrà avanti all'infinito.
     *
     * @return
     */
    open fun duration(): Long {
        var duration: Long = 0
        val n = frames!!.size
        for (i in 0 until n) {
            duration += frames!![i]!!.duration
        }
        return (duration * rate).toLong()
    }

    fun getFrame(index: Int): K {
        return frames!![index]
    }
    /**
     * durata dell'animazione. Anche se è infinita, viene restituito il conto dei ms di durata dei vari frame (moltiplicati per il cycleCount).
     * @return
     */
    /**
     *
     *
     * Defines whether this AbstractAnimation reverses direction on alternating cycles. If true, the AbstractAnimation will proceed forward on the first cycle, then reverses on the second cycle,
     * and so on. Otherwise, animation will loop such that each cycle proceeds forward from the start. It is not possible to change the autoReverse flag of a running AbstractAnimation. If
     * the value of autoReverse is changed for a running AbstractAnimation, the animation has to be stopped and started again to pick up the new value.
     *
     *
     * **DefaultValue: false**
     */
    //public boolean autoReverse=false;
    /**
     *
     * Definisce due frame: il primo con i valori di default ed il secondo con il valore che viene passato qua. L'intervallo di
     * tempo viene utilizzato per definire il passaggio tra i due.
     *
     *
     * L'intervallo annulla gli altri eventuali frame già presenti.
     *
     * @param value
     * @param duration
     */
    fun setInterval(firstFrame: K, secondFrame: K) {
        frames!!.clear()
        add(firstFrame)
        add(secondFrame)
    }

    /**
     * numero di frames
     *
     * @return
     */
    fun size(): Int {
        return frames!!.size /* *this.cycleCount */
    }
}