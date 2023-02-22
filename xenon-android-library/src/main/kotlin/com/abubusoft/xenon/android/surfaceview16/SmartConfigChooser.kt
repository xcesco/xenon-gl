package com.abubusoft.xenon.android.surfaceview16

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.DeviceInfo
import com.abubusoft.xenon.android.XenonGLDebugFlags
import com.abubusoft.xenon.android.surfaceview.ConfigOptions
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.DisplayFormatType
import com.abubusoft.xenon.opengl.XenonEGL
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Consente di selezionare una configurazione in modo smart: se la memoria del device è inferiore al giga, allora forziamo il format del display a RGB_565. Questo per evitare sui dispositivi tipo Nexus 7 che ci siano dei problemi di
 * memoria.
 *
 * @author xcesco
 */
class SmartConfigChooser(protected var options: ConfigOptions) : ArgonConfigChooser16 {
    /**
     * inizializza il contesto
     */
    override fun findBestMatch(xenonEGL: XenonEGL) {
        chooseConfig(xenonEGL.argonEGL, xenonEGL.argonGLDisplay)
    }

    init {
        val info = DeviceInfo
        if (options.displayFormat === DisplayFormatType.DONT_CARE) {
            // options.displayFormat(DisplayFormat.RGBA_8888);
            if (info.availableRAM > 1024) {
                Logger.info("Display Profile HIGH %s", DisplayFormatType.RGBA_8888)
                options.displayFormat(DisplayFormatType.RGBA_8888)
            } else {
                Logger.info("Display Profile LOW %s", DisplayFormatType.RGB_565)
                options.displayFormat(DisplayFormatType.RGB_565)
            }
        } else {
            Logger.info("Display Profile FORCED TO %s", options.displayFormat)
        }
    }

    override fun chooseConfig(egl: EGL10?, display: EGLDisplay?): EGLConfig? {
        var suggestedConfig: EGLConfig? = null
        if (suggestedConfigId == INVALID_CONFIG_ID) {
            val filter = ConfigChooserHelper16.buildConfigFilter(options)

            // prendiamo il numero di configurazioni disponibili
            val num_config = IntArray(1)
            egl!!.eglChooseConfig(display, filter, null, 0, num_config)
            val numConfigs = num_config[0]
            require(numConfigs > 0) { "No configs match configSpec" }

            // now actually read the configurations.
            var currentConfig: EGLConfig
            val configs = arrayOfNulls<EGLConfig>(numConfigs)
            egl.eglChooseConfig(display, filter, configs, configs.size, num_config)
            for (i in configs.indices) {
                currentConfig = configs[i]!!
                if (XenonGLDebugFlags.DISPLAY_ALL_GL_CONFIGS) {
                    ConfigChooserHelper16.printConfig(egl, display, currentConfig)
                }
            }
            Logger.info("Smart config choose following config:")
            val config = ConfigChooserHelper16.chooseConfig(egl, display, configs, options)
            ConfigChooserHelper16.printConfig(egl, display, config)
            val value = IntArray(1)
            egl.eglGetConfigAttrib(display, config, EGL10.EGL_CONFIG_ID, value)
            suggestedConfigId = value[0]
            suggestedConfig = config
        } else {
            val config = arrayOfNulls<EGLConfig>(1)
            val attrib_list = intArrayOf(EGL10.EGL_CONFIG_ID, suggestedConfigId, EGL10.EGL_NONE)
            val count = IntArray(1)
            egl!!.eglChooseConfig(display, attrib_list, config, config.size, count)
            suggestedConfig = config[0]
            Logger.info("####=========RELOAD CONFIG_ID " + suggestedConfigId + " " + suggestedConfig)
        }
        return suggestedConfig
    }

    override val pixelFormat: Int
        get() = options.displayFormat.pixelFormat

    companion object {
        private const val INVALID_CONFIG_ID = -1
        var suggestedConfigId = INVALID_CONFIG_ID
    }
}