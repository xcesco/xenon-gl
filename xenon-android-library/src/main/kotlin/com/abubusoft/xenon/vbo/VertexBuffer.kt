package com.abubusoft.xenon.vbo

import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *
 *
 * Posizione dei vertici di uno shape
 *
 * <dl>
 * <dt>VertexBuffer</dt>
 * <dd>contiene le coordinate di un vertice in uno spazio tridimensinoale</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>parametrico</dd>
 * <dt>Tipo</dt>
 * <dd>FLOAT</dd>
</dl> *
 * [link](http://www.learnopengles.com/android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos)
 *
 * @author Francesco Benincasa
 */
@BindType
class VertexBuffer : AbstractBuffer {
    internal constructor(vertexCountValue: Int, allocationValue: BufferAllocationType?) : super(vertexCountValue, POSITION_DIMENSIONS, allocationValue) {
        coords = FloatArray(capacity)
        build()
    }

    internal constructor() {}

    /**
     * serve a costruire il buffer interno in base al resto degli attributi del buffer. Questo metodo viene invocato quando si creare con la factory il buffer, ma quando viene caricato da file l'oggetto, deve essere forzata la sua
     * esecuzione.
     */
    override fun build() {
        // se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
        buffer = if (allocation === BufferAllocationType.STATIC) {
            FloatBuffer.wrap(coords)
        } else {
            ByteBuffer.allocateDirect(capacity * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }
    }

    /**
     * coordinate che possono essere modificate direttamente
     */
    @BindXml(elementTag = "v")
    var coords: FloatArray? = null

    /**
     * coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
     */
    @BindDisabled
    var buffer: FloatBuffer? = null

    /**
     *
     *
     * Aggiorna il float buffer ed eventualmente il vbo in video memory.
     *
     *
     *
     *
     * Se il vbo non può essere aggiornato nuovamente, allora viene sollevata un'eccezione.
     *
     */
    fun update(size: Int) {
        // passiamo da client java a client nativo
        buffer!!.put(coords, 0, size).position(0)
        if (allocation !== BufferAllocationType.CLIENT) {
            if (firstUpdate) {
                setupVBO()
                firstUpdate = false

                // in caso di BufferAllocationType.STATIC non dobbiamo più fare niente, dato
                // che il buffer e l'array locale puntano alla stessa zona di memoria
            } else {
                if (allocation === BufferAllocationType.STATIC) {
                    val msg = "Try to allocation VertexBuffer STATIC"
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
     * Provvede a passare i dati dal buffer gestito direttamente da JAVA al native buffer che poi viene utilizzato direttamente da openGL
     *
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

    companion object {
        private const val serialVersionUID = -6221339606408978235L

        /**
         * numero di coordinate che definiscono una posizione in uno spazio tridimensionale.
         */
        const val POSITION_DIMENSIONS = 3
        const val OFFSET_X = 0
        const val OFFSET_Y = 1
        const val OFFSET_Z = 2
    }
}