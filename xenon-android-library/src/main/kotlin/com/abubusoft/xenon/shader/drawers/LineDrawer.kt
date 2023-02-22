/**
 *
 */
package com.abubusoft.xenon.shader.drawers

import android.graphics.Color
import android.opengl.GLES20
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshDrawModeType
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.shader.ShaderLine
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.vbo.BufferAllocationType

/**
 * @author Francesco Benincasa
 */
class LineDrawer {
    protected var shader: ShaderLine
    fun begin() {
        shader.use()
    }

    var lineWidth: Int
    private var a = 0
    private var r = 0
    private var g = 0
    private var b = 0

    init {
        shader = ShaderManager.instance().createShaderLine()
        lineWidth = 1
        setColor(Color.RED)
    }

    fun setLineWidth(width: Int) {
        lineWidth = XenonMath.clampI(width, XenonGL.lineWidthRange[0], XenonGL.lineWidthRange[1])
    }

    fun end() {}

    /**
     * Imposta il colore da usare per il drawer al prossimo draw. Se non viene
     * cambiato il colore, questo rimane.
     *
     * @param colorValue
     */
    fun setColor(colorValue: Int) {
        a = Color.alpha(colorValue)
        r = Color.red(colorValue)
        g = Color.green(colorValue)
        b = Color.blue(colorValue)
    }

    /**
     *
     *
     * Dato uno shader ed uno shape, lo disegna.
     *
     *
     * @param mesh
     * @param modelViewProjection
     */
    fun draw(mesh: Mesh, modelViewProjection: Matrix4x4) {
        val mode = MeshDrawModeType.LINES

        // imposta la larghezza delle linee.
        GLES20.glLineWidth(lineWidth.toFloat())

        // array di vertici
        shader.setVertexCoordinatesArray(mesh.vertices)
        // matrice di proiezione
        shader.setModelViewProjectionMatrix(modelViewProjection.asFloatBuffer())
        shader.setColor(a, r, g, b)
        if (mesh.indexesEnabled) {
            if (mesh.indexes.allocation === BufferAllocationType.CLIENT) {
                GLES20.glDrawElements(mode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, mesh.indexes.buffer)
            } else {
                // impostiamo gli indici
                shader.setIndexBuffer(mesh.indexes)
                GLES20.glDrawElements(mode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0)
                shader.unsetIndexBuffer(mesh.indexes)
            }
        } else {
            GLES20.glDrawArrays(mode.value, 0, mesh.vertexCount)
        }
    }
}