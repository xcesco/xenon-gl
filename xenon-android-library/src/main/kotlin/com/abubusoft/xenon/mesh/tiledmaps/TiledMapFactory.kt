package com.abubusoft.xenon.mesh.tiledmaps

import android.content.Context
import com.abubusoft.xenon.core.util.ResourceUtility.resolveAddress
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXException
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderHandler
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType
import com.abubusoft.xenon.texture.TextureFilterType
import java.io.IOException

/**
 * Factory delle tiled map, una mappa suddivisa in mattonelle.
 *
 * @author Francesco Benincasa
 */
object TiledMapFactory {
    /**
     * Carica da una resource
     *
     * @param filename
     * @param context
     * @return
     * @throws TMXException
     */
    @Throws(TMXException::class)
    fun loadFromResources(context: Context, resourceName: String?, textureFilter: TextureFilterType?): TiledMap? {
        val resId = resolveAddress(context, resourceName!!)
        val tiledMap = loadFromResources(context, resId, textureFilter)

        // creaiamo shader
        tiledMap!!.init(context)
        return tiledMap
    }

    /**
     * Carica da un
     *
     * @param filename
     * @param context
     * @return
     * @throws TMXException
     */
    @Throws(TMXException::class)
    fun loadFromAsset(context: Context, filename: String?, textureFilter: TextureFilterType?): TiledMap? {
        return try {
            val loader = TMXLoaderHandler()
            val tiledMap = loader.load(context, context.assets.open(filename!!), TMXLoaderType.ASSET_LOADER, textureFilter)

            // creaiamo shader
            tiledMap!!.init(context)
            tiledMap
        } catch (e: IOException) {
            throw TMXException(e)
        }
    }

    /**
     * Carica da una resource
     *
     * @param context
     * @param sourceId
     * @param textureFilter
     * @return
     * @throws TMXException
     */
    @Throws(TMXException::class)
    fun loadFromResources(context: Context, sourceId: Int, textureFilter: TextureFilterType?): TiledMap? {
        return try {
            val loader = TMXLoaderHandler()
            val tiledMap = loader.load(context, context.resources.openRawResource(sourceId), TMXLoaderType.RES_LOADER, textureFilter)

            // creaiamo shader
            tiledMap!!.init(context)
            tiledMap
        } catch (e: Exception) {
            throw TMXException(e)
        }
    }
}