/*
 * Copyright (c) 2010-2011 e3roid project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of the project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps

import android.content.Context
import android.util.SparseArray
import com.abubusoft.xenon.mesh.MeshFactory.createTile
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.MeshTile
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getInt
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getString
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType
import com.abubusoft.xenon.texture.*
import org.xml.sax.Attributes

class TileSet(val firstGID: Int, atts: Attributes?, loaderTypeValue: TMXLoaderType, context: Context, textureOptionsValue: TextureOptions) {
    val name: String
    val tileWidth: Int
    val tileHeight: Int

    /**
     * The spacing in pixels between the tiles in this tileset (applies to the tileset image).
     */
    val spacing: Int

    /**
     * The margin around the tiles in this tileset (applies to the tileset image).
     */
    val margin: Int
    private val context: Context
    private var sprite: MeshTile? = null

    /**
     * texture associata al tile set
     */
    var texture: AtlasTexture? = null

    /**
     * immagine da utilizzare come texture
     */
    var imageSource: String? = null
    private val tileProperties = SparseArray<ArrayList<TileProperty>>()

    /**
     * tipo di caricamento dell'immagine: da asset o da risorsa. A seconda del valore, tratta in modo diverso l'imageSource.
     */
    private val loaderType: TMXLoaderType

    /**
     * Opzione per la creazione del texture atlas.
     */
    private val textureOptions: TextureOptions

    /**
     *
     *
     * Horizontal offset in pixels dei tiles
     *
     *
     */
    var drawOffsetX = 0

    /**
     * Vertical offset in pixels (positive is down) dei tiles
     *
     */
    var drawOffsetY = 0

    /**
     * Costruttore. Definisce firstGID, name, tileWidth, tileHeight, spacing, margin,
     *
     * @param firstGID
     * @param atts
     * @param loaderTypeValue
     * @param context
     */
    init {
        name = getString(atts!!, NAME)
        tileWidth = getInt(atts, TILE_WIDTH)
        tileHeight = getInt(atts, TILE_HEIGHT)
        spacing = getInt(atts, SPACING, 0)
        margin = getInt(atts, MARGIN, 0)
        loaderType = loaderTypeValue
        this.context = context
        textureOptions = textureOptionsValue
    }

    /**
     * @param id
     * @param property
     */
    fun addTileProperty(id: Int, property: TileProperty) {
        val gid = firstGID + id
        var properties = tileProperties[gid]
        if (properties == null) {
            properties = ArrayList()
        }
        properties.add(property)
        tileProperties.put(gid, properties)
    }

    /**
     * Restituisce l'elenco delle proprietà o null se non ci sono proprietà per il tile selezionato.
     *
     * @param gid
     * @return
     */
    fun getTileProperty(gid: Int): ArrayList<TileProperty> {
        return tileProperties[gid]
    }

    /**
     * recupera il tile associato al gid
     *
     * @param gid
     * @return sprite o tile associato
     */
    fun getSprite(gid: Int): MeshTile? {
        sprite = getSprite()
        val index = gid - firstGID
        val column = index % getCount(texture!!.info.dimension.width, texture!!.tileWidth, spacing)
        val row = index / getCount(texture!!.info.dimension.width, texture!!.tileWidth, spacing)
        sprite!!.tileColumnIndex = column
        sprite!!.tileRowIndex = row
        return sprite
    }

    /**
     * Recupera l'atlas definito per il tileSet. Se non esiste lo crea, altrimenti lo recupera.
     *
     * @return sprite da utilizzare per definire il tile corrente
     */
    fun getSprite(): MeshTile {
        if (texture == null || sprite == null) {
            val tto = AtlasTextureOptions.build()
            tto.tileWidth(tileWidth).tileHeight(tileHeight).margin(margin).spacing(spacing)
            var temp: Texture? = null
            temp = if (loaderType === TMXLoaderType.ASSET_LOADER) {
                TextureManager.instance().createTextureFromAssetsFile(context, imageSource, textureOptions)
            } else {
                // case RES_LOADER:
                TextureManager.instance().createTextureFromResourceString(context, imageSource, textureOptions)
            }
            texture = TextureManager.instance().createAtlasTexture(temp, tto)
            sprite = createTile(tileWidth.toFloat(), tileHeight.toFloat(), MeshOptions.build())
            sprite!!.drawOffsetX = drawOffsetX
            sprite!!.drawOffsetY = drawOffsetY
        }
        return sprite!!
    }

    private fun getCount(total: Int, unit: Int, spacing: Int): Int {
        return total / (unit + spacing)
    }

    companion object {
        private const val NAME = "name"
        private const val TILE_WIDTH = "tilewidth"
        private const val TILE_HEIGHT = "tileheight"
        private const val SPACING = "spacing"
        private const val MARGIN = "margin"
    }
}