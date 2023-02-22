/**
 *
 */
package com.abubusoft.xenon.texture

/**
 *
 * Opzioni relative alla sostituzione delle texture. Serve fondamentalmente
 * ad indicare il fatto che la texture venga caricata in modo asincrono o in
 * modo sincrono.
 *
 * @author Francesco Benincasa
 */
class TextureReplaceOptions {
    /**
     *
     *
     * Carica la bitmap in async mode
     *
     */
    var asyncLoad = false

    /**
     *
     *
     * Listener
     *
     */
    var asyncLoaderListener: TextureAsyncLoaderListener? = null

    /**
     * Fluent interface per asyncLoad.
     *
     * @param textureSizeValue
     * @return
     */
    fun asyncLoad(value: Boolean): TextureReplaceOptions {
        asyncLoad = value
        return this
    }

    /**
     * Fluent interface per asyncLoad.
     *
     * @param textureSizeValue
     * @return
     */
    fun asyncLoaderListener(value: TextureAsyncLoaderListener?): TextureReplaceOptions {
        asyncLoad = asyncLoad || value != null
        asyncLoaderListener = value
        return this
    }

    /**
     *
     * Effettua una copia di questa istanza.
     * @return
     */
    fun copy(): TextureReplaceOptions {
        return build().asyncLoad(asyncLoad).asyncLoaderListener(asyncLoaderListener)
    }

    companion object {
        /**
         *
         * Build delle opzioni. configurazione di default:
         *
         * asyncLoad(false)
         *
         * @return
         */
        fun build(): TextureReplaceOptions {
            // configurazione di default
            return TextureReplaceOptions().asyncLoad(false)
        }
    }
}