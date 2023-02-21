/**
 * 
 */
package com.abubusoft.xenon.texture;

/**
 * <p>Opzioni relative alla sostituzione delle texture. Serve fondamentalmente
 * ad indicare il fatto che la texture venga caricata in modo asincrono o in 
 * modo sincrono.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public class TextureReplaceOptions {
	/**
	 * <p>Build delle opzioni. configurazione di default:</p>
	 * 
	 * asyncLoad(false)
	 * 
	 * @return
	 */
	public static TextureReplaceOptions build() {
		// configurazione di default
		return (new TextureReplaceOptions()).asyncLoad(false);
	}
	
	
	/**
	 * <p>
	 * Carica la bitmap in async mode
	 * </p>
	 */
	public boolean asyncLoad;

	/**
	 * <p>
	 * Listener
	 * </p>
	 */
	public TextureAsyncLoaderListener asyncLoaderListener;
	
	/**
	 * Fluent interface per asyncLoad.
	 * 
	 * @param textureSizeValue
	 * @return
	 */
	public TextureReplaceOptions asyncLoad(boolean value) {
		asyncLoad = value;
		return this;
	}

	/**
	 * Fluent interface per asyncLoad.
	 * 
	 * @param textureSizeValue
	 * @return
	 */
	public TextureReplaceOptions asyncLoaderListener(TextureAsyncLoaderListener value) {
		asyncLoad = asyncLoad || value != null;
		asyncLoaderListener = value;
		return this;
	}

	/**
	 * <p>Effettua una copia di questa istanza.
	 * @return
	 */
	public TextureReplaceOptions copy() {
		return build().asyncLoad(this.asyncLoad).asyncLoaderListener(this.asyncLoaderListener);
	}
}
