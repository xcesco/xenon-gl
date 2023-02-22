package com.abubusoft.xenon.misc

import android.os.SystemClock
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.opengl.OnFPSUpdateListener

object FPSCounter {
    /**
     * tempo trascorso tra l'inizio del frame precedente e l'inizio del frame corrente.
     */
    var enlapsedTime: Long = 0
    private var lastFrameTime = SystemClock.elapsedRealtime()

    /**
     * numero di frame da contare prima di visualizzare le statistiche di rendering
     */
    const val FPS_LIMIT_TO_DISPLAY_INFO = 1000
    private var initialTimeForFpsAvg: Long = 0
    var averageFpsCount = 0

    /**
     * media del tempo impiegato per renderizzare un frame
     */
    var averageFpsTime = 0f

    /**
     * media dei FPS
     */
    var averageFps = 0f
    fun onDrawFrameBegin(now: Long) {
        // calcoliamo il deltaTime, il tempo trascorso dall'ultimo frame
        enlapsedTime = now - lastFrameTime

        // se è minore di 0, allora facciamo che non ci muoviamo
        if (enlapsedTime < 0) enlapsedTime = 0
        // ultima volta che è stato calcolato il frame: ora.
        lastFrameTime = now
        if (averageFpsCount == 0) {
            // lo calcoliamo solo una volta ogni tanto, all'inizio del conteggio
            // dei frame
            initialTimeForFpsAvg = now
        }
    }

    /**
     * Indica se è il tempo di visualizzare i fps medi
     *
     */
    val isTimeToShowInfo: Boolean
        get() = averageFpsCount >= FPS_LIMIT_TO_DISPLAY_INFO

    fun onDrawFrameEnd(listener: OnFPSUpdateListener?) {
        // se sono passati maxCount frame facciamo la media
        if (averageFpsCount >= FPS_LIMIT_TO_DISPLAY_INFO) {

            // tempo trascorso dal momento prima di disegnare il primo frame ad ora
            val delta = SystemClock.elapsedRealtime() - initialTimeForFpsAvg

            // calcoliamo media per disegnare un singolo frame
            averageFpsTime = delta.toFloat() / averageFpsCount

            // invertiamo averageTime per ottenere lo stesso valore in FPS
            averageFps = (1000.0 / averageFpsTime).toFloat()
            Logger.debug("Avg time for render a frame = %s ms. FPS = %s", averageFpsTime, averageFps)
            listener?.onFPSUpdate(averageFps.toDouble())
            averageFpsCount = 0
        } else {
            averageFpsCount++
        }
    }
}