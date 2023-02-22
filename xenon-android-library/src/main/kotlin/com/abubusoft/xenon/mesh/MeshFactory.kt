/**
 *
 */
package com.abubusoft.xenon.mesh

import android.content.Context
import android.graphics.Color
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.math.XenonMath.squareDistanceFromOrigin
import com.abubusoft.xenon.mesh.modifiers.ColorModifier
import com.abubusoft.xenon.mesh.modifiers.TextureModifier
import com.abubusoft.xenon.mesh.persistence.androidxml.AndroidXmlAdapter
import com.abubusoft.xenon.mesh.persistence.androidxml.AndroidXmlLoader
import com.abubusoft.xenon.mesh.persistence.kripton.KriptonLoader
import com.abubusoft.xenon.mesh.persistence.max3d.Max3DSAdapter
import com.abubusoft.xenon.mesh.persistence.max3d.Max3DSLoader
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontAdapter
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontLoader
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 * factory per gli shape generati da codice.
 *
 * @author Francesco Benincasa
 */
object MeshFactory {
    /**
     * dimensione di un float espressa in byte
     */
    const val FLOAT_SIZE = 4

    /**
     * numero di coordinate per definire un colore (rgba)
     */
    const val COLOR_ELEMENTS = 4

    /**
     * numero di coordinate nello spazio delle texture
     */
    const val TEXTURE_DIMENSION = 2

    /**
     * dimensione di un short espressa in byte
     */
    const val SHORT_SIZE = 2

    /**
     * numero di vertici per quadrato
     */
    const val VERTEX_PER_TRIANGLED_QUAD = 6

    /**
     * numero di indici per tile (due triangoli con 6 vertici, di cui 2 condivisi)
     */
    const val INDEX_PER_QUAD = 6

    /**
     * numero di vertici per quadrato
     */
    const val VERTEX_PER_QUAD = 4

    /**
     * numero di vertici per triangolo
     */
    const val VERTEX_PER_TRIANGLE = 3

    /**
     * coordinate per vertice
     */
    const val VERTEX_DIMENSION = 3

    /**
     * numero di triangolo per quadrato
     */
    const val TRIANGLE_PER_QUAD = 2

    /**
     * Come [.createPlaneMesh], con la differenza che c'è una riga ed una colonna.
     *
     * @param width
     * @param height
     * @param options
     * @return mesh
     */
    fun createPlaneMesh(width: Float, height: Float, options: MeshOptions): Mesh {
        return createPlaneMesh(width, height, 1, 1, options)
    }

    /**
     * Genera un piano di una certa dimensione suddiviso in mattonelle. Il numero di mattonelle per riga e per colonna è passato come parametro. Per definizione questo shape è indicizzato. I vertici del piano sono tutti condivisi, quindi
     * non è possibile mettere diverse texture per ogni mattonella. Il piano si comporta come una mesh.
     *
     * L'origine del sistema di riferimento usato per generare le coordinate si trova nel centro del plane. I vertici sono generati da top, left verso sx.
     *
     *
     * <pre>
     * 0 --- 1 --- 2
     * |     |     |
     * 3 --- 4 --- 5
     * |     |     |
     * 6 --- 7 --- 8
    </pre> *
     *
     * <table border=0>
     * <tr>
     * <td>
     *
     * <table border='1'>
     * <tr>
     * <td>** #**</td>
     * <td>**Vertex **</td>
     * <td>**Texture **</td>
    </tr> *
     * <tr>
     * <td>0</td>
     * <td>( -5.0, 5.0, 0.0)</td>
     * <td>( 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>1</td>
     * <td>( 0.0, 5.0, 0.0)</td>
     * <td>( 0.5, 0.0)</td>
    </tr> *
     * <tr>
     * <td>2</td>
     * <td>( 5.0, 5.0, 0.0)</td>
     * <td>( 1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>3</td>
     * <td>( -5.0, -0.0, 0.0)</td>
     * <td>( 0.0, 0.5)</td>
    </tr> *
     * <tr>
     * <td>4</td>
     * <td>( 0.0, -0.0, 0.0)</td>
     * <td>( 0.5, 0.5)</td>
    </tr> *
     * <tr>
     * <td>5</td>
     * <td>( 5.0, -0.0, 0.0)</td>
     * <td>( 1.0, 0.5)</td>
    </tr> *
     * <tr>
     * <td>6</td>
     * <td>( -5.0, -5.0, 0.0)</td>
     * <td>( 0.0, 1.0)</td>
    </tr> *
     * <tr>
     * <td>7</td>
     * <td>( 0.0, -5.0, 0.0)</td>
     * <td>( 0.5, 1.0)</td>
    </tr> *
     * <tr>
     * <td>8</td>
     * <td>( 5.0, -5.0, 0.0)</td>
     * <td>( 1.0, 1.0)</td>
    </tr> *
    </table> *
     *
    </td> *
     * <td>
     *
     * <table border='1'>
     * <tr>
     * <td>**Triangles**</td>
    </tr> *
     * <tr>
     * <td>( 0, 3, 4)</td>
    </tr> *
     * <tr>
     * <td>( 4, 1, 0)</td>
    </tr> *
     * <tr>
     * <td>( 1, 4, 5)</td>
    </tr> *
     * <tr>
     * <td>( 5, 2, 1)</td>
    </tr> *
     * <tr>
     * <td>( 3, 6, 7)</td>
    </tr> *
     * <tr>
     * <td>( 7, 4, 3)</td>
    </tr> *
     * <tr>
     * <td>( 4, 7, 8)</td>
    </tr> *
     * <tr>
     * <td>( 8, 5, 4)</td>
    </tr> *
    </table> *
     *
    </td> *
    </tr> *
    </table> *
     *
     * @param width
     * larghezza del plane
     * @param height
     * altezza del plane
     * @param rows
     * numero di righe in cui è suddiviso il plane
     * @param cols
     * numero di colonne in cui è suddiviso il plane
     * @param options
     * opzioni della mesh
     * @return mesh
     */
    fun createPlaneMesh(width: Float, height: Float, rows: Int, cols: Int, options: MeshOptions): Mesh {
        val shape = QuadMesh()

        // imposta il bounding box
        shape.boundingBox[width, height] = 0f
        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        shape.boundingSphereRadius = Math.sqrt(squareDistanceFromOrigin(width * 0.5f, height * 0.5f, 0f).toDouble()).toFloat()

        // ogni quadrato contiene 4 vertici
        val numVertices = (rows + 1) * (cols + 1)

        // vertici
        shape.vertexCount = numVertices
        shape.vertices = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation)
        var t: Float
        var s: Float
        val fRows = rows.toFloat()
        val fCols = cols.toFloat()
        var current = 0

        // calcoliamo i vertici dal top,left.
        for (y in 0..rows) {
            t = y / fRows
            for (x in 0..cols) {
                s = x / fCols
                shape.vertices!!.coords!![current + 0] = width * 0.5f * (2 * s - 1)
                shape.vertices!!.coords!![current + 1] = -height * 0.5f * (2 * t - 1)
                shape.vertices!!.coords!![current + 2] = 0f
                current += VERTEX_DIMENSION
            }
        }

        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        shape.boundingSphereRadius =
            Math.sqrt(squareDistanceFromOrigin(shape.vertices!!.coords!![0], shape.vertices!!.coords!![1], shape.vertices!!.coords!![2]).toDouble()).toFloat()
        if (options.updateAfterCreation) {
            shape.vertices!!.update()
        }

        // texture
        if (options.textureEnabled) {
            shape.texturesEnabled = true
            // impostiamo il numero di texture
            shape.texturesCount = options.texturesCount

            val yFactor = options.textureCoordRect.height / rows
            val xFactor = options.textureCoordRect.width / cols

            shape.textures = Array(options.texturesCount) { index ->
                val texture = BufferManager.createTextureBuffer(numVertices, options.bufferOptions.textureAllocation)
                current = 0
                for (y in 0..rows) {
                    // lo facciamo per ribaltare le coord texture. startY va da 0 a rows
                    t = options.textureCoordRect.y + yFactor * if (options.textureInverseY) rows - y else y
                    for (x in 0..cols) {
                        s = options.textureCoordRect.x + xFactor * x
                        texture.coords!![current + 0] = s
                        texture.coords!![current + 1] = t
                        current += TEXTURE_DIMENSION
                    }
                }

                // Quando passo un array ad un direct buffer devo poi riposizionare a 0
                if (options.updateAfterCreation) {
                    texture.update()
                }

                texture
            }
        }

