package com.abubusoft.xenon

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.SystemClock
import android.view.Window
import android.view.WindowManager
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.XenonActivity4OpenGL
import com.abubusoft.xenon.android.XenonView4OpenGL
import com.abubusoft.xenon.android.XenonWallpaper
import com.abubusoft.xenon.android.listener.XenonGestureDetector
import com.abubusoft.xenon.android.listener.XenonGestureListener
import com.abubusoft.xenon.camera.CameraManager
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.engine.Phase
import com.abubusoft.xenon.misc.FPSCounter
import com.abubusoft.xenon.misc.FPSLimiter
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.opengl.XenonGLConfigChooser
import com.abubusoft.xenon.opengl.XenonGLHelper
import com.abubusoft.xenon.opengl.XenonGLRenderer
import com.abubusoft.xenon.settings.XenonSettings
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.texture.TextureManager
import com.abubusoft.xenon.vbo.BufferManager

class Xenon4OpenGL : Xenon4BaseImpl<XenonApplication4OpenGL>(), Xenon {
    /**
     * indica se è la prima volta che si sta creando la scena
     */
    protected var firstSceneCreation = false

    /**
     * Indica che la scena è pronta per essere renderizzata. Serve in particolar
     * modo per verificare se gestione o meno del touch
     *
     * @return true se la scena è pronta per essere disegnata.
     */
    var isSceneReady = false
        protected set

    /**
     *
     */
    fun reset() {
        firstSceneCreation = true
    }

    /**
     * listener delle gesture
     */
    var gestureListener: XenonGestureListener? = null

    /**
     * effettua la configurazione di xenon
     */
    fun applySettings() {
        // carica la configurazione

        // impostiamo la camera di default.
        CameraManager.init(settings.viewFrustum)

        // opengl
        XenonGL.startup(settings.openGL.version, settings.openGL.debug)
        FPSLimiter.maxFrameRate = settings.openGL.maxFPS

        // gestore delle gesture
        try {
            gestureListener = XenonBeanContext.createInstance(Class.forName(settings.application.gestureListenerClazz.trim { it <= ' ' })) as XenonGestureListener
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
        }
    }

    /**
     * Renderer utilizzato per il rendering sia da wallpaper che da activity
     */
    lateinit var renderer: XenonGLRenderer

    /**
     * indica il tempo attuale in mills. Utilizzato in [.onDrawFrameBegin]
     */
    private var now: Long = 0

    /**
     *
     *
     * Activity corrente nella quale è presente la openglView [XenonView4OpenGL]
     *
     */
    var activity: XenonActivity4OpenGL? = null
    private var screenIsChanged = false
    private var somePreferenceIsChanged = false

