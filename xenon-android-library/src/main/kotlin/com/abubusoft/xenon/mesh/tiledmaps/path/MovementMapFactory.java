package com.abubusoft.xenon.mesh.tiledmaps.path;

import java.util.HashSet;

import com.abubusoft.xenon.mesh.tiledmaps.Tile;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;

/**
 * <p>
 * Factory delle mappe dei movimenti, ovvero una mappa di nodi/tile per i quali è consentito muovere i player
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
 * <p>
 * Per la generazione di questi grafici ho usato <a href="http://jsbin.com/qohop/latest/edit?js,output">jsbin</a>
 * </p>
 * <p>
 * Meglio <a href="http://jsbin.com/luxogutija/edit?js,output">jsbin</a> con padding 150.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class MovementMapFactory {
	/**
	 * <h3>Mappa completa</h3>
	 * 
	 * <p>
	 * E' una mappa che contiene tutte le tile per una mappa.
	 * </p>
	 * 
	 * <img src="doc-files/map_full.png"/>
	 * 
	 * 
	 * @param map
	 * @return
	 */
	public static MovementMap buildMovementMap(TiledMap map) {
		MovementMap mm = new MovementMap(map);

		int count = map.tileColumns * map.tileRows;
		mm.nodes = new Node[count];

		int cols = map.tileColumns;
		Node[] nodes = mm.nodes;

		for (int i = 0; i < count; i++) {
			nodes[i] = new Node(i, MovementMap.DIRECTIONS_4);

			for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
				// up
				nodes[i].arcs[0] = (i - cols) >= 0 ? (i - cols) : MovementMap.INVALID_ARC;
				// down
				nodes[i].arcs[1] = (i + cols) < count ? (i + cols) : MovementMap.INVALID_ARC;
				// left
				nodes[i].arcs[2] = (i - 1) % cols != cols - 1 ? (i - 1) : MovementMap.INVALID_ARC;
				// right
				nodes[i].arcs[3] = (i + 1) % cols != 0 ? (i + 1) : MovementMap.INVALID_ARC;
			}
		}

		return mm;
	}

	/**
	 * <h3>Mappa ad esclusione</h3>
	 * 
	 * <p>
	 * Dalla mappa completa vengono rimossi tutti i nodi che sono stat riempiti all'interno di un layer selezionato
	 * </p>
	 * 
	 * <img src="doc-files/map_exclusion.png"/>
	 * 
	 * @param map
	 * @param propertyName
	 *            nome della property della tile da usare
	 * @return
	 */
	public static MovementMap buildMovementMapByTileProperties(TiledMap map, String layerName, String propertyName) {
		MovementMap mm = buildMovementMap(map);

		TiledLayer layer = map.findLayer(layerName);
		int n = layer.tiles.length;

		// creiamo il set di nodi da escludere
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < n; i++) {
			if (!Tile.isEmpty(layer.tiles[i])) {
				// inseriamo l'elemento nel set da escludere se è diverso da 0 o non è definito
				if (map.getTileProperty(layer.tiles[i].gid, propertyName, "0").equals("0")) {
					set.add(i);
				}
			}
		}

		// ora togliamo dalla mm tutti gli elementi da escludere
		for (int i = 0; i < n; i++) {
			// togliamo ogni arco uscente dal nodo da escludere
			if (set.contains(i)) {
				for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
					mm.nodes[i].arcs[j] = MovementMap.INVALID_ARC;
				}
			} else {
				for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
					if (set.contains(mm.nodes[i].arcs[j])) {
						mm.nodes[i].arcs[j] = MovementMap.INVALID_ARC;
					}
				}
			}
		}

		return mm;
	}

	/**
	 * <h3>Mappa ad esclusione</h3>
	 * 
	 * <p>
	 * Dalla mappa completa vengono rimossi tutti i nodi che sono stat riempiti all'interno di un layer selezionato
	 * </p>
	 * 
	 * <img src="doc-files/map_exclusion.png"/>
	 * 
	 * @param map
	 * @param exclusionLayer
	 * @return
	 */
	public static MovementMap buildMovementMapByExclusion(TiledMap map, String exclusionLayer) {
		MovementMap mm = buildMovementMap(map);

		TiledLayer layer = map.findLayer(exclusionLayer);
		int n = layer.tiles.length;

		// creiamo il set di nodi da escludere
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < n; i++) {
			if (!Tile.isEmpty(layer.tiles[i])) {
				set.add(i);
			}
		}

		// ora togliamo dalla mm tutti gli elementi da escludere
		for (int i = 0; i < n; i++) {
			// togliamo ogni arco uscente dal nodo da escludere
			if (set.contains(i)) {
				for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
					mm.nodes[i].arcs[j] = MovementMap.INVALID_ARC;
				}
			} else {
				for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
					if (set.contains(mm.nodes[i].arcs[j])) {
						mm.nodes[i].arcs[j] = MovementMap.INVALID_ARC;
					}
				}
			}
		}

		return mm;
	}

	/**
	 * <h3>Mappa ad inclusione</h3>
	 * 
	 * <p>
	 * Nella mappa completa vengono inclusi solo i nodi che sono stat riempiti all'interno di un layer selezionato
	 * </p>
	 * 
	 * <img src="doc-files/map_inclusion.png"/>
	 * 
	 * @param map
	 * @param inclusionLayer
	 * @return
	 */
	public static MovementMap buildMovementMapByInclusion(TiledMap map, String inclusionLayer) {
		MovementMap mm = buildMovementMap(map);

		TiledLayer layer = map.findLayer(inclusionLayer);
		int n = layer.tiles.length;

		// creiamo il set di nodi da includere
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < n; i++) {
			if (!Tile.isEmpty(layer.tiles[i])) {
				set.add(i);
			}
		}

		// ora togliamo dalla mm tutti gli elementi da escludere
		for (int i = 0; i < n; i++) {
			// togliamo ogni arco uscente dal nodo da escludere
			if (!set.contains(i)) {
				for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
					mm.nodes[i].arcs[j] = MovementMap.INVALID_ARC;
				}
			} else {
				for (int j = 0; j < MovementMap.DIRECTIONS_4; j++) {
					if (!set.contains(mm.nodes[i].arcs[j])) {
						mm.nodes[i].arcs[j] = MovementMap.INVALID_ARC;
					}
				}
			}
		}

		return mm;
	}
}
