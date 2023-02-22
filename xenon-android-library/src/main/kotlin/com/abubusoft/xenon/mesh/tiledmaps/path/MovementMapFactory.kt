package com.abubusoft.xenon.mesh.tiledmaps.path

import com.abubusoft.xenon.mesh.tiledmaps.Tile
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap

/**
 *
 *
 * Factory delle mappe dei movimenti, ovvero una mappa di nodi/tile per i quali è consentito muovere i player
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
 *
 *
 * Per la generazione di questi grafici ho usato [jsbin](http://jsbin.com/qohop/latest/edit?js,output)
 *
 *
 *
 * Meglio [jsbin](http://jsbin.com/luxogutija/edit?js,output) con padding 150.
 *
 *
 * @author Francesco Benincasa
 */
object MovementMapFactory {
    /**
     * <h3>Mappa completa</h3>
     *
     *
     *
     * E' una mappa che contiene tutte le tile per una mappa.
     *
     *
     * <img src="doc-files/map_full.png"></img>
     *
     *
     * @param map
     * @return
     */
    fun buildMovementMap(map: TiledMap): MovementMap {
        val mm = MovementMap(map)
        val count = map.tileColumns * map.tileRows
        mm.nodes = arrayOfNulls(count)
        val cols = map.tileColumns
        val nodes = mm.nodes
        for (i in 0 until count) {
            nodes!![i] = Node(i, MovementMap.Companion.DIRECTIONS_4)
            for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                // up
                nodes[i]!!.arcs[0] = if (i - cols >= 0) i - cols else MovementMap.Companion.INVALID_ARC
                // down
                nodes[i]!!.arcs[1] = if (i + cols < count) i + cols else MovementMap.Companion.INVALID_ARC
                // left
                nodes[i]!!.arcs[2] = if ((i - 1) % cols != cols - 1) i - 1 else MovementMap.Companion.INVALID_ARC
                // right
                nodes[i]!!.arcs[3] = if ((i + 1) % cols != 0) i + 1 else MovementMap.Companion.INVALID_ARC
            }
        }
        return mm
    }

    /**
     * <h3>Mappa ad esclusione</h3>
     *
     *
     *
     * Dalla mappa completa vengono rimossi tutti i nodi che sono stat riempiti all'interno di un layer selezionato
     *
     *
     * <img src="doc-files/map_exclusion.png"></img>
     *
     * @param map
     * @param propertyName
     * nome della property della tile da usare
     * @return
     */
    fun buildMovementMapByTileProperties(map: TiledMap, layerName: String?, propertyName: String?): MovementMap {
        val mm = buildMovementMap(map)
        val layer = map.findLayer(layerName)
        val n = layer.tiles.size

        // creiamo il set di nodi da escludere
        val set = HashSet<Int>()
        for (i in 0 until n) {
            if (!Tile.isEmpty(layer.tiles[i])) {
                // inseriamo l'elemento nel set da escludere se è diverso da 0 o non è definito
                if (map.getTileProperty(layer.tiles[i].gid, propertyName, "0") == "0") {
                    set.add(i)
                }
            }
        }

        // ora togliamo dalla mm tutti gli elementi da escludere
        for (i in 0 until n) {
            // togliamo ogni arco uscente dal nodo da escludere
            if (set.contains(i)) {
                for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                    mm.nodes[i]!!.arcs[j] = MovementMap.Companion.INVALID_ARC
                }
            } else {
                for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                    if (set.contains(mm.nodes[i]!!.arcs[j])) {
                        mm.nodes[i]!!.arcs[j] = MovementMap.Companion.INVALID_ARC
                    }
                }
            }
        }
        return mm
    }

    /**
     * <h3>Mappa ad esclusione</h3>
     *
     *
     *
     * Dalla mappa completa vengono rimossi tutti i nodi che sono stat riempiti all'interno di un layer selezionato
     *
     *
     * <img src="doc-files/map_exclusion.png"></img>
     *
     * @param map
     * @param exclusionLayer
     * @return
     */
    fun buildMovementMapByExclusion(map: TiledMap, exclusionLayer: String?): MovementMap {
        val mm = buildMovementMap(map)
        val layer = map.findLayer(exclusionLayer)
        val n = layer.tiles.size

        // creiamo il set di nodi da escludere
        val set = HashSet<Int>()
        for (i in 0 until n) {
            if (!Tile.isEmpty(layer.tiles[i])) {
                set.add(i)
            }
        }

        // ora togliamo dalla mm tutti gli elementi da escludere
        for (i in 0 until n) {
            // togliamo ogni arco uscente dal nodo da escludere
            if (set.contains(i)) {
                for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                    mm.nodes[i]!!.arcs[j] = MovementMap.Companion.INVALID_ARC
                }
            } else {
                for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                    if (set.contains(mm.nodes[i]!!.arcs[j])) {
                        mm.nodes[i]!!.arcs[j] = MovementMap.Companion.INVALID_ARC
                    }
                }
            }
        }
        return mm
    }

    /**
     * <h3>Mappa ad inclusione</h3>
     *
     *
     *
     * Nella mappa completa vengono inclusi solo i nodi che sono stat riempiti all'interno di un layer selezionato
     *
     *
     * <img src="doc-files/map_inclusion.png"></img>
     *
     * @param map
     * @param inclusionLayer
     * @return
     */
    fun buildMovementMapByInclusion(map: TiledMap, inclusionLayer: String?): MovementMap {
        val mm = buildMovementMap(map)
        val layer = map.findLayer(inclusionLayer)
        val n = layer.tiles.size

        // creiamo il set di nodi da includere
        val set = HashSet<Int>()
        for (i in 0 until n) {
            if (!Tile.isEmpty(layer.tiles[i])) {
                set.add(i)
            }
        }

        // ora togliamo dalla mm tutti gli elementi da escludere
        for (i in 0 until n) {
            // togliamo ogni arco uscente dal nodo da escludere
            if (!set.contains(i)) {
                for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                    mm.nodes[i]!!.arcs[j] = MovementMap.Companion.INVALID_ARC
                }
            } else {
                for (j in 0 until MovementMap.Companion.DIRECTIONS_4) {
                    if (!set.contains(mm.nodes[i]!!.arcs[j])) {
                        mm.nodes[i]!!.arcs[j] = MovementMap.Companion.INVALID_ARC
                    }
                }
            }
        }
        return mm
    }
}