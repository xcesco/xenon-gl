package com.abubusoft.xenon.mesh.tiledmaps.tmx;

/**
 * Attributi del tag map
 * 
 * @author xcesco
 *
 */
public class MapAttributes {

	/**
	 * The TMX format version, generally 1.0.
	 */
	public static final String VERSION = "version";

	/**
	 * Map orientation. Tiled supports "orthogonal", "isometric", "staggered" (since 0.9) and "hexagonal" (since 0.11).
	 */
	public static final String ORIENTATION = "orientation";

	/**
	 * The order in which tiles on tile layers are rendered. Valid values are right-down (the default), right-up, left-down and left-up. In all cases, the map is drawn row-by-row. (since 0.10, but only supported for orthogonal maps at the
	 * moment)
	 */
	public static final String RENDER_ORDER = "renderorder";

	/**
	 * The map width in tiles.
	 */
	public static final String WIDTH = "width";

	/**
	 * The map height in tiles.
	 */
	public static final String HEIGHT = "height";

	/**
	 * The width of a tile.
	 */
	public static final String TILE_WIDTH = "tilewidth";

	/**
	 * The height of a tile.
	 */
	public static final String TILE_HEIGHT = "tileheight";

	/**
	 * For staggered and hexagonal maps, determines which axis ("x" or "y") is staggered. (since 0.11)
	 * 
	 * @since 0.11
	 */
	public static final String STAGGER_AXIS = "staggeraxis";

	/**
	 * For staggered and hexagonal maps, determines whether the "even" or "odd" indexes along the staggered axis are shifted. (since 0.11)
	 * 
	 * @since 0.11
	 */
	public static final String STAGGER_INDEX = "staggerindex";

	/**
	 * The background color of the map. (since 0.9, optional, may include alpha value since 0.15 in the form #AARRGGBB)
	 * 
	 * @since 0.9
	 */
	public static final String BACKGROUND_COLOR = "backgroundcolor";

	/**
	 * Stores the next available ID for new objects. This number is stored to prevent reuse of the same ID after objects have been removed. (since 0.11)
	 * 
	 * @since 0.11
	 */
	public static final String NEXT_OBJECT_ID = "nextobjectid";

}
