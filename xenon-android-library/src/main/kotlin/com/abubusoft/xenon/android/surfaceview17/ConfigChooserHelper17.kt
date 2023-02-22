package com.abubusoft.xenon.android.surfaceview17

import android.annotation.TargetApi
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLDisplay
import android.os.Build
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.surfaceview.ConfigOptions
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.*
import com.abubusoft.xenon.android.surfaceview16.ExtendedConfigOptions

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
abstract class ConfigChooserHelper17 {
    fun findBestConfig(display: EGLDisplay, configs: Array<EGLConfig>, options: ConfigOptions): EGLConfig? {
        var closestConfig: EGLConfig? = null
        var closestDistance = 1000
        for (config in configs) {
            //  -----------------------------------------------------------------
            val d = findConfigAttrib(display, config, EGL14.EGL_DEPTH_SIZE, 0)
            when (options.depthSize) {
                DepthSizeType.NONE -> continue
                DepthSizeType.DEPTH_SIZE_16, DepthSizeType.DEPTH_SIZE_24 -> if (options.depthSize.value != d) continue
                DepthSizeType.DONT_CARE -> {}
                else -> {}
            }

            //  -----------------------------------------------------------------
            val s = findConfigAttrib(display, config, EGL14.EGL_STENCIL_SIZE, 0)
            when (options.stencilSize) {
                StencilSizeType.NONE -> continue
                StencilSizeType.STENCIL_SIZE_8 -> if (options.stencilSize.value != d) continue
                StencilSizeType.DONT_CARE -> {}
                else -> {}
            }

            //  -----------------------------------------------------------------
            if (d >= options.depthSize.value && s >= options.stencilSize.value) {
                val r = findConfigAttrib(display, config, EGL14.EGL_RED_SIZE, 0)
                val g = findConfigAttrib(display, config, EGL14.EGL_GREEN_SIZE, 0)
                val b = findConfigAttrib(display, config, EGL14.EGL_BLUE_SIZE, 0)
                val a = findConfigAttrib(display, config, EGL14.EGL_ALPHA_SIZE, 0)
                val distance =
                    Math.abs(r - options.displayFormat.r) + Math.abs(g - options.displayFormat.g) + Math.abs(b - options.displayFormat.b) + Math.abs(a - options.displayFormat.a)
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestConfig = config
                }
            }
        }
        return closestConfig
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
    private fun findConfigAttrib(display: EGLDisplay, config: EGLConfig, attribute: Int, defaultValue: Int): Int {
        return if (EGL14.eglGetConfigAttrib(display, config, attribute, mValue, 0)) {
            mValue[0]
        } else defaultValue
    }

