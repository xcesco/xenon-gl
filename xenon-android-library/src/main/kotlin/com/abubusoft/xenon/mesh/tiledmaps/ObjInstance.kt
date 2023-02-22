/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * <p>Rappresenta l'istanza di una obj class
 * @author Francesco Benincasa
 * 
 */
public class ObjInstance extends ObjBase {

	/**
	 * <p>
	 * classe di riferimento
	 * </p>
	 */
	public ObjClass clazz;

	public static ObjInstance buildInstance(String name, ObjClass clazz) {
		ObjInstance result = new ObjInstance();

		result.name = name;
		result.category = CategoryType.INSTANCE;
		result.type = clazz.type;
		result.x = clazz.x;
		result.y = clazz.y;
		result.width = clazz.width;
		result.height = clazz.height;
		result.visible = true;

		result.clazz = clazz;

		return result;
	}

	ObjInstance() {
		category = CategoryType.INSTANCE;
	}
}
