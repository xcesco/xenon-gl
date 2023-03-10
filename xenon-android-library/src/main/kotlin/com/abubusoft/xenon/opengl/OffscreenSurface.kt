package com.abubusoft.xenon.opengl

/**
 * Off-screen EGL surface (pbuffer).
 *
 *
 * It's good practice to explicitly release() the surface, preferably from a "finally" block.
 */
class OffscreenSurface(eglCore: XenonEGL, width: Int, height: Int) : EglSurfaceBase(eglCore) {
    /**
     * Creates an off-screen surface with the specified width and height.
     */
    init {
        createOffscreenSurface(width, height)
    }

    /**
     * Releases any resources associated with the surface.
     */
    fun release() {
        releaseEglSurface()
    }
}