package com.abubusoft.xenon.mesh.tiledmaps

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.SparseIntArray
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.MeshFactory.createTiledGrid
import com.abubusoft.xenon.mesh.MeshGrid
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.MeshTile
import com.abubusoft.xenon.mesh.tiledmaps.Layer.LayerType
import com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController
import com.abubusoft.xenon.mesh.tiledmaps.tmx.MapAttributes
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getInt
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil.getString
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties
import com.abubusoft.xenon.shader.ArgonShaderOptions
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.texture.AtlasTexture
import com.abubusoft.xenon.vbo.BufferAllocationOptions
import com.abubusoft.xenon.vbo.BufferAllocationType
import org.xml.sax.Attributes
import java.util.*

/**
 *
 *
 * Definisce una mappa mediante il formato [tmx](http://doc.mapeditor.org/reference/tmx-map-format/)
 *
 *
 *
 *
 *
 * Una tiledmap consiste in una griglia multilayer di tile che vengono visualizzati sullo schermo. L'origine del sistema di riferimento interno alla mappa è posto in alto a sinistra della mappa. La window che viene utilizzata per
 * visualizzare solo parte visibile dello schermo si basa sullo stesso concetto, quindi la sua origine è in alto a sx. Se la window viene posizionata top|left, allora le due origini coincideranno.
 *
 *
 *
 * <img src="doc-files/sistemaRiferimento.jpg"></img>
 *
 *
 *
 *
 * Nel momento in cui la windo viene spostata, ad esempio a destra, il sistema di riferimento cambierà.
 *
 *
 *
 *
 *
 * La window rappresenta la finestra sulla mappa. Essa non cambia mai di posizione, viene spostata la mappa.
 *
 *
 *
 *
 *
 * windowCenter e mapCenter non cambiano mai, rimangono come punti di riferimento assoluti. Quando la mappa viene spostata, vengono semplicemente utilizzate le mattonelle apposite.
 *
 *
 *
 *
 *
 * Quando viene effettuato uno scroll della finestra, viene spostato positionInMap che rappresenta il punto in alto a sx della window. Il centro della finestra essendo fisso sarà sempre determinato in base a quello.
 *
 *
 * @author Francesco Benincasa
 * @see .mapCenter
 */
class TiledMap @SuppressLint("DefaultLocale") constructor(atts: Attributes?) : PropertiesCollector() {
    val backgroundColorR: Int
    val backgroundColorG: Int
    val backgroundColorB: Int
    val backgroundColorA: Int

    /**
     *
     *
     * Shader utilizzato per il rendering della tiledmap. Viene creato quando si crea la mappa mediante lettura da file.
     *
     */
    var shader: ShaderTiledMap? = null

    /**
     *
     */
    val orientation: MapOrientationType

    /**
     *
     *
     * Indica la posizione nella mappa del punto in alto a sinistra della window.
     *
     *
     *
     * <img src="doc-files/position.png"></img>
     *
     *
     *
     *
     * Il sistema di riferimento utilizzato è quello che ha come origine il punto in alto a sinistra, rivolto verso il basso.
     *
     *
     *
     * <img src="doc-files/sistemaRiferimento.jpg"></img>
     */
    val positionInMap: Point2

    /**
     * numero di colonne di tiles
     */
    val tileColumns: Int

    /**
     * numero di righe di tiles
     */
    val tileRows: Int

    /**
     *
     *
     * Width di default di una tile.
     *
     *
     *
     * Rappresenta la larghezza della tile nella rappresentazione a window.
     *
     */
    val tileWidth: Int

    /**
     *
     *
     * Height di default di una tile.
     *
     *
     *
     * Rappresenta l'altezza della tile nella rappresentazione a window.
     *
     */
    val tileHeight: Int

    /**
     *
     *
     * lista delle animazioni.
     *
     */
    val animations = ArrayList<TileAnimation?>()

