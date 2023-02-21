package com.abubusoft.xenon.mesh.tiledmaps.internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass;
import com.abubusoft.xenon.mesh.tiledmaps.Tile;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.shader.ShaderTiledMap;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.BufferManager;
import com.abubusoft.xenon.vbo.IndexBuffer;
import com.abubusoft.xenon.vbo.TextureBuffer;
import com.abubusoft.xenon.vbo.VertexBuffer;

public abstract class TiledLayerHandler extends AbstractLayerHandler<TiledLayer> {

	public TiledLayerHandler(TiledLayer layer) {
		super(layer);
	}
	
	/**
	 * Buffer dei vertici. Può coincidere con quello condiviso della mappa o in caso di tile di dimensioni diverse,
	 * può essere creato per i fatti suoi.
	 */
	protected VertexBuffer vertexBuffer;
	
	/**
	 * associato a questo layer, contiene le coordinate delle texture
	 */
	public TextureBuffer textureBuffer;

	/**
	 * associato a questo layer, contiene gli indici dei triangoli da usare.
	 */
	public IndexBuffer indexBuffer;
	
	/**
	 * array delle texture usate
	 */
	public float[] textureSelector;

	/**
	 * array delle texture selezionate
	 */
	public FloatBuffer textureIndex;
	
	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler#onBuildView(com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView)
	 */
	@Override
	public void onBuildView(TiledMapView view) {
		super.onBuildView(view);
		
		vertexBuffer=view.windowVerticesBuffer;
		
		// definiamo questi buffer per ogni tiledlayer perchè così non devono essere ripuliti ogni volta. Se
		// stiamo fermi, questi qua vanno già bene.
		indexBuffer = BufferManager.instance().createIndexBuffer(view.windowTileColumns * view.windowTileRows * IndexBuffer.INDEX_IN_QUAD_TILE, BufferAllocationType.STREAM);
		textureBuffer = BufferManager.instance().createTextureBuffer(view.windowTileColumns * view.windowTileRows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STREAM);				

		textureSelector = new float[view.windowTileColumns * view.windowTileRows];
		textureIndex = ByteBuffer.allocateDirect(view.windowTileColumns * view.windowTileRows * VertexBuffer.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	/**
	 * Per una tile, imposta la texture associata
	 * 
	 * @param currentQuad
	 * @param tile
	 */
	public void setTextureSelector(int currentQuad, Tile tile) {
		int basePtr = currentQuad * VertexBuffer.VERTEX_IN_QUAD_TILE;

		if (Tile.isEmpty(tile)) {
			textureSelector[basePtr + 0] = 0;
			textureSelector[basePtr + 1] = 0;
			textureSelector[basePtr + 2] = 0;
			textureSelector[basePtr + 3] = 0;
		} else {
			textureSelector[basePtr + 0] = tile.textureSelector;
			textureSelector[basePtr + 1] = tile.textureSelector;
			textureSelector[basePtr + 2] = tile.textureSelector;
			textureSelector[basePtr + 3] = tile.textureSelector;
		}

	}

	/**
	 * <p>
	 * Consente di disegnare solo una porzione della tile. Utile nel caso di oggetti.
	 * </p>
	 * 
	 * @param shader
	 * 
	 * @param deltaTime
	 * 
	 * @param clazz
	 * 
	 * @param modelview
	 * 
	 */
	public abstract void drawLayerPart(ShaderTiledMap shader, long deltaTime, ObjClass clazz, Matrix4x4 modelview);

}
