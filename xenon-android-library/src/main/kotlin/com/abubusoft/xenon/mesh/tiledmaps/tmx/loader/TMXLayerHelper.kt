/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.abubusoft.xenon.mesh.MeshTile;
import com.abubusoft.xenon.mesh.tiledmaps.Layer;
import com.abubusoft.xenon.mesh.tiledmaps.Tile;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.kripton.android.Logger;
import org.xml.sax.Attributes;

import android.util.Base64;
import android.util.Base64InputStream;

/**
 * <p>
 * Helper per caricare una layer da un file tmx.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class TMXLayerHelper {

	/**
	 * <p>
	 * Legge il gid sottoforma di long (comprende i flag di flip)
	 * </p>
	 * 
	 * @param atts
	 */
	public static void addTile(TiledLayer layer, Attributes atts) {
		addTile(layer, SAXUtil.getLong(atts, "gid"));
	}

	/**
	 * <p>
	 * Legge il gid, con i flag di flipping ancora inseriti. Per questo motivo al posto di essere un int è messo come long. In read rawGID verrà trasformato.
	 * </p>
	 * 
	 * @param dataIn
	 * @return
	 * @throws IOException
	 */
	private static long readRawGID(DataInputStream dataIn) throws IOException {
		// devo necessarimente usare long e non int, altrimenti le operazioni di
		// shift mi
		// danno valori negativi!
		long lowestByte = dataIn.read();
		long secondLowestByte = dataIn.read();
		long secondHighestByte = dataIn.read();
		long highestByte = dataIn.read();

		if (lowestByte < 0 || secondLowestByte < 0 || secondHighestByte < 0 || highestByte < 0) {
			throw new IllegalArgumentException("Couldn't read gid from stream.");
		}

		return lowestByte | secondLowestByte << 8 | secondHighestByte << 16 | highestByte << 24;
	}

	/**
	 * <p>
	 * Estrae dalla stringa data in input la definizione delle varie tile.
	 * </p>
	 * 
	 * @param data
	 * @param encoding
	 * @param compression
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void extract(TiledLayer layer, String data, String encoding, String compression) throws IOException {
		DataInputStream dataIn = null;
		try {
			InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

			if (encoding != null && encoding.equals("base64")) {
				in = new Base64InputStream(in, Base64.DEFAULT);
			}
			if (compression != null) {
				if (compression.equals("gzip")) {
					in = new GZIPInputStream(in);

				} else if (compression.equals("zlib")) {
					in = new InflaterInputStream(in);
				} else {
					throw new IllegalArgumentException("compression '" + compression + "' is not supported.");
				}
			}

			dataIn = new DataInputStream(in);

			int expectedTileCount = layer.tileColumns * layer.tileRows;
			while (layer.tileCounter < expectedTileCount) {
				addTile(layer, readRawGID(dataIn));
			}
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
		} finally {
			try {
				if (dataIn!=null) dataIn.close();
			} catch (IOException e) {
				Logger.error(e.toString());
			}
		}
	}

	/**
	 * <p>
	 * Provvede a creare una tile
	 * </p>
	 * 
	 * @param gid
	 */
	private static void addTile(TiledLayer layer, long rawGid) {
		int column = layer.tileCounter % layer.tileColumns;
		int row = layer.tileCounter / layer.tileColumns;

		Tile currentTile = null;

		// inverte per startX
		final long FLIPPED_HORIZONTALLY_FLAG = 0x80000000;

		// inverte per startY
		final long FLIPPED_VERTICALLY_FLAG = 0x40000000;

		// inverte startX con startY
		final long FLIPPED_DIAGONALLY_FLAG = 0x20000000;

		// Read out the flags
		boolean flipped_horizontally = (rawGid & FLIPPED_HORIZONTALLY_FLAG) > 0;
		boolean flipped_vertically = (rawGid & FLIPPED_VERTICALLY_FLAG) > 0;
		boolean flipped_diagonally = (rawGid & FLIPPED_DIAGONALLY_FLAG) > 0;

		rawGid &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);

		int gid = (int) rawGid;
		
		int currentTileIndex=row * layer.tileColumns + column;

		if (gid == 0) {
			layer.tiles[currentTileIndex] = Tile.getEmptyTile(column, row);
			
		} else {
			MeshTile sprite = layer.tiledMap.getSpriteByGID(gid);
			if (sprite == null)
				return;

			// creiamo un tile e lo impostiamo adeguatamente
			currentTile = new Tile(gid, column, row, sprite.tileColumnIndex, sprite.tileRowIndex, (int) sprite.tileWidth, (int) sprite.tileHeight, sprite.drawOffsetX, sprite.drawOffsetY);

			currentTile.horizontalFlip = flipped_horizontally;
			currentTile.verticalFlip = flipped_vertically;
			currentTile.diagonalFlip = flipped_diagonally;

			layer.tiles[currentTileIndex] = currentTile;

			// verifichiamo la dimensione massima dei tile presenti nel layer
			layer.tileWidthMax = Math.max(layer.tileWidthMax,(int)sprite.tileWidth);
			layer.tileHeightMax = Math.max(layer.tileHeightMax,(int)sprite.tileHeight);
		}
		
		// se il layer ha dei tiles con degli offset diversi da quello corrente, lo registriamo
		if (currentTileIndex==0){
			layer.drawOffsetUnique=true;
			layer.drawOffsetX=layer.tiles[currentTileIndex].drawOffsetX;
			layer.drawOffsetY=layer.tiles[currentTileIndex].drawOffsetY;
		}
		else if (layer.drawOffsetUnique && (layer.tiles[currentTileIndex].drawOffsetX!=layer.drawOffsetX || layer.tiles[currentTileIndex].drawOffsetY!=layer.drawOffsetY))
		{
			layer.drawOffsetUnique=false;
		}

		layer.tileCounter++;
	}

	/**
	 * <p>
	 * Rimuove tutti i layer considerati come preview (di imageLayer):
	 * </p>
	 * <ul>
	 * <li>Quelli che contengono nel nome <b>preview</b></li>
	 * <li>Quelli che hanno una proprietà di nome preview = true</li>
	 * </ul>
	 * <p>
	 * Questo metodo rimuove tutti layer da rimuovere, ma i tileset ad essi associati rimangono. Questo perchè nel caso di ImageLayer presumo che i tileSet sono li stessi dei layer
	 * fittizi usati per vedere come rendera.
	 * </p>
	 * 
	 * @param tiledMap
	 */
	public static void removePreviewLayer(TiledMap tiledMap) {
		boolean finito = false;
		Layer current = null;

		while (!finito) {
			finito = true;
			// scorriamo i layer per vedere se ci sono degli elementi da
			// rimuovere
			for (int i = 0; i < tiledMap.layers.size(); i++) {
				current = tiledMap.layers.get(i);

				if (current.isPreviewLayer()) {
					finito = false;
					tiledMap.layers.remove(i);
					break;
				}
			}

		}

	}

}
