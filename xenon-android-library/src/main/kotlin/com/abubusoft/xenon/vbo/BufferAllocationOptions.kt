package com.abubusoft.xenon.vbo

class BufferAllocationOptions {
    /**
     *
     *
     * Una volta aggiornato il vbo con il metodo update, indica se rilasciare o meno la memoria client, ovvero quella che consente di aggiornare il vbo.
     *
     */
    // public boolean releaseClientMemory;
    /**
     *
     *
     * tipo di index buffer
     *
     */
    var indexAllocation: BufferAllocationType? = null

    /**
     * tipo di vertex buffer
     */
    var vertexAllocation: BufferAllocationType? = null

    /**
     * tipo di texture buffer
     */
    var textureAllocation: BufferAllocationType? = null

    /**
     * tipo di color buffer
     */
    var colorAllocation: BufferAllocationType? = null

    /**
     * allocazione per le normali
     */
    var normalAllocation: BufferAllocationType? = null

    /**
     * allocazione per gli attributi
     */
    var attributeAllocation: BufferAllocationType? = null

    /**
     *
     *
     * Imposta in una volta sola tutti e tre tipi di buffer
     *
     *
     * @param value
     * @return this
     */
    fun allocation(value: BufferAllocationType?): BufferAllocationOptions {
        textureAllocation = value
        vertexAllocation = value
        indexAllocation = value
        colorAllocation = value
        normalAllocation = value
        attributeAllocation = value
        return this
    }

    /*
	 * public BufferAllocationOptions releaseClientMemory(boolean value) { releaseClientMemory = value;
	 * 
	 * return this; }
	 */
    fun indexAllocation(value: BufferAllocationType): BufferAllocationOptions {
        indexAllocation = value
        return this
    }

    fun vertexAllocation(value: BufferAllocationType): BufferAllocationOptions {
        vertexAllocation = value
        return this
    }

    fun colorAllocation(value: BufferAllocationType): BufferAllocationOptions {
        colorAllocation = value
        return this
    }

    fun textureAllocation(value: BufferAllocationType): BufferAllocationOptions {
        textureAllocation = value
        return this
    }

    fun normalAllocation(value: BufferAllocationType): BufferAllocationOptions {
        textureAllocation = value
        return this
    }

    fun attributeAllocation(value: BufferAllocationType): BufferAllocationOptions {
        textureAllocation = value
        return this
    }

    companion object {
        /**
         *
         *
         * Configurazione base:
         *
         *
         *  * BufferAllocationType = CLIENT
         *  * releaseClientMemory = false
         *  * updateAfterCreation = true
         *
         *
         * @return this
         */
        fun build(): BufferAllocationOptions {
            val value = BufferAllocationOptions()
            value.allocation(BufferAllocationType.CLIENT)
            return value
        }
    }
}