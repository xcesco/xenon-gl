package com.abubusoft.xenon.texture

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.misc.NormalizedTimer
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer
import com.abubusoft.xenon.texture.DynamicTexture.DynamicTextureController

/**
 *
 *
 * Questa implementazione del controller consente di controllare il
 * caricamento delle texture mediante un touchTimer.
 *
 *
 *
 *
 * Questo meccanismo è quello che penso sia quello più standard, per questo
 * motivo è stato implemetato.
 *
 *
 * @author Francesco Benincasa
 */
class DynamicTextureTimerController(durationInMills: Long) : DynamicTextureController {
    /**
     * touchTimer usato per monitorare il tempo.
     */
    var timer: NormalizedTimer

    /**
     * flag usato per indicare il fatto che il load della texture è pronto.
     */
    var readyToLoad: Boolean

    /**
     *
     * Costruttore
     *
     * @param durationInMills
     */
    init {
        timer = NormalizedTimer(TypeNormalizedTimer.ONE_TIME, durationInMills)
        readyToLoad = true
        timer.start()
    }

    override fun onCheckForUpdate(enlapsedTime: Long): Boolean {
        // aggiorniamo il touchTimer
        timer.update(enlapsedTime)
        return if (timer.normalizedEnlapsedTime == 1f && readyToLoad) {
            Logger.info("Ready for load another texture")
            readyToLoad = false
            true
        } else {
            false
        }
    }

    override fun onTextureReady(texture: Texture?) {
        // eseguito quando viene la texture viene caricata.
        Logger.info("Texture loaded")
        readyToLoad = true
        timer.start()
    }

    override fun forceUpdate(): Boolean {
        // non possiamo forzare
        return false
    }
}