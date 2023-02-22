package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.vbo.VertexBuffer;

/**
 * Rappresenta la vista sulla tiled map, ovvero lo schermo che visualizza solo una parte della mappa.
 * 
 * @author xcesco
 *
 */
public class TiledMapView {

	public int windowBorder;

	/**
	 * numero di row da considerare come offeset rispetto all'inizio vero e proprio
	 */
    public int tileRowOffset;

    public TiledMapView() {
		windowCenter = new Point2();
		tileBase=new Point2();
	}

	/**
	 * <p>
	 * Vbo per contenere i vertici che compongono la view. Il sistema di riferimento per le coordinate contenute in questo VBO sono espresse mediante il sistema di riferimento della window.
	 * </p>
	 */
	public VertexBuffer windowVerticesBuffer;

	/**
	 * <p>
	 * dimensione dello schermo, considerando lo schermo come un quadrato.
	 * </p>
	 */
	public float windowDimension = 0;

	/**
	 * <p>
	 * dimensioni dello schermo in pixel, alla distanza {@link #distanceFromViewer} rispetto allo view.
	 * </p>
	 * 
	 * <p>
	 * Tiene conto dell'aspect ratio dello schermo.
	 * </p>
	 */
	public int windowWidth;

	/**
	 * <p>
	 * centro della finestra, rispetto alle sistema di coordinate della positionInMap (in alto a sx). Non cambia mai, a presindere dallo scroll. Questo perchè indica sempre e comunque il centro della finestra di visione rispetto a
	 * positionInMap.
	 * </p>
	 * 
	 * <img src="doc-files/sistemaRiferimento.jpg"/>
	 * 
	 * <p>
	 * Le coordinate rispetto a positionInMap sono così calcolate:
	 * </p>
	 * 
	 * <pre>
	 * windowCenter.x = (windowWidth / 2f) + tileWidth;
	 * windowCenter.y = (windowHeight / 2f) + tileHeight;
	 * </pre>
	 * 
	 * <p>
	 * Il sistema di riferimento definito da windowCenter ha l'asse delle Y rivolte verso l'alto.
	 * </p>
	 * 
	 * @see TiledMap#positionInMap
	 */
	public final Point2 windowCenter;

	/**
	 * la distanza del piano rispetto alla camera. Dipende dalle dimensioni del piano. In pixel
	 */
	public float distanceFromViewer;

	/**
	 * <p>
	 * Dimensioni dello schermo in pixel, alla distanza {@link #distanceFromViewer} rispetto allo view.
	 * </p>
	 * <p>
	 * Tiene conto dell'aspect ratio dello schermo.
	 * </p>
	 */
	public int windowHeight;

	/**
	 * numero di colonne visibili su schermo (+2 di offset)
	 */
	public int windowTileColumns;

	/**
	 * numero di righe visibili su schermo (+2 di offset)
	 */
	public int windowTileRows;

	/**
	 * <p>Rappresenta il massimo valore che il punto d'origine può assumere in X, nel sistema di riferimento della mappa.</p>
	 * 
	 */
	public float mapMaxPositionValueX;
	
	/**
	 * <p>Rappresenta il massimo valore che il punto d'origine può assumere in X, nel sistema di riferimento della mappa.</p>
	 */
	public float mapMaxPositionValueY;

	/**
	 * <p>Usato nelle mappe staggered isometriche.</p>
	 */
	public final Point2 tileBase;

	public float tiledWindowWidth;

	public float tiledWindowHeight;

}
