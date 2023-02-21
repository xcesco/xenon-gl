package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

/**
 * <p>
 * Nomi di objectgroups predefiniti.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class TMXPredefinedObjectGroups {
	/**
	 * <p>
	 * Object layer che contiene le definizioni degli oggetti. In questo livello
	 * andiamo a definire i template dei vari oggetti. Gli oggetti in questo
	 * layer sono caratterizzati da uno boundary (definito appunto qui), da un
	 * body e da un insieme di tile che vanno a definire lo shape dell'oggetto.
	 * Questo layer dovrebbe avere due proprietà:
	 * </p>
	 * <ul>
	 * <li>shapes: nome dell'objectgroup che contiene gli object</li>
	 * <li>parts: nome del tiled layer che contiene lo shape.</p>
	 * </ul>
	 */
	public static final String OBJECTGROUP_CLASSES = "classes";

	/**
	 * <p>
	 * Object layer che contiene i body box2d associati ai template.
	 * </p>
	 */
	public static final String OBJECTGROUP_BODIES = "bodies";

	/**
	 * <p>
	 * object layer contenente i vari sprite e object da disegnare. Contiene le
	 * istanza delle varie classi definite in {@link #OBJECTGROUP_CLASSES}
	 * </p>
	 */
	public static final String OBJECTGROUP_OBJECTS = "objects";
}
