package com.abubusoft.xenon.opengl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGL10;

import com.abubusoft.xenon.ScreenInfo;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.SparseArray;

/**
 * Wrapper dell'openGL 2. Gestisce le funzioni in comune. Deve essere inizializzato allo startup dell'applicazione al fine di decidere cosa fare.
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class XenonGL {
	
	public final static int EGL_OPENGL_ES2_BIT=4;
	
	public final static int EGL_OPENGL_ES3_BIT=64;

	static OpenGLVersion version;
	
	private static Boolean gl30Enabled;
	
	private static Boolean gl31Enabled;

	/**
	 * <p>Recupera le informazioni della versione di OpenGL supportata dal device.</p>
	 * <p>Come da specifiche <a href="https://www.khronos.org/opengles/sdk/docs/man/xhtml/glGetString.xml">khronos</a>.</p>
	 * 
	 * @return
	 * 		versione openg suppotata dai driver
	 */
	public static OpenGLVersion getVersion() {
		return version;
	}
	
	/**
	 * Verifica se il device ha il supporto ad opengl es 3.0
	 * 
	 * @return
	 * 		true se supporta opengl-es 3.0
	 */
	public static boolean isGL30Supported()
	{
		if (gl30Enabled==null)
		{
			version=getVersion();
			gl30Enabled=version.isGreaterEqualsThan("3.0");
		}
		
		return gl30Enabled;
	}

	/**
	 * Verifica se il device ha il supporto ad opengl es 3.1
	 * 
	 * @return
	 * 		true se supporta opengl-es 3.1
	 */
	public static boolean isGL31Supported()
	{
		if (gl31Enabled==null)
		{
			version=getVersion();
			gl31Enabled=version.isGreaterEqualsThan("3.1");
		}
		
		return gl31Enabled;
	}
	

	private static SparseArray<String> fields;

	public static String decodeEGLConstant(int value) {

		synchronized (fields) {
			if (fields == null) {
				fields = new SparseArray<String>();
				Field[] col = EGL10.class.getFields();
				for (Field item : col) {
					if (item.getType() == Integer.class || item.getType() == Integer.TYPE) {
						try {
							fields.put(item.getInt(null), item.getName());
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}

		return fields.get(value);

	}

	/**
	 * half float
	 * 
	 * <a href="http://www.khronos.org/registry/gles/extensions/OES/OES_texture_float.txt">OES_texture_float</a>
	 */
	public final static int GL_HALF_FLOAT_OES = 0x8D61;

	/**
	 * 
	 * tipo di texture esterna
	 * 
	 * <a href="http://www.khronos.org/registry/gles/extensions/OES/OES_EGL_image_external.txt">OES_EGL_image_external</a>
	 */
	public final static int TEXTURE_EXTERNAL_OES = 0x8D65;

	/**
	 * informazioni relative allo schermo
	 */
	public final static ScreenInfo screenInfo = new ScreenInfo();

	/**
	 * livello di debug opengl
	 */
	public static boolean openGLDebug;

	/**
	 * range delle larghezza delle linee
	 */
	public static int[] lineWidthRange;

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
	public static void startup(int openGLVersion, boolean openGLDebugValue) {
		openGLDebug = openGLDebugValue;

		errors.append(0, "GL_NO_ERROR");
		errors.append(0x0500, "GL_INVALID_ENUM");
		errors.append(0x0501, "GL_INVALID_VALUE");
		errors.append(0x0502, "GL_INVALID_OPERATION");
		errors.append(0x0505, "GL_OUT_OF_MEMORY");
	}

	/**
	 * Riversa una bitmap in una texture
	 * 
	 * @param source
	 *            bitmap sorgente
	 * @param target
	 *            texture id target
	 * @return byte buffer contenente l'immagine
	 */
	public static ByteBuffer bitmapToTexImage2D(Bitmap source, int target) {
		ByteBuffer fcbuffer = null;
		fcbuffer = ByteBuffer.allocateDirect(source.getHeight() * source.getWidth() * 4);

		source.copyPixelsToBuffer(fcbuffer);
		fcbuffer.position(0);

		// alloca e copia immagine
		GLES20.glTexImage2D(target, 0, GLES20.GL_RGBA, source.getWidth(), source.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, fcbuffer);
		// copia immagine
		// GLES20.glTexSubImage2D(target, 0, 0, 0, source.getWidth(), source.getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, fcbuffer);

		fcbuffer = null;
		source.recycle();

		return fcbuffer;
	}

	/**
	 * recupera le dimensioni delle texture massime per il device
	 * 
	 * 
	 * @return dimensioni massime della texture 512 - 4096
	 */
	public static int getMaxTextureSize() {
		// put the maximum texture size in the array.
		int[] max = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0);

		return max[0];
	}

	public static void checkGlError(String op) {
		checkGlError(op, "");
	}

	static SparseArray<String> errors = new SparseArray<String>();

	public static String translateError(int code) {
		String msg = errors.get(code);
		return msg == null ? "unknown" : msg;
	}

	/**
	 * Clear error for OpenGL
	 * 
	 */
	public static void clearGlError() {
		while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
		}
	}

	/**
	 * Error for OpenGL
	 * 
	 * @param op
	 */
	public static void checkGlError(String op, String subOp) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Logger.error("GL20 %s %s: glError %s (code %s)", op, subOp, translateError(error), error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

	public static void updateScreen(int screenWidth, int screenHeight) {
		screenInfo.updateDimensions(screenWidth, screenHeight);		
	}

	public static ArgonGLView createArgonGLView(Context context) {
		//return new ArgonGLSurfaceView17(context);
		return new ArgonGLSurfaceView16(context);
	}

	public static void checkGLVersion() {
		getVersion();		
	}

}
