package com.abubusoft.xenon.mesh.persistence.max3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public abstract class MeshParserBase {

	protected class MaterialDef {
		public String name;
		public int ambientColor;
		public int diffuseColor;
		public int specularColor;
		public float specularCoefficient;
		public float alpha;
		public String ambientTexture;
		public String diffuseTexture;
		public String specularColorTexture;
		public String specularHighlightTexture;
		public String alphaTexture;
		public String bumpTexture;
	}
	
	/**
	 * Open a BufferedReader for the current resource or file with a buffer size of 8192 bytes.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	/*protected BufferedReader getBufferedReader() throws FileNotFoundException {
		return getBufferedReader(8192);
	}*/

	/**
	 * Open a BufferedReader for the current resource or file with a given buffer size.
	 * 
	 * @param size
	 *            Size of buffer in number of bytes
	 * @return
	 * @throws FileNotFoundException
	 */
	/*protected BufferedReader getBufferedReader(int size) throws FileNotFoundException {
		BufferedReader buffer = null;

		if (mFile == null) {
			buffer = new BufferedReader(new InputStreamReader(mResources.openRawResource(mResourceId)), size);
		} else {
			buffer = new BufferedReader(new FileReader(mFile), size);
		}

		return buffer;
	}*/

	/**
	 * Open a BufferedReader for the current resource or file with a buffer size of 8192 bytes.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	/*protected BufferedInputStream getBufferedInputStream() throws FileNotFoundException {
		return getBufferedInputStream(8192);
	}*/

	/**
	 * Open a BufferedReader for the current resource or file using the given buffer size.
	 * 
	 * @param size
	 * @return
	 * @throws FileNotFoundException
	 */
	/*protected BufferedInputStream getBufferedInputStream(int size) throws FileNotFoundException {
		BufferedInputStream bis;

		if (mFile == null) {
			bis = new BufferedInputStream(mResources.openRawResource(mResourceId), size);
		} else {
			bis = new BufferedInputStream(new FileInputStream(mFile), size);
		}

		return bis;
	}*/

	/**
	 * Open a DataInputStream for the current resource or file using Little Endian format with a buffer size of 8192
	 * bytes.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	/*protected LittleEndianDataInputStream getLittleEndianInputStream() throws FileNotFoundException {
		return getLittleEndianInputStream(8192);
	}*/

	/**
	 * Open a DataInputStream for the current resource or file using Little Endian format with a given buffer size.
	 * 
	 * @param size
	 *            Size of buffer in number of bytes
	 * @return
	 * @throws FileNotFoundException
	 */
	/*protected LittleEndianDataInputStream getLittleEndianInputStream(int size) throws FileNotFoundException {
		return new LittleEndianDataInputStream(getBufferedInputStream(size));
	}*/

	/*protected String readString(InputStream stream) throws IOException {
		String result = new String();
		byte inByte;
		while ((inByte = (byte) stream.read()) != 0)
			result += (char) inByte;
		return result;
	}*/

	protected int readInt(InputStream stream) throws IOException {
		return stream.read() | (stream.read() << 8) | (stream.read() << 16)
				| (stream.read() << 24);
	}

	protected int readShort(InputStream stream) throws IOException {
		return (stream.read() | (stream.read() << 8));
	}

	protected float readFloat(InputStream stream) throws IOException {
		return Float.intBitsToFloat(readInt(stream));
	}

	protected String getOnlyFileName(String fileName) {
		String fName = new String(fileName);
		int indexOf = fName.lastIndexOf("\\");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		indexOf = fName.lastIndexOf("/");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		return fName.toLowerCase(Locale.ENGLISH).replaceAll("\\s", "_");
	}

	protected String getFileNameWithoutExtension(String fileName) {
		String fName = fileName.substring(0, fileName.lastIndexOf("."));
		int indexOf = fName.lastIndexOf("\\");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		indexOf = fName.lastIndexOf("/");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		return fName.toLowerCase(Locale.ENGLISH).replaceAll("\\s", "_");
	}

	
}
