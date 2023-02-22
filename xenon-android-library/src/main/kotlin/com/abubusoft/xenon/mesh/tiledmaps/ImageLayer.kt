/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps

import android.content.Context
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.MeshFactory.createSprite
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.MeshSprite
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier.setTextureCoords
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.ImageLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType
import com.abubusoft.xenon.texture.*
import com.abubusoft.xenon.vbo.BufferAllocationOptions
import com.abubusoft.xenon.vbo.BufferAllocationType
import org.xml.sax.Attributes

/**
 *
 *
 * Un layer che contiene una singola immagine.
 *
 *
 * @author Francesco Benincasa
 */
class ImageLayer(
    tiledMap: TiledMap,
    /**
     * tipo di caricamento dell'immagine: da asset o da risorsa. A seconda del valore, tratta in modo diverso l'imageSource.
     */
    private val loaderType: TMXLoaderType, atts: Attributes?, textureFilterValue: TextureFilterType?
) : Layer(LayerType.IMAGE, tiledMap, atts) {
    /**
     * Opzione per la creazione del texture atlas.
     */
    private val textureOptions: TextureOptions

    /**
     * Nome della texture associata al layer
     */
    var imageSource: String? = null

    /**
     * modo di riempimento
     */
    var fillMode: FillModeType

    /**
     *
     *
     * Modi di riempire il layer
     *
     *
     * @author Francesco Benincasa
     */
    enum class FillModeType {
        /**
         * Ricopre la finestra, considerando quando
         */
        REPEAT_ON_WINDOW,

        /**
         * Ricopre la window con la texture, senza ripetizione.
         */
        EXPAND_ON_WINDOW, REPEAT_ON_MAP, EXPAND_ON_MAP
    }

    /**
     *
     *
     * Evento da invocare quando si effettua il resize della window
     *
     */
    override fun onBuildView(view: TiledMapView) {
        handler!!.onBuildView(view)
        fillMode = FillModeType.REPEAT_ON_WINDOW
        val texture: Texture = textureList[0]

        // nel costruttore le dimensioni della window non le abbiamo
        // fullWindowTile.setDimensions(tiledMap.windowWidth, tiledMap.windowHeight);
        shape = createSprite(
            view.windowWidth.toFloat(), view.windowHeight.toFloat(),
            MeshOptions.build().bufferAllocationOptions(
                BufferAllocationOptions.build().indexAllocation(BufferAllocationType.STATIC).vertexAllocation(BufferAllocationType.STATIC)
                    .textureAllocation(BufferAllocationType.STREAM)
            )
        )
        when (fillMode) {
            FillModeType.REPEAT_ON_WINDOW ->            // fullWindowTile.setTextureCoordinate(0f, (float) (1.0 * tiledMap.windowWidth / texture.info.dimension.width), 0, (float) (1.0 * tiledMap.windowHeight /
                // texture.info.dimension.height));
                setTextureCoords(
                    shape!!.textures[0],
                    0,
                    0f,
                    (1.0 * view.windowWidth / texture.info.dimension.width).toFloat(),
                    0f,
                    (1.0 * view.windowHeight / texture.info.dimension.height).toFloat(),
                    false,
                    true
                )
            FillModeType.EXPAND_ON_WINDOW ->            // ok
                // fullWindowTile.setTextureCoordinate(0, 1, 0, 1);
                setTextureCoords(shape!!.textures[0], 0, 0f, 1f, 0f, 1f, false, true)
            FillModeType.REPEAT_ON_MAP -> {}
            FillModeType.EXPAND_ON_MAP -> {}
        }
    }

    /**
     *
     *
     * mesh per disegnare lo sfondo.
     *
     */
    var shape: MeshSprite? = null
    protected var handler: ImageLayerHandler? = null

    init {
        textureOptions = TextureOptions.build().textureFilter(textureFilterValue).textureRepeat(TextureRepeatType.REPEAT)
        fillMode = FillModeType.REPEAT_ON_WINDOW

        // fullWindowTile = new Tile();
    }

    /**
     *
     *
     * Carichiamo la texture e la inseriamo tra le texture usate da questo layer
     *
     *
     * @param context
     */
    fun loadTexture(context: Context?) {
        var temp: Texture? = null
        temp = when (loaderType) {
            TMXLoaderType.ASSET_LOADER -> TextureManager.instance().createTextureFromAssetsFile(context, imageSource, textureOptions)
            TMXLoaderType.RES_LOADER -> TextureManager.instance().createTextureFromResourceString(context, imageSource, textureOptions)
            else -> throw XenonRuntimeException("Type of loader is not supported")
        }
        val tto = AtlasTextureOptions.build()
        val texture: AtlasTexture
        tto.tileWidth(temp.info.dimension.width).tileHeight(temp.info.dimension.height).margin(0).spacing(0)
        texture = TextureManager.instance().createAtlasTexture(temp, tto)
        textureList.add(texture)
    }

    override fun buildHandler(handler: AbstractLayerHandler<*>?) {
        this.handler = handler as ImageLayerHandler?
    }

    override fun drawer(): LayerDrawer? {
        return handler
    }

    override fun view(): TiledMapView? {
        return handler!!.view()
    }
}