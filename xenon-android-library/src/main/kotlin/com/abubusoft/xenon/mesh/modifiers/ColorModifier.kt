package com.abubusoft.xenon.mesh.modifiers

import android.graphics.Color
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshDrawModeType
import com.abubusoft.xenon.mesh.MeshSprite

/**
 * Dato uno shape, provvede a modificarne i colori per vertice. I colori dei vertici vengono definiti per
 *
 * @author Francesco Benincasa
 */
object ColorModifier {
    /**
     * numero di elementi negli array relativi ad un singolo vertice/colore (rgba)
     */
    const val COLOR_ELEMENTS = 4

    /**
     * dato un componente di colore da 0 a 256, lo normalizza da 0 a 1.
     */
    const val COLOR_NORMALIZER_MULTIPLER = 1f / 256f

    /**
     * Dato uno sprite, lo definisce come uno
     *
     * @param shape
     * @param upperColor
     * @param lowerColor
     */
    fun setVerticalGradientColors(shape: MeshSprite, upperColor: Int, lowerColor: Int) {
        when (shape.drawMode) {
            MeshDrawModeType.INDEXED_TRIANGLES -> {

                // ci sono 4 triangoli
                val colors = intArrayOf(upperColor, lowerColor, lowerColor, upperColor)
                setColors(shape, colors, true)
            }
            MeshDrawModeType.TRIANGLES -> {

                // ce ne sono 6
                val colors = intArrayOf(upperColor, lowerColor, lowerColor, lowerColor, upperColor, upperColor)
                setColors(shape, colors, true)
            }
            else -> throw XenonRuntimeException("setVerticalGradientColors not implemented for drawMode " + shape.drawMode)
        }
    }

    /**
     * @param shape
     * @param upperLeftColor
     * @param upperRightColor
     * @param bottomLeftColor
     * @param bottomRightColor
     */
    fun setColors(shape: MeshSprite, upperLeftColor: Int, upperRightColor: Int, bottomLeftColor: Int, bottomRightColor: Int) {
        when (shape.drawMode) {
            MeshDrawModeType.INDEXED_TRIANGLES -> {

                // ci sono 4 vertici
                val colors = intArrayOf(upperLeftColor, upperRightColor, upperLeftColor, bottomRightColor)
                setColors(shape, colors, true)
            }
            MeshDrawModeType.TRIANGLES -> {

                // ci sono 6 vertici
                val colors = intArrayOf(upperLeftColor, bottomLeftColor, bottomRightColor, bottomRightColor, upperLeftColor, upperRightColor)
                setColors(shape, colors, true)
            }
            else -> throw XenonRuntimeException("setColors not implemented for drawMode " + shape.drawMode)
        }
    }

    /**
     * Dato uno shape, imposta per tutti i vertici lo stesso colore.
     *
     * Vengono impostati `colorCurrentValue` e `colorValue`.
     *
     * @param shape
     * @param color
     */
    fun setColor(shape: Mesh, color: Int, update: Boolean) {
        val n = shape.vertexCount * COLOR_ELEMENTS
        var i = 0
        while (i < n) {
            shape.colors!!.components!![i] = Color.red(color) * COLOR_NORMALIZER_MULTIPLER
            shape.colors!!.components!![i + 1] = Color.green(color) * COLOR_NORMALIZER_MULTIPLER
            shape.colors!!.components!![i + 2] = Color.blue(color) * COLOR_NORMALIZER_MULTIPLER
            shape.colors!!.components!![i + 3] = Color.alpha(color) * COLOR_NORMALIZER_MULTIPLER
            i += 4
        }
        if (update) shape.colors!!.update()
    }

    /**
     *
     *
     * Dato uno shape, imposta per tutti i vertici lo stesso colore.
     *
     *
     *
     * Vengono impostati `colorCurrentValue` e `colorValue`.
     *
     *
     *
     * **Reso private per evitare casini con le dimensioni dell'array di colori.
     ** *
     *
     * @param shape
     * @param color
     */
    private fun setColors(shape: Mesh, colors: IntArray, update: Boolean) {
        val n = shape.vertexCount * COLOR_ELEMENTS
        var colorI = 0
        var value: Int
        var i = 0
        while (i < n) {
            value = colors[colorI]
            shape.colors!!.components!![i] = Color.red(value) * COLOR_NORMALIZER_MULTIPLER
            shape.colors!!.components!![i + 1] = Color.green(value) * COLOR_NORMALIZER_MULTIPLER
            shape.colors!!.components!![i + 2] = Color.blue(value) * COLOR_NORMALIZER_MULTIPLER
            shape.colors!!.components!![i + 3] = Color.alpha(value) * COLOR_NORMALIZER_MULTIPLER
            colorI = (colorI + 1) % colors.size
            i += COLOR_ELEMENTS
        }
        if (update) shape.colors!!.update()
    }
}