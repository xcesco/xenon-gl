package com.abubusoft.xenon.mesh.persistence.wavefront;

import java.util.ArrayList;
import java.util.HashMap;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshDrawModeType;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshHelper;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.modifiers.ColorModifier;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.Face;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.FlatVertexF;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.UVCoord;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.VertexF;
import com.abubusoft.xenon.vbo.TextureBuffer;
import com.abubusoft.xenon.vbo.BufferManager;
import com.abubusoft.xenon.vbo.BufferAllocationOptions;

import android.annotation.SuppressLint;
import android.graphics.Color;

public abstract class WavefrontAdapter {

	/**
	 * <p>
	 * Builder delle chiavi per identificare univocamente i vertici.
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public static class VertexKeyBuilder {
		private int off1;
		private int off2;
		private int normalEnabler;
		private int textureEnabler;

		public VertexKeyBuilder(WavefrontModelData model, MeshOptions options) {
			int numVertex = model.vertices.size();
			int numTexture = options.textureEnabled ? model.tex.size() : 1;
			normalEnabler = options.normalsEnabled ? 1 : 0;
			textureEnabler = options.textureEnabled ? 1 : 0;

			off1 = XenonMath.findNextPowerOf10(numVertex);
			off2 = XenonMath.findNextPowerOf10(numTexture);
		}

		/**
		 * <p>
		 * Recupera UID della tripletta vertice/texture/normal. Se gli ultimi due non hanno valore valido, non vengono presi in considerazione.
		 * </p>
		 * 
		 * @param vertexIndex
		 * @param textureIndex
		 * @param normalIndex
		 * @return
		 */
		public int getUID(int vertexIndex, int textureIndex, int normalIndex) {
			int value;
			value = vertexIndex + (textureIndex >= 0 ? textureIndex * (off1 * textureEnabler) : 0) + (normalIndex >= 0 ? normalIndex * (off1 * off2 * normalEnabler) : 0);

			return value;
		}
	}

	/**
	 * <p>
	 * converte il model in una mesh i cui vertici sono indicizzati.
	 * </p>
	 * 
	 * @param model
	 * @param shape
	 * @param options
	 */
	@SuppressLint("UseSparseArrays")
	protected static void convertModelToIndexedMesh(WavefrontModelData model, Mesh shape, MeshOptions options) {
		HashMap<Integer, Integer> mapFlatVerticeKeys = new HashMap<Integer, Integer>();
		ArrayList<FlatVertexF> flatVertices = new ArrayList<FlatVertexF>();
		VertexKeyBuilder vkb = new VertexKeyBuilder(model, options);

		int subvertexIndex = 0;
		Face currentTriangle;
		VertexF v;
		float[] mins = new float[3];
		float[] maxs = new float[3];

		int uid;
		int vertexIndex;
		FlatVertexF fv;

		// mesh name
		shape.name = model.name;

		int numTriangles = model.triangles.size();

		// iteriamo sui triangoli per ottenere l'elenco dei vertici flat
		for (int i = 0; i < numTriangles; i++) {
			currentTriangle = model.triangles.get(i);

			// ci occupiamo dei vertici
			for (int si = 0; si < MeshFactory.VERTEX_PER_TRIANGLE; si++) {
				// work for boundingbox
				v = model.vertices.get(currentTriangle.vertexIndex[si]);
				MeshHelper.buildMinMaxArray(v.x, v.y, v.z, mins, maxs);

				// ricaviamo values
				uid = vkb.getUID(currentTriangle.vertexIndex[si], currentTriangle.textureIndex[si], currentTriangle.normalIndex[si]);

				if (!mapFlatVerticeKeys.containsKey(uid)) {
					// ASSERT: non abbiamo l'elemento inserito
					fv = new FlatVertexF();
					fv.vertex = model.vertices.get(currentTriangle.vertexIndex[si]);

					if (currentTriangle.textureIndex[si] >= 0) {
						fv.tex = model.tex.get(currentTriangle.textureIndex[si]);
					}

					if (currentTriangle.normalIndex[si] >= 0) {
						fv.normal = model.normals.get(currentTriangle.normalIndex[si]);
					}

					flatVertices.add(fv);
					vertexIndex = flatVertices.size() - 1;
					fv.index = vertexIndex;
					mapFlatVerticeKeys.put(uid, vertexIndex);
				} else {
					fv = flatVertices.get(mapFlatVerticeKeys.get(uid));
				}

				// per il vertice, registriamo la sua chiave
				currentTriangle.indexes[si] = fv.index;
			}
		}

		MeshHelper.defineBoundaries(shape, mins, maxs);

		// ora abbiamo tutto, possiamo costruire i vari array

		// vertici
		int numVertices = flatVertices.size();
		shape.vertexCount = flatVertices.size();
		shape.vertices = BufferManager.instance().createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation);

		subvertexIndex = 0;
		for (int i = 0; i < numVertices; i++) {
			FlatVertexF fvf = flatVertices.get(i);

			shape.vertices.coords[subvertexIndex + 0] = fvf.vertex.x;
			shape.vertices.coords[subvertexIndex + 1] = fvf.vertex.y;
			shape.vertices.coords[subvertexIndex + 2] = fvf.vertex.z;

			subvertexIndex += MeshFactory.VERTEX_DIMENSION;
		}
		shape.vertices.update();

		// texture
		if (options.textureEnabled && model.tex.size() > 0) {
			shape.texturesEnabled = true;
			// impostiamo il numero di texture
			shape.texturesCount = options.texturesCount;
			// numero di texture
			// shape.texturesCoords = new float[options.texturesCount][];
			shape.textures = new TextureBuffer[options.texturesCount];

			// allochiamo i texture buffer
			for (int a = 0; a < shape.textures.length; a++) {
				shape.textures[a] = BufferManager.instance().createTextureBuffer(numVertices, options.bufferOptions.textureAllocation);
				// shape.textures[a] = ByteBuffer.allocateDirect(numVertices * MeshFactory.TEXTURE_DIMENSION *
				// MeshFactory.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
			}

			subvertexIndex = 0;
			for (int i = 0; i < numVertices; i++) {
				FlatVertexF fvf = flatVertices.get(i);

				// ci occupiamo delle texture
				for (int a = 0; a < shape.textures.length; a++) {
					shape.textures[a].coords[subvertexIndex + 0] = fvf.tex.u;

					// normalmente quando esportiamo, invertiamo startY con z.
					// mettiamo 1 - v, per avere lo stesso effetto
					// su 3d studio max.
					shape.textures[a].coords[subvertexIndex + 1] = 1 - fvf.tex.v;
				}

				subvertexIndex += MeshFactory.TEXTURE_DIMENSION;
			}

			for (int a = 0; a < shape.textures.length; a++) {
				shape.textures[a].update();
				// shape.textures[a].put(shape.texturesCoords[a]).position(0);
			}

		} else {
			shape.texturesEnabled = false;
		}

		// normali
		if (options.normalsEnabled && model.normals.size() > 0) {
			shape.normalsEnabled = true;
			shape.normals = BufferManager.instance().createVertexBuffer(numVertices, options.bufferOptions.normalAllocation);

			subvertexIndex = 0;
			// iteriamo sui triangoli
			for (int i = 0; i < numTriangles; i++) {
				FlatVertexF fvf = flatVertices.get(i);

				// ci occupiamo dei vertici
				for (int si = 0; si < MeshFactory.VERTEX_PER_TRIANGLE; si++) {
					shape.normals.coords[subvertexIndex + 0] = fvf.normal.x;
					shape.normals.coords[subvertexIndex + 1] = fvf.normal.y;
					shape.normals.coords[subvertexIndex + 2] = fvf.normal.z;
				}
				subvertexIndex += MeshFactory.TEXTURE_DIMENSION;
			}
		} else {
			shape.normalsEnabled = false;
		}

		// colore
		if (options.colorsEnabled) {
			shape.colorsEnabled = true;
			shape.colors = BufferManager.instance().createColorBuffer(numVertices, options.bufferOptions.vertexAllocation);

			ColorModifier.setColor(shape, Color.WHITE, true);

		} else {
			shape.colorsEnabled = false;
		}

		// indici: ci sono sempre
		shape.indexesCount = numTriangles * MeshFactory.VERTEX_PER_TRIANGLE;
		shape.indexesEnabled = true;
		shape.indexes = BufferManager.instance().createIndexBuffer(numTriangles * MeshFactory.VERTEX_PER_TRIANGLE, options.bufferOptions.indexAllocation);
		// ByteBuffer.allocateDirect( * MeshFactory.SHORT_SIZE).order(ByteOrder.nativeOrder()).asShortBuffer();

		subvertexIndex = 0;
		// iteriamo sui triangoli
		for (int i = 0; i < numTriangles; i++) {
			currentTriangle = model.triangles.get(i);

			// ci occupiamo dei vertici
			shape.indexes.values[subvertexIndex + 0] = (short) currentTriangle.indexes[0];
			shape.indexes.values[subvertexIndex + 1] = (short) currentTriangle.indexes[1];
			shape.indexes.values[subvertexIndex + 2] = (short) currentTriangle.indexes[2];

			subvertexIndex += MeshFactory.VERTEX_PER_TRIANGLE;
		}
		shape.indexes.update();

		// stile di disegno
		shape.drawMode = MeshDrawModeType.INDEXED_TRIANGLES;

		mapFlatVerticeKeys.clear();
		flatVertices.clear();
	}

	/**
	 * Converte il model in una mesh senza indici.
	 * 
	 * @param model
	 * @param shape
	 * @param options
	 */
	protected static void convertModelToMesh(WavefrontModelData model, Mesh shape, MeshOptions options) {
		int numVertices = model.triangles.size() * MeshFactory.VERTEX_PER_TRIANGLE;

		// vertici
		shape.vertexCount = numVertices;
		// shape.vertices = ByteBuffer.allocateDirect(numVertices * MeshFactory.VERTEX_DIMENSION * MeshFactory.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
		shape.vertices = BufferManager.instance().createVertexBuffer(numVertices, BufferAllocationOptions.build().vertexAllocation);
		// shape.vertices.position(0);
		// shape.verticesCoords = new float[numVertices * MeshFactory.VERTEX_DIMENSION];

		int subvertexIndex = 0;
		Face currentFace;
		VertexF v;

		// mesh name
		shape.name = model.name;

		float[] mins = new float[3];
		float[] maxs = new float[3];

		int numTriangles = model.triangles.size();

		// iteriamo sui triangoli
		for (int i = 0; i < numTriangles; i++) {
			currentFace = model.triangles.get(i);

			// ci occupiamo dei vertici
			for (int si = 0; si < MeshFactory.VERTEX_PER_TRIANGLE; si++) {
				v = model.vertices.get(currentFace.vertexIndex[si]);
				subvertexIndex = (i * MeshFactory.VERTEX_PER_TRIANGLE * MeshFactory.VERTEX_DIMENSION) + si * MeshFactory.VERTEX_DIMENSION;
				shape.vertices.coords[subvertexIndex + 0] = v.x;
				shape.vertices.coords[subvertexIndex + 1] = v.y;
				shape.vertices.coords[subvertexIndex + 2] = v.z;

				if (mins[0] > v.x)
					mins[0] = v.x;
				if (mins[1] > v.y)
					mins[1] = v.y;
				if (mins[2] > v.z)
					mins[2] = v.z;

				if (maxs[0] < v.x)
					maxs[0] = v.x;
				if (maxs[1] < v.y)
					maxs[1] = v.y;
				if (maxs[2] < v.z)
					maxs[2] = v.z;

				// i += MeshFactory.VERTEX_DIMENSION;
			}
		}

		// shape.vertices.put(shape.verticesCoords).position(0);
		// if (options.vertexBufferOptions.updateAfterCreation) {
		shape.vertices.update();
		// }

		// impostiamo boundingbox
		shape.boundingBox.set(Math.abs(maxs[0] - mins[0]), Math.abs(maxs[1] - mins[1]), Math.abs(maxs[2] - mins[2]));

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
		shape.boundingSphereRadius = (float) (0.8660254037844386 * shape.boundingBox.width);

		// texture
		if (options.textureEnabled && model.tex.size() > 0) {
			shape.texturesEnabled = true;
			// impostiamo il numero di texture
			shape.texturesCount = options.texturesCount;
			// numero di texture
			// shape.texturesCoords = new float[options.texturesCount][];
			shape.textures = new TextureBuffer[options.texturesCount];

			// allochiamo i texture buffer
			for (int a = 0; a < shape.textures.length; a++) {
				shape.textures[a] = BufferManager.instance().createTextureBuffer(numVertices, options.bufferOptions.textureAllocation);
				// shape.textures[a] = ByteBuffer.allocateDirect(numVertices * MeshFactory.TEXTURE_DIMENSION *
				// MeshFactory.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
			}

			UVCoord uv;
			// iteriamo sui triangoli
			for (int i = 0; i < numTriangles; i++) {
				currentFace = model.triangles.get(i);

				// ci occupiamo delle texture
				for (int si = 0; si < MeshFactory.VERTEX_PER_TRIANGLE; si++) {
					uv = model.tex.get(currentFace.textureIndex[si]);
					subvertexIndex = (i * MeshFactory.VERTEX_PER_TRIANGLE * MeshFactory.TEXTURE_DIMENSION) + si * MeshFactory.TEXTURE_DIMENSION;

					for (int a = 0; a < shape.textures.length; a++) {
						shape.textures[a].coords[subvertexIndex + 0] = uv.u;

						// normalmente quando esportiamo, invertiamo startY con z.
						// mettiamo 1 - v, per avere lo stesso effetto
						// su 3d studio max.
						shape.textures[a].coords[subvertexIndex + 1] = 1 - uv.v;
					}

					// i += MeshFactory.TEXTURE_DIMENSION;
				}
			}

			for (int a = 0; a < shape.textures.length; a++) {
				shape.textures[a].update();
			}

		} else {
			shape.texturesEnabled = false;
		}

		// normali
		if (options.normalsEnabled && model.normals.size() > 0) {
			shape.normalsEnabled = true;
			shape.normals = BufferManager.instance().createVertexBuffer(numVertices, options.bufferOptions.normalAllocation);

			// iteriamo sui triangoli
			for (int i = 0; i < numTriangles; i++) {
				currentFace = model.triangles.get(i);

				// ci occupiamo dei vertici
				for (int si = 0; si < MeshFactory.VERTEX_PER_TRIANGLE; si++) {
					v = model.normals.get(currentFace.normalIndex[si]);
					subvertexIndex = (i * MeshFactory.VERTEX_PER_TRIANGLE) + si * MeshFactory.VERTEX_DIMENSION;
					shape.normals.coords[subvertexIndex + 0] = v.x;
					shape.normals.coords[subvertexIndex + 1] = v.y;
					shape.normals.coords[subvertexIndex + 2] = v.z;

					i += MeshFactory.VERTEX_DIMENSION;
				}
			}

			shape.normals.update();
		} else {
			shape.normalsEnabled = false;
		}

		// colore
		if (options.colorsEnabled) {
			shape.colorsEnabled = true;
			shape.colors = BufferManager.instance().createColorBuffer(shape.vertexCount, options.bufferOptions.colorAllocation);

			ColorModifier.setColor(shape, Color.WHITE, true);

			shape.colors.update();

		} else {
			shape.colorsEnabled = false;
		}

		// indici: non ci sono indici
		shape.indexesCount = 0;
		shape.indexesEnabled = false;

		// stile di disegno
		shape.drawMode = MeshDrawModeType.TRIANGLES;
	}

	/**
	 * <p>
	 * Converte il model in una mesh
	 * </p>
	 * 
	 * @param model
	 * @param options
	 * @return mesh
	 */
	public static Mesh convertModelToShape(WavefrontModelData model, MeshOptions options) {
		Mesh mesh = MeshHelper.create(options);

		if (options.indicesEnabled) {
			convertModelToIndexedMesh(model, mesh, options);
		} else {
			convertModelToMesh(model, mesh, options);
		}

		return mesh;

	}
}
