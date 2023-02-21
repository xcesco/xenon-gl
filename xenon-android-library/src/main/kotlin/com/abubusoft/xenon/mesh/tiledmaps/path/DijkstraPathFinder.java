/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.path;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * @author Francesco Benincasa
 * 
 */
public class DijkstraPathFinder implements PathFinder {

	public static final int COST_INFINITE = 1000000;
	public static final int NODE_INVALID = -1;

	protected final static NodeComparatorByCost nodeComparator=new NodeComparatorByCost();

	protected MovementMap map;

	protected HashSet<Node> S;

	protected PriorityQueue<Node> T;

	public DijkstraPathFinder(MovementMap map, boolean oblique) {
		this.map = map;

		S = new HashSet<>();
		T = new PriorityQueue<>(map.nodes.length, nodeComparator);
		
		path=new Path(map);
	}
	
	protected Path path;

	@Override
	public Path findPath(Mover mover, int sKeyNode, int tKeyNode) {
		S.clear();
		T.clear();
		
		S.add(map.nodes[sKeyNode]);

		for (int i = 0; i < map.nodes.length; i++) {
			// dobbiamo prima inserire il costo e poi lo inseriamo
			if (i == sKeyNode) {
				map.nodes[sKeyNode].cost = 0;
			} else {
				map.nodes[i].cost = COST_INFINITE;
			}
			map.nodes[i].parent = NODE_INVALID;

			// tutti i nodi del grafo non sono ottimizzati e quindi stanno in T
			T.add(map.nodes[i]);
		}

		Node u=null;
		int alt=0;
		while (!T.isEmpty()) {
			u = T.poll();
			
			if (u.cost==COST_INFINITE)
			{
				break;
			}
			
			for (int i=0; i<u.arcs.length;i++)
			{
				if (u.arcs[i]==NODE_INVALID) continue;
				// dove 1 è il costo dell'arco
				alt=u.cost+1;
				
				if (alt<map.nodes[u.arcs[i]].cost)
				{
					map.nodes[u.arcs[i]].cost=alt;
					map.nodes[u.arcs[i]].parent=u.id;
					
					T.remove(map.nodes[u.arcs[i]]);
					T.add(map.nodes[u.arcs[i]]);
					
				}
			}
		}
		
		path.clear();
		
		int last=tKeyNode;
		
		u=map.nodes[tKeyNode];
		while (u.parent!=NODE_INVALID)
		{
			last=u.id;
			path.prependStep(u.id);
			u=map.nodes[u.parent];
			
			path.prependMove(MoveType.detect(last, u.id, map.columns));
		}

		return path;
	}

}
