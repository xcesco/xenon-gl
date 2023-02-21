/**
 *
 */
package com.abubusoft.xenon.core.collections

import java.util.*

/**
 * @author Francesco Benincasa
 */
abstract class ObjectBulletPool<E : PooledObject?>(protected var maxObjects: Int) {
    protected var usedObjects: LinkedList<BulletItem<E>>
    protected var freeObjects: LinkedList<BulletItem<E>>
    protected var iteraction: BulletIterator<E>? = null
    protected var freeObjectIndex = -1

    init {
        usedObjects = LinkedList()
        freeObjects = LinkedList()
    }

    /**
     * Creates a new object for the object pool.
     *
     * @return new object instance for the object pool
     */
    abstract fun createPooledObject(): E

    /**
     * Creates a new object or returns a free object from the pool.
     *
     * @return a PoolObject instance already initialized
     */
    @Synchronized
    fun newObject(): E? {
        var obj: E? = null
        if (freeObjectIndex == -1) {
            // There are no free objects so I just
            // create a new object that is not in the pool.
            obj = createPooledObject()
        } else {
            // Get an object from the pool
            //obj = usedObjects.get(freeObjectIndex);
            freeObjectIndex--
        }

        // Initialize the object
        obj!!.initializePoolObject()
        return obj
    }

    /**
     * Stores an object instance in the pool to make it available for a
     * subsequent call to newObject() (the object is considered free).
     *
     * @param obj
     * the object to store in the pool and that will be finalized
     */
    @Synchronized
    fun freeObject(obj: E?) {
        if (obj != null) {
            // Finalize the object
            obj.finalizePoolObject()

            // I can put an object in the pool only if there is still room for
            // it
            if (freeObjectIndex < maxObjects) {
                freeObjectIndex++

                // Put the object in the pool
                //freeObjects.set(freeObjectIndex,obj);
            }
        }
    }

    fun interaction() {
        val n = usedObjects.size
        //BulletStatusType status;
        for (i in 0 until n) {
            //status=iteraction.iteraction(usedObjects);

            /*if (status==BulletStatus.REMOVE)
			{
				
			}*/
        }
    }
}