/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import android.graphics.Rect
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.tiledmaps.*

/**
 *
 *
 * Classe di supporto per la definizione delle classi di oggettti.
 *
 *
 * @author Francesco Benincasa
 */
object TMXObjectClassHelper {
    /**
     *
     *
     * Prende la [TiledMap] appena costruita ed effettua le seguenti operazioni:
     *
     *
     *
     *  * Cerca le triplette di layer: [TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES] (object layer), shapes (tiled layer), parts (object layer): sono i 3 livelli che
     * consentono di definire delle classi di oggetti. Le triplette possono avere lo stesso suffisso quali ad esempio "tree", o "albero". Gli object layer contengono istanza di
     * oggetti [ObjDefinition]
     *  * Ricava da classes la definizione creando quindi degli oggetti [ObjClass] partendo dagli [ObjDefinition] del object layer class e object class parts. I
     * nuovi oggetti vengono salvati nella mappa delle classi di oggetti [TiledMap.objectClasses]. Il sistema di riferimento dei parts diventa il punto top,left del
     * [ObjDefinition] messo nel object layer class.
     *  * Per ogni classe, l'origine diventa il top, left della classe. Per gli object trovati nella definizione dei body, viene salvato il centro del body.
     *
     *
     *
     *
     * La definizione nell'object layer [TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES] serve anche a definire l'area nel layer [TMXPredefinedLayers.TILEDLAYER_SHAPES]
     * da cui prendere le varie tile che definiscono lo shape dell'intero oggetto.
     *
     *
     * @param tiledMap
     * mappa da completare
     */
    fun buildObjectClasses(tiledMap: TiledMap?) {
        val n = tiledMap!!.objectLayers.size
        var classesOL: ObjectLayer
        var shapeTL: TiledLayer?
        var bodiesOL: ObjectLayer?
        var classesOLKey: String
        var shapesTLKey: String?
        var bodiesOLKey: String
        for (i in 0 until n) {
            classesOL = tiledMap.objectLayers[i]

            // un OL definisce delle classi se finisce con CLASSES
            // shape e parts sono calcolati di in quanto al posto di avere
            // classes hanno shapes e parts nei nomi.
            if (classesOL.name.endsWith(TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES)) {
                // siamo in presenza di una definizione di classi
                classesOLKey = classesOL.name
                shapesTLKey = classesOLKey.replace(TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES, TMXPredefinedLayers.TILEDLAYER_SHAPES)
                shapeTL = tiledMap.findLayer(shapesTLKey)
                bodiesOLKey = classesOLKey.replace(TMXPredefinedObjectGroups.OBJECTGROUP_CLASSES, TMXPredefinedObjectGroups.OBJECTGROUP_BODIES)
                bodiesOL = tiledMap.findtObjectGroup(bodiesOLKey)
                if (shapeTL == null || bodiesOL == null) {
                    Logger.error("Error with %s class category, with shape layer %s and parts object layer %s", classesOLKey, shapesTLKey, bodiesOLKey)
                } else {
                    Logger.info("Find %s class category layer, with shape layer %s and parts object layer %s", classesOLKey, shapesTLKey, bodiesOLKey)

                    // iteriamo su tutti gli oggetti presenti nel layer di
                    // definizione
                    val m = classesOL.getObjects().size
                    var classDefinition: ObjDefinition
                    var objectClass: ObjClass
                    val rect = Rect()
                    val rectIns = Rect()
                    val origin = Point2()
                    var currentBody: ObjDefinition
                    for (j in 0 until m) {
                        classDefinition = classesOL.getObjects()[j]
                        objectClass = ObjClass()
                        objectClass.name = classDefinition.name
                        objectClass.properties = classDefinition.properties
                        objectClass.width = classDefinition.width
                        objectClass.height = classDefinition.height
                        objectClass.type = classDefinition.getProperty("allocation", "STATIC")

                        // siamo nel sistema map, impostiamo come origine del sistema di riferimento
                        // il top left dell'object class
                        origin.setCoords(classDefinition.x, classDefinition.y)
                        // origin.addCoords(+objectClass.width*0.5f,+objectClass.height*0.5f);
                        Logger.info("Create %s class", objectClass.name)

                        // definisce la classe bodiesOL.
                        // si parte sempre dal tile più in alto a sx.
                        objectClass.shapeLayer = shapeTL
                        objectClass.shapeColOffset = Math.round(classDefinition.x) % tiledMap.tileWidth
                        objectClass.shapeColBegin = Math.round(classDefinition.x) / tiledMap.tileWidth
                        objectClass.shapeColSize = ((classDefinition.width + 0.5f) / tiledMap.tileWidth).toInt()
                        objectClass.shapeRowBegin = Math.round(classDefinition.y) / tiledMap.tileHeight
                        objectClass.shapeRowSize = ((classDefinition.height + 0.5f) / tiledMap.tileHeight).toInt()
                        objectClass.shapeRowOffset = Math.round(classDefinition.y) % tiledMap.tileHeight
                        rect[Math.round(classDefinition.x), Math.round(classDefinition.y), Math.round(classDefinition.x + classDefinition.width)] =
                            Math.round(classDefinition.y + classDefinition.height)

                        // tutti inizialmente impostati a false
                        val used = BooleanArray(bodiesOL.objects.size)

                        // carichiamo ora la definizione dei vari oggetti
                        val r = bodiesOL.objects.size
                        for (z in 0 until r) {
                            currentBody = bodiesOL.objects[z]
                            if (!used[z]) {
                                rectIns[Math.round(currentBody.x), Math.round(currentBody.y), Math.round(currentBody.x + currentBody.width)] =
                                    Math.round(currentBody.y + currentBody.height)
                                if (rect.contains(rectIns)) {
                                    used[z] = true
                                    currentBody.x += currentBody.width * 0.5f
                                    currentBody.y += currentBody.height * 0.5f
                                    // sempre in versione tiledmap, ma il
                                    // sistema di riferimento è relativo al
                                    // centro della definizione dell'oggetto.
                                    currentBody.x -= origin.x
                                    currentBody.y -= origin.y
                                    objectClass.parts.add(currentBody)
                                    Logger.debug("Add body %s", currentBody.name)
                                }
                            }
                        }
                        tiledMap.objectClasses[classDefinition.name] = objectClass
                    }

                    // alla fine cancelliamo i tiles che abbiamo utilizzato
                    val a = tiledMap.removeObjectgroup(classesOLKey)
                    val b = tiledMap.removeObjectgroup(bodiesOLKey)
                    val c = tiledMap.removeLayer(shapesTLKey)
                    if (a && b && c) {
                        Logger.info("Successfully removed class category")
                    } else {
                        Logger.error("Error in deletion of class")
                    }
                }
            }
        }
    }
}