/**
 * 
 */
package com.abubusoft.xenon.misc;

/**
 * Rappresenta un touchTimer normalizzato. Viene aggiornato in millisecondi.
 * 
 * Il range va da 0 a 1. Il max time viene espresso in millisecondi.
 * 
 * @author Francesco Benincasa
 * 
 */
public class NormalizedTimer {

	/**
	 * Tipi di touchTimer.
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum TypeNormalizedTimer {
		/**
		 * viene eseguito una volta sola.
		 */
		ONE_TIME,

		/**
		 * viene eseguito all'infinito. Quando arriva alla fine, ricomincia
		 */
		REPEAT_FOREVER;
	}

	/**
	 * <p>
	 * Fattore moltiplicativo: moltiplica i secondi in millisecondi.
	 * </p>
	 */
	public final static long SECOND_IN_MILLISECONDS = 1000;

	/**
	 * <p>
	 * Fattore moltiplicativo: moltiplica i minuti in millisecondi.
	 * </p>
	 */
	public final static long MINUTE_IN_MILLISECONDS = SECOND_IN_MILLISECONDS * 60;

	/**
	 * <p>
	 * Fattore moltiplicativo: moltiplica le ore in millisecondi.
	 * </p>
	 */
	public final static long HOUR_IN_MILLISECONDS = MINUTE_IN_MILLISECONDS * 60;

	/**
	 * tempo corrente in millisecondi
	 */
	public long enlapsedTime;

	/**
	 * tempo massimo
	 */
	public long durationTime;

	/**
	 * tipo di touchTimer
	 */
	public TypeNormalizedTimer type;

	/**
	 * <p>
	 * Costruttore.
	 * </p>
	 * 
	 * @param value
	 *            tipo di touchTimer
	 */
	public NormalizedTimer(TypeNormalizedTimer value) {
		reset();
		type = value;
	}

	/**
	 * <p>
	 * Costruttore.
	 * </p>
	 * 
	 * @param value
	 *            tipo di touchTimer
	 * @param durationTimeValue
	 *            durata in millisecondi del touchTimer
	 */
	public NormalizedTimer(TypeNormalizedTimer value, long durationTimeValue) {
		reset();
		type = value;
		durationTime = durationTimeValue;
	}

	/**
	 * resetta il touchTimer
	 */
	public void reset() {
		enlapsedTime = 0;
		currentClock = Clock.now();
	}

	/**
	 * Imposta il tempo massimo, quello a cui arrivare
	 * 
	 * @param maxTimeValue
	 */
	public void setMaxTime(long maxTimeValue) {
		durationTime = maxTimeValue;
	}

	/**
	 * Aggiorna il touchTimer in millisecondi. Usa il orologio interno per determinare quanti ms sono passati.
	 * 
	 * @return se true indica che ha raggiunto il tempo massimo
	 */
	public boolean update() {
		long delta = Clock.now() - currentClock;
		currentClock += delta;
		return update(delta);
	}

	/**
	 * <p>
	 * Accetta in ingresso un valore da 0 a 1 (se non è così viene comunque definito nel range [0, 1]) e dopo averlo riportato in millisecondi, lo aggiunge al timer.
	 * </p>
	 * 
	 * @param value
	 *            valore da 0 a 1 da aggiungere
	 * @return come {@link #update(long)

	 */
	public boolean addNormalizedValue(float value) {
		value = value * durationTime;

		return update((long) value);
	}

	/**
	 * <p>
	 * Aggiorna il tempo in millisecondi. Se true indica che ha raggiunto il tempo massimo. Se aggiungiamo un delta negativo ad un timer
	 * one_time, questo non piò scendere sotto 0. Se invece il timer è infinito, sotto lo 0 viene applicata la formula durationTime-mills
	 * </p>
	 * 
	 * @param mills
	 *            tempo in millisecondi trascorsi dall'ultima iterazione
	 * @return se true indica che ha raggiunto il tempo massimo
	 */
	public boolean update(long mills) {
		if (!started)
			return false;
		boolean finished;

		if (type == TypeNormalizedTimer.ONE_TIME) {
			// se <0 lo impostiamo a 0
			if (mills < 0) {
				mills = 0;
			}
			
			if (mills + enlapsedTime >= durationTime) {
				// ASSERT: sforato tempo massimo
				enlapsedTime = durationTime;
				stop();
				return true;
			} else {
				// siamo ancora dentro
				enlapsedTime = (enlapsedTime + mills) % durationTime;
				return false;
			}
		} else {
			enlapsedTime=mills + enlapsedTime;

			// se negativo tipo -0.1, lo facciamo rientrare comunque nel range 0 .. 1
			if (enlapsedTime < 0)
			{
				enlapsedTime=durationTime+enlapsedTime;
			}
			
			// se abbiamo sforato il tempo massimo, allora abbiamo restituiamo
			// true nell'update
			finished = (enlapsedTime >= durationTime) ? true : false;
			enlapsedTime = enlapsedTime % durationTime;

			return finished;
		}
	}

	/**
	 * Esprime il tempo sottoforma di float che va da 0 a 1.
	 * 
	 * @return
	 */
	public float getNormalizedEnlapsedTime() {
		return (1.0f * enlapsedTime) / durationTime;
	}

	/**
	 * Imposta il touchTimer
	 * 
	 * @param enlapsedTimeValue
	 *            tempo trascorso
	 * @param durationTimeValue
	 *            durata del touchTimer
	 * @param typeValue
	 */
	public void set(long enlapsedTimeValue, long durationTimeValue, TypeNormalizedTimer typeValue) {
		enlapsedTime = enlapsedTimeValue;
		durationTime = durationTimeValue;
		type = typeValue;
	}

	protected boolean started;

	/**
	 * usato per gli update automatici.
	 */
	protected long currentClock;

	public boolean isStarted() {
		return started;
	}

	public void start() {
		started = true;
		reset();
	}

	public void stop() {
		started = false;
	}

	public NormalizedTimer copy() {
		NormalizedTimer value = new NormalizedTimer(type);

		value.enlapsedTime = enlapsedTime;
		value.durationTime = durationTime;
		return value;
	}

	public void copyInto(NormalizedTimer destination) {
		destination.type = type;
		destination.enlapsedTime = enlapsedTime;
		destination.durationTime = durationTime;

	}
}
