package com.abubusoft.xenon;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.android.XenonActivity4OpenGL;
import com.abubusoft.xenon.android.XenonView4OpenGL;
import com.abubusoft.xenon.android.XenonWallpaper;
import com.abubusoft.xenon.android.listener.XenonGestureDetector;
import com.abubusoft.xenon.android.listener.XenonGestureListener;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.xenon.camera.CameraManager;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.misc.FPSCounter;
import com.abubusoft.xenon.misc.FPSLimiter;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.opengl.XenonGLConfigChooser;
import com.abubusoft.xenon.opengl.XenonGLHelper;
import com.abubusoft.xenon.opengl.XenonGLRenderer;
import com.abubusoft.xenon.settings.XenonSettings;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.texture.TextureManager;
import com.abubusoft.xenon.vbo.BufferManager;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;

public class Xenon4OpenGL extends Xenon4BaseImpl<XenonApplication4OpenGL> implements Xenon {

    private static final long serialVersionUID = 4176307344192146991L;

    /**
     * indica se è la prima volta che si sta creando la scena
     */
    protected boolean firstSceneCreation;

    protected boolean sceneReady;

    /**
     * Indica che la scena è pronta per essere renderizzata. Serve in particolar
     * modo per verificare se gestione o meno del touch
     *
     * @return true se la scena è pronta per essere disegnata.
     */
    public boolean isSceneReady() {
        return sceneReady;
    }

    /**
     *
     */
    public void reset() {
        firstSceneCreation = true;
    }

    /**
     * listener delle gesture
     */
    public XenonGestureListener gestureListener;

    /**
     * costruttore
     */
    public Xenon4OpenGL() {
        super();

        sceneReady = false;
    }

