package com.abubusoft.xenon.android.surfaceview16;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.abubusoft.xenon.DeviceInfo;
import com.abubusoft.xenon.android.XenonGLDebugFlags;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.DisplayFormatType;
import com.abubusoft.xenon.opengl.XenonEGL;
import com.abubusoft.kripton.android.Logger;

/**
 * Consente di selezionare una configurazione in modo smart: se la memoria del device è inferiore al giga, allora forziamo il format del display a RGB_565. Questo per evitare sui dispositivi tipo Nexus 7 che ci siano dei problemi di
 * memoria.
 * 
 * @author xcesco
 *
 */
public class SmartConfigChooser implements ArgonConfigChooser16 {

	private static final int INVALID_CONFIG_ID = -1;

	public static int suggestedConfigId = INVALID_CONFIG_ID;

	/**
	 * inizializza il contesto
	 */
	@Override
	public void findBestMatch(XenonEGL xenonEGL) {
		chooseConfig(xenonEGL.getArgonEGL(), xenonEGL.getArgonGLDisplay());
	}

	protected ConfigOptions options;

	public SmartConfigChooser(ConfigOptions options) {
		this.options = options;
		DeviceInfo info = DeviceInfo.instance();

		if (options.displayFormat == DisplayFormatType.DONT_CARE) {
			// options.displayFormat(DisplayFormat.RGBA_8888);
			if (info.getAvailableRAM() > 1024) {
				Logger.info("Display Profile HIGH %s", DisplayFormatType.RGBA_8888);
				options.displayFormat(DisplayFormatType.RGBA_8888);
			} else {
				Logger.info("Display Profile LOW %s", DisplayFormatType.RGB_565);
				options.displayFormat(DisplayFormatType.RGB_565);
			}
		} else {
			Logger.info("Display Profile FORCED TO %s", options.displayFormat);

		}

	}

	@Override
	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
		EGLConfig suggestedConfig = null;
		if (suggestedConfigId == INVALID_CONFIG_ID) {
			int[] filter = ConfigChooserHelper16.buildConfigFilter(options);

			// prendiamo il numero di configurazioni disponibili
			int[] num_config = new int[1];
			egl.eglChooseConfig(display, filter, null, 0, num_config);
			int numConfigs = num_config[0];

			if (numConfigs <= 0) {
				throw new IllegalArgumentException("No configs match configSpec");
			}

			// now actually read the configurations.
			EGLConfig currentConfig;
			EGLConfig[] configs = new EGLConfig[numConfigs];
			egl.eglChooseConfig(display, filter, configs, configs.length, num_config);

			for (int i = 0; i < configs.length; i++) {
				currentConfig = configs[i];
				if (XenonGLDebugFlags.DISPLAY_ALL_GL_CONFIGS) {
					ConfigChooserHelper16.printConfig(egl, display, currentConfig);
				}
			}

			Logger.info("Smart config choose following config:");
			EGLConfig config = ConfigChooserHelper16.chooseConfig(egl, display, configs, options);
			ConfigChooserHelper16.printConfig(egl, display, config);

			int[] value = new int[1];
			egl.eglGetConfigAttrib(display, config, EGL10.EGL_CONFIG_ID, value);

			suggestedConfigId = value[0];
			suggestedConfig = config;
		} else {

			EGLConfig[] config = new EGLConfig[1];
			int[] attrib_list = { EGL10.EGL_CONFIG_ID, suggestedConfigId, EGL10.EGL_NONE };
			int[] count = new int[1];
			egl.eglChooseConfig(display, attrib_list, config, config.length, count);

			suggestedConfig = config[0];

			Logger.info("####=========RELOAD CONFIG_ID " + suggestedConfigId + " " + suggestedConfig);

		}

		return suggestedConfig;
	}

	@Override
	public int getPixelFormat() {
		return options.displayFormat.pixelFormat;
	}

}
