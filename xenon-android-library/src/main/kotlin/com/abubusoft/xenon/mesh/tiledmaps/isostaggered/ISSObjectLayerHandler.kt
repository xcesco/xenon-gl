package com.abubusoft.xenon.mesh.tiledmaps.isostaggered;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.internal.ObjectLayerHandler;
import com.abubusoft.xenon.shader.ShaderTiledMap;

//TODO da gestire completamente
public class ISSObjectLayerHandler extends ObjectLayerHandler  {

	
	public ISSObjectLayerHandler(ObjectLayer layer) {
		super(layer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.LayerDrawer#drawLayer(org.abubu.argon .shader.TiledMapShader, long, int, int, int, int, com.abubusoft.xenon.math.Matrix4x4)
	 */
	@Override
	public void drawLayer(ShaderTiledMap shader, long enlapsedTime, int startLayerColumn, int startLayerRow, int offsetX, int offsetY, Matrix4x4 modelview) {
		if (layer.visible && layer.objectDrawer != null) {
			// con la definizione in vertex array abbiamo posizionato le tile
			// corrette
			// con gli offset andiamo a spostarli anche dei pixel subtile che
			// servono per
			// considerare lo scroll
			matrix.build(modelview);

			layer.objectDrawer.onObjectLayerFrameDraw(layer.tiledMap, layer, enlapsedTime, matrix);

			// se siamo qua molto probabilmente abbiamo cambiato shader, quindi
			// lo ripristiniamo per sicurezza.
			shader.use();
		}

	}
	
}
