/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.ArrayList;

/**
 * @author Francesco Benincasa
 *
 */
public class ObjClass extends ObjBase {
	
	public ObjClass()
	{
		category=CategoryType.CLASS;
	}
	
	/**
	 * layer di appartenenza dello shape
	 */
	public TiledLayer shapeLayer;

	/**
	 * prima colonna da considerare per lo shape
	 */
	public int shapeColBegin;

	public int shapeColSize;

	public int shapeRowBegin;

	public int shapeRowSize;

	/**
	 * pixel da considerare in orizzontale per collocare lo shape rispetto alla posizione delle tile
	 */
	public int shapeColOffset;

	/**
	 * pixel da considerare in verticale per collocare lo shape rispetto alla posizione delle tile
	 */
	public int shapeRowOffset;

	/**
	 * <p>Elenco delle parti che compongono l'elemento.</p>
	 */
	public ArrayList<ObjDefinition> parts=new ArrayList<>();
	
	
}
