package com.abubusoft.xenon.mesh.tiledmaps.isostaggered

import android.opengl.GLES20
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier.setTextureCoords
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer.FillModeType
import com.abubusoft.xenon.mesh.tiledmaps.internal.ImageLayerHandler
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.vbo.BufferAllocationType

//TODO da gestire completamente
class ISSImageLayerHandler(layer: ImageLayer?) : ImageLayerHandler(layer!!) {
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.LayerDrawer#drawLayer(com.abubusoft.xenon.shader.ShaderTiledMap, long, int, int, int, int, com.abubusoft.xenon.math.Matrix4x4)
	 */
    override fun drawLayer(shader: ShaderTiledMap?, enlapsedTime: Long, startLayerColumn: Int, startLayerRow: Int, offsetX: Int, offsetY: Int, modelview: Matrix4x4?) {
        val view = view()
        when (layer!!.fillMode) {
            FillModeType.EXPAND_ON_WINDOW -> {}
            FillModeType.REPEAT_ON_WINDOW -> {
                val invX = 1f / layer!!.textureList[0].info.dimension.width
                val invY = 1f / layer!!.textureList[0].info.dimension.height
                setTextureCoords(
                    layer!!.shape.textures[0], 0, layer!!.layerOffsetX * invX, (view!!.windowWidth + layer!!.layerOffsetX) * invX, layer!!.layerOffsetY * invY,
                    (view.windowHeight + layer!!.layerOffsetY) * invY, false, true
                )
            }
            else -> {}
        }
        // ottimizzazione (texture index=0)
        // shape.setTextureCoordsOnCursor(0, fullWindowTile);
        // shape.cursorMove();
        matrix.buildIdentityMatrix()
        matrix.multiply(modelview!!, matrix)

        // utilizziamo il cursore per aggiornare
        // shape.updateOnCursor(tiledMap.onlyOneTexture4Layer);
        shader!!.setOpacity(layer!!.opacity)
        shader.setVertexCoordinatesArray(layer!!.shape.vertices!!)
        // impostiamo le texture
        shader.setTextureCoordinatesArray(0, layer!!.shape.textures[0])
        shader.setModelViewProjectionMatrix(matrix.asFloatBuffer())

        // il selector di texture qua non ha senso dato che abbiamo una sola immagine

        // GLES20.glDrawArrays(shape.drawMode.value, 0, shape.cursorRead());

        // GLES20.glDrawArrays(shape.drawStyle.value, 0, shape.readCursor());
        // shader.setIndexBuffer(indexBuffer);
        shader.setIndexBuffer(layer!!.shape.indexes!!)
        if (layer!!.shape.indexes!!.allocation === BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(layer!!.shape.drawMode!!.value, layer!!.shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, layer!!.shape.indexes!!.buffer)
        } else {
            GLES20.glDrawElements(layer!!.shape.drawMode!!.value, layer!!.shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0)
        }
        shader.unsetIndexBuffer(layer!!.shape.indexes!!)
    }
}