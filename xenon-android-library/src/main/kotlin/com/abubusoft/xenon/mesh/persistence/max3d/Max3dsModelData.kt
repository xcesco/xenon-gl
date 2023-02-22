package com.abubusoft.xenon.mesh.persistence.max3d

/**
 *
 * Sempre e solo triangoli
 * @author Francesco Benincasa
 */
class Max3dsModelData(var name: String) {
    /**
     * startX 3
     */
    var vertices: FloatArray

    /**
     * startX 3
     */
    var normals: FloatArray

    /**
     * startX 2
     */
    var textCoords: FloatArray

    /**
     * startX 3
     */
    var indices: IntArray
    fun setData(aVertices: FloatArray, aNormals: FloatArray, aTexCoords: FloatArray, aIndices: IntArray) {
        vertices = aVertices
        normals = aNormals
        textCoords = aTexCoords
        indices = aIndices
    }
}