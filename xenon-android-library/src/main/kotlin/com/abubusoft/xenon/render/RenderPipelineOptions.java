package com.abubusoft.xenon.render;

import com.abubusoft.xenon.texture.RenderedTextureOptions;
import com.abubusoft.xenon.texture.TextureSizeType;

/**
 * <p>
 * Opzioni per la creazione una render
 * 
 * @author Francesco Benincasa
 * 
 */
public class RenderPipelineOptions {
	
	/**
	 * Se true indica che le texture e la mesh utilizzata è fatta apposta per entrare completamente nello schermo
	 * Se false la mesh e le texture vengono usate semplicemente come un quadrato.
	 */
	public boolean optimized;
	
	/**
	 * dimensioni del viewport
	 */
	public TextureSizeType viewportDimensions;

	/**
	 * opzioni della texture per il rendering
	 */
	public RenderedTextureOptions renderTextureOptions;

	private RenderPipelineOptions() {

	}

	/**
	 * dimensioni standard del viewport
	 */
	public static TextureSizeType VIEWPORT_DIMENSION_NORMAL = TextureSizeType.SIZE_512x512;

	public static TextureSizeType VIEWPORT_DIMENSION_BIG = TextureSizeType.SIZE_1024x1024;
	
	public static TextureSizeType VIEWPORT_DIMENSION_HD = TextureSizeType.SIZE_2048x2048;

	public static RenderPipelineOptions build() {
		return (new RenderPipelineOptions()).dimension(VIEWPORT_DIMENSION_NORMAL).renderTextureOptions(RenderedTextureOptions.build()).optimized(true);
	}

	public RenderPipelineOptions renderTextureOptions(RenderedTextureOptions value) {
		renderTextureOptions = value;
		return this;
	}

	public RenderPipelineOptions dimension(TextureSizeType value) {
		viewportDimensions=value;
		return this;
	}
	
	public RenderPipelineOptions optimized(boolean value) {
		optimized=value;
		return this;
	}
}
