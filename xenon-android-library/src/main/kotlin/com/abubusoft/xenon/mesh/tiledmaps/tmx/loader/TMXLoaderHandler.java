package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer;
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TileProperty;
import com.abubusoft.xenon.mesh.tiledmaps.TileSet;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes;
import com.abubusoft.xenon.texture.TextureFilterType;
import com.abubusoft.xenon.texture.TextureOptions;
import com.abubusoft.xenon.texture.TextureRepeatType;
import com.abubusoft.kripton.android.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class TMXLoaderHandler extends DefaultHandler {

	private static final String ATTR_VALUE = "value";
	private static final String ATTR_NAME = "name";

	private static final String TAG_DATA = "data";
	private static final String TAG_DATA_ATTRIBUTE_ENCODING = "encoding";
	private static final String TAG_DATA_ATTRIBUTE_COMPRESSION = "compression";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_LAYER = "layer";
	private static final String TAG_IMAGE_LAYER = "imagelayer";
	private static final String TAG_MAP = "map";
	private static final String TAG_PROPERTY = "property";
	private static final String TAG_TILESET = "tileset";
	private static final String TAG_TILESET_ATTRIBUTE_SOURCE = "source";
	private static final String TAG_TILESET_ATTRIBUTE_FIRSTGID = "firstgid";
	private static final String TAG_TILE = "tile";
	private static final String TAG_TILE_ATTRIBUTE_ID = "id";
	private static final String TAG_IMAGE_ATTRIBUTE_SOURCE = "source";

	private static final String TAG_OBJECTGROUP = "objectgroup";
	private static final String TAG_OBJECT = "object";
	private static final String TAG_TILEOFFSET = "tileoffset";

	private StringBuilder characters = new StringBuilder();

	private Context context;
	private TiledMap tiledMap;
	private String encoding;
	private String compression;
	private int lastTileSetTileID;

	private boolean inTileset = false;
	private boolean inTile = false;
	private boolean inData = false;
	private boolean inObject = false;
	private boolean inLayer;
	private boolean inImageLayer;
	private boolean inObjectLayer;
	private boolean inMap;
	/**
	 * indica da dove stiamo caricando i files
	 */
	private TMXLoaderType loaderType;

	/**
	 * opzioni da applicare alla texture
	 */
	private TextureFilterType textureFilter;

	/**
	 * Carica da un input stream
	 * 
	 */
	public TiledMap load(Context context, InputStream inputStream, TMXLoaderType loaderTypeValue, TextureFilterType textureFilterValue) throws TMXException {
		try {
			this.context = context;
			this.loaderType = loaderTypeValue;
			this.textureFilter = textureFilterValue;

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);

			xr.parse(new InputSource(new BufferedInputStream(inputStream)));
		} catch (Exception e) {
			Logger.fatal("%s", e.getMessage());
			throw new TMXException(e);
		}

		return this.tiledMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		// serve a farlo funzionare anche da junit
		if (localName.length() == 0)
			localName = qName;

		switch (localName) {
		case TAG_MAP:
			// definisce una mappa
			tiledMap = new TiledMap(atts);
			inMap = true;
			break;
		case TAG_TILESET:
			// definisce un tileset
			this.inTileset = true;
			String tsxTileSetSource = SAXUtil.getString(atts, TAG_TILESET_ATTRIBUTE_SOURCE);

			// il tileSet è con file esterno, e verrà definito in seguito con il
			// tag image
			if (tsxTileSetSource == null) {
				// il textureRepeat=true serve per le immagini che eventualmente
				// posson essere usate
				// per l'image layer
				this.tiledMap.addTileSet(new TileSet(SAXUtil.getInt(atts, TAG_TILESET_ATTRIBUTE_FIRSTGID), atts, loaderType, context, TextureOptions.build().textureRepeat(TextureRepeatType.REPEAT).textureFilter(textureFilter)));
			}
			break;
		case TAG_IMAGE: {
			// tileSet -> image : definisce l'atlas da caricare
			ArrayList<TileSet> tmxTileSets = this.tiledMap.tileSets;

			String imageName = SAXUtil.getString(atts, TAG_IMAGE_ATTRIBUTE_SOURCE);

			if (inImageLayer) {
				// ASSERT: siamo in imageLayer
				ImageLayer currentImageLayer = (ImageLayer) this.tiledMap.layers.get(tiledMap.layers.size() - 1);
				currentImageLayer.imageSource = imageName;
			} else {
				// ASSERT: siamo in tileSet
				tmxTileSets.get(tmxTileSets.size() - 1).imageSource = imageName;
			}
		}
			break;
		case TAG_TILE:
			this.inTile = true;
			if (inTileset) {
				this.lastTileSetTileID = SAXUtil.getInt(atts, TAG_TILE_ATTRIBUTE_ID);
			} else if (inData) {
				TiledLayer tiledLayer = (TiledLayer) tiledMap.layers.get(tiledMap.layers.size() - 1);

				// aggiunge il tile all'ultimo layer
				TMXLayerHelper.addTile(tiledLayer, atts);
			}
			break;
		case TAG_PROPERTY: {
			if (inTile) {
				ArrayList<TileSet> tmxTileSets = this.tiledMap.tileSets;
				tmxTileSets.get(tmxTileSets.size() - 1).addTileProperty(this.lastTileSetTileID, new TileProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE)));
			} else if (inObject) {
				ArrayList<ObjectLayer> groups = this.tiledMap.getObjectGroups();
				ObjectLayer lastGroup = groups.get(groups.size() - 1);

				ArrayList<ObjDefinition> objects = lastGroup.getObjects();
				objects.get(objects.size() - 1).addProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE));
			} else if (inLayer || inImageLayer || inObjectLayer) {
				// inserisce le proprietà per i layer
				tiledMap.layers.get(tiledMap.layers.size() - 1).addProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE));
			} else if (inMap) {
				tiledMap.addProperty(SAXUtil.getString(atts, ATTR_NAME), SAXUtil.getString(atts, ATTR_VALUE));
			}
		}
			break;
		case TAG_LAYER:
			tiledMap.addLayer(new TiledLayer(this.tiledMap, atts));
			inLayer = true;
			break;
		case TAG_TILEOFFSET:
			// recupera l'offset per il tileset (interno ad un tileset)
			if (inTileset) {
				TileSet tileSet=tiledMap.tileSets.get(this.tiledMap.tileSets.size() - 1);
				tileSet.drawOffsetX = SAXUtil.getInt(atts, LayerAttributes.OFFSET_X, 0);
				tileSet.drawOffsetY = SAXUtil.getInt(atts, LayerAttributes.OFFSET_Y, 0);
			}

			break;
		case TAG_IMAGE_LAYER:
			// creiamo nuovo image layer
			tiledMap.addLayer(new ImageLayer(this.tiledMap, loaderType, atts, textureFilter));
			inImageLayer = true;
			break;
		case TAG_DATA:
			inData = true;
			encoding = SAXUtil.getString(atts, TAG_DATA_ATTRIBUTE_ENCODING);
			compression = SAXUtil.getString(atts, TAG_DATA_ATTRIBUTE_COMPRESSION);
			break;
		case TAG_OBJECTGROUP:
			tiledMap.addObjectGroup(new ObjectLayer(this.tiledMap, atts));
			inObjectLayer = true;
			break;
		case TAG_OBJECT:
			inObject = true;
			ArrayList<ObjectLayer> groups = this.tiledMap.getObjectGroups();
			groups.get(groups.size() - 1).addObject(ObjDefinition.build(atts));
			break;
		default:
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// serve a farlo funzionare anche da junit
		if (localName.length() == 0)
			localName = qName;

		switch (localName) {
		case TAG_MAP: {
			// rimuoviamo tutti gli eventuali layer da rimuovere
			TMXLayerHelper.removePreviewLayer(tiledMap);
			// definisce le texture per ogni tile
			tiledMap.assignTextureToLayers(context);

			// vediamo se ci sono animazioni
			TileMapAnimationHelper.buildAnimations(tiledMap);

			// crea le classi di oggetti
			TMXObjectClassHelper.buildObjectClasses(tiledMap);
			inMap = false;
			break;
		}
		case TAG_TILESET: {
			this.inTileset = false;
			break;
		}
		case TAG_TILE: {
			this.inTile = false;
			break;
		}
		case TAG_LAYER: {
			this.inLayer = false;
			break;
		}
		case TAG_IMAGE_LAYER: {
			this.inImageLayer = false;
			break;
		}
		case TAG_OBJECTGROUP: {
			this.inObjectLayer = false;
			break;
		}
		case TAG_DATA: {
			boolean binarySaved = this.compression != null && this.encoding != null;
			if (binarySaved) {
				// l'ultimo layer sicuramente è un tiledLayer
				TiledLayer tiledLayer = (TiledLayer) this.tiledMap.layers.get(this.tiledMap.layers.size() - 1);
				try {
					TMXLayerHelper.extract(tiledLayer, this.characters.toString().trim(), this.encoding, this.compression);
				} catch (IOException e) {
					throw new SAXException(e);
				}
				this.compression = null;
				this.encoding = null;
			}
			this.inData = false;
			break;
		}
		case TAG_OBJECT: {
			this.inObject = false;
			break;
		}

		}

		this.characters.setLength(0);
	}

	@Override
	public void characters(char[] characters, int start, int length) throws SAXException {
		this.characters.append(characters, start, length);
	}
}
