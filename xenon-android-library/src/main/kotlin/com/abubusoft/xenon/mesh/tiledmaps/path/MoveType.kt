package com.abubusoft.xenon.mesh.tiledmaps.path

enum class MoveType {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        fun detect(nodeS: Int, nodeT: Int, columns: Int): MoveType? {
            val diff = nodeS - nodeT
            if (diff == 1) {
                return RIGHT
            } else if (diff == -1) {
                return LEFT
            } else if (diff == -columns) {
                return UP
            } else if (diff == columns) {
                return DOWN
            }
            return null
        }
    }
}