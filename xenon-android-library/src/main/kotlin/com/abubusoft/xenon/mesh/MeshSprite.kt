package com.abubusoft.xenon.mesh

import com.abubusoft.kripton.annotation.BindType

/**
 *
 * Rappresenta uno sprite. E' una classe di comodità, dato che al momento
 * non contiene alcun metodo particolare.
 *
 * @author Francesco Benincasa
 */
@BindType
open class MeshSprite : QuadMesh() {
    companion object {
        private const val serialVersionUID = 7897155846173355548L
    }
}