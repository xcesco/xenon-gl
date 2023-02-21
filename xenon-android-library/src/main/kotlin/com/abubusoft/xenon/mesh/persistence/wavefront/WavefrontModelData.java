package com.abubusoft.xenon.mesh.persistence.wavefront;

import java.util.ArrayList;

/**
 * <p>
 * Modello intermedio. Serve a passare dal modello del formato grafico di input outpu a quello utilizzato da Xenon.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class WavefrontModelData {
	
	public String name;

	/**
	 * <p>
	 * Tutte le informazioni possibili su un vertice sono contenute qua.
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public static class FlatVertexF {
		int index;
		VertexF vertex;
		UVCoord tex;
		VertexF normal;
	}

	/**
	 * coordinate di un vertex
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public static class VertexF {
		public float x;
		public float y;
		public float z;
	}

	/**
	 * coordinate 2d di una texture
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public static class UVCoord {
		public float u;
		public float v;
	}

	/**
	 * un triangolo.
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public static class Face {

		public final static int INDEX_VERTEX0 = 0;
		public final static int INDEX_VERTEX1 = 1;
		public final static int INDEX_VERTEX2 = 2;

		public Face() {
			vertexIndex = new int[3];
			textureIndex = new int[3];
			normalIndex = new int[3];

			indexes = new int[3];
		}

		public int[] vertexIndex;
		public int[] textureIndex;
		public int[] normalIndex;

		/**
		 * 
		 */
		public int[] indexes;
	}

	public ArrayList<VertexF> vertices;
	public ArrayList<UVCoord> tex;
	public ArrayList<Face> triangles;
	public ArrayList<VertexF> normals;
}