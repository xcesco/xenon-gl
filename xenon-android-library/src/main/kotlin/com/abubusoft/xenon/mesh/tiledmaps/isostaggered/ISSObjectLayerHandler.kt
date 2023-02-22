package com.abubusoft.xenon.mesh.tiledmaps.isostaggered

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer
import com.abubusoft.xenon.mesh.tiledmaps.internal.ObjectLayerHandler
import com.abubusoft.xenon.shader.ShaderTiledMap

//TODO da gestire completamente
class ISSObjectLayerHandler(layer: ObjectLayer?) : ObjectLayerHandler(layer!!) {
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.LayerDrawer#drawLayer(org.abubu.argon .shader.TiledMapShader, long, int, int, int, int, com.abubusoft.xenon.math.Matrix4x4)
	 */
    override fun drawLayer(shader: ShaderTiledMap?, enlapsedTime: Long, startLayerColumn: Int, startLayerRow: Int, offsetX: Int, offsetY: Int, modelview: Matrix4x4?) {
        if (layer!!.visible && layer!!.objectDrawer != null) {
            // con la definizione in vertex array abbiamo posizionato le tile
            // corrette
            // con gli offset andiamo a spostarli anche dei pixel subtile che
            // servono per
            // considerare lo scroll
            matrix.build(modelview!!)
            layer!!.objectDrawer.onObjectLayerFrameDraw(layer!!.tiledMap, layer, enlapsedTime, matrix)

            // se siamo qua molto probabilmente abbiamo cambiato shader, quindi
            // lo ripristiniamo per sicurezza.
            shader!!.use()
        }
    }
}