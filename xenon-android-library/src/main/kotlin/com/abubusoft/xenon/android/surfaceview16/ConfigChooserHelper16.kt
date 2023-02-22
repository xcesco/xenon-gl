package com.abubusoft.xenon.android.surfaceview16

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.surfaceview.ConfigOptions
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

object ConfigChooserHelper16 {
    // costanti per il multisampling su tegra (https://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java?r=2)
    const val EGL_COVERAGE_BUFFERS_NV = 0x30E0
    const val EGL_COVERAGE_SAMPLES_NV = 0x30E1
    const val EGL_PRESERVED_RESOURCES = 0x3030
    const val EGL_OPENGL_ES2_BIT = 4
    const val EGL_OPENGL_ES3_BIT = 0x0005
    const val DELIMITER_NAME = 0xFFFF
    const val DELIMITER_VALUE = ""
    private val mValue = IntArray(1)
    fun findBestConfig(egl: EGL10?, display: EGLDisplay?, configs: Array<EGLConfig?>, options: ConfigOptions): EGLConfig? {
        var closestConfig: EGLConfig? = null
        var closestDistance = 1000
        for (config in configs) {
            //  -----------------------------------------------------------------
            val d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0)
            when (options.depthSize) {
                DepthSizeType.NONE -> continue
                DepthSizeType.DEPTH_SIZE_16, DepthSizeType.DEPTH_SIZE_24 -> if (options.depthSize.value != d) continue
                DepthSizeType.DONT_CARE -> {}
                else -> {}
            }

            //  -----------------------------------------------------------------
            val s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0)
            when (options.stencilSize) {
                StencilSizeType.NONE -> continue
                StencilSizeType.STENCIL_SIZE_8 -> if (options.stencilSize.value != d) continue
                StencilSizeType.DONT_CARE -> {}
                else -> {}
            }