    /**
     *
     *
     * durata di default dei frame delle animazioni.
     *
     */
    var animationFrameDefaultDuration: Long = 100

    /**
     *
     *
     * layer definiti.
     *
     */
    val layers = ArrayList<Layer>()

    /**
     *
     *
     * insieme di tile Set definiti. Ogni tileset è associato ad una o più texture.
     *
     */
    var tileSets = ArrayList<TileSet>()

    /**
     *
     *
     * Centro della mappa. Può essere usato per inserire degli oggetti in modo assoluto rispetto alla mappa. Esso viene calcolato in base all'origine della tiledMap (top left).
     *
     *
     *
     * <img src="doc-files/calcoloMapCenter1.jpg"></img>
     *
     *
     *
     *
     * Ovvero
     *
     *
     *
     * <img src="doc-files/calcoloMapCenter2.jpg"></img>
     *
     *
     *
     *
     * Il calcolo algebrico delle coordinate di mapCenter rispetto a windowCenter:
     *
     *
     *
     * <pre>
     * mapCenter.x = tiledMap.mapWidth / 2f;
     * mapCenter.y = tiledMap.mapHeight / 2f;
    </pre> *
     *
     *
     *
     *
     * Non cambia mai, viene definito assieme alla posizione della window
     *
     */
    val mapCenter: Point2

    /**
     *
     *
     * Definizione degli oggetti.
     *
     */
    val objectClasses = HashMap<String, ObjClass>()

    /**
     *
     *
     * Layer
     */
    var objectGroups = ArrayList<ObjectLayer>()
    private val tileSetPropertyCache = SparseIntArray()
    private val spriteCache = SparseIntArray()

    /**
     *
     *
     * Mesh per disegnare parte di layer. Non serve per disegnare i layer, ma quanto per disegnare i vari pezzi, tipicamente per gli oggetti.
     *
     */
    var spriteMesh: MeshGrid? = null

    /**
     *
     *
     * Larghezza della mappa nel sistema di riferimento della mappa. E' data dalla larghezza delle tile per le colonne presenti nella mappa. **Attenzione che tra orthogonal project e isometric cambia il suo calcolo**.
     *
     * <h3>Calcolo su mappa orthogonal</h3> `mapWidth = tileColumns * tileWidth;` <h3>Calcolo su mappa isometric</h3> `mapWidth = tileColumns * tileHeight;`
     *
     *
     * Non tiene conto del bordo del diamante generato per lo scroll.
     *
     */
    var mapWidth = 0

    /**
     *
     *
     * Altezza della mappa nel sistema di riferimento della mappa. **Attenzione che tra orthogonal project e isometric cambia il suo calcolo**.
     *
     * <h3>Calcolo su mappa orthogonal</h3> `mapHeight = tileRows * tileHeight;` <h3>Calcolo su mappa isometric</h3> `mapHeight = tileColumns * tileHeight;`
     *
     *
     * Non tiene conto del bordo del diamante generato per lo scroll.
     *
     */
    var mapHeight = 0

    /**
     * indica se lo scroll orizzontale è consentito o meno.
     */
    var scrollHorizontalLocked = false

    /**
     * indica se lo scroll verticale è consentito o meno.
     */
    var scrollVerticalLocked = false

    /**
     *
     *
     * Listener relativo allo scroll.
     *
     */
    var movementEventListener: MovementListener? = null

    /**
     *
     *
     * Numero di area in cui la tiledmap è suddiviso in orizzontale
     *
     */
    var scrollHorizontalAreaCount = 0

    /**
     *
     *
     * Numero di area in cui la tiledmap è suddiviso in verticale
     *
     */
    var scrollVerticalAreaCount = 0
    var listenerOptions: MovementListenerOptions? = null
    var scrollHorizontalPreviousArea = 0
    var scrollVerticalPreviousArea = 0
    var scrollHorizontalCurrentArea = 0
    var scrollVerticalCurrentArea = 0

