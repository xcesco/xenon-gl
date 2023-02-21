package com.abubusoft.xenon.core.graphic.animation

import android.content.Context
import com.abubusoft.xenon.core.graphic.BitmapUtility
import com.abubusoft.xenon.core.util.IOUtility

/**
 * Factory degli sprite, animati e non.
 *
 * @author Francesco Benincasa
 */
object BitmapAtlasLoaderGDX {
    fun createBitmapAtlas(context: Context, spriteDefinitionResourceId: Int, animationDefinitionResourceId: Int, imageResourceId: Int): HashMap<String, BitmapAnimation> {
        return try {
            val source = BitmapUtility.loadImageFromResource(context, imageResourceId)
            val spriteDefinition = IOUtility.readRawTextFile(context, spriteDefinitionResourceId)
            val tilesMap = LoaderGDXHelper.createTiles(spriteDefinition, source)
            val animationDefinition = IOUtility.readRawTextFile(context, animationDefinitionResourceId)
            val animationMap = LoaderGDXHelper.createAnimations(context.resources, animationDefinition, tilesMap)
            source.recycle()
            animationMap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}