package com.abubusoft.xenon.mesh.persistence.max3d;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.xenon.math.Vector3;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;

/**
 * 3DS object parser. This is a work in progress. Materials aren't parsed yet.
 * 
 * @author dennis.ippel
 * @author lacasrac
 * 
 */
public class Max3DSLoader extends MeshParserBase {

	public enum ChunkType {
			IDENTIFIER_3DS(0x4D4D),
			MESH_BLOCK(0x3D3D),
			OBJECT_BLOCK(0x4000),
			TRIMESH(0x4100),
			VERTICES(0x4110),
			FACES(0x4120),
			TEXCOORD(0x4140),
			TEX_MAP(0xA200),
			TRI_MATERIAL(0x4130),
			TEX_NAME(0xA000),
			TEX_FILENAME(0xA300),
			MATERIAL(0xAFFF);

		public final int value;

		private ChunkType(int value) {
			this.value = value;
		}

		public static ChunkType parseValue(int v) {
			for (ChunkType item : values()) {
				if (v == item.value)
					return item;
			}

			return null;

		}
	}

	private ArrayList<ArrayList<Vector3>> mVertices = new ArrayList<ArrayList<Vector3>>();
	private ArrayList<Vector3[]> mNormals = new ArrayList<Vector3[]>();
	private ArrayList<ArrayList<Vector3>> mVertNormals = new ArrayList<ArrayList<Vector3>>();
	private ArrayList<ArrayList<Vector3>> mTexCoords = new ArrayList<ArrayList<Vector3>>();
	private ArrayList<ArrayList<Integer>> mIndices = new ArrayList<ArrayList<Integer>>();
	private ArrayList<String> mObjNames = new ArrayList<String>();

	private int mChunkID;
	private int mChunkEndOffset;
	private boolean mEndReached = false;
	private int mObjects = -1;
	private ArrayList<Max3dsModelData> models;

	public static ArrayList<Max3dsModelData> parse(InputStream stream) throws IOException {
		Max3DSLoader parser = new Max3DSLoader();
		return parser.parseInternal(stream);
	}

	protected ArrayList<Max3dsModelData> parseInternal(InputStream stream) throws IOException {
		models = new ArrayList<Max3dsModelData>();
		Logger.info("Start parsing 3DS");

		readHeader(stream);
		if (mChunkID != ChunkType.IDENTIFIER_3DS.value) {
			Logger.error("Not a valid 3DS file");
			return null;
		}

		while (!mEndReached) {
			readChunk(stream);
		}

		build();

		// TODO
		/*
		 * if (mRootObject.getNumChildren() == 1) mRootObject = mRootObject.getChildAt(0);
		 */

		stream.close();

		Logger.info("End parsing 3DS");

		return models;
	}

	void readChunk(InputStream stream) throws IOException {
		readHeader(stream);

		ChunkType chunk = ChunkType.parseValue(mChunkID);
		// Logger.debug("CHUNK %s - %s", mChunkID, chunk);

		if (chunk == null) {
			skipRead(stream);
		} else {

			switch (chunk) {
			case MESH_BLOCK:
				break;
			case OBJECT_BLOCK:
				mObjects++;
				mObjNames.add(readString(stream));
				break;
			case TRIMESH:
				break;
			case VERTICES:
				readVertices(stream);
				break;
			case FACES:
				readFaces(stream);
				break;
			case TEXCOORD:
				readTexCoords(stream);
				break;
			case TEX_NAME:
				// mCurrentMaterialKey = readString(stream);
				skipRead(stream);
				break;
			case TEX_FILENAME:
				String fileName = readString(stream);
				Logger.debug("TEX_FILENAME %s", fileName);

				// StringBuffer texture = new StringBuffer(packageID);
				// texture.append(":drawable/");
				//
				// StringBuffer textureName = new StringBuffer(fileName.toLowerCase());
				// int dotIndex = textureName.lastIndexOf(".");
				// if (dotIndex > -1)
				// texture.append(textureName.substring(0, dotIndex));
				// else
				// texture.append(textureName);
				//
				// textureAtlas.addBitmapAsset(new BitmapAsset(mCurrentMaterialKey, texture.toString()));
				// skipRead(stream);
				break;
			case TRI_MATERIAL:
				// String materialName = readString(stream);
				// int numFaces = readShort(stream);
				//
				// for (int i = 0; i < numFaces; i++) {
				// int faceIndex = readShort(stream);
				// co.faces.get(faceIndex).materialKey = materialName;
				// }
				skipRead(stream);
				break;
			case MATERIAL:
				break;
			case TEX_MAP:
				break;
			default:
				skipRead(stream);
			}
		}
	}

