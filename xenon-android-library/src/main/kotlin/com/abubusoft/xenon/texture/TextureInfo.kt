/**
 * 
 */
package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.opengl.XenonGL;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Contiene le informazioni relative ad una texture. Sono informazioni asettiche rispetto ad openGL, quindi ad esempio non si troverà qui il bindingId.
 * 
 * Gestisce anche le cube texture, quindi ad ogni texture possono essere associati più di una risorsa.
 * 
 * @author Francesco Benincasa
 * 
 */
public class TextureInfo {

	public TextureInfo(TextureLoadType loadValue) {
		this(loadValue, TextureType.TEXTURE2D);
	}

	public TextureInfo(TextureLoadType loadValue, TextureType typeValue) {
		load = loadValue;
		type = typeValue;

		switch (type) {
		case TEXTURE2D:
			fileName = new String[1];
			resourceId = new int[1];
			break;
		case TEXTURE2D_CUBIC:
			fileName = new String[6];
			resourceId = new int[6];
			break;
		case TEXTURE_EXTERNAL:
			//TODO CHECK
			break;
		}
	}

	/**
	 * Tipo di caricamento
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum TextureLoadType {
		ASSET_TEXTURE, BITMAP_TEXTURE, FILE_TEXTURE, RESOURCE_TEXTURE
	};

	/**
	 * <p>
	 * Tipi di texture: standard o cubiche.
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum TextureType {
		/**
		 * texture generiche
		 */
		TEXTURE2D(GLES20.GL_TEXTURE_2D),
		/**
		 * texture cubiche
		 */
		TEXTURE2D_CUBIC(GLES20.GL_TEXTURE_CUBE_MAP),
		/**
		 * texture esterne
		 */
		TEXTURE_EXTERNAL(XenonGL.TEXTURE_EXTERNAL_OES);

		/**
		 * <p>
		 * Valore da usare per il binding opengl.
		 * </p>
		 */
		public int value;

		TextureType(int val) {
			value = val;
		}
	}

	/**
	 * tipo di caricamento
	 */
	public final TextureLoadType load;

	/**
	 * tipo di texture
	 */
	public final TextureType type;

	/**
	 * address della risorsa
	 */
	protected int[] resourceId;

	public void setResourceId(int value) {
		setResourceId(0, value);
	}

	public void setResourceId(int index, int value) {
		resourceId[index] = value;
	}

	public int getResourceId() {
		return getResourceId(0);
	}

	public int getResourceId(int index) {
		return resourceId[index];
	}

	/**
	 * nome del filename
	 */
	protected String[] fileName;

	public void setFileName(String value) {
		setFileName(0, value);
	}

	public void setFileName(int index, String value) {
		fileName[index] = value;
	}

	public String getFileName() {
		return getFileName(0);
	}

	public String getFileName(int index) {
		return fileName[index];
	}

	/**
	 * dimensione della texture
	 */
	public TextureDimension dimension;

	/**
	 * opzioni per la costruzione della texture
	 */
	public TextureOptions options;

	/**
	 * Contesto dal quale viene recupearata
	 */
	public Context resourceContext;
}
