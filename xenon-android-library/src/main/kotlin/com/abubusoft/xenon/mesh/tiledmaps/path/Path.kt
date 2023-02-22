package com.abubusoft.xenon.mesh.tiledmaps.path

/**
 * A path determined by some path finding algorithm. A series of steps from the starting location to the target location. This includes a step for the initial location.
 *
 * @author Kevin Glass
 */
class Path
/**
 * Create an empty path
 */(private val map: MovementMap) {
    fun clear() {
        steps.clear()
        moves.clear()
    }

    /** The list of steps building up this path  */
    private val steps = ArrayList<Int>()

    /** The list of steps building up this path  */
    private val moves = ArrayList<MoveType?>()

    /**
     * Get the length of the path, i.e. the number of steps
     *
     * @return The number of steps in this path
     */
    fun size(): Int {
        return steps.size
    }

    /**
     * Get the step at a given index in the path
     *
     * @param index
     * The index of the step to retrieve. Note this should be >= 0 and < getLength();
     * @return The step information, the position on the map.
     */
    fun getStep(index: Int): Int {
        return steps[index]
    }

    /**
     * Get the x coordinate for the step at the given index
     *
     * @param index
     * The index of the step whose x coordinate should be retrieved
     * @return The x coordinate at the step
     */
    fun getX(index: Int): Int {
        return getStep(index) % map.columns
    }

    /**
     * Get the y coordinate for the step at the given index
     *
     * @param index
     * The index of the step whose y coordinate should be retrieved
     * @return The y coordinate at the step
     */
    fun getY(index: Int): Int {
        return getStep(index) / map.columns
    }

    /**
     * Append a step to the path.
     *
     * @param x
     * The x coordinate of the new step
     * @param y
     * The y coordinate of the new step
     */
    fun appendStep(nodeKey: Int) {
        steps.add(nodeKey)
    }

    var last = 0

    /**
     * Prepend a step to the path.
     *
     * @param x
     * The x coordinate of the new step
     * @param y
     * The y coordinate of the new step
     */
    fun prependStep(nodeKey: Int) {
        steps.add(0, nodeKey)
    }

    fun prependMove(move: MoveType?) {
        moves.add(0, move)
    }

    fun getMove(i: Int): MoveType? {
        return moves[i]
    }
}