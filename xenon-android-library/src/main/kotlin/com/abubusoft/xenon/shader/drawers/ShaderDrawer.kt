package com.abubusoft.xenon.shader.drawers

import android.opengl.GLES20
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.vbo.BufferAllocationType

/**
 * @author Francesco Benincasa
 */
object ShaderDrawer {
    /**
     *
     *
     * Dato uno shader ed uno shape, lo disegna.
     *
     *
     * @param shader
     * @param shape
     */
    fun draw(shader: Shader, shape: Mesh, modelViewProjection: Matrix4x4) {
        // matrice di proiezione
        shader.setModelViewProjectionMatrix(modelViewProjection.asFloatBuffer())
        if (shape.indexesEnabled) {
            if (shape.indexes.allocation === BufferAllocationType.CLIENT) {
                GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, shape.indexes.buffer)
            } else {
                shader.setIndexBuffer(shape.indexes)
                GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0)
                shader.unsetIndexBuffer(shape.indexes)
            }
        } else {
            GLES20.glDrawArrays(shape.drawMode.value, 0, shape.vertexCount)
        }
    }
}