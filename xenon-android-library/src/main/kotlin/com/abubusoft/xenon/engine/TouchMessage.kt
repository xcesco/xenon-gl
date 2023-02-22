/**
 *
 */
package com.abubusoft.xenon.engine

import com.abubusoft.xenon.core.collections.PooledObject

/**
 * @author Francesco Benincasa
 */
class TouchMessage : PooledObject {
    var type: TouchType? = null
    var x = 0f
    var y = 0f
    override fun initializePoolObject() {}
    override fun finalizePoolObject() {
        type = null
        x = 0f
        y = 0f
    }
}