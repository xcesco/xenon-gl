package com.abubusoft.xenon.audio.sound

import android.media.AudioManager
import android.media.SoundPool
import android.util.SparseArray
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.audio.BaseAudioManager
import com.abubusoft.xenon.audio.exception.AudioException
import com.abubusoft.xenon.audio.exception.SoundException

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 13:22:59 - 11.03.2010
 */
class SoundManager @JvmOverloads constructor(pMaxSimultaneousStreams: Int = MAX_SIMULTANEOUS_STREAMS_DEFAULT) : BaseAudioManager<Sound?>(), SoundPool.OnLoadCompleteListener {
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    val soundPool: SoundPool
    private val mSoundMap = SparseArray<Sound>()

    // ===========================================================
    // Constructors
    // ===========================================================
    init {
        soundPool = SoundPool(pMaxSimultaneousStreams, AudioManager.STREAM_MUSIC, 0)
        soundPool.setOnLoadCompleteListener(this)
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    override fun add(pSound: Sound) {
        super.add(pSound)
        mSoundMap.put(pSound.soundID, pSound)
    }

    override fun remove(pSound: Sound): Boolean {
        val removed = super.remove(pSound)
        if (removed) {
            mSoundMap.remove(pSound.soundID)
        }
        return removed
    }

    @Throws(AudioException::class)
    override fun releaseAll() {
        super.releaseAll()
        soundPool.release()
    }

    @Synchronized
    override fun onLoadComplete(pSoundPool: SoundPool, pSoundID: Int, pStatus: Int) {
        if (pStatus == SOUND_STATUS_OK) {
            val sound = mSoundMap[pSoundID]
            if (sound == null) {
                try {
                    throw SoundException("Unexpected soundID: '$pSoundID'.")
                } catch (e: SoundException) {
                    Logger.error(e.message)
                    e.printStackTrace()
                }
            } else {
                sound.isLoaded = true
            }
        }
    } // ===========================================================

    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    companion object {
        private val instance = SoundManager()
        fun instance(): SoundManager {
            return instance
        }

        // ===========================================================
        // Constants
        // ===========================================================
        private const val SOUND_STATUS_OK = 0
        const val MAX_SIMULTANEOUS_STREAMS_DEFAULT = 5
    }
}