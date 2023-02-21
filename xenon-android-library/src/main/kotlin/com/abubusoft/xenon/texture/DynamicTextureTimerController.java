package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.misc.NormalizedTimer;
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer;
import com.abubusoft.xenon.texture.DynamicTexture.DynamicTextureController;
import com.abubusoft.kripton.android.Logger;

/**
 * <p>
 * Questa implementazione del controller consente di controllare il
 * caricamento delle texture mediante un touchTimer.
 * </p>
 * 
 * <p>
 * Questo meccanismo è quello che penso sia quello più standard, per questo
 * motivo è stato implemetato.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class DynamicTextureTimerController implements DynamicTextureController {

	/**
	 * touchTimer usato per monitorare il tempo.
	 */
	NormalizedTimer timer;
	
	/**
	 * flag usato per indicare il fatto che il load della texture è pronto.
	 */
	boolean readyToLoad;

	/**
	 * <p>Costruttore</p>
	 * 
	 * @param durationInMills
	 */
	public DynamicTextureTimerController(long durationInMills) {
		timer = new NormalizedTimer(TypeNormalizedTimer.ONE_TIME, durationInMills);
		readyToLoad=true;
		timer.start();
	}

	@Override
	public boolean onCheckForUpdate(long enlapsedTime) {
		// aggiorniamo il touchTimer
		timer.update(enlapsedTime);
		if (timer.getNormalizedEnlapsedTime() == 1f && readyToLoad) {
			Logger.info("Ready for load another texture");
			readyToLoad = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onTextureReady(Texture texture) {
		// eseguito quando viene la texture viene caricata.
		Logger.info("Texture loaded");
		readyToLoad = true;
		timer.start();
	}

	@Override
	public boolean forceUpdate() {
		// non possiamo forzare
		return false;
	}

}