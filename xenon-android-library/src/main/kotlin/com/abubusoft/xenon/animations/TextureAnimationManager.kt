/**
 *
 */
package com.abubusoft.xenon.animations

import android.content.Context
import com.abubusoft.xenon.texture.TextureOptions

/**
 * @author Francesco Benincasa
 */
class TextureAnimationManager
/**
 * mappa delle atlas
 */
//private HashMap<String, TextureAnimationAtlas> atlasMap;
private constructor() : AbstractAnimationManager<TextureTimeline?, TextureAnimation?>() {
    override fun clear() {
        var item: TextureAnimation
        var frame: TextureKeyFrame?
        for (i in animationList.indices) {
            item = animationList[i]!!
            for (j in 0 until item.size()) {
                frame = item.getFrame(j)
                frame!!.texture = null
                frame.textureRegion = null
            }
        }
        timelineMap.clear()
        timelineList.clear()
        animationMap.clear()
        animationList.clear()
    }

    /**
     *
     *
     * Carica la definizione di una texture animation. L'animazione si compone di 3 file: l'atlas, la definizione degli sprite e quella delle animazioni. Dato ad esempio un
     * baseName **heart** i nomi dei file dovrebbero essere rispettivamente **heart.png** **heart.sprites** e **heart.animations**
     *
     *
     *
     *
     * Tutti e tre i file si trovano nella cartella `assets`.
     *
     *
     * @param context
     * context
     * @param baseName
     * nome di base dell'animazione
     * @param options
     * opzioni per il caricamento della texture
     * @return atlas
     */
    fun createTextureAnimationAtlasFromAssets(context: Context?, baseName: String, options: TextureOptions?) {
        setup(TextureAnimationLoader.loadFromAssets(context, baseName, options))
    }

    /**
     * Carica da file la definizione di animazioni.
     *
     * @param context
     * @param spriteDefinitionResourceId
     * resource Id della definizione degli sprite
     * @param animationDefinitionResourceId
     * resource Id della definizione delle animazioni
     * @param imageResourceId
     * @return
     */
    fun createTextureAnimationAtlasFromResources(
        context: Context?,
        spriteDefinitionResourceId: Int,
        animationDefinitionResourceId: Int,
        imageResourceId: Int,
        options: TextureOptions?
    ) {
        setup(TextureAnimationLoader.loadFromResources(context, spriteDefinitionResourceId, animationDefinitionResourceId, imageResourceId, options))
    }

    /**
     *
     *
     * Registra l'atlas e gli assegna un uid. Se lo shader non è stato ancora definito, lo definisce
     *
     *
     * @param atlas
     * @return
     */
    fun setup(atlas: ArrayList<TextureAnimation?>?) {
        var item: TextureAnimation?
        // creiamo associazione tra animazione ed atlas
        for (i in atlas!!.indices) {
            item = atlas[i]
            animationMap[item!!.name!!] = item
            animationList.add(item)
        }
    }

    companion object {
        /**
         * instanza singleton
         */
        private val instance = TextureAnimationManager()

        /**
         * pattern singleton
         *
         * @return
         */
        fun instance(): TextureAnimationManager {
            return instance
        }
    }
}