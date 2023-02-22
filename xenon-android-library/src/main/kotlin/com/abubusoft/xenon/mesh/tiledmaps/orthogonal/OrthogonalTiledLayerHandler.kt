/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.orthogonal

import android.opengl.GLES20
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.MeshDrawModeType
import com.abubusoft.xenon.mesh.modifiers.IndexQuadModifier.setIndexes
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier.setTextureCoords
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.vbo.IndexBuffer

/**
 * @author xcesco
 */
class OrthogonalTiledLayerHandler(layer: TiledLayer?) : TiledLayerHandler(layer!!) {
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
    override fun drawLayerPart(shader: ShaderTiledMap?, deltaTime: Long, clazz: ObjClass?, modelview: Matrix4x4?) {
        val shape = layer!!.tiledMap.spriteMesh

        // int startLayerColumn, int startLayerRow, int screenOffsetX, int screenOffsetY, int rowsCount, int colsCount

        // float screenCenterX = tiledMap.windowCenter.x;
        // float screenCenterY = tiledMap.windowCenter.y;
        var layerCurrentRow: Int
        var layerCurrentColumn: Int

        // facciamo in modo di avere sempre indici positivi e compresi tra 0 e
        // rows/columns
        // se è negativo --> row - (modulo), se positivo --> 0 + modulo
        val startLayerRow = (if (clazz!!.shapeRowBegin < 0) layer!!.tileRows else 0) + clazz.shapeRowBegin % layer!!.tileRows
        val startLayerColumn = (if (clazz.shapeColBegin < 0) layer!!.tileColumns else 0) + clazz.shapeColBegin % layer!!.tileColumns

        // posizione della tile sullo schermo in base al sistema di origine in
        // mezzo allo schermo
        // int sceneX;
        // int sceneY;

        // resettiamo cursore
        shape.cursorReset()

        // ciclo per disegnare
        layerCurrentRow = startLayerRow % layer!!.tileRows
        for (i in 0 until clazz.shapeRowSize) {

            // se siamo oltre le dimensioni del layer, resettiamo la colonna
            layerCurrentColumn = startLayerColumn
            if (layerCurrentColumn >= layer!!.tileColumns) layerCurrentColumn = 0
            for (j in 0 until clazz.shapeColSize) {

                // prende dalla definizione del layer il tile da disegnare
                layer!!.tileToDraw = layer!!.tiles[layerCurrentRow * layer!!.tileColumns + layerCurrentColumn]

                // aggiungiamo coordinate solo se necessario (tile valida,
                // ovvero gid !=0)
                if (layer!!.tileToDraw.gid != 0) {
                    shape.setVertexCoordsOnCursor(
                        ((layerCurrentColumn - startLayerColumn) * layer!!.tileToDraw.width).toFloat(),
                        (-(layerCurrentRow - startLayerRow) * layer!!.tileToDraw.height).toFloat(),
                        layer!!.tileToDraw
                    )

                    // ottimizzazione (texture index=0)
                    shape.setTextureCoordsOnCursor(0, layer!!.tileToDraw)

                    // Se abbiamo più di una texture dobbiamo impostare il
                    // selettore di texture
                    if (!layer!!.tiledMap.onlyOneTexture4Layer) {
                        setTextureSelector(i * clazz.shapeColSize + j, layer!!.tileToDraw)
                    }

                    // ci posizioniamo sul prossimo tile da disegnare
                    shape.cursorMove()
                }
                layerCurrentColumn = (layerCurrentColumn + 1) % layer!!.tileColumns
            }
            layerCurrentRow = (layerCurrentRow + 1) % layer!!.tileRows
        }

        // con la definizione in vertex array abbiamo posizionato le tile
        // corrette
        // con gli offset andiamo a spostarli anche dei pixel subtile che
        // servono per
        // considerare lo scroll
        matrix.buildTranslationMatrix(-clazz.shapeRowOffset.toFloat(), clazz.shapeColOffset.toFloat(), 0f)
        matrix.multiply(modelview!!, matrix)

        // utilizziamo il cursore per aggiornare
        // Se abbiamo più di una texture dobbiamo impostare il selettore di
        // texture
        if (!layer!!.tiledMap.onlyOneTexture4Layer) {
            textureIndex!!.put(textureSelector).position(0)
        }
        shape.updateBuffersOnCursor()
        shader!!.setOpacity(layer!!.opacity)
        shader.setVertexCoordinatesArray(shape.vertices!!)
        shader.setModelViewProjectionMatrix(matrix.asFloatBuffer())

        // Se abbiamo più di una texture dobbiamo impostare il selettore di
        // texture
        if (!layer!!.tiledMap.onlyOneTexture4Layer) {
            shader.setTextureSelectorArray(textureIndex)
        }

        // impostiamo le texture
        shader.setTextureCoordinatesArray(0, shape.textures[0])
        shader.setIndexBuffer(shape.indexes!!)
        if (shape.indexes!!.allocation === BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(shape.drawMode!!.value, shape.cursor * IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, shape.indexes!!.buffer)
        } else {
            GLES20.glDrawElements(shape.drawMode!!.value, shape.cursor * IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, 0)
        }
        shader.unsetIndexBuffer(shape.indexes!!)

        // GLES20.glDrawArrays(shape.drawMode.value, 0, shape.cursorRead());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.LayerDrawer#drawLayer(org.abubu.argon .shader.TiledMapShader, long, int, int, int, int, com.abubusoft.xenon.math.Matrix4x4)
	 */
    override fun drawLayer(shader: ShaderTiledMap?, deltaTime: Long, startLayerColumn: Int, startLayerRow: Int, offsetX: Int, offsetY: Int, modelview: Matrix4x4?) {
        //MeshGrid shape = layer.tiledMap.windowMesh;

        // la griglia
        var startLayerColumn = startLayerColumn
        var startLayerRow = startLayerRow
        val windowColumns = view!!.windowTileColumns
        val windowRows = view!!.windowTileRows
        val screenCenterX = view!!.windowCenter.x
        val screenCenterY = view!!.windowCenter.y
        var layerCurrentRow: Int
        var layerCurrentColumn: Int
        startLayerRow -= view!!.windowBorder
        startLayerColumn -= view!!.windowBorder

        // facciamo in modo di avere sempre indici positivi e compresi tra 0 e
        // rows/columns
        // se è negativo --> row - (modulo), se positivo --> 0 + modulo
        startLayerRow = (if (startLayerRow < 0) layer!!.tileRows else 0) + startLayerRow % layer!!.tileRows
        startLayerColumn = (if (startLayerColumn < 0) layer!!.tileColumns else 0) + startLayerColumn % layer!!.tileColumns

        // posizione della tile sullo schermo in base al sistema di origine in
        // mezzo allo schermo
        // int sceneX;
        // int sceneY;
        if (layer!!.oldStartLayerColumn != startLayerColumn || layer!!.oldStartLayerRow != startLayerRow) {
            // resettiamo cursore
            //shape.cursorReset();
            indexBuffer!!.cursorReset()
            textureBuffer!!.cursorReset()

            // ciclo per disegnare
            layerCurrentRow = startLayerRow % layer!!.tileRows
            layer!!.oldStartLayerColumn = startLayerColumn
            layer!!.oldStartLayerRow = startLayerRow
            for (i in 0 until windowRows) {

                // se siamo oltre le dimensioni del layer, resettiamo la colonna
                layerCurrentColumn = startLayerColumn
                if (layerCurrentColumn >= layer!!.tileColumns) layerCurrentColumn = 0
                for (j in 0 until windowColumns) {

                    // prende dalla definizione del layer il tile da disegnare
                    layer!!.tileToDraw = layer!!.tiles[layerCurrentRow * layer!!.tileColumns + layerCurrentColumn]

                    // aggiungiamo coordinate solo se necessario (tile valida,
                    // ovvero gid !=0)
                    if (layer!!.tileToDraw.gid != 0) {
                        // sceneX = j * tileToDraw.width;
                        // sceneY = i * tileToDraw.height;

                        // old sistema
                        // -------------------------
                        // shape.setVertexCoordsOnCursor(sceneX, -sceneY, tileToDraw);
                        // ottimizzazione (texture index=0)
                        // shape.setTextureCoordsOnCursor(0, tileToDraw);

                        // Se abbiamo più di una texture dobbiamo impostare il
                        // selettore di texture
                        if (!layer!!.tiledMap.onlyOneTexture4Layer) {
                            setTextureSelector(i * windowColumns + j, layer!!.tileToDraw)
                        }

                        // ci posizioniamo sul prossimo tile da disegnare
                        // shape.cursorMove();
                        // -------------------------

                        // new sistema
                        setTextureCoords(textureBuffer!!, i * windowColumns + j, layer!!.tileToDraw, false)
                        setIndexes(indexBuffer!!, indexBuffer!!.cursor, i * windowColumns + j, false)
                        indexBuffer!!.cursorMove(IndexBuffer.INDEX_IN_QUAD_TILE)
                        // textureBuffer.cursorMove(VertexBuffer.VERTEX_IN_QUAD_TILE);
                    }
                    layerCurrentColumn = (layerCurrentColumn + 1) % layer!!.tileColumns
                }
                layerCurrentRow = (layerCurrentRow + 1) % layer!!.tileRows
            }
            indexBuffer!!.update()
            textureBuffer!!.update()
            // Se abbiamo più di una texture dobbiamo impostare il selettore di
            // texture
            if (!layer!!.tiledMap.onlyOneTexture4Layer) {
                textureIndex!!.put(textureSelector).position(0)
            }
        }
        // con la definizione in vertex array abbiamo posizionato le tile
        // corrette
        // con gli offset andiamo a spostarli anche dei pixel subtile che
        // servono per
        // considerare lo scroll
        matrix.buildTranslationMatrix(-offsetX - screenCenterX, offsetY + screenCenterY, 0f)
        matrix.multiply(modelview!!, matrix)

        // utilizziamo il cursore per aggiornare
        // shape.updateOnCursor(tiledMap.onlyOneTexture4Layer);
        shader!!.setOpacity(layer!!.opacity)
        shader.setVertexCoordinatesArray(vertexBuffer!!)
        shader.setModelViewProjectionMatrix(matrix.asFloatBuffer())

        // Se abbiamo più di una texture dobbiamo impostare il selettore di
        // texture
        if (!layer!!.tiledMap.onlyOneTexture4Layer) {
            shader.setTextureSelectorArray(textureIndex)
        }

        // impostiamo le texture
        // shader.setTextureCoordinatesArray(0, shape.textures[0]);
        shader.setTextureCoordinatesArray(0, textureBuffer!!)

        // GLES20.glDrawArrays(shape.drawStyle.value, 0, shape.readCursor());
        shader.setIndexBuffer(indexBuffer!!)
        if (indexBuffer!!.allocation === BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(MeshDrawModeType.INDEXED_TRIANGLES.value, indexBuffer!!.cursor, GLES20.GL_UNSIGNED_SHORT, indexBuffer!!.buffer)
        } else {
            GLES20.glDrawElements(MeshDrawModeType.INDEXED_TRIANGLES.value, indexBuffer!!.cursor, GLES20.GL_UNSIGNED_SHORT, 0)
        }
        shader.unsetIndexBuffer(indexBuffer!!)
    }
}