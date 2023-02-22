package com.abubusoft.xenon.texture;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;

/**
 * Opzioni per la creazione di una texture esterna.
 * 
 * @author xcesco
 *
 */
public class ExternalTextureOptions {
	/**
	 * nome della texture
	 */
	public String name = "[ExternalTexture noname]";

	/**
	 * associato alla camera
	 */
	public SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener;

	/**
	 * media player
	 */
	public MediaPlayer mediaPlayer;

	/**
	 * dimensioni finali della texture
	 */
	public TextureSizeType textureSize;

	/**
	 * Rapporto tra width e height della texture da considerare come valido.
	 * Permette di selezionare quanta parte della texture considerare come
	 * buona.
	 */
	public double aspectRatio;

	/**
	 * Fluent interface per textureSize.
	 * 
	 * @param textureSizeValue
	 * @return
	 */
	public ExternalTextureOptions size(TextureSizeType value) {
		textureSize = value;
		return this;
	}

	/**
	 * Fluent interface per aspectRatio.
	 * 
	 * @param aspectRatioValue
	 * @return
	 */
	public ExternalTextureOptions aspectRatio(TextureAspectRatioType value) {
		aspectRatio = value.aspectXY;
		return this;
	}

	/**
	 * Fluent interface per aspectRatio.
	 * 
	 * @param aspectRatioValue
	 * @return this
	 */
	public ExternalTextureOptions aspectRatio(double value) {
		aspectRatio = value;
		return this;
	}

	public static ExternalTextureOptions build() {
		return (new ExternalTextureOptions()).size(TextureSizeType.SIZE_UNBOUND).aspectRatio(TextureAspectRatioType.RATIO1_1);
	}

	/**
	 * <p>
	 * Crea una copia della configurazione
	 * </p>
	 * 
	 * @return
	 */
	public ExternalTextureOptions copy(ExternalTextureOptions src) {
		ExternalTextureOptions nuovo = build();

		nuovo.aspectRatio = src.aspectRatio;
		nuovo.textureSize = src.textureSize;
		nuovo.mediaPlayer = src.mediaPlayer;
		nuovo.onFrameAvailableListener = src.onFrameAvailableListener;

		return nuovo;
	}

	/**
	 * Fluent interface per name
	 * 
	 * @param value
	 * @return
	 */
	public ExternalTextureOptions name(String value) {
		name = value;
		return this;
	}

	public ExternalTextureOptions mediaPlayer(MediaPlayer value) {
		mediaPlayer = value;
		return this;
	}

	public ExternalTextureOptions onFrameAvailableListener(OnFrameAvailableListener value) {
		onFrameAvailableListener = value;
		return this;
	}

	public TextureOptions toTextureOptions() {
		TextureOptions options = TextureOptions.build();

		options.aspectRatio = this.aspectRatio;
		options.name = this.name;
		options.textureSize = this.textureSize;

		return options;
	}
}
