/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.tiledmaps.*
import com.abubusoft.xenon.mesh.tiledmaps.Layer.LayerType

/**
 *
 * @author Francesco Benincasa
 */
object TileMapAnimationHelper {
    /**
     * Cerca di costruire eventuali animazioni
     *
     * @param tiledMap
     */
    fun buildAnimations(tiledMap: TiledMap?) {
        tiledMap!!.animations.clear()
        /**
         * mappa dei layer associati al loro nome
         */
        val layersMap = HashMap<String, Layer>()
        for (item in tiledMap.layers) {
            layersMap[item.name] = item
        }
        var animationName: String
        var animation: TileAnimation

        // impostiamo la durata di default di un frame a 100.
        tiledMap.animationFrameDefaultDuration = tiledMap.getPropertyAsLong(TMXPredefinedProperties.ANIMATION_FRAME_DEFAULT_DURATION, 100)

        // per ogni proprietà vediamo se contiene un'animazione
        for ((key, value) in tiledMap.properties) {
            if (key.startsWith(TMXPredefinedProperties.ANIMATION_PREFIX) && key != TMXPredefinedProperties.ANIMATION_FRAME_DEFAULT_DURATION) {
                // animation
                animationName = key.substring(TMXPredefinedProperties.ANIMATION_PREFIX.length)

                // recuperiamo lista di animazioni
                animation = createAnimation(tiledMap, layersMap, animationName, value)
                animation.start(0)
                tiledMap.animations.add(animation)
            }
        }
    }

    /**
     * Crea un'animazione
     *
     * @param tiledMap
     * @param layersMap
     * @param animationName
     * @param animationDefinition
     * @return
     */
    private fun createAnimation(tiledMap: TiledMap?, layersMap: HashMap<String, Layer>, animationName: String, animationDefinition: String): TileAnimation {
        val animation = TileAnimation(animationName)
        // assert: abbiamo qualche animazione
        val frames = animationDefinition.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in frames) {
            item = item.trim { it <= ' ' }
            animation.frames.add(createFrame(tiledMap, layersMap, item))
        }
        return animation
    }

    /**
     * Crea un frame di un'animazione
     *
     * @param tiledMap
     * @param frames
     * @param layersMap
     * @return
     */
    private fun createFrame(tiledMap: TiledMap?, layersMap: HashMap<String, Layer>, frameDefinition: String): TileAnimationFrame {
        val frame: TileAnimationFrame
        // non abbiamo ancora inserito l'animazione, quindi quello che segue è l'indice dell'animazione
        val animationIndex = tiledMap!!.animations.size
        val values = frameDefinition.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val tempLayer = layersMap[values[0].trim { it <= ' ' }]
        if (tempLayer!!.type != LayerType.TILED) throw XenonRuntimeException("For an animation can not use an image layer")
        val layer = tempLayer as TiledLayer?
        layer!!.animationOwnerIndex = animationIndex
        frame = TileAnimationFrame(layer, tiledMap.animationFrameDefaultDuration)
        if (values.size > 1) {
            frame.duration = values[1].trim { it <= ' ' }.toLong()
        }
        return frame
    }
}