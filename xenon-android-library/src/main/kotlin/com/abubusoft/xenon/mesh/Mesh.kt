package com.abubusoft.xenon.mesh;

import java.io.Serializable;

import com.abubusoft.xenon.math.Dimension3;
import com.abubusoft.xenon.vbo.AttributeBuffer;
import com.abubusoft.xenon.vbo.ColorBuffer;
import com.abubusoft.xenon.vbo.IndexBuffer;
import com.abubusoft.xenon.vbo.TextureBuffer;
import com.abubusoft.xenon.vbo.VertexBuffer;
import com.abubusoft.xenon.core.Uncryptable;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindDisabled;
import com.abubusoft.kripton.annotation.BindType;

/**
 * <p>
 * Rappresenta uno shape di base. Le forme non hanno una posizione, una rotazione o quant'altro le possono collocare all'interno di uno spazio. Servono solo a descrivere la forma
 * di un oggetto.
 * </p>
 * 
 * <p>
 * Per collocare uno shape all'interno di uno spazio, bisogna utilizzare necessariamente un'entity.
 * </p>
 * 
 * <p>
 * Supporta:
 * <ul>
 * <li><strong>vertici</strong>: un set di vertici</li>
 * <li><strong>texture components</strong>: n set di coordinate uv</li>
 * <li>normali</li>
 * <li>indici</li>
 * <li><strong>colori</strong>: un colore per vertice</li>
 * </ul>
 * 
 * 
 * @author Francesco Benincasa
 * 
 */
@Uncryptable
@BindType
public class Mesh implements Serializable {
	
	/**
	 * tipo di shape {@link MeshType}
	 */
	@Bind
	public MeshType type;

	/**
	 * numero di coordinate per texel
	 */
	public final static int TEXTURE_DIMENSIONS = 2;

	/**
	 * numero di coordinate per vertice
	 */
	public final static int VERTICES_DIMENSIONS = 3;

	public static final int OFFSET_X = 0;

	public static final int OFFSET_Y = 1;

	public static final int OFFSET_Z = 2;

	@Bind
	public String name;

	private static final long serialVersionUID = -3939445945842866443L;

	/**
	 * Definiamo costruttore con scope package, in modo da non poter essere definito senza l'apposita factory
	 */
	Mesh() {
		// di default è su base triangolare.
		type=MeshType.TRIANGLES_BASED;
	}

	/*
	 * public int genSphere(int numSlices, float radius) { int i; int j; int numParallels = numSlices; int numVertices = (numParallels + 1) * (numSlices + 1); int numIndices =
	 * numParallels * numSlices * 6; float angleStep = ((2.0f * (float) Math.PI) / numSlices);
	 * 
	 * // Allocate memory for buffers vertices = ByteBuffer.allocateDirect(numVertices * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); normalsCoords =
	 * ByteBuffer.allocateDirect(numVertices * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); textureCoords = ByteBuffer.allocateDirect(numVertices * 2 *
	 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); indexes = ByteBuffer.allocateDirect(numIndices * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
	 * 
	 * for (i = 0; i < numParallels + 1; i++) { for (j = 0; j < numSlices + 1; j++) { int vertex = (i * (numSlices + 1) + j) * 3;
	 * 
	 * vertices.put(vertex + 0, (float) (radius * Math.sin(angleStep * (float) i) * Math.sin(angleStep * (float) j)));
	 * 
	 * vertices.put(vertex + 1, (float) (radius * Math.cos(angleStep * (float) i))); vertices.put(vertex + 2, (float) (radius * Math.sin(angleStep * (float) i) * Math.cos(angleStep
	 * * (float) j)));
	 * 
	 * normalsCoords.put(vertex + 0, vertices.get(vertex + 0) / radius); normalsCoords.put(vertex + 1, vertices.get(vertex + 1) / radius); normalsCoords.put(vertex + 2,
	 * vertices.get(vertex + 2) / radius);
	 * 
	 * int texIndex = (i * (numSlices + 1) + j) * 2; textureCoords.put(texIndex + 0, (float) j / (float) numSlices); textureCoords.put(texIndex + 1, (1.0f - (float) i) / (float)
	 * (numParallels - 1)); } }
	 * 
	 * int index = 0; for (i = 0; i < numParallels; i++) { for (j = 0; j < numSlices; j++) { indexes.put(index++, (short) (i * (numSlices + 1) + j)); indexes.put(index++, (short)
	 * ((i + 1) * (numSlices + 1) + j)); indexes.put(index++, (short) ((i + 1) * (numSlices + 1) + (j + 1)));
	 * 
	 * indexes.put(index++, (short) (i * (numSlices + 1) + j)); indexes.put(index++, (short) ((i + 1) * (numSlices + 1) + (j + 1))); indexes.put(index++, (short) (i * (numSlices +
	 * 1) + (j + 1)));
	 * 
	 * } } indexesCount = numIndices;
	 * 
	 * return numIndices; }
	 * 
	 * 
	 * public int genCube(float scaleFactor) { int i; int numVertices = 24; int numCubeIndices = 36;
	 * 
	 * float[] cubeVerts = { -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
	 * -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,
	 * -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, };
	 * 
	 * float[] cubeNormals = { 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
	 * 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
	 * 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, };
	 * 
	 * float[] cubeTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
	 * 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, };
	 * 
	 * // Allocate memory for buffers vertices = ByteBuffer.allocateDirect(numVertices * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); normalsCoords =
	 * ByteBuffer.allocateDirect(numVertices * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); textureCoords = ByteBuffer.allocateDirect(numVertices * 2 *
	 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); indexes = ByteBuffer.allocateDirect(vertexCount * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
	 * 
	 * vertices.put(cubeVerts).position(0); for (i = 0; i < numVertices * 3; i++) { vertices.put(i, vertices.get(i) * scaleFactor); }
	 * 
	 * normalsCoords.put(cubeNormals).position(0); textureCoords.put(cubeTex).position(0);
	 * 
	 * short[] cubeIndices = { 0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19, 20, 23, 22, 20, 22, 21 };
	 * 
	 * indexes.put(cubeIndices).position(0); vertexCount = numCubeIndices; return vertexCount; }
	 */

