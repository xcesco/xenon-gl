/**
 * 
 */
package com.abubusoft.xenon.texture;


/**
 * @author Francesco Benincasa
 * 
 */
public class TextureOptions {

	/**
	 * nome della texture
	 */
	public String name = "[undefined]";

	/**
	 * dimensioni finali della texture
	 */
	public TextureSizeType textureSize;

	/**
	 * tipo di filtro. Di default è quello da usare negli atlas, il NEAREST
	 */
	public TextureFilterType textureFilter;

	/**
	 * formato degli elementi che compongono la texture: unsigned byte o float
	 */
	public TextureInternalFormatType textureInternalFormat;

	/**
	 * Aspect ratio della texture, ovvero permette di selezionare quanta parte della texture considerare come buona.
	 */
	public double aspectRatio;

	/**
	 * trasformazione da applicare all'immagine prima di trasformarla in texture.
	 */
	public BitmapTransformation transformation;

	/**
	 * se true, abilita il salvataggio della texture su file temporaneo
	 */
	public boolean debugTextureOnFile;

	/**
	 * opacità 0 - 1f
	 */
	public float opacity;

	/**
	 * indica se ripetere la texture, sia in startX che in startY
	 */
	public TextureRepeatType textureRepeat;

	/**
	 * Fluent interface per textureRepeat. true/false. Wrappa TextureWrap.ClampToEdge, TextureWrap.ClampToEdge
	 * 
	 * @param textureRepeat
	 * @return
	 */
	public TextureOptions textureRepeat(TextureRepeatType value) {
		textureRepeat = value;
		return this;
	}

	/**
	 * Fluent interface per opacity. 0 - 1f
	 * 
	 * @param opacity
	 * @return
	 */
	public TextureOptions opacity(float value) {
		opacity = value;
		return this;
	}

	/**
	 * Fluent interface per textureSize.
	 * 
	 * @param textureSizeValue
	 * @return
	 * 		this
	 */
	public TextureOptions size(TextureSizeType value) {
		textureSize = value;
		return this;
	}

	/**
	 * Fluent interface per aspectRatio.
	 * 
	 * @param aspectRatioValue
	 * @return
	 * 		this
	 */
	public TextureOptions aspectRatio(TextureAspectRatioType value) {
		aspectRatio = value.aspectXY;
		return this;
	}
	
	/**
	 * Fluent interface per aspectRatio.
	 * 
	 * @param aspectRatioValue
	 * @return
	 * 		this
	 */
	public TextureOptions aspectRatio(double value) {
		aspectRatio = value;
		return this;
	}

	/**
	 * Fluent interface per debugTextureOnFile.
	 * 
	 * @param value
	 * @return
	 * 		this
	 */
	public TextureOptions debugTextureOnFile(boolean value) {
		debugTextureOnFile = value;
		return this;
	}

	/**
	 * Fluent interface per transformation.
	 * 
	 * @param aspectRatioValue
	 * @return
	 * 		this
	 */
	public TextureOptions transformation(BitmapTransformation value) {
		transformation = value;
		return this;
	}

	/**
	 * Tipo di formato interno della texture: unsigned byte o float
	 * 
	 * @param value
	 * @return
	 */
	public TextureOptions textureInternalFormat(TextureInternalFormatType value) {
		textureInternalFormat = value;
		return this;
	}

	/**
	 * Build delle opzioni. configurazione di default:
	 * 
	 * <ul>
	 * <li><b>textureSize</b>: {@link TextureSize#SIZE_UNBOUND}</li>
	 * <li><b>aspectRatio</b>: {@link TypeAspectRatio#RATIO1_1}</li>
	 * <li><b>opacity</b>: 1.0f</li>
	 * <li><b>textureRepeat</b>: {@link TextureRepeatType#NO_REPEAT}</li>
	 * <li><b>textureFilter</b>: {@link TextureFilterType#NEAREST}</li>
	 * <li><b>asyncLoad</b>: false</li>
	 * <li><b>debugTextureOnFile</b>: false</li>
	 * </ul>
	 * 
	 * @return
	 */
	public static TextureOptions build() {
		// configurazione di default
		return (new TextureOptions())
				.size(TextureSizeType.SIZE_UNBOUND)
				.aspectRatio(TextureAspectRatioType.RATIO1_1)
				.opacity(1.0f).textureRepeat(TextureRepeatType.NO_REPEAT)
				.debugTextureOnFile(false)
				.textureFilter(TextureFilterType.NEAREST)
				.textureInternalFormat(TextureInternalFormatType.UNSIGNED_BYTE);
	}

	/**
	 * <p>
	 * Crea una copia della configurazione
	 * </p>
	 * 
	 * @return
	 */
	public TextureOptions copy(TextureOptions src) {
		TextureOptions nuovo = build();

		nuovo.aspectRatio = src.aspectRatio;
		nuovo.debugTextureOnFile = src.debugTextureOnFile;
		nuovo.opacity = src.opacity;
		nuovo.textureFilter = src.textureFilter;
		nuovo.textureRepeat = src.textureRepeat;
		nuovo.textureSize = src.textureSize;
		nuovo.transformation = src.transformation;

		return nuovo;
	}

	/**
	 * Fluent interface per textureFilter
	 * 
	 * @param value
	 * @return
	 */
	public TextureOptions textureFilter(TextureFilterType value) {
		textureFilter = value;
		return this;
	}

	/**
	 * Fluent interface per name
	 * 
	 * @param value
	 * @return
	 */
	public TextureOptions name(String value) {
		name = value;
		return this;
	}
}
