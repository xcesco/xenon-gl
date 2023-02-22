package com.abubusoft.xenon.mesh.tiledmaps.tmx

object LayerAttributes {
    /**
     *
     * Rendering offset for this layer in pixels. Defaults to 0. (since 0.14).
     *
     *
     * E' un valore da sottrarre alle coordinate delle tile
     */
    const val OFFSET_X = "x"

    /**
     *
     * Rendering offset for this layer in pixels. Defaults to 0. (since 0.14)
     *
     *
     * E' un valore da sottrarre alle coordinate delle tile
     */
    const val OFFSET_Y = "y"

    /**
     * The width of the layer in tiles. Traditionally required, but as of Tiled Qt always the same as the map width.
     */
    const val WIDTH = "width"

    /**
     * The height of the layer in tiles. Traditionally required, but as of Tiled Qt always the same as the map height.
     */
    const val HEIGHT = "height"

    /**
     * The opacity of the layer as a value from 0 to 1. Defaults to 1.
     */
    const val OPACITY = "opacity"

    /**
     * The name of the layer.
     */
    const val NAME = "name"
}