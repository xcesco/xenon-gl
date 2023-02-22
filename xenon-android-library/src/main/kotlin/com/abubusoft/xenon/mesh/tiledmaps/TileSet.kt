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
package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.ArrayList;

import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureManager;
import com.abubusoft.xenon.texture.TextureOptions;
import com.abubusoft.xenon.texture.AtlasTexture;
import com.abubusoft.xenon.texture.AtlasTextureOptions;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.MeshTile;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType;
import org.xml.sax.Attributes;

import android.content.Context;
import android.util.SparseArray;

public class TileSet {

	private static final String NAME = "name";
	private static final String TILE_WIDTH = "tilewidth";
	private static final String TILE_HEIGHT = "tileheight";
	private static final String SPACING = "spacing";
	private static final String MARGIN = "margin";

	public final int firstGID;
	public final String name;
	public final int tileWidth;
	public final int tileHeight;

	/**
	 * The spacing in pixels between the tiles in this tileset (applies to the tileset image).
	 */
	public final int spacing;

	/**
	 * The margin around the tiles in this tileset (applies to the tileset image).
	 */
	public final int margin;

	private final Context context;

	private MeshTile sprite;

	/**
	 * texture associata al tile set
	 */
	public AtlasTexture texture;

	/**
	 * immagine da utilizzare come texture
	 */
	public String imageSource;

	private final SparseArray<ArrayList<TileProperty>> tileProperties = new SparseArray<ArrayList<TileProperty>>();

	/**
	 * tipo di caricamento dell'immagine: da asset o da risorsa. A seconda del valore, tratta in modo diverso l'imageSource.
	 */
	private final TMXLoaderType loaderType;

	/**
	 * Opzione per la creazione del texture atlas.
	 */
	private TextureOptions textureOptions;

	/**
	 * <p>
	 * Horizontal offset in pixels dei tiles
	 * </p>
	 * 
	 */
	public int drawOffsetX;

	/**
	 * Vertical offset in pixels (positive is down) dei tiles
	 * 
	 */
	public int drawOffsetY;

	/**
	 * Costruttore. Definisce firstGID, name, tileWidth, tileHeight, spacing, margin,
	 * 
	 * @param firstGID
	 * @param atts
	 * @param loaderTypeValue
	 * @param context
	 */
	public TileSet(int firstGID, Attributes atts, TMXLoaderType loaderTypeValue, Context context, TextureOptions textureOptionsValue) {
		this.firstGID = firstGID;
		this.name = SAXUtil.getString(atts, NAME);
		this.tileWidth = SAXUtil.getInt(atts, TILE_WIDTH);
		this.tileHeight = SAXUtil.getInt(atts, TILE_HEIGHT);
		this.spacing = SAXUtil.getInt(atts, SPACING, 0);
		this.margin = SAXUtil.getInt(atts, MARGIN, 0);
		this.loaderType = loaderTypeValue;
		this.context = context;
		this.textureOptions = textureOptionsValue;
	}

	/**
	 * @param id
	 * @param property
	 */
	public void addTileProperty(int id, TileProperty property) {
		int gid = firstGID + id;
		ArrayList<TileProperty> properties = tileProperties.get(gid);
		if (properties == null) {
			properties = new ArrayList<TileProperty>();
		}
		properties.add(property);
		tileProperties.put(gid, properties);
	}

	/**
	 * Restituisce l'elenco delle proprietà o null se non ci sono proprietà per il tile selezionato.
	 * 
	 * @param gid
	 * @return
	 */
	public ArrayList<TileProperty> getTileProperty(int gid) {
		return tileProperties.get(gid);
	}

	/**
	 * recupera il tile associato al gid
	 * 
	 * @param gid
	 * @return sprite o tile associato
	 */
	public MeshTile getSprite(int gid) {
		sprite = getSprite();

		int index = gid - firstGID;
		int column = index % getCount(texture.info.dimension.width, texture.tileWidth, spacing);
		int row = index / getCount(texture.info.dimension.width, texture.tileWidth, spacing);

		sprite.tileColumnIndex = column;
		sprite.tileRowIndex = row;

		return sprite;
	}

	/**
	 * Recupera l'atlas definito per il tileSet. Se non esiste lo crea, altrimenti lo recupera.
	 * 
	 * @return sprite da utilizzare per definire il tile corrente
	 */
	public MeshTile getSprite() {
		if (texture == null || sprite == null) {
			AtlasTextureOptions tto = AtlasTextureOptions.build();
			tto.tileWidth(tileWidth).tileHeight(tileHeight).margin(margin).spacing(spacing);

			Texture temp = null;
			if (loaderType == TMXLoaderType.ASSET_LOADER) {
				temp = TextureManager.instance().createTextureFromAssetsFile(context, imageSource, textureOptions);
			} else {
				// case RES_LOADER:
				temp = TextureManager.instance().createTextureFromResourceString(context, imageSource, textureOptions);
			}

			texture = TextureManager.instance().createAtlasTexture(temp, tto);
			sprite = MeshFactory.createTile(tileWidth, tileHeight, MeshOptions.build());
			sprite.drawOffsetX = this.drawOffsetX;
			sprite.drawOffsetY = this.drawOffsetY;
		}
		return sprite;
	}

	private int getCount(int total, int unit, int spacing) {
		return (total / (unit + spacing));
	}

}