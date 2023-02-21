package com.abubusoft.xenon.audio

import com.abubusoft.xenon.audio.exception.AudioException
import com.abubusoft.xenon.audio.exception.SoundReleasedException

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 16:35:37 - 13.06.2010
 */
abstract class BaseAudioEntity     // ===========================================================
// Constructors
// ===========================================================
    (  // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    private val audioManager: IAudioManager<out IAudioEntity>
) : IAudioEntity {
    protected var leftVolume = 1.0f
    protected var rightVolume = 1.0f

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    var isReleased = false
        private set

    @Throws(AudioException::class, SoundReleasedException::class)
    protected open fun getAudioManager(): IAudioManager<out IAudioEntity>? {
        assertNotReleased()
        return audioManager
    }

    @get:Throws(AudioException::class)
    val actualLeftVolume: Float
        get() {
            assertNotReleased()
            return leftVolume * masterVolume
        }

    @get:Throws(AudioException::class)
    val actualRightVolume: Float
        get() {
            assertNotReleased()
            return rightVolume * masterVolume
        }

    @get:Throws(AudioException::class)
    protected val masterVolume: Float
        protected get() {
            assertNotReleased()
            return audioManager.masterVolume
        }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Throws(AudioException::class, SoundReleasedException::class)
    protected abstract fun throwOnReleased()
    @Throws(AudioException::class)
    override fun getVolume(): Float {
        assertNotReleased()
        return (leftVolume + rightVolume) * 0.5f
    }

    @Throws(AudioException::class)
    override fun getLeftVolume(): Float {
        assertNotReleased()
        return leftVolume
    }

    @Throws(AudioException::class)
    override fun getRightVolume(): Float {
        assertNotReleased()
        return rightVolume
    }

    @Throws(AudioException::class)
    override fun setVolume(pVolume: Float) {
        assertNotReleased()
        this.setVolume(pVolume, pVolume)
    }

    @Throws(AudioException::class)
    override fun setVolume(pLeftVolume: Float, pRightVolume: Float) {
        assertNotReleased()
        leftVolume = pLeftVolume
        rightVolume = pRightVolume
    }

    @Throws(AudioException::class)
    override fun onMasterVolumeChanged(pMasterVolume: Float) {
        assertNotReleased()
    }

    @Throws(AudioException::class)
    override fun play() {
        assertNotReleased()
    }

    @Throws(AudioException::class)
    override fun pause() {
        assertNotReleased()
    }

    @Throws(AudioException::class)
    override fun resume() {
        assertNotReleased()
    }

    @Throws(AudioException::class)
    override fun stop() {
        assertNotReleased()
    }

    @Throws(AudioException::class)
    override fun setLooping(pLooping: Boolean) {
        assertNotReleased()
    }

    @Throws(AudioException::class)
    override fun release() {
        assertNotReleased()
        isReleased = true
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Throws(AudioException::class)
    protected fun assertNotReleased() {
        if (isReleased) {
            throwOnReleased()
        }
    } // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}