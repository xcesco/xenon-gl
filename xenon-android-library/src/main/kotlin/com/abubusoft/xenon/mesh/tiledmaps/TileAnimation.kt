package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.ArrayList;

/**
 * Rappresenta una sequenza di layer.
 * 
 * @author Francesco Benincasa
 * 
 */
public class TileAnimation {

	public int currentFrameIndex;

	/**
	 * tempo trascorso dall'inizio dell'animazione
	 * 
	 */
	public long enlapsedTime;

	/**
	 * sequenza di frame
	 */
	public final ArrayList<TileAnimationFrame> frames;

	public boolean loop = true;
	/**
	 * nome dell'animazione
	 */
	public final String name;

	/**
	 * se true indica che l'animazione è stata avviata
	 */
	public boolean started = false;

	public TileAnimation(String nameValue) {
		name = nameValue;
		frames = new ArrayList<TileAnimationFrame>();
	}

	/**
	 * Inidica se l'animator ha lanciato l'animazione corrente o no. Se è in
	 * loop infinito ed è stata lanciata, ovviamente non finirà mai.
	 * 
	 * @return
	 */
	public boolean isFinished() {
		// se non è in loop e non è iniziato, ovviamente è finito (o non ancora
		// iniziato)
		// se è in loop, non finirà mai, ma è iniziato stopped=true
		return !started;
	}

	/**
	 * Verifica se il layer passato come argomento deve essere disegnato o meno
	 * in base allo stato dell'animazione
	 * 
	 * @param layer
	 * @return
	 */
	public boolean isLayerToDraw(Layer layer) {
		return frames.get(currentFrameIndex).layer.equals(layer);
	}

	/**
	 * Avvia l'animazione e ci posizioniamo sul frame corretto.
	 * 
	 * @param status
	 * @param enlapsedTimeValue
	 */
	public void start(long enlapsedTimeValue) {
		started = true;
		enlapsedTime = enlapsedTimeValue;
		currentFrameIndex = 0;

		// ci posizioniamo sul frame corretto
		while (enlapsedTime > frames.get(currentFrameIndex).duration) {
			enlapsedTime -= frames.get(currentFrameIndex).duration;
			currentFrameIndex++;

			if (currentFrameIndex >= frames.size()) {
				if (!loop) {
					started = false;
				}
				currentFrameIndex = 0;
			}
		}
	}

	public void update(long enlapsedTimeValue) {
		if (!started)
			return;

		enlapsedTime += enlapsedTimeValue;

		while (enlapsedTime > frames.get(currentFrameIndex).duration) {
			enlapsedTime -= frames.get(currentFrameIndex).duration;
			currentFrameIndex++;

			if (currentFrameIndex >= frames.size()) {
				if (!loop) {
					started = false;
				}
				currentFrameIndex = 0;
			}
		}
	}
}
