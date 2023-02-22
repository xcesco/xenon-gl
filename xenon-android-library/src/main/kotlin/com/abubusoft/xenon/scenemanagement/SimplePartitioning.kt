package com.abubusoft.xenon.scenemanagement

import com.abubusoft.xenon.ScreenInfo
import com.abubusoft.xenon.android.wallpaper.WallpaperManager
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.core.util.ElioCollection.sort
import com.abubusoft.xenon.entity.Entity
import com.abubusoft.xenon.math.*
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.scenemanagement.internal.ZComparator

/**
 * Un semplice gestore di spazio. Come per il [PlainPartitioning], le
 * entità vengono gestite mediante una semplice lista.
 *
 * Il metodo {@#retrieveZOrderedList()} ordina gli
 * elementi per z e poi analizza il rettangolo che rappresenta la parte di
 * schermo con la bounding sphere dell'entity.
 *
 *
 * @author Francesco Benincasa
 */
class SimplePartitioning<E : Entity<*>?> : SpacePartitioning<E?> {
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.spacepartitioning.SpacePartitioning#clear()
	 */
    override fun clear() {
        list.clear()
        zOrderedList.clear()
        temp.clear()
        tempOrderedList.clear()
    }

    /**
     * lista degli oggetti
     */
    protected var list: MutableList<E?>
    protected var cameraRect: AlignedRectangle2D? = null
    protected var worldPoint1 = Point3()
    protected var worldPoint2 = Point3()
    protected var circle = Circle()

    /**
     * lista degli oggetti ordinati per z
     */
    protected var zOrderedList: ArrayList<E?>

    /**
     * lista degli oggetti ordinati per z
     */
    protected var tempOrderedList: ArrayList<E?>

    /**
     * lista temporanea
     */
    protected var temp: ArrayList<E>

    /**
     * ordinatore
     */
    protected var comparator: ZComparator<E?>
    private var oldWallpaperOffsetX = -1f
    private val pointInPlane = FloatArray(16)

    /**
     * costruttore
     */
    init {
        list = ArrayList()
        zOrderedList = ArrayList()
        comparator = ZComparator()
        temp = ArrayList()
        tempOrderedList = ArrayList()
    }

    /**
     * Aggiunge un elemento alla lista di elementi presenti nel mondo. Tra le
     * varie, invoca il metodo savePosition per memorizzare la posizione inziale
     *
     * @param entity
     */
    override fun add(entity: E?) {
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
     * @throws Exception
     * @throws InstantiationException
     */
    override fun add(position: Point3, shape: Mesh, clazz: Class<E?>): E? {
        var entity: E? = null
        try {
            entity = clazz.newInstance()
            entity!!.mesh = shape
            position.copyInto(entity.position)
            add(entity)
        } catch (e: InstantiationException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return entity
    }

    /**
     * Ordina gli elementi per z e poi analizza il rettangolo che rappresenta la
     * parte di schermo con la bounding sphere dell'entity.
     *
     * @see com.abubusoft.xenon.scenemanagement.SpacePartitioning.retrieveZOrderedList
     */
    override fun retrieveZOrderedList(camera: Camera, screenInfo: ScreenInfo): ArrayList<E?> {

        // definiamo il rect a livello di camera
        if (cameraRect == null) {
            cameraRect = AlignedRectangle2D(camera.position.x, camera.position.y, screenInfo.width.toFloat(), screenInfo.height.toFloat())
        } else {
            cameraRect!!.center.setCoords(camera.position.x, camera.position.y)
            cameraRect!!.width = screenInfo.width.toFloat()
            cameraRect!!.height = screenInfo.height.toFloat()
        }

        // è stato cambiato lo screen?
        if (WallpaperManager.instance().getScreenOffset() !== oldWallpaperOffsetX) {

            // se non cambia nulla, al prossimo ciclo non passiamo per di qua.
            oldWallpaperOffsetX = WallpaperManager.instance().getScreenOffset()

            // Collections.sort(zOrderedList, comparator);
            sort(zOrderedList, comparator)
            tempOrderedList.clear()
            var lastZ = 10f
            var item: E?
            val n = zOrderedList.size
            for (i in 0 until n) {
                item = zOrderedList[i]

                // calcoliamo dimensione camera in base alla distanza z
                if (lastZ < item!!.position.z) {
                    lastZ = item.position.z
                    XenonMath.convertViewToWorldST(camera, worldPoint1, pointInPlane, 0f, 0f, lastZ)
                    XenonMath.convertViewToWorldST(camera, worldPoint2, pointInPlane, screenInfo.width.toFloat(), screenInfo.height.toFloat(), lastZ)
                    cameraRect!!.width = XenonMath.abs(worldPoint2.x - worldPoint1.x)
                    cameraRect!!.height = XenonMath.abs(worldPoint2.y - worldPoint1.y)
                    cameraRect!!.center.x = (worldPoint1.x + worldPoint2.x) / 2
                    cameraRect!!.center.y = (worldPoint1.y + worldPoint2.y) / 2
                }
                circle.center.x = item.position.x
                circle.center.y = item.position.y
                circle.radius = item.boundingRadius

                // vediamo se inserire l'oggetto nella lista degli elementi
                // visibili a schermo
                if (cameraRect!!.intersect(circle)) {
                    tempOrderedList.add(item)
                }
            }
        } else {
            // Se siamo qua, lo screen non è cambiato, dobbiamo solo ordinare
            // gli elementi in base allo z-order
            sort(tempOrderedList, comparator)
        }

        // TODO da ottimizzare
        // time = Timer.now() - time;
        // Logger.debug("CALCOLO "+tempOrderedList.size()+" su "+zOrderedList.size()+" in "+time+" ms.");
        return tempOrderedList
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
        val bounding = Sphere()
        var item: E?
        val n = zOrderedList.size

        // for (E item : tempOrderedList) {
        for (i in 0 until n) {
            item = zOrderedList[i]
            if (item!!.collidable) {
                bounding[item.position] = item.boundingRadius
                if (bounding.intersect(touchPoint)) {
                    return item
                }
            }
        }
        return null
    }
}