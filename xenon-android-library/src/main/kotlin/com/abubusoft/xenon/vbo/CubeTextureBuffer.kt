package com.abubusoft.xenon.vbo

import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.kripton.annotation.BindXml
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

//TODO da usare.
/**
 *
 *
 * Buffer VBO per le texture cubiche.
 *
 *
 * <dl>
 * <dt>CubeTexture</dt>
 * <dd>contiene le coordinate per le texture cubiche</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>3</dd>
 * <dt>Tipo</dt>
 * <dd>FLOAT</dd>
</dl> *
 *
 * @author Francesco Benincasa
 */
class CubeTextureBuffer : AbstractBuffer {
    internal constructor(vertexCountValue: Int, allocationValue: BufferAllocationType?) : super(vertexCountValue, CUBE_TEXTURE_DIMENSIONS, allocationValue) {
        coords = FloatArray(capacity)
    }

    internal constructor() {}

    /**
     * coordinate che possono essere modificate direttamente
     */
    @BindXml(elementTag = "v")
    var coords: FloatArray?=null

    /**
     * Coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
     */
    var buffer: FloatBuffer? = null

    /**
     * Aggiorna il float buffer ed eventualmente il vbo in video memory.
     *
     * Se il vbo non può essere aggiornato nuovamente, allora viene sollevata un'eccezione.
     */
    fun update(size: Int) {
        // passiamo da client java a client nativo
        buffer!!.put(coords, 0, size).position(0)
        // buffer.put(components).position(0);
        if (allocation !== BufferAllocationType.CLIENT) {
            if (firstUpdate) {
                setupVBO()
                firstUpdate = false

                // in caso di BufferAllocationType.STATIC non dobbiamo più fare niente, dato
                // che il buffer e l'array locale puntano alla stessa zona di memoria
            } else {
                if (allocation === BufferAllocationType.STATIC) {
                    val msg = "Try to allocation TextureBuffer STATIC"
                    Logger.fatal(msg)
                    throw RuntimeException(msg)
                }

                // Bind to the buffer. Future commands will affect this buffer specifically.
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bindingId)

                // Transfer data from client memory to the buffer.
                // We can release the client memory after this call.
                GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, buffer!!.capacity() * BYTES_PER_FLOAT, buffer)

                // IMPORTANT: Unbind from the buffer when we're done with it.
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            }
        }
    }

    private fun setupVBO() {
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bindingId)

        // Transfer data from client memory to the buffer.
        // We can release the client memory after this call.
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer!!.capacity() * BYTES_PER_FLOAT, buffer, allocation!!.value)

        // IMPORTANT: Unbind from the buffer when we're done with it.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    /**
     *
     *
     * Se il tipo di allocazione è di tipo [BufferAllocationType.STATIC] allora questo array punta, dopo il metodo [.update]
     *
     */
    override fun update() {
        update(capacity)
    }

    /**
     * Se è un vbo, ricarichiamo i valori
     */
    override fun reload() {
        if (allocation !== BufferAllocationType.CLIENT) {
            setupVBO()
        }
    }

    override fun unbind() {
        coords = null
        buffer = null
        // destroyDirectByteBuffer(buffer);
    }

    override fun build() {
        // se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
        buffer = if (allocation === BufferAllocationType.STATIC) {
            FloatBuffer.wrap(coords)
        } else {
            ByteBuffer.allocateDirect(capacity * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }
    }

    companion object {
        private const val serialVersionUID = -7293463267603536972L
        const val CUBE_TEXTURE_DIMENSIONS = 3

        /**
         * Le coordinate nello spazio delle texture: s t p
         */
        const val OFFSET_S = 0

        /**
         * Le coordinate nello spazio delle texture: s t p
         */
        const val OFFSET_T = 1

        /**
         * Le coordinate nello spazio delle texture: s t p
         */
        const val OFFSET_P = 2
    }
}