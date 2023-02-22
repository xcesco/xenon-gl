/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * Opzioni di visualizzazione relative alle tiled map.
 * 
 * @author Francesco Benincasa
 * 
 */
/**
 * @author Francesco Benincasa
 * 
 */
public class TiledMapOptions {

	/**
	 * Se true consente di creare un controller.
	 */
	public boolean createController;

	/**
	 * posizione iniziale sulla mappa
	 */
	public TiledMapPositionType startPosition = TiledMapPositionType.MIDDLE_CENTER;

	/**
	 * Indica quale dimensione (altezza, larghezza) prendere come riferimento per riempire lo schermo. Ad esempio se selezioniamo FILL_HEIGHT, vuol dire che lo schermo sarà riempito in altezza.
	 * 
	 */
	public TiledMapFillScreenType fillScreenType = TiledMapFillScreenType.FILL_HEIGHT;

	/**
	 * Numero di tile visibili per dimensione. Viene utilizzato solo se fillScreenType è definito come {@link TiledMapFillScreenType#FILL_CUSTOM_HEIGHT} o {@link TiledMapFillScreenType#FILL_CUSTOM_WIDTH}
	 */
	public int visibleTiles = 0;

	/**
	 * indica se lo scroll orizzontale è consentito o meno.
	 */
	public boolean scrollHorizontalLocked = true;

	/**
	 * indica se lo scroll verticale è consentito o meno.
	 */
	public boolean scrollVerticalLocked = true;

	/**
	 * a prescindere dalla dimensione presa come riferimento, questa percentuale indica quanto della dimensione è resa visibile sullo schermo.
	 */
	public float visiblePercentage = 1.0f;

	/**
	 * build della configurazione di base.
	 * 
	 * <ul>
	 * <li><b>createController</b>: true - crea il controller</li>
	 * <li><b>fillScreenType</b>: {@link TiledMapFillScreenType#FILL_HEIGHT}</li>
	 * <li><b>scrollHorizontalLocked</b>: true - lo scroll orizzontale</li>
	 * <li><b>scrollVerticalLocked</b>: true - lo scroll verticale</li>
	 * <li><b>startPosition</b>: {@link TiledMapPositionType#MIDDLE_CENTER}</li>
	 * <li><b>visibileTiles</b>: 0 - Numero di tile visibili per dimensione. Viene utilizzato solo se fillScreenType è definito come {@link TiledMapFillScreenType#FILL_CUSTOM}</li>
	 * <li><b>visiblePercentage</b>: 1 - questa percentuale indica quanto della dimensione è resa visibile sullo schermo.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public static TiledMapOptions build() {
		return (new TiledMapOptions()).createController(true);
	}

	public TiledMapOptions startPosition(TiledMapPositionType value) {
		startPosition = value;
		return this;
	}

	public TiledMapOptions scrollHorizontalLocked(boolean value) {
		scrollHorizontalLocked = value;
		return this;
	}

	public TiledMapOptions scrollVerticalLocked(boolean value) {
		scrollVerticalLocked = value;
		return this;
	}

	public TiledMapOptions fillScreenType(TiledMapFillScreenType value) {
		fillScreenType = value;
		return this;
	}

	public TiledMapOptions fillScreenType(TiledMapFillScreenType value, int visibleTiles) {
		fillScreenType = value;
		this.visibleTiles = visibleTiles;
		return this;
	}

	/**
	 * a prescindere dalla dimensione presa come riferimento, questa percentuale indica quanto della dimensione è resa visibile sullo schermo.
	 * 
	 * Questo parametro non è in sostituzione del numero di tiles da visualizzare. Si va ad aggiungere a tale definizione.
	 * 
	 * @param value
	 * @return this
	 */
	public TiledMapOptions visiblePercentage(float value) {
		visiblePercentage = value;
		return this;
	}

	/**
	 * <p>
	 * Numero di tile visibili per dimensione. Viene utilizzato solo se fillScreenType è definito come {@link TiledMapFillScreenType#FILL_CUSTOM_HEIGHT} o {@link TiledMapFillScreenType#FILL_CUSTOM_WIDTH}.
	 * </p>
	 * 
	 * <p>
	 * Da tenere in considerazione che per il sistema lo schermo è sempre da considerarsi come un quadrato avente come lato la dimensione dello schermo più grande.
	 * </p>
	 * 
	 */
	public TiledMapOptions visibileTiles(int value) {
		visibleTiles = value;

		return this;
	}

	/**
	 * Se true consente di creare un controller.
	 * 
	 * @param value
	 * @return
	 */
	public TiledMapOptions createController(boolean value) {
		createController = value;

		return this;
	}

}
