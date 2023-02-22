package com.abubusoft.xenon.mesh.persistence.max3d

import com.abubusoft.xenon.mesh.*
import com.abubusoft.xenon.vbo.BufferManager
import java.util.*

object Max3DSAdapter {
    fun convertModelToIndexedTriangleShape(model: Max3dsModelData, options: MeshOptions): Mesh {
        val mesh = MeshHelper.create(options)

        // prima di tutto ottimizziamo, ovvero trovare quei vertici che sono fondamentalmente uguali.
        // in questo formato ho notato che conviene definire un epsilon di confronto.
        val fm = FlatModel()
        var current: FlatVertex
        for (i in model.indices.indices) {
            current = FlatVertex()
            current.vertex[0] = model.vertices[model.indices[i] * 3 + 0]
            current.vertex[1] = model.vertices[model.indices[i] * 3 + 1]
            current.vertex[2] = model.vertices[model.indices[i] * 3 + 2]
            if (options.textureEnabled) {
                current.tex[0] = model.textCoords[model.indices[i] * 2 + 0]
                current.tex[1] = model.textCoords[model.indices[i] * 2 + 1]
            }
            if (options.normalsEnabled) {
                current.normal[0] = model.normals[model.indices[i] * 3 + 0]
                current.normal[1] = model.normals[model.indices[i] * 3 + 1]
                current.normal[2] = model.normals[model.indices[i] * 3 + 2]
            }
            fm.addFaces(i, current)
        }
        options.indicesEnabled(true)

        // vertici min e max, servono per determinare il minimo ed il max
        val min = FloatArray(3)
        val max = FloatArray(3)

        // gli indici ci sono sempre
        run {
            val nfaces = model.indices.size
            mesh.indexesEnabled = true
            mesh.indexesCount = nfaces * 3
            mesh.indexes = BufferManager.instance().createIndexBuffer(nfaces, options.bufferOptions.indexAllocation)
            var i = 0
            while (i < nfaces) {
                mesh.indexes!!.values!![i + 0] = fm.findTranslatedIndex(model.indices[i + 0]).toShort()
                mesh.indexes!!.values!![i + 1] = fm.findTranslatedIndex(model.indices[i + 1]).toShort()
                mesh.indexes!!.values!![i + 2] = fm.findTranslatedIndex(model.indices[i + 2]).toShort()
                i += MeshFactory.VERTEX_PER_TRIANGLE
            }

            // update
            mesh.indexes!!.update()
        }

        // vertici
        run {
            val nvertex = fm.list.size
            mesh.vertexCount = nvertex
            mesh.vertices = BufferManager.instance().createVertexBuffer(nvertex, options.bufferOptions.vertexAllocation)
            var i = 0
            while (i < nvertex * MeshFactory.VERTEX_DIMENSION) {
                current = fm.list[i / 3]
                mesh.vertices!!.coords!![i + 0] = current.vertex[0]
                mesh.vertices!!.coords!![i + 1] = current.vertex[1]
                mesh.vertices!!.coords!![i + 2] = current.vertex[2]
                min[0] = Math.min(min[0], mesh.vertices!!.coords!![i + 0])
                min[1] = Math.min(min[1], mesh.vertices!!.coords!![i + 1])
                min[2] = Math.min(min[2], mesh.vertices!!.coords!![i + 2])
                max[0] = Math.max(max[0], mesh.vertices!!.coords!![i + 0])
                max[1] = Math.max(max[1], mesh.vertices!!.coords!![i + 1])
                max[2] = Math.max(max[2], mesh.vertices!!.coords!![i + 2])
                i += MeshFactory.VERTEX_DIMENSION
            }

            // update
            mesh.vertices!!.update()
        }

        // impostiamo boundingbox
        mesh.boundingBox[Math.abs(max[0] - min[0]), Math.abs(max[1] - min[1])] = Math.abs(max[2] - min[2])

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
        mesh.boundingSphereRadius = (0.8660254037844386 * mesh.boundingBox.width).toFloat()

        // texture
        if (options.textureEnabled) {
            val ntexture = fm.list.size
            mesh.textures = arrayOfNulls(options.texturesCount)
            mesh.texturesEnabled = true
            mesh.texturesCount = options.texturesCount
            for (t in 0 until mesh.texturesCount) {
                mesh.textures[t] = BufferManager.instance().createTextureBuffer(ntexture, options.bufferOptions.textureAllocation)
                var i = 0
                while (i < ntexture * 2) {
                    current = fm.list[i / 2]
                    mesh.textures[t].coords!![i + 0] = current.tex[0]
                    mesh.textures[t].coords!![i + 1] = current.tex[1]
                    i += 2
                }

                // update
                mesh.textures[t].update()
            }
        }

        // normal
        if (options.normalsEnabled) {
            val nnormals = fm.list.size
            mesh.normalsEnabled = true
            mesh.normals = BufferManager.instance().createVertexBuffer(nnormals, options.bufferOptions.textureAllocation)
            var i = 0
            while (i < nnormals * 3) {
                current = fm.list[i / 3]
                mesh.normals!!.coords!![i + 0] = current.normal[0]
                mesh.normals!!.coords!![i + 1] = current.normal[1]
                mesh.normals!!.coords!![i + 2] = current.normal[2]
                i += 3
            }

            // update
            mesh.normals!!.update()
        }

        // stile di disegno
        mesh.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
        return mesh
    }

    class FlatVertex {
        var vertex = FloatArray(3)
        var tex = FloatArray(2)
        var normal = FloatArray(3)
        var indexSet: HashSet<Int>? = HashSet()

        /* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + if (indexSet == null) 0 else indexSet.hashCode()
            result = prime * result + Arrays.hashCode(normal)
            result = prime * result + Arrays.hashCode(tex)
            result = prime * result + Arrays.hashCode(vertex)
            return result
        }

        /* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
        override fun equals(obj: Any?): Boolean {
            if (this === obj) return true
            if (obj == null) return false
            if (javaClass != obj.javaClass) return false
            val other = obj as FlatVertex
            if (!Arrays.equals(normal, other.normal)) return false
            if (!Arrays.equals(tex, other.tex)) return false
            return if (!Arrays.equals(vertex, other.vertex)) false else true
        }

        private fun isSameValue(a: Float, b: Float): Boolean {
            return Math.abs(a - b) < .00001
        }
    }

    class FlatModel {
        var list = ArrayList<FlatVertex>()
        fun addFaces(vertexIndex: Int, currentVertex: FlatVertex): Boolean {
            var t: FlatVertex
            for (i in list.indices) {
                t = list[i]
                if (t == currentVertex) {
                    t.indexSet!!.add(vertexIndex)
                    return false
                }
            }

            // aggiungiamo al set di index anche questo indice
            currentVertex.indexSet!!.add(vertexIndex)
            list.add(currentVertex)
            return true
        }

        fun findVertex(oldIndex: Int): FlatVertex? {
            var t: FlatVertex
            for (i in list.indices) {
                t = list[i]
                if (t.indexSet!!.contains(oldIndex)) {
                    return t
                }
            }
            return null
        }

        /**
         * dato il vecchio indice del vertice, lo converte nel nuovo indice.
         *
         * @param oldIndex
         * @return
         */
        fun findTranslatedIndex(oldIndex: Int): Int {
            var t: FlatVertex
            for (i in list.indices) {
                t = list[i]
                if (t.indexSet!!.contains(oldIndex)) {
                    return i
                }
            }
            return -1
        }
    }
}