    /**
     * rappresenta il controller con il quale pilotare la visualizzazione sulla tiled map.
     */
    var controller: MapController? = null
    private val renderOrder: String

    /**
     * view sulla mappa
     */
    val view: TiledMapView
    fun view(): TiledMapView {
        return view
    }

    /**
     *
     */
    fun setMovementListener(scrollEventListenerValue: MovementListener?, optionsValue: MovementListenerOptions?) {
        listenerOptions = optionsValue
        listenerOptions!!.horizontalAreaInvSize = 1f * listenerOptions!!.horizontalAreaCount / mapWidth
        listenerOptions!!.verticalAreaInvSize = 1f * listenerOptions!!.verticalAreaCount / mapHeight
        movementEventListener = scrollEventListenerValue
        resetScrollAreas()
    }

    /**
     * effettua il reset delle aree in cui è suddivisa la mappa
     */
    fun resetScrollAreas() {
        scrollHorizontalCurrentArea = -1
        scrollVerticalCurrentArea = -1
        scrollHorizontalPreviousArea = -1
        scrollVerticalPreviousArea = -1
    }

    /**
     *
     *
     * Aggiunge il layer group al set di group object e alla lista di layers.
     *
     * @param group
     */
    fun addObjectGroup(group: ObjectLayer) {
        objectGroups.add(group)
        addLayer(group)
    }

    /**
     *
     *
     * Normalmente i layer object non vengono disegnati. Per farlo, bisogna definire loro un drawer.
     *
     *
     * @param objectLayerName
     * @param drawer
     * @return true se è stato trovato l'object layer con il nome specificato
     */
    fun attachObjectLayerDrawer(objectLayerName: String, drawer: ObjectLayerDrawer?): Boolean {
        for (i in objectGroups.indices) {
            if (objectLayerName.equals(objectGroups[i].name, ignoreCase = true)) {
                objectGroups[i].setObjectDrawer(drawer)
                return true
            }
        }
        return false
    }

    fun addTileSet(tileSet: TileSet) {
        tileSets.add(tileSet)
    }

    fun addLayer(layer: Layer) {
        layers.add(layer)
    }

