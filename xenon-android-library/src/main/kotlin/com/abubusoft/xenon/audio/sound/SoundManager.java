package com.abubusoft.xenon.audio.sound;

import com.abubusoft.xenon.audio.BaseAudioManager;
import com.abubusoft.xenon.audio.exception.AudioException;
import com.abubusoft.xenon.audio.exception.SoundException;
import com.abubusoft.kripton.android.Logger;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.SparseArray;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 13:22:59 - 11.03.2010
 */
public class SoundManager extends BaseAudioManager<Sound> implements OnLoadCompleteListener {

	private static final SoundManager instance = new SoundManager();

	public static final SoundManager instance() {
		return instance;
	}

	// ===========================================================
	// Constants
	// ===========================================================

	private static final int SOUND_STATUS_OK = 0;

	public static final int MAX_SIMULTANEOUS_STREAMS_DEFAULT = 5;

	// ===========================================================
	// Fields
	// ===========================================================

	private final SoundPool mSoundPool;
	private final SparseArray<Sound> mSoundMap = new SparseArray<Sound>();

	// ===========================================================
	// Constructors
	// ===========================================================

	public SoundManager() {
		this(MAX_SIMULTANEOUS_STREAMS_DEFAULT);
	}

	public SoundManager(final int pMaxSimultaneousStreams) {
		this.mSoundPool = new SoundPool(pMaxSimultaneousStreams, AudioManager.STREAM_MUSIC, 0);
		this.mSoundPool.setOnLoadCompleteListener(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	SoundPool getSoundPool() {
		return this.mSoundPool;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void add(final Sound pSound) {
		super.add(pSound);

		this.mSoundMap.put(pSound.getSoundID(), pSound);
	}

	@Override
	public boolean remove(final Sound pSound) {
		final boolean removed = super.remove(pSound);
		if (removed) {
			this.mSoundMap.remove(pSound.getSoundID());
		}
		return removed;

	}

	@Override
	public void releaseAll() throws AudioException {
		super.releaseAll();

		this.mSoundPool.release();
	}

	@Override
	public synchronized void onLoadComplete(final SoundPool pSoundPool, final int pSoundID, final int pStatus) {
		if (pStatus == SoundManager.SOUND_STATUS_OK) {
			final Sound sound = this.mSoundMap.get(pSoundID);
			if (sound == null) {
				try {
					throw new SoundException("Unexpected soundID: '" + pSoundID + "'.");
				} catch (SoundException e) {
					Logger.error(e.getMessage());
					e.printStackTrace();
				}
			} else {
				sound.setLoaded(true);
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
