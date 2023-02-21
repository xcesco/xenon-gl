package com.abubusoft.xenon.scenemanagement.internal;

import java.util.Comparator;

import com.abubusoft.xenon.entity.BaseEntity;

public class ZComparator<E extends BaseEntity> implements Comparator<E> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(E entity1, E entity2) {
		return (int)(entity1.position.z - entity2.position.z);
	}

}