    /**
     *
     *
     * Per ogni layer, in base alle tile o alla texture associata (per le imageLayer) assegna le varie texture.
     *
     */
    fun assignTextureToLayers(context: Context?) {
        var currentLayer: Layer
        var currentTile: Tile
        var currentGid: Int
        var currentTexture: AtlasTexture?
        var textureIndex: Byte
        var atlasRow: Int
        var atlasCol: Int
        var temp: Float

        // impostiamo texture
        texturesCount = tileSets.size
        textureCounterMaxPerLayer = 0

        // per ogni layer
        for (l in layers.indices) {
            currentLayer = layers[l]

            // imposta la velocità
            currentLayer.speedPercentageX = currentLayer.getPropertyAsFloat(TMXPredefinedProperties.SPEED_PERCENTAGE_X, 1.0f)
            currentLayer.speedPercentageY = currentLayer.getPropertyAsFloat(TMXPredefinedProperties.SPEED_PERCENTAGE_Y, 1.0f)
            when (currentLayer.type) {
                LayerType.TILED -> {
                    val currentTiledLayer = currentLayer as TiledLayer

                    // per ogni tile presente nel layer
                    val numTiles = currentTiledLayer.tileRows * currentTiledLayer.tileColumns
                    var i = 0
                    while (i < numTiles) {
                        currentTile = currentTiledLayer.tiles[i]
                        currentGid = currentTile.gid
                        currentTexture = getTextureByGID(currentGid)
                        if (currentTexture != null) {
                            textureIndex = currentTiledLayer.textureList.indexOf(currentTexture).toByte()

                            // aggiungiamo la texture se finora non era stata usata
                            // in nessuna tile
                            if (textureIndex.toInt() == -1) {
                                currentTiledLayer.textureList.add(currentTexture)
                                textureIndex = (currentTiledLayer.textureList.size - 1).toByte()
                            }
                            currentTile.textureSelector = textureIndex

                            // calcoliamo dimensioni del tile nella texture
                            atlasRow = currentTile.atlasRow
                            atlasCol = currentTile.atlasColumn
                            /*
                         * // come da http://stackoverflow.com/questions/3962385/ android-opengl-es-texture-bleeding
						 */

                            // fix su texel adjustment
                            val halfPixelAdjust = (0.5 * (1.0f / currentTexture.columnsCount / currentTile.width)).toFloat()
                            // float halfPixelAdjust = 0f;
                            currentTexture.selectedCurrentFrame(atlasRow, atlasCol)
                            currentTile.lowY = currentTexture.coordStartY + halfPixelAdjust
                            currentTile.highY = currentTexture.coordEndY - halfPixelAdjust
                            currentTile.lowX = currentTexture.coordStartX + halfPixelAdjust
                            currentTile.highX = currentTexture.coordEndX - halfPixelAdjust
                            if (!currentTile.diagonalFlip) {
                                if (currentTile.horizontalFlip) {
                                    temp = currentTile.lowX
                                    currentTile.lowX = currentTile.highX
                                    currentTile.highX = temp
                                }
                                if (currentTile.verticalFlip) {
                                    temp = currentTile.lowY
                                    currentTile.lowY = currentTile.highY
                                    currentTile.highY = temp
                                }
                            } else {
                                if (!currentTile.verticalFlip) {
                                    temp = currentTile.lowX
                                    currentTile.lowX = currentTile.highX
                                    currentTile.highX = temp
                                }
                                if (!currentTile.horizontalFlip) {
                                    temp = currentTile.lowY
                                    currentTile.lowY = currentTile.highY
                                    currentTile.highY = temp
                                }
                            }
                        } else {
                            // impostiamola come empty tile
                            currentTile.gid = 0
                        }
                        i++
                    }

                    // usiamo il counter delle texture
                    textureCounterMaxPerLayer = Math.max(textureCounterMaxPerLayer, currentTiledLayer.textureList.size)
                }
                LayerType.IMAGE -> {

                    // carica la texture per l'imageLayer
                    val currentImageLayer = currentLayer as ImageLayer
                    var currentTileSet: TileSet
                    var trovato = false
                    val textureSource = currentImageLayer.imageSource
                    // se troviamo un tileSet con questo elemento, lo riusiamo, dato
                    // che la texture è stata
                    // già caricata.
                    val numTileSet = tileSets.size
                    var i = 0
                    while (i < numTileSet) {
                        currentTileSet = tileSets[i]
                        if (currentTileSet.imageSource == textureSource) {
                            // abbiamo trovato un tileset con la stessa texture,
                            // quindi la riusiamo
                            currentImageLayer.textureList.add(currentTileSet.texture!!)
                            trovato = true
                            break
                        }
                        i++
                    }
                    if (!trovato) {
                        currentImageLayer.loadTexture(context)
                        // il layer usa una texture che non è contemplata nei
                        // tileSet, quindi dobbiamo incrementare il
                        // numero di texture usate
                        texturesCount++
                    }
                }
                LayerType.OBJECTS -> {}
            }
        }
    }

    /**
     *
     *
     * Data una gid, restituisce una texture. Se la gid è 0 allora restituisce una texture null, ad indicare che la tile è vuota.
     *
     *
     * @param gid
     * @return
     */
    private fun getTextureByGID(gid: Int): AtlasTexture? {
        try {
            // analizza i tileset dal più alto al più basso
            for (i in tileSets.indices.reversed()) {
                val tileSet = tileSets[i]
                if (gid >= tileSet.firstGID) {
                    return tileSet.texture
                }
            }
        } catch (e: Exception) {
            Logger.error(e.toString())
        }
        return null
    }

