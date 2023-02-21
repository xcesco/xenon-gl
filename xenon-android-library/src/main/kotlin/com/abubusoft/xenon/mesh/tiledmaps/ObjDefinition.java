package com.abubusoft.xenon.mesh.tiledmaps;

import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties;
import org.xml.sax.Attributes;

/**
 * <p>
 * Il sistema di coordinate parte da in alto a sx della mappa.
 * </p>
 * 
 * <pre>
 * ( x, y ) -----------------------	( x+width, y )  
 * |                                |
 * |                                |
 * |                                |
 * ( x, y+height ) ---------------- ( x+width, y+height )
 * </pre>
 * 
 * 
 * @author Francesco Benincasa
 * 
 */
public class ObjDefinition extends ObjBase {

	public ObjDefinition() {
		category = CategoryType.DEFINITION;
	}

	/**
	 * <p>
	 * Crea un oggetto partendo dagli attributi definiti nell'xml.
	 * </p>
	 * 
	 * @param attrs
	 * @return definizione dell'oggetto
	 */
	public static ObjDefinition build(Attributes attrs) {
		ObjDefinition result = new ObjDefinition();

		result.name = SAXUtil.getString(attrs, "name");
		result.type = SAXUtil.getString(attrs, "allocation");

		// pur essendo float, li recuperiamo come int, dato che sono memorizzati come interi (coordiante rispetto alla mappa).
		result.x = SAXUtil.getInt(attrs, "x", 0);
		result.y = SAXUtil.getInt(attrs, "y", 0);
		result.width = SAXUtil.getInt(attrs, "width", 0);
		result.height = SAXUtil.getInt(attrs, "height", 0);

		result.visible = SAXUtil.getInt(attrs, TMXPredefinedProperties.VISIBLE, 1) == 1;

		return result;
	}
}
