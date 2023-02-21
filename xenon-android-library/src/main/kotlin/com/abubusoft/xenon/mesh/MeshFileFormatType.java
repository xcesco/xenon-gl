package com.abubusoft.xenon.mesh;

/**
 * <p>Tipi di formati per la lettura degli shape supportati.</p>
 * @author Francesco Benincasa
 *
 */
public enum MeshFileFormatType {
	/**
	 * Formato Obj o Wavefront.
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/w/index.php?title=Wavefront_.obj_file">Online version</a></li>
	 * <li><a href="./doc-files/Wavefront.pdf">PDF Version</a></li>
	 * </ul>
	 */
	WAVEFRONT,
	/**
	 * Formato 3D studio max.
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/.3ds">Online version</a></li>
	 * <li><a href="http://www.spacesimulator.net/wiki/index.php?title=Tutorials:3ds_Loader">Online version 1</a></li>
	 * <li><a href="http://www.the-labs.com/Blender/3dsspec.html">Online version 2</a></li>
	 * <li><a href="./doc-files/3ds.pdf">PDF Version</a></li>
	 * <li><a href="./doc-files/3ds1.pdf">PDF Version 2</a></li>
	 * </ul> 
	 */
	MAX3D,
	
	ANDROID_XML,
	
	/**
	 * Formato JSON ottenuto mediante la mia libreria kripton
	 */
	KRIPTON_JSON,
	
	/**
	 * Formato XML ottenuto mediante la mia libreria kripton
	 */
	KRIPTON_XML;

}
