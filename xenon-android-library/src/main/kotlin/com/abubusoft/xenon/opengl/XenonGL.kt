package com.abubusoft.xenon.opengl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.SparseArray
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.ScreenInfo
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGL10

/**
 * Wrapper dell'openGL 2. Gestisce le funzioni in comune. Deve essere inizializzato allo startup dell'applicazione al fine di decidere cosa fare.
 *
 * @author Francesco Benincasa
 */
object XenonGL {
    const val EGL_OPENGL_ES2_BIT = 4
    const val EGL_OPENGL_ES3_BIT = 64

    /**
     *
     * Recupera le informazioni della versione di OpenGL supportata dal device.
     *
     * Come da specifiche [khronos](https://www.khronos.org/opengles/sdk/docs/man/xhtml/glGetString.xml).
     *
     * @return
     * versione openg suppotata dai driver
     */
    lateinit var version: OpenGLVersion

    /**
     * Verifica se il device ha il supporto ad opengl es 3.0
     *
     * @return true se supporta opengl-es 3.0
     */
    val isGL30Supported: Boolean
        get() = version.isGreaterEqualsThan("3.0")

    /**
     * Verifica se il device ha il supporto ad opengl es 3.1
     *
     * @return true se supporta opengl-es 3.1
     */
    val isGL31Supported: Boolean
        get() = version.isGreaterEqualsThan("3.1")

    private var fields: SparseArray<String>? = null

    private fun decodeEGLConstant(value: Int): String {
        synchronized(fields!!) {
            if (fields == null) {
                fields = SparseArray()
                val col = EGL10::class.java.fields
                for (item in col) {
                    if (item.type == Int::class.java || item.type == Integer.TYPE) {
                        try {
                            fields!!.put(item.getInt(null), item.name)
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        return fields!![value]
    }

    /**
     * half float
     *
     * [OES_texture_float](http://www.khronos.org/registry/gles/extensions/OES/OES_texture_float.txt)
     */
    const val GL_HALF_FLOAT_OES = 0x8D61

    /**
     *
     * tipo di texture esterna
     *
     * [OES_EGL_image_external](http://www.khronos.org/registry/gles/extensions/OES/OES_EGL_image_external.txt)
     */
    const val TEXTURE_EXTERNAL_OES = 0x8D65

    /**
     * informazioni relative allo schermo
     */
    val screenInfo = ScreenInfo()

    /**
     * livello di debug opengl
     */
    var openGLDebug = false

    /**
     * range delle larghezza delle linee
     */
    lateinit var lineWidthRange: IntArray
    /**
     * indica se le estensioni sono state già verificate
     */
    // private static boolean extensionAlreadyChecked = false;
    /**
     * Inizializza il sistema.
     *
     * @param openGLVersion
     * @param openGLDebugValue
     */
    fun startup(openGLVersion: Int, openGLDebugValue: Boolean) {
        openGLDebug = openGLDebugValue
        errors.append(0, "GL_NO_ERROR")
        errors.append(0x0500, "GL_INVALID_ENUM")
        errors.append(0x0501, "GL_INVALID_VALUE")
        errors.append(0x0502, "GL_INVALID_OPERATION")
        errors.append(0x0505, "GL_OUT_OF_MEMORY")
    }

    /**
     * Riversa una bitmap in una texture
     *
     * @param source
     * bitmap sorgente
     * @param target
     * texture id target
     * @return byte buffer contenente l'immagine
     */
    fun bitmapToTexImage2D(source: Bitmap, target: Int): ByteBuffer? {
        var fcbuffer: ByteBuffer? = null
        fcbuffer = ByteBuffer.allocateDirect(source.height * source.width * 4)
        source.copyPixelsToBuffer(fcbuffer)
        fcbuffer.position(0)

        // alloca e copia immagine
        GLES20.glTexImage2D(target, 0, GLES20.GL_RGBA, source.width, source.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, fcbuffer)
        // copia immagine
        // GLES20.glTexSubImage2D(target, 0, 0, 0, source.getWidth(), source.getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, fcbuffer);
        fcbuffer = null
        source.recycle()
        return fcbuffer
    }// put the maximum texture size in the array.

    /**
     * recupera le dimensioni delle texture massime per il device
     *
     *
     * @return dimensioni massime della texture 512 - 4096
     */
    val maxTextureSize: Int
        get() {
            // put the maximum texture size in the array.
            val max = IntArray(1)
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0)
            return max[0]
        }
    var errors = SparseArray<String>()
    fun translateError(code: Int): String {
        val msg = errors[code]
        return msg ?: "unknown"
    }

    /**
     * Clear error for OpenGL
     *
     */
    fun clearGlError() {
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
        }
    }

    /**
     * Error for OpenGL
     *
     * @param op
     */
    @JvmOverloads
    fun checkGlError(op: String, subOp: String? = "") {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Logger.error("GL20 %s %s: glError %s (code %s)", op, subOp, translateError(error), error)
            throw RuntimeException("$op: glError $error")
        }
    }

    fun updateScreen(screenWidth: Int, screenHeight: Int) {
        screenInfo.updateDimensions(screenWidth, screenHeight)
    }

    fun createArgonGLView(context: Context): ArgonGLView {
        //return new ArgonGLSurfaceView17(context);
        return ArgonGLSurfaceView16(context)
    }

    fun checkGLVersion() {
        version
    }
}