package com.abubusoft.xenon.mesh.modifiers;

import com.abubusoft.xenon.vbo.IndexBuffer;
import com.abubusoft.xenon.vbo.VertexBuffer;

public class IndexQuadModifier {

	/**
	 * <p>
	 * </p>
	 * 
	 * @param indexBuffer
	 * @param cursor
	 * @param numTile
	 * @param b
	 */
	public static void setIndexes(IndexBuffer indexBuffer, int cursor, int numTile, boolean update) {
		int c = indexBuffer.cursor;

		// dobbiamo convertire da # di tile a # di indice vertice. In ogni tile ci sono 4 vertici, quindi il passaggio è semplice.
		numTile *= VertexBuffer.VERTEX_IN_QUAD_TILE;

		indexBuffer.values[c + 0] = (short) (numTile + 0);
		indexBuffer.values[c + 1] = (short) (numTile + 1);
		indexBuffer.values[c + 2] = (short) (numTile + 2);

		indexBuffer.values[c + 3] = (short) (numTile + 2);
		indexBuffer.values[c + 4] = (short) (numTile + 3);
		indexBuffer.values[c + 5] = (short) (numTile + 0);

		if (update) {
			indexBuffer.update();
		}
	}

}
