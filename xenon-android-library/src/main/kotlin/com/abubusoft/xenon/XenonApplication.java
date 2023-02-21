package com.abubusoft.xenon;

import android.app.Activity;
import android.content.Context;

@SuppressWarnings("rawtypes")
public interface XenonApplication<E extends Xenon> {
	/**
	 * Context dell'applicazione
	 * 
	 * @return
	 * 		context dell'applicazione
	 */
	public Context context();

	/**
	 * Viene eseguito solo la prima volta che lancio l'applicazione
	 */
	public void onAfterStartupFirstTime();

	/**
	 * Viene eseguita la prima volta che eseguo questa vestione
	 * dell'applicazione
	 */
	public void onAfterStartupFirstTimeForThisVersion();

	/**
	 * Evento che viene scatenato nella fase di avvio, nel caso in cui la
	 * configurazione venga creata o resettata.
	 */
	public void onConfigReset();

	/**
	 * Quando l'applicazione chiude
	 */
	public void onDestroy(Activity activity);

	/**
	 * startup dell'applicazione. In questo metodo abbiamo già definito le varie
	 * configurazioni ma non abbiamo ancora creato la view e quindi non abbiamo
	 * ancora il contesto opengl definito
	 */
	public void onStartup();

	/**
	 * Imposta argon
	 * 
	 * @param argonValue
	 */
	public void setArgon(E argonValue);
}
