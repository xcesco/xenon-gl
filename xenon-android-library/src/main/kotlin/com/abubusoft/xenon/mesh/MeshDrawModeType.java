package com.abubusoft.xenon.mesh;

import android.opengl.GLES20;

/**
 * <p>Tipo di drawing supportati</p>
 * 
 * <img src="doc-files/triangleDrawStyle.jpg"/>
 * 
 * <p>In base al tipo di stile, cambiano inoltre i modi in cui gli attributi dello shape possono essere assegnati.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public enum MeshDrawModeType
{
	/**
	 * <p>Disegno con indici triangoli, scollegati tra loro. Utile per disegnare i quad o le mesh genericamente calcolate.</p>
	 * 
	 */
	INDEXED_TRIANGLES(GLES20.GL_TRIANGLES),
	
	/**
	 * 
	 */
	LINES(GLES20.GL_LINES),
	
	/**
	 * <p>Disegno semplici triangoli, scollegati tra loro. Serve per gli oggetti caricati dal formato .obj</p>
	 * 
	 */
	TRIANGLES(GLES20.GL_TRIANGLES),
	
	/**
	 * <p>Triangle strip</p>
	 *
	 */
	TRIANGLE_STRIP(GLES20.GL_TRIANGLE_STRIP);
	
	/**
	 * valore associato
	 */
	public int value;
	
	private MeshDrawModeType(int newValue)
	{
		value=newValue;
	}
}