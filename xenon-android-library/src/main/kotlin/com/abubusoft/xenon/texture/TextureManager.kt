/**
 *
 */
package com.abubusoft.xenon.texture

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.core.util.IOUtility.deleteTempFiles
import com.abubusoft.xenon.opengl.XenonGL.clearGlError
import com.abubusoft.xenon.texture.CubeTextureBinder.bindTextureFromResourceId
import com.abubusoft.xenon.texture.TextureInfo.TextureType

/**
 * Gestore delle texture. Ha la possibilità di ricaricare in automatico le texture, se il flat viene opporturnamente valorizzato. Il contro effetto è che va a creare delle
 * immmagini di cache.
 *
 * @author Francesco Benincasa
 */
class TextureManager private constructor() {
    /**
     * Rigenera le texture.
     */
    fun reloadTextures() {
        // puliamo i binder
        if (textures.size > 0) {
            val n = textures.size
            val textureIds = IntArray(n)

            // ricaviamo tutti i bindingId
            for (i in 0 until n) {
                textureIds[i] = textures[i].bindingId
                textures[i].unbind()
            }
            GLES20.glDeleteTextures(textureIds.size, textureIds, 0)
            GLES20.glFlush()

            // cancelliamo le vecchie texture
            Logger.debug("Unbinded $n old textures, without deleting them ")
            var current: Texture
            var info: TextureInfo?
            for (i in 0 until n) {
                current = textures[i]
                current.bindingId = newTextureBindingId()
                current.reload()
                if (!current.ready) {
                    Logger.warn("Texture %s index: %s not ready (async load)", current.name, current.index)
                    continue
                }
                info = current.info
                when (info!!.type) {
                    TextureType.TEXTURE2D -> {
                        val currentTexture = current
                        when (info.load) {
                            TextureInfo.TextureLoadType.ASSET_TEXTURE -> TextureBinder.bindTextureFromAssetsFile(
                                currentTexture,
                                info.resourceContext,
                                info.getFileName(),
                                info.options,
                                TextureReplaceOptions.Companion.build()
                            )
                            TextureInfo.TextureLoadType.RESOURCE_TEXTURE -> TextureBinder.bindTextureFromResourceId(
                                currentTexture,
                                info.resourceContext,
                                info.getResourceId(),
                                info.options,
                                TextureReplaceOptions.Companion.build()
                            )
                            TextureInfo.TextureLoadType.FILE_TEXTURE -> TextureBinder.bindTextureFromFile(
                                currentTexture,
                                info.resourceContext,
                                info.getFileName(),
                                info.options,
                                TextureReplaceOptions.Companion.build()
                            )
                            TextureInfo.TextureLoadType.BITMAP_TEXTURE ->                        // carichiamo dalla texture temporanea
                                TextureBinder.bindTextureFromFile(currentTexture, info.resourceContext, info.getFileName(), info.options, TextureReplaceOptions.Companion.build())
                            else -> {}
                        }
                    }
                    TextureType.TEXTURE2D_CUBIC -> {
                        val currentCubeTexture = current as CubeTexture
                        when (info.load) {
                            TextureInfo.TextureLoadType.RESOURCE_TEXTURE -> bindTextureFromResourceId(
                                currentCubeTexture,
                                info.resourceContext!!,
                                currentCubeTexture.bindingId,
                                info.getResourceId(0),
                                info.getResourceId(1),
                                info.getResourceId(2),
                                info.getResourceId(3),
                                info.getResourceId(4),
                                info.getResourceId(5),
                                info.options!!,
                                TextureReplaceOptions.Companion.build()
                            )
                            TextureInfo.TextureLoadType.ASSET_TEXTURE, TextureInfo.TextureLoadType.BITMAP_TEXTURE, TextureInfo.TextureLoadType.FILE_TEXTURE -> {}
                        }
                        Logger.debug("Rebind texture %s (%s)", i, info.load)
                    }
                    TextureType.TEXTURE_EXTERNAL -> {
                        val currentExternalTexture = current as ExternalTexture
                        ExternalTextureBinder.bindTexture(
                            currentExternalTexture,
                            currentExternalTexture.bindingId,
                            currentExternalTexture.info!!.options,
                            TextureReplaceOptions.Companion.build()
                        )
                    }
                }

                // Importante: serve per le render texture
                current.reload()
            }
        }
    }

    /**
     * array delle texture
     */
    private val textures: ArrayList<Texture>

