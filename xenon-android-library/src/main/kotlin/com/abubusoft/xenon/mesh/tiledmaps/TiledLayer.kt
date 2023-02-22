package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getInt
import org.xml.sax.Attributes

class TiledLayer(tiledMap: TiledMap, atts: Attributes?) : Layer(LayerType.TILED, tiledMap, atts) {
    /**
     * colonne del layer
     */
    val tileColumns: Int

    /**
     * righe del layer
     */
    val tileRows: Int

    /**
     * Indica se è visualizzabile o meno
     *
     * @return true se il layer è drawable
     */
    override var isDrawable: Boolean
        get() = if (animationOwnerIndex == -1) {
            visible
        } else {
            tiledMap.animations[animationOwnerIndex]!!.isLayerToDraw(this)
        }
        set(isDrawable) {
            super.isDrawable = isDrawable
        }

    /**
     *
     *
     * Mappa delle tile. Le tile vengono memorizzate in orizzontale, ovvero:
     *
     *
     * `tile[row][col]`
     *
     *
     *
     * Con un unico array questo diventa
     *
     *
     * `tile[row*colSize+col]`
     */
    val tiles: Array<Tile?>

    /**
     * contatore dei tile presenti. Serve a tener traccia di quante tile sono state inserite fino a questo momento.
     */
    var tileCounter = 0
    var useLoop = false

    /**
     * larghezza del layer in pixel
     */
    var width = 0

    /**
     * altezza del layer in pixel
     */
    var height = 0

    /**
     * l'indica l'elemento da disegnare
     */
    var tileToDraw: Tile? = null
    var oldStartLayerColumn: Int
    var oldStartLayerRow: Int

    /**
     * indica l'animazione associata
     */
    var animationOwnerIndex: Int
    var handler: TiledLayerHandler? = null

    /**
     * rappresenta la larghezza massima registrata per i tile inseriti in questo layer
     */
    var tileWidthMax = 0

    /**
     * rappresenta l'altezza massima registrata per i tile inseriti in questo layer
     */
    var tileHeightMax = 0

    /**
     * valore dell'screenOffsetX da usare per tutti gli elementi. Se e solo se uniqueOffset=true.
     */
    var drawOffsetX = 0f

    /**
     * valore dell'screenOffsetY da usare per tutti gli elementi. Se e solo se uniqueOffset=true.
     */
    var drawOffsetY = 0f

    /**
     * se true, indica che gli offset, sia x che y sono identici per tutte le tile del layer, quindi non occorre
     * modificarle
     */
    var drawOffsetUnique = true
    /**
     *
     * Rendering offset for this layer in pixels. Defaults to 0. (since 0.14)
     *
     * **DA IMPOSTARE SEMPRE A 0. Il valore nella definizione della mappa viene ignorato**
     */
    //public int tileOffsetX;
    /**
     * Rendering offset for this layer in pixels. Defaults to 0. (since 0.14)
     *
     * **DA IMPOSTARE SEMPRE A 0. Il valore nella definizione della mappa viene ignorato**
     */
    //public int tileOffsetY;
    init {
        tileColumns = getInt(atts!!, LayerAttributes.WIDTH)
        tileRows = getInt(atts, LayerAttributes.HEIGHT)

        //this.tileOffsetX = SAXUtil.getInt(atts, LayerAttributes.OFFSET_X, 0);
        //this.tileOffsetY = SAXUtil.getInt(atts, LayerAttributes.OFFSET_Y, 0);
        tiles = arrayOfNulls(tileRows * tileColumns)
        animationOwnerIndex = -1
        oldStartLayerColumn = -1
        oldStartLayerRow = -1
        drawOffsetUnique = true
        drawOffsetX = 0f
        drawOffsetY = 0f
    }

    /*
	 * public int getColumns() { return this.tileColumns; }
	 * 
	 * public int getRows() { return this.tileRows; }
	 * 
	 * public boolean contains(int startX, int startY) { return false; }
	 */
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.Layer#onWindowCreate()
	 */
    override fun onBuildView(view: TiledMapView) {
        handler!!.onBuildView(view)
    }

    override fun buildHandler(handler: AbstractLayerHandler<*>?) {
        this.handler = handler as TiledLayerHandler?
    }

    override fun drawer(): LayerDrawer? {
        return handler
    }

    override fun view(): TiledMapView? {
        return handler!!.view()
    }
}