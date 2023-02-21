package com.abubusoft.xenon.audio.exception

/**
 * (c) Zynga 2011
 *
 * @author Nicolas Gramlich <ngramlich></ngramlich>@zynga.com>
 * @since 20:37:53 - 09.11.2011
 */
open class SoundException : AudioException {
    // ===========================================================
    // Fields
    // ===========================================================
    // ===========================================================
    // Constructors
    // ===========================================================
    constructor(pMessage: String?) : super(pMessage) {}
    constructor(pThrowable: Throwable?) : super(pThrowable) {}
    constructor(pMessage: String?, pThrowable: Throwable?) : super(pMessage, pThrowable) {} // ===========================================================

    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    companion object {
        // ===========================================================
        // Constants
        // ===========================================================
        private const val serialVersionUID = 2647561236520151571L
    }
}