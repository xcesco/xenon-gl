package com.abubusoft.xenon.mesh.persistence.wavefront

import android.annotation.SuppressLint
import android.graphics.Color
import com.abubusoft.xenon.math.XenonMath.findNextPowerOf10
import com.abubusoft.xenon.mesh.*
import com.abubusoft.xenon.mesh.modifiers.ColorModifier.setColor
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.*
import com.abubusoft.xenon.vbo.BufferAllocationOptions
import com.abubusoft.xenon.vbo.BufferManager

object WavefrontAdapter {
    /**
     *
     *
     * converte il model in una mesh i cui vertici sono indicizzati.
     *
     *
     * @param model
     * @param shape
     * @param options
     */
    @SuppressLint("UseSparseArrays")
    internal fun convertModelToIndexedMesh(model: WavefrontModelData, shape: Mesh, options: MeshOptions) {
        val mapFlatVerticeKeys = HashMap<Int, Int>()
        val flatVertices = ArrayList<FlatVertexF>()
        val vkb = VertexKeyBuilder(model, options)
        var subvertexIndex = 0
        var currentTriangle: WavefrontModelData.Face?
        var v: VertexF?
        val mins = FloatArray(3)
        val maxs = FloatArray(3)
        var uid: Int
        var vertexIndex: Int
        var fv: FlatVertexF

        // mesh name
        shape.name = model.name
        val numTriangles = model.triangles!!.size

        // iteriamo sui triangoli per ottenere l'elenco dei vertici flat
        for (i in 0 until numTriangles) {
            currentTriangle = model.triangles!![i]

            // ci occupiamo dei vertici
            for (si in 0 until MeshFactory.VERTEX_PER_TRIANGLE) {
                // work for boundingbox
                v = model.vertices!![currentTriangle!!.vertexIndex[si]]
                MeshHelper.buildMinMaxArray(v!!.x, v.y, v.z, mins, maxs)

                // ricaviamo values
                uid = vkb.getUID(currentTriangle.vertexIndex[si], currentTriangle.textureIndex[si], currentTriangle.normalIndex[si])
                if (!mapFlatVerticeKeys.containsKey(uid)) {
                    // ASSERT: non abbiamo l'elemento inserito
                    fv = FlatVertexF()
                    fv.vertex = model.vertices!![currentTriangle.vertexIndex[si]]
                    if (currentTriangle.textureIndex[si] >= 0) {
                        fv.tex = model.tex!![currentTriangle.textureIndex[si]]
                    }
                    if (currentTriangle.normalIndex[si] >= 0) {
                        fv.normal = model.normals!![currentTriangle.normalIndex[si]]
                    }
                    flatVertices.add(fv)
                    vertexIndex = flatVertices.size - 1
                    fv.index = vertexIndex
                    mapFlatVerticeKeys[uid] = vertexIndex
                } else {
                    fv = flatVertices[mapFlatVerticeKeys[uid]!!]
                }

                // per il vertice, registriamo la sua chiave
                currentTriangle.indexes[si] = fv.index
            }
        }
        MeshHelper.defineBoundaries(shape, mins, maxs)

        // ora abbiamo tutto, possiamo costruire i vari array

        // vertici
        val numVertices = flatVertices.size
        shape.vertexCount = flatVertices.size
        shape.vertices = BufferManager.instance().createVertexBuffer(numVertices, options.bufferOptions.vertexAllocation)
        subvertexIndex = 0
        for (i in 0 until numVertices) {
            val fvf = flatVertices[i]
            shape.vertices!!.coords!![subvertexIndex + 0] = fvf.vertex!!.x
            shape.vertices!!.coords!![subvertexIndex + 1] = fvf.vertex!!.y
            shape.vertices!!.coords!![subvertexIndex + 2] = fvf.vertex!!.z
            subvertexIndex += MeshFactory.VERTEX_DIMENSION
        }
        shape.vertices!!.update()

        // texture
        if (options.textureEnabled && model.tex!!.size > 0) {
            shape.texturesEnabled = true
            // impostiamo il numero di texture
            shape.texturesCount = options.texturesCount
            // numero di texture
            // shape.texturesCoords = new float[options.texturesCount][];
            shape.textures = arrayOfNulls(options.texturesCount)

            // allochiamo i texture buffer
            for (a in shape.textures.indices) {
                shape.textures[a] = BufferManager.instance().createTextureBuffer(numVertices, options.bufferOptions.textureAllocation)
                // shape.textures[a] = ByteBuffer.allocateDirect(numVertices * MeshFactory.TEXTURE_DIMENSION *
                // MeshFactory.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
            }
            subvertexIndex = 0
            for (i in 0 until numVertices) {
                val fvf = flatVertices[i]

                // ci occupiamo delle texture
                for (a in shape.textures.indices) {
                    shape.textures[a].coords!![subvertexIndex + 0] = fvf.tex!!.u

                    // normalmente quando esportiamo, invertiamo startY con z.
                    // mettiamo 1 - v, per avere lo stesso effetto
                    // su 3d studio max.
                    shape.textures[a].coords!![subvertexIndex + 1] = 1 - fvf.tex!!.v
                }
                subvertexIndex += MeshFactory.TEXTURE_DIMENSION
            }
            for (a in shape.textures.indices) {
                shape.textures[a].update()
                // shape.textures[a].put(shape.texturesCoords[a]).position(0);
            }
        } else {
            shape.texturesEnabled = false
        }

        // normali
        if (options.normalsEnabled && model.normals!!.size > 0) {
            shape.normalsEnabled = true
            shape.normals = BufferManager.instance().createVertexBuffer(numVertices, options.bufferOptions.normalAllocation)
            subvertexIndex = 0
            // iteriamo sui triangoli
            for (i in 0 until numTriangles) {
                val fvf = flatVertices[i]

                // ci occupiamo dei vertici
                for (si in 0 until MeshFactory.VERTEX_PER_TRIANGLE) {
                    shape.normals!!.coords!![subvertexIndex + 0] = fvf.normal!!.x
                    shape.normals!!.coords!![subvertexIndex + 1] = fvf.normal!!.y
                    shape.normals!!.coords!![subvertexIndex + 2] = fvf.normal!!.z
                }
                subvertexIndex += MeshFactory.TEXTURE_DIMENSION
            }
        } else {
            shape.normalsEnabled = false
        }

        // colore
        if (options.colorsEnabled) {
            shape.colorsEnabled = true
            shape.colors = BufferManager.instance().createColorBuffer(numVertices, options.bufferOptions.vertexAllocation)
            setColor(shape, Color.WHITE, true)
        } else {
            shape.colorsEnabled = false
        }

        // indici: ci sono sempre
        shape.indexesCount = numTriangles * MeshFactory.VERTEX_PER_TRIANGLE
        shape.indexesEnabled = true
        shape.indexes = BufferManager.instance().createIndexBuffer(numTriangles * MeshFactory.VERTEX_PER_TRIANGLE, options.bufferOptions.indexAllocation)
        // ByteBuffer.allocateDirect( * MeshFactory.SHORT_SIZE).order(ByteOrder.nativeOrder()).asShortBuffer();
        subvertexIndex = 0
        // iteriamo sui triangoli
        for (i in 0 until numTriangles) {
            currentTriangle = model.triangles!![i]

            // ci occupiamo dei vertici
            shape.indexes!!.values!![subvertexIndex + 0] = currentTriangle!!.indexes[0].toShort()
            shape.indexes!!.values!![subvertexIndex + 1] = currentTriangle.indexes[1].toShort()
            shape.indexes!!.values!![subvertexIndex + 2] = currentTriangle.indexes[2].toShort()
            subvertexIndex += MeshFactory.VERTEX_PER_TRIANGLE
        }
        shape.indexes!!.update()

        // stile di disegno
        shape.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
        mapFlatVerticeKeys.clear()
        flatVertices.clear()
    }

