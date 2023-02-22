package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.xenon.mesh.*
import com.abubusoft.xenon.vbo.BufferManager
import java.util.*

object AndroidXmlAdapter {
    fun convertModelToIndexedTriangleShape(model: XmlDataModel, options: MeshOptions): Mesh {
        val mesh = MeshHelper.create(options)
        // prima di tutto ottimizziamo, ovvero trovare quei vertici che sono fondamentalmente uguali.
        // in questo formato ho notato che conviene definire un epsilon di confronto.
        val fm = FlatModel()
        var current: FlatVertex
        val list = model.geometry!!.vertexBuffer!!.list
        for (i in list!!.indices) {
            current = FlatVertex()
            current.vertex[0] = list[i]!!.position!!.x
            current.vertex[1] = list[i]!!.position!!.y
            current.vertex[2] = list[i]!!.position!!.z
            if (options.textureEnabled) {
                current.tex[0] = list[i]!!.texcoord!!.u
                current.tex[1] = list[i]!!.texcoord!!.v
            }
            if (options.normalsEnabled) {
                current.normal[0] = list[i]!!.normal!!.x
                current.normal[1] = list[i]!!.normal!!.y
                current.normal[2] = list[i]!!.normal!!.z
            }
            fm.addFaces(i, current)
        }
        options.indicesEnabled(true)

        // vertici min e max, servono per determinare il minimo ed il max
        val min = FloatArray(3)
        val max = FloatArray(3)

        // gli indici ci sono sempre
        run {
            val nfaces = model.submeshes!!.submesh!!.faces!!.list!!.size
            mesh.indexesEnabled = true
            mesh.indexesCount = nfaces * 3
            // stile di disegno
            mesh.drawMode = MeshDrawModeType.INDEXED_TRIANGLES
            mesh.indexes = BufferManager.instance().createIndexBuffer(nfaces * 3, options.bufferOptions.indexAllocation)
            var face: XmlFace
            val listFaces = model.submeshes!!.submesh!!.faces!!.list
            for (i in 0 until nfaces) {
                face = listFaces!![i]
                mesh.indexes!!.values!![i * 3 + 0] = fm.findTranslatedIndex(face.vertexIndex0).toShort()
                mesh.indexes!!.values!![i * 3 + 1] = fm.findTranslatedIndex(face.vertexIndex1).toShort()
                mesh.indexes!!.values!![i * 3 + 2] = fm.findTranslatedIndex(face.vertexIndex2).toShort()
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

        // nome
        mesh.name = model.names!!.names!![0]!!.name
        return mesh
    }

    class FlatVertex {
        var vertex = FloatArray(3)
        var tex = FloatArray(2)
        var normal = FloatArray(3)
        var indexSet = HashSet<Int>()

        /* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + Arrays.hashCode(normal)
            result = prime * result + Arrays.hashCode(tex)
            result = prime * result + Arrays.hashCode(vertex)
            return result
        }

        override fun equals(obj: Any?): Boolean {
            if (this === obj) return true
            if (obj == null) return false
            if (javaClass != obj.javaClass) return false
            val other = obj as FlatVertex
            for (i in vertex.indices) {
                if (!isSameValue(vertex[i], other.vertex[i])) return false
            }
            for (i in tex.indices) {
                if (!isSameValue(tex[i], other.tex[i])) return false
            }
            for (i in normal.indices) {
                if (!isSameValue(normal[i], other.normal[i])) return false
            }
            return true
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
                    t.indexSet.add(vertexIndex)
                    return false
                }
            }

            // aggiungiamo al set di index anche questo indice
            currentVertex.indexSet.add(vertexIndex)
            list.add(currentVertex)
            return true
        }

        fun findVertex(oldIndex: Int): FlatVertex? {
            var t: FlatVertex
            for (i in list.indices) {
                t = list[i]
                if (t.indexSet.contains(oldIndex)) {
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
                if (t.indexSet.contains(oldIndex)) {
                    return i
                }
            }
            return -1
        }
    }
}