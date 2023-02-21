package com.abubusoft.xenon.audio

import com.abubusoft.xenon.audio.exception.AudioException

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 14:53:29 - 13.06.2010
 */
interface IAudioEntity {
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    @Throws(AudioException::class)
    fun play()

    @Throws(AudioException::class)
    fun pause()

    @Throws(AudioException::class)
    fun resume()

    @Throws(AudioException::class)
    fun stop()

    @get:Throws(AudioException::class)
    @set:Throws(AudioException::class)
    var volume: Float

    @get:Throws(AudioException::class)
    val leftVolume: Float

    @get:Throws(AudioException::class)
    val rightVolume: Float

    @Throws(AudioException::class)
    fun setVolume(pLeftVolume: Float, pRightVolume: Float)

    @Throws(AudioException::class)
    fun onMasterVolumeChanged(pMasterVolume: Float)

    @Throws(AudioException::class)
    fun setLooping(pLooping: Boolean)

    @Throws(AudioException::class)
    fun release()
}