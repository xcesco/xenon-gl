package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.misc.NormalizedTimer;
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer;
import com.abubusoft.xenon.texture.DynamicTexture.DynamicTextureController;
import com.abubusoft.kripton.android.Logger;

/**
 * <p>
 * Questa implementazione del controller consente di controllare il
 * caricamento delle texture mediante un click di un pulsante o programmaticamente.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class DynamicTextureForcedController implements DynamicTextureController {

	/**
	 * flag usato per indicare il fatto che il load della texture è pronto.
	 */
	boolean readyToLoad;
	
	boolean forced;

	/**
	 * <p>Costruttore</p>
	 */ 
	public DynamicTextureForcedController() {
		readyToLoad=true;
		forced=false;
	}

	@Override
	public boolean onCheckForUpdate(long enlapsedTime) {
		// aggiorniamo il touchTimer
		if (forced && readyToLoad) {
			Logger.info("Ready for load another texture");
			readyToLoad = false;
			forced=false;
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
	}

	@Override
	public boolean forceUpdate() {
		if (readyToLoad)
		{
			Logger.info("Just ask to load another texture");
			forced=true;
			
			return true;
		} else {
			Logger.info("It's still loading another texture");
			
			return false;
		}
	}

}