    /**
     * effettua la configurazione di xenon
     */
    public void applySettings() {
        // carica la configurazione

        // impostiamo la camera di default.
        CameraManager.instance().init(settings.viewFrustum);

        // opengl
        XenonGL.startup(settings.openGL.version, settings.openGL.debug);
        FPSLimiter.maxFrameRate = settings.openGL.maxFPS;

        // gestore delle gesture
        try {
            gestureListener = (XenonGestureListener) XenonBeanContext.createInstance(Class.forName(settings.application.gestureListenerClazz.trim()));
        } catch (Exception e) {
            Logger.fatal(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Renderer utilizzato per il rendering sia da wallpaper che da activity
     */
    public XenonGLRenderer renderer;

    /**
     * indica il tempo attuale in mills. Utilizzato in {@link #onDrawFrameBegin()}
     */
    private long now;

    /**
     * <p>
     * Activity corrente nella quale è presente la openglView {@link XenonView4OpenGL}
     * </p>
     */
    public XenonActivity4OpenGL activity;

    private boolean screenIsChanged;

    private boolean somePreferenceIsChanged;

    /**
     * Da eseguire durante la creazione dell'activity: imposta i flag per nascondere il titolo della finestra, crea il renderer e lo associa all'activity.
     *
     * @param currentActivity
     * @throws Exception
     */
    public void onActivityCreated(XenonActivity4OpenGL currentActivity) throws Exception {

        try {
            this.activity = currentActivity;
            reset();

            // finestra senza titolo
            currentActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            currentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // surface per il rendering
            //GLSurfaceViewEGL14  view = new GLSurfaceViewEGL14(currentActivity);
            ArgonGLView view = XenonGL.createArgonGLView(currentActivity);

            // abilitiamo se richiesto il debug opengl
            if (settings.openGL.debug) {
                view.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
            }

            // impostiamo il safeMode
            renderer = currentActivity.createRenderer();
            renderer.setSafeMode(settings.openGL.safeMode);

            view.setEGLContextClientVersion(2);
            view.setPreserveEGLContextOnPause(true);

            // impostiamo il formato a 32 bit
            //view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            //view.getHolder().setFormat(PixelFormat.RGBA_8888);
            view.getHolder().setFormat(XenonGLConfigChooser.build().getPixelFormat());
            view.setRenderer(renderer);

            // inizializza
            //renderer.initialize(view);

            // impostiamo la glSurfaceView per l'activity
            currentActivity.setArgonGLSurfaceView(view);

            // gestore delle gesture, lo impostiamo sull'activity o sul service
            currentActivity.gestureDetector = new XenonGestureDetector(this, gestureListener);
        } catch (Exception e) {
            // e' un errore che dobbiamo cmq visualizzare. Poi cmq sollevviamo
            // l'eccezione.
            Logger.fatal(e.getMessage());
            e.printStackTrace();
            throw (e);
        }
    }

    /**
     * Creazione del servizio ed impostazione del renderer
     *
     * @param service
     */
    public synchronized void onServiceCreated(XenonWallpaper service) {

        try {
            reset();
            // renderer = (ArgonRenderer) Class.forName(settings.openGL.rendererClazz).newInstance();
            // renderer=new ArgonRendererV2();
            renderer = service.createRenderer();
            renderer.setSafeMode(settings.openGL.safeMode);

            service.setRenderer(renderer);
            // gestore delle gesture, lo impostiamo sull'activity o sul service
            service.setGestureDetector(new XenonGestureDetector(this, gestureListener));
            // impostiamo il formato a 32 bit

        } catch (Exception e) {
            Logger.fatal(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }

    public synchronized boolean isSceneToCreate() {
        return firstSceneCreation || somePreferenceIsChanged || screenIsChanged || previewStatusChanged;
    }

    public synchronized void onSceneCreation() {
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
        Logger.info("Scene changed firstSceneCreation: %s, somePreferenceIsChanged: %s, screenIsChanged: %s, previewStateIsChanged: %s", firstSceneCreation, somePreferenceIsChanged, screenIsChanged, previewStatusChanged);

        if (isSceneToCreate()) {
            try {
                TextureManager.instance().clearTextures();
                ShaderManager.instance().clearShaders();
                BufferManager.instance().clearBuffers();

                Logger.warn("****  Gargabe Collection ****");
                System.gc();
            } catch (Exception e) {
                Logger.error("Error during resource cleaning: %s" + e.getMessage());
                e.printStackTrace();
            }

            // creiamo la scena
            Logger.info("####Create scene");
            application.onSceneCreate(firstSceneCreation, somePreferenceIsChanged, screenIsChanged);
        } else {

            Logger.info("####Same scene, contextPreservedOnPause " + contextPreservedOnPause);
            if (!contextPreservedOnPause) {
                // qua se ci sono errori non posso far altro che sbottare
                TextureManager.instance().reloadTextures();
                ShaderManager.instance().reloadShaders();
                BufferManager.instance().reloadVertexBuffers();
                Logger.info(" > Reload all resources");
            } else {

                Logger.info(" > No reload needed");
            }

            // carichiamo le varie risorse
            application.onSceneRestore(firstSceneCreation, somePreferenceIsChanged, screenIsChanged);
        }
        Logger.info("#### ON SCENE READY");
        application.onSceneReady(somePreferenceIsChanged, somePreferenceIsChanged, screenIsChanged);

        firstSceneCreation = false;
        somePreferenceIsChanged = false;
        screenIsChanged = false;
        previewStatusChanged = false;

        // windowSurfaceCreated=false;
        Logger.info("#### FINE RIGENERO SCENA");

        // azzeriamo il flag, sempre e comunque
        //ElioPreferenceActivityManager.getInstance().resetPreferenceChanged();

        // ora la scena è pronta
        sceneReady = true;
    }

    /**
     * Invocato quando la surfaceView cambia e dobbiamo quindi ridisegnare
     *
     * @param width
     * @param height
     */
    public synchronized void onSurfaceChanged(int width, int height) {
        // in questo modo, se sono stati impostati a true, finchè non passiamo per la generazione
        // della scena non verranno messi a false a causa di duplice invocazione di questo metodo.
        screenIsChanged = screenIsChanged || (XenonGL.screenInfo.width != width) || (XenonGL.screenInfo.height != height);
        //somePreferenceIsChanged = somePreferenceIsChanged || ElioPreferenceActivityManager.getInstance().isPreferenceChanged();

        // aggiorniamo dimensioni schermo
        XenonGL.updateScreen(width, height);

        Logger.info("###### Xenon4OpenGL onSurfaceChanged Counter ");
        // imposta la camera di default ed imposta screenInfo!
        application.setDefaultCamera(CameraManager.instance().onSurfaceChanged(width, height));
    }

    /**
     * routine per la fine del frame draw
     */
    public void onDrawFrameEnd() {
        FPSLimiter.onDrawFrameEnd();
        FPSCounter.onDrawFrameEnd(null);
    }

    /**
     * routine per l'inizio del frame draw
     */
    public void onDrawFrameBegin() {
        now = SystemClock.elapsedRealtime();

        FPSCounter.onDrawFrameBegin(now);
        FPSLimiter.onDrawFrameBegin(now);

    }

    /**
     * Se true indica il contesto viene preservato durante il pause resume. Di default è false.
     */
    public boolean contextPreservedOnPause = true;

    /**
     * Indica se stiamo passando da una preview alla visualizzazione vera e propria o viceversa.
     */
    private boolean previewStatusChanged;

    public boolean isPreviewStatusChanged() {
        return previewStatusChanged;
    }

    // public boolean windowSurfaceCreated;

    /**
     * eseguito quando viene creata la surface
     */
    public void onSurfaceCreated() {
        Logger.info("###### Xenon4OpenGL onSurfaceCreated");

        if (firstSceneCreation) {
            XenonGL.checkGLVersion();
            Logger.info("@@@ OpenGL version %s", XenonGL.getVersion());
        }

        application.onWindowCreate();

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

    public synchronized void onDestroy() {
        application.onDestroy(null);
    }

    /**
     * Creaiamo la view.
     *
     * @param argonView
     * @throws Exception
     */
    public void onViewCreated(XenonView4OpenGL argonView) throws Exception {

        try {
            // abilitiamo se richiesto il debug opengl
            if (settings.openGL.debug) {
                argonView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
            }

            renderer = argonView.createRenderer();
            renderer.setSafeMode(settings.openGL.safeMode);

            argonView.setEGLContextClientVersion(2);

            argonView.getHolder().setFormat(XenonGLConfigChooser.build().getPixelFormat());
            argonView.setRenderer(renderer);

            argonView.gestureDetector = new XenonGestureDetector(this, gestureListener);
        } catch (Exception e) {
            // e' un errore che dobbiamo cmq visualizzare. Poi cmq sollevviamo
            // l'eccezione.
            Logger.fatal(e.getMessage());
            e.printStackTrace();
            throw (e);
        }

    }

    /**
     * Eseguiamo startup dell'applicazione, applichiamo i settings, costruiamo lo screen info e poi invochiamo il metodo parent.
     */
    @Override
    public void init(Context contextValue, XenonSettings settingsValue) {
        super.init(contextValue, settingsValue);

        applySettings();

        ScreenInfo.build(context, XenonGL.screenInfo);
        XenonGLHelper.onStartup();

        Logger.info("Screen resolution %s x %s - Density %s - Resolution %s", XenonGL.screenInfo.width, XenonGL.screenInfo.height, XenonGL.screenInfo.densityClass, XenonGL.screenInfo.resolution);
    }

    /**
     * per ogni contesto andrebbe a cancellare qualcosa che deve rimanere valido per gli altri contesti.
     */
    @Deprecated
    public void onOGLContextDestroy() {
        Logger.info("> Xenon4OpenGL onOGLContextDestroy");

		/*
		 * Logger.info("> BEGIN Unbind textures, shaders, vbos");
		 * 
		 * Logger.info("> END Unbind textures, shaders, vbos");
		 */
		/*
		 * TextureManager.instance().clearTextures(); ShaderManager.instance().clearShaders(); BufferManager.instance().clearBuffers();
		 */
    }

    public synchronized void setPreviewStatusChanged(boolean value) {
        previewStatusChanged = value;
    }

    public synchronized void onFrameDraw(Phase phase, long enlapsedTime, float speedAdapter) {
        application.onFrameDraw(phase, enlapsedTime, speedAdapter);
    }

    public synchronized void onFramePrepare(Phase phase, long enlapsedTime, float speedAdapter) {
        application.onFramePrepare(phase, enlapsedTime, speedAdapter);
    }

    /**
     * @param activity
     */
    public synchronized void onPause(XenonActivity4OpenGL activity) {
        // viewStatus = ViewStatusType.SURFACE_ON_PAUSE;
        sceneReady = false;
        application.onPause(activity);
    }

    /**
     * @param activity
     */
    public synchronized void onResume(XenonActivity4OpenGL activity) {
        application.onResume(activity);
    }

}
