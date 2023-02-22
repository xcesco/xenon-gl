package com.abubusoft.xenon.animations

import android.content.Context
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.util.IOUtility.readRawTextFile
import com.abubusoft.xenon.core.util.IOUtility.readTextFileFromAssets
import com.abubusoft.xenon.texture.AtlasTextureOptions
import com.abubusoft.xenon.texture.TextureManager
import com.abubusoft.xenon.texture.TextureOptions

/**
 * Classe di utilitaà per caricare animazioni in formato gdx
 *
 * @author Francesco Benincasa
 */
internal object TextureAnimationLoader {
    /**
     *
     *
     * Carica la definizione di una texture animation. L'animazione si compone di 3 risorse: l'atlas, la definizione degli sprite e quella delle animazioni. Dato ad esempio un
     * baseName **heart** i nomi dei file dovrebbero essere rispettivamente **heart.png** **heart_sprites.txt** e **heart_animations.txt**
     *
     *
     * @param context
     * context
     * @param spriteDefinitionResourceId
     * resource id della definizione dello sprite
     * @param animationDefinitionResourceId
     * resource id della definizione delle animazioni
     * @param imageResourceId
     * id dell'immagine
     * @param options
     * opzioni per il caricamento della texture
     * @return atlas
     */
    fun loadFromResources(
        context: Context?,
        spriteDefinitionResourceId: Int,
        animationDefinitionResourceId: Int,
        imageResourceId: Int,
        options: TextureOptions?
    ): ArrayList<TextureAnimation?>? {
        return try {
            val texture = TextureManager.instance().createTextureFromResourceId(context, imageResourceId, options)
            val atlasTexture = TextureManager.instance().createAtlasTexture(texture, AtlasTextureOptions.build())
            val spriteDefinition = readRawTextFile(context!!, spriteDefinitionResourceId)
            val tilesMap = GDXParserHelper.createTiles(spriteDefinition, atlasTexture)
            val animationDefinition = readRawTextFile(context, animationDefinitionResourceId)
            GDXParserHelper.createAnimations(animationDefinition, tilesMap, atlasTexture)
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            null
        }
    }

    /**
     *
     *
     * Carica la definizione di una texture animation. L'animazione si compone di 3 file: l'atlas, la definizione degli sprite e quella delle animazioni. Dato ad esempio un
     * baseName **heart** i nomi dei file dovrebbero essere rispettivamente **heart.png** **heart.sprites** e **heart.animations**
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
    fun loadFromAssets(context: Context?, baseName: String, options: TextureOptions?): ArrayList<TextureAnimation?>? {
        return try {
            val texture = TextureManager.instance().createTextureFromAssetsFile(context, "$baseName.png", options)
            val atlasTexture = TextureManager.instance().createAtlasTexture(texture, AtlasTextureOptions.build())
            val spriteDefinition = readTextFileFromAssets(context!!, "$baseName.sprites")
            val tilesMap = GDXParserHelper.createTiles(spriteDefinition, atlasTexture)
            val animationDefinition = readTextFileFromAssets(context, "$baseName.animations")
            GDXParserHelper.createAnimations(animationDefinition, tilesMap, atlasTexture)
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            null
        }
    }
}