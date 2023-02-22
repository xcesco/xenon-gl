package com.abubusoft.xenon.texture

/**
 * Opzioni di una texture tiled, quella utilizzata per creare delle tiles
 *
 * @author Francesco Benincasa
 */
class AtlasTextureOptions {
    /**
     * larghezza della tile
     */
    var tileWidth = 0

    /**
     * altezza della tile
     */
    var tileHeight = 0

    /**
     * row selezionata, (startX , )
     */
    var selectedRow = 0

    /**
     * colonna selezinata (  , startY)
     */
    var selectedColumn = 0

    /**
     * dimensioni in pixel del bordo (cornice) di ogni tiles
     */
    var spacing = 0

    /**
     * dimensioni in pixel del margine complessivo
     */
    var margin = 0

    /**
     * larghezza della tile
     */
    fun tileWidth(value: Int): AtlasTextureOptions {
        tileWidth = value
        return this
    }

    /**
     * altezza della tile
     */
    fun tileHeight(value: Int): AtlasTextureOptions {
        tileHeight = value
        return this
    }

    /**
     * row selezionata, (startX , )
     */
    fun selectedRow(value: Int): AtlasTextureOptions {
        selectedRow = value
        return this
    }

    /**
     * colonna selezinata (  , startY)
     */
    fun selectedColumn(value: Int): AtlasTextureOptions {
        selectedColumn = value
        return this
    }

    /**
     * dimensioni in pixel del bordo (cornice)
     */
    fun spacing(value: Int): AtlasTextureOptions {
        spacing = value
        return this
    }

    /**
     * dimensioni in pixel del margine (tra ogni tile)
     */
    fun margin(value: Int): AtlasTextureOptions {
        margin = value
        return this
    }

    companion object {
        /**
         * Build delle opzioni. configurazione di default:
         * tileWidth = 32
         * tileHeight= 32
         * selectedRow = 0
         * selectedColumn = 0
         * spacing = 0
         * margin = 0
         *
         * @return
         */
        fun build(): AtlasTextureOptions {
            // configurazione di default
            return AtlasTextureOptions().spacing(0).margin(0).selectedColumn(0).selectedRow(0).tileHeight(32).tileWidth(32)
        }
    }
}