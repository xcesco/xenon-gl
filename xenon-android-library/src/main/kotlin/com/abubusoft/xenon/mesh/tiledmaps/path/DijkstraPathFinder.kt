/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.path

import java.util.*

/**
 * @author Francesco Benincasa
 */
class DijkstraPathFinder(protected var map: MovementMap, oblique: Boolean) : PathFinder {
    protected var S: HashSet<Node?>
    protected var T: PriorityQueue<Node?>
    protected var path: Path

    init {
        S = HashSet()
        T = PriorityQueue(map.nodes.size, nodeComparator)
        path = Path(map)
    }

    override fun findPath(mover: Mover?, sKeyNode: Int, tKeyNode: Int): Path? {
        S.clear()
        T.clear()
        S.add(map.nodes[sKeyNode])
        for (i in map.nodes.indices) {
            // dobbiamo prima inserire il costo e poi lo inseriamo
            if (i == sKeyNode) {
                map.nodes[sKeyNode]!!.cost = 0
            } else {
                map.nodes[i]!!.cost = COST_INFINITE
            }
            map.nodes[i]!!.parent = NODE_INVALID

            // tutti i nodi del grafo non sono ottimizzati e quindi stanno in T
            T.add(map.nodes[i])
        }
        var u: Node? = null
        var alt = 0
        while (!T.isEmpty()) {
            u = T.poll()
            if (u!!.cost == COST_INFINITE) {
                break
            }
            for (i in u.arcs.indices) {
                if (u.arcs[i] == NODE_INVALID) continue
                // dove 1 è il costo dell'arco
                alt = u.cost + 1
                if (alt < map.nodes[u.arcs[i]]!!.cost) {
                    map.nodes[u.arcs[i]]!!.cost = alt
                    map.nodes[u.arcs[i]]!!.parent = u.id
                    T.remove(map.nodes[u.arcs[i]])
                    T.add(map.nodes[u.arcs[i]])
                }
            }
        }
        path.clear()
        var last = tKeyNode
        u = map.nodes[tKeyNode]
        while (u!!.parent != NODE_INVALID) {
            last = u.id
            path.prependStep(u.id)
            u = map.nodes[u.parent]
            path.prependMove(MoveType.Companion.detect(last, u!!.id, map.columns))
        }
        return path
    }

    companion object {
        const val COST_INFINITE = 1000000
        const val NODE_INVALID = -1
        protected val nodeComparator = NodeComparatorByCost()
    }
}