package com.abubusoft.xenon.mesh.modifiers;

import static com.abubusoft.xenon.mesh.MeshFactory.VERTEX_DIMENSION;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshHelper;
import com.abubusoft.xenon.mesh.MeshSprite;
import com.abubusoft.xenon.vbo.VertexBuffer;

/**
 * <p>
 * Modificatore dei vertici.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class VertexModifier {

	public interface VertexListener {
		void onApply(int index, Point3 inputVertex, Point3 outputVertex);
	}
	
	/**
	 * Itera su tutti i vertici della mesh e vi applica una trasformazione di scaling. Alla fine dell'iterazione,
	 * aggiorna anche il boundingBox e la boundingSphere.
	 * 
	 * @param mesh
	 * 		mesh sulla quale applicare la modifica
	 * @param scaleFactor
	 * 		fattore di scala da applicare
	 * @param update
	 * 		se true indica di aggiornare direttament anche i native buffer.
	 */
	public static void scale(Mesh mesh, final float scaleFactor, boolean update) {
		iterate(mesh, new VertexListener() {
			
			@Override
			public void onApply(int index, Point3 inputVertex, Point3 outputVertex) {				
				outputVertex.setCoords(scaleFactor*inputVertex.x, scaleFactor*inputVertex.y, scaleFactor*inputVertex.z);
			}
		}, update);	
	}

	/**
	 * Itera su tutti i vertici della mesh e vi applica la trasformazione desiderata. Alla fine dell'iterazione,
	 * aggiorna anche il boundingBox e la boundingSphere.
	 * 
	 * @param mesh
	 * 		mesh sulla quale applicare la modifica
	 * @param listener
	 * 		modifica da applicare
	 * @param update
	 * 		se true indica di aggiornare direttament anche i native buffer.
	 */
	public static void iterate(Mesh mesh, VertexListener listener, boolean update) {

		float[] mins = new float[3];
		float[] maxs = new float[3];
		Point3 result = new Point3();
		Point3 input = new Point3();
		int vertexIndex = 0;
		
		float[] coords=mesh.vertices.coords;
		int n=coords.length;
		
		for (int i = 0; i < n; i += VertexBuffer.POSITION_DIMENSIONS) {
			input.setCoords(coords[i + 0], coords[i + 1], coords[i + 2]);
			input.copyInto(result);

			listener.onApply(vertexIndex, input, result);

			coords[i + 0] = result.x;
			coords[i + 1] = result.y;
			coords[i + 2] = result.z;

			MeshHelper.buildMinMaxArray(input.x, input.y, input.z, mins, maxs);
			if (mins[0] > result.x)
				mins[0] = result.x;
			if (mins[1] > result.y)
				mins[1] = result.y;
			if (mins[2] > result.z)
				mins[2] = result.z;

			if (maxs[0] < result.x)
				maxs[0] = result.x;
			if (maxs[1] < result.y)
				maxs[1] = result.y;
			if (maxs[2] < result.z)
				maxs[2] = result.z;

			vertexIndex++;
		}

		// impostiamo boundingbox
		mesh.boundingBox.set(Math.abs(maxs[0] - mins[0]), Math.abs(maxs[1] - mins[1]), Math.abs(maxs[2] - mins[2]));

		// calcoliamo boundingSphere radius, ovvero il raggio della sfera che
		// contiene lo shape
		// Se parti da una sfera avente un raggio di 5 cm, il cubo inscritto è
		// quel cubo che può essere inserito esattamente nella tua sfera, in
		// modo tale che la distanza tra due vertici del cubo che siano opposti
		// tra loro misuri in lunghezza 10 cm (che è il diametro della sfera di
		// partenza). Per calcolare con precisione la lunghezza dello spigolo
		// del cubo inscritto devi dividere il diametro della sfera per la
		// radice quadrata di 3 (1,732 circa).
		// http://vivalascuola.studenti.it/come-determinare-le-misure-di-cubi-legati-a-sfere-140075.html#steps_2
		mesh.boundingSphereRadius = (float) (0.8660254037844386 * mesh.boundingBox.width);

		if (update)
			mesh.vertices.update();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param shape
	 * @param widthValue
	 * @param heightValue
	 * @param update
	 */
	public static void setDimension(MeshSprite shape, float widthValue, float heightValue, boolean update) {
		float startX = -widthValue / 2f;
		// impostiamo boundingbox
		shape.boundingBox.set(widthValue, heightValue, 0f);

		float currentX = startX;
		float highY = heightValue / 2.0f;
		float lowY = -highY;
		float deltaX = widthValue;

		for (int i = 0; i < VertexBuffer.VERTEX_IN_QUAD_TILE * VertexBuffer.POSITION_DIMENSIONS; i += VertexBuffer.VERTEX_IN_QUAD_TILE * VertexBuffer.POSITION_DIMENSIONS) {
			// vertex higher startX, startY, z
			shape.vertices.coords[i + 0] = currentX;
			shape.vertices.coords[i + 1] = highY;
			shape.vertices.coords[i + 2] = 0.0f;

			// vertex lower startX, startY, z
			shape.vertices.coords[i + 3] = currentX;
			shape.vertices.coords[i + 4] = lowY;
			shape.vertices.coords[i + 5] = 0.0f;

			// vertex higher startX, startY, z
			shape.vertices.coords[i + 6] = currentX + deltaX;
			shape.vertices.coords[i + 7] = lowY;
			shape.vertices.coords[i + 8] = 0.0f;

			// triangolo 2

			// vertex higher startX, startY, z
			shape.vertices.coords[i + 9] = currentX + deltaX;
			shape.vertices.coords[i + 10] = highY;
			shape.vertices.coords[i + 11] = 0.0f;

			currentX += deltaX;
		}
		// shape.vertices.put(shape.verticesCoords).position(0);
		if (update) {
			// ASSERT: aggiorno subito il buffer
			shape.vertices.update();
		}
		// calcoliamo boundingSphere radius, ovvero il raggio della sfera che
		// contiene lo shape
		shape.boundingSphereRadius = (float) Math.sqrt(XenonMath.squareDistanceFromOrigin(shape.vertices.coords[0], shape.vertices.coords[1], shape.vertices.coords[2]));
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
	 * @param shape
	 * @param updateBuffer
	 *            se true indica che deve essere effettuato l'update del buffer dal float[]
	 * 
	 */
	public static void flipVertical(Mesh shape, boolean updateBuffer) {
		flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer);
	}

	/**
	 * Swap delle coordinate
	 * 
	 * @param shape
	 * @param updateBuffer
	 *            se true indica che deve essere effettuato l'update del buffer dal float[]
	 */
	public static void flipHorizontal(Mesh shape, boolean updateBuffer) {
		flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer);
	}

	/**
	 * Dato uno shape, provvede ad invertire le coordinate della texture associata. Questo vuol dire, ad esempio, che se prima la texture va da 0 a 1 (in orizzontale), dopo l'applicazione di questo metodo andrà da 1 a 0.
	 * 
	 * @param shape
	 * @param type
	 * @param updateBuffer
	 *            se true indica che deve essere effettuato l'update del buffer dal float[]
	 */
	public static void flip(Mesh shape, VerticesFlipType type, boolean updateBuffer) {
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
