/**
 *
 */
package com.abubusoft.xenon.texture

import android.content.Context
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.texture.DynamicTexture.DynamicTextureController

/**
 *
 *
 * Texture che può essere ricaricata durante l'esecuzione dell'applicazione.
 *
 *
 *
 *
 *
 * La texture ricaricabile si basa su un triplo buffering di texture: infatti
 * una viene usata per il disegno, un'altra viene utilizzata per il caricamento
 * della texture ed un'altra viene utilizzata per iniziare il successivo cambio,
 * eventualmente.
 *
 *
 *
 * Questo codice lo si mette ad esempio in application.onCreate: serve a creare
 * la texture dinamica, inizializzarlo e definire il controller.
 *
 *
 *
 * <pre>
 * dynamicTexture = tm.createDynamicTexture(context(), tm.createTextureFromResourceId(context(), config.planet.getResourceId(&quot;planet_color&quot;), to));
 *
 * dynamicTexture.init(DynamicTextureValues.build().loadResourceStrings(sequenza), to, new DynamicTextureController() {
 *
 * &#064;Override
 * public boolean onCheckForUpdate(long enlapsedTime) {
 * if (changeTextureTimer.getNormalizedEnlapsedTime() == 1f &amp;&amp; done) {
 * Logger.fatal(&quot;readyToSwapTexture &gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt; YYESS&quot;);
 * done = false;
 * return true;
 * } else {
 * return false;
 * }
 * }
 *
 * &#064;Override
 * public void onTextureReady(Texture texture) {
 * Logger.fatal(&quot;onTextureReady -----------------------&quot;);
 * done = true;
 * changeTextureTimer.start();
 *
 * }
 *
 * });
</pre> *
 *
 *
 * Nel onDraw dell'applicazione poi metti
 *
 *
 *
 * <pre>
 * dynamicTexture.update(context(), enlapsedTime);
 * ...
 * planetShader.setTextureOfPlanet(dynamicTexture.getCurrentTexture());
</pre> *
 *
 * @author Francesco Benincasa
 * @see DynamicTextureController
 *
 * @see DynamicTextureTimerController
 */
class DynamicTexture internal constructor(tx1: Texture?, tx2: Texture?, tx3: Texture?) {
    /**
     *
     *
     * Gestore del cambiamento delle texture dinamiche. Consente di decidere
     * cosa fare quando viene fatto il controllo sulla texture
     *
     * @author Francesco Benincasa
     */
    interface DynamicTextureController {
        /**
         *
         *
         * Dato il tempo trascorso dall'ultimo controllor, restituisce true quando bisogna cambiare la texture.
         *
         *
         * @return
         */
        fun onCheckForUpdate(enlapsedTime: Long): Boolean

        /**
         *
         * Forza l'aggiornamento della texture.
         *
         *
         * @return
         */
        fun forceUpdate(): Boolean

        /**
         *
         *
         * Invocato quando la texture è stata caricata.
         *
         *
         * @param texture
         */
        fun onTextureReady(texture: Texture?)
    }

    /**
     * indice corrente per la visualizzazione della texture
     */
    var textureCurrentIndex: Int

    /**
     * indice della texture da caricare
     */
    var textureLoadIndex: Int

    /**
     *
     */
    var options: TextureOptions? = null

    /**
     *
     *
     *
     */
    var reload: DynamicTextureValues? = null

    /**
     *
     *
     * elenco delle texture usate come buffering
     *
     */
    var textures: Array<Texture?>

    /**
     *
     */
    var controller: DynamicTextureController? = null

    /**
     *
     */
    var textureLoaderOptions: TextureReplaceOptions? = null

    /**
     *
     *
     * Costruttore. Le 3 texture sono state già definite nel texture manager.
     *
     *
     * @param tx1
     * @param tx2
     * @param tx3
     * @param textureOptionsValue
     */
    init {
        textures = arrayOfNulls(3)
        textures[0] = tx1
        textures[1] = tx2
        textures[2] = tx3
        textureCurrentIndex = 0
        textureLoadIndex = 1
    }

    /**
     *
     *
     * Configura la texture dinamica.
     *
     *
     * @param values       valori che devono essere caricati di volta in volta
     * @param optionsValue texture options da applicare alle varie texture
     * @param controlValue controller che regola il caricamento delle texture
     */
    fun init(values: DynamicTextureValues, optionsValue: TextureOptions?, controlValue: DynamicTextureController?) {
        options = optionsValue
        reload = values
        controller = controlValue
        textureLoaderOptions = TextureReplaceOptions.Companion.build().asyncLoaderListener(TextureAsyncLoaderListener { texture: Texture ->
            Logger.info("Texture %s loaded %s (info %s) index %s", texture.name, reload!!.currentIndexToLoad, texture.info != null, texture.index)

            // indice delle texture nelle quali rispettivamente corrente nel
            // quale caricare
            textureCurrentIndex = (textureCurrentIndex + 1) % textures.size
            textureLoadIndex = (textureLoadIndex + 1) % textures.size

            // posizione nei valori da ricaricare
            reload!!.currentIndexToLoad = (reload!!.currentIndexToLoad + 1) % values.strings.size
            controlValue?.onTextureReady(texture)
        })
    }

    /**
     *
     *
     * Esegue il controllo per vedere se dobbiamo cambiare texture. Questo
     * metodo esegue in modo asinc l'eventuale caricamento delle texture, se il
     * modulo è abilitato.
     *
     *
     * @param context
     * @param enlapsedTime tempo in millisecondi
     */
    fun update(context: Context, enlapsedTime: Long) {
        if (controller != null && controller!!.onCheckForUpdate(enlapsedTime)) {
            // ASSERT: è tempo di cambiare
            when (reload!!.load) {
                DynamicTextureValues.TextureLoadType.ASSETS_FILE -> TextureManager.Companion.instance().replaceTextureFromAssetsFile(
                    textures[textureLoadIndex]!!.index, context, reload!!.strings[reload!!.currentIndexToLoad], options, textureLoaderOptions
                )
                DynamicTextureValues.TextureLoadType.BITMAP -> TextureManager.Companion.instance().replaceTextureFromBitmap(
                    textures[textureLoadIndex]!!.index, context, reload!!.bitmaps[reload!!.currentIndexToLoad], options, textureLoaderOptions
                )
                DynamicTextureValues.TextureLoadType.FILE -> TextureManager.Companion.instance().replaceTextureFromFile(
                    textures[textureLoadIndex]!!.index, context, reload!!.strings[reload!!.currentIndexToLoad], options, textureLoaderOptions
                )
                DynamicTextureValues.TextureLoadType.RESOURCE_ID -> TextureManager.Companion.instance().replaceTextureFromResourceId(
                    textures[textureLoadIndex]!!.index, context, reload!!.resourceIds[reload!!.currentIndexToLoad], options, textureLoaderOptions
                )
                DynamicTextureValues.TextureLoadType.RESOURCE_STRING -> TextureManager.Companion.instance().replaceTextureFromResourceString(
                    textures[textureLoadIndex]!!.index, context, reload!!.strings[reload!!.currentIndexToLoad], options, textureLoaderOptions
                )
            }
        }
    }

    val currentTexture: Texture?
        get() = textures[textureCurrentIndex]
}