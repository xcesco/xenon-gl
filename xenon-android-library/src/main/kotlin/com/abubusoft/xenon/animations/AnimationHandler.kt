/**
 *
 */
package com.abubusoft.xenon.animations

import com.abubusoft.xenon.animations.events.EventAnimationListener
import com.abubusoft.xenon.animations.events.EventFrameListener
import com.abubusoft.xenon.interpolations.Interpolation
import com.abubusoft.xenon.interpolations.InterpolationLinear

/**
 *
 * @author Francesco Benincasa
 * @param <K>
</K> */
abstract class AnimationHandler<K : KeyFrame> {
    /**
     *
     *
     * Restituisce il valore dell'animazione inserendo il frame corrente, quello successivo (che può anche essere nullo) ed il tempo trascorso da quando siamo sul frame corrente.
     *
     *
     *
     *
     * **Il valore deve essere consumato prima di invocare lo stesso metodo sulla stessa animazione. Questo è dovuto al fatto che si usa una variabile di appoggio che rende
     * il sistema non THREAD-SAFE.**
     *
     *
     * @param current
     * @param enlapsedTime
     * @param next
     * @return
     */
    protected abstract fun value(current: K, enlapsedTime: Long, next: K?): K
    protected abstract fun buildFrame(): K

    constructor(animation: Animation<K>) {
        set(animation)
    }

    constructor() {}

    protected lateinit var temp: K
    /**
     * @return the name
     */
    /**
     * @param name the name to set
     */
    /**
     * nome dell'animazione
     */
    var name: String? = null
    /**
     * Se `true indica
     *
     * @return
    ` */
    /**
     * @param loop the loop to set
     */
    /**
     * loop dell'animazione
     */
    var isLoop = false

    /**
     * Defines the direction/speed at which the AbstractAnimation is expected to be played. The absolute value of rate indicates the speed which the AbstractAnimation is to be
     * played, while the sign of rate indicates the direction. A positive value of rate indicates forward play, a negative value indicates backward play and 0.0 to stop a running
     * AbstractAnimation. Rate 1.0 is normal play, 2.0 is 2 time normal, -1.0 is backwards, etc... Inverting the rate of a running AbstractAnimation will cause the
     * AbstractAnimation to reverse direction in place and play back over the portion of the AbstractAnimation that has already elapsed.
     *
     * **DefaultValue: 1.0**
     */
    var rate = 1.0f
        protected set
    /**
     *
     *
     * Defines the number of cycles in this animation. The cycleCount may be INDEFINITE for animations that repeat indefinitely, but must otherwise be > 0. It is not possible to
     * change the cycleCount of a running AbstractAnimation. If the value of cycleCount is changed for a running AbstractAnimation, the animation has to be stopped and started
     * again to pick up the new value.
     *
     *
     * **DefaultValue: 1.0**
     */
    // public int cycleCount=1;
    /**
     *
     *
     * Defines whether this AbstractAnimation reverses direction on alternating cycles. If true, the AbstractAnimation will proceed forward on the first cycle, then reverses on the
     * second cycle, and so on. Otherwise, animation will loop such that each cycle proceeds forward from the start. It is not possible to change the autoReverse flag of a running
     * AbstractAnimation. If the value of autoReverse is changed for a running AbstractAnimation, the animation has to be stopped and started again to pick up the new value.
     *
     *
     * **DefaultValue: false**
     */
    // public boolean autoReverse=false;
    /**
     * Copia da input tutte le caratteristiche dell'animazione.
     *
     * @param input
     */
    fun copyFrom(input: Animation<K>) {
        // autoReverse = input.autoReverse;
        // cycleCount = input.cycleCount;
        isLoop = input.isLoop
        rate = input.rate
    }

    fun name(): String? {
        return name
    }

    /**
     * durata dell'animazione. Anche se è infinita, viene restituito il conto dei ms di durata dei vari frame (moltiplicati per il cycleCount).
     *
     * @return
     */
    var interpolation = DEFAULT_INTERPOLATOR

    /**
     * indice corrente del frame dell'animazione
     */
    var currentFrameIndex = 0

    /**
     * key frame corrente
     */
    lateinit var currentFrame: K