    /**
     * indica se le texture devono essere caricate in automatico alla creazione dello screen.
     */
    val isTexturesReloadable: Boolean

    /**
     * Costruttore
     */
    init {
        textures = ArrayList()
        isTexturesReloadable = true
    }

    /**
     * Aggiunge una nuova texture al manager
     *
     * @param newTexture
     */
    private fun appendToManagedTexture(newTexture: Texture) {
        textures.add(newTexture)
        newTexture.index = textures.size - 1
        Logger.debug("Texture index %s, bindingId %s is created, Loaded %s", newTexture.index, newTexture.bindingId, newTexture.ready)
    }

    /**
     * Se sono presenti delle texture, dealloca i suoi id. Viene invocato
     *
     * @param gl
     * @param context
     * @param resource
     */
    fun clearTextures(): Int {
        val context = getBean<Context>(XenonBeanType.CONTEXT)
        // puliamo cmq tutte le immagini cachate
        val c = deleteTempFiles(context!!, "texture_")
        Logger.debug("Deleted $c old textures cached on files")
        return if (textures.size > 0) {
            val n = textures.size
            val textureIds = IntArray(n)
            clearGlError()

            // ricaviamo tutti i bindingId
            for (i in 0 until n) {
                textureIds[i] = textures[i].bindingId
                textures[i].unbind()
            }
            GLES20.glDeleteTextures(textureIds.size, textureIds, 0)
            GLES20.glFlush()

            // cancelliamo le vecchie texture
            textures.clear()
            Logger.debug("Unbinded $n old textures ")
            n
        } else {
            // non dobbiamo cancellare alcuna texture
            Logger.debug("No texture to unbind")
            0
        }
    }

    /**
     *
     *
     * Carica una texture da un file.
     *
     *
     * @param url
     * path del file da caricare
     * @param options
     * opzioni per caricare la texture
     */
    fun createTextureFromFile(context: Context?, url: String?, options: TextureOptions): Texture {
        val bindingId = newTextureBindingId()
        val texture = Texture(options.name, bindingId)
        appendToManagedTexture(texture)
        texture.updateInfo(TextureBinder.bindTextureFromFile(texture, context, url, options, TextureReplaceOptions.Companion.build()))
        return texture
    }

    /**
     * Carica una texture da un file in un assets
     *
     * @param url
     * path del file da caricare
     * @param options
     * opzioni per caricare la texture
     */
    fun createTextureFromAssetsFile(context: Context?, url: String?, options: TextureOptions?): Texture {
        val bindingId = newTextureBindingId()
        val texture = Texture(url, bindingId)
        appendToManagedTexture(texture)
        texture.updateInfo(TextureBinder.bindTextureFromAssetsFile(texture, context, url, options, TextureReplaceOptions.Companion.build()))
        return texture
    }

    /**
     * Carica una texture da un resource id. Le dimensioni della texture sono quelle dell'immagine stessa, non viene fatto alcun controllo sulla sua dimensione.
     *
     * @param index
     * indice della texture da definire
     * @param resourceIdx
     */
    fun createTextureFromResourceId(context: Context, resourceIdx: Int, options: TextureOptions?): Texture {
        val bindingId = newTextureBindingId()
        val textureName = context.resources.getResourceEntryName(resourceIdx)
        val texture = Texture(textureName, bindingId)
        appendToManagedTexture(texture)
        texture.updateInfo(TextureBinder.bindTextureFromResourceId(texture, context, resourceIdx, options, TextureReplaceOptions.Companion.build()))
        return texture
    }

    /**
     *
     *
     * Carica una texture da una bitmap.
     *
     *
     * @param url
     * path del file da caricare
     * @param options
     * opzioni per caricare la texture
     */
    fun createTextureFromBitmap(context: Context?, bitmap: Bitmap?, options: TextureOptions): Texture {
        val bindingId = newTextureBindingId()
        val texture = Texture(options.name, bindingId)
        appendToManagedTexture(texture)
        texture.updateInfo(TextureBinder.bindTextureFromBitmap(texture, context, bitmap, options, TextureReplaceOptions.Companion.build()))
        return texture
    }

    /**
     * Carica una lista di risorse sottoforma di puntatore di risorsa
     *
     * @param gl
     * @param context
     * @param resource
     */
    fun createTextureFromResourceIdList(context: Context, resourceIdList: List<Int>, options: TextureOptions?) {
        val textureIds = IntArray(resourceIdList.size)
        GLES20.glGenTextures(textureIds.size, textureIds, 0)
        Logger.debug("Generated " + resourceIdList.size + " texture ids")
        for (i in resourceIdList.indices) {
            createTextureFromResourceId(context, resourceIdList[i], options)
        }
    }

