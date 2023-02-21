/**
 * 
 */
package com.abubusoft.xenon.texture;

import android.opengl.GLES20;

/**
 * <p>
 * Tipo di ripetizione della texture.
 * </p>
 * <p>
 * <a href="http://www.khronos.org/opengles/sdk/docs/man/xhtml/glTexParameter.xml">Link delle specifiche</a.>
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public enum TextureRepeatType {

	/**
	 * <p>
	 * Non effettua la ripetizione. Prima e dopo il range [0..1], viene lasciato il valore 0 o 1. E' il valore di default
	 * </p>
	 * 
	 * <pre>
	 * GL_CLAMP_TO_EDGE causes s coordinates to be clamped to the range 1 2N 1 - 1 2N , where N is the size of the texture in the direction of clamping. GL_REPEAT causes the
	 * integer part of the s coordinate to be ignored
	 * </pre>
	 */
	NO_REPEAT(GLES20.GL_CLAMP_TO_EDGE),

	/**
	 * <p>
	 * La parte intera della coordinata viene ignorata. La texture viene ripetuta
	 * </p>
	 * 
	 * <pre>
	 * GL_REPEAT causes the integer part of the s coordinate to be ignored
	 * </pre>
	 */
	REPEAT(GLES20.GL_REPEAT),

	/**
	 * <p>La texture viene ripetuta. Se la parte intera è pari, range [0 .. 1].. se la parte intera è dispari, 
	 * la texture viene invertita nella forma <code>1 - frac</code>.</p>
	 * <pre>
	 * GL_MIRRORED_REPEAT causes the s coordinate to be set to the fractional part of the texture coordinate 
	 * if the integer part of s is even; if the integer part of s is odd, then the s texture coordinate is 
	 * set to 1 - frac ⁡ s , where frac ⁡ s represents the fractional part of s.
	 * </pre>
	 */
	MIRRORED_REPEAT(GLES20.GL_MIRRORED_REPEAT);

	private TextureRepeatType(int value) {
		this.value = value;
	}

	public int value;
}
