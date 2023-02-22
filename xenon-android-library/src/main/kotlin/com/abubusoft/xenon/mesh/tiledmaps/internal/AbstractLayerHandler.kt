package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.Layer

abstract class AbstractLayerHandler<E : Layer?>(protected var layer: E) : LayerHandler {
    protected var view: TiledMapView? = null

    /**
     *
     *
     * Matrice di trasformazione. Serve per i calcoli relativi alla traslazione del layer.
     *
     */
    protected val matrix = Matrix4x4()

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.LayerHandler#onBuildView(com.abubusoft.xenon.mesh.tiledmaps.internal.MapView)
	 */
    override fun onBuildView(view: TiledMapView) {
        this.view = view
    }

    fun view(): TiledMapView? {
        return view
    }
}