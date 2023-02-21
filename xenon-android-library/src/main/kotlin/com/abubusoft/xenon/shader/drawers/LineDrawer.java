/**
 * 
 */
package com.abubusoft.xenon.shader.drawers;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshDrawModeType;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.shader.ShaderLine;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.vbo.BufferAllocationType;

import android.graphics.Color;
import android.opengl.GLES20;

/**
 * @author Francesco Benincasa
 * 
 */
public class LineDrawer {

	protected ShaderLine shader;

	public LineDrawer() {
		shader = ShaderManager.instance().createShaderLine();
		lineWidth=1;
		setColor(Color.RED);
	}

	public void begin() {
		shader.use();
	}

	int lineWidth;
	private int a;
	private int r;
	private int g;
	private int b;

	public void setLineWidth(int width) {
		lineWidth = XenonMath.clampI(width, XenonGL.lineWidthRange[0], XenonGL.lineWidthRange[1]);
	}

	public void end() {
	}

	/**
	 * Imposta il colore da usare per il drawer al prossimo draw. Se non viene
	 * cambiato il colore, questo rimane.
	 * 
	 * @param colorValue
	 */
	public void setColor(int colorValue) {
		a = Color.alpha(colorValue);
		r = Color.red(colorValue);
		g = Color.green(colorValue);
		b = Color.blue(colorValue);
	}

	/**
	 * <p>
	 * Dato uno shader ed uno shape, lo disegna.
	 * </p>
	 * 
	 * @param mesh
	 * @param modelViewProjection
	 */
	public void draw(Mesh mesh, Matrix4x4 modelViewProjection) {
		MeshDrawModeType mode = MeshDrawModeType.LINES;

		// imposta la larghezza delle linee.
		GLES20.glLineWidth(lineWidth);

		// array di vertici
		shader.setVertexCoordinatesArray(mesh.vertices);
		// matrice di proiezione
		shader.setModelViewProjectionMatrix(modelViewProjection.asFloatBuffer());
		shader.setColor(a, r, g, b);

		if (mesh.indexesEnabled) {
			if (mesh.indexes.allocation == BufferAllocationType.CLIENT) {
				GLES20.glDrawElements(mode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, mesh.indexes.buffer);
			} else {
				// impostiamo gli indici
				shader.setIndexBuffer(mesh.indexes);
				GLES20.glDrawElements(mode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0);
				shader.unsetIndexBuffer(mesh.indexes);
			}
		} else {
			GLES20.glDrawArrays(mode.value, 0, mesh.vertexCount);
		}

	}
}
