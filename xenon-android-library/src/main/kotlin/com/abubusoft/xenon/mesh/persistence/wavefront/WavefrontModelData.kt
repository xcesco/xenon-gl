package com.abubusoft.xenon.mesh.persistence.wavefront

/**
 *
 *
 * Modello intermedio. Serve a passare dal modello del formato grafico di input outpu a quello utilizzato da Xenon.
 *
 *
 * @author Francesco Benincasa
 */
class WavefrontModelData {
    var name: String? = null

    /**
     *
     *
     * Tutte le informazioni possibili su un vertice sono contenute qua.
     *
     *
     * @author Francesco Benincasa
     */
    class FlatVertexF {
        var index = 0
        var vertex: VertexF? = null
        var tex: UVCoord? = null
        var normal: VertexF? = null
    }

    /**
     * coordinate di un vertex
     *
     * @author Francesco Benincasa
     */
    class VertexF {
        var x = 0f
        var y = 0f
        var z = 0f
    }

    /**
     * coordinate 2d di una texture
     *
     * @author Francesco Benincasa
     */
    class UVCoord {
        var u = 0f
        var v = 0f
    }

    /**
     * un triangolo.
     *
     * @author Francesco Benincasa
     */
    class Face {
        var vertexIndex: IntArray
        var textureIndex: IntArray
        var normalIndex: IntArray

        /**
         *
         */
        var indexes: IntArray

        init {
            vertexIndex = IntArray(3)
            textureIndex = IntArray(3)
            normalIndex = IntArray(3)
            indexes = IntArray(3)
        }

        companion object {
            const val INDEX_VERTEX0 = 0
            const val INDEX_VERTEX1 = 1
            const val INDEX_VERTEX2 = 2
        }
    }

    var vertices: ArrayList<VertexF?>? = null
    var tex: ArrayList<UVCoord?>? = null
    var triangles: ArrayList<Face?>? = null
    var normals: ArrayList<VertexF?>? = null
}