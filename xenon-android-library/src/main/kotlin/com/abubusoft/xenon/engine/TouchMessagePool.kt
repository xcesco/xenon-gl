/**
 *
 */
package com.abubusoft.xenon.engine

import com.abubusoft.xenon.core.collections.ObjectPool

/**
 * @author Francesco Benincasa
 */
class TouchMessagePool : ObjectPool<TouchMessage?> {
    constructor(maxSize: Int) : super(maxSize) {}
    constructor() : super(DEFAULT_CAPACITY) {}

    public override fun createPooledObject(): TouchMessage {
        return TouchMessage()
    }
}