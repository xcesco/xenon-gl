package com.abubusoft.xenon.scenemanagement.internal

import com.abubusoft.xenon.entity.BaseEntity

class ZComparator<E : BaseEntity?> : Comparator<E> {
    /* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    override fun compare(entity1: E, entity2: E): Int {
        return (entity1!!.position.z - entity2!!.position.z).toInt()
    }
}