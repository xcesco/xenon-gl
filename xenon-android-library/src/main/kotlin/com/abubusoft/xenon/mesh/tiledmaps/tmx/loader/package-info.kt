/**
 *
 * Contiene il codice necessario a caricare una tiledMap da un file in formato TMX.
 *
 * @author xcesco
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import com.abubusoft.xenon.math.Point2.setCoords
import android.annotation.SuppressLint
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.TileAnimation
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TileMapAnimationHelper
import com.abubusoft.xenon.mesh.tiledmaps.TileAnimationFrame
import com.abubusoft.xenon.mesh.tiledmaps.Layer.LayerType
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLayerHelper
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil
import kotlin.Throws
import android.util.Base64InputStream
import com.abubusoft.xenon.mesh.MeshTile
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType
import com.abubusoft.xenon.texture.TextureFilterType
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXException
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderHandler
import com.abubusoft.xenon.mesh.tiledmaps.TileSet
import com.abubusoft.xenon.texture.TextureOptions
import com.abubusoft.xenon.texture.TextureRepeatType
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer
import com.abubusoft.xenon.mesh.tiledmaps.TileProperty
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXObjectClassHelper
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedObjectGroups
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedLayers
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass
import com.abubusoft.xenon.math.Point2
