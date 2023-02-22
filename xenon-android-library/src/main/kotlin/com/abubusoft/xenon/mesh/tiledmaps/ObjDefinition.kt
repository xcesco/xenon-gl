package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getInt
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getString
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties
import org.xml.sax.Attributes

/**
 *
 *
 * Il sistema di coordinate parte da in alto a sx della mappa.
 *
 *
 * <pre>
 * ( x, y ) -----------------------	( x+width, y )
 * |                                |
 * |                                |
 * |                                |
 * ( x, y+height ) ---------------- ( x+width, y+height )
</pre> *
 *
 *
 * @author Francesco Benincasa
 */
class ObjDefinition : ObjBase() {
    init {
        category = CategoryType.DEFINITION
    }

    companion object {
        /**
         *
         *
         * Crea un oggetto partendo dagli attributi definiti nell'xml.
         *
         *
         * @param attrs
         * @return definizione dell'oggetto
         */
        fun build(attrs: Attributes?): ObjDefinition {
            val result = ObjDefinition()
            result.name = getString(attrs!!, "name")
            result.type = getString(attrs, "allocation")

            // pur essendo float, li recuperiamo come int, dato che sono memorizzati come interi (coordiante rispetto alla mappa).
            result.x = getInt(attrs, "x", 0).toFloat()
            result.y = getInt(attrs, "y", 0).toFloat()
            result.width = getInt(attrs, "width", 0).toFloat()
            result.height = getInt(attrs, "height", 0).toFloat()
            result.visible = getInt(attrs, TMXPredefinedProperties.VISIBLE, 1) == 1
            return result
        }
    }
}