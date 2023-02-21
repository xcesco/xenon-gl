package com.abubusoft.xenon.vbo

import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

/**
 *
 *
 * Indici per i triangoli che compongono gli shape.
 *
 * <dl>
 * <dt>IndexBuffer</dt>
 * <dd>contiene gli indici dei triangoli degli shape</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>1</dd>
 * <dt>Tipo</dt>
 * <dd>SHORT</dd>
</dl> *
 * http://www.learnopengles.com/android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos/
 *
 * @author Francesco Benincasa
 */
@BindType
class IndexBuffer : AbstractBuffer {
    internal constructor(vertexCountValue: Int, allocation: BufferAllocationType?) : super(vertexCountValue, 1, allocation) {
        values = ShortArray(capacity)
        build()
    }

    internal constructor() {}

    /**
     * indici che possono essere modificati direttamente
     */
    @BindXml(elementTag = "i")
    var values: ShortArray? = null

    /**
     * coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
     */
    @BindDisabled
    var buffer: ShortBuffer? = null

    /**
     * Aggiorna il float buffer ed eventualmente il vbo in video memory.
     *
     * Se il vbo non può essere aggiornato nuovamente, allora viene sollevata un'eccezione.
     */
    fun update(size: Int) {
        // passiamo da client java a client nativo
        buffer!!.put(values, 0, size).position(0)
        if (allocation !== BufferAllocationType.CLIENT) {
            if (firstUpdate) {
                setupIVBO()
                firstUpdate = false

                // in caso di BufferAllocationType.STATIC non dobbiamo più fare niente, dato
                // che il buffer e l'array locale puntano alla stessa zona di memoria
            } else {
                if (allocation === BufferAllocationType.STATIC) {
                    val msg = "Try to modify IndexBuffer STATIC"
                    Logger.fatal(msg)
                    throw RuntimeException(msg)
                }

                // Bind to the buffer. Future commands will affect this buffer specifically.
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bindingId)

                // Transfer data from client memory to the buffer.
                // We can release the client memory after this call.
                GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0, buffer!!.capacity() * BYTES_PER_SHORT, buffer)

                // IMPORTANT: Unbind from the buffer when we're done with it.
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, BINDING_ID_INVALID)
            }
        }
    }

    private fun setupIVBO() {
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bindingId)

        // Transfer data from client memory to the buffer.
        // We can release the client memory after this call.
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer!!.capacity() * BYTES_PER_SHORT, buffer, allocation!!.value)

        // IMPORTANT: Unbind from the buffer when we're done with it.
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
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
            setupIVBO()
        }
    }

    override fun unbind() {
        values = null
        buffer = null
        // destroyDirectByteBuffer(buffer);
    }

    override fun build() {
        // se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
        buffer = if (allocation === BufferAllocationType.STATIC) {
            ShortBuffer.wrap(values)
        } else {
            ByteBuffer.allocateDirect(capacity * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer()
        }
    }

    companion object {
        private const val serialVersionUID = -5924109187631222447L

        /**
         *
         *
         * Byte che compongono uno short
         *
         */
        private const val BYTES_PER_SHORT = 2

        /**
         *
         *
         *
         */
        const val INDEX_IN_QUAD_TILE = 6
    }
}