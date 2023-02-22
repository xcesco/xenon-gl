package com.abubusoft.xenon

import android.app.Activity
import com.abubusoft.xenon.android.surfaceview.ConfigOptions
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.engine.Phase

/**
 * Interfaccia per la gestione del wallpaper mediante application
 *
 * @author Francesco Benincasa
 */
interface XenonApplication4OpenGL : XenonApplication, UpdateTaskListener {
    /**
     * invocato quando creo una finestra
     */
    fun onWindowCreate()

    /**
     * Restituisce la configurazione da usare per la creazione delle view opengl
     * @return
     * configurazione da usare le view opengl
     */
    fun chooseArgonGLConfig(): ConfigOptions

    /**
     * evento scatenato quando viene messo in pausa il servizio o l'activity
     */
    fun onPause(currentActivity: Activity)

    /**
     * evento scatenato quando viene riavviata l'applicazione o l'activity
     */
    fun onResume(currentActivity: Activity)

    /**
     * Crea la scena in base alle preference e ad argonContext.
     *
     * Se viene invocato questo metodo, non viene invocato il metodo onSceneRestore.
     *
     * @param argon
     * @param sharedPreference
     * @param firstSceneCreation
     * @param preferencesIsChanged
     *              se true indica che qualche preferenza è cambiata
     * @param screenIsChanged
     *              se true indica che lo schermo è cambiato (risoluzione o scope)
     */
    fun onSceneCreate(firstSceneCreation: Boolean, preferencesIsChanged: Boolean, screenIsChanged: Boolean)

    /**
     * La scena non è cambiata, ma bisogna ricaricare lo schermo. Le texture e gli shader sono stati già ricaricati. Carica le risorse associate alla scena.
     *
     * @param argon
     * @param sharedPreference
     * @param firstSceneCreation
     * @param preferencesIsChanged
     * @param screenIsChanged
     */
    fun onSceneRestore(firstSceneCreation: Boolean, preferencesIsChanged: Boolean, screenIsChanged: Boolean)

    /**
     * Dopo aver creato la scena [.onSceneCreated] o averla recuperata [.onSceneResume], viene invocato sempre il metodo onSceneReady. Questo consente di mettere a
     * fattor comune il codice che serve in entrambe le situazioni, come ad esempio le impostazioni relative al contesto opengl.
     *
     * @param firstSceneCreation
     * @param preferencesIsChanged
     * @param screenIsChanged
     */
    fun onSceneReady(firstSceneCreation: Boolean, preferencesIsChanged: Boolean, screenIsChanged: Boolean)

    /**
     * Disegna il frame.
     *
     * @param phase
     *      phase in cui viene eseguito il task
     * @param enlapsedTime
     *         tempo trascorso tra un frame ed un altro
     * @param speedAdapter
     *          data una velocità espressa in termini di m/s,
     *          questo parametro consente di adattare gli spostamenti in base al tempo passato.
     */
    fun onFrameDraw(phase: Phase, enlapsedTime: Long, speedAdapter: Float)

    /**
     *
     *
     * Imposta la camera di default, quella che ci deve essere per forza.
     *
     *
     * @param onSurfaceChanged
     */
    fun setDefaultCamera(onSurfaceChanged: Camera)
}