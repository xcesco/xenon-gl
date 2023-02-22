/**
 *
 */
package com.abubusoft.xenon.math

import com.abubusoft.xenon.engine.Phase
import com.abubusoft.xenon.engine.SharedData

/**
 * @author Francesco Benincasa
 */
class SharedVector3 : SharedData {
    var values = arrayOfNulls<Vector3>(2)
    override fun update() {
        values[0]!!.copyInto(values[1]!!)
    }

    operator fun get(phase: Phase): Vector3? {
        return values[phase.ordinal]
    }

    fun get(): Vector3? {
        return values[Phase.LOGIC.ordinal]
    }
}