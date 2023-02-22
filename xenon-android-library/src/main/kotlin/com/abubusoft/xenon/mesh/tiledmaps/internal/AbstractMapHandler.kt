/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.XenonApplication4OpenGL
import com.abubusoft.xenon.mesh.tiledmaps.Layer

/**
 * Gestore base per un gestore di tiledmap
 *
 * @author xcesco
 * @param <E>
</E> */
abstract class AbstractMapHandler<E : AbstractMapController?>(map: TiledMap) : MapHandler {
    protected var layerOffsetHolder: LayerOffsetHolder
    protected var map: TiledMap

    /**
     * controller con il cast alla classe concreta. Serve ad evitare ad invocazione di dover effettuare il cast.
     */
    protected var controller: E? = null

    init {
        this.map = map
        layerOffsetHolder = LayerOffsetHolder()
    }

    /**
     *
     *
     * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel [XenonApplication4OpenGL.onSceneReady]
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     *
     * @param deltaTime
     * tempo trascorso dall'ultimo draw
     * @param modelViewProjection
     * matrice MVP
     */
    override fun draw(deltaTime: Long, modelViewProjection: Matrix4x4?) {
        // puliamo schermo
        GLES20.glClearColor(map.backgroundColorR.toFloat(), map.backgroundColorG.toFloat(), map.backgroundColorB.toFloat(), map.backgroundColorA.toFloat())
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // aggiorniamo il frame marker per gli oggetti
        ObjDefinition.updateGlobalFrameMarker()
        val shader: ShaderTiledMap = map.shader
        var item: Layer
        // offset
        var n: Int
        shader.use()
        map.updateTime(deltaTime)
        // le texture vengono impostate all'inizio
        val nTexture: Int = map.texturesCount
        val nLayer: Int = map.layers.size
        if (nTexture == 1) {
            // impostiamo la texture una volta sola, ce n'è solo una
            shader.setTexture(0, map.layers.get(0).textureList.get(0))

            // versione ottimizzata
            for (i in 0 until nLayer) {
                item = map.layers.get(i)

                // http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
                convertMap2ViewLayer(layerOffsetHolder, Math.round(item.layerOffsetX), Math.round(item.layerOffsetY))
                if (item.isDrawable) {
                    item.drawer().drawLayer(
                        shader,
                        deltaTime,
                        layerOffsetHolder.tileIndexX,
                        layerOffsetHolder.tileIndexY,
                        layerOffsetHolder.screenOffsetX,
                        layerOffsetHolder.screenOffsetY,
                        modelViewProjection
                    )
                }
            }
        } else {
            // versione normale
            for (i in 0 until nLayer) {
                item = map.layers.get(i)

                // http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
                /*
				 * currentMapOffsetX = Math.round(item.screenOffsetX); currentMapOffsetY = Math.round(item.screenOffsetY);
				 * 
				 * screenOffsetX = currentMapOffsetX % map.tileWidth; screenOffsetY = currentMapOffsetY % map.tileWidth;
				 */convertMap2ViewLayer(layerOffsetHolder, Math.round(item.layerOffsetX), Math.round(item.layerOffsetY))
                if (item.isDrawable) {
                    n = item.textureList.size

                    // imposta le texture
                    for (j in 0 until n) {
                        shader.setTexture(j, item.textureList[j])
                    }
                    item.drawer().drawLayer(
                        shader,
                        deltaTime,
                        layerOffsetHolder.tileIndexX,
                        layerOffsetHolder.tileIndexY,
                        layerOffsetHolder.screenOffsetX,
                        layerOffsetHolder.screenOffsetY,
                        modelViewProjection
                    )
                }
            }
        }
    }

    /**
     *
     *
     * Disegna la mappa in base al controller.
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel [XenonApplication4OpenGL.onSceneReady]
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     * @param deltaTime
     */
    override fun draw(deltaTime: Long) {
        draw(deltaTime, map.controller.getMatrixModelViewProjection())
    }
}