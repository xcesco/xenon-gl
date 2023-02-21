package com.abubusoft.xenon;

import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.engine.Phase;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Interfaccia per la gestione del wallpaper mediante application
 * 
 * @author Francesco Benincasa
 * 
 */
public interface XenonApplication4OpenGL extends XenonApplication<Xenon4OpenGL>, UpdateTaskListener {
	
	/**
	 * invocato quando creo una finestra 
	 */
	public void onWindowCreate();
	
	/**
	 * Restituisce la configurazione da usare per la creazione delle view opengl
	 * @return
	 * 		configurazione da usare le view opengl
	 */
	public ConfigOptions chooseArgonGLConfig();
	
	
	/*
	 * on scene created: creato quando creo la scena. on scene resume: quando recupero la scena dopo una pausa. Non viene invocato quando la scena viene creata (dato che non viene
	 * recuperata). on scene ready: dopo created e resume, viene invocato sempre questo metodo. on scene pause: quando metto in pausa l'activity che gestisce la scena on scene
	 * draw: quando disegno la scena
	 * 
	 * onSceneCreated onSceneResume onSceneReady onScenePause onSceneDraw
	 */

	/**
	 * evento scatenato quando viene messo in pausa il servizio o l'activity
	 */
	public void onPause(Activity currentActivity);

	/**
	 * evento scatenato quando viene riavviata l'applicazione o l'activity
	 */
	public void onResume(Activity currentActivity);

	/**
	 * <p>
	 * Crea la scena in base alle preference e ad argonContext.
	 * </p>
	 * 
	 * <p>
	 * Se viene invocato questo metodo, non viene invocato il metodo onSceneRestore.
	 * </p>
	 * 
	 * @param argon
	 * @param sharedPreference
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 *            se true indica che qualche preferenza è cambiata
	 * @param screenIsChanged
	 *            se true indica che lo schermo è cambiato (risoluzione o scope)
	 */
	void onSceneCreate(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged);

	/**
	 * <p>
	 * La scena non è cambiata, ma bisogna ricaricare lo schermo. Le texture e gli shader sono stati già ricaricati. Carica le risorse associate alla scena.
	 * </p>
	 * 
	 * @param argon
	 * @param sharedPreference
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	void onSceneRestore(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged);

	/**
	 * <p>
	 * Dopo aver creato la scena {@link #onSceneCreated} o averla recuperata {@link #onSceneResume}, viene invocato sempre il metodo onSceneReady. Questo consente di mettere a
	 * fattor comune il codice che serve in entrambe le situazioni, come ad esempio le impostazioni relative al contesto opengl.
	 * </p>
	 *
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	void onSceneReady(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged);

	/**
	 * <p>
	 * Disegna il frame.
	 * </p>
	 * +
	 * 
	 * @param phase
	 *            phase in cui viene eseguito il task
	 * @param enlapsedTime
	 *            tempo trascorso tra un frame ed un altro
	 * @param speedAdapter
	 *            data una velocità espressa in termini di m/s, questo parametro consente di adattare gli spostamenti in base al tempo passato.
	 */
	void onFrameDraw(Phase phase, long enlapsedTime, float speedAdapter);

	/**
	 * <p>
	 * Prepara il frame in termini di spostamenti, collision detection etc etc.
	 * </p>
	 * 
	 * @param enlapsedTime
	 *            tempo trascorso dal frame precendente
	 * @param speedAdapter
	 *            dato una velocità espressa pixel/secondo, questo fattore di moltiplicazione deve essere moltiplicato per la velocità per avere la relativa velocità per il disegno
	 *            per quel frame.
	 */
	// void onFramePrepare(long enlapsedTime, float speedAdapter);

	/**
	 * <p>
	 * Imposta la camera di default, quella che ci deve essere per forza.
	 * </p>
	 * 
	 * @param onSurfaceChanged
	 */
	public void setDefaultCamera(Camera onSurfaceChanged);
}
