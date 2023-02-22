package com.abubusoft.xenon.util.adt

import android.util.SparseArray

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 11:51:29 - 20.08.2010
 * @param <T>
</T> */
open class Library<T> {
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    protected val mItems: SparseArray<T>

    // ===========================================================
    // Constructors
    // ===========================================================
    constructor() {
        mItems = SparseArray()
    }

    constructor(pInitialCapacity: Int) {
        mItems = SparseArray(pInitialCapacity)
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    fun put(pID: Int, pItem: T) {
        val existingItem = mItems[pID]
        if (existingItem == null) {
            mItems.put(pID, pItem)
        } else {
            throw IllegalArgumentException("ID: '$pID' is already associated with item: '$existingItem'.")
        }
    }

    fun remove(pID: Int) {
        mItems.remove(pID)
    }

    operator fun get(pID: Int): T {
        return mItems[pID]
    } // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}