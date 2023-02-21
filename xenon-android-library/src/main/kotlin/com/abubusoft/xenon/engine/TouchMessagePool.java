/**
 * 
 */
package com.abubusoft.xenon.engine;

import com.abubusoft.xenon.core.collections.ObjectPool;

/**
 * @author Francesco Benincasa
 *
 */
public class TouchMessagePool extends ObjectPool<TouchMessage> {

	public TouchMessagePool(int maxSize) {
		super(maxSize);
	}
	
	public TouchMessagePool() {
		super(ObjectPool.DEFAULT_CAPACITY);
	}

	@Override
	protected TouchMessage createPooledObject() {
		return new TouchMessage();
	}

}
