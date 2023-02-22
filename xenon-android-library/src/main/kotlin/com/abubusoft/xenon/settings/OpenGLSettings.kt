package com.abubusoft.xenon.settings;

import com.abubusoft.xenon.core.Uncryptable;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;

/**
 * Configurazione dell'opengl.
 * 
 * @author Francesco Benincasa
 *
 */
@Uncryptable
@BindType
public class OpenGLSettings {

	/**
	 * versione di opengl da usare
	 */
	@Bind("openGLVersion")
	public int version = 2;

	/**
	 * livello di debug
	 */
	@Bind("openGLDebug")
	public boolean debug = false;

	/**
	 * FPS massimi. Se impostato a 0 vuol dire che non ci sono limiti
	 */
	@Bind("openGLMaxFPS")
	public int maxFPS = 0;

	/**
	 * indica se gestire il draw frame dentro un enorme try catch, in modo da
	 * non far esplodere l'applicazione in caso di eccezione.
	 */
	@Bind("openGLSafeMode")
	public boolean safeMode = true;
	
	/**
	 * <p>Indica il sistema che deve prevedere il caricamento async delle texture e degli shader.</p>
	 */
	@Bind("openGLAsyncMode")
	public boolean asyncMode = true;
	
}
