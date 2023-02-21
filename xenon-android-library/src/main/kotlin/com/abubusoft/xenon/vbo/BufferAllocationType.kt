package com.abubusoft.xenon.vbo

import android.opengl.GLES20

/**
 * @author Francesco Benincasa
 */
enum class BufferAllocationType(val value: Int) {
    /**
     *
     * Usa solo la parte client. Non definisce un reale vertex buffer object.
     */
    CLIENT(0),

    /**
     * GL_STATIC_DRAW - for static content, which never changing (examples: terrain, buildings)
     */
    STATIC(GLES20.GL_STATIC_DRAW),

    /**
     * GL_DYNAMIC_DRAW - for spontaneously changing data (examples: skybox, units, characters, anything that relies on actual input)
     */
    DYNAMIC(GLES20.GL_DYNAMIC_DRAW),

    /**
     * GL_STREAM_DRAW - for constantly updating data (examples: some 2D text like FPS counter, particles)
     */
    STREAM(GLES20.GL_STREAM_DRAW);
}