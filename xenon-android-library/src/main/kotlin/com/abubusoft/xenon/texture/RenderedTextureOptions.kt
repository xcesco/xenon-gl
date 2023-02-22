/**
 * 
 */
package com.abubusoft.xenon.texture;

/**
 * @author Francesco Benincasa
 * 
 */
public class RenderedTextureOptions {

	/**
	 * <p>
	 * Valori predefiniti:
	 * </p>
	 * <dl>
	 * <dt>name</dt>
	 * <dd>[undefined]</dd>
	 * <dt>depthBuffer</dt>
	 * <dd>true</dd>
	 * <dt>textureInternalFormat</dt>
	 * <dd>TextureInternalFormatType.UNSIGNED_BYTE</dd>
	 * <dt>renderFactor</dt>
	 * <dd>1.0</dd>
	 * </dl>
	 * 
	 * @return
	 * 		opzioni
	 */
	public static RenderedTextureOptions build() {
		return (new RenderedTextureOptions()).depthBuffer(true).textureInternalFormat(TextureInternalFormatType.UNSIGNED_BYTE).renderFactor(1.f);
	}

	public boolean depthBuffer;
	
	public float renderFactor;

	public RenderedTextureOptions depthBuffer(boolean value) {
		depthBuffer = value;
		return this;
	}
	
	public RenderedTextureOptions renderFactor(float value) {
		renderFactor = value;
		return this;
	}

	public String name = "[RenderedTexture noname]";

	/**
	 * Tipo di formato interno della texture: unsigned byte o float
	 * 
	 */
	public TextureInternalFormatType textureInternalFormat;

	public RenderedTextureOptions name(String value) {
		this.name = value;

		return this;
	}

	/**
	 * Tipo di formato interno della texture: unsigned byte o float
	 * 
	 * @param value
	 * @return
	 */
	public RenderedTextureOptions textureInternalFormat(TextureInternalFormatType value) {
		textureInternalFormat = value;
		return this;
	}
}
