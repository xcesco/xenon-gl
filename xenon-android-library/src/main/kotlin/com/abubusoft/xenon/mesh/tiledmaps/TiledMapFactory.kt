package com.abubusoft.xenon.mesh.tiledmaps;

import java.io.IOException;

import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXException;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderHandler;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType;
import com.abubusoft.xenon.texture.TextureFilterType;
import com.abubusoft.xenon.core.util.ResourceUtility;

import android.content.Context;

/**
 * Factory delle tiled map, una mappa suddivisa in mattonelle.
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class TiledMapFactory {

	/**
	 * Carica da una resource
	 * 
	 * @param filename
	 * @param context
	 * @return
	 * @throws TMXException
	 */
	public static TiledMap loadFromResources(Context context, String resourceName, TextureFilterType textureFilter) throws TMXException {
		int resId = ResourceUtility.resolveAddress(context, resourceName);
		TiledMap tiledMap = loadFromResources(context, resId, textureFilter);
		
		// creaiamo shader
		tiledMap.init(context);

		return tiledMap;
	}

	/**
	 * Carica da un
	 * 
	 * @param filename
	 * @param context
	 * @return
	 * @throws TMXException
	 */
	public static TiledMap loadFromAsset(Context context, String filename, TextureFilterType textureFilter) throws TMXException {
		try {
			TMXLoaderHandler loader = new TMXLoaderHandler();
			TiledMap tiledMap = loader.load(context, context.getAssets().open(filename), TMXLoaderType.ASSET_LOADER, textureFilter);
			
			// creaiamo shader
			tiledMap.init(context);

			return tiledMap;
		} catch (IOException e) {
			throw new TMXException(e);
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
	public static TiledMap loadFromResources(Context context, int sourceId, TextureFilterType textureFilter) throws TMXException {
		try {
			TMXLoaderHandler loader = new TMXLoaderHandler();

			TiledMap tiledMap = loader.load(context, context.getResources().openRawResource(sourceId), TMXLoaderType.RES_LOADER, textureFilter);
			
			// creaiamo shader
			tiledMap.init(context);

			return tiledMap;

		} catch (Exception e) {
			throw new TMXException(e);
		}
	}
}