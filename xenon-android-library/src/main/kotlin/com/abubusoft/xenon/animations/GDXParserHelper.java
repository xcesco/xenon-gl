package com.abubusoft.xenon.animations;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;

import com.abubusoft.xenon.texture.AtlasTexture;
import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureRegion;

/**
 * Analizza la definizione di uno sprite
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressLint("DefaultLocale")
class GDXParserHelper {

	private static final float FIXED_POINT_FLOAT_MULTI = 100000.0f;
	private static final int FIXED_POINT_INT_MULTI = 100000;

	private final static int SPRITE_NAME_INDEX = 0;
	private final static int SPRITE_XY_INDEX = 2;
	private final static int SPRITE_SIZE_INDEX = 3;

	/**
	 * <p>
	 * Data una texture e la definizione degli sprite, crea una mappa di tile, la cui chiave è data dal nome dello sprite.
	 * </p>
	 * 
	 * <p>
	 * Le tile definite devono avere un id > 0, altrimenti verranno considerate come tile nulle.
	 * </p>
	 * 
	 * @param input
	 * @param texture
	 * @return
	 */
	static HashMap<String, TextureRegion> createTiles(String input, AtlasTexture texture) {

		HashMap<String, TextureRegion> map = new HashMap<>();
		TextureRegion tile = null;

		String[] definition = input.split("\n");
		String[] temp;
		String name;
		int xy1, xy2;
		int size1, size2;
		float tempX1, tempX2, tempY1, tempY2;
		
		int atlasRow, atlasColumn, height, width;
		
		for (int i = 4; i < definition.length; i += 7) {
			name = definition[i + SPRITE_NAME_INDEX].trim();
			temp = definition[i + SPRITE_XY_INDEX].replace("xy:", "").trim().split(",");
			xy1 = Integer.valueOf(temp[0].trim());
			xy2 = Integer.valueOf(temp[1].trim());

			temp = definition[i + SPRITE_SIZE_INDEX].replace("size:", "").trim().split(",");
			size1 = Integer.valueOf(temp[0].trim());
			size2 = Integer.valueOf(temp[1].trim());

			/*
			 * tile = new Tile(c++, -1, -1, (xy1 / size1), (xy2 / size2), size1, size2); // calcolo con fixed point tempY1 = ((tile.atlasRow * tile.height)) * FIXED_POINT_INT_MULTI
			 * / texture.info.dimension.height; tempY2 = tempY1 + ((tile.height) * FIXED_POINT_INT_MULTI / texture.info.dimension.height);
			 * 
			 * tempY1 += FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height); tempY2 -= FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height);
			 * 
			 * // startX tempX1 = (((xy1 / size1) * tile.width)) * FIXED_POINT_INT_MULTI / texture.info.dimension.width; tempX2 = tempX1 + ((tile.width) * FIXED_POINT_INT_MULTI /
			 * texture.info.dimension.width);
			 * 
			 * tempX1 += FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width); tempX2 -= FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width);
			 * 
			 * tile.textureLowY = (float) tempY1 / FIXED_POINT_FLOAT_MULTI; tile.textureHighY = (float) tempY2 / FIXED_POINT_FLOAT_MULTI;
			 * 
			 * tile.textureLowX = (float) tempX1 / FIXED_POINT_FLOAT_MULTI; tile.textureHighX = (float) tempX2 / FIXED_POINT_FLOAT_MULTI;
			 */
			
			// calcolo posizione
			atlasColumn=(xy1 / size1);
			atlasRow=(xy2 / size2);
			width=size1;
			height=size2;

			tile = new TextureRegion();

			// calcolo con fixed point
			tempY1 = ((atlasRow * height)) * FIXED_POINT_INT_MULTI / texture.info.dimension.height;
			tempY2 = tempY1 + ((height) * FIXED_POINT_INT_MULTI / texture.info.dimension.height);

			tempY1 += FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height);
			tempY2 -= FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height);

			// startX
			tempX1 = (((xy1 / size1) * width)) * FIXED_POINT_INT_MULTI / texture.info.dimension.width;
			tempX2 = tempX1 + ((width) * FIXED_POINT_INT_MULTI / texture.info.dimension.width);

			tempX1 += FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width);
			tempX2 -= FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width);

			tile.lowY = (float) tempY1 / FIXED_POINT_FLOAT_MULTI;
			tile.highY = (float) tempY2 / FIXED_POINT_FLOAT_MULTI;

			tile.lowX = (float) tempX1 / FIXED_POINT_FLOAT_MULTI;
			tile.highX = (float) tempX2 / FIXED_POINT_FLOAT_MULTI;

			map.put(name, tile);
		}

		return map;
	}

	/**
	 * <p>
	 * Crea un'animazione a partire dalla definizione che si trova nella stringa passata come input. La tileMap contiene la definizione dei frame da utilizzare per l'animazione.
	 * </p>
	 * 
	 * @param input
	 * @param tilesMap
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	static ArrayList<TextureAnimation> createAnimations(String input, HashMap<String, TextureRegion> tilesMap, Texture texture) {
		ArrayList<TextureAnimation> list=new ArrayList<>();
		
		String name = null;
		TextureAnimation definition = null;
		TextureKeyFrame frame = null;
		String[] inputDefinition = input.split("\n");
		String row;
		String[] temp;

		for (int i = 0; i < inputDefinition.length; i++) {
			row = inputDefinition[i].trim();
			if ("{".equals(row)) {
				definition = new TextureAnimation();
				definition.name = name;
			} else if ("}".equals(row)) {
				//map.put(name, definition);
				list.add(definition);
			} else if (row.startsWith("looping:")) {
				row = row.replace("looping:", "").trim();

				if ("true".equalsIgnoreCase(row))
					definition.loop = true;

			} else if (row.startsWith("frame:")) {
				row = row.replace("frame:", "").trim();
				temp = row.split(",");

				frame = new TextureKeyFrame();

				// inseriamo il nome del frame, il tile relativo e la durata
				frame.name = temp[0].trim().toLowerCase();
				frame.duration = Long.parseLong(temp[3].trim());

				frame.texture=texture;
				frame.textureRegion = tilesMap.get(frame.name);

				definition.frames.add(frame);
			} else {
				name = inputDefinition[i].trim().toLowerCase();
			}
		}

		return list;
	}

}
