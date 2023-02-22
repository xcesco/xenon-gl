package com.abubusoft.xenon.texture

import android.graphics.Bitmap

/**
 *
 *
 * Opzioni relative al cambio dinamico delle texture.
 *
 *
 * @author Francesco Benincasa
 */
class DynamicTextureValues
/**
 * costruttore
 */ private constructor() {
    /**
     *
     *
     * Modo nel quale carichiamo le texture.
     *
     *
     * @author Francesco Benincasa
     */
    enum class TextureLoadType {
        ASSETS_FILE, BITMAP, FILE, RESOURCE_ID, RESOURCE_STRING
    }

    /**
     * indice corrente della texture da caricare
     */
    var currentIndexToLoad = 0

    /**
     *
     *
     * Indica da dove caricare le texture
     *
     */
    var load: TextureLoadType? = null

    /**
     *
     *
     * Eventuale contenitore di stringhe.
     *
     */
    var strings: Array<String?>

    /**
     *
     *
     * Eventuale contenitore di bitmap.
     *
     */
    var bitmaps: Array<Bitmap?>

    /**
     *
     *
     * Eventuale contenitore di resourceId.
     *
     */
    var resourceIds: IntArray

    /**
     *
     *
     * Carica texture da files
     *
     *
     * @param values
     * @return
     */
    fun loadAssetsFile(vararg values: String?): DynamicTextureValues {
        load = TextureLoadType.ASSETS_FILE
        strings = values
        bitmaps = arrayOfNulls(values.size)
        resourceIds = IntArray(values.size)
        return this
    }

    fun loadBitmaps(vararg values: Bitmap?): DynamicTextureValues {
        load = TextureLoadType.BITMAP
        bitmaps = values
        resourceIds = IntArray(values.size)
        strings = arrayOfNulls(values.size)
        return this
    }

    fun loadFiles(vararg values: String?): DynamicTextureValues {
        load = TextureLoadType.FILE
        strings = values
        bitmaps = arrayOfNulls(values.size)
        resourceIds = IntArray(values.size)
        return this
    }

    fun loadResourceIds(vararg values: Int): DynamicTextureValues {
        load = TextureLoadType.RESOURCE_ID
        resourceIds = values
        strings = arrayOfNulls(values.size)
        bitmaps = arrayOfNulls(values.size)
        return this
    }

    fun loadResourceStrings(vararg values: String?): DynamicTextureValues {
        load = TextureLoadType.RESOURCE_STRING
        strings = values
        bitmaps = arrayOfNulls(values.size)
        resourceIds = IntArray(values.size)
        return this
    }

    companion object {
        /**
         * Builder
         *
         * @return
         */
        fun build(): DynamicTextureValues {
            return DynamicTextureValues()
        }
    }
}