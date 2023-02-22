package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import android.content.Context
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.mesh.tiledmaps.*
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes
import com.abubusoft.xenon.texture.TextureFilterType
import com.abubusoft.xenon.texture.TextureOptions
import com.abubusoft.xenon.texture.TextureRepeatType
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

class TMXLoaderHandler : DefaultHandler() {
    private val characters = StringBuilder()
    private var context: Context? = null
    private var tiledMap: TiledMap? = null
    private var encoding: String? = null
    private var compression: String? = null
    private var lastTileSetTileID = 0
    private var inTileset = false
    private var inTile = false
    private var inData = false
    private var inObject = false
    private var inLayer = false
    private var inImageLayer = false
    private var inObjectLayer = false
    private var inMap = false

    /**
     * indica da dove stiamo caricando i files
     */
    private var loaderType: TMXLoaderType? = null

    /**
     * opzioni da applicare alla texture
     */
    private var textureFilter: TextureFilterType? = null

    /**
     * Carica da un input stream
     *
     */
    @Throws(TMXException::class)
    fun load(context: Context?, inputStream: InputStream?, loaderTypeValue: TMXLoaderType?, textureFilterValue: TextureFilterType?): TiledMap? {
        try {
            this.context = context
            loaderType = loaderTypeValue
            textureFilter = textureFilterValue
            val spf = SAXParserFactory.newInstance()
            val sp = spf.newSAXParser()
            val xr = sp.xmlReader
            xr.contentHandler = this
            xr.parse(InputSource(BufferedInputStream(inputStream)))
        } catch (e: Exception) {
            Logger.fatal("%s", e.message)
            throw TMXException(e)
        }
        return tiledMap
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, atts: Attributes) {
        // serve a farlo funzionare anche da junit
        var localName = localName
        if (localName.length == 0) localName = qName
        when (localName) {
            TAG_MAP -> {
                // definisce una mappa
                tiledMap = TiledMap(atts)
                inMap = true
            }
            TAG_TILESET -> {
                // definisce un tileset
                inTileset = true
                val tsxTileSetSource = SAXUtil.getString(atts, TAG_TILESET_ATTRIBUTE_SOURCE)

                // il tileSet è con file esterno, e verrà definito in seguito con il
                // tag image
                if (tsxTileSetSource == null) {
                    // il textureRepeat=true serve per le immagini che eventualmente
                    // posson essere usate
                    // per l'image layer
                    tiledMap!!.addTileSet(
                        TileSet(
                            SAXUtil.getInt(atts, TAG_TILESET_ATTRIBUTE_FIRSTGID),
                            atts,
                            loaderType,
                            context,
                            TextureOptions.build().textureRepeat(TextureRepeatType.REPEAT).textureFilter(textureFilter)
                        )
                    )
                }
            }
            TAG_IMAGE -> {

                // tileSet -> image : definisce l'atlas da caricare
                val tmxTileSets = tiledMap!!.tileSets
                val imageName = SAXUtil.getString(atts, TAG_IMAGE_ATTRIBUTE_SOURCE)
                if (inImageLayer) {
                    // ASSERT: siamo in imageLayer
                    val currentImageLayer = tiledMap!!.layers[tiledMap!!.layers.size - 1] as ImageLayer
                    currentImageLayer.imageSource = imageName
                } else {
                    // ASSERT: siamo in tileSet
                    tmxTileSets[tmxTileSets.size - 1].imageSource = imageName
                }
            }
            TAG_TILE -> {
                inTile = true
                if (inTileset) {
                    lastTileSetTileID = SAXUtil.getInt(atts, TAG_TILE_ATTRIBUTE_ID)
                } else if (inData) {
                    val tiledLayer = tiledMap!!.layers[tiledMap!!.layers.size - 1] as TiledLayer

                    // aggiunge il tile all'ultimo layer
                    TMXLayerHelper.addTile(tiledLayer, atts)
                }
            }
            TAG_PROPERTY -> {
                if (inTile) {
                    val tmxTileSets = tiledMap!!.tileSets
                    tmxTileSets[tmxTileSets.size - 1].addTileProperty(lastTileSetTileID, TileProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE)))
                } else if (inObject) {
                    val groups = tiledMap!!.objectGroups
                    val lastGroup = groups[groups.size - 1]
                    val objects = lastGroup.getObjects()
                    objects[objects.size - 1].addProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE))
                } else if (inLayer || inImageLayer || inObjectLayer) {
                    // inserisce le proprietà per i layer
                    tiledMap!!.layers[tiledMap!!.layers.size - 1].addProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE))
                } else if (inMap) {
                    tiledMap!!.addProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE))
                }
            }
            TAG_LAYER -> {
                tiledMap!!.addLayer(TiledLayer(tiledMap, atts))
                inLayer = true
            }
            TAG_TILEOFFSET ->            // recupera l'offset per il tileset (interno ad un tileset)
                if (inTileset) {
                    val tileSet = tiledMap!!.tileSets[tiledMap!!.tileSets.size - 1]
                    tileSet.drawOffsetX = SAXUtil.getInt(atts, LayerAttributes.OFFSET_X, 0)
                    tileSet.drawOffsetY = SAXUtil.getInt(atts, LayerAttributes.OFFSET_Y, 0)
                }
            TAG_IMAGE_LAYER -> {
                // creiamo nuovo image layer
                tiledMap!!.addLayer(ImageLayer(tiledMap, loaderType, atts, textureFilter))
                inImageLayer = true
            }
            TAG_DATA -> {
                inData = true
                encoding = SAXUtil.getString(atts, TAG_DATA_ATTRIBUTE_ENCODING)
                compression = SAXUtil.getString(atts, TAG_DATA_ATTRIBUTE_COMPRESSION)
            }
            TAG_OBJECTGROUP -> {
                tiledMap!!.addObjectGroup(ObjectLayer(tiledMap, atts))
                inObjectLayer = true
            }
            TAG_OBJECT -> {
                inObject = true
                val groups = tiledMap!!.objectGroups
                groups[groups.size - 1].addObject(ObjDefinition.build(atts))
            }
            else -> {}
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        // serve a farlo funzionare anche da junit
        var localName = localName
        if (localName.length == 0) localName = qName
        when (localName) {
            TAG_MAP -> {

                // rimuoviamo tutti gli eventuali layer da rimuovere
                TMXLayerHelper.removePreviewLayer(tiledMap)
                // definisce le texture per ogni tile
                tiledMap!!.assignTextureToLayers(context)

                // vediamo se ci sono animazioni
                TileMapAnimationHelper.buildAnimations(tiledMap)

                // crea le classi di oggetti
                TMXObjectClassHelper.buildObjectClasses(tiledMap)
                inMap = false
            }
            TAG_TILESET -> {
                inTileset = false
            }
            TAG_TILE -> {
                inTile = false
            }
            TAG_LAYER -> {
                inLayer = false
            }
            TAG_IMAGE_LAYER -> {
                inImageLayer = false
            }
            TAG_OBJECTGROUP -> {
                inObjectLayer = false
            }
            TAG_DATA -> {
                val binarySaved = compression != null && encoding != null
                if (binarySaved) {
                    // l'ultimo layer sicuramente è un tiledLayer
                    val tiledLayer = tiledMap!!.layers[tiledMap!!.layers.size - 1] as TiledLayer
                    try {
                        TMXLayerHelper.extract(tiledLayer, characters.toString().trim { it <= ' ' }, encoding, compression)
                    } catch (e: IOException) {
                        throw SAXException(e)
                    }
                    compression = null
                    encoding = null
                }
                inData = false
            }
            TAG_OBJECT -> {
                inObject = false
            }
        }
        characters.setLength(0)
    }

    @Throws(SAXException::class)
    override fun characters(characters: CharArray, start: Int, length: Int) {
        this.characters.append(characters, start, length)
    }

    companion object {
        private const val ATTR_VALUE = "value"
        private const val ATTR_NAME = "name"
        private const val TAG_DATA = "data"
        private const val TAG_DATA_ATTRIBUTE_ENCODING = "encoding"
        private const val TAG_DATA_ATTRIBUTE_COMPRESSION = "compression"
        private const val TAG_IMAGE = "image"
        private const val TAG_LAYER = "layer"
        private const val TAG_IMAGE_LAYER = "imagelayer"
        private const val TAG_MAP = "map"
        private const val TAG_PROPERTY = "property"
        private const val TAG_TILESET = "tileset"
        private const val TAG_TILESET_ATTRIBUTE_SOURCE = "source"
        private const val TAG_TILESET_ATTRIBUTE_FIRSTGID = "firstgid"
        private const val TAG_TILE = "tile"
        private const val TAG_TILE_ATTRIBUTE_ID = "id"
        private const val TAG_IMAGE_ATTRIBUTE_SOURCE = "source"
        private const val TAG_OBJECTGROUP = "objectgroup"
        private const val TAG_OBJECT = "object"
        private const val TAG_TILEOFFSET = "tileoffset"
    }
}