package com.abubusoft.xenon.texture

/**
 * Rappresenta un puntatore ad una texture. E' sempre bene utilizzare le referenze alle texture piuttosto che
 * le texture direttamente.
 *
 * @author Francesco Benincasa
 */
class TextureReference {
    /**
     * indice della texture referenziata.
     */
    var index = 0

    /**
     * Costruttore
     *
     * @param textureIndexValue
     */
    constructor() {
        update(INVALID_REFERENCE)
    }

    /**
     * Costruttore
     *
     * @param textureIndexValue
     */
    constructor(textureIndexValue: Int) {
        update(textureIndexValue)
    }

    fun copy(): TextureReference {
        return TextureReference(index)
    }

    fun copyInto(destination: TextureReference) {
        destination.update(index)
    }

    /**
     * Restituisce la texture referenziata
     * @return
     */
    fun get(): Texture {
        return TextureManager.Companion.instance().getTexture(index)
    }

    /**
     * @return
     */
    val isValid: Boolean
        get() = index != INVALID_REFERENCE

    /**
     * Aggiorniamo texture referenziata.
     *
     * @param textureIndexValue
     */
    fun update(value: Int) {
        index = value
    }

    /**
     * Aggiorniamo texture referenziata.
     *
     * @param textureIndexValue
     */
    fun update(value: Texture) {
        index = value.index
    }

    /**
     * Aggiorniamo texture referenziata.
     *
     * @param textureIndexValue
     */
    fun update(value: TextureReference) {
        index = value.index
    }

    companion object {
        /**
         * costante per indicare valore invalido
         */
        const val INVALID_REFERENCE = -1
    }
}