package com.abubusoft.xenon.audio;

import com.abubusoft.xenon.audio.exception.AudioException;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:53:29 - 13.06.2010
 */
public interface IAudioEntity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void play() throws AudioException;
	public void pause() throws AudioException;
	public void resume() throws AudioException;
	public void stop() throws AudioException;

	public float getVolume() throws AudioException;
	public void setVolume(final float pVolume) throws AudioException;

	public float getLeftVolume() throws AudioException;
	public float getRightVolume() throws AudioException;
	public void setVolume(final float pLeftVolume, final float pRightVolume) throws AudioException;

	public void onMasterVolumeChanged(final float pMasterVolume) throws AudioException;

	public void setLooping(final boolean pLooping) throws AudioException;

	public void release() throws AudioException;
}
