package com.abubusoft.xenon.texture;

/**
 * Texture suddivisa in rettangoli (tipicamente quadrati) di eguali dimensioni.
 * 
 * @author Francesco Benincasa
 * 
 */
public class AtlasTexture extends Texture {
	/**
	 * indice colonna del tile selezionato
	 */
	public int selectedColumn;
	
	/**
	 * indice riga del tile selezionato
	 */
	public int selectedRow;
	
	/**
	 * numero totale di righe
	 */
	public int rowsCount;
	
	/**
	 * numero totale di colonne 
	 */
	public int columnsCount;
	
	/**
	 * larghezza del tile
	 */
	public final int tileWidth;
	
	/**
	 * altezza del tile
	 */
	public final int tileHeight;
	
	/**
	 * bordo di ogni tile
	 */
	public final int spacing;
	
	/**
	 * margine della texture
	 */
	public final int margin;

	/**
	 * @param bindingId
	 * @param options
	 * @param dimension
	 */
	AtlasTexture(Texture texture, AtlasTextureOptions options) {
		super(texture.name, texture.bindingId);
		this.updateInfo(texture.info);
		
		this.index=texture.index;
		this.selectedColumn = options.selectedColumn;
		this.selectedRow = options.selectedRow;
		this.tileWidth = options.tileWidth;
		this.tileHeight = options.tileHeight;
		this.spacing = options.spacing;
		this.margin = options.margin;
		
		rowsCount=info.dimension.height / (tileHeight + spacing);
		columnsCount=info.dimension.width / (tileWidth + spacing);
	}

	/**
	 * <p>Imposta l'elemento dell'atlas selezionato.</p>
	 * 
	 * <p>Dopo aver invocato questo metodo, è possibile recuperare le coordinate UV del frame.</p>
	 * 
	 * @param row
	 * 		riga
	 * @param col
	 * 		colonna
	 */
	public void selectedCurrentFrame(int row, int col) {
		this.selectedColumn = col;
		this.selectedRow = row;
	}

	public float getCoordStartX() {
		float value = margin + ((tileWidth + spacing) * selectedColumn);
		return value / info.dimension.width; 
	}

	public float getCoordStartY() {
		float value = margin + ((tileHeight + spacing) * selectedRow);
		return value / info.dimension.height;
	}

	public float getCoordEndX() {
		float value = margin + ((tileWidth + spacing) * selectedColumn) + tileWidth;
		return value / info.dimension.width;
	}

	public float getCoordEndY() {
		float value = margin + ((tileHeight + spacing) * selectedRow) + tileHeight;
		return value / info.dimension.height;
	}

}