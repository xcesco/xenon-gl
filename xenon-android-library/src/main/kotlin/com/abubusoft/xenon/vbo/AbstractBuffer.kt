package com.abubusoft.xenon.vbo

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.xenon.engine.SharedData
import com.abubusoft.xenon.mesh.QuadMesh
import java.io.Serializable

abstract class AbstractBuffer : SharedData, Serializable {
    internal constructor(vertexCountValue: Int, vertexDimensionValue: Int, allocationValue: BufferAllocationType) {
        allocation = allocationValue
        vertexCount = vertexCountValue
        vertexDimension = vertexDimensionValue
        capacity = vertexCount * vertexDimension
        firstUpdate = true
    }

    internal constructor() {
        firstUpdate = true
    }

    /**
     * Indica se è stato già fatto il primo update.
     *
     * Se `true` indica se deve essere fatto il prima update.
     */
    @BindDisabled
    @Transient
    protected var firstUpdate: Boolean

    /**
     * numero di vertici
     */
    @Bind
    var vertexCount = 0

    /**
     * dimensioni di un vertice
     */
    @Bind
    var vertexDimension = 0

    /**
     * dimensioni di un vertice
     */
    fun vertexDimension(): Int {
        return vertexDimension
    }

    /**
     * capacità del buffer, in termini di vertici * dimensioni
     */
    @Bind
    var capacity = 0

    /**
     *
     */
    @Bind
    var cursor = 0

    /**
     *
     *
     * Imposta al primo tile
     */
    fun cursorReset() {
        cursor = 0
    }

    /**
     * Indica se il buffer è aggiornabile.
     *
     * @return
     * true se il buffer è aggiornabile
     */
    val isUpdatable: Boolean
        get() = if (allocation == BufferAllocationType.STATIC) {
            firstUpdate
        } else true

    /**
     * Sposta il cursore di n posizioni. Il nextVertex viene moltiplicato per le dimensioni dei vertici.
     */
    fun cursorMove(nextVertex: Int) {
        cursor += nextVertex * vertexDimension
    }

    /**
     * Legge il cursore attuale. Si può usare direttamente l'attributo cursor.
     *
     * @return valore cursori
     */
    fun cursorRead(): Int {
        return cursor
    }

    /**
     * binding id del vbo. Non può essere final in quanto in caso di load può cambiare.
     */
    var bindingId = BINDING_ID_INVALID

    /**
     * tipo di buffer. Messo qua da options per comodità
     */
    @Bind
    lateinit var allocation: BufferAllocationType

    /**
     * costruisce in base ai parametri già definiti nel buffer il buffer interno.
     */
    abstract fun build()
    abstract fun reload()
    abstract fun unbind()

    companion object {
        private const val serialVersionUID = 8424163604930751532L

        /**
         * dimensione di un float espressa in byte
         */
        const val BYTES_PER_FLOAT = 4

        /**
         *
         *
         * Per comodità lo mettiamo anche qua. Numero di vertici in un quad (4)
         *
         */
        const val VERTEX_IN_QUAD_TILE = QuadMesh.VERTEX_IN_INDEXED_QUAD

        /**
         *
         *
         * Binding hardware non valido
         *
         */
        @JvmStatic
        val BINDING_ID_INVALID = 0
    }
}