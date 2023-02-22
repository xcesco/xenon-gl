package com.abubusoft.xenon.mesh.persistence.max3d

import java.io.IOException
import java.io.InputStream

abstract class MeshParserBase {
    protected inner class MaterialDef {
        var name: String? = null
        var ambientColor = 0
        var diffuseColor = 0
        var specularColor = 0
        var specularCoefficient = 0f
        var alpha = 0f
        var ambientTexture: String? = null
        var diffuseTexture: String? = null
        var specularColorTexture: String? = null
        var specularHighlightTexture: String? = null
        var alphaTexture: String? = null
        var bumpTexture: String? = null
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
     * Size of buffer in number of bytes
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
     * Size of buffer in number of bytes
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
    @Throws(IOException::class)
    protected open fun readInt(stream: InputStream): Int {
        return (stream.read() or (stream.read() shl 8) or (stream.read() shl 16)
                or (stream.read() shl 24))
    }

    @Throws(IOException::class)
    protected open fun readShort(stream: InputStream): Int {
        return stream.read() or (stream.read() shl 8)
    }

    @Throws(IOException::class)
    protected open fun readFloat(stream: InputStream): Float {
        return java.lang.Float.intBitsToFloat(readInt(stream))
    }

    protected fun getOnlyFileName(fileName: String?): String {
        var fName: String = String(fileName)
        var indexOf = fName.lastIndexOf("\\")
        if (indexOf > -1) fName = fName.substring(indexOf + 1, fName.length)
        indexOf = fName.lastIndexOf("/")
        if (indexOf > -1) fName = fName.substring(indexOf + 1, fName.length)
        return fName.lowercase().replace("\\s".toRegex(), "_")
    }

    protected fun getFileNameWithoutExtension(fileName: String): String {
        var fName = fileName.substring(0, fileName.lastIndexOf("."))
        var indexOf = fName.lastIndexOf("\\")
        if (indexOf > -1) fName = fName.substring(indexOf + 1, fName.length)
        indexOf = fName.lastIndexOf("/")
        if (indexOf > -1) fName = fName.substring(indexOf + 1, fName.length)
        return fName.lowercase().replace("\\s".toRegex(), "_")
    }
}