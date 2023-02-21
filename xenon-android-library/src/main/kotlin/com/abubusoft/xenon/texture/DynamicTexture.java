/**
 *
 */
package com.abubusoft.xenon.texture;

import com.abubusoft.kripton.android.Logger;

import android.content.Context;

/**
 * <p>
 * Texture che può essere ricaricata durante l'esecuzione dell'applicazione.
 * </p>
 * <p>
 * <p>
 * La texture ricaricabile si basa su un triplo buffering di texture: infatti
 * una viene usata per il disegno, un'altra viene utilizzata per il caricamento
 * della texture ed un'altra viene utilizzata per iniziare il successivo cambio,
 * eventualmente.
 * </p>
 * <p>
 * Questo codice lo si mette ad esempio in application.onCreate: serve a creare
 * la texture dinamica, inizializzarlo e definire il controller.
 * </p>
 * <p>
 * <pre>
 * dynamicTexture = tm.createDynamicTexture(context(), tm.createTextureFromResourceId(context(), config.planet.getResourceId(&quot;planet_color&quot;), to));
 *
 * dynamicTexture.init(DynamicTextureValues.build().loadResourceStrings(sequenza), to, new DynamicTextureController() {
 *
 * 	&#064;Override
 * 	public boolean onCheckForUpdate(long enlapsedTime) {
 * 		if (changeTextureTimer.getNormalizedEnlapsedTime() == 1f &amp;&amp; done) {
 * 			Logger.fatal(&quot;readyToSwapTexture &gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt; YYESS&quot;);
 * 			done = false;
 * 			return true;
 *        } else {
 * 			return false;
 *        }
 *    }
 *
 * 	&#064;Override
 * 	public void onTextureReady(Texture texture) {
 * 		Logger.fatal(&quot;onTextureReady -----------------------&quot;);
 * 		done = true;
 * 		changeTextureTimer.start();
 *
 *    }
 *
 * });
 * </pre>
 * <p>
 * Nel onDraw dell'applicazione poi metti
 * </p>
 * <p>
 * <pre>
 * dynamicTexture.update(context(), enlapsedTime);
 * ...
 * planetShader.setTextureOfPlanet(dynamicTexture.getCurrentTexture());
 * </pre>
 *
 * @author Francesco Benincasa
 * @see DynamicTextureController
 * @see DynamicTextureTimerController
 */
public class DynamicTexture {

    /**
     * <p>
     * Gestore del cambiamento delle texture dinamiche. Consente di decidere
     * cosa fare quando viene fatto il controllo sulla texture
     *
     * @author Francesco Benincasa
     */
    public interface DynamicTextureController {
        /**
         * <p>
         * Dato il tempo trascorso dall'ultimo controllor, restituisce true quando bisogna cambiare la texture.
         * </p>
         *
         * @return
         */
        boolean onCheckForUpdate(long enlapsedTime);

        /**
         * <p>Forza l'aggiornamento della texture.
         * </p>
         *
         * @return
         */
        boolean forceUpdate();

        /**
         * <p>
         * Invocato quando la texture è stata caricata.
         * </p>
         *
         * @param texture
         */
        void onTextureReady(Texture texture);

    }

    /**
     * indice corrente per la visualizzazione della texture
     */
    int textureCurrentIndex;

    /**
     * indice della texture da caricare
     */
    int textureLoadIndex;

    /**
     *
     */
    TextureOptions options;

    /**
     * <p>
     * </p>
     */
    DynamicTextureValues reload;

    /**
     * <p>
     * elenco delle texture usate come buffering
     * </p>
     */
    Texture[] textures;

    /**
     *
     */
    DynamicTextureController controller;

    /**
     *
     */
    TextureReplaceOptions textureLoaderOptions;

    /**
     * <p>
     * Costruttore. Le 3 texture sono state già definite nel texture manager.
     * </p>
     *
     * @param tx1
     * @param tx2
     * @param tx3
     * @param textureOptionsValue
     */
    DynamicTexture(Texture tx1, Texture tx2, Texture tx3) {
        textures = new Texture[3];

        textures[0] = tx1;
        textures[1] = tx2;
        textures[2] = tx3;

        textureCurrentIndex = 0;
        textureLoadIndex = 1;
    }

    /**
     * <p>
     * Configura la texture dinamica.
     * </p>
     *
     * @param values       valori che devono essere caricati di volta in volta
     * @param optionsValue texture options da applicare alle varie texture
     * @param controlValue controller che regola il caricamento delle texture
     */
    public void init(final DynamicTextureValues values, final TextureOptions optionsValue, final DynamicTextureController controlValue) {
        options = optionsValue;
        reload = values;
        controller = controlValue;
        textureLoaderOptions = TextureReplaceOptions.build().asyncLoaderListener((Texture texture) -> {
            Logger.info("Texture %s loaded %s (info %s) index %s", texture.name, reload.currentIndexToLoad, texture.info != null, texture.index);

            // indice delle texture nelle quali rispettivamente corrente nel
            // quale caricare
            textureCurrentIndex = (textureCurrentIndex + 1) % textures.length;
            textureLoadIndex = (textureLoadIndex + 1) % textures.length;

            // posizione nei valori da ricaricare
            reload.currentIndexToLoad = (reload.currentIndexToLoad + 1) % values.strings.length;

            if (controlValue != null)
                controlValue.onTextureReady(texture);

        });
    }

    /**
     * <p>
     * Esegue il controllo per vedere se dobbiamo cambiare texture. Questo
     * metodo esegue in modo asinc l'eventuale caricamento delle texture, se il
     * modulo è abilitato.
     * </p>
     *
     * @param context
     * @param enlapsedTime tempo in millisecondi
     */
    public void update(Context context, long enlapsedTime) {
        if (controller != null && controller.onCheckForUpdate(enlapsedTime)) {
            // ASSERT: è tempo di cambiare
            switch (reload.load) {
                case ASSETS_FILE:
                    TextureManager.instance().replaceTextureFromAssetsFile(textures[textureLoadIndex].index, context, reload.strings[reload.currentIndexToLoad], options, textureLoaderOptions);
                    break;
                case BITMAP:
                    TextureManager.instance().replaceTextureFromBitmap(textures[textureLoadIndex].index, context, reload.bitmaps[reload.currentIndexToLoad], options, textureLoaderOptions);
                    break;
                case FILE:
                    TextureManager.instance().replaceTextureFromFile(textures[textureLoadIndex].index, context, reload.strings[reload.currentIndexToLoad], options, textureLoaderOptions);
                    break;
                case RESOURCE_ID:
                    TextureManager.instance().replaceTextureFromResourceId(textures[textureLoadIndex].index, context, reload.resourceIds[reload.currentIndexToLoad], options, textureLoaderOptions);
                    break;
                case RESOURCE_STRING:
                    TextureManager.instance().replaceTextureFromResourceString(textures[textureLoadIndex].index, context, reload.strings[reload.currentIndexToLoad], options, textureLoaderOptions);
                    break;
            }
        }
    }

    public Texture getCurrentTexture() {
        return textures[textureCurrentIndex];
    }

}
