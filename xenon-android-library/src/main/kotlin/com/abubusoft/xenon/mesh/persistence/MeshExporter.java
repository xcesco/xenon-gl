/**
 * 
 */
package com.abubusoft.xenon.mesh.persistence;

import java.io.File;

import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontExporter;

/**
 * @author Francesco Benincasa
 *
 */
public abstract class MeshExporter {

	public static boolean exportWavefront(File file, Mesh mesh)
	{
		return WavefrontExporter.export(file, mesh);
	}
}
