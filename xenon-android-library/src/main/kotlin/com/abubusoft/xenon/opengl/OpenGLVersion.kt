/**
 *
 */
package com.abubusoft.xenon.opengl

/**
 * Rappresenta la versione OpenGL nella sua forma standard x.y,
 * ovvero major.minor
 *
 * @author Francesco Benincasa
 */
class OpenGLVersion(versionString: String) : Comparable<OpenGLVersion> {
    /**
     * major
     */
    var major = 0

    /**
     * minor
     */
    var minor = 0

    /**
     * Data una stringa, lo converte in stringa. Se non riesce a convertirlo,
     * rimane tutto 0.0.0.
     *
     * @param versionString
     */
    init {
        if (versionString.length > 0) {
            val array = versionString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            major = array[0].toInt()
            if (array.size >= 2) {
                minor = array[1].toInt()
            }
        }
    }

    /**
     * Se due Versioni vengono comparate, si vedono le relative major minor e
     * build per dire che sono uguali o meno.
     *
     * @see java.lang.Object.equals
     */
    override fun equals(o: Any?): Boolean {
        return if (o is OpenGLVersion) {
            val o1 = o
            major == o1.major && minor == o1.minor
        } else super.equals(o)
    }

    override fun compareTo(another: OpenGLVersion): Int {
        return if (major == another.major) {
            if (minor == another.minor) {
                0
            } else {
                Integer.valueOf(minor).compareTo(Integer.valueOf(another.minor))
            }
        } else {
            Integer.valueOf(major).compareTo(Integer.valueOf(another.major))
        }
    }

    fun isGreaterEqualsThan(version: String): Boolean {
        return this.compareTo(OpenGLVersion(version)) <= 0
    }

    fun isLowerEqualsThan(version: String): Boolean {
        return this.compareTo(OpenGLVersion(version)) >= 0
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    override fun toString(): String {
        return "$major.$minor"
    }
}