package com.abubusoft.xenon.mesh.tiledmaps.internal

interface LayerHandler : LayerDrawer {
    /**
     * evento sulla creazione della window
     */
    fun onBuildView(view: TiledMapView)
}