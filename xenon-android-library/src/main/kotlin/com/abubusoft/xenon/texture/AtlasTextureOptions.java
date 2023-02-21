package com.abubusoft.xenon.texture;

/**
 * Opzioni di una texture tiled, quella utilizzata per creare delle tiles
 * 
 * @author Francesco Benincasa
 *
 */
public class AtlasTextureOptions {
	/**
	 * larghezza della tile
	 */
	public int tileWidth;
	
	/**
	 * altezza della tile
	 */
	public int tileHeight;
	
	/**
	 * row selezionata, (startX , )
	 */
	public int selectedRow;
	
	/**
	 * colonna selezinata (  , startY)
	 */
	public int selectedColumn;
	
	/**
	 * dimensioni in pixel del bordo (cornice) di ogni tiles
	 */
	public int spacing;
	
	/**
	 * dimensioni in pixel del margine complessivo
	 */
	public int margin;
	
	
	/**
	 * larghezza della tile
	 */
	public AtlasTextureOptions tileWidth(int value)
	{
		tileWidth=value;
		
		return this;
	}
	
	/**
	 * altezza della tile
	 */
	public AtlasTextureOptions tileHeight(int value)
	{
		tileHeight=value;
		
		return this;
	}
	
	/**
	 * row selezionata, (startX , )
	 */
	public AtlasTextureOptions selectedRow(int value)
	{
		selectedRow=value;
		
		return this;
	}
	
	/**
	 * colonna selezinata (  , startY)
	 */
	public AtlasTextureOptions selectedColumn(int value)
	{
		selectedColumn=value;
		
		return this;
	}
	
	/**
	 * dimensioni in pixel del bordo (cornice)
	 */
	public AtlasTextureOptions spacing(int value)
	{
		spacing=value;
		
		return this;
	}
	
	/**
	 * dimensioni in pixel del margine (tra ogni tile)
	 */
	public AtlasTextureOptions margin(int value)
	{
		margin=value;
		
		return this;
	}
	
	/**
	 * Build delle opzioni. configurazione di default:
	 * tileWidth = 32
	 * tileHeight= 32
	 * selectedRow = 0
	 * selectedColumn = 0
	 * spacing = 0
	 * margin = 0
	 * 
	 * @return
	 */
	public static AtlasTextureOptions build()
	{
		// configurazione di default
		return (new AtlasTextureOptions()).spacing(0).margin(0).selectedColumn(0).selectedRow(0).tileHeight(32).tileWidth(32);
	}
}
