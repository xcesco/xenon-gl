package com.abubusoft.xenon.android.surfaceview17;

import java.util.ArrayList;

import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.DepthSizeType;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.DisplayFormatType;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.MultiSampleType;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.StencilSizeType;
import com.abubusoft.xenon.android.surfaceview16.ExtendedConfigOptions;
import com.abubusoft.kripton.android.Logger;

import android.annotation.TargetApi;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLDisplay;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public abstract class ConfigChooserHelper17 {
	// costanti per il multisampling su tegra (https://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java?r=2)
	static final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
	static final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

	public static final int EGL_PRESERVED_RESOURCES = 0x3030;

	public static final int EGL_OPENGL_ES3_BIT = 0x0005;

	static final int DELIMITER_NAME = 0xFFFF;
	static final String DELIMITER_VALUE = "";

	private static int[] mValue = new int[1];

	public EGLConfig findBestConfig(EGLDisplay display, EGLConfig[] configs, ConfigOptions options) {
		EGLConfig closestConfig = null;
		int closestDistance = 1000;
		for (EGLConfig config : configs) {
			//  -----------------------------------------------------------------
			int d = findConfigAttrib(display, config, EGL14.EGL_DEPTH_SIZE, 0);
			switch (options.depthSize) {
			case NONE:
				continue;
			case DEPTH_SIZE_16:
			case DEPTH_SIZE_24:
				if (options.depthSize.value != d)
					continue;
				break;
			case DONT_CARE:
			default:
				break;
			}

			//  -----------------------------------------------------------------
			int s = findConfigAttrib(display, config, EGL14.EGL_STENCIL_SIZE, 0);
			switch (options.stencilSize) {
			case NONE:
				continue;
			case STENCIL_SIZE_8:
				if (options.stencilSize.value != d)
					continue;
				break;
			case DONT_CARE:
			default:
				break;
			}

			//  -----------------------------------------------------------------
			if (d >= options.depthSize.value && s >= options.stencilSize.value) {
				int r = findConfigAttrib(display, config, EGL14.EGL_RED_SIZE, 0);
				int g = findConfigAttrib(display, config, EGL14.EGL_GREEN_SIZE, 0);
				int b = findConfigAttrib(display, config, EGL14.EGL_BLUE_SIZE, 0);
				int a = findConfigAttrib(display, config, EGL14.EGL_ALPHA_SIZE, 0);
				int distance = Math.abs(r - options.displayFormat.r) + Math.abs(g - options.displayFormat.g) + Math.abs(b - options.displayFormat.b) + Math.abs(a - options.displayFormat.a);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestConfig = config;
				}
			}
		}
		return closestConfig;
	}

	/**
	 * Recupera l'attributo richiesto
	 * 
	 * @param display
	 * @param config
	 * @param attribute
	 * @param defaultValue
	 * @return
	 */
	private int findConfigAttrib(EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
		if (EGL14.eglGetConfigAttrib(display, config, attribute, mValue, 0)) {
			return mValue[0];
		}
		return defaultValue;
	}

	/**
	 * Costruisce una lista di attributi da applicare come filtro per una determinata configurazione
	 * 
	 * @param options
	 * @return
	 */
	public static int[] buildConfigFilter(ConfigOptions options) {
		return buildConfigFilter(options, ExtendedConfigOptions.DONT_CARE);
	}

	public static EGLConfig[] listConfig(EGLDisplay display) {

		ConfigOptions options = ConfigOptions.build().displayFormat(DisplayFormatType.RGB_565).depthSize(DepthSizeType.NONE).stencilSize(StencilSizeType.DONT_CARE).multiSample(MultiSampleType.DONT_CARE);
		int[] attribList = buildConfigFilter(options);

		EGLConfig[] configList = new EGLConfig[20];

		int[] configCounter = new int[1];

		EGL14.eglChooseConfig(display, attribList, 0, configList, 0, configList.length, configCounter, 0);
		for (int i = 0; i < configCounter[0]; i++) {
			printConfig(display, configList[i]);
		}

		// gestione multisamping
		if (options.multiSample == MultiSampleType.ENABLED && configCounter[0] == 0) {
			// abbiamo richiesto antialiasing ma non abbiamo trovato nulla, abilitiamo extension nvidia
			Logger.warn("Multisample enabled, but no config found, try with NVIDIA EXTENSION");
			attribList = buildConfigFilter(options, ExtendedConfigOptions.MULTISAMPLE_NVIDIA);
			EGL14.eglChooseConfig(display, attribList, 0, configList, 0, configList.length, configCounter, 0);
			for (int i = 0; i < configCounter[0]; i++) {
				printConfig(display, configList[i]);
			}

			if (configCounter[0] == 0) {
				// proviamo senza niente
				Logger.warn("No NVIDIA EXTENSION for antialiasing found, disable antialiasing");
				options.multiSample = MultiSampleType.DONT_CARE;
				attribList = buildConfigFilter(options);
				EGL14.eglChooseConfig(display, attribList, 0, configList, 0, configList.length, configCounter, 0);
				for (int i = 0; i < configCounter[0]; i++) {
					printConfig(display, configList[i]);
				}
			}
		}

		return configList;
	}

	private static int[] buildConfigFilter(ConfigOptions options, ExtendedConfigOptions extended) {
		ArrayList<Integer> list = new ArrayList<Integer>();

		// gestione del display format
		switch (options.displayFormat) {
		case RGB_565:
			// @formatter:off			
			list.add(EGL14.EGL_RED_SIZE);list.add(5);
			list.add(EGL14.EGL_GREEN_SIZE);list.add(6);
			list.add(EGL14.EGL_BLUE_SIZE);list.add(5);
			// @formatter:on
			break;
		case RGBA_8888:
			// @formatter:off
			list.add(EGL14.EGL_ALPHA_SIZE);list.add(8);
			list.add(EGL14.EGL_RED_SIZE);list.add(8);
			list.add(EGL14.EGL_GREEN_SIZE);list.add(8);
			list.add(EGL14.EGL_BLUE_SIZE);list.add(8);
			// @formatter:on
			break;
		case DONT_CARE:
		default:
			break;
		}

		// gestione del depth buffer
		switch (options.depthSize) {
		case DEPTH_SIZE_16:
			// @formatter:off
			list.add(EGL14.EGL_DEPTH_SIZE);list.add(16);
			// @formatter:on
			break;
		case DEPTH_SIZE_24:
			// @formatter:off
			list.add(EGL14.EGL_DEPTH_SIZE);list.add(24);
			// @formatter:on
			break;
		case NONE:
			// @formatter:off
			list.add(EGL14.EGL_DEPTH_SIZE);list.add(0);
			// @formatter:on
			break;
		case DONT_CARE:
		default:
			break;
		}

		// gestione dello stencil buffer
		switch (options.stencilSize) {
		case STENCIL_SIZE_8:
			// @formatter:off
			list.add(EGL14.EGL_STENCIL_SIZE);list.add(8);
			// @formatter:on
			break;
		case NONE:
			// @formatter:off
			list.add(EGL14.EGL_STENCIL_SIZE);list.add(0);
			// @formatter:on
			break;
		case DONT_CARE:
		default:
			break;
		}

		// gestione versione opengl es client
		switch (options.clientVersion) {
		case OPENGL_ES_2:
			// @formatter:off
			list.add(EGL14.EGL_RENDERABLE_TYPE);list.add(EGL14.EGL_OPENGL_ES2_BIT); 
			// @formatter:on
			break;
		default:
			break;
		}

		// ----- estensioni ------

		// le estensioni hanno precedenza sulla configurazione
		if (extended == ExtendedConfigOptions.MULTISAMPLE_NVIDIA) {
			// @formatter:off
			list.add(EGL_COVERAGE_BUFFERS_NV);list.add(1);
			list.add(EGL_COVERAGE_SAMPLES_NV);list.add(2); 
			// @formatter:on
		} else {
			// se non è richiesta il sampling NVIDIA possiamo vedere eventualmente se è richiesto il sampling standard
			switch (options.multiSample) {
			case ENABLED:
				// @formatter:off
				list.add(EGL14.EGL_SAMPLE_BUFFERS);list.add(1);
				list.add(EGL14.EGL_SAMPLES);list.add(2);
				
				// @formatter:on
				break;
			case DONT_CARE:
			default:
				break;
			}
		}

		list.add(EGL14.EGL_NONE);

		int[] result = new int[list.size()];
		int i = 0;
		for (Integer item : list) {
			result[i++] = item;
		}

		return result;
	}

	public static void printConfig(EGLDisplay display, EGLConfig config) {

		// @formatter:off
		int[] attributes = {
				EGL14.EGL_CONFIG_ID, DELIMITER_NAME,
				EGL14.EGL_BUFFER_SIZE, DELIMITER_NAME,
				EGL14.EGL_RED_SIZE, EGL14.EGL_GREEN_SIZE, EGL14.EGL_BLUE_SIZE, EGL14.EGL_ALPHA_SIZE, DELIMITER_NAME,
				EGL14.EGL_DEPTH_SIZE, EGL14.EGL_STENCIL_SIZE, DELIMITER_NAME,
				EGL14.EGL_CONFIG_CAVEAT, DELIMITER_NAME,
				EGL14.EGL_LEVEL, EGL14.EGL_MAX_PBUFFER_HEIGHT, EGL14.EGL_MAX_PBUFFER_PIXELS, EGL14.EGL_MAX_PBUFFER_WIDTH, EGL14.EGL_NATIVE_RENDERABLE, EGL14.EGL_NATIVE_VISUAL_ID, EGL14.EGL_NATIVE_VISUAL_TYPE, DELIMITER_NAME,
				EGL_PRESERVED_RESOURCES, DELIMITER_NAME,
				EGL14.EGL_SAMPLES, EGL14.EGL_SAMPLE_BUFFERS, EGL14.EGL_SURFACE_TYPE, DELIMITER_NAME,
				EGL14.EGL_TRANSPARENT_TYPE, EGL14.EGL_TRANSPARENT_RED_VALUE, EGL14.EGL_TRANSPARENT_GREEN_VALUE, EGL14.EGL_TRANSPARENT_BLUE_VALUE, EGL14.EGL_BIND_TO_TEXTURE_RGB, DELIMITER_NAME,
				EGL14.EGL_BIND_TO_TEXTURE_RGBA, DELIMITER_NAME,
				EGL14.EGL_MIN_SWAP_INTERVAL, EGL14.EGL_MAX_SWAP_INTERVAL, DELIMITER_NAME,
				EGL14.EGL_LUMINANCE_SIZE, EGL14.EGL_ALPHA_MASK_SIZE, EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RENDERABLE_TYPE, DELIMITER_NAME,
				EGL14.EGL_CONFORMANT, DELIMITER_NAME,
				EGL_COVERAGE_BUFFERS_NV, EGL_COVERAGE_SAMPLES_NV, DELIMITER_NAME };
		String[] names = {			
				"EGL_CONFIG_ID", DELIMITER_VALUE,
				"EGL_BUFFER_SIZE", DELIMITER_VALUE,
				"EGL_RED_SIZE", "EGL_GREEN_SIZE", "EGL_BLUE_SIZE", "EGL_ALPHA_SIZE", DELIMITER_VALUE,
				"EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", DELIMITER_VALUE,
				"EGL_CONFIG_CAVEAT", DELIMITER_VALUE,
				"EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT","EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", DELIMITER_VALUE,
				"EGL_PRESERVED_RESOURCES", DELIMITER_VALUE,
				"EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", DELIMITER_VALUE,
				"EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", DELIMITER_VALUE,
				"EGL_BIND_TO_TEXTURE_RGBA", DELIMITER_VALUE,
				"EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", DELIMITER_VALUE,
				"EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", DELIMITER_VALUE,
				"EGL_CONFORMANT", DELIMITER_VALUE,
				"EGL_COVERAGE_BUFFERS_NV", "EGL_COVERAGE_SAMPLES_NV", DELIMITER_VALUE };		
		// @formatter:on
		int[] value = new int[1];

		StringBuffer buffer = new StringBuffer();

		buffer.append("================================\n");
		for (int i = 0; i < attributes.length; i++) {
			int attribute = attributes[i];
			String name = names[i];
			if (attribute == DELIMITER_NAME) {
				buffer.append("\n");
			} else if (EGL14.eglGetConfigAttrib(display, config, attribute, value, 0)) {
				buffer.append(String.format("  %s: %d", name, value[0]));
			} else {
				// Log.w(TAG, String.format("  %s: failed\n", name));
				buffer.append(String.format("  %s: [NOT DEFINED]", name));
				EGL14.eglGetError();
				// while (egl.eglGetError() != EGL14.EGL_SUCCESS)
				// ;
			}
		}

		Logger.info(buffer.toString());
	}

}