    /**
     * Carica una texture da una risorsa. Se manca il drawable davanti, viene messo in automatico.
     *
     * @param context
     * @param stringResourceIdx
     * @param options
     */
    fun createTextureFromResourceString(context: Context, stringResourceIdx: String, options: TextureOptions?): Texture {
        val bindingId = newTextureBindingId()
        val texture = Texture(stringResourceIdx, bindingId)
        appendToManagedTexture(texture)
        texture.updateInfo(TextureBinder.bindTextureFromResourceString(texture, context, stringResourceIdx, options, TextureReplaceOptions.Companion.build()))
        return texture
    }

    /**
     *
     *
     * Crea una texture sulla quale scrivere successivamente.
     *
     *
     *
     * **Non può essere eseguito in modo asincrono**
     *
     *
     * @param context
     * @param width
     * @param height
     * @return
     */
    fun createRenderedTexture(context: Context?, dimensions: TextureSizeType, options: RenderedTextureOptions): RenderedTexture {
        var bitmap = Bitmap.createBitmap(dimensions.width, dimensions.height, Bitmap.Config.ARGB_8888)
        val createdTexture = createTextureFromBitmap(
            context,
            bitmap,
            TextureOptions.Companion.build().textureRepeat(TextureRepeatType.NO_REPEAT).textureFilter(TextureFilterType.NEAREST).name(options.name)
                .textureInternalFormat(options.textureInternalFormat)
        )
        val renderedTexture = RenderedTexture(createdTexture, options)

        // sostituiamo la vecchia texture con quella per il rendering
        textures[renderedTexture.index] = renderedTexture
        if (!bitmap!!.isRecycled) bitmap.recycle()
        bitmap = null
        return renderedTexture
    }

    /**
     *
     *
     *
     *
     * @param context
     * @param width
     * @param height
     * @param options
     * @return
     */
    fun createExternalTexture(options: ExternalTextureOptions): ExternalTexture {
        val bindingId = newTextureBindingId()
        val texture = ExternalTexture(options.name, bindingId, options)
        appendToManagedTexture(texture)
        texture.updateInfo(ExternalTextureBinder.bindTexture(texture, bindingId, options.toTextureOptions(), TextureReplaceOptions.Companion.build()))
        return texture
    }

    /**
     *
     *
     * Incapsula una texture in una tiled. Usato solo da AtlasLoader
     *
     *
     * @param createdTexture
     * @param tiledOptions
     * @return
     */
    fun createAtlasTexture(createdTexture: Texture?, tiledOptions: AtlasTextureOptions?): AtlasTexture {
        val tiledTexture = AtlasTexture(createdTexture!!, tiledOptions!!)
        textures[tiledTexture.index] = tiledTexture
        return tiledTexture
    }

    /**
     * Dato uno context e 6 immagini, crea una texture cube.
     *
     * @param context
     * @param upperX
     * @param lowerX
     * @param upperY
     * @param lowerY
     * @param upperZ
     * @param lowerZ
     * @param options
     * @return
     */
    fun createCubeTextureFromResourceId(context: Context?, upperX: Int, lowerX: Int, upperY: Int, lowerY: Int, upperZ: Int, lowerZ: Int, options: TextureOptions): CubeTexture {
        val bindingId = newTextureBindingId()
        val cubeTexture = CubeTexture(options.name!!, bindingId)
        appendToManagedTexture(cubeTexture)
        cubeTexture.updateInfo(
            bindTextureFromResourceId(
                cubeTexture,
                context!!,
                bindingId,
                upperX,
                lowerX,
                upperY,
                lowerY,
                upperZ,
                lowerZ,
                options,
                TextureReplaceOptions.Companion.build()
            )
        )
        return cubeTexture
    }

