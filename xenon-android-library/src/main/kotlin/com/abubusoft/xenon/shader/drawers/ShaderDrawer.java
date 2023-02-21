package com.abubusoft.xenon.shader.drawers;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.vbo.BufferAllocationType;

import android.opengl.GLES20;

/**
 * @author Francesco Benincasa
 * 
 */
public abstract class ShaderDrawer {

	/**
	 * <p>
	 * Dato uno shader ed uno shape, lo disegna.
	 * </p>
	 * 
	 * @param shader
	 * @param shape
	 */
	public static void draw(Shader shader, Mesh shape, Matrix4x4 modelViewProjection) {
		// matrice di proiezione
		shader.setModelViewProjectionMatrix(modelViewProjection.asFloatBuffer());

		if (shape.indexesEnabled) {
			if (shape.indexes.allocation==BufferAllocationType.CLIENT)
			{
				GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, shape.indexes.buffer);
			} else {
				shader.setIndexBuffer(shape.indexes);
				GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0);
				shader.unsetIndexBuffer(shape.indexes);
			}
		} else {
			GLES20.glDrawArrays(shape.drawMode.value, 0, shape.vertexCount);
		}

	}

}
