/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.xenon.entity.SpriteEntity

/**
 * @author Francesco Benincasa
 */
class ObjSprite : ObjBase() {
    /**
     *
     *
     * sprite entity associato.
     *
     */
    var spriteEntity: SpriteEntity? = null

    init {
        category = CategoryType.SPRITE
    }

    companion object {
        /**
         *
         *
         * Crea un object sprite a partire da un object instance.
         *
         *
         * @param objSource
         * @return
         */
        fun build(objSource: ObjDefinition, entity: SpriteEntity?): ObjSprite {
            val ret = ObjSprite()
            ret.name = objSource.name
            ret.visible = objSource.visible
            ret.width = objSource.width
            ret.height = objSource.height
            ret.x = objSource.x
            ret.y = objSource.y
            ret.properties = objSource.properties
            ret.spriteEntity = entity
            return ret
        }
    }
}