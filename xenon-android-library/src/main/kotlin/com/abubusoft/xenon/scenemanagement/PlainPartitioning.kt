package com.abubusoft.xenon.scenemanagement

import com.abubusoft.xenon.ScreenInfo
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.entity.Entity
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.scenemanagement.internal.ZComparator
import java.util.*

/**
 *
 * Un semplice gestore di spazio. Semplicemente contiene un elenco di entità.
 * Internamente le entità vengono gestite con una semplice lista.
 *
 *
 * Il metodo che recupera le entità in base allo z order semplicemente ordina
 * tutte le entità presenti nella scena e le presenta ordinate dal più vicino
 * alla più lontana.
 *
 * @author Francesco Benincasa
 */
class PlainPartitioning<E : Entity<*>?> : SpacePartitioning<E?> {
    protected var selector: BoundingBox2DSelector<E>
    override fun clear() {
        list.clear()
        zOrderedList.clear()
    }

    /**
     * lista degli oggetti
     */
    protected var list: ArrayList<E?>

    /**
     * lista degli oggetti ordinati per z
     */
    protected var zOrderedList: ArrayList<E?>

    /**
     * lista temporanea
     */
    protected var temp: ArrayList<E>

    /**
     * ordinatore
     */
    protected var comparator: ZComparator<*>

    /**
     * Aggiunge un elemento alla lista di elementi presenti nel mondo. Tra le
     * varie, invoca il metodo savePosition per memorizzare la posizione inziale
     *
     * @param entity
     */
    override fun add(entity: E?) {
        isOrderChanged = true
        // salva la posizione
        entity!!.savePosition()
        list.add(entity)
        zOrderedList.add(entity)
    }

    /**
     * Aggiunge un oggetto mesh in una determinata posizione. Tra le varie,
     * invoca il metodo savePosition per memorizzare la posizione inziale.
     *
     * @param position
     * @param mesh
     * @throws IllegalAccessException
     * @throws Exception
     * @throws InstantiationException
     */
    override fun add(position: Point3, shape: Mesh, clazz: Class<E?>): E? {
        isOrderChanged = true
        var entity: E? = null
        try {
            entity = clazz.newInstance()
            entity!!.mesh = shape
            position.copyInto(entity.position)
            add(entity)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return entity
    }

    /**
     * indica se gli elementi hanno cambiato ordine.
     */
    var isOrderChanged = false

    init {
        list = ArrayList()
        zOrderedList = ArrayList()
        comparator = ZComparator<Any?>()
        temp = ArrayList()
        selector = BoundingBox2DSelector()
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.abubusoft.xenon.spacepartitioning.SpacePartitioning#retrieveZOrderedList
	 * ()
	 */
    override fun retrieveZOrderedList(camera: Camera, screenInfo: ScreenInfo): ArrayList<E?> {
        if (isOrderChanged) {
            Collections.sort(zOrderedList, comparator)
        }
        isOrderChanged = false
        return zOrderedList
    }

    /**
     * Partiamo dal presupposto che possiamo utilizzare il frame precedentemente
     * disegnato, quindi vale ancora lo stesso zOrderedList
     *
     * @param touchPoint
     * @return
     */
    override fun retrieveTouchedEntity(touchPoint: Point3?): E? {
        temp.clear()
        selector.setSelectorPoint(touchPoint)
        var item: E?
        val n = zOrderedList.size
        for (i in 0 until n) {
            item = zOrderedList[i]
            // touchPoint.copyTo(zTransformedTouch);
            if (item!!.collidable) {
                if (selector.isSelected(item)) {
                    return item
                }
            }
        }
        return null
    }
}