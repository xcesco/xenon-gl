package com.abubusoft.xenon.opengl

import com.abubusoft.xenon.android.XenonService4OpenGL

interface XenonGLRenderer {
    /**
     * Called when the surface is created or recreated.
     *
     *
     * Called when the rendering thread starts and whenever the EGL context is lost. The EGL context will typically be lost when the Android device
     * awakes after going to sleep.
     *
     *
     * Since this method is called at the beginning of rendering, as well as every time the EGL context is lost, this method is a convenient place to
     * put code to create resources that need to be created when the rendering starts, and that need to be recreated when the EGL context is lost.
     * Textures are an example of a resource that you might want to create here.
     *
     *
     * Note that when the EGL context is lost, all OpenGL resources associated with that context will be automatically deleted. You do not need to
     * call the corresponding "glDelete" methods such as glDeleteTextures to manually delete these lost resources.
     */
    fun onSurfaceCreated()

    /**
     * Called when the surface changed size.
     *
     *
     * Called after the surface is created and whenever the OpenGL ES surface size changes.
     *
     *
     * Typically you will set your viewport here. If your camera is fixed then you could also set your projection matrix here:
     *
     * <pre class="prettyprint">
     * void onSurfaceChanged(int width, int height)
     * {
     * gl.glViewport(0, 0, width, height);
     * // for a fixed camera, set the projection too
     * float ratio = (float)width / height;
     * gl.glMatrixMode(GL10.GL_PROJECTION);
     * gl.glLoadIdentity();
     * gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
     * }
    </pre> *
     *
     * @param width
     * Surface width
     * @param height
     * Surface height
     */
    fun onSurfaceChanged(width: Int, height: Int)

    /**
     * Called to draw the current frame.
     *
     *
     * This method is responsible for drawing the current frame.
     *
     *
     * The implementation of this method typically looks like this:
     *
     * <pre class="prettyprint">
     * void onDrawFrame()
     * {
     * gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
     * // ... other gl calls to render the scene ...
     * }
    </pre> *
     */
    fun onDrawFrame()

    /**
     * Called when the GL thread is exiting
     */
    fun onDestroy()

    /**
     * Viene eseguito sul thread della UI o del service.
     */
    fun onPause()

    /**
     * Viene eseguito sul thread della UI o del service.
     */
    fun onResume()
    var frameRate: Double

    /**
     *
     * Imposta il safe mode, ovvero il fatto di supportare le eccezioni durante il rendering.
     * @param value
     */
    fun setSafeMode(value: Boolean)
    fun setService(service: XenonService4OpenGL?)
    fun preserveEGLContextOnPause()

    companion object {
        const val RENDERMODE_CONTINUOUSLY = 1
        const val RENDERMODE_WHEN_DIRTY = 0
    }
}