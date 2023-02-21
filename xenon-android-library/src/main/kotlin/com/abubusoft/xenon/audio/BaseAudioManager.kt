package com.abubusoft.xenon.audio

import com.abubusoft.xenon.audio.exception.AudioException

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 18:07:02 - 13.06.2010
 */
abstract class BaseAudioManager<T : IAudioEntity?> : IAudioManager<T> {
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    protected val mAudioEntities = ArrayList<T>()
    protected var mMasterVolume = 1.0f

    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    override fun getMasterVolume(): Float {
        return mMasterVolume
    }

    @Throws(AudioException::class)
    override fun setMasterVolume(pMasterVolume: Float) {
        mMasterVolume = pMasterVolume
        val audioEntities = mAudioEntities
        for (i in audioEntities.indices.reversed()) {
            val audioEntity = audioEntities[i]
            audioEntity!!.onMasterVolumeChanged(pMasterVolume)
        }
    }

    override fun add(pAudioEntity: T) {
        mAudioEntities.add(pAudioEntity)
    }

    override fun remove(pAudioEntity: T): Boolean {
        return mAudioEntities.remove(pAudioEntity)
    }

    @Throws(AudioException::class)
    override fun releaseAll() {
        val audioEntities = mAudioEntities
        for (i in audioEntities.indices.reversed()) {
            val audioEntity = audioEntities[i]
            audioEntity!!.stop()
            audioEntity.release()
        }
    } // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}