    /**
     * Da eseguire durante la creazione dell'activity: imposta i flag per nascondere il titolo della finestra, crea il renderer e lo associa all'activity.
     *
     * @param currentActivity
     * @throws Exception
     */
    @Throws(Exception::class)
    fun onActivityCreated(currentActivity: XenonActivity4OpenGL) {
        try {
            activity = currentActivity
            reset()

            // finestra senza titolo
            currentActivity.requestWindowFeature(Window.FEATURE_NO_TITLE)
            currentActivity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

            // surface per il rendering
            //GLSurfaceViewEGL14  view = new GLSurfaceViewEGL14(currentActivity);
            val view = XenonGL.createArgonGLView(currentActivity)

            // abilitiamo se richiesto il debug opengl
            if (settings.openGL.debug) {
                view.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR or GLSurfaceView.DEBUG_LOG_GL_CALLS)
            }

            // impostiamo il safeMode
            renderer = currentActivity.createRenderer()
            renderer.setSafeMode(settings.openGL.safeMode)
            view.setEGLContextClientVersion(2)
            view.setPreserveEGLContextOnPause(true)

            // impostiamo il formato a 32 bit
            view.holder.setFormat(XenonGLConfigChooser.build().pixelFormat)
            view.setRenderer(renderer)

            // impostiamo la glSurfaceView per l'activity
            currentActivity.setArgonGLSurfaceView(view)

            // gestore delle gesture, lo impostiamo sull'activity o sul service
            currentActivity.gestureDetector = XenonGestureDetector(this, gestureListener!!)
        } catch (e: Exception) {
            // e' un errore che dobbiamo cmq visualizzare. Poi cmq sollevviamo
            // l'eccezione.
            Logger.fatal(e.message)
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Creazione del servizio ed impostazione del renderer
     *
     * @param service
     */
    @Synchronized
    fun onServiceCreated(service: XenonWallpaper) {
        try {
            reset()
            // renderer = (ArgonRenderer) Class.forName(settings.openGL.rendererClazz).newInstance();
            // renderer=new ArgonRendererV2();
            renderer = service.createRenderer()
            renderer.setSafeMode(settings.openGL.safeMode)
            service.setRenderer(renderer)
            // gestore delle gesture, lo impostiamo sull'activity o sul service
            service.setGestureDetector(XenonGestureDetector(this, gestureListener!!))
            // impostiamo il formato a 32 bit
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    @get:Synchronized
    val isSceneToCreate: Boolean
        get() = firstSceneCreation || somePreferenceIsChanged || screenIsChanged || isPreviewStatusChanged

    @Synchronized
    fun onSceneCreation() {
        // Logger.info("XENON onSurfaceChanged");
        // boolean screenIsChanged;
        // boolean somePreferenceIsChanged;

        // lo dobbiamo fare prima di camera manager, ovvero prima che cambi
        // screenInfo

        // la prima volta ha senso, poi
        // screenWidth = width;
        // screenHeight = height;
        // firstSceneCreation=true;

        // QUA c'era tutto il codice prima
        Logger.info(
            "Scene changed firstSceneCreation: %s, somePreferenceIsChanged: %s, screenIsChanged: %s, previewStateIsChanged: %s",
            firstSceneCreation,
            somePreferenceIsChanged,
            screenIsChanged,
            isPreviewStatusChanged
        )
        if (isSceneToCreate) {
            try {
                TextureManager.instance().clearTextures()
                ShaderManager.instance().clearShaders()
                BufferManager.instance().clearBuffers()
                Logger.warn("****  Gargabe Collection ****")
                System.gc()
            } catch (e: Exception) {
                Logger.error("Error during resource cleaning: %s" + e.message)
                e.printStackTrace()
            }

            // creiamo la scena
            Logger.info("####Create scene")
            application!!.onSceneCreate(firstSceneCreation, somePreferenceIsChanged, screenIsChanged)
        } else {
            Logger.info("####Same scene, contextPreservedOnPause $contextPreservedOnPause")
            if (!contextPreservedOnPause) {
                // qua se ci sono errori non posso far altro che sbottare
                TextureManager.instance().reloadTextures()
                ShaderManager.instance().reloadShaders()
                BufferManager.instance().reloadVertexBuffers()
                Logger.info(" > Reload all resources")
            } else {
                Logger.info(" > No reload needed")
            }

            // carichiamo le varie risorse
            application!!.onSceneRestore(firstSceneCreation, somePreferenceIsChanged, screenIsChanged)
        }
        Logger.info("#### ON SCENE READY")
        application!!.onSceneReady(somePreferenceIsChanged, somePreferenceIsChanged, screenIsChanged)
        firstSceneCreation = false
        somePreferenceIsChanged = false
        screenIsChanged = false
        isPreviewStatusChanged = false

        // windowSurfaceCreated=false;
        Logger.info("#### FINE RIGENERO SCENA")

        // azzeriamo il flag, sempre e comunque
        //ElioPreferenceActivityManager.getInstance().resetPreferenceChanged();

        // ora la scena è pronta
        isSceneReady = true
    }

    /**
     * Invocato quando la surfaceView cambia e dobbiamo quindi ridisegnare
     *
     * @param width
     * @param height
     */
    @Synchronized
    fun onSurfaceChanged(width: Int, height: Int) {
        // in questo modo, se sono stati impostati a true, finchè non passiamo per la generazione
        // della scena non verranno messi a false a causa di duplice invocazione di questo metodo.
        screenIsChanged = screenIsChanged || XenonGL.screenInfo.width != width || XenonGL.screenInfo.height != height
        //somePreferenceIsChanged = somePreferenceIsChanged || ElioPreferenceActivityManager.getInstance().isPreferenceChanged();

        // aggiorniamo dimensioni schermo
        XenonGL.updateScreen(width, height)
        Logger.info("###### Xenon4OpenGL onSurfaceChanged Counter ")
        // imposta la camera di default ed imposta screenInfo!
        application.setDefaultCamera(CameraManager.onSurfaceChanged(width, height))
    }

    /**
     * routine per la fine del frame draw
     */
    fun onDrawFrameEnd() {
        FPSLimiter.onDrawFrameEnd()
        FPSCounter.onDrawFrameEnd(null)
    }

    /**
     * routine per l'inizio del frame draw
     */
    fun onDrawFrameBegin() {
        now = SystemClock.elapsedRealtime()
        FPSCounter.onDrawFrameBegin(now)
        FPSLimiter.onDrawFrameBegin(now)
    }

    /**
     * Se true indica il contesto viene preservato durante il pause resume. Di default è false.
     */
    var contextPreservedOnPause = true

    /**
     * Indica se stiamo passando da una preview alla visualizzazione vera e propria o viceversa.
     */
    @set:Synchronized
    var isPreviewStatusChanged = false

    // public boolean windowSurfaceCreated;
    /**
     * eseguito quando viene creata la surface
     */
    fun onSurfaceCreated() {
        Logger.info("###### Xenon4OpenGL onSurfaceCreated")
        if (firstSceneCreation) {
            XenonGL.checkGLVersion()
            Logger.info("@@@ OpenGL version %s", XenonGL.version)
        }
        application!!.onWindowCreate()

        /*
         * application.setDefaultCamera(CameraManager.instance().onSurfaceChanged(1024, 1024));
		 * 
		 * // creazione scena TextureManager.instance().clearTextures(context); ShaderManager.instance().clearShaders(); BufferManager.instance().clearVertexBuffers();
		 * 
		 * // creiamo la scena Logger.info("####Create scene"); application.onSceneCreate(preferences, firstSceneCreation, false, false); application.onSceneReady(preferences, false, false, false); Logger.info("> GL Finish");
		 * GLES20.glFinish();
		 */
        // viewStatus = ViewStatusType.SURFACE_ON_PAUSE;
    }

    @Synchronized
    fun onDestroy() {
        application.onDestroy(null)
    }

    /**
     * Creaiamo la view.
     *
     * @param argonView
     * @throws Exception
     */
    @Throws(Exception::class)
    fun onViewCreated(argonView: XenonView4OpenGL) {
        try {
            // abilitiamo se richiesto il debug opengl
            if (settings.openGL.debug) {
                argonView.debugFlags = GLSurfaceView.DEBUG_CHECK_GL_ERROR or GLSurfaceView.DEBUG_LOG_GL_CALLS
            }
            renderer = argonView.createRenderer()
            renderer.setSafeMode(settings.openGL.safeMode)
            argonView.setEGLContextClientVersion(2)
            argonView.holder.setFormat(XenonGLConfigChooser.build().pixelFormat)
            argonView.setRenderer(renderer)
            argonView.gestureDetector = XenonGestureDetector(this, gestureListener!!)
        } catch (e: Exception) {
            // e' un errore che dobbiamo cmq visualizzare. Poi cmq sollevviamo
            // l'eccezione.
            Logger.fatal(e.message)
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Eseguiamo startup dell'applicazione, applichiamo i settings, costruiamo lo screen info e poi invochiamo il metodo parent.
     */
    override fun init(contextValue: Context, settingsValue: XenonSettings) {
        super.init(contextValue, settingsValue)
        applySettings()
        ScreenInfo.build(context, XenonGL.screenInfo)
        XenonGLHelper.onStartup()
        Logger.info(
            "Screen resolution %s x %s - Density %s - Resolution %s",
            XenonGL.screenInfo.width,
            XenonGL.screenInfo.height,
            XenonGL.screenInfo.densityClass,
            XenonGL.screenInfo.resolution
        )
    }

    /**
     * per ogni contesto andrebbe a cancellare qualcosa che deve rimanere valido per gli altri contesti.
     */
    @Deprecated("")
    fun onOGLContextDestroy() {
        Logger.info("> Xenon4OpenGL onOGLContextDestroy")

        /*
		 * Logger.info("> BEGIN Unbind textures, shaders, vbos");
		 * 
		 * Logger.info("> END Unbind textures, shaders, vbos");
		 */
        /*
		 * TextureManager.instance().clearTextures(); ShaderManager.instance().clearShaders(); BufferManager.instance().clearBuffers();
		 */
    }

    @Synchronized
    fun onFrameDraw(phase: Phase?, enlapsedTime: Long, speedAdapter: Float) {
        application!!.onFrameDraw(phase, enlapsedTime, speedAdapter)
    }

    @Synchronized
    fun onFramePrepare(phase: Phase?, enlapsedTime: Long, speedAdapter: Float) {
        application!!.onFramePrepare(phase, enlapsedTime, speedAdapter)
    }

    /**
     * @param activity
     */
    @Synchronized
    fun onPause(activity: XenonActivity4OpenGL?) {
        // viewStatus = ViewStatusType.SURFACE_ON_PAUSE;
        isSceneReady = false
        application!!.onPause(activity)
    }

    /**
     * @param activity
     */
    @Synchronized
    fun onResume(activity: XenonActivity4OpenGL?) {
        application!!.onResume(activity)
    }

    companion object {
        private const val serialVersionUID = 4176307344192146991L
    }
}