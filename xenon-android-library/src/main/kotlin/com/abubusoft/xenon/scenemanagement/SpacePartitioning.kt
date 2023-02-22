/**
 *
 */
package com.abubusoft.xenon.scenemanagement

import com.abubusoft.xenon.ScreenInfo
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.entity.Entity
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.mesh.Mesh

/**
 * Rappresenta un sistema di partizionamento dello spazio 2D/3D.
 *
 * @author Francesco Benincasa
 *
 * @param <E>
</E> */
@Uncryptable
interface SpacePartitioning<E : Entity<*>?> {
    /**
     * azzera lo stato dello space partioning
     */
    fun clear()

    /**
     * Aggiunge un elemento alla lista di elementi presenti nel mondo. Tra le
     * varie, invoca il metodo savePosition per memorizzare la posizione inziale
     *
     * @param entity
     */
    fun add(entity: E)

    /**
     * Aggiunge un oggetto mesh in una determinata posizione. Tra le varie,
     * invoca il metodo savePosition per memorizzare la posizione inziale.
     *
     * @param position
     * @throws Exception
     * @throws InstantiationException
     */
    fun add(position: Point3, shape: Mesh, clazz: Class<E>): E

    /**
     * recupera l'elenco delle entità da visualizzare in ordine di z crescente, ovvero
     * da quello più vicino a quello più distante.
     *
     * @return
     */
    fun retrieveZOrderedList(camera: Camera, screenInfo: ScreenInfo): ArrayList<E>

    /**
     * Partiamo dal presupposto che possiamo utilizzare il frame precedentemente
     * disegnato, quindi vale ancora lo stesso zOrderedList
     *
     * @param touchPoint
     * @return
     */
    fun retrieveTouchedEntity(touchPoint: Point3?): E
}