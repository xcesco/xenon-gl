package com.abubusoft.xenon.mesh.tiledmaps.path

class NodeComparatorByCost : Comparator<Node> {
    override fun compare(lhs: Node, rhs: Node): Int {
        return lhs.cost - rhs.cost
    }
}