    /**
     * Converte il model in una mesh senza indici.
     *
     * @param model
     * @param shape
     * @param options
     */
    internal fun convertModelToMesh(model: WavefrontModelData, shape: Mesh, options: MeshOptions) {
        val numVertices = model.triangles!!.size * MeshFactory.VERTEX_PER_TRIANGLE

        // vertici
        shape.vertexCount = numVertices
        // shape.vertices = ByteBuffer.allocateDirect(numVertices * MeshFactory.VERTEX_DIMENSION * MeshFactory.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        shape.vertices = BufferManager.instance().createVertexBuffer(numVertices, BufferAllocationOptions.build().vertexAllocation)
        // shape.vertices.position(0);
        // shape.verticesCoords = new float[numVertices * MeshFactory.VERTEX_DIMENSION];
        var subvertexIndex = 0
        var currentFace: WavefrontModelData.Face?
        var v: VertexF?

        // mesh name
        shape.name = model.name
        val mins = FloatArray(3)
        val maxs = FloatArray(3)
        val numTriangles = model.triangles!!.size

        // iteriamo sui triangoli
        for (i in 0 until numTriangles) {
            currentFace = model.triangles!![i]

            // ci occupiamo dei vertici
            for (si in 0 until MeshFactory.VERTEX_PER_TRIANGLE) {
                v = model.vertices!![currentFace!!.vertexIndex[si]]
                subvertexIndex = i * MeshFactory.VERTEX_PER_TRIANGLE * MeshFactory.VERTEX_DIMENSION + si * MeshFactory.VERTEX_DIMENSION
                shape.vertices!!.coords!![subvertexIndex + 0] = v!!.x
                shape.vertices!!.coords!![subvertexIndex + 1] = v.y
                shape.vertices!!.coords!![subvertexIndex + 2] = v.z
                if (mins[0] > v.x) mins[0] = v.x
                if (mins[1] > v.y) mins[1] = v.y
                if (mins[2] > v.z) mins[2] = v.z
                if (maxs[0] < v.x) maxs[0] = v.x
                if (maxs[1] < v.y) maxs[1] = v.y
                if (maxs[2] < v.z) maxs[2] = v.z

                // i += MeshFactory.VERTEX_DIMENSION;
            }
        }

        // shape.vertices.put(shape.verticesCoords).position(0);
        // if (options.vertexBufferOptions.updateAfterCreation) {
        shape.vertices!!.update()
        // }

        // impostiamo boundingbox
        shape.boundingBox[Math.abs(maxs[0] - mins[0]), Math.abs(maxs[1] - mins[1])] = Math.abs(maxs[2] - mins[2])

        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        // Se parti da una sfera avente un raggio di 5 cm, il cubo inscritto è
        // quel cubo che può essere inserito esattamente nella tua sfera, in
        // modo tale che la distanza tra due vertici del cubo che siano opposti
        // tra loro misuri in lunghezza 10 cm (che è il diametro della sfera di
        // partenza). Per calcolare con precisione la lunghezza dello spigolo
        // del cubo inscritto devi dividere il diametro della sfera per la
        // radice quadrata di 3 (1,732 circa).
        // http://vivalascuola.studenti.it/come-determinare-le-misure-di-cubi-legati-a-sfere-140075.html#steps_2
        shape.boundingSphereRadius = (0.8660254037844386 * shape.boundingBox.width).toFloat()

        // texture
        if (options.textureEnabled && model.tex!!.size > 0) {
            shape.texturesEnabled = true
            // impostiamo il numero di texture
            shape.texturesCount = options.texturesCount
            // numero di texture
            // shape.texturesCoords = new float[options.texturesCount][];
            shape.textures = arrayOfNulls(options.texturesCount)

            // allochiamo i texture buffer
            for (a in shape.textures.indices) {
                shape.textures[a] = BufferManager.instance().createTextureBuffer(numVertices, options.bufferOptions.textureAllocation)
                // shape.textures[a] = ByteBuffer.allocateDirect(numVertices * MeshFactory.TEXTURE_DIMENSION *
                // MeshFactory.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
            }
            var uv: UVCoord?
            // iteriamo sui triangoli
            for (i in 0 until numTriangles) {
                currentFace = model.triangles!![i]

                // ci occupiamo delle texture
                for (si in 0 until MeshFactory.VERTEX_PER_TRIANGLE) {
                    uv = model.tex!![currentFace!!.textureIndex[si]]
                    subvertexIndex = i * MeshFactory.VERTEX_PER_TRIANGLE * MeshFactory.TEXTURE_DIMENSION + si * MeshFactory.TEXTURE_DIMENSION
                    for (a in shape.textures.indices) {
                        shape.textures[a].coords!![subvertexIndex + 0] = uv!!.u

                        // normalmente quando esportiamo, invertiamo startY con z.
                        // mettiamo 1 - v, per avere lo stesso effetto
                        // su 3d studio max.
                        shape.textures[a].coords!![subvertexIndex + 1] = 1 - uv.v
                    }

                    // i += MeshFactory.TEXTURE_DIMENSION;
                }
            }
            for (a in shape.textures.indices) {
                shape.textures[a].update()
            }
        } else {
            shape.texturesEnabled = false
        }

        // normali
        if (options.normalsEnabled && model.normals!!.size > 0) {
            shape.normalsEnabled = true
            shape.normals = BufferManager.instance().createVertexBuffer(numVertices, options.bufferOptions.normalAllocation)

            // iteriamo sui triangoli
            var i = 0
            while (i < numTriangles) {
                currentFace = model.triangles!![i]

                // ci occupiamo dei vertici
                for (si in 0 until MeshFactory.VERTEX_PER_TRIANGLE) {
                    v = model.normals!![currentFace!!.normalIndex[si]]
                    subvertexIndex = i * MeshFactory.VERTEX_PER_TRIANGLE + si * MeshFactory.VERTEX_DIMENSION
                    shape.normals!!.coords!![subvertexIndex + 0] = v!!.x
                    shape.normals!!.coords!![subvertexIndex + 1] = v.y
                    shape.normals!!.coords!![subvertexIndex + 2] = v.z
                    i += MeshFactory.VERTEX_DIMENSION
                }
                i++
            }
            shape.normals!!.update()
        } else {
            shape.normalsEnabled = false
        }

        // colore
        if (options.colorsEnabled) {
            shape.colorsEnabled = true
            shape.colors = BufferManager.instance().createColorBuffer(shape.vertexCount, options.bufferOptions.colorAllocation)
            setColor(shape, Color.WHITE, true)
            shape.colors!!.update()
        } else {
            shape.colorsEnabled = false
        }

        // indici: non ci sono indici
        shape.indexesCount = 0
        shape.indexesEnabled = false

        // stile di disegno
        shape.drawMode = MeshDrawModeType.TRIANGLES
    }

