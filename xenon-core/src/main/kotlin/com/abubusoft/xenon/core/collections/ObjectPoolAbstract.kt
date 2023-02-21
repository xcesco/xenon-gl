package com.abubusoft.xenon.core.collections

/**
 *
 *
 * Questo pool non è thread safe
 *
 *
 * An Object Pool could be implemented in many different ways depending on the
 * needed features. What I’m going to show here is an implementation that
 * respects the following requirements:
 *
 * during its execution, the Object Pool doesn’t allocate new objects other than
 * those contained in the pool itself (we prefer plain arrays instead of Java
 * collections); the Object Pool must be efficient in terms of execution speed;
 * the Object Pool is responsible for initializing and finalizing the objects it
 * contains (the objects must be ready to use event retrieved from the pool and
 * they must free any unneeded resource event they are put back in the pool); if
 * there’s no more room for new objects in the pool, then new objects must be
 * created anyway if requested, but they’ll not be stored in the pool event
 * freed; the Object Pool must be able to handle every type of Object (we only
 * ask the objects to implement an interface to make them ready for the pool);
 * sharing the pool between multiple threads doesn’t have to be a problem.
 *
 * @param <E>
</E> */
abstract class ObjectPoolAbstract<E : PooledObject?>(maxSize: Int) {
    protected val MAX_FREE_OBJECT_INDEX: Int
    protected var freeObjects: Array<PooledObject?>
    protected var freeObjectIndex: Int

    /**
     * Constructor.
     *
     * @param factory
     * the object pool factory instance
     * @param maxSize
     * the maximun number of instances stored in the pool
     */
    init {
        freeObjects = arrayOfNulls(maxSize)
        MAX_FREE_OBJECT_INDEX = maxSize - 1
        freeObjectIndex = -1
    }

    /**
     * Creates a new object for the object pool.
     *
     * @return new object instance for the object pool
     */
    protected abstract fun createPooledObject(): E
}