	public void build() {
		int num = mVertices.size();
		for (int j = 0; j < num; ++j) {
			ArrayList<Integer> indices = mIndices.get(j);
			ArrayList<Vector3> vertices = mVertices.get(j);
			ArrayList<Vector3> texCoords = null;
			ArrayList<Vector3> vertNormals = mVertNormals.get(j);

			if (mTexCoords.size() > 0)
				texCoords = mTexCoords.get(j);

			int len = indices.size();
			float[] aVertices = new float[len * 3];
			float[] aNormals = new float[len * 3];
			float[] aTexCoords = new float[len * 2];
			int[] aIndices = new int[len];

			int ic = 0;
			int itn = 0;
			int itc = 0;
			int ivi = 0;

			Vector3 coord;
			Vector3 texcoord;
			Vector3 normal;

			for (int i = 0; i < len; i += 3) {
				int v1 = indices.get(i);
				int v2 = indices.get(i + 1);
				int v3 = indices.get(i + 2);

				coord = vertices.get(v1);

				aVertices[ic++] = coord.x;
				aVertices[ic++] = coord.y;
				aVertices[ic++] = coord.z;

				aIndices[ivi] = ivi++;

				coord = vertices.get(v2);
				aVertices[ic++] = coord.x;
				aVertices[ic++] = coord.y;
				aVertices[ic++] = coord.z;

				aIndices[ivi] = ivi++;

				coord = vertices.get(v3);
				aVertices[ic++] = coord.x;
				aVertices[ic++] = coord.y;
				aVertices[ic++] = coord.z;

				aIndices[ivi] = ivi++;

				if (texCoords != null && texCoords.size() > 0) {
					texcoord = texCoords.get(v1);

					aTexCoords[itc++] = texcoord.x;
					aTexCoords[itc++] = texcoord.y;

					texcoord = texCoords.get(v2);

					aTexCoords[itc++] = texcoord.x;
					aTexCoords[itc++] = texcoord.y;

					texcoord = texCoords.get(v3);

					aTexCoords[itc++] = texcoord.x;
					aTexCoords[itc++] = texcoord.y;
				}

				normal = vertNormals.get(v1);
				aNormals[itn++] = normal.x;
				aNormals[itn++] = normal.y;
				aNormals[itn++] = normal.z;
				normal = vertNormals.get(v2);

				aNormals[itn++] = normal.x;
				aNormals[itn++] = normal.y;
				aNormals[itn++] = normal.z;

				normal = vertNormals.get(v3);

				aNormals[itn++] = normal.x;
				aNormals[itn++] = normal.y;
				aNormals[itn++] = normal.z;
			}

			Max3dsModelData targetObj = new Max3dsModelData(mObjNames.get(j));
			targetObj.setData(aVertices, aNormals, aTexCoords, aIndices);
			// -- diffuse material with random color. for now.
			// TODO
			/*
			 * DiffuseMaterial material = new DiffuseMaterial(); material.setUseSingleColor(true); targetObj.setMaterial(material); targetObj.setColor(0xff000000 + (int) (Math.random() * 0xffffff)); mRootObject.addChild(targetObj);
			 */
			models.add(targetObj);
		}
	}

	public void clear() {
		for (int i = 0; i < mObjects; ++i) {
			mIndices.get(i).clear();
			mVertNormals.get(i).clear();
			mVertices.get(i).clear();
			mTexCoords.get(i).clear();
		}
		mIndices.clear();
		mVertNormals.clear();
		mVertices.clear();
		mTexCoords.clear();
	}

	protected void skipRead(InputStream stream) throws IOException {
		for (int i = 0; (i < mChunkEndOffset - 6) && !mEndReached; i++) {
			mEndReached = stream.read() < 0;
		}
	}

