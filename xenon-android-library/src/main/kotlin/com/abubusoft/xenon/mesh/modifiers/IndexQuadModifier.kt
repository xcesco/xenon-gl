package com.abubusoft.xenon.mesh.modifiers

import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.IndexBuffer
import com.abubusoft.xenon.vbo.VertexBuffer

object IndexQuadModifier {
    /**
     *
     *
     *
     *
     * @param indexBuffer
     * @param cursor
     * @param numTile
     * @param b
     */
    fun setIndexes(indexBuffer: IndexBuffer, cursor: Int, numTile: Int, update: Boolean) {
        var numTile = numTile
        val c = indexBuffer.cursor

        // dobbiamo convertire da # di tile a # di indice vertice. In ogni tile ci sono 4 vertici, quindi il passaggio è semplice.
        numTile *= AbstractBuffer.VERTEX_IN_QUAD_TILE
        indexBuffer.values!![c + 0] = (numTile + 0).toShort()
        indexBuffer.values!![c + 1] = (numTile + 1).toShort()
        indexBuffer.values!![c + 2] = (numTile + 2).toShort()
        indexBuffer.values!![c + 3] = (numTile + 2).toShort()
        indexBuffer.values!![c + 4] = (numTile + 3).toShort()
        indexBuffer.values!![c + 5] = (numTile + 0).toShort()
        if (update) {
            indexBuffer.update()
        }
    }
}