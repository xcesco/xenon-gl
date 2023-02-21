/**
 *
 */
package com.abubusoft.xenon.core.collections

/**
 * @author Francesco Benincasa
 */
interface BulletIterator<E : PooledObject?> {
    fun iteraction(item: E, status: BulletStatusType?)
}