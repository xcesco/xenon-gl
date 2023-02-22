package com.abubusoft.xenon.mesh.tiledmaps

/**
 *
 *
 * Rappresenta un oggetto base, entità che viene definita tipicamente negli object layer ma che può rappresentare anche una classe (template) di object o uno sprite object.
 *
 *
 *
 *
 * Molto importante è il concetto di **marker**. Ad ogni ciclo di disegno, per come è composto il mondo box2d e per come può essere interrogato mediante una query AABB, un
 * oggetto può essere rilevato più volte, nel caso in cui vi siano associate più fixture. Per evitare disegni duplicati, si è pensato di introdurre una sorta di marcatura temporale
 * che indica l'ultima volta che l'oggetto è stato disegnato. Con questo marker è possibile determinare se l'oggetto è stato già disegnato per questo frame o meno.
 *
 *
 * @author Francesco Benincasa
 */
abstract class ObjBase : PropertiesCollector() {
    /**
     *
     * Indica se l'oggetto è visibile o meno.
     */
    var visible = false

    /**
     * Categorie di oggetti
     *
     * @author Francesco Benincasa
     */
    enum class CategoryType {
        /**
         * oggetto di tipo classe
         */
        CLASS,

        /**
         * istanza di classe
         */
        INSTANCE,

        /**
         * sprite
         */
        SPRITE,

        /**
         * definizione
         */
        DEFINITION
    }

    /**
     *
     *
     * categoria di instanza. Consente di specificare il tipo di oggetto mediante [CategoryType].
     *
     */
    var category: CategoryType? = null

    /**
     * nome della classe
     */
    var name: String? = null

    /**
     * coordinate left dell'oggetto. Le coordinate sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
     */
    var x = 0f

    /**
     * coordinate top dell'oggetto. Le coordinate sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
     */
    var y = 0f

    /**
     * tipo di body
     */
    var type: String? = null

    /**
     * larghezza. Le dimensioni sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
     */
    var width = 0f

    /**
     * altezza. Le dimensioni sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
     */
    var height = 0f

    /**
     *
     *
     * Viene utilizzato per indicare qual'è stato l'ultimo frame per il quale questo oggetto è stato disegnato.
     *
     */
    protected var marker = 0

    /**
     *
     *
     * Aggiorna il marker di questa istanza portandolo ad assumere lo stesso valore del globalFrameMarker.
     *
     */
    fun updateFrameMarker() {
        marker = globalFrameMarker
    }

    /**
     *
     *
     * L'oggetto è stato già marcato per questo frame.
     *
     *
     * @return
     * true se per questo frame l'oggetto è stato già marcato
     */
    val isFrameMarkerUpdated: Boolean
        get() = marker == globalFrameMarker

    companion object {
        /**
         *
         *
         * contatore generale dei frame. Va da 1 a 1000.
         *
         */
        protected var globalFrameMarker = 1

        /**
         *
         *
         * Reinizializza il frame marker.
         *
         */
        fun resetFrameMarker() {
            globalFrameMarker = 1
        }

        /**
         *
         *
         * aggiorna ad inizio frame il global frame marker.
         *
         */
        fun updateGlobalFrameMarker() {
            (globalFrameMarker = globalFrameMarker + 1) % 1000

            // se è 0, valore usato per i marker non ancora usati
            if (globalFrameMarker == 0) globalFrameMarker = 1
        }
    }
}