    /**
     * key frame successivo
     */
    var nextFrame: K? = null

    /**
     * tempo trascorso dall'inizio dell'animazione
     */
    var enlapsedTime: Long = 0

    /**
     * una volta terminata l'animazione, indica quanti ms sono avanzati rispetto al termine dell'animazione
     */
    var remaingTime: Long = 0

    /**
     * se true indica che l'animazione è stata avviata
     */
    var status = StatusType.STOPPED

    /**
     * The possible states for handler.
     */
    enum class StatusType {
        PAUSED,

        /** The running state.  */
        RUNNING,

        /** The stopped state.  */
        STOPPED
    }

    /**
     * variabile temporanea usata nel metodo [AnimationHandler.update]
     */
    var currentFrameDuration: Long = 0

    /**
     * listener per gli eventi legati all'animazione (che include anche i frame)
     */
    protected var animationListener: EventAnimationListener<K>? = null

    /**
     * listener per gli eventi legati esclusivamente ai frame
     */
    private var frameListener: EventFrameListener<K>? = null

    /**
     * Imposta l'event listener
     *
     * @param listener
     */
    fun setAnimationListener(listener: EventAnimationListener<K>?) {
        animationListener = listener
    }

    open fun setFrameListener(listener: EventFrameListener<K>?) {
        frameListener = listener
    }

    /**
     *
     *
     * Aggiorna lo stato dell'animazione agganciata all'animator.
     *
     *
     * @param animation
     * animazione corrente
     * @param enlapsedTimeValue
     * tempo trascorso dall'ultima volta che abbiamo fatto refresh
     * @param channel
     * @return frame corrente
     */
    open fun update(enlapsedTimeValue: Long): K {
        if (status != StatusType.RUNNING) {
            return value(currentFrame, enlapsedTime, nextFrame)
        }
        enlapsedTime += enlapsedTimeValue
        currentFrameDuration = (currentFrame!!.duration * rate).toLong()
        while (enlapsedTime >= currentFrameDuration) {
            enlapsedTime -= currentFrameDuration
            if (enlapsedTimeValue >= 0) {
                if (currentFrameIndex + 1 >= animation!!.size()) {
                    if (!isLoop && cycleCount == 0) {
                        status = StatusType.STOPPED
                    }
                }
                if (frameListener != null) frameListener!!.onFrameEnd(currentFrame)
                if (status == StatusType.STOPPED) {
                    remaingTime = enlapsedTime
                    enlapsedTime = currentFrameDuration
                    if (animationListener != null) animationListener!!.onAnimationStop(currentFrame)
                    break
                }
            }
            currentFrameIndex++
            if (currentFrameIndex >= animation!!.size()) {
                if (!isLoop) {
                    if (cycleCount == 0) {
                        status = StatusType.STOPPED
                        currentFrameIndex = animation!!.size() - 1
                    } else {
                        cycleCount--
                        currentFrameIndex = 0
                    }
                } else {
                    currentFrameIndex = 0
                }
            }

            // impostiamo il frame corrente e quello successivo
            currentFrame = animation!!.getFrame(currentFrameIndex)
            nextFrame = if (currentFrameIndex < animation!!.size() - 1) animation!!.getFrame(currentFrameIndex + 1) else null
            if (frameListener != null) {
                frameListener!!.onFrameBegin(currentFrame)
            }
            currentFrameDuration = (animation!!.getFrame(currentFrameIndex)!!.duration * rate).toLong()
        }
        temp = value(currentFrame, enlapsedTime, nextFrame)
        return temp
    }

    /**
     *
     *
     * Rieseguiamo ancora una volta l'animazione. Se è in esecuzione, aggiungiamo un ciclo in più. Se è già fermo, viene rieseguito.
     *
     */
    open fun oneMoreTime() {
        when (status) {
            StatusType.RUNNING, StatusType.PAUSED -> cycleCount++
            StatusType.STOPPED -> play()
            else -> {}
        }
    }

    /**
     * animazione di riferimento
     */
    var animation: Animation<K>? = null

