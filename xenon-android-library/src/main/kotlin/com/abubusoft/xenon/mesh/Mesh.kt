package com.abubusoft.xenon.mesh

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.math.Dimension3
import com.abubusoft.xenon.vbo.*
import java.io.Serializable

/**
 * Rappresenta uno shape di base. Le forme non hanno una posizione, una rotazione o quant'altro le possono collocare all'interno di uno spazio. Servono solo a descrivere la forma
 * di un oggetto.
 *
 * Per collocare uno shape all'interno di uno spazio, bisogna utilizzare necessariamente un'entity.
 *
 * Supporta:
 *
 *  * **vertici**: un set di vertici
 *  * **texture components**: n set di coordinate uv
 *  * normali
 *  * indici
 *  * **colori**: un colore per vertice
 *
 *
 * @author Francesco Benincasa
 */
@Uncryptable
@BindType
open class Mesh internal constructor() : Serializable {
    /**
     * tipo di shape [MeshType]
     */
    @Bind
    var type: MeshType

    @Bind
    var name: String? = null

    /**
     * vertici
     */
    @Bind
    var vertices: VertexBuffer? = null

    /**
     * normali abilitate
     */
    @Bind
    var normalsEnabled = false

    /**
     * normali
     */
    @Bind
    var normals: VertexBuffer? = null

    /**
     * texture abilitate
     */
    @Bind
    var texturesEnabled = false

    /**
     * numero di texture
     */
    @Bind
    var texturesCount = 0

    /**
     *
     *
     * Array di array delle coordinate nel mondo texture
     *
     */
    @Bind
    lateinit var textures: Array<TextureBuffer>

    /**
     * indica se gli indici sono abilitati
     */
    @Bind
    var indexesEnabled = false

    /**
     * indici
     */
    @Bind
    var indexes: IndexBuffer? = null

    /**
     * tipo di disegno
     */
    @Bind
    var drawMode: MeshDrawModeType? = null

    /**
     * numero di vertici presenti
     */
    @Bind
    var vertexCount = 0

    /**
     * numero di indici presenti
     */
    @Bind
    var indexesCount = 0

    /**
     * colori abilitati
     */
    @Bind
    var colorsEnabled = false

    /**
     * buffer dei colori. Ogni vertice ha 4 byte per colore
     */
    @Bind
    @Transient
    var colors: ColorBuffer? = null

    /**
     * raggio della sfera che contiene l'intero oggetto
     */
    @Bind
    var boundingSphereRadius = 0f

    /**
     * dimensioni del bounding box
     */
    @Bind
    var boundingBox = Dimension3()

    @Bind
    var attributesEnabled = false

    @Bind
    var attributesCount = 0

    @BindDisabled
    lateinit var attributes: Array<AttributeBuffer>

    /**
     * Definiamo costruttore con scope package, in modo da non poter essere definito senza l'apposita factory
     */
    init {
        // di default è su base triangolare.
        type = MeshType.TRIANGLES_BASED
    }

    /**
     *
     *
     * Effettua l'aggiornamento di tutti i buffer che non sono di tipo STATIC.
     *
     */
    fun updateBuffers() {
        // vertici
        // vertices.put(verticesCoords).position(0);
        if (vertices!!.isUpdatable) {
            vertices!!.update()
        }

        // texture
        if (texturesEnabled) {
            for (i in textures.indices) {
                if (textures[i].isUpdatable) {
                    textures[i].update() /* put(texturesCoords[i]).position(0); */
                }
            }
        }

        // colori
        if (colorsEnabled && colors!!.isUpdatable) {
            colors!!.update()
        }

        // indici
        if (indexesEnabled && indexes!!.isUpdatable) {
            indexes!!.update()
        }
    }

    companion object {
        /**
         * numero di coordinate per texel
         */
        const val TEXTURE_DIMENSIONS = 2

        /**
         * numero di coordinate per vertice
         */
        const val VERTICES_DIMENSIONS = 3
        const val OFFSET_X = 0
        const val OFFSET_Y = 1
        const val OFFSET_Z = 2
        private const val serialVersionUID = -3939445945842866443L
    }
}