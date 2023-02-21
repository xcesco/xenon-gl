package com.abubusoft.xenon.audio.sound

import android.media.SoundPool
import com.abubusoft.xenon.audio.BaseAudioEntity
import com.abubusoft.xenon.audio.exception.AudioException
import com.abubusoft.xenon.audio.exception.SoundReleasedException

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 13:22:15 - 11.03.2010
 */
class Sound  // ===========================================================
// Constructors
// ===========================================================
internal constructor(
    pSoundManager: SoundManager?, // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    var soundID: Int
) : BaseAudioEntity(pSoundManager) {
    var streamID = 0
        private set
    var isLoaded = false
    private var mLoopCount = 0
    private var mRate = 1.0f

    @Throws(AudioException::class, SoundReleasedException::class)
    fun setLoopCount(pLoopCount: Int) {
        assertNotReleased()
        mLoopCount = pLoopCount
        if (streamID != 0) {
            soundPool!!.setLoop(streamID, pLoopCount)
        }
    }

    @set:Throws(SoundReleasedException::class, AudioException::class)
    var rate: Float
        get() = mRate
        set(pRate) {
            assertNotReleased()
            mRate = pRate
            if (streamID != 0) {
                soundPool!!.setRate(streamID, pRate)
            }
        }

    @get:Throws(AudioException::class)
    private val soundPool: SoundPool?
        private get() = audioManager.soundPool

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Throws(AudioException::class)
    override fun getAudioManager(): SoundManager {
        return super.getAudioManager() as SoundManager
    }

    @Throws(SoundReleasedException::class)
    override fun throwOnReleased() {
        throw SoundReleasedException("throwOnReleased")
    }

    @Throws(AudioException::class)
    override fun play() {
        super.play()
        val masterVolume = this.masterVolume
        val leftVolume = leftVolume * masterVolume
        val rightVolume = rightVolume * masterVolume
        streamID = soundPool!!.play(soundID, leftVolume, rightVolume, 1, mLoopCount, mRate)
    }

    @Throws(AudioException::class)
    override fun stop() {
        super.stop()
        if (streamID != 0) {
            soundPool!!.stop(streamID)
        }
    }

    @Throws(AudioException::class)
    override fun resume() {
        super.resume()
        if (streamID != 0) {
            soundPool!!.resume(streamID)
        }
    }

    @Throws(AudioException::class)
    override fun pause() {
        super.pause()
        if (streamID != 0) {
            soundPool!!.pause(streamID)
        }
    }

    @Throws(AudioException::class)
    override fun release() {
        assertNotReleased()
        soundPool!!.unload(soundID)
        soundID = 0
        isLoaded = false
        audioManager.remove(this)
        super.release()
    }

    @Throws(AudioException::class)
    override fun setLooping(pLooping: Boolean) {
        super.setLooping(pLooping)
        setLoopCount(if (pLooping) -1 else 0)
    }

    @Throws(AudioException::class)
    override fun setVolume(pLeftVolume: Float, pRightVolume: Float) {
        super.setVolume(pLeftVolume, pRightVolume)
        if (streamID != 0) {
            val masterVolume = this.masterVolume
            val leftVolume = leftVolume * masterVolume
            val rightVolume = rightVolume * masterVolume
            soundPool!!.setVolume(streamID, leftVolume, rightVolume)
        }
    }

    @Throws(AudioException::class)
    override fun onMasterVolumeChanged(pMasterVolume: Float) {
        this.setVolume(leftVolume, rightVolume)
    } // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}