    /**
     *
     *
     * Converte il model in una mesh
     *
     *
     * @param model
     * @param options
     * @return mesh
     */
    fun convertModelToShape(model: WavefrontModelData, options: MeshOptions): Mesh {
        val mesh = MeshHelper.create(options)
        if (options.indicesEnabled) {
            convertModelToIndexedMesh(model, mesh, options)
        } else {
            convertModelToMesh(model, mesh, options)
        }
        return mesh
    }

    /**
     *
     *
     * Builder delle chiavi per identificare univocamente i vertici.
     *
     *
     * @author Francesco Benincasa
     */
    class VertexKeyBuilder(model: WavefrontModelData, options: MeshOptions) {
        private val off1: Int
        private val off2: Int
        private val normalEnabler: Int
        private val textureEnabler: Int

        init {
            val numVertex = model.vertices!!.size
            val numTexture = if (options.textureEnabled) model.tex!!.size else 1
            normalEnabler = if (options.normalsEnabled) 1 else 0
            textureEnabler = if (options.textureEnabled) 1 else 0
            off1 = findNextPowerOf10(numVertex)
            off2 = findNextPowerOf10(numTexture)
        }

        /**
         *
         *
         * Recupera UID della tripletta vertice/texture/normal. Se gli ultimi due non hanno valore valido, non vengono presi in considerazione.
         *
         *
         * @param vertexIndex
         * @param textureIndex
         * @param normalIndex
         * @return
         */
        fun getUID(vertexIndex: Int, textureIndex: Int, normalIndex: Int): Int {
            val value: Int
            value = vertexIndex + (if (textureIndex >= 0) textureIndex * (off1 * textureEnabler) else 0) + if (normalIndex >= 0) normalIndex * (off1 * off2 * normalEnabler) else 0
            return value
        }
    }
}