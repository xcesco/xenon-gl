package com.abubusoft.xenon.core.util

import java.io.*

/** Provides utility methods to copy streams  */
object StreamUtility {
    const val DEFAULT_BUFFER_SIZE = 8192

    val EMPTY_BYTES = ByteArray(0)

    /**
     * Copy the data from an [InputStream] to an [OutputStream] without closing the stream.
     *
     * @throws IOException
     */
    /**
     * Copy the data from an [InputStream] to an [OutputStream] without closing the stream.
     *
     * @throws IOException
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }
    /**
     * Copy the data from an [InputStream] to a byte array without closing the stream.
     *
     * @param estimatedSize
     * Used to preallocate a possibly correct sized byte array to avoid an array copy.
     * @throws IOException
     */
    /**
     * Copy the data from an [InputStream] to a byte array without closing the stream.
     *
     * @throws IOException
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun copyStreamToByteArray(input: InputStream, estimatedSize: Int = input.available()): ByteArray {
        val baos: ByteArrayOutputStream = OptimizedByteArrayOutputStream(Math.max(0, estimatedSize))
        copyStream(input, baos)
        return baos.toByteArray()
    }
    /**
     * Copy the data from an [InputStream] to a string using the default charset.
     *
     * @param approxStringLength
     * Used to preallocate a possibly correct sized StringBulder to avoid an array copy.
     * @throws IOException
     */
    /**
     * Copy the data from an [InputStream] to a string using the default charset without closing the stream.
     *
     * @throws IOException
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun copyStreamToString(input: InputStream, approxStringLength: Int = input.available()): String {
        val reader = BufferedReader(InputStreamReader(input))
        val w = StringWriter(Math.max(0, approxStringLength))
        val buffer = CharArray(DEFAULT_BUFFER_SIZE)
        var charsRead: Int
        while (reader.read(buffer).also { charsRead = it } != -1) {
            w.write(buffer, 0, charsRead)
        }
        return w.toString()
    }

    /** Close and ignore all errors.  */
    fun closeQuietly(c: Closeable?) {
        if (c != null) try {
            c.close()
        } catch (e: Exception) {
            // ignore
        }
    }

    /** A ByteArrayOutputStream which avoids copying of the byte array if not necessary.  */
    private class OptimizedByteArrayOutputStream internal constructor(initialSize: Int) : ByteArrayOutputStream(initialSize) {
        @Synchronized
        override fun toByteArray(): ByteArray {
            return if (count == buf.size) buf else super.toByteArray()
        }
    }
}