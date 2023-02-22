package com.abubusoft.xenon.mesh.tiledmaps

/**
 * Rappresenta una sequenza di layer.
 *
 * @author Francesco Benincasa
 */
class TileAnimation(
    /**
     * nome dell'animazione
     */
    val name: String
) {
    var currentFrameIndex = 0

    /**
     * tempo trascorso dall'inizio dell'animazione
     *
     */
    var enlapsedTime: Long = 0

    /**
     * sequenza di frame
     */
    val frames: ArrayList<TileAnimationFrame>
    var loop = true

    /**
     * se true indica che l'animazione è stata avviata
     */
    var started = false

    init {
        frames = ArrayList()
    }// se non è in loop e non è iniziato, ovviamente è finito (o non ancora
    // iniziato)
    // se è in loop, non finirà mai, ma è iniziato stopped=true
    /**
     * Inidica se l'animator ha lanciato l'animazione corrente o no. Se è in
     * loop infinito ed è stata lanciata, ovviamente non finirà mai.
     *
     * @return
     */
    val isFinished: Boolean
        get() =// se non è in loop e non è iniziato, ovviamente è finito (o non ancora
        // iniziato)
            // se è in loop, non finirà mai, ma è iniziato stopped=true
            !started

    /**
     * Verifica se il layer passato come argomento deve essere disegnato o meno
     * in base allo stato dell'animazione
     *
     * @param layer
     * @return
     */
    fun isLayerToDraw(layer: Layer): Boolean {
        return frames[currentFrameIndex].layer == layer
    }

    /**
     * Avvia l'animazione e ci posizioniamo sul frame corretto.
     *
     * @param status
     * @param enlapsedTimeValue
     */
    fun start(enlapsedTimeValue: Long) {
        started = true
        enlapsedTime = enlapsedTimeValue
        currentFrameIndex = 0

        // ci posizioniamo sul frame corretto
        while (enlapsedTime > frames[currentFrameIndex].duration) {
            enlapsedTime -= frames[currentFrameIndex].duration
            currentFrameIndex++
            if (currentFrameIndex >= frames.size) {
                if (!loop) {
                    started = false
                }
                currentFrameIndex = 0
            }
        }
    }

    fun update(enlapsedTimeValue: Long) {
        if (!started) return
        enlapsedTime += enlapsedTimeValue
        while (enlapsedTime > frames[currentFrameIndex].duration) {
            enlapsedTime -= frames[currentFrameIndex].duration
            currentFrameIndex++
            if (currentFrameIndex >= frames.size) {
                if (!loop) {
                    started = false
                }
                currentFrameIndex = 0
            }
        }
    }
}