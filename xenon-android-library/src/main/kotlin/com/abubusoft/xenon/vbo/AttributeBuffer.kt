package com.abubusoft.xenon.vbo

import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.kripton.annotation.BindXml
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *
 *
 * Attributi dei vertici di uno shape
 *
 * <dl>
 * <dt>AttributeBuffer</dt>
 * <dd>contiene le coordinate di un vertice in uno spazio tridimensinoale</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>parametro</dd>
 * <dt>Tipo</dt>
 * <dd>FLOAT</dd>
</dl> *
 *
 *
 * [android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos](http://www.learnopengles.com/android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos/)
 *
 *
 * @author Francesco Benincasa
 */
class AttributeBuffer : AbstractBuffer {
    enum class AttributeDimensionType(var value: Int) {
        DIM_1(1), DIM_2(2), DIM_3(3), DIM_4(4);
    }

    internal constructor(vertexCountValue: Int, dimension: AttributeDimensionType, allocation: BufferAllocationType?) : super(vertexCountValue, dimension.value, allocation) {
        coords = FloatArray(capacity)
        build()
    }

    internal constructor() {}

    /**
     *
     *
     * Coordinate che possono essere modificate direttamente.
     *
     *
     *
     *
     * Se il tipo di allocazione è di tipo [BufferAllocationType.STATIC] allora questo array punta, dopo il metodo [.update]
     *
     */
    @BindXml(elementTag = "v")
    private var coords: FloatArray? = null

    /**
     * coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
     */
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

        if (allocation != BufferAllocationType.CLIENT) {
            if (firstUpdate) {
                setupVBO()
                firstUpdate = false

                // possiamo annullare l'array. Non possiamo cancellare il buffer per questione di reload
                if (allocation == BufferAllocationType.STATIC) {
                    // facciamolo puntare alla zona di memoria del buffer
                    coords = buffer!!.array()
                }
            } else {
                if (allocation == BufferAllocationType.STATIC) {
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

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.renderer.SharedData#update()
	 */
    override fun update() {
        update(capacity)
    }

    /**
     * Se è un vbo, ricarichiamo i valori
     */
    override fun reload() {
        if (allocation != BufferAllocationType.CLIENT) {
            setupVBO()
        }
    }

    override fun unbind() {
        coords = null
        buffer = null
    }

    override fun build() {
        // se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
        buffer = if (allocation == BufferAllocationType.STATIC) {
            FloatBuffer.wrap(coords)
        } else {
            ByteBuffer.allocateDirect(capacity * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }
    }

    companion object {
        private const val serialVersionUID = 305300211006352642L
        const val OFFSET_X = 0
        const val OFFSET_Y = 1
        const val OFFSET_Z = 2
    }
}