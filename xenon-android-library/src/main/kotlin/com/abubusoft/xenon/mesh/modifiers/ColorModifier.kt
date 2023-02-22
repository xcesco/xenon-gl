package com.abubusoft.xenon.mesh.modifiers;

import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshSprite;

import android.graphics.Color;

/**
 * Dato uno shape, provvede a modificarne i colori per vertice. I colori dei vertici vengono definiti per
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class ColorModifier {

	/**
	 * numero di elementi negli array relativi ad un singolo vertice/colore (rgba)
	 */
	public static final int COLOR_ELEMENTS = 4;

	/**
	 * dato un componente di colore da 0 a 256, lo normalizza da 0 a 1.
	 */
	public static final float COLOR_NORMALIZER_MULTIPLER = 1f / 256f;

	/**
	 * Dato uno sprite, lo definisce come uno
	 * 
	 * @param shape
	 * @param upperColor
	 * @param lowerColor
	 */
	public static void setVerticalGradientColors(MeshSprite shape, int upperColor, int lowerColor) {
		switch (shape.drawMode) {
		case INDEXED_TRIANGLES: {
			// ci sono 4 triangoli
			int[] colors = { upperColor, lowerColor, lowerColor, upperColor };
			setColors(shape, colors, true);
			break;
		}
		case TRIANGLES: {
			// ce ne sono 6
			int[] colors = { upperColor, lowerColor, lowerColor, lowerColor, upperColor, upperColor };
			setColors(shape, colors, true);
			break;
		}
		default:
			// TODO implementare
			throw (new XenonRuntimeException("setVerticalGradientColors not implemented for drawMode " + shape.drawMode));
		}
	}

	/**
	 * @param shape
	 * @param upperLeftColor
	 * @param upperRightColor
	 * @param bottomLeftColor
	 * @param bottomRightColor
	 */
	public static void setColors(MeshSprite shape, int upperLeftColor, int upperRightColor, int bottomLeftColor, int bottomRightColor) {
		switch (shape.drawMode) {
		case INDEXED_TRIANGLES: {
			// ci sono 4 vertici
			int[] colors = { upperLeftColor, upperRightColor, upperLeftColor, bottomRightColor };
			setColors(shape, colors, true);
			break;
		}
		case TRIANGLES: {
			// ci sono 6 vertici
			int[] colors = { upperLeftColor, bottomLeftColor, bottomRightColor, bottomRightColor, upperLeftColor, upperRightColor };
			setColors(shape, colors, true);
			break;
		}
		default:
			// TODO implementare
			throw (new XenonRuntimeException("setColors not implemented for drawMode " + shape.drawMode));
		}
	}

	/**
	 * <p>
	 * Dato uno shape, imposta per tutti i vertici lo stesso colore.
	 * </p>
	 * <p>
	 * Vengono impostati <code>colorCurrentValue</code> e <code>colorValue</code>.
	 * </p>
	 * 
	 * @param shape
	 * @param color
	 */
	public static void setColor(Mesh shape, int color, boolean update) {
		int n = shape.vertexCount * COLOR_ELEMENTS;
		for (int i = 0; i < n; i += 4) {
			shape.colors.components[i] = Color.red(color) * COLOR_NORMALIZER_MULTIPLER;
			shape.colors.components[i + 1] = Color.green(color) * COLOR_NORMALIZER_MULTIPLER;
			shape.colors.components[i + 2] = Color.blue(color) * COLOR_NORMALIZER_MULTIPLER;
			shape.colors.components[i + 3] = Color.alpha(color) * COLOR_NORMALIZER_MULTIPLER;
		}

		if (update)
			shape.colors.update();
	}

	/**
	 * <p>
	 * Dato uno shape, imposta per tutti i vertici lo stesso colore.
	 * </p>
	 * <p>
	 * Vengono impostati <code>colorCurrentValue</code> e <code>colorValue</code>.
	 * </p>
	 * <p>
	 * <b>Reso private per evitare casini con le dimensioni dell'array di colori.
	 * </p>
	 * 
	 * @param shape
	 * @param color
	 */
	private static void setColors(Mesh shape, int[] colors, boolean update) {
		int n = shape.vertexCount * COLOR_ELEMENTS;

		int colorI = 0;
		int value;
		for (int i = 0; i < n; i += COLOR_ELEMENTS) {

			value = colors[colorI];
			shape.colors.components[i] = Color.red(value) * COLOR_NORMALIZER_MULTIPLER;
			shape.colors.components[i + 1] = Color.green(value) * COLOR_NORMALIZER_MULTIPLER;
			shape.colors.components[i + 2] = Color.blue(value) * COLOR_NORMALIZER_MULTIPLER;
			shape.colors.components[i + 3] = Color.alpha(value) * COLOR_NORMALIZER_MULTIPLER;

			colorI = (colorI + 1) % colors.length;
		}

		if (update)
			shape.colors.update();
	}
}
