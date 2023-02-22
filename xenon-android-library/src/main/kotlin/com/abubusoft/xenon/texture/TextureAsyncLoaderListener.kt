package com.abubusoft.xenon.texture;

/**
 * <p>Listener relativo al caricamento asincrono delle texture.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public interface TextureAsyncLoaderListener {
	
	/**
	 * <p>Evento scatenato quando la texture è bindata</p>
	 * @param texture
	 */
	void onTextureReady(Texture texture);

}
