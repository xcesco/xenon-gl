package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

/**
 *
 *
 * Nomi di objectgroups predefiniti.
 *
 *
 * @author Francesco Benincasa
 */
object TMXPredefinedObjectGroups {
    /**
     *
     *
     * Object layer che contiene le definizioni degli oggetti. In questo livello
     * andiamo a definire i template dei vari oggetti. Gli oggetti in questo
     * layer sono caratterizzati da uno boundary (definito appunto qui), da un
     * body e da un insieme di tile che vanno a definire lo shape dell'oggetto.
     * Questo layer dovrebbe avere due proprietà:
     *
     *
     *  * shapes: nome dell'objectgroup che contiene gli object
     *  * parts: nome del tiled layer che contiene lo shape.
     *
     */
    const val OBJECTGROUP_CLASSES = "classes"

    /**
     *
     *
     * Object layer che contiene i body box2d associati ai template.
     *
     */
    const val OBJECTGROUP_BODIES = "bodies"

    /**
     *
     *
     * object layer contenente i vari sprite e object da disegnare. Contiene le
     * istanza delle varie classi definite in [.OBJECTGROUP_CLASSES]
     *
     */
    const val OBJECTGROUP_OBJECTS = "objects"
}