            //  -----------------------------------------------------------------
            if (d >= options.depthSize.value && s >= options.stencilSize.value) {
                val r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0)
                val g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0)
                val b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0)
                val a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0)
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

    private fun findConfigAttrib(egl: EGL10?, display: EGLDisplay?, config: EGLConfig?, attribute: Int, defaultValue: Int): Int {
        return if (egl!!.eglGetConfigAttrib(display, config, attribute, mValue)) {
            mValue[0]
        } else defaultValue
    }

    /**
     * Costruisce una lista di attributi da applicare come filtro per una determinata configurazione
     *
     * @param options
     */
    fun buildConfigFilter(options: ConfigOptions): IntArray {
        return buildConfigFilter(options, ExtendedConfigOptions.DONT_CARE)
    }

    private fun buildConfigFilter(options: ConfigOptions, extended: ExtendedConfigOptions): IntArray {
        val list = ArrayList<Int>()
        when (options.displayFormat) {
            DisplayFormatType.RGB_565 -> {
                // @formatter:off			
                list.add(EGL10.EGL_RED_SIZE)
                list.add(5)
                list.add(EGL10.EGL_GREEN_SIZE)
                list.add(6)
                list.add(EGL10.EGL_BLUE_SIZE)
                list.add(5)
            }
            DisplayFormatType.RGBA_8888 -> {
                // @formatter:off
                list.add(EGL10.EGL_ALPHA_SIZE)
                list.add(8)
                list.add(EGL10.EGL_RED_SIZE)
                list.add(8)
                list.add(EGL10.EGL_GREEN_SIZE)
                list.add(8)
                list.add(EGL10.EGL_BLUE_SIZE)
                list.add(8)
            }
            DisplayFormatType.DONT_CARE -> {}
            else -> {}
        }
        when (options.depthSize) {
            DepthSizeType.DEPTH_SIZE_16 -> {
                // @formatter:off
                list.add(EGL10.EGL_DEPTH_SIZE)
                list.add(16)
            }
            DepthSizeType.DEPTH_SIZE_24 -> {
                // @formatter:off
                list.add(EGL10.EGL_DEPTH_SIZE)
                list.add(24)
            }
            DepthSizeType.NONE -> {
                // @formatter:off
                list.add(EGL10.EGL_DEPTH_SIZE)
                list.add(0)
            }
            DepthSizeType.DONT_CARE -> {}
            else -> {}
        }
        when (options.stencilSize) {
            StencilSizeType.STENCIL_SIZE_8 -> {
                // @formatter:off
                list.add(EGL10.EGL_STENCIL_SIZE)
                list.add(8)
            }
            StencilSizeType.NONE -> {
                // @formatter:off
                list.add(EGL10.EGL_STENCIL_SIZE)
                list.add(0)
            }
            StencilSizeType.DONT_CARE -> {}
            else -> {}
        }
        when (options.clientVersion) {
            ClientVersionType.OPENGL_ES_2 -> {
                // @formatter:off
                list.add(EGL10.EGL_RENDERABLE_TYPE)
                list.add(EGL_OPENGL_ES2_BIT)
            }
            ClientVersionType.OPENGL_ES_3, ClientVersionType.OPENGL_ES_3_1 -> {
                // @formatter:off
                list.add(EGL10.EGL_RENDERABLE_TYPE)
                list.add(EGL_OPENGL_ES3_BIT)
            }
            else -> {}
        }

        // ----- estensioni ------

        // le estensioni hanno precedenza sulla configurazione
        if (extended == ExtendedConfigOptions.MULTISAMPLE_NVIDIA) {
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
                    list.add(EGL10.EGL_SAMPLE_BUFFERS)
                    list.add(1)
                    list.add(EGL10.EGL_SAMPLES)
                    list.add(2)
                }
                MultiSampleType.NONE -> {
                    // @formatter:off
                    list.add(EGL10.EGL_SAMPLE_BUFFERS)
                    list.add(0)
                    list.add(EGL10.EGL_SAMPLES)
                    list.add(0)
                }
                MultiSampleType.DONT_CARE -> {}
                else -> {}
            }
        }
        list.add(EGL10.EGL_NONE)
        val result = IntArray(list.size)
        var i = 0
        for (item in list) {
            result[i++] = item
        }
        return result
    }

    fun printConfig(egl: EGL10?, display: EGLDisplay?, config: EGLConfig?) {
        // @formatter:off
        val attributes = intArrayOf(
            EGL10.EGL_CONFIG_ID,
            DELIMITER_NAME,
            EGL10.EGL_BUFFER_SIZE,
            DELIMITER_NAME,
            EGL10.EGL_RED_SIZE,
            EGL10.EGL_GREEN_SIZE,
            EGL10.EGL_BLUE_SIZE,
            EGL10.EGL_ALPHA_SIZE,
            DELIMITER_NAME,
            EGL10.EGL_DEPTH_SIZE,
            EGL10.EGL_STENCIL_SIZE,
            DELIMITER_NAME,
            EGL10.EGL_CONFIG_CAVEAT,
            DELIMITER_NAME,
            EGL10.EGL_LEVEL,
            EGL10.EGL_MAX_PBUFFER_HEIGHT,
            EGL10.EGL_MAX_PBUFFER_PIXELS,
            EGL10.EGL_MAX_PBUFFER_WIDTH,
            EGL10.EGL_NATIVE_RENDERABLE,
            EGL10.EGL_NATIVE_VISUAL_ID,
            EGL10.EGL_NATIVE_VISUAL_TYPE,
            DELIMITER_NAME,
            EGL10.EGL_SAMPLES,
            EGL10.EGL_SAMPLE_BUFFERS,
            EGL10.EGL_SURFACE_TYPE,
            DELIMITER_NAME,
            EGL10.EGL_TRANSPARENT_TYPE,
            EGL10.EGL_TRANSPARENT_RED_VALUE,
            EGL10.EGL_TRANSPARENT_GREEN_VALUE,
            EGL10.EGL_TRANSPARENT_BLUE_VALUE,
            DELIMITER_NAME,
            EGL10.EGL_LUMINANCE_SIZE,
            EGL10.EGL_ALPHA_MASK_SIZE,
            EGL10.EGL_COLOR_BUFFER_TYPE,
            EGL10.EGL_RENDERABLE_TYPE,
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
            "EGL_SAMPLES",
            "EGL_SAMPLE_BUFFERS",
            "EGL_SURFACE_TYPE",
            DELIMITER_VALUE,
            "EGL_TRANSPARENT_TYPE",
            "EGL_TRANSPARENT_RED_VALUE",
            "EGL_TRANSPARENT_GREEN_VALUE",
            "EGL_TRANSPARENT_BLUE_VALUE",
            DELIMITER_VALUE,
            "EGL_LUMINANCE_SIZE",
            "EGL_ALPHA_MASK_SIZE",
            "EGL_COLOR_BUFFER_TYPE",
            "EGL_RENDERABLE_TYPE",
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
            } else if (egl!!.eglGetConfigAttrib(display, config, attribute, value)) {
                buffer.append(String.format("  %s: %d", name, value[0]))
            } else {
                buffer.append(String.format("  %s: [NOT DEFINED]", name))
                egl.eglGetError()
            }
        }
        Logger.info(buffer.toString())
    }

    fun chooseConfig(egl: EGL10?, display: EGLDisplay?, configs: Array<EGLConfig?>, options: ConfigOptions): EGLConfig? {
        var best: EGLConfig? = null
        var bestAA: EGLConfig? = null
        var safe: EGLConfig? = null // default back to 565 when no exact match found
        for (config in configs) {
            val d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0)
            val s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0)

            // We need at least mDepthSize and mStencilSize bits
            if (d < options.depthSize.value || s < options.stencilSize.value) continue

            // We want an *exact* match for red/green/blue/alpha
            val r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0)
            val g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0)
            val b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0)
            val a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0)

            // Match RGB565 as a fallback
            if (safe == null && r == 5 && g == 6 && b == 5 && a == 0) {
                safe = config
            }
            // if we have a match, we chose this as our non AA fallback if that one
            // isn't set already.
            if (best == null && r == options.displayFormat.r && g == options.displayFormat.g && b == options.displayFormat.b && a == options.displayFormat.a) {
                best = config

                // if no AA is requested we can bail out here.
                if (options.multiSample === MultiSampleType.NONE) {
                    break
                }
            }

            // now check for MSAA support
            var hasSampleBuffers = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLE_BUFFERS, 0)
            var numSamples = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLES, 0)

            // We take the first sort of matching config, thank you.
            if (bestAA == null && hasSampleBuffers == 1 && options.multiSample === MultiSampleType.ENABLED && r == options.displayFormat.r && g == options.displayFormat.g && b == options.displayFormat.b && a == options.displayFormat.a) {
                bestAA = config
                continue
            }

            // for this to work we need to call the extension glCoverageMaskNV which is not
            // exposed in the Android bindings. We'd have to link agains the NVidia SDK and
            // that is simply not going to happen.
            // // still no luck, let's try CSAA support
            hasSampleBuffers = findConfigAttrib(egl, display, config, EGL_COVERAGE_BUFFERS_NV, 0)
            numSamples = findConfigAttrib(egl, display, config, EGL_COVERAGE_SAMPLES_NV, 0)

            // We take the first sort of matching config, thank you.
            if ((bestAA == null && hasSampleBuffers == 1) and (options.multiSample === MultiSampleType.ENABLED) and (r == options.displayFormat.r) && g == options.displayFormat.g && b == options.displayFormat.b && a == options.displayFormat.a) {
                bestAA = config
                continue
            }
        }
        return bestAA ?: (best ?: safe)
    }
}