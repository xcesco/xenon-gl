package com.abubusoft.xenon.mesh.tiledmaps.path;

import java.util.Comparator;

public class NodeComparatorByCost implements Comparator<Node> {

	@Override
	public int compare(Node lhs, Node rhs) {
		return lhs.cost-rhs.cost;
	}

}
