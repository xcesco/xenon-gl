package com.abubusoft.xenon.misc;

import com.abubusoft.xenon.opengl.OnFPSUpdateListener;
import com.abubusoft.kripton.android.Logger;

import android.os.SystemClock;

public class FPSCounter {
	/**
	 * tempo trascorso tra l'inizio del frame precedente e l'inizio del frame corrente.
	 */
	public static long enlapsedTime = 0;

	private static long lastFrameTime = SystemClock.elapsedRealtime();

	/**
	 * numero di frame da contare prima di visualizzare le statistiche di rendering
	 */
	static final int FPS_LIMIT_TO_DISPLAY_INFO = 1000;

	private static long initialTimeForFpsAvg;
	
	static int averageFpsCount = 0;
	
	/**
	 * media del tempo impiegato per renderizzare un frame
	 */
	public static float averageFpsTime;
	
	/**
	 * media dei FPS
	 */
	public static float averageFps;

	public static void onDrawFrameBegin(long now) {
		// calcoliamo il deltaTime, il tempo trascorso dall'ultimo frame
		enlapsedTime = now - lastFrameTime;
		
		// se è minore di 0, allora facciamo che non ci muoviamo
		if (enlapsedTime<0) enlapsedTime=0;
		// ultima volta che è stato calcolato il frame: ora.
		lastFrameTime = now;

		if (averageFpsCount == 0) {
			// lo calcoliamo solo una volta ogni tanto, all'inizio del conteggio
			// dei frame
			initialTimeForFpsAvg = now;
		}
	}
	
	/**
	 * Indica se è il tempo di visualizzare i fps medi
	 * 
	 */
	public static boolean isTimeToShowInfo()
	{
		return averageFpsCount >= FPS_LIMIT_TO_DISPLAY_INFO;
	}

	public static void onDrawFrameEnd(OnFPSUpdateListener listener) {
		// se sono passati maxCount frame facciamo la media
		if (averageFpsCount >= FPS_LIMIT_TO_DISPLAY_INFO) {

			// tempo trascorso dal momento prima di disegnare il primo frame ad ora
			long delta = SystemClock.elapsedRealtime() - initialTimeForFpsAvg;

			// calcoliamo media per disegnare un singolo frame
			averageFpsTime = ((float) (delta)) / averageFpsCount;
			
			// invertiamo averageTime per ottenere lo stesso valore in FPS
			averageFps = (float) (1000.0 / averageFpsTime);

			Logger.debug("Avg time for render a frame = %s ms. FPS = %s", averageFpsTime, averageFps);
			
			if (listener != null)
				listener.onFPSUpdate(averageFps);

			averageFpsCount = 0;
		} else {
			averageFpsCount++;
		}
	}
}
