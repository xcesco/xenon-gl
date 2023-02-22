package com.abubusoft.xenon.texture

/**
 * Texture suddivisa in rettangoli (tipicamente quadrati) di eguali dimensioni.
 *
 * @author Francesco Benincasa
 */
class AtlasTexture internal constructor(texture: Texture, options: AtlasTextureOptions) : Texture(texture.name, texture.bindingId) {
    /**
     * indice colonna del tile selezionato
     */
    var selectedColumn: Int

    /**
     * indice riga del tile selezionato
     */
    var selectedRow: Int

    /**
     * numero totale di righe
     */
    var rowsCount: Int

    /**
     * numero totale di colonne
     */
    var columnsCount: Int

    /**
     * larghezza del tile
     */
    val tileWidth: Int

    /**
     * altezza del tile
     */
    val tileHeight: Int

    /**
     * bordo di ogni tile
     */
    val spacing: Int

    /**
     * margine della texture
     */
    val margin: Int

    /**
     * @param bindingId
     * @param options
     * @param dimension
     */
    init {
        updateInfo(texture.info)
        index = texture.index
        selectedColumn = options.selectedColumn
        selectedRow = options.selectedRow
        tileWidth = options.tileWidth
        tileHeight = options.tileHeight
        spacing = options.spacing
        margin = options.margin
        rowsCount = info.dimension.height / (tileHeight + spacing)
        columnsCount = info.dimension.width / (tileWidth + spacing)
    }

    /**
     *
     * Imposta l'elemento dell'atlas selezionato.
     *
     *
     * Dopo aver invocato questo metodo, è possibile recuperare le coordinate UV del frame.
     *
     * @param row
     * riga
     * @param col
     * colonna
     */
    fun selectedCurrentFrame(row: Int, col: Int) {
        selectedColumn = col
        selectedRow = row
    }

    val coordStartX: Float
        get() {
            val value = (margin + (tileWidth + spacing) * selectedColumn).toFloat()
            return value / info.dimension.width
        }
    val coordStartY: Float
        get() {
            val value = (margin + (tileHeight + spacing) * selectedRow).toFloat()
            return value / info.dimension.height
        }
    val coordEndX: Float
        get() {
            val value = (margin + (tileWidth + spacing) * selectedColumn + tileWidth).toFloat()
            return value / info.dimension.width
        }
    val coordEndY: Float
        get() {
            val value = (margin + (tileHeight + spacing) * selectedRow + tileHeight).toFloat()
            return value / info.dimension.height
        }
}