    companion object {
        // costanti per il multisampling su tegra (https://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java?r=2)
        const val EGL_COVERAGE_BUFFERS_NV = 0x30E0
        const val EGL_COVERAGE_SAMPLES_NV = 0x30E1
        const val EGL_PRESERVED_RESOURCES = 0x3030
        const val EGL_OPENGL_ES3_BIT = 0x0005
        const val DELIMITER_NAME = 0xFFFF
        const val DELIMITER_VALUE = ""
        private val mValue = IntArray(1)

        /**
         * Costruisce una lista di attributi da applicare come filtro per una determinata configurazione
         *
         * @param options
         * @return
         */
        fun buildConfigFilter(options: ConfigOptions): IntArray {
            return buildConfigFilter(options, ExtendedConfigOptions.DONT_CARE)
        }

        fun listConfig(display: EGLDisplay?): Array<EGLConfig?> {
            val options = ConfigOptions.build().displayFormat(DisplayFormatType.RGB_565).depthSize(DepthSizeType.NONE).stencilSize(StencilSizeType.DONT_CARE)
                .multiSample(MultiSampleType.DONT_CARE)
            var attribList = buildConfigFilter(options)
            val configList = arrayOfNulls<EGLConfig>(20)
            val configCounter = IntArray(1)
            EGL14.eglChooseConfig(display, attribList, 0, configList, 0, configList.size, configCounter, 0)
            for (i in 0 until configCounter[0]) {
                printConfig(display, configList[i])
            }

            // gestione multisamping
            if (options.multiSample === MultiSampleType.ENABLED && configCounter[0] == 0) {
                // abbiamo richiesto antialiasing ma non abbiamo trovato nulla, abilitiamo extension nvidia
                Logger.warn("Multisample enabled, but no config found, try with NVIDIA EXTENSION")
                attribList = buildConfigFilter(options, ExtendedConfigOptions.MULTISAMPLE_NVIDIA)
                EGL14.eglChooseConfig(display, attribList, 0, configList, 0, configList.size, configCounter, 0)
                for (i in 0 until configCounter[0]) {
                    printConfig(display, configList[i])
                }
                if (configCounter[0] == 0) {
                    // proviamo senza niente
                    Logger.warn("No NVIDIA EXTENSION for antialiasing found, disable antialiasing")
                    options.multiSample = MultiSampleType.DONT_CARE
                    attribList = buildConfigFilter(options)
                    EGL14.eglChooseConfig(display, attribList, 0, configList, 0, configList.size, configCounter, 0)
                    for (i in 0 until configCounter[0]) {
                        printConfig(display, configList[i])
                    }
                }
            }
            return configList
        }

        private fun buildConfigFilter(options: ConfigOptions, extended: ExtendedConfigOptions): IntArray {
            val list = ArrayList<Int>()
            when (options.displayFormat) {
                DisplayFormatType.RGB_565 -> {
                    // @formatter:off			
                    list.add(EGL14.EGL_RED_SIZE)
                    list.add(5)
                    list.add(EGL14.EGL_GREEN_SIZE)
                    list.add(6)
                    list.add(EGL14.EGL_BLUE_SIZE)
                    list.add(5)
                }
                DisplayFormatType.RGBA_8888 -> {
                    // @formatter:off
                    list.add(EGL14.EGL_ALPHA_SIZE)
                    list.add(8)
                    list.add(EGL14.EGL_RED_SIZE)
                    list.add(8)
                    list.add(EGL14.EGL_GREEN_SIZE)
                    list.add(8)
                    list.add(EGL14.EGL_BLUE_SIZE)
                    list.add(8)
                }
                DisplayFormatType.DONT_CARE -> {}
                else -> {}
            }
            when (options.depthSize) {
                DepthSizeType.DEPTH_SIZE_16 -> {
                    // @formatter:off
                    list.add(EGL14.EGL_DEPTH_SIZE)
                    list.add(16)
                }
                DepthSizeType.DEPTH_SIZE_24 -> {
                    // @formatter:off
                    list.add(EGL14.EGL_DEPTH_SIZE)
                    list.add(24)
                }
                DepthSizeType.NONE -> {
                    // @formatter:off
                    list.add(EGL14.EGL_DEPTH_SIZE)
                    list.add(0)
                }
                DepthSizeType.DONT_CARE -> {}
                else -> {}
            }
            when (options.stencilSize) {
                StencilSizeType.STENCIL_SIZE_8 -> {
                    // @formatter:off
                    list.add(EGL14.EGL_STENCIL_SIZE)
                    list.add(8)
                }
                StencilSizeType.NONE -> {
                    // @formatter:off
                    list.add(EGL14.EGL_STENCIL_SIZE)
                    list.add(0)
                }
                StencilSizeType.DONT_CARE -> {}
                else -> {}
            }
            when (options.clientVersion) {
                ClientVersionType.OPENGL_ES_2 -> {
                    // @formatter:off
                    list.add(EGL14.EGL_RENDERABLE_TYPE)
                    list.add(EGL14.EGL_OPENGL_ES2_BIT)
                }
                else -> {}
            }

            // ----- estensioni ------

            // le estensioni hanno precedenza sulla configurazione
            if (extended === ExtendedConfigOptions.MULTISAMPLE_NVIDIA) {
                // @formatter:off
                list.add(EGL_COVERAGE_BUFFERS_NV)
                list.add(1)
                list.add(EGL_COVERAGE_SAMPLES_NV)
                list.add(2)
                // @formatter:on
            } else {
                // se non è richiesta il sampling NVIDIA possiamo vedere eventualmente se è richiesto il sampling standard
                when (options.multiSample) {
                    MultiSampleType.ENABLED -> {
                        // @formatter:off
                        list.add(EGL14.EGL_SAMPLE_BUFFERS)
                        list.add(1)
                        list.add(EGL14.EGL_SAMPLES)
                        list.add(2)
                    }
                    MultiSampleType.DONT_CARE -> {}
                    else -> {}
                }
            }
            list.add(EGL14.EGL_NONE)
            val result = IntArray(list.size)
            var i = 0
            for (item in list) {
                result[i++] = item
            }
            return result
        }

        fun printConfig(display: EGLDisplay?, config: EGLConfig?) {

            // @formatter:off
            val attributes = intArrayOf(
                EGL14.EGL_CONFIG_ID,
                DELIMITER_NAME,
                EGL14.EGL_BUFFER_SIZE,
                DELIMITER_NAME,
                EGL14.EGL_RED_SIZE,
                EGL14.EGL_GREEN_SIZE,
                EGL14.EGL_BLUE_SIZE,
                EGL14.EGL_ALPHA_SIZE,
                DELIMITER_NAME,
                EGL14.EGL_DEPTH_SIZE,
                EGL14.EGL_STENCIL_SIZE,
                DELIMITER_NAME,
                EGL14.EGL_CONFIG_CAVEAT,
                DELIMITER_NAME,
                EGL14.EGL_LEVEL,
                EGL14.EGL_MAX_PBUFFER_HEIGHT,
                EGL14.EGL_MAX_PBUFFER_PIXELS,
                EGL14.EGL_MAX_PBUFFER_WIDTH,
                EGL14.EGL_NATIVE_RENDERABLE,
                EGL14.EGL_NATIVE_VISUAL_ID,
                EGL14.EGL_NATIVE_VISUAL_TYPE,
                DELIMITER_NAME,
                EGL_PRESERVED_RESOURCES,
                DELIMITER_NAME,
                EGL14.EGL_SAMPLES,
                EGL14.EGL_SAMPLE_BUFFERS,
                EGL14.EGL_SURFACE_TYPE,
                DELIMITER_NAME,
                EGL14.EGL_TRANSPARENT_TYPE,
                EGL14.EGL_TRANSPARENT_RED_VALUE,
                EGL14.EGL_TRANSPARENT_GREEN_VALUE,
                EGL14.EGL_TRANSPARENT_BLUE_VALUE,
                EGL14.EGL_BIND_TO_TEXTURE_RGB,
                DELIMITER_NAME,
                EGL14.EGL_BIND_TO_TEXTURE_RGBA,
                DELIMITER_NAME,
                EGL14.EGL_MIN_SWAP_INTERVAL,
                EGL14.EGL_MAX_SWAP_INTERVAL,
                DELIMITER_NAME,
                EGL14.EGL_LUMINANCE_SIZE,
                EGL14.EGL_ALPHA_MASK_SIZE,
                EGL14.EGL_COLOR_BUFFER_TYPE,
                EGL14.EGL_RENDERABLE_TYPE,
                DELIMITER_NAME,
                EGL14.EGL_CONFORMANT,
                DELIMITER_NAME,
                EGL_COVERAGE_BUFFERS_NV,
                EGL_COVERAGE_SAMPLES_NV,
                DELIMITER_NAME
            )
            val names = arrayOf(
                "EGL_CONFIG_ID",
                DELIMITER_VALUE,
                "EGL_BUFFER_SIZE",
                DELIMITER_VALUE,
                "EGL_RED_SIZE",
                "EGL_GREEN_SIZE",
                "EGL_BLUE_SIZE",
                "EGL_ALPHA_SIZE",
                DELIMITER_VALUE,
                "EGL_DEPTH_SIZE",
                "EGL_STENCIL_SIZE",
                DELIMITER_VALUE,
                "EGL_CONFIG_CAVEAT",
                DELIMITER_VALUE,
                "EGL_LEVEL",
                "EGL_MAX_PBUFFER_HEIGHT",
                "EGL_MAX_PBUFFER_PIXELS",
                "EGL_MAX_PBUFFER_WIDTH",
                "EGL_NATIVE_RENDERABLE",
                "EGL_NATIVE_VISUAL_ID",
                "EGL_NATIVE_VISUAL_TYPE",
                DELIMITER_VALUE,
                "EGL_PRESERVED_RESOURCES",
                DELIMITER_VALUE,
                "EGL_SAMPLES",
                "EGL_SAMPLE_BUFFERS",
                "EGL_SURFACE_TYPE",
                DELIMITER_VALUE,
                "EGL_TRANSPARENT_TYPE",
                "EGL_TRANSPARENT_RED_VALUE",
                "EGL_TRANSPARENT_GREEN_VALUE",
                "EGL_TRANSPARENT_BLUE_VALUE",
                "EGL_BIND_TO_TEXTURE_RGB",
                DELIMITER_VALUE,
                "EGL_BIND_TO_TEXTURE_RGBA",
                DELIMITER_VALUE,
                "EGL_MIN_SWAP_INTERVAL",
                "EGL_MAX_SWAP_INTERVAL",
                DELIMITER_VALUE,
                "EGL_LUMINANCE_SIZE",
                "EGL_ALPHA_MASK_SIZE",
                "EGL_COLOR_BUFFER_TYPE",
                "EGL_RENDERABLE_TYPE",
                DELIMITER_VALUE,
                "EGL_CONFORMANT",
                DELIMITER_VALUE,
                "EGL_COVERAGE_BUFFERS_NV",
                "EGL_COVERAGE_SAMPLES_NV",
                DELIMITER_VALUE
            )
            // @formatter:on
            val value = IntArray(1)
            val buffer = StringBuffer()
            buffer.append("================================\n")
            for (i in attributes.indices) {
                val attribute = attributes[i]
                val name = names[i]
                if (attribute == DELIMITER_NAME) {
                    buffer.append("\n")
                } else if (EGL14.eglGetConfigAttrib(display, config, attribute, value, 0)) {
                    buffer.append(String.format("  %s: %d", name, value[0]))
                } else {
                    // Log.w(TAG, String.format("  %s: failed\n", name));
                    buffer.append(String.format("  %s: [NOT DEFINED]", name))
                    EGL14.eglGetError()
                    // while (egl.eglGetError() != EGL14.EGL_SUCCESS)
                    // ;
                }
            }
            Logger.info(buffer.toString())
        }
    }
}