    /**
     *
     *
     *
     *
     * @param gid
     * @return sprite associato al gid
     */
    fun getSpriteByGID(gid: Int): MeshTile? {
        try {
            if (spriteCache.indexOfKey(gid) >= 0) {
                val index = spriteCache[gid]
                return tileSets[index].getSprite(gid)
            }
            for (i in tileSets.indices.reversed()) {
                val tileSet = tileSets[i]
                if (gid >= tileSet.firstGID) {
                    val sprite = tileSet.getSprite(gid)
                    spriteCache.put(gid, i)
                    return sprite
                }
            }
        } catch (e: Exception) {
            Logger.error(e.toString())
        }
        return null
    }

    /**
     * Restuisce il valore di una proprietà di una tile, mediante un sistema di cache delle tile
     *
     * @param gid
     * @return
     */
    fun getTileProperties(gid: Int): ArrayList<TileProperty?>? {
        if (tileSetPropertyCache.indexOfKey(gid) >= 0) {
            val index = tileSetPropertyCache[gid]
            return tileSets[index].getTileProperty(gid)
        }
        for (i in tileSets.indices) {
            val tileSet = tileSets[i]
            val props = tileSet.getTileProperty(gid)
            if (props != null) {
                tileSetPropertyCache.put(gid, i)
                return props
            }
        }
        return null
    }

    /**
     * Restuisce il valore di una proprietà di una tile, mediante un sistema di cache delle tile
     *
     * @param gid
     * @param key
     * @param defaultValue
     * @return valore della chiave sottforma di stringa
     */
    fun getTileProperty(gid: Int, key: String, defaultValue: String?): String? {
        val props = getTileProperties(gid) ?: return defaultValue

        // se non abbiamo proprietà restituiamo comunque il defaultValue
        val n = props.size
        for (i in 0 until n) {
            if (props[i].getName() == key) {
                return props[i].getValue()
            }
        }
        return defaultValue
    }

    /**
     *
     *
     * Data una chiave, prova a rimuovere un object layer.
     *
     *
     * @param key
     */
    fun removeObjectgroup(key: String): Boolean {
        val og = findtObjectGroup(key)
        if (og != null) {
            // rimuoviamo sia dai layer che dagli objectgroup
            layers.remove(og)
            return objectGroups.remove(og)
        }
        return false
    }

    /**
     *
     *
     * Rimuove un objectlayer, partendo dalla sua chiave.
     *
     *
     * @param key
     * @return
     */
    fun removeLayer(key: String): Boolean {
        val layer: Layer? = findLayer(key)
        return if (layer != null) {
            layers.remove(layer)
        } else false
    }

    /**
     *
     *
     * Imposta la visibilità di un layer o di un object layer.
     *
     *
     * @param name
     * @param visibleValue
     */
    fun setVisible(name: String, visibleValue: Boolean) {
        for (i in objectGroups.indices) {
            if (name.equals(objectGroups[i].name, ignoreCase = true)) {
                objectGroups[i].visible = visibleValue
                return
            }
        }
        for (i in layers.indices) {
            if (name.equals(layers[i].name, ignoreCase = true)) {
                layers[i].visible = visibleValue
                return
            }
        }
    }

    /**
     *
     *
     * Indica se un layer o un object layer è visibile.
     *
     *
     * @param name
     * @param visibleValue
     */
    fun isVisible(name: String): Boolean {
        for (i in objectGroups.indices) {
            if (name.equals(objectGroups[i].name, ignoreCase = true)) {
                return objectGroups[i].visible
            }
        }
        for (i in layers.indices) {
            if (name.equals(layers[i].name, ignoreCase = true)) {
                return layers[i].visible
            }
        }
        return false
    }

