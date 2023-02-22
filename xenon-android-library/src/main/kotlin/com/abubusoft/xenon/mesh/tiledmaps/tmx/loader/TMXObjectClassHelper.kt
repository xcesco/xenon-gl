/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass;
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.kripton.android.Logger;

import android.graphics.Rect;

/**
 * <p>
 * Classe di supporto per la definizione delle classi di oggettti.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class TMXObjectClassHelper {
	/**
	 * <p>
	 * Prende la {@link TiledMap} appena costruita ed effettua le seguenti operazioni:
	 * </p>
	 * 
	 * <ul>
	 * <li>Cerca le triplette di layer: {@link TMXPredefinedObjectGroups#OBJECTGROUP_CLASSES} (object layer), shapes (tiled layer), parts (object layer): sono i 3 livelli che
	 * consentono di definire delle classi di oggetti. Le triplette possono avere lo stesso suffisso quali ad esempio "tree", o "albero". Gli object layer contengono istanza di
	 * oggetti {@link ObjDefinition}</li>
	 * <li>Ricava da classes la definizione creando quindi degli oggetti {@link ObjClass} partendo dagli {@link ObjDefinition} del object layer class e object class parts. I
	 * nuovi oggetti vengono salvati nella mappa delle classi di oggetti {@link TiledMap#objectClasses}. Il sistema di riferimento dei parts diventa il punto top,left del
	 * {@link ObjDefinition} messo nel object layer class.</li>
	 * <li>Per ogni classe, l'origine diventa il top, left della classe. Per gli object trovati nella definizione dei body, viene salvato il centro del body.
	 * </ul>
	 * 
	 * <p>
	 * La definizione nell'object layer {@link TMXPredefinedObjectGroups#OBJECTGROUP_CLASSES} serve anche a definire l'area nel layer {@link TMXPredefinedLayers#TILEDLAYER_SHAPES}
	 * da cui prendere le varie tile che definiscono lo shape dell'intero oggetto.
	 * </p>
	 * 
	 * @param tiledMap
	 *            mappa da completare
	 */
	public static void buildObjectClasses(TiledMap tiledMap) {
		int n = tiledMap.objectLayers.size();

		ObjectLayer classesOL;
		TiledLayer shapeTL;
		ObjectLayer bodiesOL;

		String classesOLKey;
		String shapesTLKey;
		String bodiesOLKey;

		for (int i = 0; i < n; i++) {
			classesOL = tiledMap.objectLayers.get(i);

			// un OL definisce delle classi se finisce con CLASSES
			// shape e parts sono calcolati di in quanto al posto di avere
			// classes hanno shapes e parts nei nomi.
			if (classesOL.name.endsWith(TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES)) {
				// siamo in presenza di una definizione di classi
				classesOLKey = classesOL.name;

				shapesTLKey = classesOLKey.replace(TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES, TMXPredefinedLayers.TILEDLAYER_SHAPES);
				shapeTL = tiledMap.findLayer(shapesTLKey);

				bodiesOLKey = classesOLKey.replace(TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES, TMXPredefinedObjectGroups.OBJECTGROUP_BODIES);
				bodiesOL = tiledMap.findtObjectGroup(bodiesOLKey);

				if (shapeTL == null || bodiesOL == null) {
					Logger.error("Error with %s class category, with shape layer %s and parts object layer %s", classesOLKey, shapesTLKey, bodiesOLKey);
				} else {
					Logger.info("Find %s class category layer, with shape layer %s and parts object layer %s", classesOLKey, shapesTLKey, bodiesOLKey);

					// iteriamo su tutti gli oggetti presenti nel layer di
					// definizione
					int m = classesOL.getObjects().size();
					ObjDefinition classDefinition;
					ObjClass objectClass;
					Rect rect = new Rect();
					Rect rectIns = new Rect();
					Point2 origin = new Point2();
					ObjDefinition currentBody;

					for (int j = 0; j < m; j++) {
						classDefinition = classesOL.getObjects().get(j);

						objectClass = new ObjClass();
						objectClass.name = classDefinition.name;
						objectClass.properties = classDefinition.properties;
						objectClass.width = classDefinition.width;
						objectClass.height = classDefinition.height;		
						objectClass.type= classDefinition.getProperty("allocation", "STATIC");

						// siamo nel sistema map, impostiamo come origine del sistema di riferimento
						// il top left dell'object class
						origin.setCoords(classDefinition.x, classDefinition.y);
						// origin.addCoords(+objectClass.width*0.5f,+objectClass.height*0.5f);

						Logger.info("Create %s class", objectClass.name);

						// definisce la classe bodiesOL.
						// si parte sempre dal tile più in alto a sx.
						objectClass.shapeLayer = shapeTL;
						objectClass.shapeColOffset = Math.round(classDefinition.x) % tiledMap.tileWidth;
						objectClass.shapeColBegin = Math.round(classDefinition.x) / tiledMap.tileWidth;
						objectClass.shapeColSize = (int) ((classDefinition.width + 0.5f) / tiledMap.tileWidth);

						objectClass.shapeRowBegin = Math.round(classDefinition.y) / tiledMap.tileHeight;
						objectClass.shapeRowSize = (int) ((classDefinition.height + 0.5f) / tiledMap.tileHeight);
						objectClass.shapeRowOffset = Math.round(classDefinition.y) % tiledMap.tileHeight;

						rect.set(Math.round(classDefinition.x), Math.round(classDefinition.y), Math.round(classDefinition.x + classDefinition.width), Math.round(classDefinition.y + classDefinition.height));

						// tutti inizialmente impostati a false
						boolean[] used = new boolean[bodiesOL.objects.size()];

						// carichiamo ora la definizione dei vari oggetti
						int r = bodiesOL.objects.size();
						for (int z = 0; z < r; z++) {
							currentBody = bodiesOL.objects.get(z);
							if (!used[z]) {
								rectIns.set(Math.round(currentBody.x), Math.round(currentBody.y), Math.round(currentBody.x + currentBody.width), Math.round(currentBody.y + currentBody.height));

								if (rect.contains(rectIns)) {
									used[z] = true;

									currentBody.x += currentBody.width * 0.5f;
									currentBody.y += currentBody.height * 0.5f;
									// sempre in versione tiledmap, ma il
									// sistema di riferimento è relativo al
									// centro della definizione dell'oggetto.
									currentBody.x -= origin.x;
									currentBody.y -= origin.y;
									objectClass.parts.add(currentBody);
									Logger.debug("Add body %s", currentBody.name);
								}
							}
						}

						tiledMap.objectClasses.put(classDefinition.name, objectClass);
					}

					// alla fine cancelliamo i tiles che abbiamo utilizzato
					boolean a = tiledMap.removeObjectgroup(classesOLKey);
					boolean b = tiledMap.removeObjectgroup(bodiesOLKey);
					boolean c = tiledMap.removeLayer(shapesTLKey);

					if (a && b && c) {
						Logger.info("Successfully removed class category");
					} else {
						Logger.error("Error in deletion of class");
					}
				}
			}
		}

	}
}
