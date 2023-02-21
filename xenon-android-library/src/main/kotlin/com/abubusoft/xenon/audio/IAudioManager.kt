/**
 *
 */
package com.abubusoft.xenon.audio

import com.abubusoft.xenon.audio.exception.AudioException

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 15:02:06 - 13.06.2010
 */
interface IAudioManager<T : IAudioEntity?> {
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    @set:Throws(AudioException::class)
    var masterVolume: Float
    fun add(pAudioEntity: T)
    fun remove(pAudioEntity: T): Boolean

    @Throws(AudioException::class)
    fun releaseAll()
}