package com.abubusoft.xenon.mesh.tiledmaps.path

class Node(val id: Int, arcCount: Int) : Comparable<Node> {
    var arcs: IntArray

    /** The path cost for this node  */
    var cost = 0

    /** The parent of this node, how we reached it in the search  */
    var parent = 0

    /** The heuristic cost of this node  */
    var heuristic = 0f

    /** The search depth of this node  */
    var depth = 0

    /**
     * costruttore
     * @param id
     * id del nodo. Immutabile
     * @param arcCount
     */
    init {
        arcs = IntArray(arcCount)
    }

    /**
     * Set the parent of this node
     *
     * @param parent
     * The parent node which lead us to this node
     * @return The depth we have no reached in searching
     */
    fun setParent(parent: Int, depth: Int): Int {
        this.depth = depth + 1
        this.parent = parent
        return depth
    }

    override fun compareTo(another: Node): Int {
        val f = heuristic + cost
        val of = another.heuristic + another.cost
        return if (f < of) {
            -1
        } else if (f > of) {
            1
        } else {
            0
        }
    }

    companion object {
        const val INVALID_PARENT = -1
    }
}