    /**
     * variabile da usare per selezionare il prossimo keyframe
     */
    protected var inc = 0
    /**
     * @return the cycleCount
     */
    /**
     * @param cycleCount the cycleCount to set
     */
    /**
     * contatore di cicli (da 0 a n). 0 indica che non ci sono cicli, viene fatto una volta sola e basta.
     */
    var cycleCount = 0

    /**
     * riporta tutto ai valori presi dall'animazione
     */
    open fun reset() {
        set(animation)
        cycleCount = 0
    }

    /**
     * Imposta l'animazione. Questo comporta il reset di tutte le variabili
     *
     * @param value
     */
    open fun set(value: Animation<K>) {
        animation = value
        copyFrom(animation!!)
        inc = 1 // animation.rate > 0 ? 1 : -1;
        status = StatusType.STOPPED
        currentFrameIndex = 0
        currentFrame = animation!!.getFrame(currentFrameIndex)
        nextFrame = if (currentFrameIndex < animation!!.size() - 1) animation!!.getFrame(currentFrameIndex + 1) else null
        enlapsedTime = 0
        cycleCount = 0

        // frame di appoggio per il calcolo del frame corrente
        temp = buildFrame()
        temp = value(currentFrame, enlapsedTime, nextFrame)
    }

    /**
     * Ricominciamo dall'inizio
     */
    open fun playFromStart() {
        status = StatusType.STOPPED
        play()
    }

    /**
     * Avvia l'animazione e ci posizioniamo sul frame corretto.
     *
     * @param handler
     * @param enlapsedTimeValue
     */
    open fun play() {
        if (animation == null) throw RuntimeException("No animation associated!")
        when (status) {
            StatusType.STOPPED -> {
                // ripartiamo da 0
                // set(animation);
                currentFrameIndex = 0
                currentFrame = animation!!.getFrame(currentFrameIndex)
                nextFrame = if (currentFrameIndex < animation!!.size() - 1) animation!!.getFrame(currentFrameIndex + 1) else null
                enlapsedTime = 0
                status = StatusType.RUNNING
                if (animationListener != null) animationListener!!.onAnimationStart()
                if (frameListener != null) frameListener!!.onFrameBegin(currentFrame)
            }
            StatusType.PAUSED -> {
                // continuiamo da dove eravamo
                status = StatusType.RUNNING
                if (animationListener != null) {
                    animationListener!!.onAnimationResume(currentFrame, enlapsedTime)
                }
            }
            else -> {}
        }
    }

    /**
     * blocca l'animazione
     */
    open fun stop() {
        status = StatusType.STOPPED
        if (animationListener != null) animationListener!!.onAnimationStop(currentFrame)
        enlapsedTime = 0
        currentFrameIndex = 0
    }

    val animationName: String?
        get() = animation.getName()

    /**
     * metto solo lo stato
     */
    open fun pause() {
        status = StatusType.PAUSED
        if (animationListener != null) animationListener!!.onAnimationPause(currentFrame, enlapsedTime)
    }// se non è in loop e non è iniziato, ovviamente è finito (o non ancora
    // iniziato)
    // se è in loop, non finirà mai, ma è iniziato stopped=true
    /**
     * Inidica se l'animator ha lanciato l'animazione corrente o no. Se è in loop infinito ed è stata lanciata, ovviamente non finirà mai.
     *
     * @return
     */
    open val isFinished: Boolean
        get() =// se non è in loop e non è iniziato, ovviamente è finito (o non ancora
        // iniziato)
            // se è in loop, non finirà mai, ma è iniziato stopped=true
            status == StatusType.STOPPED

    /**
     * indica se è in stato di running
     * @return
     */
    val isPlaying: Boolean
        get() = status == StatusType.RUNNING

    open fun duration(): Long {
        return animation!!.duration() * (cycleCount + 1)
    }

    /**
     * Recupera il valore corrente.
     *
     * @return
     */
    open fun value(): K {
        return temp
    }

    /**
     * Percentuale di avanzamento all'interno del frame
     *
     * @return
     */
    /*
	 * public float percentage() { return (1f*enlapsedTime)/currentFrameDuration; }
	 */
    companion object {
        protected val DEFAULT_INTERPOLATOR: Interpolation = InterpolationLinear.instance()
    }
}