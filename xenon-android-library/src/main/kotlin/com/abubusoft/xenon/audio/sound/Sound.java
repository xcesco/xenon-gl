package com.abubusoft.xenon.audio.sound;


import com.abubusoft.xenon.audio.BaseAudioEntity;
import com.abubusoft.xenon.audio.exception.AudioException;
import com.abubusoft.xenon.audio.exception.SoundReleasedException;

import android.media.SoundPool;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 13:22:15 - 11.03.2010
 */
public class Sound extends BaseAudioEntity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private int mSoundID;
	private int mStreamID;

	private boolean mLoaded;

	private int mLoopCount;
	private float mRate = 1.0f;

	// ===========================================================
	// Constructors
	// ===========================================================

	Sound(final SoundManager pSoundManager, final int pSoundID) {
		super(pSoundManager);

		this.mSoundID = pSoundID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getSoundID() {
		return this.mSoundID;
	}

	public int getStreamID() {
		return this.mStreamID;
	}

	public boolean isLoaded() {
		return this.mLoaded;
	}

	public void setLoaded(final boolean pLoaded) {
		this.mLoaded = pLoaded;
	}

	public void setLoopCount(final int pLoopCount) throws AudioException, SoundReleasedException   {
		this.assertNotReleased();

		this.mLoopCount = pLoopCount;
		if(this.mStreamID != 0) {
			this.getSoundPool().setLoop(this.mStreamID, pLoopCount);
		}
	}

	public float getRate() {
		return this.mRate;
	}

	public void setRate(final float pRate) throws SoundReleasedException, AudioException {
		this.assertNotReleased();

		this.mRate = pRate;
		if(this.mStreamID != 0) {
			this.getSoundPool().setRate(this.mStreamID, pRate);
		}
	}

	private SoundPool getSoundPool() throws AudioException {
		return this.getAudioManager().getSoundPool();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected SoundManager getAudioManager() throws AudioException {
		return (SoundManager)super.getAudioManager();
	}

	@Override
	protected void throwOnReleased() throws SoundReleasedException {
		throw new SoundReleasedException("throwOnReleased");
	}

	@Override
	public void play() throws AudioException {
		super.play();

		final float masterVolume = this.getMasterVolume();
		final float leftVolume = this.leftVolume * masterVolume;
		final float rightVolume = this.rightVolume * masterVolume;

		this.mStreamID = this.getSoundPool().play(this.mSoundID, leftVolume, rightVolume, 1, this.mLoopCount, this.mRate);
	}

	@Override
	public void stop() throws AudioException {
		super.stop();

		if(this.mStreamID != 0) {
			this.getSoundPool().stop(this.mStreamID);
		}
	}

	@Override
	public void resume() throws AudioException {
		super.resume();

		if(this.mStreamID != 0) {
			this.getSoundPool().resume(this.mStreamID);
		}
	}

	@Override
	public void pause() throws AudioException {
		super.pause();

		if(this.mStreamID != 0) {
			this.getSoundPool().pause(this.mStreamID);
		}
	}

	@Override
	public void release() throws AudioException {
		this.assertNotReleased();

		this.getSoundPool().unload(this.mSoundID);
		this.mSoundID = 0;
		this.mLoaded = false;

		this.getAudioManager().remove(this);

		super.release();
	}

	@Override
	public void setLooping(final boolean pLooping) throws AudioException {
		super.setLooping(pLooping);

		this.setLoopCount((pLooping) ? -1 : 0);
	}

	@Override
	public void setVolume(final float pLeftVolume, final float pRightVolume) throws AudioException {
		super.setVolume(pLeftVolume, pRightVolume);

		if(this.mStreamID != 0){
			final float masterVolume = this.getMasterVolume();
			final float leftVolume = this.leftVolume * masterVolume;
			final float rightVolume = this.rightVolume * masterVolume;

			this.getSoundPool().setVolume(this.mStreamID, leftVolume, rightVolume);
		}
	}

	@Override
	public void onMasterVolumeChanged(final float pMasterVolume) throws AudioException {
		this.setVolume(leftVolume, rightVolume);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
