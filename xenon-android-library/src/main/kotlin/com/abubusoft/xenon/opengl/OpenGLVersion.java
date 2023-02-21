/**
 * 
 */
package com.abubusoft.xenon.opengl;

/**
 * Rappresenta la versione OpenGL nella sua forma standard x.y,
 * ovvero major.minor
 * 
 * @author Francesco Benincasa
 * 
 */
public class OpenGLVersion implements Comparable<OpenGLVersion> {

	/**
	 * major
	 */
	int major;

	/**
	 * minor
	 */
	int minor;

	/**
	 * Data una stringa, lo converte in stringa. Se non riesce a convertirlo,
	 * rimane tutto 0.0.0.
	 * 
	 * @param versionString
	 */
	public OpenGLVersion(String versionString) {
		if (versionString.length() > 0) {
			String[] array = versionString.split("\\.");

			major = Integer.parseInt(array[0]);
						
			if (array.length >= 2) {
				minor = Integer.parseInt(array[1]);
			}
		}
	}

	/**
	 * Se due Versioni vengono comparate, si vedono le relative major minor e
	 * build per dire che sono uguali o meno.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof OpenGLVersion) {
			OpenGLVersion o1 = (OpenGLVersion) o;

			return major == o1.major && minor == o1.minor;
		} else
			return super.equals(o);
	}

	@Override
	public int compareTo(OpenGLVersion another) {
		if (major == another.major) {
			if (minor == another.minor) {
				return 0;
			} else {
				return Integer.valueOf(minor).compareTo(Integer.valueOf(another.minor));
			}
		} else {
			return Integer.valueOf(major).compareTo(Integer.valueOf(another.major));
		}
	}
	
	public boolean isGreaterEqualsThan(String version)
	{
		return this.compareTo(new OpenGLVersion(version))<=0;
	}
	
	public boolean isLowerEqualsThan(String version)
	{
		return this.compareTo(new OpenGLVersion(version))>=0;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return major+"."+minor;
	}

}
