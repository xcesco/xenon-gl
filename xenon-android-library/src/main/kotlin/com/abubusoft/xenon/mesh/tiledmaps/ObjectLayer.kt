package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer
import com.abubusoft.xenon.mesh.tiledmaps.internal.ObjectLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getInt
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties
import org.xml.sax.Attributes

class ObjectLayer(tiledMap: TiledMap, atts: Attributes?) : Layer(LayerType.OBJECTS, tiledMap, atts) {
    /**
     *
     *
     * Elenco di oggetti definiti nel layer
     *
     *
     * @return elenco degli oggetti
     */
    /**
     *
     *
     * Elenco di oggetti presenti nel layer.
     *
     */
    var objects: ArrayList<ObjDefinition>

    /**
     *
     *
     * Drawer da utilizzare per il draw del layer.
     *
     */
    var objectDrawer: ObjectLayerDrawer? = null
    private var handler: ObjectLayerHandler? = null

    /**
     *
     *
     * Costruttore
     *
     *
     * @param tiledMap
     * @param atts
     */
    init {
        objects = ArrayList()
        visible = getInt(atts!!, TMXPredefinedProperties.VISIBLE, 1) == 1
    }

    /**
     *
     *
     * Aggiunge un oggetto.
     *
     *
     * @param object
     */
    fun addObject(`object`: ObjDefinition) {
        objects.add(`object`)
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.mesh.tiledmaps.Layer#onBuildView(com.abubusoft.xenon.mesh.tiledmaps.internal.MapView)
	 */
    override fun onBuildView(view: TiledMapView) {
        handler!!.onBuildView(view)
    }

    /**
     *
     *
     * Imposta il drawer da utilizzare per il rendering.
     *
     *
     * @param value
     */
    fun setObjectDrawer(value: ObjectLayerDrawer?) {
        objectDrawer = value
    }

    override fun buildHandler(handler: AbstractLayerHandler<*>?) {
        this.handler = handler as ObjectLayerHandler?
    }

    override fun drawer(): LayerDrawer? {
        return handler
    }

    override fun view(): TiledMapView? {
        return handler!!.view()
    }
}