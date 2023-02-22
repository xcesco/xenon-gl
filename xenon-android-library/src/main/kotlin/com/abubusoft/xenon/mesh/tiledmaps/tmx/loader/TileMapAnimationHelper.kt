/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

import java.util.HashMap;
import java.util.Map.Entry;

import com.abubusoft.xenon.mesh.tiledmaps.Layer;
import com.abubusoft.xenon.mesh.tiledmaps.TileAnimation;
import com.abubusoft.xenon.mesh.tiledmaps.TileAnimationFrame;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.Layer.LayerType;
import com.abubusoft.xenon.core.XenonRuntimeException;

/**
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class TileMapAnimationHelper {

	/**
	 * Cerca di costruire eventuali animazioni
	 * 
	 * @param tiledMap
	 */
	public static void buildAnimations(TiledMap tiledMap) {
		tiledMap.animations.clear();

		/**
		 * mappa dei layer associati al loro nome
		 */
		HashMap<String, Layer> layersMap = new HashMap<String, Layer>();
		for (Layer item : tiledMap.layers) {
			layersMap.put(item.name, item);
		}

		String animationName;
		TileAnimation animation;
		
		// impostiamo la durata di default di un frame a 100.
		tiledMap.animationFrameDefaultDuration=tiledMap.getPropertyAsLong(TMXPredefinedProperties.ANIMATION_FRAME_DEFAULT_DURATION, 100);

		// per ogni proprietà vediamo se contiene un'animazione
		for (Entry<String, String> item : tiledMap.properties.entrySet()) {
			if (item.getKey().startsWith(TMXPredefinedProperties.ANIMATION_PREFIX) && !item.getKey().equals(TMXPredefinedProperties.ANIMATION_FRAME_DEFAULT_DURATION)) {
				// animation
				animationName = item.getKey().substring(TMXPredefinedProperties.ANIMATION_PREFIX.length());

				// recuperiamo lista di animazioni
				animation = createAnimation(tiledMap, layersMap, animationName, item.getValue());
				animation.start(0);
				tiledMap.animations.add(animation);
			}

		}

	}

	/**
	 * Crea un'animazione
	 * 
	 * @param tiledMap
	 * @param layersMap
	 * @param animationName
	 * @param animationDefinition
	 * @return
	 */
	private static TileAnimation createAnimation(TiledMap tiledMap, HashMap<String, Layer> layersMap, String animationName, String animationDefinition) {		
		TileAnimation animation = new TileAnimation(animationName);
		// assert: abbiamo qualche animazione
		String[] frames = animationDefinition.split(";");

		for (String item : frames) {
			item = item.trim();

			animation.frames.add(createFrame(tiledMap, layersMap, item));
		}

		return animation;
	}

	/**
	 * Crea un frame di un'animazione
	 * 
	 * @param tiledMap
	 * @param frames
	 * @param layersMap
	 * @return
	 */
	private static TileAnimationFrame createFrame(TiledMap tiledMap, HashMap<String, Layer> layersMap, String frameDefinition) {
		TileAnimationFrame frame;
		// non abbiamo ancora inserito l'animazione, quindi quello che segue è l'indice dell'animazione
		int animationIndex=tiledMap.animations.size();
		String[] values = frameDefinition.split("=");

		Layer tempLayer = layersMap.get(values[0].trim());
		
		if (tempLayer.type!=LayerType.TILED) throw new XenonRuntimeException("For an animation can not use an image layer");
		TiledLayer layer = (TiledLayer) tempLayer;
		layer.animationOwnerIndex=animationIndex;

		frame = new TileAnimationFrame(layer, tiledMap.animationFrameDefaultDuration);
		if (values.length > 1) {
			frame.duration = Long.parseLong(values[1].trim());
		}

		return frame;
	}

}
