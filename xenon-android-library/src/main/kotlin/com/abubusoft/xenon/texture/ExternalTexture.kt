package com.abubusoft.xenon.texture;

import com.abubusoft.kripton.android.Logger;

import android.graphics.SurfaceTexture;

/**
 * <p>
 * Texture esterna.
 * </p>
 * 
 * <p>
 * When <target> is TEXTURE_EXTERNAL_OES only NEAREST and LINEAR are accepted as
 * TEXTURE_MIN_FILTER, only CLAMP_TO_EDGE is accepted as TEXTURE_WRAP_S and
 * TEXTURE_WRAP_T, and only FALSE is accepted as GENERATE_MIPMAP. Attempting to
 * set other values for TEXTURE_MIN_FILTER, TEXTURE_WRAP_S, TEXTURE_WRAP_T, or
 * GENERATE_MIPMAP will result in an INVALID_ENUM error.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class ExternalTexture extends Texture {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.texture.Texture#unbind()
	 */
	@Override
	protected void unbind() {
		super.unbind();

		Logger.debug("unbind ExternalTexture %s (%s)", name, bindingId);

		if (options.mediaPlayer != null && options.mediaPlayer.isPlaying()) {
			// curretPosition=options.mediaPlayer.getCurrentPosition();
			options.mediaPlayer.pause();
			// options.mediaPlayer.st
			// options.mediaPlayer.release();
			Logger.debug("-- media player associated paused and released");
		}

		//mMediaPlayer.stop();
		//mMediaPlayer.release();
		if (surface!=null)
		{
			surface.release();
			surface=null;
		}
		Logger.debug("-- surface detached and released");
	}

	/**
	 * opzioni dell'external texture
	 */
	public final ExternalTextureOptions options;

	public ExternalTexture(String nameValue, int bindingIdValue, ExternalTextureOptions optionsValue) {
		super(nameValue, bindingIdValue);

		options = optionsValue;

	}

	@Override
	protected void reload() {
		super.reload();

		updateInfo(ExternalTextureBinder.bindTexture(this, bindingId, options.toTextureOptions(), TextureReplaceOptions.build()));
	}

	/**
	 * Superficie associata alla texture esterna.
	 */
	public SurfaceTexture surface;

	public void update() {

		if (surface != null)
			surface.updateTexImage();
	}

}
