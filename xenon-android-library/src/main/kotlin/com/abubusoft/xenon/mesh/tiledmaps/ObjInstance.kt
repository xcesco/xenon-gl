/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps

/**
 *
 * Rappresenta l'istanza di una obj class
 * @author Francesco Benincasa
 */
class ObjInstance internal constructor() : ObjBase() {
    /**
     *
     *
     * classe di riferimento
     *
     */
    var clazz: ObjClass? = null

    init {
        category = CategoryType.INSTANCE
    }

    companion object {
        fun buildInstance(name: String?, clazz: ObjClass): ObjInstance {
            val result = ObjInstance()
            result.name = name
            result.category = CategoryType.INSTANCE
            result.type = clazz.type
            result.x = clazz.x
            result.y = clazz.y
            result.width = clazz.width
            result.height = clazz.height
            result.visible = true
            result.clazz = clazz
            return result
        }
    }
}