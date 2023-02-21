package com.abubusoft.xenon.mesh.modifiers;

import static com.abubusoft.xenon.mesh.MeshFactory.VERTEX_DIMENSION;

import com.abubusoft.xenon.mesh.QuadMesh;
import com.abubusoft.xenon.vbo.VertexBuffer;

/**
 * <p>
 * Modificatore degli shape a base Quad, ovvero tutti quelli che derivano da QuadMesh.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class VertexQuadModifier {

	/**
	 * <p>
	 * Imposta i vertici del quad in modo da avere un quadrato di width startX height.
	 * </p>
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
	 * <p>
	 * Imposta i vertici del quad in modo da avere un quadrato di width startX height.
	 * </p>
	 * 
	 * @param vertices
	 * 			vertex buffer da modificare
	 * @param quadIndex
	 * 			indice del quadrato da modificare
	 * @param left
	 * 			coordinata left
	 * @param top
	 * 			coordinate top
	 * @param witdh
	 * 			larghezza del rettangolo
	 * @param height
	 * 			altezza del rettangolo
	 * @param update
	 * 			aggiornare l'intero vertex buffer
	 */
	public static void setVertexCoords(VertexBuffer vertices, int quadIndex, float left, float top, float witdh, float height, boolean update) {
		float currentX = left;
		float highY = top;
		float lowY = top - height;
		float deltaX = witdh;

		int i = quadIndex * VertexBuffer.VERTEX_IN_QUAD_TILE*VertexBuffer.POSITION_DIMENSIONS;

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
	}

	/**
	 * Indica il tipo di swap della texture.
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum VerticesFlipType {
		HORIZONTAL, VERTICAL
	};

	/**
	 * Swap delle coordinate
	 * 
	 * @param sprite
	 * @param updateBuffer
	 *            se true indica che deve essere effettuato l'update del buffer dal float[]
	 * 
	 */
	public static void flipVertical(QuadMesh shape, boolean updateBuffer) {
		flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer);
	}

	/**
	 * Swap delle coordinate
	 * 
	 * @param sprite
	 * @param updateBuffer
	 *            se true indica che deve essere effettuato l'update del buffer dal float[]
	 */
	public static void flipHorizontal(QuadMesh shape, boolean updateBuffer) {
		flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer);
	}

	/**
	 * Dato uno shape, provvede ad invertire le coordinate della texture associata. Questo vuol dire, ad esempio, che se prima la texture va da 0 a 1 (in orizzontale), dopo
	 * l'applicazione di questo metodo andrà da 1 a 0.
	 * 
	 * @param shape
	 * @param allocation
	 * @param updateBuffer
	 *            se true indica che deve essere effettuato l'update del buffer dal float[]
	 */
	public static void flip(QuadMesh shape, VerticesFlipType type, boolean updateBuffer) {
		// bounding box non cambia

		// il ciclo cambia un vertice alla volta per fare in modo che il sistema
		// funzioni
		// su tutti i tipi di shape.
		int offset = 0;
		switch (type) {
		case HORIZONTAL:
			// offset startX
			offset = 0;
			break;
		case VERTICAL:
			// offset startX
			offset = 1;
			break;
		}

		int n = shape.vertexCount;

		// n * VERTEX_ELEMENT: per ogni vertice abbiamo 3 coordinate
		for (int i = 0; i < n * VERTEX_DIMENSION; i += VERTEX_DIMENSION) {
			shape.vertices.coords[i + offset] *= -1f;
		}

		// Quando passo un array ad un direct buffer devo poi riposizionare a 0ù
		if (updateBuffer)
			shape.vertices.update();
		// shape.vertices.put(shape.vertices.coords).position(0);

		// non richiede il calcolo del bounding sphere dato che non cambia
	}

}
