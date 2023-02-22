package com.abubusoft.xenon.mesh.persistence.wavefront;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.Face;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.UVCoord;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.VertexF;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;

/**
 * <p>
 * Wavefront obj loader
 * </p>
 * <p>
 * Per le <a href="http://en.wikipedia.org/wiki/Wavefront_.obj_file">specifiche</a> seguire il link.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class WavefrontLoader {
	private static final String SPACE = "\\s+";
	private static final String SLASH = "/";

	/**
	 * <p>
	 * Carica un oggetto in formato obj.
	 * </p>
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 * 		model data recuperato dal file
	 */
	public static WavefrontModelData loadFromAsset(Context context, String fileName) {
		try {
			return load(context.getAssets().open(fileName));
		} catch (IOException e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

	/**
	 * @param in
	 * @return
	 */
	static WavefrontModelData load(InputStream in) {
		WavefrontModelData model = new WavefrontModelData();
		ArrayList<VertexF> vertices = new ArrayList<VertexF>();
		ArrayList<VertexF> normals = new ArrayList<VertexF>();
		ArrayList<UVCoord> uvs = new ArrayList<UVCoord>();
		ArrayList<Face> faces = new ArrayList<Face>();

		try {

			// 1) read in verticies,
			// 2) read in uvs
			// 3) create faces which are verticies and uvs expanded
			// 4) unroll faces into WavefrontModelData using sequential indicies
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			// StringTokenizer st;
			String line;

			line = reader.readLine();

			String[] tokens;
			String[] temp;
			int current;
			int index;

			Logger.info("Loading obj data");
			while (line != null) {
				current = 0;
				tokens = line.trim().split(SPACE);
				// st = new StringTokenizer(line, SPACE);

				if (line.startsWith("# object")) {
					model.name=line.replace("# object", "").trim();
				}

				if (tokens.length > 1) {
					String lineType = tokens[current++];
					if (lineType.equals("v")) {
						// vertex
						VertexF vert = new VertexF();
						vert.x = Float.valueOf(tokens[current++]);
						vert.y = Float.valueOf(tokens[current++]);
						vert.z = Float.valueOf(tokens[current++]);
						vertices.add(vert);
					} else if (lineType.equals("vn")) {
						// vertex
						VertexF normal = new VertexF();
						normal.x = Float.valueOf(tokens[current++]);
						normal.y = Float.valueOf(tokens[current++]);
						normal.z = Float.valueOf(tokens[current++]);
						normals.add(normal);
					} else if (lineType.equals("vt")) {
						// texture mapping
						UVCoord uv = new UVCoord();
						uv.u = Float.valueOf(tokens[current++]);
						uv.v = Float.valueOf(tokens[current++]);
						uvs.add(uv);
					} else if (lineType.equals("f")) {
						// face
						Face face = new Face();

						for (int a = 0; a < 3; a++) {
							temp = tokens[current++].split(SLASH);

							index = Integer.valueOf(temp[0]) - 1;
							face.vertexIndex[a] = index;
							// face.v[a] = vertices.get(index);

							// li inizializziamo ora a -1
							face.textureIndex[a] = -1;
							face.normalIndex[a] = -1;

							if (temp.length > 1 && temp[1].length() > 0) {
								// texture-coordinate
								index = Integer.valueOf(temp[1]) - 1;
								face.textureIndex[a] = index;
								// face.uv[a] = uvs.get(index);
							}
							if (temp.length > 2) {
								// normal
								index = Integer.valueOf(temp[2]) - 1;
								face.normalIndex[a] = index;
								// face.n[a] = normals.get(index);
							}
						}

						faces.add(face);
					}
				}
				line = reader.readLine();
			}
			// printFaces(faces);
			int facesSize = faces.size();
			Logger.info(facesSize + " polys");

			model.vertices = vertices;
			model.tex = uvs;
			model.normals = normals;
			model.triangles = faces;
			reader.close();

		} catch (IOException e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}

		return model;
	}

	public static WavefrontModelData loadFromResources(Context context, int resourceId) {
		try {
			return load(context.getResources().openRawResource(resourceId));
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

}