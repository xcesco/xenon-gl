package com.abubusoft.xenon.shader.drawers;

import com.abubusoft.xenon.XenonApplication4OpenGL;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;

/**
 * @author Francesco Benincasa
 * 
 */
public abstract class TiledMapShaderDrawer {

	/**
	 * <p>
	 * Disegna la mappa in base al controller.
	 * </p>
	 * 
	 * <p>
	 * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel
	 * {@link XenonApplication4OpenGL#onSceneReady(android.content.SharedPreferences, boolean, boolean, boolean)})
	 * </p>
	 * 
	 * <pre>
	 * GLES20.glEnable(GLES20.GL_BLEND);
	 * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	 * </pre>
	 * 
	 * @param map
	 * @param deltaTime
	 */
	public static void draw(TiledMap map, long deltaTime) {
		map.handler.draw(deltaTime, map.controller.getMatrixModelViewProjection());
	}

	/**
	 * <p>
	 * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
	 * </p>
	 * 
	 * <p>
	 * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel
	 * {@link XenonApplication4OpenGL#onSceneReady(android.content.SharedPreferences, boolean, boolean, boolean)})
	 * </p>
	 * 
	 * <pre>
	 * GLES20.glEnable(GLES20.GL_BLEND);
	 * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	 * </pre>
	 * 
	 * 
	 * @param map
	 *            mappa da disegnare
	 * @param deltaTime
	 *            tempo trascorso dall'ultimo draw
	 * @param modelViewProjection
	 *            matrice MVP
	 */
	public static void draw(TiledMap map, long deltaTime, Matrix4x4 modelViewProjection) {
		map.handler.draw(deltaTime, modelViewProjection);
	}
}
