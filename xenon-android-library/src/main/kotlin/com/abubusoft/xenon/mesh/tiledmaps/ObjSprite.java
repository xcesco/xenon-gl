/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps;

import com.abubusoft.xenon.entity.SpriteEntity;

/**
 * @author Francesco Benincasa
 * 
 */
public class ObjSprite extends ObjBase {
	public ObjSprite() {
		category = CategoryType.SPRITE;
	}

	/**
	 * <p>
	 * sprite entity associato.
	 * </p>
	 */
	public SpriteEntity spriteEntity;

	/**
	 * <p>
	 * Crea un object sprite a partire da un object instance.
	 * </p>
	 * 
	 * @param objSource
	 * @return
	 */
	public static ObjSprite build(ObjDefinition objSource, SpriteEntity entity) {
		ObjSprite ret = new ObjSprite();
		ret.name = objSource.name;
		ret.visible = objSource.visible;
		ret.width = objSource.width;
		ret.height = objSource.height;
		ret.x = objSource.x;
		ret.y = objSource.y;
		ret.properties = objSource.properties;
		ret.spriteEntity=entity;

		return ret;
	}
}
