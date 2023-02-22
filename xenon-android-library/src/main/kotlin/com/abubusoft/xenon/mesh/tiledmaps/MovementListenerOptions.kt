package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * <p>
 * Opzioni relative al listener sugli eventi di spostamento relativi alla mappa.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class MovementListenerOptions {

	/**
	 * numero di aree in orizzontale in cui suddividere logicamente la mappa
	 */
	public int horizontalAreaCount;

	public float horizontalAreaInvSize;

	/**
	 * numero di aree in verticale in cui suddividere logicamente la mappa
	 */
	public int verticalAreaCount;

	public float verticalAreaInvSize;

	private MovementListenerOptions() {

	}

	/**
	 * <p>
	 * Opzioni di default:
	 * </p>
	 * <ul>
	 * <li>horizontalAreaCount = 1</li>
	 * <li>verticalAreaCount = 1</li>
	 * </ul>
	 * 
	 * @return
	 * 		this
	 */
	public static MovementListenerOptions build() {
		return (new MovementListenerOptions()).horizontalAreaCount(1).verticalAreaCount(1);
	}

	/**
	 * numero di aree in orizzontale in cui suddividere logicamente la mappa
	 */
	public MovementListenerOptions horizontalAreaCount(int value) {
		horizontalAreaCount = value;
		return this;
	}

	/**
	 * numero di aree in verticale in cui suddividere logicamente la mappa
	 */
	public MovementListenerOptions verticalAreaCount(int value) {
		verticalAreaCount = value;
		return this;
	}

}
