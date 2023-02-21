package com.abubusoft.xenon.audio.sound;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:23:03 - 11.03.2010
 */
public class SoundFactory {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static String sAssetBasePath = "";

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * @param pAssetBasePath
	 *            must end with '<code>/</code>' or have <code>.length() == 0</code>.
	 */
	public static void setAssetBasePath(final String pAssetBasePath) {
		if (pAssetBasePath.endsWith("/") || pAssetBasePath.length() == 0) {
			SoundFactory.sAssetBasePath = pAssetBasePath;
		} else {
			throw new IllegalStateException("pAssetBasePath must end with '/' or be lenght zero.");
		}
	}

	public static String getAssetBasePath() {
		return SoundFactory.sAssetBasePath;
	}

	public static void onCreate() {
		SoundFactory.setAssetBasePath("");
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static Sound createSoundFromPath(final String pPath) {
		SoundManager pSoundManager = SoundManager.instance();
		final int soundID = pSoundManager.getSoundPool().load(pPath, 1);
		final Sound sound = new Sound(pSoundManager, soundID);
		pSoundManager.add(sound);
		return sound;

	}

	public static Sound createSoundFromAsset(final Context pContext, final String pAssetPath) {
		SoundManager pSoundManager = SoundManager.instance();
		int soundID;
		try {
			soundID = pSoundManager.getSoundPool().load(pContext.getAssets().openFd(SoundFactory.sAssetBasePath + pAssetPath), 1);
		} catch (IOException e) {
			Logger.error(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
		final Sound sound = new Sound(pSoundManager, soundID);
		pSoundManager.add(sound);
		return sound;
	}

	public static Sound createSoundFromResource(final Context pContext, final int pSoundResID) {
		SoundManager pSoundManager = SoundManager.instance();
		final int soundID = pSoundManager.getSoundPool().load(pContext, pSoundResID, 1);
		final Sound sound = new Sound(pSoundManager, soundID);
		pSoundManager.add(sound);
		return sound;
	}

	public static Sound createSoundFromFile(final File pFile) {
		return SoundFactory.createSoundFromPath(pFile.getAbsolutePath());
	}

	public static Sound createSoundFromAssetFileDescriptor(final AssetFileDescriptor pAssetFileDescriptor) {
		SoundManager pSoundManager = SoundManager.instance();
		final int soundID = pSoundManager.getSoundPool().load(pAssetFileDescriptor, 1);
		final Sound sound = new Sound(pSoundManager, soundID);
		pSoundManager.add(sound);
		return sound;
	}

	public static Sound createSoundFromFileDescriptor(final FileDescriptor pFileDescriptor, final long pOffset, final long pLength) throws IOException {
		SoundManager pSoundManager = SoundManager.instance();
		final int soundID = pSoundManager.getSoundPool().load(pFileDescriptor, pOffset, pLength, 1);
		final Sound sound = new Sound(pSoundManager, soundID);
		pSoundManager.add(sound);
		return sound;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
