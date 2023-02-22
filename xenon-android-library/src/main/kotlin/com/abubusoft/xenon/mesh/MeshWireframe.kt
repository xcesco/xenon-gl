/**
 *
 */
package com.abubusoft.xenon.mesh

import com.abubusoft.kripton.annotation.BindType
import java.io.Serializable

/**
 * Rappresenta un oggetto wireframe
 *
 * @author Francesco Benincasa
 */
@BindType
class MeshWireframe : Mesh, Serializable {
    internal constructor() {}
    internal constructor(input: Mesh) {
        type = input.type

        // impostiamo boundingbox
        boundingBox[input.boundingBox.width, input.boundingBox.height] = input.boundingBox.depth
        boundingSphereRadius = input.boundingSphereRadius

        // impostiamo vertici e texture
        vertexCount = input.vertexCount
        vertices = input.vertices
        texturesCount = input.texturesCount
        textures = input.textures
    }

    companion object {
        private const val serialVersionUID = -8580829785311186419L
    }
}