package com.abubusoft.xenon.mesh.tiledmaps.tmx

/**
 * Attributi del tag map
 *
 * @author xcesco
 */
object MapAttributes {
    /**
     * The TMX format version, generally 1.0.
     */
    const val VERSION = "version"

    /**
     * Map orientation. Tiled supports "orthogonal", "isometric", "staggered" (since 0.9) and "hexagonal" (since 0.11).
     */
    const val ORIENTATION = "orientation"

    /**
     * The order in which tiles on tile layers are rendered. Valid values are right-down (the default), right-up, left-down and left-up. In all cases, the map is drawn row-by-row. (since 0.10, but only supported for orthogonal maps at the
     * moment)
     */
    const val RENDER_ORDER = "renderorder"

    /**
     * The map width in tiles.
     */
    const val WIDTH = "width"

    /**
     * The map height in tiles.
     */
    const val HEIGHT = "height"

    /**
     * The width of a tile.
     */
    const val TILE_WIDTH = "tilewidth"

    /**
     * The height of a tile.
     */
    const val TILE_HEIGHT = "tileheight"

    /**
     * For staggered and hexagonal maps, determines which axis ("x" or "y") is staggered. (since 0.11)
     *
     * @since 0.11
     */
    const val STAGGER_AXIS = "staggeraxis"

    /**
     * For staggered and hexagonal maps, determines whether the "even" or "odd" indexes along the staggered axis are shifted. (since 0.11)
     *
     * @since 0.11
     */
    const val STAGGER_INDEX = "staggerindex"

    /**
     * The background color of the map. (since 0.9, optional, may include alpha value since 0.15 in the form #AARRGGBB)
     *
     * @since 0.9
     */
    const val BACKGROUND_COLOR = "backgroundcolor"

    /**
     * Stores the next available ID for new objects. This number is stored to prevent reuse of the same ID after objects have been removed. (since 0.11)
     *
     * @since 0.11
     */
    const val NEXT_OBJECT_ID = "nextobjectid"
}