package com.abubusoft.xenon.mesh.tiledmaps.path

import com.abubusoft.xenon.mesh.tiledmaps.TiledMap

/**
 *
 *
 * Mappa dei movimenti, ovvero una mappa di nodi/tile per i quali è consentito muovere i player
 *
 *
 *
 *
 * Definiamo una mappa tmx 5x3.
 *
 *
 * <img src="doc-files/tiledMap.png"></img>
 *
 *
 *
 * Per esempio abbiamo definito in un layer i tile in marrone.
 *
 *
 *
 *
 * Ci sono 3 tipi di mappe possibili
 *
 *
 * <h3>Mappa completa</h3>
 *
 *
 *
 * E' una mappa che contiene tutte le tile per una mappa.
 *
 *
 * <img src="doc-files/map_full.png"></img>
 *
 * <h3>Mappa ad esclusione</h3>
 *
 *
 *
 * Dalla mappa completa vengono rimossi tutti i nodi che sono stat riempiti all'interno di un layer selezionato
 *
 *
 * <img src="doc-files/map_exclusion.png"></img>
 *
 * <h3>Mappa ad inclusione</h3>
 *
 *
 *
 * Nella mappa completa vengono inclusi solo i nodi che sono stat riempiti all'interno di un layer selezionato
 *
 *
 * <img src="doc-files/map_inclusion.png"></img>
 *
 * @author Francesco Benincasa
 */
class MovementMap(var map: TiledMap) {
    var nodes: Array<Node?>
    val columns: Int
    val rows: Int

    init {
        columns = map.tileColumns
        rows = map.tileRows
    }

    /**
     *
     *
     * Verifica se ci sono collegamenti verso il nodo
     *
     *
     * @param mover
     * @param keyNode
     * @return
     */
    fun blocked(mover: Mover?, keyNode: Int): Boolean {
        val node = nodes[keyNode]
        for (i in node!!.arcs.indices) {
            if (node.arcs[i] != INVALID_ARC) {
                return false
            }
        }
        return true
    }

    /**
     * Get the cost of moving through the given tile. This can be used to make certain areas more desirable. A simple and valid implementation of this method would be to return 1
     * in all cases.
     *
     * @param mover
     * The mover that is trying to move across the tile
     * @param sx
     * The x coordinate of the tile we're moving from
     * @param sy
     * The y coordinate of the tile we're moving from
     * @param tx
     * The x coordinate of the tile we're moving to
     * @param ty
     * The y coordinate of the tile we're moving to
     * @return The relative cost of moving across the given tile
     */
    fun getCost(mover: Mover?, sKeyNode: Int, tKeyNode: Int): Int {
        val a = Math.abs(sKeyNode - tKeyNode)
        val yd = a % columns
        val xd = a / columns
        return xd + yd
    }

    /**
     * Notification that the path finder visited a given tile. This is used for debugging new heuristics.
     *
     * @param x
     * The x coordinate of the tile that was visited
     * @param y
     * The y coordinate of the tile that was visited
     */
    fun pathFinderVisited(keyNode: Int) {}
    fun isNodeAccessible(keyNode: Int): Boolean {
        var accessible = false
        accessible = accessible && nodes[keyNode]!!.arcs[0] != INVALID_ARC
        accessible = accessible && nodes[keyNode]!!.arcs[1] != INVALID_ARC
        accessible = accessible && nodes[keyNode]!!.arcs[2] != INVALID_ARC
        accessible = accessible && nodes[keyNode]!!.arcs[3] != INVALID_ARC
        return accessible
    }

    companion object {
        const val DIRECTIONS_4 = 4
        const val INVALID_ARC = -1
    }
}