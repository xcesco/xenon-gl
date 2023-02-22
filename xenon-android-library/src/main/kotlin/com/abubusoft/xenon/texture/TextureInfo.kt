/**
 *
 */
package com.abubusoft.xenon.texture

import android.content.Context
import android.opengl.GLES20
import com.abubusoft.xenon.opengl.XenonGL

/**
 * Contiene le informazioni relative ad una texture. Sono informazioni asettiche rispetto ad openGL, quindi ad esempio non si troverà qui il bindingId.
 *
 * Gestisce anche le cube texture, quindi ad ogni texture possono essere associati più di una risorsa.
 *
 * @author Francesco Benincasa
 */
class TextureInfo @JvmOverloads constructor(
    /**
     * tipo di caricamento
     */
    val load: TextureLoadType,
    /**
     * tipo di texture
     */
    val type: TextureType = TextureType.TEXTURE2D
) {
    /**
     * Tipo di caricamento
     *
     * @author Francesco Benincasa
     */
    enum class TextureLoadType {
        ASSET_TEXTURE, BITMAP_TEXTURE, FILE_TEXTURE, RESOURCE_TEXTURE
    }

    /**
     *
     *
     * Tipi di texture: standard o cubiche.
     *
     *
     * @author Francesco Benincasa
     */
    enum class TextureType(
        /**
         *
         *
         * Valore da usare per il binding opengl.
         *
         */
        var value: Int
    ) {
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
    }

    /**
     * address della risorsa
     */
    protected var resourceId: IntArray
    fun setResourceId(value: Int) {
        setResourceId(0, value)
    }

    fun setResourceId(index: Int, value: Int) {
        resourceId[index] = value
    }

    fun getResourceId(): Int {
        return getResourceId(0)
    }

    fun getResourceId(index: Int): Int {
        return resourceId[index]
    }

    /**
     * nome del filename
     */
    protected var fileName: Array<String?>
    fun setFileName(value: String?) {
        setFileName(0, value)
    }

    fun setFileName(index: Int, value: String?) {
        fileName[index] = value
    }

    fun getFileName(): String? {
        return getFileName(0)
    }

    fun getFileName(index: Int): String? {
        return fileName[index]
    }

    /**
     * dimensione della texture
     */
    var dimension: TextureDimension? = null

    /**
     * opzioni per la costruzione della texture
     */
    var options: TextureOptions? = null

    /**
     * Contesto dal quale viene recupearata
     */
    var resourceContext: Context? = null

    init {
        when (type) {
            TextureType.TEXTURE2D -> {
                fileName = arrayOfNulls(1)
                resourceId = IntArray(1)
            }
            TextureType.TEXTURE2D_CUBIC -> {
                fileName = arrayOfNulls(6)
                resourceId = IntArray(6)
            }
            TextureType.TEXTURE_EXTERNAL -> {}
        }
    }
}