    /**
     *
     *
     * Crea una texture ricaricabile. Questo in realtà si traduce nella creazione di un sistema di triplo buffering
     *
     *
     * @param context
     * @param createdTexture
     * @param options
     * @return
     */
    fun createDynamicTexture(context: Context?, createdTexture: Texture?): DynamicTexture {
        return DynamicTexture(
            createdTexture, createTextureFromBitmap(context, Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888), TextureOptions.Companion.build()), createTextureFromBitmap(
                context,
                Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888), TextureOptions.Companion.build()
            )
        )
    }

    /**
     *
     *
     * Recupera la texture.
     *
     *
     * @param textureIndex
     * @return
     */
    fun getTexture(textureIndex: Int): Texture {
        return textures[textureIndex]
    }

    /**
     *
     *
     * Numero di texture attualmente allocate.
     *
     *
     * @return
     */
    val numberOfTextures: Int
        get() = textures.size

    /**
     *
     *
     * Recupera il bindingId.
     *
     *
     * @param textureIndex
     * @return
     */
    fun getTextureBindingId(textureIndex: Int): Int {
        return textures[textureIndex].bindingId
    }

    /**
     *
     *
     * Recupera la texture come atlas.
     *
     *
     * @param textureIndex
     * @return
     */
    fun getAtlasTexture(textureIndex: Int): AtlasTexture {
        return textures[textureIndex] as AtlasTexture
    }

    /**
     *
     *
     * Genera un singolo texture id e lo restituisce.
     *
     *
     * @return
     */
    private fun newTextureBindingId(): Int {
        val resourceId = IntArray(1)
        GLES20.glGenTextures(resourceId.size, resourceId, 0)
        return resourceId[0]
    }

    /**
     *
     *
     * Ricarica una texture da un file. Il binding id rimane invariato.
     *
     *
     * @param url
     * path del file da caricare
     * @param options
     * opzioni per caricare la texture
     */
    fun replaceTextureFromFile(index: Int, context: Context?, url: String?, options: TextureOptions?, loadOptions: TextureReplaceOptions?): Texture {
        val texture = getTexture(index)
        texture.updateInfo(TextureBinder.bindTextureFromFile(texture, context, url, options, loadOptions))
        return texture
    }

    /**
     *
     *
     * Ricarica una texture da un file assets. Il binding id rimane invariato.
     *
     *
     * @param url
     * path del file da caricare
     * @param options
     * opzioni per caricare la texture
     */
    fun replaceTextureFromAssetsFile(index: Int, context: Context?, url: String?, options: TextureOptions?, loadOptions: TextureReplaceOptions?): Texture {
        val texture = getTexture(index)
        texture.updateInfo(TextureBinder.bindTextureFromAssetsFile(texture, context, url, options, loadOptions))
        return texture
    }

    /**
     *
     *
     * Ricarica una texture da una bitmap. Il binding id rimane invariato.
     *
     *
     * @param url
     * path del file da caricare
     * @param options
     * opzioni per caricare la texture
     */
    fun replaceTextureFromBitmap(index: Int, context: Context?, bitmap: Bitmap?, options: TextureOptions?, loadOptions: TextureReplaceOptions?): Texture {
        val texture = getTexture(index)
        texture.updateInfo(TextureBinder.bindTextureFromBitmap(texture, context, bitmap, options, loadOptions))
        return texture
    }

    /**
     * Ricarica una texture da un resource id alla posizione definita. Le dimensioni della texture sono quelle dell'immagine stessa, non viene fatto alcun controllo sulla sua
     * dimensione.
     *
     * @param index
     * indice della texture da sostituire
     * @param context
     * contesto
     * @param resourceIdx
     * risorsaId
     * @param options
     * opzioni
     * @return texture appena creata, con lo stesso binding id di prima
     */
    fun replaceTextureFromResourceId(index: Int, context: Context?, resourceIdx: Int, options: TextureOptions?, loadOptions: TextureReplaceOptions?): Texture {
        val texture = getTexture(index)
        texture.updateInfo(TextureBinder.bindTextureFromResourceId(texture, context, resourceIdx, options, loadOptions))
        return texture
    }

    /**
     * Ricarica una texture da una risorsa alla posizione definita
     *
     * @param index
     * indice della texture da sostituire
     * @param context
     * context
     * @param stringResourceIdx
     * risorsa id sottoforma di stringa
     * @param options
     * opzioni
     * @return texture appena creata, con lo stesso binding id di prima
     */
    fun replaceTextureFromResourceString(index: Int, context: Context, resourceIdString: String?, options: TextureOptions?, loadOptions: TextureReplaceOptions?): Texture {
        val texture = getTexture(index)
        texture.updateInfo(TextureBinder.bindTextureFromResourceString(texture, context, resourceIdString, options, loadOptions))
        return texture
    }

    companion object {
        /**
         * Singleton
         */
        private val instance = TextureManager()

        /**
         * Get instance
         *
         * @return
         */
        fun instance(): TextureManager {
            return instance
        }
    }
}