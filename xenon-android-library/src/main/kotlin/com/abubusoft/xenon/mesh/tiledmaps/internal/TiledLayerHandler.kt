package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.*
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.vbo.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

abstract class TiledLayerHandler(layer: TiledLayer) : AbstractLayerHandler<TiledLayer?>(layer) {
    /**
     * Buffer dei vertici. Può coincidere con quello condiviso della mappa o in caso di tile di dimensioni diverse,
     * può essere creato per i fatti suoi.
     */
    protected var vertexBuffer: VertexBuffer? = null

    /**
     * associato a questo layer, contiene le coordinate delle texture
     */
    var textureBuffer: TextureBuffer? = null

    /**
     * associato a questo layer, contiene gli indici dei triangoli da usare.
     */
    var indexBuffer: IndexBuffer? = null

    /**
     * array delle texture usate
     */
    var textureSelector: FloatArray

    /**
     * array delle texture selezionate
     */
    var textureIndex: FloatBuffer? = null

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler#onBuildView(com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView)
	 */
    override fun onBuildView(view: TiledMapView) {
        super.onBuildView(view)
        vertexBuffer = view.windowVerticesBuffer

        // definiamo questi buffer per ogni tiledlayer perchè così non devono essere ripuliti ogni volta. Se
        // stiamo fermi, questi qua vanno già bene.
        indexBuffer = BufferManager.instance().createIndexBuffer(view.windowTileColumns * view.windowTileRows * IndexBuffer.INDEX_IN_QUAD_TILE, BufferAllocationType.STREAM)
        textureBuffer = BufferManager.instance().createTextureBuffer(view.windowTileColumns * view.windowTileRows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STREAM)
        textureSelector = FloatArray(view.windowTileColumns * view.windowTileRows)
        textureIndex = ByteBuffer.allocateDirect(view.windowTileColumns * view.windowTileRows * VertexBuffer.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
    }

    /**
     * Per una tile, imposta la texture associata
     *
     * @param currentQuad
     * @param tile
     */
    fun setTextureSelector(currentQuad: Int, tile: Tile) {
        val basePtr: Int = currentQuad * VertexBuffer.VERTEX_IN_QUAD_TILE
        if (Tile.isEmpty(tile)) {
            textureSelector[basePtr + 0] = 0f
            textureSelector[basePtr + 1] = 0f
            textureSelector[basePtr + 2] = 0f
            textureSelector[basePtr + 3] = 0f
        } else {
            textureSelector[basePtr + 0] = tile.textureSelector.toFloat()
            textureSelector[basePtr + 1] = tile.textureSelector.toFloat()
            textureSelector[basePtr + 2] = tile.textureSelector.toFloat()
            textureSelector[basePtr + 3] = tile.textureSelector.toFloat()
        }
    }

    /**
     *
     *
     * Consente di disegnare solo una porzione della tile. Utile nel caso di oggetti.
     *
     *
     * @param shader
     *
     * @param deltaTime
     *
     * @param clazz
     *
     * @param modelview
     */
    abstract fun drawLayerPart(shader: ShaderTiledMap?, deltaTime: Long, clazz: ObjClass?, modelview: Matrix4x4?)
}