	protected void readVertices(InputStream buffer) throws IOException {
		float x, y, z;
		int numVertices = readShort(buffer);
		ArrayList<Vector3> vertices = new ArrayList<Vector3>();

		for (int i = 0; i < numVertices; i++) {
			x = readFloat(buffer);
			y = readFloat(buffer);
			z = readFloat(buffer);

			vertices.add(new Vector3(x, y, z));
		}

		mVertices.add(vertices);
	}

	protected void readTexCoords(InputStream buffer) throws IOException {
		int numVertices = readShort(buffer);
		ArrayList<Vector3> texCoords = new ArrayList<Vector3>();

		for (int i = 0; i < numVertices; i++) {
			float x = readFloat(buffer);
			float y = 1 - readFloat(buffer);

			texCoords.add(new Vector3(x, y, 0));
		}

		mTexCoords.add(texCoords);
	}

	protected void readFaces(InputStream buffer) throws IOException {
		int triangles = readShort(buffer);
		Vector3[] normals = new Vector3[triangles];
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int i = 0; i < triangles; i++) {
			int[] vertexIDs = new int[3];
			vertexIDs[0] = readShort(buffer);
			vertexIDs[1] = readShort(buffer);
			vertexIDs[2] = readShort(buffer);
			readShort(buffer);

			indices.add(vertexIDs[0]);
			indices.add(vertexIDs[1]);
			indices.add(vertexIDs[2]);

			Vector3 normal = calculateFaceNormal(vertexIDs);
			normals[i] = normal;
		}

		mNormals.add(new Vector3[triangles]);
		mIndices.add(indices);

		int numVertices = mVertices.get(mObjects).size();
		int numIndices = indices.size();

		ArrayList<Vector3> vertNormals = new ArrayList<Vector3>();

		for (int i = 0; i < numVertices; i++) {

			Vector3 vertexNormal = new Vector3();

			for (int j = 0; j < numIndices; j += 3) {
				int id1 = indices.get(j);
				int id2 = indices.get(j + 1);
				int id3 = indices.get(j + 2);

				if (id1 == i || id2 == i || id3 == i) {
					vertexNormal.add(normals[j / 3]);
				}
			}
			vertexNormal.normalize();
			vertNormals.add(vertexNormal);
		}

		mVertNormals.add(vertNormals);
	}

	private Vector3 calculateFaceNormal(int[] vertexIDs) {
		ArrayList<Vector3> vertices = mVertices.get(mObjects);
		Vector3 v1 = vertices.get(vertexIDs[0]);
		Vector3 v2 = vertices.get(vertexIDs[2]);
		Vector3 v3 = vertices.get(vertexIDs[1]);

		Vector3 vector1 = Vector3.subtract(v2, v1);
		Vector3 vector2 = Vector3.subtract(v3, v1);

		Vector3 normal = Vector3.crossProduct(vector1, vector2);
		normal.normalize();
		return normal;
	}

	protected void readHeader(InputStream stream) throws IOException {
		mChunkID = readShort(stream);
		mChunkEndOffset = readInt(stream);
		mEndReached = mChunkID < 0;
	}

	protected String readString(InputStream stream) throws IOException {
		String result = new String();
		byte inByte;
		while ((inByte = (byte) stream.read()) != 0)
			result += (char) inByte;
		return result;
	}

	protected int readInt(InputStream stream) throws IOException {
		return stream.read() | (stream.read() << 8) | (stream.read() << 16) | (stream.read() << 24);
	}

	protected int readShort(InputStream stream) throws IOException {
		return (stream.read() | (stream.read() << 8));
	}

	protected float readFloat(InputStream stream) throws IOException {
		return Float.intBitsToFloat(readInt(stream));
	}

	/**
	 * <p>
	 * Carica un oggetto in formato Max3D dagli assets.
	 * </p>
	 * 
	 * @param context
	 * @param fileName
	 * @return modello in formato 3Dmax
	 */
	public static Max3dsModelData loadFromAsset(Context context, String fileName) {
		try {
			return parse(context.getAssets().open(fileName)).get(0);
		} catch (IOException e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

	/**
	 * Carica il modello in format Max3D dalle risorse
	 * 
	 * @param context
	 * @param resourceId
	 * @return modello in formato 3Dmax
	 */
	public static Max3dsModelData loadFromResources(Context context, int resourceId) {
		try {
			return parse(context.getResources().openRawResource(resourceId)).get(0);
		} catch (IOException e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

}
