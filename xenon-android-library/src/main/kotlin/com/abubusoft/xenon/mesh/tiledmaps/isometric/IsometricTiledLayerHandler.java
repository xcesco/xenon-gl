/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.isometric;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.MeshDrawModeType;
import com.abubusoft.xenon.mesh.MeshGrid;
import com.abubusoft.xenon.mesh.modifiers.AttributeQuadModifier;
import com.abubusoft.xenon.mesh.modifiers.IndexQuadModifier;
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier;
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass;
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.shader.ShaderTiledMap;
import com.abubusoft.xenon.vbo.AttributeBuffer;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.IndexBuffer;

import android.opengl.GLES20;

/**
 * @author xcesco
 */
public class IsometricTiledLayerHandler extends TiledLayerHandler {

    private AttributeBuffer offsetBuffer;

    public IsometricTiledLayerHandler(TiledLayer layer) {
        super(layer);

    }

    /**
     * <p>
     * Consente di disegnare solo una porzione della tile. Utile nel caso di oggetti.
     * </p>
     *
     * @param shader
     * @param deltaTime
     * @param clazz
     * @param modelview
     */
    public void drawLayerPart(ShaderTiledMap shader, long deltaTime, ObjClass clazz, Matrix4x4 modelview) {
        MeshGrid shape = layer.tiledMap.spriteMesh;

        // int startLayerColumn, int startLayerRow, int screenOffsetX, int screenOffsetY, int rowsCount, int colsCount

        // float screenCenterX = tiledMap.windowCenter.x;
        // float screenCenterY = tiledMap.windowCenter.y;

        int layerCurrentRow;
        int layerCurrentColumn;

        // facciamo in modo di avere sempre indici positivi e compresi tra 0 e
        // rows/columns
        // se è negativo --> row - (modulo), se positivo --> 0 + modulo
        int startLayerRow = (clazz.shapeRowBegin < 0 ? layer.tileRows : 0) + (clazz.shapeRowBegin % layer.tileRows);
        int startLayerColumn = (clazz.shapeColBegin < 0 ? layer.tileColumns : 0) + (clazz.shapeColBegin % layer.tileColumns);

        // posizione della tile sullo schermo in base al sistema di origine in
        // mezzo allo schermo
        // int sceneX;
        // int sceneY;

        // resettiamo cursore
        shape.cursorReset();

        // ciclo per disegnare
        layerCurrentRow = startLayerRow % layer.tileRows;
        for (int i = 0; i < clazz.shapeRowSize; i++) {

            // se siamo oltre le dimensioni del layer, resettiamo la colonna
            layerCurrentColumn = startLayerColumn;
            if (layerCurrentColumn >= layer.tileColumns)
                layerCurrentColumn = 0;

            for (int j = 0; j < clazz.shapeColSize; j++) {

                // prende dalla definizione del layer il tile da disegnare
                layer.tileToDraw = layer.tiles[layerCurrentRow * layer.tileColumns + layerCurrentColumn];

                // aggiungiamo coordinate solo se necessario (tile valida,
                // ovvero gid !=0)
                if (layer.tileToDraw.gid != 0) {
                    shape.setVertexCoordsOnCursor((layerCurrentColumn - startLayerColumn) * layer.tileToDraw.width, -(layerCurrentRow - startLayerRow) * layer.tileToDraw.height, layer.tileToDraw);

                    // ottimizzazione (texture index=0)
                    shape.setTextureCoordsOnCursor(0, layer.tileToDraw);

                    // Se abbiamo più di una texture dobbiamo impostare il
                    // selettore di texture
                    if (!layer.tiledMap.onlyOneTexture4Layer) {
                        setTextureSelector(i * clazz.shapeColSize + j, layer.tileToDraw);
                    }

                    // ci posizioniamo sul prossimo tile da disegnare
                    shape.cursorMove();
                }

                layerCurrentColumn = (layerCurrentColumn + 1) % layer.tileColumns;
            }

            layerCurrentRow = (layerCurrentRow + 1) % layer.tileRows;
        }

        // con la definizione in vertex array abbiamo posizionato le tile
        // corrette
        // con gli offset andiamo a spostarli anche dei pixel subtile che
        // servono per
        // considerare lo scroll
        matrix.buildTranslationMatrix(-clazz.shapeRowOffset, clazz.shapeColOffset, 0);
        matrix.multiply(modelview, matrix);

        // utilizziamo il cursore per aggiornare
        // Se abbiamo più di una texture dobbiamo impostare il selettore di
        // texture
        if (!layer.tiledMap.onlyOneTexture4Layer) {
            textureIndex.put(textureSelector).position(0);
        }
        shape.updateBuffersOnCursor();

        shader.setOpacity(layer.opacity);
        shader.setVertexCoordinatesArray(shape.vertices);
        shader.setModelViewProjectionMatrix(matrix.asFloatBuffer());

        // Se abbiamo più di una texture dobbiamo impostare il selettore di
        // texture
        if (!layer.tiledMap.onlyOneTexture4Layer) {
            shader.setTextureSelectorArray(textureIndex);
        }

        // impostiamo le texture
        shader.setTextureCoordinatesArray(0, shape.textures[0]);

        shader.setIndexBuffer(shape.indexes);
        if (shape.indexes.allocation == BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(shape.drawMode.value, shape.cursor * IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, shape.indexes.buffer);
        } else {
            GLES20.glDrawElements(shape.drawMode.value, shape.cursor * IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, 0);
        }
        shader.unsetIndexBuffer(shape.indexes);

        // GLES20.glDrawArrays(shape.drawMode.value, 0, shape.cursorRead());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.abubusoft.xenon.mesh.tiledmaps.LayerDrawer#drawLayer(org.abubu.argon .shader.TiledMapShader, long, int, int, int, int, com.abubusoft.xenon.math.Matrix4x4)
     */
    public void drawLayer(ShaderTiledMap shader, long deltaTime, int startLayerColumn, int startLayerRow, int offsetX, int offsetY, Matrix4x4 modelview) {
        // MeshGrid shape = windowMesh;

        // la griglia
        int windowColumns = view.windowTileColumns;
        int windowRows = view.windowTileRows;

        int layerCurrentRow;
        int layerCurrentColumn;

        startLayerRow -= view.windowBorder;
        startLayerColumn -= view.windowBorder;

        // facciamo in modo di avere sempre indici positivi e compresi tra 0 e
        // rows/columns
        // se è negativo --> row - (modulo), se positivo --> 0 + modulo
        startLayerRow = (startLayerRow < 0 ? layer.tileRows : 0) + (startLayerRow % layer.tileRows);
        startLayerColumn = (startLayerColumn < 0 ? layer.tileColumns : 0) + (startLayerColumn % layer.tileColumns);

        // posizione della tile sullo schermo in base al sistema di origine in
        // mezzo allo schermo
        // int sceneX;
        // int sceneY;

        if (layer.oldStartLayerColumn != startLayerColumn || layer.oldStartLayerRow != startLayerRow) {
            // resettiamo cursore
            // shape.cursorReset();

            indexBuffer.cursorReset();
            textureBuffer.cursorReset();

            if (!layer.drawOffsetUnique) {
                offsetBuffer.cursorReset();
            }

            // ciclo per disegnare
            layerCurrentRow = startLayerRow % layer.tileRows;

            layer.oldStartLayerColumn = startLayerColumn;
            layer.oldStartLayerRow = startLayerRow;

            for (int i = 0; i < windowRows; i++) {

                // se siamo oltre le dimensioni del layer, resettiamo la colonna
                layerCurrentColumn = startLayerColumn;
                if (layerCurrentColumn >= layer.tileColumns)
                    layerCurrentColumn = 0;

                for (int j = 0; j < windowColumns; j++) {

                    // prende dalla definizione del layer il tile da disegnare
                    layer.tileToDraw = layer.tiles[layerCurrentRow * layer.tileColumns + layerCurrentColumn];

                    // aggiungiamo coordinate solo se necessario (tile valida,
                    // ovvero gid !=0)
                    if (layer.tileToDraw.gid != 0) {
                        // sceneX = j * tileToDraw.width;
                        // sceneY = i * tileToDraw.height;

                        // old sistema
                        // -------------------------
                        // shape.setVertexCoordsOnCursor(sceneX, -sceneY, tileToDraw);
                        // ottimizzazione (texture index=0)
                        // shape.setTextureCoordsOnCursor(0, tileToDraw);

                        // Se abbiamo più di una texture dobbiamo impostare il
                        // selettore di texture
                        if (!layer.tiledMap.onlyOneTexture4Layer) {
                            setTextureSelector(i * windowColumns + j, layer.tileToDraw);
                        }

                        // ci posizioniamo sul prossimo tile da disegnare
                        // shape.cursorMove();
                        // -------------------------

                        // new sistema
                        TextureQuadModifier.setTextureCoords(textureBuffer, i * windowColumns + j, layer.tileToDraw, false);

                        IndexQuadModifier.setIndexes(indexBuffer, indexBuffer.cursor, i * windowColumns + j, false);
                        indexBuffer.cursorMove(IndexBuffer.INDEX_IN_QUAD_TILE);

                        if (!layer.drawOffsetUnique)
                            AttributeQuadModifier.setVertexAttributes2(offsetBuffer, i * windowColumns + j, layer.tileToDraw.drawOffsetX, layer.tileToDraw.drawOffsetY, false);
                        // textureBuffer.cursorMove(VertexBuffer.VERTEX_IN_QUAD_TILE);
                    }

                    layerCurrentColumn = (layerCurrentColumn + 1) % layer.tileColumns;
                }

                layerCurrentRow = (layerCurrentRow + 1) % layer.tileRows;

            }

            indexBuffer.update();
            textureBuffer.update();

            if (!layer.drawOffsetUnique) {
                offsetBuffer.update();
            }
            // Se abbiamo più di una texture dobbiamo impostare il selettore di
            // texture
            if (!layer.tiledMap.onlyOneTexture4Layer) {
                textureIndex.put(textureSelector).position(0);
            }

        }
        // con la definizione in vertex array abbiamo posizionato le tile
        // corrette
        // con gli offset andiamo a spostarli anche dei pixel subtile che
        // servono per
        // considerare lo scroll
        // matrix.buildTranslationMatrix(-screenOffsetX - screenCenterX, screenOffsetY + screenCenterY, 0);
        // matrix.buildTranslationMatrix(-screenOffsetX - screenCenterX, screenOffsetY + screenCenterY, 0);

        // matrix.buildTranslationMatrix(- screenCenterX, screenCenterY, 0);
        // matrix.buildTranslationMatrix(0, screenCenterY, 0);
        // matrix.buildTranslationMatrix(-screenOffsetX - screenCenterX, screenOffsetY + screenCenterY, 0);

        // tentativo 2
        //matrix.buildTranslationMatrix(-screenOffsetX, screenOffsetY, 0);

        // tentativo X
        // la camera punta già al centro della window.
        matrix.buildTranslationMatrix(-offsetX, -offsetY, 0);

        if (layer.drawOffsetUnique) {
            //matrix.translate(layer.drawOffsetX, layer.drawOffsetY, 0f);
        }
        // matrix.buildScaleMatrix(0.5f, 0.5f, 0.5f);

        matrix.multiply(modelview, matrix);

        // utilizziamo il cursore per aggiornare
        // shape.updateOnCursor(tiledMap.onlyOneTexture4Layer);

        shader.setOpacity(layer.opacity);
        shader.setVertexCoordinatesArray(vertexBuffer);
        shader.setModelViewProjectionMatrix(matrix.asFloatBuffer());

        // Se abbiamo più di una texture dobbiamo impostare il selettore di
        // texture

        if (!layer.tiledMap.onlyOneTexture4Layer) {
            shader.setTextureSelectorArray(textureIndex);
        }

        // impostiamo le texture
        // shader.setTextureCoordinatesArray(0, shape.textures[0]);
        shader.setTextureCoordinatesArray(0, textureBuffer);

        // GLES20.glDrawArrays(shape.drawStyle.value, 0, shape.readCursor());
        shader.setIndexBuffer(indexBuffer);

        if (indexBuffer.allocation == BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(MeshDrawModeType.INDEXED_TRIANGLES.value, indexBuffer.cursor, GLES20.GL_UNSIGNED_SHORT, indexBuffer.buffer);
        } else {
            GLES20.glDrawElements(MeshDrawModeType.INDEXED_TRIANGLES.value, indexBuffer.cursor, GLES20.GL_UNSIGNED_SHORT, 0);
        }

        shader.unsetIndexBuffer(indexBuffer);
    }

    @Override
    public void onBuildView(TiledMapView view) {
        super.onBuildView(view);

        if (layer.tileWidthMax != layer.tiledMap.tileWidth || layer.tileHeightMax != layer.tiledMap.tileHeight) {
            // le dimensioni delle tile tra map e layer non coincidono, quindi il layer avrà bisogno
            // di un suo vertex buffer
            // creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
            // i layer che hanno come dimensione delle tile le stesse di default.
            /*
			 * float sceneX; float sceneY; vertexBuffer = BufferManager.instance().createVertexBuffer(view.windowTileColumns * view.windowTileRows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC); for (int i = 0; i <
			 * view.windowTileRows; i++) { for (int j = 0; j < view.windowTileColumns; j++) { sceneX = (j - i) * layer.tiledMap.tileWidth * .5f - layer.tiledMap.tileWidth * .5f; sceneY = (j + i) * layer.tiledMap.tileHeight * .5f;
			 * 
			 * VertexQuadModifier.setVertexCoords(vertexBuffer, i * view.windowTileColumns + j, sceneX, -sceneY, layer.tileWidthMax, layer.tileHeightMax, false); } }
			 */
            vertexBuffer = IsometricHelper.buildDiamondVertexBuffer(view.windowTileRows, view.windowTileColumns, layer.tiledMap.tileWidth * .5f, layer.tiledMap.tileHeight * .5f, layer.tileWidthMax, layer.tileHeightMax);
            // lo impostiamo una volta per tutte, tanto non verrà mai cambiato
            vertexBuffer.update();

            if (!layer.drawOffsetUnique) {
                offsetBuffer = IsometricHelper.buildDiamondOffsetAttributeBuffer(view.windowTileColumns, view.windowTileRows);
            }

            // abbiamo più di un tileset in questo layer, quindi
            if (this.layer.textureList.size() > 1) {

            }
        }

        // MeshOptions options=MeshOptions.build();//.bufferAllocation(BufferAllocationType.STATIC).textureEnabled(false).colorEnabled(false);

		/*
		 * windowMesh = MeshFactory.createIsometricTiledGrid(layer.tiledMap.tileWidth * layer.tiledMap.windowTileColumns, layer.tiledMap.tileHeight * layer.tiledMap.windowTileRows, layer.tileWidthMax, layer.tileHeightMax,
		 * layer.tiledMap.tileWidth, layer.tiledMap.tileHeight, layer.tiledMap.windowTileRows, layer.tiledMap.windowTileColumns, options);
		 */

        // windowMesh = MeshFactory.createTiledGrid(layer.tiledMap.tileWidth * layer.tiledMap.windowTileColumns, layer.tiledMap.tileHeight * layer.tiledMap.windowTileRows, layer.tiledMap.windowTileRows, layer.tiledMap.windowTileColumns,
        // MeshOptions.build());

    }
}
