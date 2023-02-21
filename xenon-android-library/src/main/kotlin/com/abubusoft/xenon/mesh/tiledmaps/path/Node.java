package com.abubusoft.xenon.mesh.tiledmaps.path;

public class Node implements Comparable<Node> {
	
	public final int id;

	public static final int INVALID_PARENT = -1;

	/**
	 * costruttore
	 * @param id
	 * 		id del nodo. Immutabile
	 * @param arcCount
	 */
	public Node(int id, int arcCount) {
		this.id=id;
		arcs = new int[arcCount];
	}

	public int[] arcs;

	/** The path cost for this node */
	public int cost;
	/** The parent of this node, how we reached it in the search */
	public int parent;
	/** The heuristic cost of this node */
	public float heuristic;
	/** The search depth of this node */
	public int depth;

	/**
	 * Set the parent of this node
	 * 
	 * @param parent
	 *            The parent node which lead us to this node
	 * @return The depth we have no reached in searching
	 */
	public int setParent(int parent, int depth) {
		this.depth = depth + 1;
		this.parent = parent;

		return depth;
	}

	@Override
	public int compareTo(Node another) {
		float f = heuristic + cost;
		float of = another.heuristic + another.cost;

		if (f < of) {
			return -1;
		} else if (f > of) {
			return 1;
		} else {
			return 0;
		}
	}
}
