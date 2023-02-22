package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.tiledmaps.Layer;

public abstract class AbstractLayerHandler<E extends Layer> implements LayerHandler {

	protected E layer;

	protected TiledMapView view;

	public AbstractLayerHandler(E layer) {
		this.layer = layer;
	}

	/**
	 * <p>
	 * Matrice di trasformazione. Serve per i calcoli relativi alla traslazione del layer.
	 * </p>
	 */
	protected final Matrix4x4 matrix = new Matrix4x4();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.LayerHandler#onBuildView(com.abubusoft.xenon.mesh.tiledmaps.internal.MapView)
	 */
	@Override
	public void onBuildView(TiledMapView view) {
		this.view = view;

	}

	public TiledMapView view() {
		return view;
	}
}
