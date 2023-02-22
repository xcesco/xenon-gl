package com.abubusoft.xenon.mesh.tiledmaps.path;

import java.util.ArrayList;

/**
 * A path determined by some path finding algorithm. A series of steps from the starting location to the target location. This includes a step for the initial location.
 * 
 * @author Kevin Glass
 */
public class Path {
	
	public void clear() {
		steps.clear();
		moves.clear();
	}

	/** The list of steps building up this path */
	private ArrayList<Integer> steps = new ArrayList<>();
	
	/** The list of steps building up this path */
	private ArrayList<MoveType> moves = new ArrayList<>();

	private MovementMap map;

	/**
	 * Create an empty path
	 */
	public Path(MovementMap map) {
		this.map = map;
	}

	/**
	 * Get the length of the path, i.e. the number of steps
	 * 
	 * @return The number of steps in this path
	 */
	public int size() {
		return steps.size();
	}

	/**
	 * Get the step at a given index in the path
	 * 
	 * @param index
	 *            The index of the step to retrieve. Note this should be >= 0 and < getLength();
	 * @return The step information, the position on the map.
	 */
	public int getStep(int index) {
		return steps.get(index);
	}

	/**
	 * Get the x coordinate for the step at the given index
	 * 
	 * @param index
	 *            The index of the step whose x coordinate should be retrieved
	 * @return The x coordinate at the step
	 */
	public int getX(int index) {
		return getStep(index) % map.columns;
	}

	/**
	 * Get the y coordinate for the step at the given index
	 * 
	 * @param index
	 *            The index of the step whose y coordinate should be retrieved
	 * @return The y coordinate at the step
	 */
	public int getY(int index) {
		return getStep(index) / map.columns;
	}

	/**
	 * Append a step to the path.
	 * 
	 * @param x
	 *            The x coordinate of the new step
	 * @param y
	 *            The y coordinate of the new step
	 */
	void appendStep(int nodeKey) {
		steps.add(nodeKey);
	}
	
	int last;

	/**
	 * Prepend a step to the path.
	 * 
	 * @param x
	 *            The x coordinate of the new step
	 * @param y
	 *            The y coordinate of the new step
	 */
	void prependStep(int nodeKey) {
		steps.add(0, nodeKey);
	}
	
	void prependMove(MoveType move) {
		moves.add(0, move);
	}

	public MoveType getMove(int i) {
		return moves.get(i);
	}
}