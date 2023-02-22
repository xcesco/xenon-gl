package com.abubusoft.xenon.mesh

import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.texture.TextureAspectRatioType
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType
import com.abubusoft.xenon.vbo.BufferAllocationOptions
import com.abubusoft.xenon.vbo.BufferAllocationType

/**
 * Opzioni per la creazione di uno shape. Di default
 * <dl>
 * <dt>texture</dt>
 * <dd>enabled. 1 texture e con aspect ratio 1:1</dd>
 * <dt>indici</dt>
 * <dd>disabled</dd>
 * <dt>normali</dt>
 * <dd>disabled</dd>
 * <dt>colori</dt>
 * <dd>disabled</dd>
 * <dt>attributes</dt>
 * <dd>disabled</dd>
</dl> *
 *
 * @author Francesco Benincasa
 */
class MeshOptions {
    fun meshClazz(clazz: Class<Mesh?>?): MeshOptions {
        meshClazz = clazz
        return this
    }

    /**
     * coordinate da usare per le texture
     */
    var textureCoordRect: TextureCoordRect? = null
    fun textureCoordRect(value: TextureCoordRect?): MeshOptions {
        textureCoordRect = value
        return this
    }

    fun textureInverseY(value: Boolean): MeshOptions {
        textureInverseY = value
        return this
    }

    var meshClazz: Class<out Mesh?>? = null

    /**
     *
     *
     * Aggiorna i vari vertexBuffer subito dopo la creazione.
     *
     */
    var updateAfterCreation = false

    /**
     * colore
     */
    var color = 0

    /**
     * indica se i colori sono abilitati
     */
    var colorsEnabled = false

    /**
     * indica gli indici sono stati abilitati
     */
    var indicesEnabled = false

    /**
     * indica se le normali sono state abilitate
     */
    var normalsEnabled = false

    /**
     * indica se gli attributi sono abilitati
     */
    var attributesEnabled = false

    /**
     * numero di attributi
     */
    var attributesCount = 0

    /**
     * indica se le texture sono abilitate
     */
    var textureEnabled = false

    /**
     * numero di texture
     */
    var texturesCount = 0

    /**
     * Se true inverte l'asse Y. Di solito l'asse punta verso l'alto. Con questo parametro invertito a true, L'asse Y va verso il basso (l'origine quindi va verso il basso).
     */
    var textureInverseY = false

    /**
     * opzioni da adottare per il vertex buffer
     */
    var bufferOptions: BufferAllocationOptions? = null

    /**
     * dimensioni degli attributi
     */
    var attributesDimension: AttributeDimensionType? = null

    /**
     * Fluent interface.
     *
     * @param value
     * @return this
     */
    fun colorEnabled(value: Boolean): MeshOptions {
        colorsEnabled = value
        return this
    }

    /**
     * Fluent interface. Se valorizzato con !=null allora impostiamo anche colorsEnabled a true.
     *
     * @param value
     * @return this
     */
    fun colorEnabled(value: Int): MeshOptions {
        color = value
        colorsEnabled = true
        return this
    }

    /**
     * Fluent interface
     *
     * @param value
     * @return this
     */
    fun indicesEnabled(value: Boolean): MeshOptions {
        indicesEnabled = value
        return this
    }

    /**
     * Fluent interface
     *
     * @param value
     * @return this
     */
    fun normalsEnabled(value: Boolean): MeshOptions {
        normalsEnabled = value
        return this
    }

    /**
     * Fluent interface per attributesEnabled
     *
     * @param value
     * @return this
     */
    fun attributesEnabled(value: Boolean): MeshOptions {
        attributesEnabled = value
        return this
    }

    /**
     * Fluent interface per attributesDimension. Definisce la dimensione dell'attribute
     *
     * @param value
     * @return this
     */
    fun attributesDimension(value: AttributeDimensionType?): MeshOptions {
        attributesDimension = value
        return this
    }

    /**
     * Fluent interface per attributesCount. Se = 0 disabilita gli attributes
     *
     * @param value
     * @return this
     */
    fun attributesCount(value: Int): MeshOptions {
        // per sicurezza
        attributesEnabled = if (value > 0) true else false
        attributesCount = value
        return this
    }

