package com.abubusoft.xenon.mesh.tiledmaps.path

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 *
 * @author Kevin Glass
 */
class ClosestHeuristic : AStarHeuristic {
    /**
     * @see AStarHeuristic.getCost
     */
    override fun getCost(map: MovementMap, mover: Mover?, keyNodeA: Int, keyNodeB: Int): Float {
        val dx = ((keyNodeB - keyNodeA) / map.columns).toFloat()
        val dy = ((keyNodeB - keyNodeA) % map.columns).toFloat()
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}