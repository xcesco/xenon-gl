package com.abubusoft.xenon.misc

import android.os.SystemClock
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.XenonLogger.verbose

/**
 * Limitatore di FPS. Impostato un limite superiore ai FPS questa classe
 * provvede a non far andar oltre i frame al secondo.
 *
 * Unico parametro definibile in Options. è [.maxFrameRate] che definisce
 * il numero di fps max.
 *
 * Se impostato a 0 indica che non vi è limite.
 *
 * @author Francesco Benincasa
 */
object FPSLimiter {
    /**
     * massimo framerate accettato
     */
    var maxFrameRate = 0

    /**
     * begin frame draw time
     */
    private var beginFrameDrawTime: Long = 0

    /**
     * end frame draw time
     */
    private var endFrameDrawTime: Long = 0

    /**
     * tempo per renderizzare questo frame
     */
    private var currentFrameRenderTime: Long = 0

    /**
     * routine per l'avvio del frame draw
     *
     * @param now
     */
    fun onDrawFrameBegin(now: Long) {
        if (maxFrameRate > 0) {
            // lo calcoliamo sempre, se abilitato
            beginFrameDrawTime = now
        }
    }

    /**
     * variabile per misurazione tempo di attesa
     */
    private var startSleepTime: Long = 0

    /**
     * variabile per misurazione tempo di attesa
     */
    private var endSleepTime: Long = 0

    /**
     * tempo di wait del thread dovuto al fatto che rispetto al framerate
     * desiderato, si è avuto un tempo di render minore.
     */
    private var currentFrameWaitTime: Long = 0

    /**
     * routine per il limitatore di FPS in fase di chiusura del frame draw
     */
    fun onDrawFrameEnd() {
        if (maxFrameRate > 0) {
            endFrameDrawTime = SystemClock.elapsedRealtime()

            // tempo per render al frame rate desiderato - tempo impiegato per
            // disegnare questo frame
            currentFrameRenderTime = endFrameDrawTime - beginFrameDrawTime

            // se per qualche motivo delta < lo rendiamo comunque positivo
            if (currentFrameRenderTime <= 0) currentFrameRenderTime = 1
            currentFrameWaitTime = (1000.0f / maxFrameRate - currentFrameRenderTime).toLong()

            // togliamo 10ms dall'attesa, considerando che lo sleep non è
            // precissimo
            currentFrameWaitTime = (currentFrameWaitTime * 0.80f).toLong()
            if (currentFrameWaitTime > 0) {
                try {
                    // per evitare che venga visualizzato ad ogni frame, viene
                    // visualizzato una volta ogni
                    // tanto
                    if (FPSCounter.isTimeToShowInfo()) {
                        //if (Logger.isEnabledFor(LoggerLevelType.VERBOSE)) {
                        Logger.verbose("Time enlapsed %s ms, Time to respect %s sleep for %s", currentFrameRenderTime, (1000.0 / maxFrameRate).toLong(), currentFrameWaitTime)
                        //}
                        startSleepTime = SystemClock.elapsedRealtime()
                    }
                    Thread.sleep(currentFrameWaitTime)
                    // per evitare che venga visualizzato ad ogni frame, viene
                    // visualizzato una volta ogni
                    // tanto
                    if (FPSCounter.isTimeToShowInfo()) {
                        endSleepTime = SystemClock.elapsedRealtime()
                        verbose("Wait time - Desidered: %s ms , Real: %s ms", currentFrameWaitTime, endSleepTime - startSleepTime)
                    }
                } catch (e: Exception) {
                    Logger.error("Errore: " + e.message)
                }
            }
        }
    }
}