package com.abubusoft.xenon.mesh.tiledmaps

/**
 * Indica quale dimensione (altezza, larghezza) prendere come riferimento per riempire lo schermo.
 * Ad esempio se selezioniamo FILL_HEIGHT, vuol dire che lo schermo sarà riempito in altezza.
 *
 * @author Francesco Benincasa
 */
enum class TiledMapFillScreenType {
    /**
     * cerca di visualizzare completamente in altezza la mappa
     */
    FILL_HEIGHT,

    /**
     * cerca di visualizzare completamente in larghezza la mappa
     */
    FILL_WIDTH,

    /**
     * il numero di tile visibili viene definito programmaticamente. Indica che verrà specificato il numero
     * di tile visibili in altezza.
     */
    FILL_CUSTOM_HEIGHT,

    /**
     * il numero di tile visibili viene definito programmaticamente. Indica che verrà specificato il numero
     * di tile visibili in larghezza.
     */
    FILL_CUSTOM_WIDTH
}