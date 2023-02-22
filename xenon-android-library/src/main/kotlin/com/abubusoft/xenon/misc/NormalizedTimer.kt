/**
 *
 */
package com.abubusoft.xenon.misc

/**
 * Rappresenta un touchTimer normalizzato. Viene aggiornato in millisecondi.
 *
 * Il range va da 0 a 1. Il max time viene espresso in millisecondi.
 *
 * @author Francesco Benincasa
 */
class NormalizedTimer {
    /**
     * Tipi di touchTimer.
     *
     * @author Francesco Benincasa
     */
    enum class TypeNormalizedTimer {
        /**
         * viene eseguito una volta sola.
         */
        ONE_TIME,

        /**
         * viene eseguito all'infinito. Quando arriva alla fine, ricomincia
         */
        REPEAT_FOREVER
    }

    /**
     * tempo corrente in millisecondi
     */
    var enlapsedTime: Long = 0

    /**
     * tempo massimo
     */
    var durationTime: Long = 0

    /**
     * tipo di touchTimer
     */
    var type: TypeNormalizedTimer

    /**
     *
     *
     * Costruttore.
     *
     *
     * @param value
     * tipo di touchTimer
     */
    constructor(value: TypeNormalizedTimer) {
        reset()
        type = value
    }

    /**
     *
     *
     * Costruttore.
     *
     *
     * @param value
     * tipo di touchTimer
     * @param durationTimeValue
     * durata in millisecondi del touchTimer
     */
    constructor(value: TypeNormalizedTimer, durationTimeValue: Long) {
        reset()
        type = value
        durationTime = durationTimeValue
    }

    /**
     * resetta il touchTimer
     */
    fun reset() {
        enlapsedTime = 0
        currentClock = Clock.now()
    }

    /**
     * Imposta il tempo massimo, quello a cui arrivare
     *
     * @param maxTimeValue
     */
    fun setMaxTime(maxTimeValue: Long) {
        durationTime = maxTimeValue
    }

    /**
     * Aggiorna il touchTimer in millisecondi. Usa il orologio interno per determinare quanti ms sono passati.
     *
     * @return se true indica che ha raggiunto il tempo massimo
     */
    fun update(): Boolean {
        val delta = Clock.now() - currentClock
        currentClock += delta
        return update(delta)
    }

    /**
     *
     *
     * Accetta in ingresso un valore da 0 a 1 (se non è così viene comunque definito nel range [0, 1]) e dopo averlo riportato in millisecondi, lo aggiunge al timer.
     *
     *
     * @param value
     * valore da 0 a 1 da aggiungere
     * @return come [.update]
     */
    fun addNormalizedValue(value: Float): Boolean {
        var value = value
        value = value * durationTime
        return update(value.toLong())
    }

    /**
     *
     *
     * Aggiorna il tempo in millisecondi. Se true indica che ha raggiunto il tempo massimo. Se aggiungiamo un delta negativo ad un timer
     * one_time, questo non piò scendere sotto 0. Se invece il timer è infinito, sotto lo 0 viene applicata la formula durationTime-mills
     *
     *
     * @param mills
     * tempo in millisecondi trascorsi dall'ultima iterazione
     * @return se true indica che ha raggiunto il tempo massimo
     */
    fun update(mills: Long): Boolean {
        var mills = mills
        if (!isStarted) return false
        val finished: Boolean
        return if (type == TypeNormalizedTimer.ONE_TIME) {
            // se <0 lo impostiamo a 0
            if (mills < 0) {
                mills = 0
            }
            if (mills + enlapsedTime >= durationTime) {
                // ASSERT: sforato tempo massimo
                enlapsedTime = durationTime
                stop()
                true
            } else {
                // siamo ancora dentro
                enlapsedTime = (enlapsedTime + mills) % durationTime
                false
            }
        } else {
            enlapsedTime = mills + enlapsedTime

            // se negativo tipo -0.1, lo facciamo rientrare comunque nel range 0 .. 1
            if (enlapsedTime < 0) {
                enlapsedTime = durationTime + enlapsedTime
            }

            // se abbiamo sforato il tempo massimo, allora abbiamo restituiamo
            // true nell'update
            finished = if (enlapsedTime >= durationTime) true else false
            enlapsedTime = enlapsedTime % durationTime
            finished
        }
    }

    /**
     * Esprime il tempo sottoforma di float che va da 0 a 1.
     *
     * @return
     */
    val normalizedEnlapsedTime: Float
        get() = 1.0f * enlapsedTime / durationTime

    /**
     * Imposta il touchTimer
     *
     * @param enlapsedTimeValue
     * tempo trascorso
     * @param durationTimeValue
     * durata del touchTimer
     * @param typeValue
     */
    operator fun set(enlapsedTimeValue: Long, durationTimeValue: Long, typeValue: TypeNormalizedTimer) {
        enlapsedTime = enlapsedTimeValue
        durationTime = durationTimeValue
        type = typeValue
    }

    var isStarted = false
        protected set

    /**
     * usato per gli update automatici.
     */
    protected var currentClock: Long = 0
    fun start() {
        isStarted = true
        reset()
    }

    fun stop() {
        isStarted = false
    }

    fun copy(): NormalizedTimer {
        val value = NormalizedTimer(type)
        value.enlapsedTime = enlapsedTime
        value.durationTime = durationTime
        return value
    }

    fun copyInto(destination: NormalizedTimer) {
        destination.type = type
        destination.enlapsedTime = enlapsedTime
        destination.durationTime = durationTime
    }

    companion object {
        /**
         *
         *
         * Fattore moltiplicativo: moltiplica i secondi in millisecondi.
         *
         */
        const val SECOND_IN_MILLISECONDS: Long = 1000

        /**
         *
         *
         * Fattore moltiplicativo: moltiplica i minuti in millisecondi.
         *
         */
        const val MINUTE_IN_MILLISECONDS = SECOND_IN_MILLISECONDS * 60

        /**
         *
         *
         * Fattore moltiplicativo: moltiplica le ore in millisecondi.
         *
         */
        const val HOUR_IN_MILLISECONDS = MINUTE_IN_MILLISECONDS * 60
    }
}