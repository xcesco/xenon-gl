package com.abubusoft.xenon.android.surfaceview16;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.DepthSizeType;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.DisplayFormatType;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.MultiSampleType;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.StencilSizeType;
import com.abubusoft.kripton.android.Logger;

public abstract class ConfigChooserHelper16 {
	// costanti per il multisampling su tegra (https://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java?r=2)
	static final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
	static final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;
	public static final int EGL_PRESERVED_RESOURCES = 0x3030;

	public static final int EGL_OPENGL_ES2_BIT = 4;
	public static final int EGL_OPENGL_ES3_BIT = 0x0005;

	static final int DELIMITER_NAME = 0xFFFF;
	static final String DELIMITER_VALUE = "";

	private static int[] mValue = new int[1];

	public static EGLConfig findBestConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs, ConfigOptions options) {
		EGLConfig closestConfig = null;
		int closestDistance = 1000;
		for (EGLConfig config : configs) {
			//  -----------------------------------------------------------------
			int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
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
			int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
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
				int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
				int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
				int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
				int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
				int distance = Math.abs(r - options.displayFormat.r) + Math.abs(g - options.displayFormat.g) + Math.abs(b - options.displayFormat.b) + Math.abs(a - options.displayFormat.a);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestConfig = config;
				}
			}
		}
		return closestConfig;
	}


	private static int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
		if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
			return mValue[0];
		}
		return defaultValue;
	}

	/**
	 * Costruisce una lista di attributi da applicare come filtro per una determinata configurazione
	 * 
	 * @param options
	 */
	public static int[] buildConfigFilter(ConfigOptions options) {
		return buildConfigFilter(options, ExtendedConfigOptions.DONT_CARE);
	}

	private static int[] buildConfigFilter(ConfigOptions options, ExtendedConfigOptions extended) {
		ArrayList<Integer> list = new ArrayList<Integer>();

		// gestione del display format
		switch (options.displayFormat) {
		case RGB_565:
			// @formatter:off			
			list.add(EGL10.EGL_RED_SIZE);list.add(5);
			list.add(EGL10.EGL_GREEN_SIZE);list.add(6);
			list.add(EGL10.EGL_BLUE_SIZE);list.add(5);
			// @formatter:on
			break;
		case RGBA_8888:
			// @formatter:off
			list.add(EGL10.EGL_ALPHA_SIZE);list.add(8);
			list.add(EGL10.EGL_RED_SIZE);list.add(8);
			list.add(EGL10.EGL_GREEN_SIZE);list.add(8);
			list.add(EGL10.EGL_BLUE_SIZE);list.add(8);
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
			list.add(EGL10.EGL_DEPTH_SIZE);list.add(16);
			// @formatter:on
			break;
		case DEPTH_SIZE_24:
			// @formatter:off
			list.add(EGL10.EGL_DEPTH_SIZE);list.add(24);
			// @formatter:on
			break;
		case NONE:
			// @formatter:off
			list.add(EGL10.EGL_DEPTH_SIZE);list.add(0);
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
			list.add(EGL10.EGL_STENCIL_SIZE);list.add(8);
			// @formatter:on
			break;
		case NONE:
			// @formatter:off
			list.add(EGL10.EGL_STENCIL_SIZE);list.add(0);
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
			list.add(EGL10.EGL_RENDERABLE_TYPE);list.add(EGL_OPENGL_ES2_BIT); 
			// @formatter:on
			break;
		case OPENGL_ES_3:
		case OPENGL_ES_3_1:
			// @formatter:off
			list.add(EGL10.EGL_RENDERABLE_TYPE);list.add(EGL_OPENGL_ES3_BIT); 
			// @formatter:on
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
				list.add(EGL10.EGL_SAMPLE_BUFFERS);list.add(1);
				list.add(EGL10.EGL_SAMPLES);list.add(2);
				
				// @formatter:on
				break;
			case NONE:
				// @formatter:off
				list.add(EGL10.EGL_SAMPLE_BUFFERS);list.add(0);
				list.add(EGL10.EGL_SAMPLES);list.add(0);
				
				// @formatter:on

			case DONT_CARE:
			default:
				break;
			}
		}

		list.add(EGL10.EGL_NONE);

		int[] result = new int[list.size()];
		int i = 0;
		for (Integer item : list) {
			result[i++] = item;
		}

		return result;
	}

	public static void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
		// @formatter:off
		int[] attributes = {
				EGL10.EGL_CONFIG_ID, DELIMITER_NAME,
				EGL10.EGL_BUFFER_SIZE, DELIMITER_NAME,
				EGL10.EGL_RED_SIZE, EGL10.EGL_GREEN_SIZE, EGL10.EGL_BLUE_SIZE, EGL10.EGL_ALPHA_SIZE, DELIMITER_NAME,
				EGL10.EGL_DEPTH_SIZE, EGL10.EGL_STENCIL_SIZE, DELIMITER_NAME,
				EGL10.EGL_CONFIG_CAVEAT, DELIMITER_NAME,
				EGL10.EGL_LEVEL, EGL10.EGL_MAX_PBUFFER_HEIGHT, EGL10.EGL_MAX_PBUFFER_PIXELS, EGL10.EGL_MAX_PBUFFER_WIDTH, EGL10.EGL_NATIVE_RENDERABLE, EGL10.EGL_NATIVE_VISUAL_ID, EGL10.EGL_NATIVE_VISUAL_TYPE, DELIMITER_NAME,
				EGL10.EGL_SAMPLES, EGL10.EGL_SAMPLE_BUFFERS, EGL10.EGL_SURFACE_TYPE, DELIMITER_NAME,
				EGL10.EGL_TRANSPARENT_TYPE, EGL10.EGL_TRANSPARENT_RED_VALUE, EGL10.EGL_TRANSPARENT_GREEN_VALUE, EGL10.EGL_TRANSPARENT_BLUE_VALUE, DELIMITER_NAME,
				EGL10.EGL_LUMINANCE_SIZE, EGL10.EGL_ALPHA_MASK_SIZE, EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RENDERABLE_TYPE, DELIMITER_NAME,
				EGL_COVERAGE_BUFFERS_NV, EGL_COVERAGE_SAMPLES_NV, DELIMITER_NAME };
		String[] names = {			
				"EGL_CONFIG_ID", DELIMITER_VALUE,
				"EGL_BUFFER_SIZE", DELIMITER_VALUE,
				"EGL_RED_SIZE", "EGL_GREEN_SIZE", "EGL_BLUE_SIZE", "EGL_ALPHA_SIZE", DELIMITER_VALUE,
				"EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", DELIMITER_VALUE,
				"EGL_CONFIG_CAVEAT", DELIMITER_VALUE,
				"EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT","EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", DELIMITER_VALUE,
				"EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", DELIMITER_VALUE,
				"EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", DELIMITER_VALUE,				
				"EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", DELIMITER_VALUE,
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
			} else if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
				buffer.append(String.format("  %s: %d", name, value[0]));
			} else {
				buffer.append(String.format("  %s: [NOT DEFINED]", name));
				egl.eglGetError();
			}
		}

		Logger.info(buffer.toString());
	}
	
	public static EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs, ConfigOptions options) {
		EGLConfig best = null;
		EGLConfig bestAA = null;
		EGLConfig safe = null; // default back to 565 when no exact match found

		for (EGLConfig config : configs) {
			int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
			int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);

			// We need at least mDepthSize and mStencilSize bits
			if (d < options.depthSize.value || s < options.stencilSize.value)
				continue;

			// We want an *exact* match for red/green/blue/alpha
			int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
			int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
			int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
			int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);

			// Match RGB565 as a fallback
			if (safe == null && r == 5 && g == 6 && b == 5 && a == 0) {
				safe = config;
			}
			// if we have a match, we chose this as our non AA fallback if that one
			// isn't set already.
			if (best == null && r == options.displayFormat.r && g == options.displayFormat.g && b == options.displayFormat.b && a == options.displayFormat.a) {
				best = config;

				// if no AA is requested we can bail out here.
				if (options.multiSample==MultiSampleType.NONE) {
					break;
				}
			}

			// now check for MSAA support
			int hasSampleBuffers = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLE_BUFFERS, 0);
			int numSamples = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLES, 0);

			// We take the first sort of matching config, thank you.
			if (bestAA == null && hasSampleBuffers == 1 && options.multiSample==MultiSampleType.ENABLED && r == options.displayFormat.r && g == options.displayFormat.g && b == options.displayFormat.b && a == options.displayFormat.a) {
				bestAA = config;
				continue;
			}

			// for this to work we need to call the extension glCoverageMaskNV which is not
			// exposed in the Android bindings. We'd have to link agains the NVidia SDK and
			// that is simply not going to happen.
			// // still no luck, let's try CSAA support
			hasSampleBuffers = findConfigAttrib(egl, display, config, EGL_COVERAGE_BUFFERS_NV, 0);
			numSamples = findConfigAttrib(egl, display, config, EGL_COVERAGE_SAMPLES_NV, 0);

			// We take the first sort of matching config, thank you.
			if (bestAA == null && hasSampleBuffers == 1 & options.multiSample==MultiSampleType.ENABLED & r == options.displayFormat.r && g == options.displayFormat.g && b == options.displayFormat.b && a == options.displayFormat.a) {
				bestAA = config;
				continue;
			}
		}

		if (bestAA != null)
			return bestAA;
		else if (best != null)
			return best;
		else
			return safe;
	}

}
