package com.abubusoft.xenon.mesh.tiledmaps.orthogonal;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier;
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer;
import com.abubusoft.xenon.mesh.tiledmaps.internal.ImageLayerHandler;
import com.abubusoft.xenon.shader.ShaderTiledMap;
import com.abubusoft.xenon.vbo.BufferAllocationType;

import android.opengl.GLES20;

public class OrthogonalImageLayerHandler extends ImageLayerHandler {

	public OrthogonalImageLayerHandler(ImageLayer layer) {
		super(layer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.LayerDrawer#drawLayer(com.abubusoft.xenon.shader.ShaderTiledMap, long, int, int, int, int, com.abubusoft.xenon.math.Matrix4x4)
	 */
	@Override
	public void drawLayer(ShaderTiledMap shader, long enlapsedTime, int startLayerColumn, int startLayerRow, int offsetX, int offsetY, Matrix4x4 modelview) {

		// resettiamo cursore
		// shape.cursorReset();
		// windowCenter tiene conto delle tile in più che vengono visualizzate.
		// per coprire semplicemnte l'intero schermo, basta andare sul width e height dello schermo
		// che già tiene conto dell'aspect ratio.
		// shape.setVertexCoordsOnCursor(-tiledMap.windowWidth / 2f, tiledMap.windowHeight / 2f, fullWindowTile);

		switch (layer.fillMode) {
		case EXPAND_ON_WINDOW:

			break;
		case REPEAT_ON_WINDOW:
			float invX = 1f / layer.textureList.get(0).info.dimension.width;
			float invY = 1f / layer.textureList.get(0).info.dimension.height;

			TextureQuadModifier.setTextureCoords(layer.shape.textures[0], 0, layer.layerOffsetX * invX, (view.windowWidth + layer.layerOffsetX) * invX, layer.layerOffsetY * invY,
					(view.windowHeight + layer.layerOffsetY) * invY, false, true);
			// fullWindowTile.setTextureCoordinate(screenOffsetX * invX, (tiledMap.windowWidth + screenOffsetX) * invX, screenOffsetY * invY, (tiledMap.windowHeight + screenOffsetY) * invY);
			break;
		default:
			break;
		}
		// ottimizzazione (texture index=0)
		// shape.setTextureCoordsOnCursor(0, fullWindowTile);
		// shape.cursorMove();

		matrix.buildIdentityMatrix();
		matrix.multiply(modelview, matrix);

		// utilizziamo il cursore per aggiornare
		// shape.updateOnCursor(tiledMap.onlyOneTexture4Layer);

		shader.setOpacity(layer.opacity);
		shader.setVertexCoordinatesArray(layer.shape.vertices);
		// impostiamo le texture
		shader.setTextureCoordinatesArray(0, layer.shape.textures[0]);
		shader.setModelViewProjectionMatrix(matrix.asFloatBuffer());

		// il selector di texture qua non ha senso dato che abbiamo una sola immagine

		// GLES20.glDrawArrays(shape.drawMode.value, 0, shape.cursorRead());

		// GLES20.glDrawArrays(shape.drawStyle.value, 0, shape.readCursor());
		// shader.setIndexBuffer(indexBuffer);
		shader.setIndexBuffer(layer.shape.indexes);

		if (layer.shape.indexes.allocation == BufferAllocationType.CLIENT) {
			GLES20.glDrawElements(layer.shape.drawMode.value, layer.shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, layer.shape.indexes.buffer);
		} else {
			GLES20.glDrawElements(layer.shape.drawMode.value, layer.shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0);
		}

		shader.unsetIndexBuffer(layer.shape.indexes);

	}
}
