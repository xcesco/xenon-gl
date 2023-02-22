package com.abubusoft.xenon.shader.drawers

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap

/**
 * @author Francesco Benincasa
 */
object TiledMapShaderDrawer {
    /**
     *
     *
     * Disegna la mappa in base al controller.
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel
     * [XenonApplication4OpenGL.onSceneReady])
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     * @param map
     * @param deltaTime
     */
    fun draw(map: TiledMap, deltaTime: Long) {
        map.handler.draw(deltaTime, map.controller.matrixModelViewProjection)
    }

    /**
     *
     *
     * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel
     * [XenonApplication4OpenGL.onSceneReady])
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     *
     * @param map
     * mappa da disegnare
     * @param deltaTime
     * tempo trascorso dall'ultimo draw
     * @param modelViewProjection
     * matrice MVP
     */
    fun draw(map: TiledMap, deltaTime: Long, modelViewProjection: Matrix4x4?) {
        map.handler.draw(deltaTime, modelViewProjection)
    }
}