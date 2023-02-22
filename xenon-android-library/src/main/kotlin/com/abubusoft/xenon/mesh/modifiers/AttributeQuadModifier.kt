package com.abubusoft.xenon.mesh.modifiers

import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.AttributeBuffer
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType
import com.abubusoft.xenon.vbo.VertexBuffer

object AttributeQuadModifier {

    /**
     * Imposta i vertici del quad in modo da avere un quadrato di width startX height.
     *
     * @param attributes
     * vertex buffer da modificare
     * @param quadIndex
     * indice del quadrato da modificare
     * @param value1
     * @param value2
     * @param update
     * aggiornare l'intero vertex buffer
     */
    fun setVertexAttributes2(attributes: AttributeBuffer, quadIndex: Int, value1: Float, value2: Float, update: Boolean) {
        val i: Int = quadIndex * AbstractBuffer.VERTEX_IN_QUAD_TILE * AttributeDimensionType.DIM_2.value

        // vertex higher startX, startY, z
        attributes.coords!![i + 0] = value1
        attributes.coords!![i + 1] = value2

        // vertex lower startX, startY, z
        attributes.coords!![i + 2] = value1
        attributes.coords!![i + 3] = value2

        // vertex higher startX, startY, z
        attributes.coords!![i + 4] = value1
        attributes.coords!![i + 5] = value2

        // vertex higher startX, startY, z
        attributes.coords!![i + 6] = value1
        attributes.coords!![i + 7] = value2
        if (update) {
            // ASSERT: aggiorno subito il buffer
            attributes.update()
        }
    }
}