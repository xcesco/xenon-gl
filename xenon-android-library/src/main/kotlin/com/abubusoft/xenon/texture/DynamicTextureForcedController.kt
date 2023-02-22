package com.abubusoft.xenon.texture

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.texture.DynamicTexture.DynamicTextureController

/**
 *
 *
 * Questa implementazione del controller consente di controllare il
 * caricamento delle texture mediante un click di un pulsante o programmaticamente.
 *
 *
 * @author Francesco Benincasa
 */
class DynamicTextureForcedController
/**
 *
 * Costruttore
 */
    : DynamicTextureController {
    /**
     * flag usato per indicare il fatto che il load della texture è pronto.
     */
    var readyToLoad = true
    var forced = false
    override fun onCheckForUpdate(enlapsedTime: Long): Boolean {
        // aggiorniamo il touchTimer
        return if (forced && readyToLoad) {
            Logger.info("Ready for load another texture")
            readyToLoad = false
            forced = false
            true
        } else {
            false
        }
    }

    override fun onTextureReady(texture: Texture?) {
        // eseguito quando viene la texture viene caricata.
        Logger.info("Texture loaded")
        readyToLoad = true
    }

    override fun forceUpdate(): Boolean {
        return if (readyToLoad) {
            Logger.info("Just ask to load another texture")
            forced = true
            true
        } else {
            Logger.info("It's still loading another texture")
            false
        }
    }
}