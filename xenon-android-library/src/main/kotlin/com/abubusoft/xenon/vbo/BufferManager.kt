package com.abubusoft.xenon.vbo

import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType
import com.abubusoft.xenon.vbo.BufferHelper.bindBuffer
import com.abubusoft.xenon.vbo.BufferHelper.newBindingId

object BufferManager {
    /**
     * numero di vbo allocati in video memory
     */
    var vboToUnbind: Int
    var vbos: ArrayList<AbstractBuffer>

    init {
        vbos = ArrayList()
        vboToUnbind = 0
    }

    /**
     *
     *
     * Crea un vbo per i colori. Effettua il build ed il bind al contesto openGL del buffer.
     *
     *
     */
    @JvmStatic
    fun createColorBuffer(vertexCountValue: Int, allocationValue: BufferAllocationType): ColorBuffer {
        val item = ColorBuffer(vertexCountValue, allocationValue)
        bindBuffer(item)
        return item
    }

    /**
     *
     *
     * Crea un vbo. Effettua il build ed il bind al contesto openGL del buffer.
     *
     *
     * @param vertexCountValue
     * @param allocationValue
     * @return vertexBuffer
     */
    @JvmStatic
    fun createVertexBuffer(vertexCountValue: Int, allocationValue: BufferAllocationType): VertexBuffer {
        val item = VertexBuffer(vertexCountValue, allocationValue)
        bindBuffer(item)
        return item
    }

    /**
     * Crea un attribute vbo di tipo float e di dimensioni parametriche. Effettua il build ed il bind al contesto openGL del buffer.
     */
    @JvmStatic
    fun createAttributeBuffer(vertexCountValue: Int, vertexDimensions: AttributeDimensionType, allocationValue: BufferAllocationType): AttributeBuffer {
        val item = AttributeBuffer(vertexCountValue, vertexDimensions, allocationValue)
        bindBuffer(item)
        return item
    }

    /**
     * Crea un texture vbo. Effettua il build ed il bind al contesto openGL del buffer.
     */
    @JvmStatic
    fun createTextureBuffer(vertexCountValue: Int, allocationValue: BufferAllocationType): TextureBuffer {
        val item = TextureBuffer(vertexCountValue, allocationValue)
        bindBuffer(item)
        return item
    }

    /**
     * Crea un vbo. Effettua il build ed il bind al contesto openGL del buffer.
     */
    @JvmStatic
    fun createIndexBuffer(vertexCountValue: Int, allocationValue: BufferAllocationType): IndexBuffer {
        val item = IndexBuffer(vertexCountValue, allocationValue)
        bindBuffer(item)
        return item
    }

    @JvmStatic
    fun clearBuffers() {
        if (vbos.size > 0) {
            var c = 0
            val n = vbos.size
            val vbosIds = IntArray(vboToUnbind)
            var current: AbstractBuffer

            // ricaviamo tutti i bindingId
            for (i in 0 until n) {
                current = vbos[i]
                if (current.allocation !== BufferAllocationType.CLIENT) {
                    vbosIds[c] = current.bindingId
                    Logger.debug("Mark as buffer to remove from GPU memory VBO-id " + current.bindingId)
                    c++
                }
            }
            XenonGL.clearGlError()
            GLES20.glDeleteBuffers(vbosIds.size, vbosIds, 0)
            // GLES20.glFlush();
            XenonGL.checkGlError("glDeleteBuffers")
            for (i in 0 until n) {
                current = vbos[i]
                current.unbind()
            }
            vbos.clear()
            vboToUnbind = 0
            Logger.debug("Clear  " + n + " VBO (" + vbosIds.size + " have gpu memory) ")
        } else {
            Logger.debug("Clear 0 VBO")
        }
        vbos.clear()
    }

    @JvmStatic
    fun reloadVertexBuffers() {
        // puliamo i binder
        if (vbos.size > 0) {
            var c = 0
            val n = vbos.size
            val vbosIds = IntArray(vboToUnbind)
            var current: AbstractBuffer

            // ricaviamo tutti i bindingId
            for (i in 0 until n) {
                current = vbos[i]
                if (current.allocation !== BufferAllocationType.CLIENT) {
                    vbosIds[c] = current.bindingId
                    Logger.debug("Mark as buffer to remove from GPU memory VBO-id " + current.bindingId)
                    c++
                }
            }
            GLES20.glDeleteBuffers(vbosIds.size, vbosIds, 0)
            GLES20.glFlush()

            // cancelliamo le vecchie texture
            Logger.debug("Unbinded $n old vbos, without deleting them ")
            for (i in 0 until n) {
                current = vbos[i]
                current.bindingId = newBindingId()
                current.reload()
                Logger.debug("Rebind vbo %s", i)
            }
        }
    }
}