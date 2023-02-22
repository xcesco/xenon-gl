package com.abubusoft.xenon.mesh.modifiers

import com.abubusoft.xenon.mesh.MeshFactory.VERTEX_DIMENSION
import com.abubusoft.xenon.mesh.QuadMesh
import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 *
 *
 * Modificatore degli shape a base Quad, ovvero tutti quelli che derivano da QuadMesh.
 *
 *
 * @author Francesco Benincasa
 */
object VertexQuadModifier {
    /**
     *
     *
     * Imposta i vertici del quad in modo da avere un quadrato di width startX height.
     *
     *
     * @param shape
     * @param widthValue
     * @param heightValue
     * @param update
     */
    /*public static void setDimension(VertexBuffer vertices, float widthValue, float heightValue, boolean update) {
		float startX = -widthValue / 2f;

		float currentX = startX;
		float highY = heightValue / 2.0f;
		float lowY = -highY;
		float deltaX = widthValue;

		int i = 0;

		// vertex higher startX, startY, z
		vertices.coords[i + 0] = currentX;
		vertices.coords[i + 1] = highY;
		vertices.coords[i + 2] = 0.0f;

		// vertex lower startX, startY, z
		vertices.coords[i + 3] = currentX;
		vertices.coords[i + 4] = lowY;
		vertices.coords[i + 5] = 0.0f;

		// vertex higher startX, startY, z
		vertices.coords[i + 6] = currentX + deltaX;
		vertices.coords[i + 7] = lowY;
		vertices.coords[i + 8] = 0.0f;

		// vertex higher startX, startY, z
		vertices.coords[i + 9] = currentX + deltaX;
		vertices.coords[i + 10] = highY;
		vertices.coords[i + 11] = 0.0f;

		currentX += deltaX;
		// shape.vertices.put(shape.verticesCoords).position(0);
		if (update) {
			// ASSERT: aggiorno subito il buffer
			vertices.update();
		}
	}*/
    /**
     *
     *
     * Imposta i vertici del quad in modo da avere un quadrato di width startX height.
     *
     *
     * @param vertices
     * vertex buffer da modificare
     * @param quadIndex
     * indice del quadrato da modificare
     * @param left
     * coordinata left
     * @param top
     * coordinate top
     * @param witdh
     * larghezza del rettangolo
     * @param height
     * altezza del rettangolo
     * @param update
     * aggiornare l'intero vertex buffer
     */
    fun setVertexCoords(vertices: VertexBuffer, quadIndex: Int, left: Float, top: Float, witdh: Float, height: Float, update: Boolean) {
        var currentX = left
        val lowY = top - height
        val i: Int = quadIndex * AbstractBuffer.VERTEX_IN_QUAD_TILE * VertexBuffer.POSITION_DIMENSIONS

        // vertex higher startX, startY, z
        vertices.coords!![i + 0] = currentX
        vertices.coords!![i + 1] = top
        vertices.coords!![i + 2] = 0.0f

        // vertex lower startX, startY, z
        vertices.coords!![i + 3] = currentX
        vertices.coords!![i + 4] = lowY
        vertices.coords!![i + 5] = 0.0f

        // vertex higher startX, startY, z
        vertices.coords!![i + 6] = currentX + witdh
        vertices.coords!![i + 7] = lowY
        vertices.coords!![i + 8] = 0.0f

        // vertex higher startX, startY, z
        vertices.coords!![i + 9] = currentX + witdh
        vertices.coords!![i + 10] = top
        vertices.coords!![i + 11] = 0.0f
        currentX += witdh
        // shape.vertices.put(shape.verticesCoords).position(0);
        if (update) {
            // ASSERT: aggiorno subito il buffer
            vertices.update()
        }
    }

    /**
     * Swap delle coordinate
     *
     * @param sprite
     * @param updateBuffer
     * se true indica che deve essere effettuato l'update del buffer dal float[]
     */
    fun flipVertical(shape: QuadMesh, updateBuffer: Boolean) {
        flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer)
    }

    /**
     * Swap delle coordinate
     *
     * @param sprite
     * @param updateBuffer
     * se true indica che deve essere effettuato l'update del buffer dal float[]
     */
    fun flipHorizontal(shape: QuadMesh, updateBuffer: Boolean) {
        flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer)
    }

    /**
     * Dato uno shape, provvede ad invertire le coordinate della texture associata. Questo vuol dire, ad esempio, che se prima la texture va da 0 a 1 (in orizzontale), dopo
     * l'applicazione di questo metodo andrà da 1 a 0.
     *
     * @param shape
     * @param allocation
     * @param updateBuffer
     * se true indica che deve essere effettuato l'update del buffer dal float[]
     */
    fun flip(shape: QuadMesh, type: VerticesFlipType, updateBuffer: Boolean) {
        // bounding box non cambia

        // il ciclo cambia un vertice alla volta per fare in modo che il sistema
        // funzioni
        // su tutti i tipi di shape.
        val offset = when (type) {
            VerticesFlipType.HORIZONTAL ->            // offset startX
                0
            VerticesFlipType.VERTICAL ->            // offset startX
                1
        }
        val n = shape.vertexCount

        // n * VERTEX_ELEMENT: per ogni vertice abbiamo 3 coordinate
        var i = 0
        while (i < n * VERTEX_DIMENSION) {
            shape.vertices!!.coords!![i + offset] *= -1f
            i += VERTEX_DIMENSION
        }

        // Quando passo un array ad un direct buffer devo poi riposizionare a 0ù
        if (updateBuffer) shape.vertices!!.update()
        // shape.vertices.put(shape.vertices.coords).position(0);

        // non richiede il calcolo del bounding sphere dato che non cambia
    }

    /**
     * Indica il tipo di swap della texture.
     *
     * @author Francesco Benincasa
     */
    enum class VerticesFlipType {
        HORIZONTAL, VERTICAL
    }
}