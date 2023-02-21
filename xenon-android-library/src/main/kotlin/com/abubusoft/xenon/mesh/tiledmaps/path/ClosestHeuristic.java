package com.abubusoft.xenon.mesh.tiledmaps.path;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 * @author Kevin Glass
 */
public class ClosestHeuristic implements AStarHeuristic {
	/**
	 * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
	 */
	public float getCost(MovementMap map, Mover mover, int keyNodeA, int keyNodeB) {		
		float dx = (keyNodeB-keyNodeA)/map.columns;
		float dy = (keyNodeB-keyNodeA)%map.columns;
		
		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));
		
		return result;
	}

}