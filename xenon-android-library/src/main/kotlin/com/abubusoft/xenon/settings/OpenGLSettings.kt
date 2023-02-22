package com.abubusoft.xenon.settings

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.core.Uncryptable

/**
 * Configurazione dell'opengl.
 *
 * @author Francesco Benincasa
 */
@Uncryptable
@BindType
class OpenGLSettings {
    /**
     * versione di opengl da usare
     */
    @Bind("openGLVersion")
    var version = 2

    /**
     * livello di debug
     */
    @Bind("openGLDebug")
    var debug = false

    /**
     * FPS massimi. Se impostato a 0 vuol dire che non ci sono limiti
     */
    @Bind("openGLMaxFPS")
    var maxFPS = 0

    /**
     * indica se gestire il draw frame dentro un enorme try catch, in modo da
     * non far esplodere l'applicazione in caso di eccezione.
     */
    @Bind("openGLSafeMode")
    var safeMode = true

    /**
     *
     * Indica il sistema che deve prevedere il caricamento async delle texture e degli shader.
     */
    @Bind("openGLAsyncMode")
    var asyncMode = true
}