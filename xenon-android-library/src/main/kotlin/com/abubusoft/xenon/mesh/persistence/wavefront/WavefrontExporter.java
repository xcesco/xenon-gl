package com.abubusoft.xenon.mesh.persistence.wavefront;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.kripton.android.Logger;

/**
 * Exporter nel formato Wavefront.
 * 
 * @author Francesco Benincasa
 * 
 */
public class WavefrontExporter {

	BufferedWriter writer = null;

	public final String version = "1.0.0";

	/**
	 * Dato un mesh in cui sono presenti i dati CLIENT (gli array sono presenti nello spazio java), si provvede ad esportare modello in un file.
	 * 
	 * Attualmente sono supportate i vertici, una texture, le normali, ed il nome della mesh.
	 * 
	 * @param file
	 * @param mesh
	 * @return
	 */
	public static boolean export(File file, Mesh mesh) {
		WavefrontExporter instance = new WavefrontExporter();

		return instance.exportInternal(file, mesh);
	}

	/**
	 * Exporter del file.
	 * 
	 * @param file
	 * @param mesh
	 * @return
	 */
	public boolean exportInternal(File file, Mesh mesh) {

		try {
			writer = new BufferedWriter(new FileWriter(file));

			writeComment("Abubu exporter %s", version);
			writeComment("File Created %s", (new Date()));
			writeLine();
			writeComment("");
			writeComment("object %s", mesh.name);
			writeComment("");
			writeLine();

			for (int i = 0; i < mesh.vertices.vertexCount * 3; i += 3) {
				writeLine("v %s %s %s", mesh.vertices.coords[i + 0], mesh.vertices.coords[i + 1], mesh.vertices.coords[i + 2]);
			}
			writeComment("%s vertices ", mesh.vertices.vertexCount);
			writeLine();

			// texture
			if (mesh.texturesCount > 0) {
				for (int i = 0; i < mesh.textures[0].coords.length; i += 2) {
					writeLine("vt %s %s %s", mesh.textures[0].coords[i + 0], mesh.textures[0].coords[i + 1], 0f);
				}
				writeComment("%s texture coords", mesh.vertices.vertexCount);
				writeLine();
			}

			// indici
			writeLine("g %s", mesh.name);
			if (mesh.indexesEnabled) {
				for (int i = 0; i < mesh.indexes.values.length; i += 3) {
					writeLine("f %s/%s %s/%s %s/%s",
					// vertice 0
							mesh.indexes.values[i + 0] + 1, mesh.indexes.values[i + 0] + 1,
							// vertice 1
							mesh.indexes.values[i + 1] + 1, mesh.indexes.values[i + 1] + 1,
							// vertice 2
							mesh.indexes.values[i + 2] + 1, mesh.indexes.values[i + 2] + 1);
				}
				writeComment("%s faces", mesh.indexesCount / 3);
				writeLine();
			}

			// Close writer
			writer.close();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw (new XenonRuntimeException(e));
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return true;
	}

	/**
	 * Aggiunge commento
	 * 
	 * @param msg
	 * @param params
	 * @throws IOException
	 */
	private void writeComment(String msg, Object... params) throws IOException {
		writer.write("# " + String.format(msg, params) + "\n");
	}

	/**
	 * Aggiunge linea
	 * 
	 * @param msg
	 * @param params
	 * @throws IOException
	 */
	private void writeLine(String msg, Object... params) throws IOException {
		writer.write(String.format(msg, params) + "\n");
	}

	/**
	 * Aggiunge linea vuota
	 * 
	 * @throws IOException
	 */
	private void writeLine() throws IOException {
		writer.write("\n");
	}
}
