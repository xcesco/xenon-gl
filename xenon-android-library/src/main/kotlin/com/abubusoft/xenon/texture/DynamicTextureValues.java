package com.abubusoft.xenon.texture;

import android.graphics.Bitmap;

/**
 * <p>
 * Opzioni relative al cambio dinamico delle texture.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class DynamicTextureValues {
	/**
	 * <p>
	 * Modo nel quale carichiamo le texture.
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum TextureLoadType {
		ASSETS_FILE, BITMAP, FILE, RESOURCE_ID, RESOURCE_STRING;
	}

	/**
	 * indice corrente della texture da caricare
	 */
	int currentIndexToLoad;

	/**
	 * <p>
	 * Indica da dove caricare le texture
	 * </p>
	 */
	TextureLoadType load;

	/**
	 * <p>
	 * Eventuale contenitore di stringhe.
	 * </p>
	 */
	String[] strings;

	/**
	 * <p>
	 * Eventuale contenitore di bitmap.
	 * </p>
	 */
	Bitmap[] bitmaps;

	/**
	 * <p>
	 * Eventuale contenitore di resourceId.
	 * </p>
	 */
	int[] resourceIds;

	/**
	 * costruttore
	 */
	private DynamicTextureValues() {
		currentIndexToLoad = 0;
	}

	/**
	 * Builder
	 * 
	 * @return
	 */
	public static DynamicTextureValues build() {
		return (new DynamicTextureValues());
	}

	/**
	 * <p>
	 * Carica texture da files
	 * </p>
	 * 
	 * @param values
	 * @return
	 */
	public DynamicTextureValues loadAssetsFile(String... values) {
		load = TextureLoadType.ASSETS_FILE;

		strings = values;
		bitmaps = new Bitmap[values.length];
		resourceIds = new int[values.length];
		return this;
	}

	public DynamicTextureValues loadBitmaps(Bitmap... values) {
		load = TextureLoadType.BITMAP;

		bitmaps = values;
		resourceIds = new int[values.length];
		strings = new String[values.length];
		return this;
	}

	public DynamicTextureValues loadFiles(String... values) {
		load = TextureLoadType.FILE;

		strings = values;
		bitmaps = new Bitmap[values.length];
		resourceIds = new int[values.length];
		return this;
	}

	public DynamicTextureValues loadResourceIds(int... values) {
		load = TextureLoadType.RESOURCE_ID;

		resourceIds = values;
		strings = new String[values.length];
		bitmaps = new Bitmap[values.length];
		return this;
	}

	public DynamicTextureValues loadResourceStrings(String... values) {
		load = TextureLoadType.RESOURCE_STRING;

		strings = values;
		bitmaps = new Bitmap[values.length];
		resourceIds = new int[values.length];
		return this;
	}

}