        // attribute
        if (options.attributesEnabled) {
            shape.attributesEnabled = true
            // impostiamo il numero di texture
            shape.attributesCount = options.attributesCount
            shape.attributes = Array(options.attributesCount) { index ->
                val attribute = BufferManager.createAttributeBuffer(numVertices, options.attributesDimension, options.bufferOptions.attributeAllocation)
                current = 0
                for (y in 0..rows) {
                    for (x in 0..cols) {
                        for (w in 0 until options.attributesDimension.value) {
                            attribute.coords!![current + w] = 0f
                        }
                        current += options.attributesDimension.value
                    }
                }

                // Quando passo un array ad un direct buffer devo poi riposizionare a 0
                if (options.updateAfterCreation) {
                    attribute.update()
                }

                attribute
            }
        }

        // normali
        if (options.normalsEnabled) {
            shape.normalsEnabled = true
            shape.normals = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.normalAllocation)
            var i = 0
            while (i < numVertices * VERTEX_DIMENSION) {
                shape.normals!!.coords!![i + 0] = 0f
                shape.normals!!.coords!![i + 1] = 0f
                shape.normals!!.coords!![i + 2] = 1f
                i += VERTEX_DIMENSION
            }

            // Quando passo un array ad un direct buffer devo poi riposizionare a 0
            if (options.updateAfterCreation) {
                shape.normals!!.update()
            }
        } else {
            shape.normalsEnabled = false
        }

        // colore
        if (options.colorsEnabled) {
            shape.colorsEnabled = true
            shape.colors = BufferManager.createColorBuffer(numVertices, options.bufferOptions.colorAllocation)
            ColorModifier.setColor(shape, Color.WHITE, options.updateAfterCreation)
        } else {
            shape.colorsEnabled = false
        }

        // indici: ci sono sempre indici
        shape.indexesCount = INDEX_PER_QUAD * (rows * cols)
        shape.indexesEnabled = true
        shape.indexes = BufferManager.createIndexBuffer(shape.indexesCount, options.bufferOptions.indexAllocation)
        current = 0
        var i: Short = 0
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                // if (x < rows && y < cols) {
                i = (x + y * (cols + 1)).toShort()
                shape.indexes!!.values!![current + 0] = i
                shape.indexes!!.values!![current + 1] = (i + 1 + cols).toShort()
                shape.indexes!!.values!![current + 2] = (i + 2 + cols).toShort()
                shape.indexes!!.values!![current + 3] = (i + 2 + cols).toShort()
                shape.indexes!!.values!![current + 4] = (i + 1).toShort()
                shape.indexes!!.values!![current + 5] = i
                current += INDEX_PER_QUAD
                // }
            }
        }

        // Quando passo un array ad un direct buffer devo poi riposizionare a 0
        if (options.updateAfterCreation) {
            shape.indexes!!.update()
        }

        // stile di disegno
        shape.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
        return shape
    }

    /**
     *
     *
     * crea un cubo avente per lato dimension e due triangoli per faccia. I vertici sono collegati tra loro. Funziona con le cube texture.
     *
     *
     * <table border="1">
     * <tr>
     * <td>** #**</td>
     * <td>**Vertex **</td>
     * <td>**Texture **</td>
     * <td>**Normal **</td>
    </tr> *
     * <tr>
     * <td>0</td>
     * <td>( -2.0, -2.0, -2.0)</td>
     * <td>( 0.0, 1.0)</td>
     * <td>( -1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>1</td>
     * <td>( -2.0, -2.0, 2.0)</td>
     * <td>( 1.0, 1.0)</td>
     * <td>( -1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>2</td>
     * <td>( -2.0, 2.0, -2.0)</td>
     * <td>( 0.0, 0.0)</td>
     * <td>( -1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>3</td>
     * <td>( -2.0, 2.0, 2.0)</td>
     * <td>( 1.0, 0.0)</td>
     * <td>( -1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>4</td>
     * <td>( 2.0, -2.0, -2.0)</td>
     * <td>( 0.0, 1.0)</td>
     * <td>( 1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>5</td>
     * <td>( 2.0, 2.0, -2.0)</td>
     * <td>( 1.0, 1.0)</td>
     * <td>( 1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>6</td>
     * <td>( 2.0, -2.0, 2.0)</td>
     * <td>( 0.0, 0.0)</td>
     * <td>( 1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>7</td>
     * <td>( 2.0, 2.0, 2.0)</td>
     * <td>( 1.0, 0.0)</td>
     * <td>( 1.0, 0.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>8</td>
     * <td>( -2.0, -2.0, -2.0)</td>
     * <td>( 0.0, 1.0)</td>
     * <td>( 0.0, -1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>9</td>
     * <td>( 2.0, -2.0, -2.0)</td>
     * <td>( 1.0, 1.0)</td>
     * <td>( 0.0, -1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>10</td>
     * <td>( -2.0, -2.0, 2.0)</td>
     * <td>( 0.0, 0.0)</td>
     * <td>( 0.0, -1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>11</td>
     * <td>( 2.0, -2.0, 2.0)</td>
     * <td>( 1.0, 0.0)</td>
     * <td>( 0.0, -1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>12</td>
     * <td>( -2.0, 2.0, -2.0)</td>
     * <td>( 0.0, 1.0)</td>
     * <td>( 0.0, 1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>13</td>
     * <td>( -2.0, 2.0, 2.0)</td>
     * <td>( 1.0, 1.0)</td>
     * <td>( 0.0, 1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>14</td>
     * <td>( 2.0, 2.0, -2.0)</td>
     * <td>( 0.0, 0.0)</td>
     * <td>( 0.0, 1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>15</td>
     * <td>( 2.0, 2.0, 2.0)</td>
     * <td>( 1.0, 0.0)</td>
     * <td>( 0.0, 1.0, 0.0)</td>
    </tr> *
     * <tr>
     * <td>16</td>
     * <td>( -2.0, -2.0, -2.0)</td>
     * <td>( 0.0, 1.0)</td>
     * <td>( 0.0, 0.0, -1.0)</td>
    </tr> *
     * <tr>
     * <td>17</td>
     * <td>( -2.0, 2.0, -2.0)</td>
     * <td>( 1.0, 1.0)</td>
     * <td>( 0.0, 0.0, -1.0)</td>
    </tr> *
     * <tr>
     * <td>18</td>
     * <td>( 2.0, -2.0, -2.0)</td>
     * <td>( 0.0, 0.0)</td>
     * <td>( 0.0, 0.0, -1.0)</td>
    </tr> *
     * <tr>
     * <td>19</td>
     * <td>( 2.0, 2.0, -2.0)</td>
     * <td>( 1.0, 0.0)</td>
     * <td>( 0.0, 0.0, -1.0)</td>
    </tr> *
     * <tr>
     * <td>20</td>
     * <td>( -2.0, -2.0, 2.0)</td>
     * <td>( 0.0, 1.0)</td>
     * <td>( 0.0, 0.0, 1.0)</td>
    </tr> *
     * <tr>
     * <td>21</td>
     * <td>( 2.0, -2.0, 2.0)</td>
     * <td>( 1.0, 1.0)</td>
     * <td>( 0.0, 0.0, 1.0)</td>
    </tr> *
     * <tr>
     * <td>22</td>
     * <td>( -2.0, 2.0, 2.0)</td>
     * <td>( 0.0, 0.0)</td>
     * <td>( 0.0, 0.0, 1.0)</td>
    </tr> *
     * <tr>
     * <td>23</td>
     * <td>( 2.0, 2.0, 2.0)</td>
     * <td>( 1.0, 0.0)</td>
     * <td>( 0.0, 0.0, 1.0)</td>
    </tr> *
    </table> *
     *
     *
     *
     * <table border="1">
     * <tr>
     * <td>**Triangles**</td>
    </tr> *
     * <tr>
     * <td>( 0, 1, 2)</td>
    </tr> *
     * <tr>
     * <td>( 2, 1, 3)</td>
    </tr> *
     * <tr>
     * <td>( 4, 5, 6)</td>
    </tr> *
     * <tr>
     * <td>( 6, 5, 7)</td>
    </tr> *
     * <tr>
     * <td>( 8, 9, 10)</td>
    </tr> *
     * <tr>
     * <td>( 10, 9, 11)</td>
    </tr> *
     * <tr>
     * <td>( 12, 13, 14)</td>
    </tr> *
     * <tr>
     * <td>( 14, 13, 15)</td>
    </tr> *
     * <tr>
     * <td>( 16, 17, 18)</td>
    </tr> *
     * <tr>
     * <td>( 18, 17, 19)</td>
    </tr> *
     * <tr>
     * <td>( 20, 21, 22)</td>
    </tr> *
     * <tr>
     * <td>( 22, 21, 23)</td>
    </tr> *
    </table> *
     *
     * @param dimension
     * @param options
     * @return mesh
     */
    fun createCubeMesh(dimension: Float, options: MeshOptions): Mesh {
        val cubeData = arrayOf(
            intArrayOf(0, 4, 2, 6, -1, 0, 0),
            intArrayOf(1, 3, 5, 7, +1, 0, 0),
            intArrayOf(0, 1, 4, 5, 0, -1, 0),
            intArrayOf(2, 6, 3, 7, 0, +1, 0),
            intArrayOf(0, 2, 1, 3, 0, 0, -1),
            intArrayOf(4, 5, 6, 7, 0, 0, +1)
        )
        val shape = Mesh()

        // imposta il bounding box
        shape.boundingBox[dimension, dimension] = 0f
        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        shape.boundingSphereRadius = Math.sqrt(squareDistanceFromOrigin(dimension * 0.5f, dimension * 0.5f, dimension * 0.5f).toDouble()).toFloat()

        // ogni quadrato contiene 4 vertici
        val numVertices = cubeData.size * 4

        // vertici
        shape.vertexCount = numVertices
        shape.vertices = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation)
        var current: Int
        current = 0
        for (i in cubeData.indices) {
            val data = cubeData[i]
            for (j in 0..3) {
                val d = data[j]
                shape.vertices!!.coords!![current + 0] = dimension * 0.5f * ((d and 1) * 2f - 1f)
                shape.vertices!!.coords!![current + 1] = dimension * 0.5f * ((d and 2) - 1f)
                shape.vertices!!.coords!![current + 2] = dimension * 0.5f * ((d and 4) / 2f - 1f)
                current += VERTEX_DIMENSION
            }
        }
        if (options.updateAfterCreation) {
            shape.vertices!!.update()
        }

        // attribute
        if (options.attributesEnabled) {
            shape.attributesEnabled = true
            // impostiamo il numero di texture
            shape.attributesCount = options.attributesCount
            shape.attributes = Array(options.attributesCount) { index ->
                val attribute = BufferManager.createAttributeBuffer(numVertices, options.attributesDimension, options.bufferOptions.attributeAllocation)
                current = 0
                for (i in cubeData.indices) {
                    for (j in 0..3) {
                        for (w in 0 until options.attributesDimension.value) {
                            attribute.coords!![current + 0] = 0f
                        }
                        current += options.attributesDimension.value
                    }
                }

                // Quando passo un array ad un direct buffer devo poi riposizionare a 0
                if (options.updateAfterCreation) {
                    attribute.update()
                }

                attribute
            }
        }

        // texture
        if (options.textureEnabled) {
            shape.texturesEnabled = true
            // impostiamo il numero di texture
            shape.texturesCount = options.texturesCount
            val yFactor = 1f
            shape.textures = Array(options.texturesCount) { index ->
                val texture = BufferManager.createTextureBuffer(numVertices, options.bufferOptions.textureAllocation)
                current = 0
                current = 0
                for (i in cubeData.indices) {
                    for (j in 0..3) {
                        texture.coords!![current + 0] = (j and 1).toFloat()
                        texture.coords!![current + 1] = yFactor * if (options.textureInverseY) (j and 2) / 2f else 1 - (j and 2) / 2f
                        current += TEXTURE_DIMENSION
                    }
                }

                // Quando passo un array ad un direct buffer devo poi riposizionare a 0
                if (options.updateAfterCreation) {
                    texture.update()
                }

                texture
            }
        }

        // normali
        if (options.normalsEnabled) {
            shape.normalsEnabled = true
            shape.normals = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.normalAllocation)
            current = 0
            for (i in cubeData.indices) {
                val data = cubeData[i]
                for (j in 0..3) {
                    shape.normals!!.coords!![current + 0] = data[4 + 0].toFloat()
                    shape.normals!!.coords!![current + 1] = data[4 + 1].toFloat()
                    shape.normals!!.coords!![current + 2] = data[4 + 2].toFloat()
                    current += VERTEX_DIMENSION
                }
            }

            // Quando passo un array ad un direct buffer devo poi riposizionare a 0
            if (options.updateAfterCreation) {
                shape.normals!!.update()
            }
        } else {
            shape.normalsEnabled = false
        }

        // colore
        if (options.colorsEnabled) {
            shape.colorsEnabled = true
            shape.colors = BufferManager.createColorBuffer(numVertices, options.bufferOptions.colorAllocation)
            ColorModifier.setColor(shape, Color.WHITE, options.updateAfterCreation)
        } else {
            shape.colorsEnabled = false
        }

        // indici: ci sono sempre indici
        shape.indexesCount = INDEX_PER_QUAD * 6
        shape.indexesEnabled = true
        shape.indexes = BufferManager.createIndexBuffer(shape.indexesCount, options.bufferOptions.indexAllocation)
        current = 0
        for (i in cubeData.indices) {
            val v = i * 4
            shape.indexes!!.values!![current + 0] = v.toShort()
            shape.indexes!!.values!![current + 1] = (v + 1).toShort()
            shape.indexes!!.values!![current + 2] = (v + 2).toShort()
            shape.indexes!!.values!![current + 3] = (v + 2).toShort()
            shape.indexes!!.values!![current + 4] = (v + 1).toShort()
            shape.indexes!!.values!![current + 5] = (v + 3).toShort()
            current += INDEX_PER_QUAD
        }

        // Quando passo un array ad un direct buffer devo poi riposizionare a 0
        if (options.updateAfterCreation) {
            shape.indexes!!.update()
        }

        // stile di disegno
        shape.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
        return shape
    }

    /*
     * public XmlDataModel createSphere(int numSlices, float radius) { XmlDataModel shape = new XmlDataModel();
     *
     * int i; int j; int numParallels = numSlices; int numVertices = (numParallels + 1) * (numSlices + 1); int numIndices = numParallels * numSlices * 6; float angleStep = ((2.0f * (float) Math.PI) / numSlices);
     *
     * // Allocate memory for buffers shape.vertices = ByteBuffer.allocateDirect(numVertices * * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); normalsCoords = ByteBuffer.allocateDirect(numVertices * 3 *
     * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); textureCoords = ByteBuffer.allocateDirect(numVertices * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); indexes = ByteBuffer.allocateDirect(numIndices *
     * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
     *
     * for (i = 0; i < numParallels + 1; i++) { for (j = 0; j < numSlices + 1; j++) { int vertex = (i * (numSlices + 1) + j) * 3;
     *
     * vertices.put(vertex + 0, (float) (radius * Math.sin(angleStep * (float) i) * Math.sin(angleStep * (float) j)));
     *
     * vertices.put(vertex + 1, (float) (radius * Math.cos(angleStep * (float) i))); vertices.put(vertex + 2, (float) (radius * Math.sin(angleStep * (float) i) * Math.cos(angleStep * (float) j)));
     *
     * normalsCoords.put(vertex + 0, vertices.get(vertex + 0) / radius); normalsCoords.put(vertex + 1, vertices.get(vertex + 1) / radius); normalsCoords.put(vertex + 2, vertices.get(vertex + 2) / radius);
     *
     * int texIndex = (i * (numSlices + 1) + j) * 2; textureCoords.put(texIndex + 0, (float) j / (float) numSlices); textureCoords.put(texIndex + 1, (1.0f - (float) i) / (float) (numParallels - 1)); } }
     *
     * int index = 0; for (i = 0; i < numParallels; i++) { for (j = 0; j < numSlices; j++) { indexes.put(index++, (short) (i * (numSlices + 1) + j)); indexes.put(index++, (short) ((i + 1) * (numSlices + 1) + j)); indexes.put(index++,
     * (short) ((i + 1) * (numSlices + 1) + (j + 1)));
     *
     * indexes.put(index++, (short) (i * (numSlices + 1) + j)); indexes.put(index++, (short) ((i + 1) * (numSlices + 1) + (j + 1))); indexes.put(index++, (short) (i * (numSlices + 1) + (j + 1)));
     *
     * } } indexesCount = numIndices;
     *
     * return numIndices; }
     */
    /**
     *
     *
     * Crea uno shape piano con indici con il seguente pattern:
     *
     *
     * <img src="doc-files/triangleStrip1.png"></img>
     *
     *
     *
     * Il numero di vertici è dato dalla formula `numVertices = (verticalBand * 2) + 2;`. Questo numero è importante nel caso in cui si vogliano definire i colori (che devono essere in numero uguale ai vertici). Dal punto di
     * vista delle dimensioni:
     *
     *
     * <img src="doc-files/createVerticalBandPlane2.png"></img>
     *
     *
     *
     * Gli indici non vengono valorizzati. Ricordiamo che le texture hanno il seguente modo di utilizzare le coordinate
     *
     *
     * <img src="doc-files/textureCoords.png"></img>
     *
     *
     *
     * Le coordinate z sono tutte impostate a 0. Le opzioni relative verranno ignorate, dato che non vengono utilizzati.
     *
     *
     * @param width
     * larghezza dello shape
     * @param height
     * altezza dello shape
     * @param textureRatio
     * indica
     * @return
     */
    /*
     * public static Mesh createPlane(float width, float height, MeshOptions options) { return createPlaneWithVerticalStripes(width, height, 1, options); }
     */
    /*
     * public static MeshWireframe createWireframe(Mesh mesh, MeshOptions options) {
     *
     * }
     */
    /**
     *
     *
     * Usa un TRIANGLE_STRIP per creare uno shape con la seguente struttura:
     *
     *
     * <img src="doc-files/createPlaneWithVerticalStripes.png"></img>
     *
     *
     *
     * Il pattern è il seguente:
     *
     *
     * <img src="doc-files/triangleStrip.png"></img>
     *
     *
     *
     * Il numero di vertici è dato dalla formula `numVertices = (verticalBand * 2) + 2;`. Questo numero è importante nel caso in cui si vogliano definire i colori (che devono essere in numero uguale ai vertici).
     *
     *
     * <h2>Dimensioni</h2>
     *
     *
     * Dal punto di vista delle dimensioni:
     *
     *
     * <img src="doc-files/createVerticalBandPlane2.png"></img>
     *
     *
     *
     * Gli indici non vengono valorizzati. Ricordiamo che le texture hanno l'origine del sistema di riferimento in basso a sinistra.
     *
     *
     * <h2>Texture</h2>
     *
     *
     * L'origine del sistema di coordinate delle texture è in basso a sinistra.
     *
     *
     * <img src="doc-files/textureCoords1.png"></img>
     *
     *
     *
     * Questo fatto comporta il fatto che i triangoli generati nel sistema di coordinate delle texture (U,V) avranno i valori con le V maggiori in alto. Dal punto di vista grafico questo si traduce in una rappresentazione capovolta rispetto
     * a quella vista startX il sistema di coordinate dei vertici.
     *
     *
     * <img src="doc-files/createPlaneWithVerticalStripesTexture.png"></img>
     *
     * <h2>Altro</h2>
     *
     *
     *
     * Le coordinate z sono tutte impostate a 0. Le opzioni relative verranno ignorate, dato che non vengono utilizzati.
     *
     *
     *
     *
     * **La differenza sostaziale con il metodo `createTiledPlane` è che quest'ultimo ha tutti i triangoli separati, quindi il modo di gestire le texture è diverso!**
     *
     *
     * @param width
     * larghezza dello shape
     * @param height
     * altezza dello shape
     * @param verticalBand
     * numero di bande verticali, da 1
     * @param textureRatio
     * indica
     * @return shape
     */
    /*
     * public static Mesh createPlaneWithVerticalStripes(float width, float height, int verticalBand, MeshOptions options) { Mesh shape = new Mesh();
     *
     * int numVertices = (verticalBand * 2) + 2;
     *
     * // vertici shape.vertexCount = numVertices; // shape.vertices = ByteBuffer.allocateDirect(numVertices * VERTEX_DIMENSION * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); // shape.vertices.position(0); //
     * shape.verticesCoords = new float[numVertices * VERTEX_DIMENSION]; shape.vertices = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation); // impostiamo boundingbox shape.boundingBox.width =
     * width; shape.boundingBox.height = height; shape.boundingBox.depth = 0f;
     *
     * float startX = -width / 2f; float deltaX = width / verticalBand; float currentX = startX; float highY = height / 2.0f; float lowY = -highY;
     *
     * for (int i = 0; i < (numVertices * VERTEX_DIMENSION); i += 2 * VERTEX_DIMENSION) { // vertex higher startX, startY, z shape.vertices.coords[i + 0] = currentX; shape.vertices.coords[i + 1] = highY; shape.vertices.coords[i + 2] = 0.0f;
     *
     * // vertex lower startX, startY, z shape.vertices.coords[i + 3] = currentX; shape.vertices.coords[i + 4] = lowY; shape.vertices.coords[i + 5] = 0.0f;
     *
     * currentX += deltaX; }
     *
     * // shape.vertices.put(shape.verticesCoords).position(0); // calcoliamo boundingSphere radius, ovvero il raggio della sfera che // contiene lo shape shape.boundingSphereRadius = (float)
     * Math.sqrt(XenonMath.squareDistanceFromOrigin(shape.vertices.coords[0], shape.vertices.coords[1], shape.vertices.coords[2]));
     *
     * if (options.updateAfterCreation) { shape.vertices.update(); }
     *
     * // texture if (options.textureEnabled) { shape.texturesEnabled = true; // impostiamo il numero di texture shape.texturesCount = options.texturesCount; // numero di texture shape.textures = new TextureBuffer[options.texturesCount];
     *
     * for (int a = 0; a < shape.textures.length; a++) { shape.textures[a] = BufferManager.createTextureBuffer(numVertices, options.bufferOptions.textureAllocation);
     *
     * // normalizzato, da 0 a 1 lowY = options.textureCoordRect.y; // valore assoluto highY = (float) (options.textureCoordRect.y + options.textureCoordRect.height);
     *
     * if (options.textureInverseY) { // invertiamo float temp = lowY; lowY = highY; highY = temp; }
     *
     * currentX = options.textureCoordRect.x; deltaX = (float) (options.textureCoordRect.width) / verticalBand;
     *
     * for (int i = 0; i < (numVertices * TEXTURE_DIMENSION); i += TEXTURE_DIMENSION * 2) { // vertex higher startX, startY shape.textures[a].coords[i + 0] = currentX; shape.textures[a].coords[i + 1] = lowY;
     *
     * // vertex lower startX, startY shape.textures[a].coords[i + 2] = currentX; shape.textures[a].coords[i + 3] = highY;
     *
     * currentX += deltaX; }
     *
     * if (options.updateAfterCreation) { shape.textures[a].update(); } // shape.textures[a].put(shape.texturesCoords[a]).position(0); }
     *
     * } else { shape.texturesEnabled = false; }
     *
     * // attribute if (options.attributesEnabled) { shape.attributesEnabled = true; // impostiamo il numero di texture shape.attributesCount = options.attributesCount; // numero di texture shape.attributes = new
     * AttributeBuffer[options.attributesCount];
     *
     * for (int a = 0; a < shape.attributes.length; a++) { shape.attributes[a] = BufferManager.createAttributeBuffer(numVertices, options.attributesDimension, options.bufferOptions.attributeAllocation);
     *
     * for (int i = 0; i < (numVertices * options.attributesDimension.value); i++) { shape.attributes[a].coords[i] = 0; }
     *
     * if (options.updateAfterCreation) { shape.attributes[a].update(); } }
     *
     * } else { shape.attributesEnabled = false; }
     *
     * // normali if (options.normalsEnabled) { shape.normalsEnabled = true; shape.normals = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.normalAllocation);
     *
     * for (int i = 0; i < numVertices * VERTEX_DIMENSION; i += VERTEX_DIMENSION) { shape.normals.coords[i + 0] = 0f; shape.normals.coords[i + 1] = 0f; shape.normals.coords[i + 2] = 1f; }
     *
     * // Quando passo un array ad un direct buffer devo poi riposizionare a 0 if (options.updateAfterCreation) { shape.normals.update(); } } else { shape.normalsEnabled = false; }
     *
     * // colore if (options.colorsEnabled) { shape.colorsEnabled = true; shape.colors = BufferManager.createColorBuffer(shape.vertexCount, options.bufferOptions.colorAllocation);
     *
     * ColorModifier.setColor(shape, options.color, options.updateAfterCreation);
     *
     * } else { shape.colorsEnabled = false; }
     *
     * // indici: non ci sono indici shape.indexesCount = 0; shape.indexesEnabled = false;
     *
     * // stile di disegno shape.drawMode = MeshDrawModeType.TRIANGLE_STRIP;
     *
     * return shape; }
     */
    /**
     *
     *
     * Realizza un piano suddiviso in righe e colonne. Ogni tile o cella è completamente scollegata dalle altre, sia in termini di vertici che di texture. Ogni cella è composta da due triangoli.
     *
     *
     *
     *
     * I tile vengono posizionati in modo tale da avere il sistema di riferimento nel centro della griglia che si va a creare.
     *
     *
     *
     *
     * Dal punto di vista delle texture, le coordinate va da 0 a 1 per tutto il mesh. Ogni tile ha quindi un pezzo di texture.
     *
     *
     *
     *
     * Si basa sui QUAD.
     *
     *
     *
     *
     * Le coordinate delle texture sono di default messe in senso antiorario.
     *
     *
     * <img src="doc-files/glFrontFace.jpg"></img>
     *
     *
     *
     * Il senso antiorario nelle texture è il valore di default di glFrontFace.
     *
     *
     * @param gridWidth
     * larghezza della griglia
     * @param gridHeight
     * altezza della griglia
     * @param gridRows
     * numero di righe di tile
     * @param gridCols
     * numero di colonne di tile
     * @param options
     * opzioni
     * @return mesh
     */
    fun createTiledGrid(gridWidth: Float, gridHeight: Float, gridRows: Int, gridCols: Int, options: MeshOptions): MeshGrid {
        var base: Int
        val shape = MeshGrid()

        // imposta dimensioni griglia
        shape.gridCols = gridCols
        shape.gridRows = gridRows

        // ogni quadrato contiene 4 vertici (3 startX triangoli , ma 2 sono condivisi)
        val numVertices = gridCols * gridRows * VERTEX_PER_QUAD

        // vertici
        shape.vertexCount = numVertices
        shape.vertices = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation)

        // il calcolo delle dimensioni delle tile viene fatto dividendo gli input
        val tileWidth = (gridWidth / gridCols).toInt()
        val tileHeight = (gridHeight / gridRows).toInt()

        // impostiamo boundingbox
        shape.boundingBox[gridWidth, gridHeight] = 0f
        val startX = -gridWidth / 2f
        val startY = gridHeight / 2f
        var deltaX = tileWidth.toFloat()
        var deltaY = tileHeight.toFloat()
        shape.tileWidth = tileWidth
        shape.tileHeight = tileHeight

        // ci posizioniamo in alto a sinistra
        var currentX = startX
        var currentY = startY
        for (j in 0 until gridRows) {
            // ci posizioniamo all'inizio della riga
            base = gridCols * VERTEX_PER_QUAD * VERTEX_DIMENSION * j
            currentX = startX
            var i = 0
            while (i < gridCols * VERTEX_PER_QUAD * VERTEX_DIMENSION) {

                // vertex higher startX, startY, z
                shape.vertices!!.coords!![base + i + 0] = currentX
                shape.vertices!!.coords!![base + i + 1] = currentY
                shape.vertices!!.coords!![base + i + 2] = 0.0f

                // vertex lower startX, startY, z
                shape.vertices!!.coords!![base + i + 3] = currentX
                shape.vertices!!.coords!![base + i + 4] = currentY - deltaY
                shape.vertices!!.coords!![base + i + 5] = 0.0f

                // vertex higher startX, startY, z
                shape.vertices!!.coords!![base + i + 6] = currentX + deltaX
                shape.vertices!!.coords!![base + i + 7] = currentY - deltaY
                shape.vertices!!.coords!![base + i + 8] = 0.0f

                // vertex higher startX, startY, z
                shape.vertices!!.coords!![base + i + 9] = currentX + deltaX
                shape.vertices!!.coords!![base + i + 10] = currentY
                shape.vertices!!.coords!![base + i + 11] = 0.0f
                currentX += deltaX
                i += VERTEX_PER_QUAD * VERTEX_DIMENSION
            }
            currentY -= deltaY
        }

        // shape.vertices.put(shape.verticesCoords).position(0);
        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        shape.boundingSphereRadius =
            Math.sqrt(squareDistanceFromOrigin(shape.vertices!!.coords!![0], shape.vertices!!.coords!![1], shape.vertices!!.coords!![2]).toDouble()).toFloat()
        if (options.updateAfterCreation) {
            shape.vertices!!.update()
        }

        // texture
        if (options.textureEnabled) {
            var t: Float
            var s: Float
            shape.texturesEnabled = true
            // impostiamo il numero di texture
            shape.texturesCount = options.texturesCount
            val xFactor = options.textureCoordRect.width / gridCols
            val yFactor = options.textureCoordRect.height / gridRows
            shape.textures = Array(options.texturesCount) { index ->
                val texture = BufferManager.createTextureBuffer(numVertices, options.bufferOptions.textureAllocation)
                deltaX = 1f / gridCols
                deltaY = yFactor * if (options.textureInverseY) -1 else 1
                for (j in 0 until gridRows) {
                    base = gridCols * VERTEX_PER_QUAD * TEXTURE_DIMENSION * j

                    // lo facciamo per ribaltare le coord texture. startY va da 0 a rows
                    t = options.textureCoordRect.y + yFactor * if (options.textureInverseY) gridRows - j else j
                    var i = 0
                    while (i < gridCols * VERTEX_PER_QUAD * TEXTURE_DIMENSION) {
                        s = options.textureCoordRect.x + xFactor * i / (VERTEX_PER_QUAD * TEXTURE_DIMENSION)
                        texture.coords!![base + i + 0] = s
                        texture.coords!![base + i + 1] = t
                        texture.coords!![base + i + 2] = s
                        texture.coords!![base + i + 3] = t + deltaY
                        texture.coords!![base + i + 4] = s + deltaX
                        texture.coords!![base + i + 5] = t + deltaY
                        texture.coords!![base + i + 6] = s + deltaX
                        texture.coords!![base + i + 7] = t
                        i += VERTEX_PER_QUAD * TEXTURE_DIMENSION
                    }
                }
                if (options.updateAfterCreation) {
                    texture.update()
                }
                texture
            }
        }

        // attribute
        if (options.attributesEnabled) {
            shape.attributesEnabled = true
            shape.attributesCount = options.attributesCount
            shape.attributes = Array(options.attributesCount) { index ->
                val attributes = BufferManager.createAttributeBuffer(numVertices, options.attributesDimension, options.bufferOptions.attributeAllocation)
                for (j in 0 until gridRows) {
                    base = gridCols * VERTEX_PER_QUAD * options.attributesDimension.value * j
                    for (i in 0 until gridCols * VERTEX_PER_QUAD * options.attributesDimension.value) {
                        attributes.coords!![base + i] = 0f
                    }
                }
                if (options.updateAfterCreation) {
                    attributes.update()
                }

                attributes
            }
        }

        // normali
        if (options.normalsEnabled) {
            shape.normalsEnabled = true
            shape.normals = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.normalAllocation)
            var i = 0
            while (i < numVertices * VERTEX_DIMENSION) {

                // startX, startY, z
                shape.normals!!.coords!![i] = 0.0f
                shape.normals!!.coords!![i + 1] = 0.0f
                shape.normals!!.coords!![i + 2] = 1.0f
                i += VERTEX_DIMENSION
            }
        } else {
            shape.normalsEnabled = false
        }

        // colore
        if (options.colorsEnabled) {
            shape.colorsEnabled = true
            shape.colors = BufferManager.createColorBuffer(numVertices, options.bufferOptions.colorAllocation)
            ColorModifier.setColor(shape, options.color, options.updateAfterCreation)
        } else {
            shape.colorsEnabled = false
        }

        // indici: ci sono sempre
        shape.indexesCount = gridCols * gridRows * INDEX_PER_QUAD
        shape.indexesEnabled = true
        shape.indexes = BufferManager.createIndexBuffer(shape.indexesCount, options.bufferOptions.indexAllocation)
        // ByteBuffer.allocateDirect( * MeshFactory.SHORT_SIZE).order(ByteOrder.nativeOrder()).asShortBuffer();
        var index = 0
        // iteriamo sulle tile
        for (i in 0 until gridCols * gridRows) {

            // ci occupiamo dei vertici
            shape.indexes!!.values!![index + 0] = (i * VERTEX_PER_QUAD + 0).toShort()
            shape.indexes!!.values!![index + 1] = (i * VERTEX_PER_QUAD + 1).toShort()
            shape.indexes!!.values!![index + 2] = (i * VERTEX_PER_QUAD + 2).toShort()
            shape.indexes!!.values!![index + 3] = (i * VERTEX_PER_QUAD + 2).toShort()
            shape.indexes!!.values!![index + 4] = (i * VERTEX_PER_QUAD + 3).toShort()
            shape.indexes!!.values!![index + 5] = (i * VERTEX_PER_QUAD + 0).toShort()
            index += INDEX_PER_QUAD

            // dobbiamo convertire da # di tile a # di indice vertice. In ogni tile ci sono 4 vertici, quindi il passaggio è semplice.
        }
        // shape.indices.position(0);
        if (options.updateAfterCreation) {
            shape.indexes!!.update()
        }

        // stile di disegno
        // shape.drawMode = MeshDrawModeType.TRIANGLES;

        // stile di disegno
        shape.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
        return shape
    }

    /**
     *
     *
     * Realizza una mattonella o sprite, a seconda di come lo voglia utilizzare. E' la base delle tiledGrid, in cui ogni tile è una storia a se stante.
     *
     *
     *
     *
     * **Serve alle tiledMap! Non posso sostituirla con plane **
     *
     *
     *
     *
     * Questa procedura è basata su:
     *
     * <pre>
     * http://www.learnopengles.com/android-lesson-eight-an-introduction-to-index-buffer-objects-ibos
    </pre> *
     *
     *
     *
     * **La differenza sostaziale con il metodo `createPlaneWithVerticalStripes` è che quest'ultimo ha tutti i triangoli separati, quindi il modo di gestire le texture è diverso!**
     *
     *
     * @param tileWidth
     * @param tileHeight
     * @param options
     *
     * @return mesh
     */
    fun createTile(tileWidth: Float, tileHeight: Float, options: MeshOptions): MeshTile {
        val shape = MeshTile()
        shape.boundingBox[tileWidth, tileHeight] = 0f

        // ogni quadrato contiene 4 vertici
        val numVertices = VERTEX_PER_QUAD

        // vertici
        shape.vertexCount = numVertices
        shape.vertices = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation)
        val startX = -tileWidth / 2f
        var deltaX = tileWidth
        shape.tileWidth = tileWidth
        shape.tileHeight = tileHeight
        var currentX = startX
        var highY = tileHeight / 2.0f
        var lowY = -highY

        // 0 --- 3
        // | \ |
        // | \ |
        // | \ |
        // 1 --- 2
        var i = 0
        while (i < shape.vertexCount) {


            // vertex higher startX, startY, z
            shape.vertices!!.coords!![i + 0] = currentX
            shape.vertices!!.coords!![i + 1] = highY
            shape.vertices!!.coords!![i + 2] = 0.0f

            // vertex lower startX, startY, z
            shape.vertices!!.coords!![i + 3] = currentX
            shape.vertices!!.coords!![i + 4] = lowY
            shape.vertices!!.coords!![i + 5] = 0.0f

            // vertex higher startX, startY, z
            shape.vertices!!.coords!![i + 6] = currentX + deltaX
            shape.vertices!!.coords!![i + 7] = lowY
            shape.vertices!!.coords!![i + 8] = 0.0f

            // vertex higher startX, startY, z
            shape.vertices!!.coords!![i + 9] = currentX + deltaX
            shape.vertices!!.coords!![i + 10] = highY
            shape.vertices!!.coords!![i + 11] = 0.0f
            currentX += deltaX
            i += VertexBuffer.POSITION_DIMENSIONS * VERTEX_PER_QUAD
        }

        // shape.vertices.put(shape.verticesCoords).position(0);
        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        shape.boundingSphereRadius =
            Math.sqrt(squareDistanceFromOrigin(shape.vertices!!.coords!![0], shape.vertices!!.coords!![1], shape.vertices!!.coords!![2]).toDouble()).toFloat()
        if (options.updateAfterCreation) {
            shape.vertices!!.update()
        }

        // texture
        if (options.textureEnabled) {
            shape.texturesEnabled = true
            // impostiamo il numero di texture
            shape.texturesCount = options.texturesCount
            shape.textures = Array(options.texturesCount) { index ->
                val texture = BufferManager.createTextureBuffer(numVertices, options.bufferOptions.textureAllocation)
                // shape.texturesCoords[a] = new float[numVertices * TEXTURE_DIMENSION];

                // normalizzato, da 0 a 1
                lowY = options.textureCoordRect.y
                highY = (options.textureCoordRect.height + options.textureCoordRect.y)
                currentX = options.textureCoordRect.x
                deltaX = (options.textureCoordRect.width + options.textureCoordRect.x)
                TextureModifier.setTextureCoords(shape, index, currentX, deltaX, lowY, highY)

                // Quando passo un array ad un direct buffer devo poi riposizionare a 0
                if (options.updateAfterCreation) {
                    texture.update()
                }

                texture
            }
        }

        // normali
        if (options.normalsEnabled) {
            shape.normalsEnabled = true
            shape.normals = BufferManager.createVertexBuffer(numVertices, options.bufferOptions.normalAllocation)
            var i = 0
            while (i < numVertices * VERTEX_DIMENSION) {
                shape.normals!!.coords!![i + 0] = 0f
                shape.normals!!.coords!![i + 1] = 0f
                shape.normals!!.coords!![i + 2] = 1f
                i += VERTEX_DIMENSION
            }

            // Quando passo un array ad un direct buffer devo poi riposizionare a 0
            if (options.updateAfterCreation) {
                shape.normals!!.update()
            }
        } else {
            shape.normalsEnabled = false
        }

        // colore
        if (options.colorsEnabled) {
            shape.colorsEnabled = true
            shape.colors = BufferManager.createColorBuffer(numVertices, options.bufferOptions.colorAllocation)
            ColorModifier.setColor(shape, options.color, options.updateAfterCreation)
        } else {
            shape.colorsEnabled = false
        }

        // indici: ci sono sempre indici
        shape.indexesCount = INDEX_PER_QUAD
        shape.indexesEnabled = true
        shape.indexes = BufferManager.createIndexBuffer(INDEX_PER_QUAD, options.bufferOptions.indexAllocation)
        shape.indexes!!.values!![0] = 0
        shape.indexes!!.values!![1] = 1
        shape.indexes!!.values!![2] = 2
        shape.indexes!!.values!![3] = 2
        shape.indexes!!.values!![4] = 3
        shape.indexes!!.values!![5] = 0

        // Quando passo un array ad un direct buffer devo poi riposizionare a 0
        if (options.updateAfterCreation) {
            shape.indexes!!.update()
        }

        // stile di disegno
        shape.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
        return shape
    }

    /**
     * Crea uno shape sprite le cui dimensioni sono state passate come parametri.
     *
     * @param widthValue
     * larghezza dello sprite
     * @param heightValue
     * altezza dello sprite
     * @param options
     * opzioni di costruzione dello sprite
     *
     * @return shape
     */
    fun createSprite(widthValue: Float, heightValue: Float, options: MeshOptions): MeshSprite {
        return createTile(widthValue, heightValue, options)
    }

    /**
     * Crea la rappresentazione wireframe di una mesh. I vertici e le texture vengono condivisi (referenziati). Vengono invece creati nuovi indici. Questo è possibile in quanto le mesh hanno un modo di essere costruite uniformi.
     *
     * @param inputMesh
     * @return mesh
     */
    fun createWireframe(inputMesh: Mesh): MeshWireframe {
        if (!inputMesh.indexesEnabled) {
            throw XenonRuntimeException("mesh " + inputMesh.name + " must contain indexes to be in wireframe mode")
        }
        if (inputMesh.indexes!!.values == null) {
            throw XenonRuntimeException("mesh " + inputMesh.name + " does not contain indexes to be in wireframe mode")
        }
        val meshWireframe = MeshWireframe(inputMesh)
        when (inputMesh.type) {
            MeshType.QUAD_BASED -> {

                // numero di quadrati per il mesh di input
                // 6 è il numero di indici per 2 triangoli
                // 8 è il numero di indici per 4 linee
                val n = inputMesh.indexesCount / 6 * 8
                meshWireframe.indexesEnabled = true
                meshWireframe.indexesCount = n
                // per ogni quadrato ci sono 4 linee
                var j = 0
                meshWireframe.indexes = BufferManager.createIndexBuffer(n, inputMesh.indexes!!.allocation)
                var i = 0
                while (i < inputMesh.indexesCount) {

                    // disegniamo per quad
                    meshWireframe.indexes!!.values!![j + 0] = inputMesh.indexes!!.values!![i + 0]
                    meshWireframe.indexes!!.values!![j + 1] = inputMesh.indexes!!.values!![i + 1]
                    meshWireframe.indexes!!.values!![j + 2] = inputMesh.indexes!!.values!![i + 1]
                    meshWireframe.indexes!!.values!![j + 3] = inputMesh.indexes!!.values!![i + 2]
                    meshWireframe.indexes!!.values!![j + 4] = inputMesh.indexes!!.values!![i + 2]
                    meshWireframe.indexes!!.values!![j + 5] = inputMesh.indexes!!.values!![i + 4]
                    meshWireframe.indexes!!.values!![j + 6] = inputMesh.indexes!!.values!![i + 4]
                    meshWireframe.indexes!!.values!![j + 7] = inputMesh.indexes!!.values!![i + 0]
                    j += 8
                    i += 6
                }
            }
            else -> {}
        }
        meshWireframe.indexes!!.update()
        return meshWireframe
    }
    /**
     * Crea uno quad shape le cui dimensioni sono state passate come parametri.
     *
     * @param widthValue
     * larghezza del quad
     * @param heightValue
     * altezza del quad
     * @param options
     * opzioni di costruzione dello sprite
     *
     * @return quad shape
     */
    /*
     * public static QuadMesh createQuad(float widthValue, float heightValue, MeshOptions options) { QuadMesh shape = new QuadMesh();
     *
     * shape.boundingBox.width = widthValue; shape.boundingBox.height = heightValue; shape.boundingBox.depth = 0f;
     *
     * // ogni quadrato contiene 6 vertici (3 startX triangoli) int numVertices = VERTEX_PER_QUAD;
     *
     * // vertici shape.vertexCount = numVertices; shape.vertices = BufferManager.createVertexBuffer(numVertices, VertexBuffer.POSITION_DIMENSIONS, options.vertexBufferOptions); // shape.vertices =
     * ByteBuffer.allocateDirect(numVertices * VERTEX_DIMENSION * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); // shape.verticesCoords = new float[numVertices * VERTEX_DIMENSION];
     *
     * float width = widthValue; float height = heightValue;
     *
     * float startX = -width / 2f; float deltaX = widthValue; //shape.tileWidth = tileWidth; //shape.tileHeight = tileHeight;
     *
     * float currentX = startX; float highY = height / 2.0f; float lowY = -highY;
     *
     * for (int i = 0; i < (numVertices * VertexBuffer.POSITION_DIMENSIONS); i += VertexBuffer.POSITION_DIMENSIONS*VERTEX_PER_QUAD) {
     *
     * // vertex higher startX, startY, z shape.vertices.coords[i + 0] = currentX; shape.vertices.coords[i + 1] = highY; shape.vertices.coords[i + 2] = 0.0f;
     *
     * // vertex lower startX, startY, z shape.vertices.coords[i + 3] = currentX; shape.vertices.coords[i + 4] = lowY; shape.vertices.coords[i + 5] = 0.0f;
     *
     * // vertex higher startX, startY, z shape.vertices.coords[i + 6] = currentX + deltaX; shape.vertices.coords[i + 7] = lowY; shape.vertices.coords[i + 8] = 0.0f;
     *
     * // vertex higher startX, startY, z shape.vertices.coords[i + 9] = currentX + deltaX; shape.vertices.coords[i + 10] = highY; shape.vertices.coords[i + 11] = 0.0f;
     *
     * currentX += deltaX; } if (options.vertexBufferOptions.updateAfterCreation) { shape.vertices.update(); } // shape.vertices.put(shape.verticesCoords).position(0); // calcoliamo boundingSphere radius, ovvero il raggio della sfera che //
     * contiene lo shape shape.boundingSphereRadius = (float) Math.sqrt(XenonMath.squareDistanceFromOrigin(shape.vertices.coords[0], shape.vertices.coords[1], shape.vertices.coords[2]));
     *
     * // texture if (options.textureEnabled) { shape.texturesEnabled = true; // impostiamo il numero di texture shape.texturesCount = options.texturesCount;
     *
     * shape.textures = new FloatBuffer[options.texturesCount]; shape.texturesCoords = new float[options.texturesCount][];
     *
     * for (int a = 0; a < shape.texturesCount; a++) { shape.textures[a] = ByteBuffer.allocateDirect(numVertices * TEXTURE_DIMENSION * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); shape.texturesCoords[a] = new
     * float[numVertices * TEXTURE_DIMENSION];
     *
     * // normalizzato, da 0 a 1 lowY = 0.0f; highY = (float) (1f / options.textureAspectRatio.aspectXY );
     *
     * currentX = 0f; deltaX = (float) ((1f / options.textureAspectRatio.aspectXY) );
     *
     * TextureQuadModifier.setTextureCoords(shape, a, currentX, (float) (1f / options.textureAspectRatio.aspectXY), lowY, highY); } }
     *
     * // normali if (options.normalsEnabled) { shape.normalsEnabled = true; shape.normals = ByteBuffer.allocateDirect(numVertices * 3 * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); // shape.normals.position(0); for (int i =
     * 0; i < (numVertices * 3); i += 3) { // startX, startY, z shape.normals.put(i, 0.0f); shape.normals.put(i + 1, 0.0f); shape.normals.put(i + 2, 1.0f); } } else { shape.normalsEnabled = false; }
     *
     * // colore if (options.colorsEnabled) { shape.colorsEnabled = true; int n = shape.vertexCount * COLOR_ELEMENTS; shape.colors = ByteBuffer.allocateDirect(n * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); shape.colorsValues = new
     * float[n]; ColorModifier.setColor(shape, Color.WHITE);
     *
     * } else { shape.colorsEnabled = false; }
     *
     * // 0 -- 3 // | \ | // | \ | // 1 -- 2 // // Triangolo 1: 0 - 1 - 2 // Triangolo 2: 0 - 2 - 3
     *
     * // indici: devono essere sempre definiti, e ce sono quanti i triangoli che definiscono il shape.indexesCount = MeshFactory.VERTEX_PER_TRIANGLED_QUAD; shape.indexesEnabled = true;
     * shape.indexes=BufferManager.createIndexBuffer(shape.indexesCount, BufferAllocationOptions.build()); for (short i=0; i<shape.indexesCount;i++) { shape.indexes.values[i+0]=(short) (i+0); shape.indexes.values[i+1]=(short)
     * (i+1); shape.indexes.values[i+2]=(short) (i+2);
     *
     * shape.indexes.values[i+3]=(short) (i+0); shape.indexes.values[i+4]=(short) (i+2); shape.indexes.values[i+5]=(short) (i+3); } shape.indexes.update();
     *
     * // stile di disegno shape.drawStyle = MeshDrawModeType.TRIANGLES;
     *
     * return shape; }
     */
    /**
     * Crea un plane indicizzato.
     *
     * @param width
     * @param height
     * @param options
     * @return
     */
    /*
     * public static XmlDataModel createIndexSimplePlane(float width, float height, MeshOptions options) { XmlDataModel shape = new XmlDataModel();
     *
     * int numVertices = 4; int numIndices = 6;
     *
     * float[] cubeTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };
     *
     * short[] cubeIndices = { 0, 2, 1, 0, 3, 2 };
     *
     * // vertici float[] vertici = { -0.5f * width, -0.5f * height, 0f, 0.5f * width, -0.5f * height, 0f, 0.5f * width, 0.5f * height, 0f, -0.5f * width, 0.5f * height, 0f };
     *
     * shape.vertices = BufferManager.createVertexBuffer(4, VertexBuffer.POSITION_DIMENSIONS, options.vertexBufferOptions);
     *
     * if (options.vertexBufferOptions.updateAfterCreation) { shape.vertices.update(); }
     *
     * // shape.verticesCoords = new float[numVertices * VERTEX_DIMENSION]; // shape.vertices = ByteBuffer.allocateDirect(numVertices * VERTEX_DIMENSION * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); //
     * shape.vertices.put(vertici).position(0); // calcoliamo boundingSphere radius, ovvero il raggio della sfera che // contiene lo shape shape.boundingSphereRadius = (float)
     * Math.sqrt(XenonMath.squareDistanceFromOrigin(shape.vertices.coords[0], shape.vertices.coords[1], shape.vertices.coords[2])); shape.boundingBox.width = width; shape.boundingBox.height = height; shape.boundingBox.depth = 0f;
     *
     * // normali if (options.normalsEnabled) { float[] normals = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f };
     *
     * shape.normalsEnabled = true; shape.normals = ByteBuffer.allocateDirect(numVertices * 3 * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); shape.normals.put(normals).position(0); }
     *
     * // texture if (options.textureEnabled) { shape.texturesEnabled = true; // impostiamo il numero di texture shape.texturesCount = options.texturesCount;
     *
     * shape.textures = new FloatBuffer[options.texturesCount]; shape.texturesCoords = new float[options.texturesCount][];
     *
     * // per ogni texture prendiamo le coordinate di cubeText for (int a = 0; a < options.texturesCount; a++) { for (int i = 0; i < numVertices * 2; i++) { shape.texturesCoords[a][i] = cubeTex[i]; }
     *
     * shape.textures[a] = ByteBuffer.allocateDirect(numVertices * 2 * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); shape.textures[a].put(shape.texturesCoords[a]).position(0); }
     *
     * }
     *
     * shape.indexesCount = numIndices; shape.indexes=BufferManager.createIndexBuffer(numIndices, BufferAllocationOptions.build()); //shape.indices = ByteBuffer.allocateDirect(numIndices *
     * SHORT_SIZE).order(ByteOrder.nativeOrder()).asShortBuffer(); //shape.indices.put(cubeIndices).position(0);
     *
     * shape.vertexCount = numVertices;
     *
     * shape.drawMode = MeshDrawModeType.TRIANGLE_STRIP;
     *
     * return shape; }
     */
    /**
     *
     *
     * Carica da file obj uno shape. Delle options, non vengono considerati i parametri che vanno a modificare le coordinate texture.
     *
     *
     *
     *
     * Provvede a caricare dai diversi tipi di formato, [MeshFileFormatType].
     *
     *
     * <h2>Obj format</h2>
     *
     *
     * Il formato è il seguente [specifiche wavefront](./doc-files/Wavefront.pdf).
     *
     *
     *
     *
     * Per l'export mediante Blender, le opzioni per l'export sono riportate qui sotto:
     *
     *
     * <img src="./doc-files/blender_obj_exporter.png"></img>
     *
     *
     *
     *
     *
     * @param context
     * @param fileName
     * @param fileFormat
     * @param options
     * @return mesh
     */
    fun loadFromAssets(context: Context?, fileName: String?, fileFormat: MeshFileFormatType?, options: MeshOptions): Mesh? {
        var mesh: Mesh? = null
        try {
            when (fileFormat) {
                MeshFileFormatType.WAVEFRONT ->                // le normali vengono calcolate per ogni vertice di triangolo, quindi in termini assoluti ogni vertice può avere più di una normale assocaita
                    mesh = WavefrontAdapter.convertModelToShape(WavefrontLoader.loadFromAsset(context, fileName), options.normalsEnabled(false).indicesEnabled(true))
                MeshFileFormatType.MAX3D -> mesh = Max3DSAdapter.convertModelToIndexedTriangleShape(Max3DSLoader.loadFromAsset(context, fileName), options)
                MeshFileFormatType.ANDROID_XML -> AndroidXmlAdapter.convertModelToIndexedTriangleShape(AndroidXmlLoader.loadFromAsset(context, fileName), options)
                MeshFileFormatType.KRIPTON_JSON -> mesh = KriptonLoader.loadMeshFromJSON(context, fileName, options)
                MeshFileFormatType.KRIPTON_XML -> mesh = KriptonLoader.loadMeshFromXML(context, fileName, options)
                else -> {}
            }
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e.message)
        }
        return mesh
    }

    fun loadFromResources(context: Context, resourceId: Int, fileFormat: MeshFileFormatType, options: MeshOptions): Mesh {
        return try {
            when (fileFormat) {
                MeshFileFormatType.WAVEFRONT ->                // le normali vengono calcolate per ogni vertice di triangolo, quindi in termini assoluti ogni vertice può avere più di una normale assocaita
                    WavefrontAdapter.convertModelToShape(WavefrontLoader.loadFromResources(context, resourceId), options.normalsEnabled(false).indicesEnabled(true))
                MeshFileFormatType.MAX3D -> Max3DSAdapter.convertModelToIndexedTriangleShape(Max3DSLoader.loadFromResources(context, resourceId), options)
                MeshFileFormatType.ANDROID_XML -> AndroidXmlAdapter.convertModelToIndexedTriangleShape(AndroidXmlLoader.loadFromResources(context, resourceId), options)
                MeshFileFormatType.KRIPTON_JSON -> KriptonLoader.loadMeshFromJSON(context, resourceId, options)
                MeshFileFormatType.KRIPTON_XML -> KriptonLoader.loadMeshFromXML(context, resourceId, options)
            }
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e.message)
        }
    }
}
