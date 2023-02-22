/**
 *
 */
package com.abubusoft.xenon.math

import com.abubusoft.xenon.engine.Phase
import com.abubusoft.xenon.engine.SharedData

/**
 * @author Francesco Benincasa
 */
class SharedPoint2 : SharedData {
    var values = arrayOfNulls<Point2>(2)
    override fun update() {
        values[0]!!.copyInto(values[1]!!)
    }

    operator fun get(phase: Phase): Point2? {
        return values[phase.ordinal]
    }

    fun get(): Point2? {
        return values[Phase.LOGIC.ordinal]
    }
}