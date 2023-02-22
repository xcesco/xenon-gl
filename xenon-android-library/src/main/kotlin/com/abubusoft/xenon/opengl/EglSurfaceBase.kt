/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abubusoft.xenon.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGL10

/**
 * Common base class for EGL surfaces.
 *
 *
 * There can be multiple surfaces associated with a single context.
 */
open class EglSurfaceBase protected constructor(
    // EglCore object we're associated with.  It may be associated with multiple surfaces.
    protected var xenonEGL: XenonEGL
) {
    private var mEGLSurface = EGL10.EGL_NO_SURFACE
    private var mWidth = -1
    private var mHeight = -1

    /**
     * Creates a window surface.
     *
     *
     * @param surface May be a Surface or SurfaceTexture.
     */
    fun createWindowSurface(surface: Any) {
        check(!(mEGLSurface !== EGL10.EGL_NO_SURFACE)) { "surface already created" }
        mEGLSurface = xenonEGL.createWindowSurface(surface)

        // Don't cache width/height here, because the size of the underlying surface can change
        // out from under us (see e.g. HardwareScalerActivity).
        //mWidth = mEglCore.querySurface(mEGLSurface, EGL14.EGL_WIDTH);
        //mHeight = mEglCore.querySurface(mEGLSurface, EGL14.EGL_HEIGHT);
    }

    /**
     * Creates an off-screen surface.
     */
    fun createOffscreenSurface(width: Int, height: Int) {
        check(!(mEGLSurface !== EGL10.EGL_NO_SURFACE)) { "surface already created" }
        mEGLSurface = xenonEGL.createOffscreenSurface(width, height)
        mWidth = width
        mHeight = height
    }

    /**
     * Returns the surface's width, in pixels.
     *
     *
     * If this is called on a window surface, and the underlying surface is in the process
     * of changing size, we may not see the new size right away (e.g. in the "surfaceChanged"
     * callback).  The size should match after the next buffer swap.
     */
    val width: Int
        get() = if (mWidth < 0) {
            xenonEGL.querySurface(mEGLSurface, EGL10.EGL_WIDTH)
        } else {
            mWidth
        }

    /**
     * Returns the surface's height, in pixels.
     */
    val height: Int
        get() = if (mHeight < 0) {
            xenonEGL.querySurface(mEGLSurface, EGL10.EGL_HEIGHT)
        } else {
            mHeight
        }

    /**
     * Release the EGL surface.
     */
    fun releaseEglSurface() {
        xenonEGL.releaseSurface(mEGLSurface)
        mEGLSurface = EGL10.EGL_NO_SURFACE
        mHeight = -1
        mWidth = mHeight
    }

    /**
     * Makes our EGL context and surface current.
     */
    fun makeCurrent() {
        xenonEGL.makeCurrent(mEGLSurface)
    }

    /**
     * Makes our EGL context and surface current for drawing, using the supplied surface
     * for reading.
     */
    fun makeCurrentReadFrom(readSurface: EglSurfaceBase) {
        xenonEGL.makeCurrent(mEGLSurface, readSurface.mEGLSurface)
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    fun swapBuffers(): Boolean {
        val result = xenonEGL.swapBuffers(mEGLSurface)
        if (!result) {
            Logger.debug("WARNING: swapBuffers() failed")
        }
        return result
    }

    /**
     * Saves the EGL surface to a file.
     *
     *
     * Expects that this object's EGL surface is current.
     */
    @Throws(IOException::class)
    fun saveFrame(file: File) {
        if (!xenonEGL.isCurrent(mEGLSurface)) {
            throw RuntimeException("Expected EGL context/surface is not current")
        }

        // glReadPixels fills in a "direct" ByteBuffer with what is essentially big-endian RGBA
        // data (i.e. a byte of red, followed by a byte of green...).  While the Bitmap
        // constructor that takes an int[] wants little-endian ARGB (blue/red swapped), the
        // Bitmap "copy pixels" method wants the same format GL provides.
        //
        // Ideally we'd have some way to re-use the ByteBuffer, especially if we're calling
        // here often.
        //
        // Making this even more interesting is the upside-down nature of GL, which means
        // our output will look upside down relative to what appears on screen if the
        // typical GL conventions are used.
        val filename = file.toString()
        val width = width
        val height = height
        val buf = ByteBuffer.allocateDirect(width * height * 4)
        buf.order(ByteOrder.LITTLE_ENDIAN)
        GLES20.glReadPixels(
            0, 0, width, height,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf
        )
        checkGlError("glReadPixels")
        buf.rewind()
        var bos: BufferedOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(filename))
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(buf)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, bos)
            bmp.recycle()
        } finally {
            bos?.close()
        }
        Logger.warn("Saved " + width + "x" + height + " frame as '" + filename + "'")
    }
}