    /**
     *
     *
     * Fluent interface per textureRatio: ovvero il rapporto tra le dimensioni in larghezza ed in altezza della texture.
     *
     *
     *
     *
     * Indica quanta parte della texture deve essere considerata come valida. Le coordinate in una texture vanno da 0 a 1, ma nel caso in cui la texture è stata ridotta, le
     * coordinate, quelle verticali, dovranno avere un limite inferiore di 1.
     *
     *
     *
     *
     * Equivale ad utilizzare il metodo:
     *
     *
     * <pre>
     * TextureCoordRect.buildFromOrigin(1.f, (float) (1f / value.aspectXY));
    </pre> *
     *
     * @param value
     * @return this
     */
    fun textureAspectRatio(value: TextureAspectRatioType): MeshOptions {
        textureCoordRect = TextureCoordRect.Companion.buildFromTopLeft(0f, 0f, 1f, (1f / value.aspectXY).toFloat())
        return this
    }

    /**
     *
     *
     * Fluent interface per textureRatio: ovvero il rapporto tra le dimensioni in larghezza ed in altezza della texture.
     *
     *
     *
     *
     * Indica quanta parte della texture deve essere considerata come valida. Le coordinate in una texture vanno da 0 a 1, ma nel caso in cui la texture è stata ridotta, le
     * coordinate, quelle verticali, dovranno avere un limite inferiore di 1.
     *
     *
     *
     *
     * Equivale ad utilizzare il metodo:
     *
     *
     * <pre>
     * TextureCoordRect.buildFromOrigin(1.f, (float) (1f / value.aspectXY));
    </pre> *
     *
     * @param aspectXY
     * rapporto da width e height
     * @return this
     */
    fun textureAspectRatio(aspectXY: Float): MeshOptions {
        textureCoordRect = TextureCoordRect.Companion.buildFromTopLeft(0f, 0f, 1f, (1f / aspectXY))
        return this
    }

    /**
     * Fluent interface per textureEnabled
     *
     * @param value
     * @return this
     */
    fun textureEnabled(value: Boolean): MeshOptions {
        textureEnabled = value
        return this
    }

    /**
     * Fluent interface per textureRatio. Se = 0 disabilita le texture
     *
     * @param value
     * @return this
     */
    fun texturesCount(value: Int): MeshOptions {
        // per sicurezza
        textureEnabled = if (value > 0) true else false
        texturesCount = value
        return this
    }

    /**
     * Fluent interface per l'allocation type
     *
     * @param value
     * @return this
     */
    fun bufferAllocationOptions(value: BufferAllocationOptions?): MeshOptions {
        // per sicurezza
        bufferOptions = value
        return this
    }

    /**
     * Fluent interface per l'allocation type
     *
     * @param value
     * @return this
     */
    fun bufferAllocation(value: BufferAllocationType?): MeshOptions {
        // per sicurezza
        bufferOptions = BufferAllocationOptions.build().allocation(value!!)
        return this
    }

    /**
     * Fluent interface per updateAfterCreation. Se true viene fatto l'update dei vari vertexbuffer durante la creazione
     *
     * @param value
     * @return this
     */
    fun updateAfterCreation(value: Boolean): MeshOptions {
        // per sicurezza
        updateAfterCreation = value
        return this
    }

    companion object {
        /**
         *
         *
         * Costruisce una configurazione con le impostazioni di base.
         *
         *
         *
         *
         *  * attributesEnabled = **disabled**
         *  * bufferAllocationOptions = **CLIENT**
         *  * meshClazz = **Mesh**
         *  * colorsEnabled = **false**
         *  * indexesEnabled = **false**
         *  * normalsEnabled = **false**
         *  * textureAspectRatio = **RATIO1_1**
         *  * textureInverseY = **false**
         *  * textureEnabled = **true**
         *  * texturesCount = **1**
         *  * texturesCoordRect = **[0 .. 1, 0 .. 1]**
         *  * updateAfterCreation = **true**
         *
         *
         *
         * @return this
         */
        fun build(): MeshOptions {
            return MeshOptions() // tipo di oggetto da creare
                .meshClazz(Mesh::class.java) // texutre
                .textureEnabled(true).texturesCount(1).textureAspectRatio(TextureAspectRatioType.RATIO1_1).textureInverseY(false)
                .textureCoordRect(TextureCoordRect.Companion.build()) // indici
                .indicesEnabled(false) // normali
                .normalsEnabled(false) // aggiorna dopo la creazione
                .updateAfterCreation(true) // colori
                .colorEnabled(false)
                .attributesEnabled(false)
                .bufferAllocationOptions(BufferAllocationOptions.build())
        }
    }
}