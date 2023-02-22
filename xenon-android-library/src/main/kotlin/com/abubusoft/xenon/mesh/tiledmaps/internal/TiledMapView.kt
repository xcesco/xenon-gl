package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 * Rappresenta la vista sulla tiled map, ovvero lo schermo che visualizza solo una parte della mappa.
 *
 * @author xcesco
 */
class TiledMapView {
    var windowBorder = 0

    /**
     * numero di row da considerare come offeset rispetto all'inizio vero e proprio
     */
    var tileRowOffset = 0

    /**
     *
     *
     * Vbo per contenere i vertici che compongono la view. Il sistema di riferimento per le coordinate contenute in questo VBO sono espresse mediante il sistema di riferimento della window.
     *
     */
    var windowVerticesBuffer: VertexBuffer? = null

    /**
     *
     *
     * dimensione dello schermo, considerando lo schermo come un quadrato.
     *
     */
    var windowDimension = 0f

    /**
     *
     *
     * dimensioni dello schermo in pixel, alla distanza [.distanceFromViewer] rispetto allo view.
     *
     *
     *
     *
     * Tiene conto dell'aspect ratio dello schermo.
     *
     */
    var windowWidth = 0

    /**
     *
     *
     * centro della finestra, rispetto alle sistema di coordinate della positionInMap (in alto a sx). Non cambia mai, a presindere dallo scroll. Questo perchè indica sempre e comunque il centro della finestra di visione rispetto a
     * positionInMap.
     *
     *
     * <img src="doc-files/sistemaRiferimento.jpg"></img>
     *
     *
     *
     * Le coordinate rispetto a positionInMap sono così calcolate:
     *
     *
     * <pre>
     * windowCenter.x = (windowWidth / 2f) + tileWidth;
     * windowCenter.y = (windowHeight / 2f) + tileHeight;
    </pre> *
     *
     *
     *
     * Il sistema di riferimento definito da windowCenter ha l'asse delle Y rivolte verso l'alto.
     *
     *
     * @see TiledMap.positionInMap
     */
    val windowCenter: Point2

    /**
     * la distanza del piano rispetto alla camera. Dipende dalle dimensioni del piano. In pixel
     */
    var distanceFromViewer = 0f

    /**
     *
     *
     * Dimensioni dello schermo in pixel, alla distanza [.distanceFromViewer] rispetto allo view.
     *
     *
     *
     * Tiene conto dell'aspect ratio dello schermo.
     *
     */
    var windowHeight = 0

    /**
     * numero di colonne visibili su schermo (+2 di offset)
     */
    var windowTileColumns = 0

    /**
     * numero di righe visibili su schermo (+2 di offset)
     */
    var windowTileRows = 0

    /**
     *
     * Rappresenta il massimo valore che il punto d'origine può assumere in X, nel sistema di riferimento della mappa.
     *
     */
    var mapMaxPositionValueX = 0f

    /**
     *
     * Rappresenta il massimo valore che il punto d'origine può assumere in X, nel sistema di riferimento della mappa.
     */
    var mapMaxPositionValueY = 0f

    /**
     *
     * Usato nelle mappe staggered isometriche.
     */
    val tileBase: Point2
    var tiledWindowWidth = 0f
    var tiledWindowHeight = 0f

    init {
        windowCenter = Point2()
        tileBase = Point2()
    }
}