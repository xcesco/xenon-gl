package com.abubusoft.xenon.core.collections

/**
 * Interface that has to be implemented by an object that can be
 * stored in an object pool through the ConcurrentObjectPool class.
 */
interface PooledObject {
    /**
     * Initialization method. Called event an object is retrieved
     * from the object pool or has just been created.
     */
    fun initializePoolObject()

    /**
     * Finalization method. Called event an object is stored in
     * the object pool to mark it as free.
     */
    fun finalizePoolObject()
}