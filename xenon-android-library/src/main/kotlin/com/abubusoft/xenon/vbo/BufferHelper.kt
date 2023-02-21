/**
 *
 */
package com.abubusoft.xenon.vbo

import android.opengl.GLES20

/**
 * @author xcesco
 */
object BufferHelper {
    /**
     *
     *
     * Genera un singolo texture id e lo restituisce.
     *
     *
     * @return
     */
    fun newBindingId(): Int {
        val resourceId = IntArray(1)
        GLES20.glGenBuffers(resourceId.size, resourceId, 0)
        return resourceId[0]
    }

    /**
     *
     *
     * Effettua il bind (ottiene un bindingId valido per il contesto OpenGL) e registra il buffer.
     *
     * @param <E>
     *
     * @param buffer
    </E> */
    fun <E : AbstractBuffer?> bindBuffer(buffer: E) {
        if (buffer!!.allocation === BufferAllocationType.CLIENT) {
            buffer!!.bindingId = AbstractBuffer.BINDING_ID_INVALID
        } else {
            buffer!!.bindingId = newBindingId()
            BufferManager.instance().vboToUnbind++
        }
        BufferManager.instance().vbos.add(buffer)
    }

    /**
     *
     *
     * Effettua il bind (ottiene un bindingId valido per il contesto OpenGL) e registra il buffer.
     *
     * @param <E>
     *
     * @param buffer
    </E> */
    fun <E : AbstractBuffer?> buildBuffer(buffer: E) {
        buffer!!.build()
    }
}