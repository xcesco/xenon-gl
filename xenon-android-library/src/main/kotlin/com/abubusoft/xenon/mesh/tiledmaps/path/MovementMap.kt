package com.abubusoft.xenon.mesh.tiledmaps.path;

import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;

/**
 * <p>
 * Mappa dei movimenti, ovvero una mappa di nodi/tile per i quali è consentito muovere i player
 * </p>
 * 
 * <p>
 * Definiamo una mappa tmx 5x3.
 * </p>
 * 
 * <img src="doc-files/tiledMap.png"/>
 * 
 * <p>
 * Per esempio abbiamo definito in un layer i tile in marrone.
 * </p>
 * 
 * <p>
 * Ci sono 3 tipi di mappe possibili
 * </p>
 * 
 * <h3>Mappa completa</h3>
 * 
 * <p>
 * E' una mappa che contiene tutte le tile per una mappa.
 * </p>
 * 
 * <img src="doc-files/map_full.png"/>
 * 
 * <h3>Mappa ad esclusione</h3>
 * 
 * <p>
 * Dalla mappa completa vengono rimossi tutti i nodi che sono stat riempiti all'interno di un layer selezionato
 * </p>
 * 
 * <img src="doc-files/map_exclusion.png"/>
 * 
 * <h3>Mappa ad inclusione</h3>
 * 
 * <p>
 * Nella mappa completa vengono inclusi solo i nodi che sono stat riempiti all'interno di un layer selezionato
 * </p>
 * 
 * <img src="doc-files/map_inclusion.png"/>
 * 
 * @author Francesco Benincasa
 * 
 */
public class MovementMap {
	public TiledMap map;

	public static final int DIRECTIONS_4 = 4;
	public static final int INVALID_ARC = -1;

	public MovementMap(TiledMap map) {
		this.map = map;
		columns = map.tileColumns;
		rows = map.tileRows;
	}

	public Node[] nodes;

	public final int columns;
	public final int rows;

	/**
	 * <p>
	 * Verifica se ci sono collegamenti verso il nodo
	 * </p>
	 * 
	 * @param mover
	 * @param keyNode
	 * @return
	 */
	public boolean blocked(Mover mover, int keyNode) {
		Node node = nodes[keyNode];
		for (int i = 0; i < node.arcs.length; i++) {
			if (node.arcs[i] != INVALID_ARC) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the cost of moving through the given tile. This can be used to make certain areas more desirable. A simple and valid implementation of this method would be to return 1
	 * in all cases.
	 * 
	 * @param mover
	 *            The mover that is trying to move across the tile
	 * @param sx
	 *            The x coordinate of the tile we're moving from
	 * @param sy
	 *            The y coordinate of the tile we're moving from
	 * @param tx
	 *            The x coordinate of the tile we're moving to
	 * @param ty
	 *            The y coordinate of the tile we're moving to
	 * @return The relative cost of moving across the given tile
	 */
	public int getCost(Mover mover, int sKeyNode, int tKeyNode) {
		int a = Math.abs(sKeyNode - tKeyNode);

		int yd = a % columns;
		int xd = a / columns;

		return xd + yd;
	}

	/**
	 * Notification that the path finder visited a given tile. This is used for debugging new heuristics.
	 * 
	 * @param x
	 *            The x coordinate of the tile that was visited
	 * @param y
	 *            The y coordinate of the tile that was visited
	 */
	public void pathFinderVisited(int keyNode) {

	}

	public boolean isNodeAccessible(int keyNode) {
		boolean accessible = false;
		accessible = accessible && (nodes[keyNode].arcs[0] != MovementMap.INVALID_ARC);
		accessible = accessible && (nodes[keyNode].arcs[1] != MovementMap.INVALID_ARC);
		accessible = accessible && (nodes[keyNode].arcs[2] != MovementMap.INVALID_ARC);
		accessible = accessible && (nodes[keyNode].arcs[3] != MovementMap.INVALID_ARC);

		return accessible;
	}
}