    /**
     * Attacca la tiledmap alla camera passata come argomento. Questo consente di creare una window sulla tiled map agganciata alla camera. Il numero di tiles per riga e colonna è data dalla divisione dello schermo in base alle dimensioni
     * dei tile, più due tile per dimensione, per gestire eventuali spostamenti
     *
     * @param cameraValue
     * @param options
     * @return controller
     */
    fun buildView(cameraValue: Camera?, options: TiledMapOptions): MapController? {
        // mapCenter viene calcolata relativa alla tileMap Origin
        mapCenter.x = mapWidth / 2f
        mapCenter.y = mapHeight / 2f

        // TODO quello che segue relativo alla view è da mettere nel map handler (cambia in base al tipo di mappa)
        handler!!.onBuildView(view, cameraValue, options)
        Logger.info("XENON - -----------------------")
        Logger.info("XENON - distanceFromViewer %s ", view.distanceFromViewer)
        Logger.info("XENON - windowWidth %s windowHeight %s", view.windowWidth, view.windowHeight)
        Logger.info("XENON - mapWidth %s mapHeight %s", mapWidth, mapHeight)
        Logger.info("XENON - windowCenterX %s windowCenterY %s", view.windowCenter.x, view.windowCenter.y)
        Logger.info("XENON - windowTileRows %s windowTileColumns %s", view.windowTileRows, view.windowTileColumns)
        Logger.info("XENON - tileWidth %s tileHeight %s", tileWidth, tileHeight)
        Logger.info("XENON - -----------------------")


        // impostazioni di scroll
        scrollVerticalLocked = options.scrollVerticalLocked
        scrollHorizontalLocked = options.scrollHorizontalLocked

        // invochiamo l'evento onWindowCreate per tutti i layer
        val n = layers.size
        for (i in 0 until n) {
            layers[i].onBuildView(view)
        }

        // impostiamo il controller. Lo impostiamo qua perchè deve avere una tiledMap già pronta prima di poter
        // funzionare
        if (options.createController) {
            controller = handler.buildMapController(this, cameraValue)
        }
        position(options.startPosition)

        // creiamo lo shape per disegnare parti di layer.
        val bufferOptions = BufferAllocationOptions.build().indexAllocation(BufferAllocationType.DYNAMIC).textureAllocation(BufferAllocationType.CLIENT)
            .vertexAllocation(BufferAllocationType.CLIENT)
        spriteMesh = createTiledGrid(
            (tileWidth * view.windowTileColumns).toFloat(),
            (tileHeight * view.windowTileRows).toFloat(),
            view.windowTileRows,
            view.windowTileColumns,
            MeshOptions.build().bufferAllocationOptions(bufferOptions)
        )
        return controller
    }

    /**
     * numero di texture usate. Il numero è ricavato dai vari tileSet e dagli imageLayer, i quali hanno un'immagine ad uso proprio.
     */
    var texturesCount = 0

    /**
     * numero di texture massime usate in contemporanea in un layer
     */
    var textureCounterMaxPerLayer = 0

    /**
     * Se true indica che c'è una texture per layer e non più di una.
     */
    var onlyOneTexture4Layer = false

    /**
     * handler necessario della mappa. La sua implementazione cambia in base al tipo di mappa
     */
    val handler: MapHandler?

    /**
     * Costruttore, definiamo orientation, columns, rows, tileWidth, tileHeight.
     *
     * @param atts attributi
     */
    init {
        // orientation
        var orientation = getString(atts!!, MapAttributes.ORIENTATION)
        if (orientation == null) orientation = MapOrientationType.ORTHOGONAL.toString()
        this.orientation = MapOrientationType.valueOf(orientation.uppercase(Locale.getDefault()))
        renderOrder = getString(atts, MapAttributes.RENDER_ORDER)
        tileColumns = getInt(atts, MapAttributes.WIDTH)
        tileRows = getInt(atts, MapAttributes.HEIGHT)
        tileWidth = getInt(atts, MapAttributes.TILE_WIDTH)
        tileHeight = getInt(atts, MapAttributes.TILE_HEIGHT)
        val bgColor = Color.parseColor(getString(atts, MapAttributes.BACKGROUND_COLOR, "#000000FF"))
        backgroundColorR = Color.red(bgColor)
        backgroundColorG = Color.green(bgColor)
        backgroundColorB = Color.blue(bgColor)
        backgroundColorA = Color.alpha(bgColor)

        // creiamo l'handler
        handler = this.orientation.createMapHandler(this)
        view = TiledMapView()
        positionInMap = Point2()
        mapCenter = Point2()
        resetScrollAreas()
    }

