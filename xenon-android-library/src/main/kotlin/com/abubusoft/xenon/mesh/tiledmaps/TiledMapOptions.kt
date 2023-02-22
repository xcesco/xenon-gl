/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps

/**
 * Opzioni di visualizzazione relative alle tiled map.
 *
 * @author Francesco Benincasa
 */
/**
 * @author Francesco Benincasa
 */
class TiledMapOptions {
    /**
     * Se true consente di creare un controller.
     */
    var createController = false

    /**
     * posizione iniziale sulla mappa
     */
    var startPosition = TiledMapPositionType.MIDDLE_CENTER

    /**
     * Indica quale dimensione (altezza, larghezza) prendere come riferimento per riempire lo schermo. Ad esempio se selezioniamo FILL_HEIGHT, vuol dire che lo schermo sarà riempito in altezza.
     *
     */
    var fillScreenType = TiledMapFillScreenType.FILL_HEIGHT

    /**
     * Numero di tile visibili per dimensione. Viene utilizzato solo se fillScreenType è definito come [TiledMapFillScreenType.FILL_CUSTOM_HEIGHT] o [TiledMapFillScreenType.FILL_CUSTOM_WIDTH]
     */
    var visibleTiles = 0

    /**
     * indica se lo scroll orizzontale è consentito o meno.
     */
    var scrollHorizontalLocked = true

    /**
     * indica se lo scroll verticale è consentito o meno.
     */
    var scrollVerticalLocked = true

    /**
     * a prescindere dalla dimensione presa come riferimento, questa percentuale indica quanto della dimensione è resa visibile sullo schermo.
     */
    var visiblePercentage = 1.0f
    fun startPosition(value: TiledMapPositionType): TiledMapOptions {
        startPosition = value
        return this
    }

    fun scrollHorizontalLocked(value: Boolean): TiledMapOptions {
        scrollHorizontalLocked = value
        return this
    }

    fun scrollVerticalLocked(value: Boolean): TiledMapOptions {
        scrollVerticalLocked = value
        return this
    }

    fun fillScreenType(value: TiledMapFillScreenType): TiledMapOptions {
        fillScreenType = value
        return this
    }

    fun fillScreenType(value: TiledMapFillScreenType, visibleTiles: Int): TiledMapOptions {
        fillScreenType = value
        this.visibleTiles = visibleTiles
        return this
    }

    /**
     * a prescindere dalla dimensione presa come riferimento, questa percentuale indica quanto della dimensione è resa visibile sullo schermo.
     *
     * Questo parametro non è in sostituzione del numero di tiles da visualizzare. Si va ad aggiungere a tale definizione.
     *
     * @param value
     * @return this
     */
    fun visiblePercentage(value: Float): TiledMapOptions {
        visiblePercentage = value
        return this
    }

    /**
     *
     *
     * Numero di tile visibili per dimensione. Viene utilizzato solo se fillScreenType è definito come [TiledMapFillScreenType.FILL_CUSTOM_HEIGHT] o [TiledMapFillScreenType.FILL_CUSTOM_WIDTH].
     *
     *
     *
     *
     * Da tenere in considerazione che per il sistema lo schermo è sempre da considerarsi come un quadrato avente come lato la dimensione dello schermo più grande.
     *
     *
     */
    fun visibileTiles(value: Int): TiledMapOptions {
        visibleTiles = value
        return this
    }

    /**
     * Se true consente di creare un controller.
     *
     * @param value
     * @return
     */
    fun createController(value: Boolean): TiledMapOptions {
        createController = value
        return this
    }

    companion object {
        /**
         * build della configurazione di base.
         *
         *
         *  * **createController**: true - crea il controller
         *  * **fillScreenType**: [TiledMapFillScreenType.FILL_HEIGHT]
         *  * **scrollHorizontalLocked**: true - lo scroll orizzontale
         *  * **scrollVerticalLocked**: true - lo scroll verticale
         *  * **startPosition**: [TiledMapPositionType.MIDDLE_CENTER]
         *  * **visibileTiles**: 0 - Numero di tile visibili per dimensione. Viene utilizzato solo se fillScreenType è definito come [TiledMapFillScreenType.FILL_CUSTOM]
         *  * **visiblePercentage**: 1 - questa percentuale indica quanto della dimensione è resa visibile sullo schermo.
         *
         *
         * @return
         */
        fun build(): TiledMapOptions {
            return TiledMapOptions().createController(true)
        }
    }
}