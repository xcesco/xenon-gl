package com.abubusoft.xenon.animations;

import java.util.ArrayList;
import java.util.HashMap;

import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureManager;
import com.abubusoft.xenon.texture.TextureOptions;
import com.abubusoft.xenon.texture.AtlasTexture;
import com.abubusoft.xenon.texture.AtlasTextureOptions;
import com.abubusoft.xenon.texture.TextureRegion;
import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.core.util.IOUtility;

import android.content.Context;

/**
 * Classe di utilitaà per caricare animazioni in formato gdx
 * 
 * @author Francesco Benincasa
 * 
 */
class TextureAnimationLoader {

	/**
	 * <p>
	 * Carica la definizione di una texture animation. L'animazione si compone di 3 risorse: l'atlas, la definizione degli sprite e quella delle animazioni. Dato ad esempio un
	 * baseName <b>heart</b> i nomi dei file dovrebbero essere rispettivamente <b>heart.png</b> <b>heart_sprites.txt</b> e <b>heart_animations.txt</b>
	 * </p>
	 * 
	 * @param context
	 *            context
	 * @param spriteDefinitionResourceId
	 *            resource id della definizione dello sprite
	 * @param animationDefinitionResourceId
	 *            resource id della definizione delle animazioni
	 * @param imageResourceId
	 *            id dell'immagine
	 * @param options
	 *            opzioni per il caricamento della texture
	 * @return atlas
	 */
	static ArrayList<TextureAnimation> loadFromResources(Context context, int spriteDefinitionResourceId, int animationDefinitionResourceId, int imageResourceId, TextureOptions options) {
		try {						
			Texture texture = TextureManager.instance().createTextureFromResourceId(context, imageResourceId, options);
			AtlasTexture atlasTexture = TextureManager.instance().createAtlasTexture(texture, AtlasTextureOptions.build());

			String spriteDefinition = IOUtility.readRawTextFile(context, spriteDefinitionResourceId);
			HashMap<String, TextureRegion> tilesMap = GDXParserHelper.createTiles(spriteDefinition, atlasTexture);

			String animationDefinition = IOUtility.readRawTextFile(context, animationDefinitionResourceId);
			ArrayList<TextureAnimation> animationMap = GDXParserHelper.createAnimations(animationDefinition, tilesMap, atlasTexture);

			return animationMap;
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * <p>
	 * Carica la definizione di una texture animation. L'animazione si compone di 3 file: l'atlas, la definizione degli sprite e quella delle animazioni. Dato ad esempio un
	 * baseName <b>heart</b> i nomi dei file dovrebbero essere rispettivamente <b>heart.png</b> <b>heart.sprites</b> e <b>heart.animations</b>
	 * </p>
	 * 
	 * @param context
	 *            context
	 * @param baseName
	 *            nome di base dell'animazione
	 * @param options
	 *            opzioni per il caricamento della texture
	 * @return atlas
	 */
	static ArrayList<TextureAnimation> loadFromAssets(Context context, String baseName, TextureOptions options) {
		try {

			Texture texture = TextureManager.instance().createTextureFromAssetsFile(context, baseName + ".png", options);
			AtlasTexture atlasTexture = TextureManager.instance().createAtlasTexture(texture, AtlasTextureOptions.build());

			String spriteDefinition = IOUtility.readTextFileFromAssets(context, baseName + ".sprites");
			HashMap<String, TextureRegion> tilesMap = GDXParserHelper.createTiles(spriteDefinition, atlasTexture);

			String animationDefinition = IOUtility.readTextFileFromAssets(context, baseName + ".animations");
			ArrayList<TextureAnimation> animationMap = GDXParserHelper.createAnimations(animationDefinition, tilesMap, atlasTexture);

			return animationMap;
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

}
