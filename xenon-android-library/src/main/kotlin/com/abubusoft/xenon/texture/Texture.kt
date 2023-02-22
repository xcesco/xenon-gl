/**
 *
 */
package com.abubusoft.xenon.texture

/**
 *
 * Rappresenta un'immagine caricata in un contesto opengl come texture. Ogni texture viene caricata in un contesto opengl mediante
 * un **bindingId**, che serve internamente ad opengl. Il `TextureManager` provvede a caricare una bitmap, assegnarli
 * un bindingId e l'indice posizionale (**index**) della texture. Questo ultimo indice viene utilizzato spesso e volentieri per referenziare
 * la texture.
 *
 * <img src="doc-files/img00017.png"></img>
 *
 * <h2>Dimensioni texture</h2>
 *
 *
 * Le texture devono avere dimensioni potenze di 2, ovvero: 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024.
 *
 * Ci sono sicurametne in giro dei metodi per utilizzare immagini che rispettano tale vincolo, ma preferisco non perdere troppo tempo su questo.
 *
 * Attenzione che su alcuni dispositivi le texture con dimensioni non valide vengono visualizzate in bianco.
 *
 * <h2>Sistema di riferimento</h2>
 *
 *
 * Il sistema di coordinate usato è U,V, nell'intervallo (0,1). **L'origine del sistema di riferimento nella bitmap
 * usata è il vertice in basso a sinistra**.
 *
 * <img src="doc-files/textureCoords1.png"></img>
 *
 *
 * Vedi [qui](http://stackoverflow.com/questions/5585368/problems-using-wavefront-objs-texture-coordinates-in-android-opengl-es).
 *
 *
 *
 * your texture coordinate normalisation step is unnecessary — to the extent that I'm not sure why it's in there — and probably broken (what if xcoord is larger than ycoord on the first line, then smaller on the second?)
 * OBJ considers (0, 0) to be the top left of a texture, OpenGL considers it to be the bottom left, so unless you've set the texture matrix stack to invert texture coordinates in code not shown, you need to invert them yourself, e.g. textureCoordinatesMesh.add(1.0 - ycoord);
 *
 *
 * <img src="doc-files/textureCoords.png"></img>
 *
 *
 *
 *
 *
 * @author Francesco Benincasa
 *
 * @see TextureManager
 */
open class Texture(
    val name: String?,
    /**
     * binding id della texture. Non può essere final in quanto in caso di load può cambiare.
     */
    var bindingId: Int
) {
    /**
     * indice della texture
     */
    var index = 0

    /**
     *
     *
     * Informazioni sulla texture. Nel caso di texture caricate async, può essere nullo.
     *
     */
    var info: TextureInfo? = null

    /**
     *
     *
     * Indica se la texture è pronta ad essere usata
     *
     */
    var ready = false

    /**
     * Costruttore della texture. Dovrebbe essere il TextureManager ad utilizzare questo costruttore.
     *
     * @param nameValue
     * @param bindingId
     * @param dimension
     */
    init {
        updateInfo(null)
    }

    /**
     * Crea una nuova referenza
     *
     * @return
     */
    fun newReference(): TextureReference {
        return TextureReference(index)
    }

    /**
     *
     *
     * Serve a ricaricare la texture
     *
     */
    open fun reload() {}

    /**
     * Quando lo schermo si chiude e cerco di scaricare la texture
     */
    open fun unbind() {}

    /**
     *
     * Aggiorna le info della texture.
     * @param value
     */
    fun updateInfo(value: TextureInfo?) {
        info = value
        ready = value != null
    }

    companion object {
        /**
         *
         * Indice della texture non valido
         */
        const val INVALID_INDEX = -1
    }
}