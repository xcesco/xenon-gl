/**
 *
 */
package com.abubusoft.xenon.core.graphic.animation

import android.content.Context
import android.graphics.drawable.AnimationDrawable

/**
 *
 * Gestore delle animazioni bitmap.
 *
 * @author Francesco Benincasa
 */
class BitmapAnimationManager private constructor() {
    /**
     * mappa degli animator
     */
    private val animationMap: HashMap<String, BitmapAnimation> = HashMap()

    /**
     * insieme delle animazioni già definite
     */
    private val animationId: HashSet<Int>

    init {
        animationId = HashSet()
    }

    fun clear() {
        animationMap.clear()
    }

    /**
     *
     * Crea un insieme di animazioni drawable a partire da un insieme di image atlas in formato gdx.
     *
     * @param context
     * @param spriteDefinitionResourceId
     * @param animationDefinitionResourceId
     * @param imageResourceId
     * @return
     */
    fun createAnimationFromGDX(context: Context, spriteDefinitionResourceId: Int, animationDefinitionResourceId: Int, imageResourceId: Int): Int {
        // se l'animationId è stato già definito, allora usciamo senza fare niente.
        if (animationId.contains(animationDefinitionResourceId)) return 0
        animationId.add(animationDefinitionResourceId)
        val atlas = BitmapAtlasLoaderGDX.createBitmapAtlas(context, spriteDefinitionResourceId, animationDefinitionResourceId, imageResourceId)

        // creiamo associazione tra animazione ed atlas
        for (item in atlas.values) {
            animationMap[item.name] = item
        }
        return atlas.size
    }

    /**
     * Recupera l'animazione da tutti gli atlas partendo dal nome
     * dell'animazione
     *
     * @param animationName
     * @return
     */
    fun getAnimation(animationName: String?): AnimationDrawable {
        return createCopy(animationMap[animationName]!!.frames)
    }

    /**
     * Crea una copia dell'animazione
     * @param src
     * @return
     */
    fun createCopy(src: AnimationDrawable): AnimationDrawable {
        val dest = AnimationDrawable()
        dest.isOneShot = src.isOneShot
        for (i in 0 until src.numberOfFrames) {
            dest.addFrame(src.getFrame(i), src.getDuration(i))
        }
        return dest
    }

    companion object {
        /**
         * pattern singleton
         *
         * @return
         */
        /**
         * instanza singleton
         */
        val instance = BitmapAnimationManager()
    }
}