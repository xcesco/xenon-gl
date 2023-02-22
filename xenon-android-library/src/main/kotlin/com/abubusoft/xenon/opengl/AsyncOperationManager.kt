package com.abubusoft.xenon.opengl

import android.annotation.SuppressLint
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.misc.Clock.now
import com.abubusoft.xenon.texture.Texture
import com.abubusoft.xenon.texture.TextureAsyncLoaderListener
import com.abubusoft.xenon.texture.TextureInfo
import javax.microedition.khronos.egl.*

/**
 *
 *
 * Si occupa di gestire le operazioni opengl async in un secondo contesto. Nel caso in cui non sia possibile a causa del device o da impostazioni dell'applicazione, questo manager
 * consente di effettuare le stesse operazioni in modo sincrono, bloccando e quindi introducendo dei lag, ma almeno l'operazione non viene impedita.
 *
 *
 *
 *
 * This class holds the shared context magic and is based on informations gathered from the following sources:
 *
 *
 *  * [Loading-textures-in-a-background-thread-on-Android](http://www.khronos.org/message_boards/showthread.php/9029-Loading-textures-in-a-background-thread-on-Android)
 *  * [5843-Texture-Sharing](http://www.khronos.org/message_boards/showthread.php/5843-Texture-Sharing)
 *  * [why-is-eglmakecurrent-failing-with-egl-bad-match](http://stackoverflow.com/questions/14062803/why-is-eglmakecurrent-failing-with-egl-bad-match)
 *
 *
 * @author Francesco Benincasa
 */
object AsyncOperationManager {
    /**
     *
     *
     * Indica se il sistema è su.
     *
     *
     * @return
     */
    /**
     * indica se le operazioni sono fattibili o meno
     */
    var isEnabled = false

    interface AsyncTextureInfoLoader {
        fun load(texture: Texture?): TextureInfo?
    }

    class TextureLoaderThread : Thread("GLThread-AsyncOperation") {
        var handler: Handler? = null

        @SuppressLint("HandlerLeak")
        override fun run() {
            Looper.prepare()
            val pbufferAttribs = intArrayOf(EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL_TEXTURE_TARGET, EGL_NO_TEXTURE, EGL_TEXTURE_FORMAT, EGL_NO_TEXTURE, EGL10.EGL_NONE)
            surfaceForTextureLoad = egl!!.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs)
            egl!!.eglMakeCurrent(display, surfaceForTextureLoad, surfaceForTextureLoad, textureContext)
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val content = msg.obj as MessageContent
                    val start2 = now()
                    Logger.debug("context switch for async texture load stopped ")

                    // EGLSurface defaultSurface =
                    // egl.eglGetCurrentSurface(EGL10.EGL_DRAW);
                    val start1 = now()
                    Logger.debug("async texture load stopped ")
                    content.texture.updateInfo(content.execute.load(content.texture))
                    Logger.debug("async texture load ended in %s ms", now() - start1)

                    // egl.eglMakeCurrent(display, defaultSurface,
                    // defaultSurface, screenContext);

                    // distruggiamo subiot
                    // egl.eglDestroySurface(display, surfaceForTextureLoad);
                    Logger.debug("context switch for async texture load ended in %s ms", now() - start2)
                    if (content.listener != null) content.listener!!.onTextureReady(content.texture)
                }
            }
            Looper.loop()
        }
    }

    private var textureContext: javax.microedition.khronos.egl.EGLContext? = null
    private var egl: EGL10? = null
    private var display: javax.microedition.khronos.egl.EGLDisplay? = null
    private var eglConfig: javax.microedition.khronos.egl.EGLConfig? = null
    internal var surfaceForTextureLoad: EGLSurface? = null
    private var textureLoaderThread: TextureLoaderThread? = null

    /**
     *
     *
     * Inizializza l'async loader. Se non è possibile impostare le operazioni asyncrone, tutte le operazioni successive verranno fatte in modo sincrono.
     *
     *
     * @param egl
     * @param renderContext
     * @param display
     * @param eglConfig
     */
    fun init(
        egl: EGL10,
        renderContext: javax.microedition.khronos.egl.EGLContext?,
        display: javax.microedition.khronos.egl.EGLDisplay?,
        eglConfig: javax.microedition.khronos.egl.EGLConfig?,
    ) {
        // la versione usata è la 2!
        val attrib_list = intArrayOf(XenonEGL.Companion.EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        this.egl = egl
        this.display = display
        this.eglConfig = eglConfig

        // screenContext = renderContext;
        textureContext = egl.eglCreateContext(display, eglConfig, renderContext, attrib_list)
        if (textureContext !== EGL10.EGL_NO_CONTEXT) {
            Logger.info("Context for async operation asyncMode.")
            isEnabled = true
            // creiamo il thread per le operazioni async su opengl
            textureLoaderThread = TextureLoaderThread()
            textureLoaderThread!!.start()
        } else {
            isEnabled = false
            Logger.fatal("Try to enable context for async operation, but failed.")
        }
    }

    var EGL_CONTEXT_CLIENT_VERSION = 0x3098

    /**
     *
     *
     * Inizializza l'async loader. Se non è possibile impostare le operazioni asyncrone, tutte le operazioni successive verranno fatte in modo sincrono.
     *
     *
     * @param egl
     * @param renderContext
     * @param display
     * @param eglConfig
     */
    //TODO da fixare
    fun init(renderContext: EGLContext?, display: EGLDisplay?, eglConfig: EGLConfig?) {
        // la versione usata è la 2!
        val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)

        //	this.display = display;
        //	this.eglConfig = eglConfig;

        // screenContext = renderContext;
        //	textureContext = EGL14.eglCreateContext(display, eglConfig, renderContext, attrib_list);
        if (textureContext !== EGL10.EGL_NO_CONTEXT) {
            Logger.info("Context for async operation asyncMode.")
            isEnabled = true
            // creiamo il thread per le operazioni async su opengl
            textureLoaderThread = TextureLoaderThread()
            textureLoaderThread!!.start()
        } else {
            isEnabled = false
            Logger.fatal("Try to enable context for async operation, but failed.")
        }
    }

    fun destroy(egl: EGL10): Boolean {
        return egl.eglDestroyContext(display, textureContext)
    }

    //TODO da fixare
    fun destroy(): Boolean {
        //return EGL14.eglDestroyContext(display, textureContext);
        return false
    }

    /**
     *
     *
     * Se i.
     *
     *
     * @param <T>
     *
     * @param execute
     * @param texture
     * @param listener
    </T> */
    fun load(texture: Texture, execute: AsyncTextureInfoLoader, listener: TextureAsyncLoaderListener?): TextureInfo? {
        return if (isEnabled) {
            val content = MessageContent(texture, execute, listener)
            val msg = textureLoaderThread!!.handler!!.obtainMessage(25, content)
            textureLoaderThread!!.handler!!.sendMessage(msg)
            null
        } else {
            Logger.error("async operations on textures are disabled! This device support multiple opengl context?")
            Logger.warn("run texture update in single thread!")
            execute.load(texture)
            listener?.onTextureReady(texture)
            texture.info
        }
    }

    fun init() {
        isEnabled = false
    }

    const val EGL_TEXTURE_TARGET = 12417
    const val EGL_NO_TEXTURE = 12380
    const val EGL_TEXTURE_FORMAT = 12416

    /**
     *
     * @author Francesco Benincasa
     */
    class MessageContent(var texture: Texture, var execute: AsyncOperationManager.AsyncTextureInfoLoader, var listener: TextureAsyncLoaderListener?)

}
