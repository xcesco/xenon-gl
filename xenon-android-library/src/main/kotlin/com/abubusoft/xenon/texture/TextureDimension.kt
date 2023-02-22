package com.abubusoft.xenon.texture

/**
 * Indica in termini di pixel ed in termini di coordinate UW normalizzate (il cui range è 0f..1f), le dimensioni
 * massime valide della texture.
 *
 * Size rappresenta le dimensioni reali della texture.
 *
 * @author Francesco Benincasa
 */
class TextureDimension(
    /**
     * dimensioni in pixel della texture
     */
    val width: Int,
    /**
     * dimensioni in pixel della texture
     */
    val height: Int,
    /**
     * dimensione massima normalizzata tra 0 e 1
     */
    val normalizedMaxWidth: Float,
    /**
     * dimensione massima normalizzata tra 0 e 1
     */
    val normalizedMaxHeight: Float,
    /**
     * dimensione reale della texture
     */
    val size: TextureSizeType
)