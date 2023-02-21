/**
 * 
 */
package com.abubusoft.xenon.animations;

import java.util.ArrayList;

import com.abubusoft.xenon.texture.TextureOptions;

import android.content.Context;

/**
 * @author Francesco Benincasa
 * 
 */
public class TextureAnimationManager extends AbstractAnimationManager<TextureTimeline, TextureAnimation> {

	/**
	 * instanza singleton
	 */
	private final static TextureAnimationManager instance = new TextureAnimationManager();

	/**
	 * pattern singleton
	 * 
	 * @return
	 */
	public static final TextureAnimationManager instance() {
		return instance;
	}

	/**
	 * mappa delle atlas
	 */
	//private HashMap<String, TextureAnimationAtlas> atlasMap;

	private TextureAnimationManager() {
	}

	public void clear() {		
		TextureAnimation item;
		TextureKeyFrame frame;
		
		for (int i=0; i<animationList.size();i++)
		{
			item=animationList.get(i);
			
			for (int j=0; j<item.size();j++)
			{
				frame=item.getFrame(j);
				frame.texture=null;
				frame.textureRegion=null;
			}
		}
		
		timelineMap.clear();
		timelineList.clear();

		animationMap.clear();
		animationList.clear();
	}

	/**
	 * <p>
	 * Carica la definizione di una texture animation. L'animazione si compone di 3 file: l'atlas, la definizione degli sprite e quella delle animazioni. Dato ad esempio un
	 * baseName <b>heart</b> i nomi dei file dovrebbero essere rispettivamente <b>heart.png</b> <b>heart.sprites</b> e <b>heart.animations</b>
	 * </p>
	 * 
	 * <p>
	 * Tutti e tre i file si trovano nella cartella <code>assets</code>.
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
	public void createTextureAnimationAtlasFromAssets(Context context, String baseName, TextureOptions options) {
		setup(TextureAnimationLoader.loadFromAssets(context, baseName, options));
	}

	/**
	 * Carica da file la definizione di animazioni.
	 * 
	 * @param context
	 * @param spriteDefinitionResourceId
	 *            resource Id della definizione degli sprite
	 * @param animationDefinitionResourceId
	 *            resource Id della definizione delle animazioni
	 * @param imageResourceId
	 * @return
	 */
	public void createTextureAnimationAtlasFromResources(Context context, int spriteDefinitionResourceId, int animationDefinitionResourceId, int imageResourceId, TextureOptions options) {
		setup(TextureAnimationLoader.loadFromResources(context, spriteDefinitionResourceId, animationDefinitionResourceId, imageResourceId, options));		
	}

	/**
	 * <p>
	 * Registra l'atlas e gli assegna un uid. Se lo shader non è stato ancora definito, lo definisce
	 * </p>
	 * 
	 * @param atlas
	 * @return
	 */
	void setup(ArrayList<TextureAnimation> atlas) {
		
		TextureAnimation item;
		// creiamo associazione tra animazione ed atlas
		for (int i=0; i<atlas.size();i++) {
			item=atlas.get(i);
			animationMap.put(item.name, item);
			animationList.add(item);
		}

		
	}

}
