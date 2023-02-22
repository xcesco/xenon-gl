package com.abubusoft.xenon.texture

import android.opengl.GLES20
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.opengl.XenonGLExtension

/**
 *
 *
 * Specifies the data allocation of the pixel data. Per il momento supportiamo texture i cui elementi sono unsigned byte, e float
 *
 *
 *
 *
 * [][<a href=]//www.opengl.org/sdk/docs/man/docbook4/xhtml/glTexSubImage2D.xml">...">&lt;a href=&quot;https://www.opengl.org/sdk/docs/man/docbook4/xhtml/glTexSubImage2D.xml&quot;&gt;...&lt;/a&gt;
 *
 *
 * @author Francesco Benincasa
 */
enum class TextureInternalFormatType(var value: Int) {
    /**
     * texture di float
     */
    FLOAT(GLES20.GL_FLOAT),

    /**
     * texture di tipo half_float (richiede  [XenonGLExtension.TEXTURE_HALF_FLOAT])
     */
    HALF_FLOAT(XenonGL.GL_HALF_FLOAT_OES),

    /**
     * texture di byte
     */
    UNSIGNED_BYTE(GLES20.GL_UNSIGNED_BYTE);
}