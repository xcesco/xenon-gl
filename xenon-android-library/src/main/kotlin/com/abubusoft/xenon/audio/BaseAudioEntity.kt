package com.abubusoft.xenon.audio;

import com.abubusoft.xenon.audio.exception.AudioException;
import com.abubusoft.xenon.audio.exception.SoundReleasedException;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 16:35:37 - 13.06.2010
 */
public abstract class BaseAudioEntity implements IAudioEntity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final IAudioManager<? extends IAudioEntity> audioManager;

	protected float leftVolume = 1.0f;
	protected float rightVolume = 1.0f;

	private boolean released;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseAudioEntity(final IAudioManager<? extends IAudioEntity> pAudioManager) {
		this.audioManager = pAudioManager;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isReleased() {
		return this.released;
	}

	protected IAudioManager<? extends IAudioEntity> getAudioManager() throws AudioException, SoundReleasedException {
		this.assertNotReleased();

		return this.audioManager;
	}

	public float getActualLeftVolume() throws AudioException {
		this.assertNotReleased();

		return this.leftVolume * this.getMasterVolume();
	}

	public float getActualRightVolume() throws AudioException {
		this.assertNotReleased();

		return this.rightVolume * this.getMasterVolume();
	}

	protected float getMasterVolume() throws AudioException {
		this.assertNotReleased();

		return this.audioManager.getMasterVolume();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected abstract void throwOnReleased() throws AudioException, SoundReleasedException ;

	@Override
	public float getVolume() throws AudioException {
		this.assertNotReleased();

		return (this.leftVolume + this.rightVolume) * 0.5f;
	}

	@Override
	public float getLeftVolume() throws AudioException {
		this.assertNotReleased();

		return this.leftVolume;
	}

	@Override
	public float getRightVolume() throws AudioException {
		this.assertNotReleased();

		return this.rightVolume;
	}

	@Override
	public final void setVolume(final float pVolume) throws AudioException {
		this.assertNotReleased();

		this.setVolume(pVolume, pVolume);
	}

	@Override
	public void setVolume(final float pLeftVolume, final float pRightVolume) throws AudioException {
		this.assertNotReleased();

		this.leftVolume = pLeftVolume;
		this.rightVolume = pRightVolume;
	}

	@Override
	public void onMasterVolumeChanged(final float pMasterVolume) throws AudioException {
		this.assertNotReleased();
	}

	@Override
	public void play() throws AudioException {
		this.assertNotReleased();
	}

	@Override
	public void pause() throws AudioException {
		this.assertNotReleased();
	}

	@Override
	public void resume() throws AudioException {
		this.assertNotReleased();
	}

	@Override
	public void stop() throws AudioException {
		this.assertNotReleased();
	}

	@Override
	public void setLooping(final boolean pLooping) throws AudioException {
		this.assertNotReleased();
	}

	@Override
	public void release() throws AudioException {
		this.assertNotReleased();

		this.released = true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void assertNotReleased() throws AudioException {
		if(this.released) {
			this.throwOnReleased();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
