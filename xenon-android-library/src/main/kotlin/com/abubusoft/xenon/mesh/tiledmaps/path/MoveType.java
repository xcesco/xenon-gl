package com.abubusoft.xenon.mesh.tiledmaps.path;

public enum MoveType {

	UP, RIGHT, DOWN, LEFT;

	public static MoveType detect(int nodeS, int nodeT, int columns) {
		int diff = nodeS - nodeT;
		if (diff == 1) {
			return MoveType.RIGHT;
		} else if (diff == -1) {
			return MoveType.LEFT;
		} else if (diff == -columns) {
			return MoveType.UP;
		} else if (diff == columns) {
			return MoveType.DOWN;
		}
		
		return null;
	}
}
