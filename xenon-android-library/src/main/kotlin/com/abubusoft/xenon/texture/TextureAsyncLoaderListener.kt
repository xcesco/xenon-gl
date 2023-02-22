package com.abubusoft.xenon.texture

/**
 *
 * Listener relativo al caricamento asincrono delle texture.
 *
 * @author Francesco Benincasa
 */
interface TextureAsyncLoaderListener {
    /**
     *
     * Evento scatenato quando la texture è bindata
     * @param texture
     */
    fun onTextureReady(texture: Texture?)
}