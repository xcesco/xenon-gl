package com.abubusoft.xenon.opengl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import com.abubusoft.xenon.XenonApplication4OpenGL;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.DeviceInfo;
import com.abubusoft.xenon.android.XenonGLDebugFlags;
import com.abubusoft.kripton.android.Logger;

import android.opengl.GLES10;
import android.opengl.GLES20;

public class XenonGLHelper {

	private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	/**
	 * Inizializza l'ambiente opengl, recuperando le informazioni relative al display (risoluzione) e al quantitativo di RAM presente sul dispostivo.
	 * 
	 * Queste informazioni verranno utilizzate per determinare la miglior configurazione da utilizzae
	 */
	public static void onStartup() {
		// rileva la risoluzione dello schermo
		DeviceInfo info = DeviceInfo.instance();
		Logger.info("RAM %s, CPU core %s", info.getAvailableRAM(), info.getCpuCores());

		XenonEGL xenonEGL = new XenonEGL();

		OffscreenSurface surface = new OffscreenSurface(xenonEGL, 1, 1);
		surface.makeCurrent();

		checkVersion();

		XenonApplication4OpenGL app = XenonBeanContext.getBean(XenonBeanType.APPLICATION);

		// impostiamo la configurazione prescelta per preselezionare la surfaceId da usare
		XenonGLConfigChooser.setOptions(app.chooseArgonGLConfig());
		XenonGLConfigChooser.build().findBestMatch(xenonEGL);

		Logger.info("OpenGL version %s", XenonGL.getVersion());
		checkGlExtensions();

		surface.release();
		xenonEGL.release();

	}

	public static EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
		Logger.info("creating OpenGL ES 2.0 context");
		int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
		EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
		return context;
	}

	public static EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
		int[] num_config = new int[1];

		int[] configSpec = filterConfigSpec();

		// recupera il numero di configurazioni ammesse dal device
		if (!egl.eglChooseConfig(display, configSpec, null, 0, num_config)) {
			throw new IllegalArgumentException("eglChooseConfig failed");
		}

		int numConfigs = num_config[0];

		if (numConfigs <= 0) {
			throw new IllegalArgumentException("No configs match configSpec");
		}

		EGLConfig[] configs = new EGLConfig[numConfigs];
		if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, num_config)) {
			throw new IllegalArgumentException("eglChooseConfig#2 failed");
		}
		EGLConfig config = configs[0];// chooseConfig(egl, display, configs);
		if (config == null) {
			throw new IllegalArgumentException("No config chosen");
		}
		return config;
	}

	private static int[] filterConfigSpec() {
		int[] configSpec = { EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_ALPHA_SIZE, 0, EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_STENCIL_SIZE, 0,

		EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */, EGL10.EGL_NONE };

		return configSpec;
	}

	public static void checkVersion() {
		OpenGLVersion version = null;
		String sVersion = GLES20.glGetString(GLES20.GL_VERSION).split(" ")[2];
		int a = sVersion.indexOf("V");
		if (a > -1) {
			// 3.1V@104.0
			sVersion = sVersion.substring(0, a);
			version = new OpenGLVersion(sVersion);
		} else {
			// 3.1
			version = new OpenGLVersion(sVersion);
		}

		XenonGL.version = version;

	}

	/**
	 * <p>
	 * Verifica le estensioni presenti sul dispositivo. Il controllo viene fatto una volta sola. Sempre questo metodo è responsabile dei flag in {@link XenonGLExtension}
	 * </p>
	 * *
	 */
	public static void checkGlExtensions() {

		// Impostiamo l'array del range delle linee
		XenonGL.lineWidthRange = new int[2];
		GLES20.glGetIntegerv(GLES20.GL_ALIASED_LINE_WIDTH_RANGE, XenonGL.lineWidthRange, 0);
		Logger.info("Lines width range supported [%s - %s]", XenonGL.lineWidthRange[0], XenonGL.lineWidthRange[1]);
		// GLES20.glGetIntegerv(GLES20.GL_ , range,0);

		// recuperiamo tutte le stringhe che contengono le varie estensioni
		ArrayList<String> extensions = new ArrayList<String>(Arrays.asList(GLES10.glGetString(GL10.GL_EXTENSIONS).split(" ")));
		Collections.sort(extensions);

		{
			XenonGLExtension xenonGLExtension;

			for (int i = 0; i < extensions.size(); i++) {
				xenonGLExtension = XenonGLExtension.parseAndFlag(extensions.get(i));
				if (XenonGLDebugFlags.DISPLAY_ALL_OPENGL_EXTENSION) {
					Logger.debug(String.format("Check opengl extension %s %s\n", extensions.get(i), xenonGLExtension != null ? "(SUPPORTED)" : ""));
				}
			}
					
		}

		{
			for (XenonGLExtension item : XenonGLExtension.values()) {

				if (item.isPresent())
					Logger.info(String.format("Supported opengl extensions %s\n", item));
			}
		}
	}
}
