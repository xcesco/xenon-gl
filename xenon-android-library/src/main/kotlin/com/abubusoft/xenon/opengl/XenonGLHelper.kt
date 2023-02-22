package com.abubusoft.xenon.opengl

import android.opengl.GLES10
import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.DeviceInfo
import com.abubusoft.xenon.XenonApplication4OpenGL
import com.abubusoft.xenon.android.XenonGLDebugFlags
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.context.XenonBeanType
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10

object XenonGLHelper {
    private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098

    /**
     * Inizializza l'ambiente opengl, recuperando le informazioni relative al display (risoluzione) e al quantitativo di RAM presente sul dispostivo.
     *
     * Queste informazioni verranno utilizzate per determinare la miglior configurazione da utilizzae
     */
    fun onStartup() {
        // rileva la risoluzione dello schermo
        val info: DeviceInfo = DeviceInfo()
        Logger.info("RAM %s, CPU core %s", info.availableRAM, info.cpuCores)
        val xenonEGL = XenonEGL()
        val surface = OffscreenSurface(xenonEGL, 1, 1)
        surface.makeCurrent()
        checkVersion()
        val app = getBean<XenonApplication4OpenGL>(XenonBeanType.APPLICATION)

        // impostiamo la configurazione prescelta per preselezionare la surfaceId da usare
        XenonGLConfigChooser.options = app!!.chooseArgonGLConfig()
        XenonGLConfigChooser.build().findBestMatch(xenonEGL)
        Logger.info("OpenGL version %s", XenonGL.version)
        checkGlExtensions()
        surface.release()
        xenonEGL.release()
    }

    fun createContext(
        egl: EGL10,
        display: EGLDisplay?,
        eglConfig: EGLConfig?,
    ): EGLContext {
        Logger.info("creating OpenGL ES 2.0 context")
        val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list)
    }

    fun chooseConfig(egl: EGL10, display: EGLDisplay?): EGLConfig {
        val num_config = IntArray(1)
        val configSpec = filterConfigSpec()

        // recupera il numero di configurazioni ammesse dal device
        require(egl.eglChooseConfig(display, configSpec, null, 0, num_config)) { "eglChooseConfig failed" }
        val numConfigs = num_config[0]
        require(numConfigs > 0) { "No configs match configSpec" }
        val configs = arrayOfNulls<EGLConfig>(numConfigs)
        require(egl.eglChooseConfig(display, configSpec, configs, numConfigs, num_config)) { "eglChooseConfig#2 failed" }
        return configs[0] ?: throw IllegalArgumentException("No config chosen")
    }

    private fun filterConfigSpec(): IntArray {
        return intArrayOf(
            EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_ALPHA_SIZE, 0, EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */, EGL10.EGL_NONE
        )
    }

    fun checkVersion() {
        var version: OpenGLVersion? = null
        var sVersion = GLES20.glGetString(GLES20.GL_VERSION).split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]
        val a = sVersion.indexOf("V")
        if (a > -1) {
            // 3.1V@104.0
            sVersion = sVersion.substring(0, a)
            version = OpenGLVersion(sVersion)
        } else {
            // 3.1
            version = OpenGLVersion(sVersion)
        }
        XenonGL.version = version
    }

    /**
     *
     *
     * Verifica le estensioni presenti sul dispositivo. Il controllo viene fatto una volta sola. Sempre questo metodo è responsabile dei flag in [XenonGLExtension]
     *
     * *
     */
    fun checkGlExtensions() {

        // Impostiamo l'array del range delle linee
        XenonGL.lineWidthRange = IntArray(2)
        GLES20.glGetIntegerv(GLES20.GL_ALIASED_LINE_WIDTH_RANGE, XenonGL.lineWidthRange, 0)
        Logger.info("Lines width range supported [%s - %s]", XenonGL.lineWidthRange[0], XenonGL.lineWidthRange[1])
        // GLES20.glGetIntegerv(GLES20.GL_ , range,0);

        // recuperiamo tutte le stringhe che contengono le varie estensioni
        val extensions = ArrayList(Arrays.asList(*GLES10.glGetString(GL10.GL_EXTENSIONS).split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()))
        Collections.sort(extensions)
        run {
            var xenonGLExtension: XenonGLExtension
            for (i in extensions.indices) {
                xenonGLExtension = XenonGLExtension.parseAndFlag(extensions[i])
                if (XenonGLDebugFlags.DISPLAY_ALL_OPENGL_EXTENSION) {
                    Logger.debug(String.format("Check opengl extension %s %s\n", extensions[i], if (xenonGLExtension != null) "(SUPPORTED)" else ""))
                }
            }
        }
        run {
            for (item in XenonGLExtension.values()) {
                if (item.isPresent) Logger.info(String.format("Supported opengl extensions %s\n", item))
            }
        }
    }
}