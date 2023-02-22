package com.abubusoft.xenon.settings

/**
 *
 * Modo in cui deve funzionare l'applicazione.
 * @author Francesco Benincasa
 */
enum class ModeType {
    /**
     * applicazione standard
     */
    APP,

    /**
     * modalità opengl. Una sola activity operativa in opengl
     */
    OPENGL
}