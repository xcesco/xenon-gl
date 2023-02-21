package com.abubusoft.xenon.mesh.persistence.max3d;

/**
 * <p>Sempre e solo triangoli</p>
 * @author Francesco Benincasa
 *
 */
public class Max3dsModelData {

	public String name;
	/**
	 * startX 3
	 */
	public float[] vertices;
	/**
	 * startX 3
	 */
	public float[] normals;
	/**
	 * startX 2
	 */
	public float[] textCoords;
	/**
	 * startX 3
	 */
	public int[] indices;

	public Max3dsModelData(String value) {
		name=value;
	}

	public void setData(float[] aVertices, float[] aNormals, float[] aTexCoords, int[] aIndices) {		
		vertices=aVertices;
		normals=aNormals;
		textCoords=aTexCoords;
		indices=aIndices;
		
	}

}
