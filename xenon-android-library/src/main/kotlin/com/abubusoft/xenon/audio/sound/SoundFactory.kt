package com.abubusoft.xenon.audio.sound

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import java.io.File
import java.io.FileDescriptor
import java.io.IOException

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 14:23:03 - 11.03.2010
 */
object SoundFactory {
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    private var sAssetBasePath = ""
    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    /**
     * @param pAssetBasePath
     * must end with '`/`' or have `.length() == 0`.
     */
    var assetBasePath: String
        get() = sAssetBasePath
        set(pAssetBasePath) {
            if (pAssetBasePath.endsWith("/") || pAssetBasePath.length == 0) {
                sAssetBasePath = pAssetBasePath
            } else {
                throw IllegalStateException("pAssetBasePath must end with '/' or be lenght zero.")
            }
        }

    fun onCreate() {
        assetBasePath = ""
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    fun createSoundFromPath(pPath: String?): Sound {
        val pSoundManager: SoundManager = SoundManager.Companion.instance()
        val soundID = pSoundManager.soundPool.load(pPath, 1)
        val sound = Sound(pSoundManager, soundID)
        pSoundManager.add(sound)
        return sound
    }

    fun createSoundFromAsset(pContext: Context, pAssetPath: String): Sound {
        val pSoundManager: SoundManager = SoundManager.Companion.instance()
        val soundID: Int
        soundID = try {
            pSoundManager.soundPool.load(pContext.assets.openFd(sAssetBasePath + pAssetPath), 1)
        } catch (e: IOException) {
            Logger.error(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
        val sound = Sound(pSoundManager, soundID)
        pSoundManager.add(sound)
        return sound
    }

    fun createSoundFromResource(pContext: Context?, pSoundResID: Int): Sound {
        val pSoundManager: SoundManager = SoundManager.Companion.instance()
        val soundID = pSoundManager.soundPool.load(pContext, pSoundResID, 1)
        val sound = Sound(pSoundManager, soundID)
        pSoundManager.add(sound)
        return sound
    }

    fun createSoundFromFile(pFile: File): Sound {
        return createSoundFromPath(pFile.absolutePath)
    }

    fun createSoundFromAssetFileDescriptor(pAssetFileDescriptor: AssetFileDescriptor?): Sound {
        val pSoundManager: SoundManager = SoundManager.Companion.instance()
        val soundID = pSoundManager.soundPool.load(pAssetFileDescriptor, 1)
        val sound = Sound(pSoundManager, soundID)
        pSoundManager.add(sound)
        return sound
    }

    @Throws(IOException::class)
    fun createSoundFromFileDescriptor(pFileDescriptor: FileDescriptor?, pOffset: Long, pLength: Long): Sound {
        val pSoundManager: SoundManager = SoundManager.Companion.instance()
        val soundID = pSoundManager.soundPool.load(pFileDescriptor, pOffset, pLength, 1)
        val sound = Sound(pSoundManager, soundID)
        pSoundManager.add(sound)
        return sound
    } // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}