	/**
	 * vertici
	 */
	@Bind
	public VertexBuffer vertices;

	/**
	 * normali abilitate
	 */
	@Bind
	public boolean normalsEnabled;

	/**
	 * normali
	 */
	@Bind
	public VertexBuffer normals;

	/**
	 * texture abilitate
	 */
	@Bind
	public boolean texturesEnabled;

	/**
	 * numero di texture
	 */
	@Bind
	public int texturesCount;

	/**
	 * <p>
	 * Array di array delle coordinate nel mondo texture
	 * </p>
	 */
	@Bind
	public TextureBuffer[] textures;

	/**
	 * indica se gli indici sono abilitati
	 */
	@Bind
	public boolean indexesEnabled;

	/**
	 * indici
	 */
	@Bind
	public IndexBuffer indexes;

	/**
	 * tipo di disegno
	 */
	@Bind
	public MeshDrawModeType drawMode;

	/**
	 * numero di vertici presenti
	 */
	@Bind
	public int vertexCount;

	/**
	 * numero di indici presenti
	 */
	@Bind
	public int indexesCount;

	/**
	 * colori abilitati
	 */
	@Bind
	public boolean colorsEnabled;

	/**
	 * buffer dei colori. Ogni vertice ha 4 byte per colore
	 */
	@Bind
	public transient ColorBuffer colors;

	/**
	 * raggio della sfera che contiene l'intero oggetto
	 */
	@Bind
	public float boundingSphereRadius = 0;

	/**
	 * dimensioni del bounding box
	 */
	@Bind
	public Dimension3 boundingBox = new Dimension3();
		
	@Bind
	public boolean attributesEnabled;

	@Bind
	public int attributesCount;

	@BindDisabled
	public AttributeBuffer[] attributes;

	/**
	 * <p>
	 * Effettua l'aggiornamento di tutti i buffer che non sono di tipo STATIC.
	 * </p>
	 */
	public void updateBuffers() {
		// vertici
		// vertices.put(verticesCoords).position(0);
		if (vertices.isUpdatable()) {
			vertices.update();
		}
	
		// texture
		if (texturesEnabled) {
			for (int i = 0; i < textures.length; i++) {
				if (textures[i].isUpdatable()) {
					textures[i].update(); /* put(texturesCoords[i]).position(0); */
				}
			}
		}
	
		// colori
		if (colorsEnabled && colors.isUpdatable()) {
			colors.update();
		}
	
		// indici
		if (indexesEnabled && indexes.isUpdatable()) {	
			indexes.update();
		}
	}
}