    /**
     *
     *
     * Muove la posizione della mappa, in termini di posizione relativa. Lo spostamento viene
     * effettuato sul sistema della mappa.
     *
     *
     * @param position
     */
    fun position(position: TiledMapPositionType?) {
        var x = 0f
        var y = 0f
        val msX = view.mapMaxPositionValueX // (mapWidth - view.windowWidth);
        val msY = view.mapMaxPositionValueY // (mapHeight - view.windowHeight);
        when (position) {
            TiledMapPositionType.LEFT_TOP -> {
                x = 0f
                y = 0f
            }
            TiledMapPositionType.LEFT_CENTER -> {
                x = 0f
                y = 0f
                if (msY > 0f) y = msY / 2f
            }
            TiledMapPositionType.LEFT_BOTTOM -> {
                x = 0f
                y = 0f
                if (msY > 0f) y = msY
            }
            TiledMapPositionType.MIDDLE_TOP -> {
                x = 0f
                if (msX > 0f) x = msX / 2f
                y = 0f
            }
            TiledMapPositionType.MIDDLE_CENTER -> {
                x = 0f
                if (msX > 0f) x = msX / 2f
                y = 0f
                if (msY > 0) y = msY / 2f
            }
            TiledMapPositionType.MIDDLE_BOTTOM -> {
                x = 0f
                if (msX > 0f) x = msX / 2f
                y = 0f
                if (msY > 0f) y = msY
            }
            TiledMapPositionType.RIGHT_TOP -> {
                x = 0f
                if (msX > 0f) x = msX
                y = 0f
            }
            TiledMapPositionType.RIGHT_CENTER -> {
                x = 0f
                if (msX > 0f) x = msX
                y = 0f
                if (msY > 0f) y = msY / 2f
            }
            TiledMapPositionType.RIGHT_BOTTOM -> {
                x = 0f
                if (msX > 0f) x = msX
                y = 0f
                if (msY > 0f) y = msY
            }
        }
        positionInMap.setCoords(x, y)
        for (i in layers.indices) {
            layers[i].position(x, y)
        }

        // in caso di spostamento e di listener!=null mandiamo evento
        if (movementEventListener != null) {
            movementEventListener!!.onPosition(x, y)
        }
        resetScrollAreas()
    }

    /**
     * Inizializza la tile, caricando lo shader.
     *
     * @param context
     */
    fun init(context: Context?) {
        onlyOneTexture4Layer = textureCounterMaxPerLayer == 1
        shader = ShaderManager.instance().createShaderTiledMap(textureCounterMaxPerLayer == 1, ArgonShaderOptions.build().numberOfTextures(texturesCount))
        ObjBase.Companion.resetFrameMarker()
    }

    fun updateTime(deltaTime: Long) {
        var animation: TileAnimation?
        for (i in animations.indices) {
            animation = animations[i]
            animation!!.update(deltaTime)
        }
    }

    /**
     *
     *
     * Cerca negli object layer un layer con il nome specificato.
     *
     *
     * @param key
     * @return layer o null
     */
    fun findLayer(key: String): TiledLayer? {
        val n = layers.size
        for (i in 0 until n) {
            val current = layers[i]
            if (key == current.name && current.type == LayerType.TILED) {
                return current as TiledLayer
            }
        }
        return null
    }

    fun findtObjectGroup(key: String): ObjectLayer? {
        val n = objectGroups.size
        var current: ObjectLayer
        for (i in 0 until n) {
            current = objectGroups[i]
            if (key == current.name) {
                return current
            }
        }
        return null
    }
}