package com.abubusoft.xenon.core.sensor.orientation.math

import java.io.Serializable
import java.util.concurrent.locks.ReentrantLock

/**
 * @author Leigh Beattie
 *
 * At the moment this is a place holder for objects that can be put in the scene graph. There may be some
 * requirements later specified.
 */
open class Renderable : Serializable {
    //Used in data managemenst and synchronisation. If you make a renderable then you should change this boolean to true.
    protected var dirty = true
    var lock = ReentrantLock()
        protected set

    fun dirty(): Boolean {
        return dirty
    }

    fun setClean() {
        dirty = false
    }

    fun setDirty() {
        dirty = true
    }

    companion object {
        /**
         * ID for serialisation
         */
        private const val serialVersionUID = 6701586807666461858L
    }
}