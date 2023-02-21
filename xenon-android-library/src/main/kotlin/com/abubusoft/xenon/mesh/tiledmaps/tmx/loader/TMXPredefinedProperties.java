package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

/**
 * Proprietà definite per i layer e startX la mappa.
 * 
 * @author Francesco Benincasa
 * 
 */
public class TMXPredefinedProperties {

	/**
	 * <p>
	 * instanceOf. Attributo che indica per un object dentro un object layer che
	 * classe implementa questa istanza.
	 * </p>
	 */
	public static final String INSTANCE_OF = "instanceOf";

	/**
	 * <p>
	 * Se impostato a true, indica che il layer deve essere rimosso
	 * </p>
	 * 
	 * Usato nei <b>LAYER</b>
	 */
	public static final String PREVIEW = "preview";

	/**
	 * Indica la percentuale di spostamento su startX per un layer. Utile per
	 * l'effetto parallasse. Va da 0 a 1.
	 * 
	 * Se non definito è 1.
	 */
	public static final String SPEED_PERCENTAGE_X = "speedPercentageX";

	/**
	 * Indica la percentuale di spostamento su startY per un layer. Utile per
	 * l'effetto parallasse. Va da 0 a 1.
	 * 
	 * Se non definito è 1.
	 */
	public static final String SPEED_PERCENTAGE_Y = "speedPercentageY";

	/**
	 * Proprietà a livello di mappa che indica le animazioni. Es: animationOnda.
	 * 
	 * I vari frame avranno come nome onda1, onda2, onda3 etc.
	 */
	public static final String ANIMATION_PREFIX = "animation";

	/**
	 * parametro di mappa relativo alla durata di default dei frame
	 */
	public static final String ANIMATION_FRAME_DEFAULT_DURATION = "animationFrameDefaultDuration";

	/**
	 * Gruppo di animazione di appartenenza. Es: prova
	 */
	public static final String ANIMATION_ID = "animationId";

	/**
	 * indice di posizione: da 0 a n
	 */
	public static final String ANIMATION_FRAME = "animationFrame";

	/**
	 * durante del frame in ms
	 */
	public static final String ANIMATION_FRAME_DURATION = "animationFrameDuration";

	public static final String ANIMATION_ENABLED = "animationFrameDuration";

	/**
	 * <p>Indica se un'entità è visibile (1) o invisibile (0).</p>
	 */
	public static final String VISIBLE = "visible";
	
	 /**
	 * <p>Indica se l'oggetto è un sensore o meno.</p>
	 */
	public static final String SENSOR="sensor";

}
