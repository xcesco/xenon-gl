package com.abubusoft.xenon.mesh.tiledmaps;

import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil;
import org.xml.sax.Attributes;

public class TiledLayer extends Layer {

	/**
	 * colonne del layer
	 */
	public final int tileColumns;

	/**
	 * righe del layer
	 */
	public final int tileRows;

	/**
	 * Indica se è visualizzabile o meno
	 * 
	 * @return true se il layer è drawable
	 */
	@Override
	public boolean isDrawable() {
		if (animationOwnerIndex == -1) {
			return visible;
		} else {
			return tiledMap.animations.get(animationOwnerIndex).isLayerToDraw(this);
		}
	}

	/**
	 * <p>
	 * Mappa delle tile. Le tile vengono memorizzate in orizzontale, ovvero:
	 * </p>
	 * 
	 * <code>tile[row][col]</code>
	 * 
	 * <p>
	 * Con un unico array questo diventa
	 * </p>
	 * 
	 * <code>tile[row*colSize+col]</code>
	 */
	public final Tile[] tiles;

	/**
	 * contatore dei tile presenti. Serve a tener traccia di quante tile sono state inserite fino a questo momento.
	 */
	public int tileCounter;

	public boolean useLoop = false;

	/**
	 * larghezza del layer in pixel
	 */
	public int width;

	/**
	 * altezza del layer in pixel
	 */
	public int height;

	/**
	 * l'indica l'elemento da disegnare
	 */
	public Tile tileToDraw;

	public int oldStartLayerColumn;

	public int oldStartLayerRow;

	/**
	 * <p>Rendering offset for this layer in pixels. Defaults to 0. (since 0.14)</p>
	 * 
	 * <b>DA IMPOSTARE SEMPRE A 0. Il valore nella definizione della mappa viene ignorato</b>
	 */
	//public int tileOffsetX;

	/**
	 * Rendering offset for this layer in pixels. Defaults to 0. (since 0.14)
	 * 
	 * <b>DA IMPOSTARE SEMPRE A 0. Il valore nella definizione della mappa viene ignorato</b>
	 */
	//public int tileOffsetY;

	public TiledLayer(TiledMap tiledMap, Attributes atts) {
		super(LayerType.TILED, tiledMap, atts);

		this.tileColumns = SAXUtil.getInt(atts, LayerAttributes.WIDTH);
		this.tileRows = SAXUtil.getInt(atts, LayerAttributes.HEIGHT);

		//this.tileOffsetX = SAXUtil.getInt(atts, LayerAttributes.OFFSET_X, 0);
		//this.tileOffsetY = SAXUtil.getInt(atts, LayerAttributes.OFFSET_Y, 0);
		
		this.tiles = new Tile[tileRows * tileColumns];
		this.animationOwnerIndex = -1;

		oldStartLayerColumn = -1;
		oldStartLayerRow = -1;
		
		drawOffsetUnique=true;
		drawOffsetX=0f;
		drawOffsetY=0f;
	}

	/**
	 * indica l'animazione associata
	 */
	public int animationOwnerIndex;

	public TiledLayerHandler handler;

	/**
	 * rappresenta la larghezza massima registrata per i tile inseriti in questo layer
	 */
	public int tileWidthMax;

	/**
	 * rappresenta l'altezza massima registrata per i tile inseriti in questo layer
	 */
	public int tileHeightMax;
	
	/**
	 * valore dell'screenOffsetX da usare per tutti gli elementi. Se e solo se uniqueOffset=true.
	 */
	public float drawOffsetX=0f;
	
	/**
	 * valore dell'screenOffsetY da usare per tutti gli elementi. Se e solo se uniqueOffset=true.
	 */
	public float drawOffsetY=0f;
	
	/**
	 * se true, indica che gli offset, sia x che y sono identici per tutte le tile del layer, quindi non occorre
	 * modificarle
	 */
	public boolean drawOffsetUnique=true;

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
	@Override
	public void onBuildView(TiledMapView view) {
		handler.onBuildView(view);		
	}

	@Override
	protected void buildHandler(AbstractLayerHandler<?> handler) {
		this.handler=(TiledLayerHandler) handler;		
	}

	@Override
	public LayerDrawer drawer() {
		return handler;
	}

	@Override
	public TiledMapView view() {
		return handler.view();
	}

}