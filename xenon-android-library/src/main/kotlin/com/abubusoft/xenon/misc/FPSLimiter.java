package com.abubusoft.xenon.misc;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.android.XenonLogger;

import android.os.SystemClock;

/**
 * Limitatore di FPS. Impostato un limite superiore ai FPS questa classe
 * provvede a non far andar oltre i frame al secondo.
 * 
 * Unico parametro definibile in Options. è {@link #maxFrameRate} che definisce
 * il numero di fps max.
 * 
 * Se impostato a 0 indica che non vi è limite.
 * 
 * @author Francesco Benincasa
 * 
 */
public class FPSLimiter {

	/**
	 * massimo framerate accettato
	 */
	public static int maxFrameRate;

	/**
	 * begin frame draw time
	 */
	private static long beginFrameDrawTime;

	/**
	 * end frame draw time
	 */
	private static long endFrameDrawTime;

	/**
	 * tempo per renderizzare questo frame
	 */
	private static long currentFrameRenderTime;

	/**
	 * routine per l'avvio del frame draw
	 * 
	 * @param now
	 */
	public static void onDrawFrameBegin(long now) {
		if (maxFrameRate > 0) {
			// lo calcoliamo sempre, se abilitato
			beginFrameDrawTime = now;
		}
	}

	/**
	 * variabile per misurazione tempo di attesa
	 */
	private static long startSleepTime;

	/**
	 * variabile per misurazione tempo di attesa
	 */
	private static long endSleepTime;

	/**
	 * tempo di wait del thread dovuto al fatto che rispetto al framerate
	 * desiderato, si è avuto un tempo di render minore.
	 */
	private static long currentFrameWaitTime;

	/**
	 * routine per il limitatore di FPS in fase di chiusura del frame draw
	 */
	public static void onDrawFrameEnd() {
		if (maxFrameRate > 0) {
			endFrameDrawTime = SystemClock.elapsedRealtime();

			// tempo per render al frame rate desiderato - tempo impiegato per
			// disegnare questo frame
			currentFrameRenderTime = (endFrameDrawTime - beginFrameDrawTime);

			// se per qualche motivo delta < lo rendiamo comunque positivo
			if (currentFrameRenderTime <= 0)
				currentFrameRenderTime = 1;

			currentFrameWaitTime = (long) ((1000.0f / maxFrameRate) - currentFrameRenderTime);

			// togliamo 10ms dall'attesa, considerando che lo sleep non è
			// precissimo
			currentFrameWaitTime = (long) (currentFrameWaitTime * 0.80f);

			if (currentFrameWaitTime > 0) {
				try {
					// per evitare che venga visualizzato ad ogni frame, viene
					// visualizzato una volta ogni
					// tanto
					if (FPSCounter.isTimeToShowInfo())
					{
						//if (Logger.isEnabledFor(LoggerLevelType.VERBOSE)) {
							Logger.verbose("Time enlapsed %s ms, Time to respect %s sleep for %s", currentFrameRenderTime, (long) ((1000.0 / maxFrameRate)), currentFrameWaitTime);
						//}
						startSleepTime = SystemClock.elapsedRealtime();
					}
					Thread.sleep(currentFrameWaitTime);
					// per evitare che venga visualizzato ad ogni frame, viene
					// visualizzato una volta ogni
					// tanto

					if (FPSCounter.isTimeToShowInfo()) {
						endSleepTime = SystemClock.elapsedRealtime();

						XenonLogger.verbose("Wait time - Desidered: %s ms , Real: %s ms", currentFrameWaitTime, (endSleepTime - startSleepTime));

					}
				} catch (Exception e) {
					Logger.error("Errore: " + e.getMessage());
				}
			}
		}
	}
}
