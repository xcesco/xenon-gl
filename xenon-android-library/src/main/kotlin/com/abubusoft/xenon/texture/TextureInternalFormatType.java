package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.opengl.XenonGLExtension;

import android.opengl.GLES20;

/**
 * <p>
 * Specifies the data allocation of the pixel data. Per il momento supportiamo texture i cui elementi sono unsigned byte, e float
 * </p>
 * 
 * <p>
 * {@link https://www.opengl.org/sdk/docs/man/docbook4/xhtml/glTexSubImage2D.xml}
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public enum TextureInternalFormatType {
	/**
	 * texture di float
	 */
	FLOAT(GLES20.GL_FLOAT),
	
	/**
	 * texture di tipo half_float (richiede  {@link XenonGLExtension#TEXTURE_HALF_FLOAT})
	 */
	HALF_FLOAT(XenonGL.GL_HALF_FLOAT_OES),
	
	/**
	 * texture di byte
	 */
	UNSIGNED_BYTE(GLES20.GL_UNSIGNED_BYTE);

	public int value;

	TextureInternalFormatType(int val) {
		value = val;
	}
}