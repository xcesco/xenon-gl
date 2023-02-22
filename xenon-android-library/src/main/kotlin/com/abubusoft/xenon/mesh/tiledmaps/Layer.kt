package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.xenon.math.XenonMath.clamp
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getFloat
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getInt
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getString
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties
import com.abubusoft.xenon.texture.AtlasTexture
import org.xml.sax.Attributes

/**
 *
 *
 * Rappresentazione di base dei layer.
 *
 *
 * @author Francesco Benincasa
 */
abstract class Layer(
    /**
     *
     *
     * Tipo di layer.
     *
     */
    val type: LayerType,
    /**
     * riferimento alla tiled map
     */
    val tiledMap: TiledMap, atts: Attributes?
) : PropertiesCollector() {
    /**
     * Configura l'handler del layer
     *
     * @param handler
     */
    protected abstract fun buildHandler(handler: AbstractLayerHandler<*>?)

    /**
     * Restituisce l'oggetto necessario a disegnare il layer stesso. Tipicamente è l'handler stesso.
     *
     * @return drawer
     */
    abstract fun drawer(): LayerDrawer?

    /**
     *
     *
     * Tipo di layer.
     *
     *
     * @author Francesco Benincasa
     */
    enum class LayerType {
        /**
         *
         *
         * Il layer visualizza un'immagine
         *
         */
        IMAGE,

        /**
         *
         *
         * Il layer visualizza un'insieme di tile.
         *
         */
        TILED,

        /**
         *
         *
         * Il lavyer contiene degli oggetti
         *
         */
        OBJECTS
    }

    /**
     *
     *
     * Indica se il layer deve essere rimosso.
     *
     *
     *
     *  * Quelli che contengono nel nome **preview**
     *  * Quelli che hanno una proprietà di nome **preview** = true
     *
     *
     * @return true se il layer deve essere rimosso
     */
    val isPreviewLayer: Boolean
        get() = "true" == properties[TMXPredefinedProperties.PREVIEW] || name.contains(TMXPredefinedProperties.PREVIEW)

    /**
     *
     *
     * Nome del layer. Non può essere cambiato.
     *
     */
    val name: String

    /**
     *
     *
     * Lista ordinata di texture
     *
     */
    var textureList: ArrayList<AtlasTexture>
    /**
     * Indica se è visualizzabile o meno
     *
     * @return true se il layer è visibile
     */
    /**
     * indica se visibile
     */
    open var isDrawable: Boolean

    /**
     *
     *
     * Livello di opacità del layer, da 0 a 1. Agisce solo a livello di channel Alpha.
     *
     */
    var opacity: Float

    /**
     * percentuale di speed startX da 0 a 1. Deve essere float
     */
    var speedPercentageX = 1.0f

    /**
     * percentuale di speed startY da 0 a 1. Deve essere float
     */
    var speedPercentageY = 1.0f

    /**
     * offset del layer rispetto alla posizione iniziale X. In pixel
     */
    var layerOffsetX = 0f

    /**
     * offset del layer rispetto alla posizione iniziale Y. In pixel
     */
    var layerOffsetY = 0f

    /**
     *
     *
     * marca il layer per essere rimosso se impostato a true
     *
     */
    var discard = false

    /**
     *
     *
     * Inizializza tutte le proprietà che i vari tipi di layer hanno in comune, a prescindere dal loro tipo. Il nome viene messo in lowercase
     *
     *
     * @param layerType
     * @param tiledMap
     * @param atts
     */
    init {
        name = getString(atts!!, LayerAttributes.NAME).lowercase()
        isDrawable = getInt(atts, TMXPredefinedProperties.VISIBLE, 1) == 1
        opacity = getFloat(atts, LayerAttributes.OPACITY, 1f)
        textureList = ArrayList()
        when (type) {
            LayerType.TILED -> buildHandler(tiledMap.handler!!.buildTiledLayerHandler(this as TiledLayer))
            LayerType.IMAGE -> buildHandler(tiledMap.handler!!.buildImageLayerHandler(this as ImageLayer))
            LayerType.OBJECTS -> buildHandler(tiledMap.handler!!.buildObjectLayerHandler(this as ObjectLayer))
            else -> throw RuntimeException("Layer type $type is not supported")
        }
    }

    /**
     * Effettua lo scroll del layer
     *
     *
     * Dobbiamo convertire le dimensioni della mappa in coordinate view riferite al layer.
     *
     * @param mapOffsetX
     * @param mapOffsetY
     */
    fun scroll(mapOffsetX: Float, mapOffsetY: Float) {
        layerOffsetX += mapOffsetX * speedPercentageX
        if (tiledMap.scrollHorizontalLocked) {
            //screenOffsetX=XenonMath.clamp(screenOffsetX, 0f, tiledMap.mapWidth - tiledMap.view.windowWidth);
            layerOffsetX = clamp(layerOffsetX, 0f, tiledMap.view.mapMaxPositionValueX)
        }
        // modulo
        layerOffsetX = layerOffsetX % tiledMap.mapWidth
        layerOffsetY += mapOffsetY * speedPercentageY
        if (tiledMap.scrollVerticalLocked) {
            //screenOffsetY=XenonMath.clamp(screenOffsetY, 0f, tiledMap.mapHeight - tiledMap.view.windowHeight);
            layerOffsetY = clamp(layerOffsetY, 0f, tiledMap.view.mapMaxPositionValueY)
        }
        // modulo
        layerOffsetY = layerOffsetY % tiledMap.mapHeight
    }

    /**
     *
     * Effettua il posizionamento del layer. Siccome la posizione è modulo dimension, rimaniamo sempre nei limiti della mappa.
     *
     *
     * Dobbiamo convertire le dimensioni della mappa in coordinate view riferite al layer.
     *
     * @param mapDistanceX
     * distanza nel sistema mappa
     * @param mapDistanceY
     * distanza nel sistema mappa
     */
    fun position(mapDistanceX: Float, mapDistanceY: Float) {
        layerOffsetX = mapDistanceX * speedPercentageX

        // se ci sono i lock, vediamo di rispettarli.
        if (tiledMap.scrollHorizontalLocked) {
            //screenOffsetX=XenonMath.clamp(screenOffsetX, 0f, tiledMap.mapWidth - tiledMap.view.windowWidth);
            layerOffsetX = clamp(layerOffsetX, 0f, tiledMap.view.mapMaxPositionValueX)
        }
        layerOffsetY = mapDistanceY * speedPercentageY
        if (tiledMap.scrollVerticalLocked) {
            //screenOffsetY=XenonMath.clamp(screenOffsetY, 0f, tiledMap.mapHeight - tiledMap.view.windowHeight);
            layerOffsetY = clamp(layerOffsetY, 0f, tiledMap.view.mapMaxPositionValueY)
        }
    }

    /**
     * Da invocare quando creo la finestra
     *
     * @param view
     * view appena costruita
     */
    abstract fun onBuildView(view: TiledMapView)
    abstract fun view(): TiledMapView?
}