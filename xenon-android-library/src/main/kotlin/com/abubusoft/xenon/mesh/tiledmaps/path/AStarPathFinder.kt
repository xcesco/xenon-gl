package com.abubusoft.xenon.mesh.tiledmaps.path

import java.util.*

/**
 * A path finder implementation that uses the AStar heuristic based algorithm
 * to determine a path.
 *
 * @author Kevin Glass
 */
class AStarPathFinder/*nodes = new Node[map.getWidthInTiles()][map.getHeightInTiles()];
		for (int x=0;x<map.getWidthInTiles();x++) {
			for (int y=0;y<map.getHeightInTiles();y++) {
				nodes[x][y] = new Node(x,y);
			}
		}*/
/**
 * Create a path finder with the default heuristic - closest to target.
 *
 * @param map The map to be searched
 * @param maxSearchDistance The maximum depth we'll search before giving up
 * @param allowDiagMovement True if the search should try diaganol movement
 */ @JvmOverloads constructor(
    /** The map being searched  */
    private val map: MovementMap,
    /** The maximum depth of search we're willing to accept before giving up  */
    private val maxSearchDistance: Int,
    /** True if we allow diaganol movement  */
    private val allowDiagMovement: Boolean,
    /** The heuristic we're applying to determine which nodes to search first  */
    private val heuristic: AStarHeuristic = ClosestHeuristic()
) : PathFinder {
    /** The set of nodes that have been searched through  */
    private val closed = ArrayList<Int>()

    /** The set of nodes that we do not yet consider fully searched  */ //private SortedList open = new SortedList();
    private val open = PriorityQueue<Int>()
    /**
     * Create a path finder
     *
     * @param heuristic The heuristic used to determine the search order of the map
     * @param map The map to be searched
     * @param maxSearchDistance The maximum depth we'll search before giving up
     * @param allowDiagMovement True if the search should try diaganol movement
     */
    /**
     * @see PathFinder.findPath
     */
    override fun findPath(mover: Mover?, sKeyNode: Int, tKeyNode: Int): Path? {
        // easy first check, if the destination is blocked, we can't get there
        var tKeyNode = tKeyNode
        if (map.blocked(mover, tKeyNode)) {
            return null
        }

        // initial state for A*. The closed group is empty. Only the starting

        // tile is in the open list and it'e're already there
        map.nodes[sKeyNode]!!.cost = 0
        map.nodes[sKeyNode]!!.depth = 0
        closed.clear()
        open.clear()
        //open.add(nodes[sKeyNode]);
        open.add(sKeyNode)
        map.nodes[tKeyNode]!!.parent = Node.Companion.INVALID_PARENT
        var currentNode: Node?
        var currentKey: Int

        // while we haven'n't exceeded our max search depth
        var maxDepth = 0
        while (maxDepth < maxSearchDistance && open.size != 0) {
            // pull out the first node in our open list, this is determined to 

            // be the most likely to be the next step based on our heuristic
            currentKey = firstInOpen
            currentNode = map.nodes[currentKey]

            //if (current == nodes[tx][ty]) {
            if (currentNode === map.nodes[tKeyNode]) {
                break
            }
            removeFromOpen(currentKey)
            addToClosed(currentKey)

            // search through all the neighbours of the current node evaluating

            // them as next steps
            var pKeyNode: Int
            for (x in -1..1) {
                for (y in -1..1) {
                    // not a neighbour, its the current tile
                    if (x == 0 && y == 0) {
                        continue
                    }

                    // if we're not allowing diaganol movement then only 

                    // one of x or y can be set
                    if (!allowDiagMovement) {
                        if (x != 0 && y != 0) {
                            continue
                        }
                    }

                    // determine the location of the neighbour and evaluate it
                    //int xp = x + current.x;
                    //int yp = y + current.y;
                    pKeyNode = x + y * map.columns + currentKey
                    if (isValidLocation(mover, sKeyNode, pKeyNode)) {
                        // the cost to get to this node is cost the current plus the movement

                        // cost to reach this node. Note that the heursitic value is only used

                        // in the sorted open list
                        val nextStepCost = currentNode!!.cost + getMovementCost(mover, currentKey, pKeyNode)
                        val neighbourKey = pKeyNode
                        val neighbour = map.nodes[pKeyNode] // nodes[xp][yp];
                        //map.pathFinderVisited(xp, yp);
                        map.pathFinderVisited(pKeyNode)

                        // if the new cost we've determined for this node is lower than 

                        // it has been previously makes sure the node hasn'e've
                        // determined that there might have been a better path to get to

                        // this node so it needs to be re-evaluated
                        if (nextStepCost < neighbour!!.cost) {
                            if (inOpenList(neighbourKey)) {
                                removeFromOpen(neighbourKey)
                            }
                            if (inClosedList(neighbourKey)) {
                                removeFromClosed(neighbourKey)
                            }
                        }

                        // if the node hasn't already been processed and discarded then

                        // reset it's cost to our current cost and add it as a next possible

                        // step (i.e. to the open list)
                        if (!inOpenList(neighbourKey) && !inClosedList(neighbourKey)) {
                            neighbour.cost = nextStepCost
                            neighbour.heuristic = getHeuristicCost(mover, pKeyNode, tKeyNode)
                            maxDepth = Math.max(maxDepth, neighbour.setParent(currentKey, currentNode.depth))
                            addToOpen(neighbourKey)
                        }
                    }
                }
            }
        }

        // since we'e've run out of search 
        // there was no path. Just return null
        if (map.nodes[tKeyNode]!!.parent == Node.Companion.INVALID_PARENT) {
            return null
        }

        // At this point we've definitely found a path so we can uses the parent

        // references of the nodes to find out way from the target location back

        // to the start recording the nodes on the way.
        val path = Path(map)
        var target = map.nodes[tKeyNode]
        while (target !== map.nodes[sKeyNode]) {
            path.prependStep(tKeyNode)
            tKeyNode = target!!.parent
            target = map.nodes[tKeyNode]
        }
        path.prependStep(sKeyNode)

        // thats it, we have our path 
        return path
    }

    /**
     * Get the first element from the open list. This is the next
     * one to be searched.
     *
     * @return The first element in the open list
     */
    protected val firstInOpen: Int
        protected get() = open.peek()

    /**
     * Add a node to the open list
     *
     * @param node The node to be added to the open list
     */
    protected fun addToOpen(nodeKey: Int) {
        open.add(nodeKey)
    }

    /**
     * Check if a node is in the open list
     *
     * @param node The node to check for
     * @return True if the node given is in the open list
     */
    protected fun inOpenList(nodeKey: Int): Boolean {
        return open.contains(nodeKey)
    }

    /**
     * Remove a node from the open list
     *
     * @param node The node to remove from the open list
     */
    protected fun removeFromOpen(nodeKey: Int) {
        open.remove(nodeKey)
    }

    /**
     * Add a node to the closed list
     *
     * @param node The node to add to the closed list
     */
    protected fun addToClosed(nodeKey: Int) {
        closed.add(nodeKey)
    }

    /**
     * Check if the node supplied is in the closed list
     *
     * @param node The node to search for
     * @return True if the node specified is in the closed list
     */
    protected fun inClosedList(nodeKey: Int): Boolean {
        return closed.contains(nodeKey)
    }

    /**
     * Remove a node from the closed list
     *
     * @param node The node to remove from the closed list
     */
    protected fun removeFromClosed(nodeKey: Int) {
        closed.removeAt(nodeKey)
    }

    /**
     * Check if a given location is valid for the supplied mover
     *
     * @param mover The mover that would hold a given location
     * @param sx The starting x coordinate
     * @param sy The starting y coordinate
     * @param x The x coordinate of the location to check
     * @param y The y coordinate of the location to check
     * @return True if the location is valid for the given mover
     */
    protected fun isValidLocation(mover: Mover?, sKeyNode: Int, tKeyNode: Int): Boolean {
        val x = tKeyNode % map.columns
        val y = tKeyNode / map.columns
        var invalid = x < 0 || y < 0 || x >= map.columns || y >= map.rows
        val sx = sKeyNode % map.columns
        val sy = sKeyNode / map.columns
        if (!invalid && (sx != x || sy) != y) {
            invalid = map.blocked(mover, tKeyNode)
        }
        return !invalid
    }

    /**
     * Get the cost to move through a given location
     *
     * @param mover The entity that is being moved
     * @param sx The x coordinate of the tile whose cost is being determined
     * @param sy The y coordiante of the tile whose cost is being determined
     * @param tx The x coordinate of the target location
     * @param ty The y coordinate of the target location
     * @return The cost of movement through the given tile
     */
    fun getMovementCost(mover: Mover?, sKeyNode: Int, tKeyNode: Int): Int {
        return map.getCost(mover, sKeyNode, tKeyNode)
    }

    /**
     * Get the heuristic cost for the given location. This determines in which
     * order the locations are processed.
     *
     * @param mover The entity that is being moved
     * @param x The x coordinate of the tile whose cost is being determined
     * @param y The y coordiante of the tile whose cost is being determined
     * @param tx The x coordinate of the target location
     * @param ty The y coordinate of the target location
     * @return The heuristic cost assigned to the tile
     */
    fun getHeuristicCost(mover: Mover?, sKeyNode: Int, tKeyNode: Int): Float {
        return heuristic.getCost(map, mover, sKeyNode, tKeyNode)
    }
}