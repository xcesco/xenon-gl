package com.abubusoft.xenon.mesh.tiledmaps.isostaggered

import android.graphics.Color
import com.abubusoft.xenon.XenonApplication4OpenGL
import com.abubusoft.xenon.camera.Camera

/**
 *
 *
 * Gestore della mappa.
 *
 *
 *
 * <h1>Costruzione della view</h1>
 *
 *
 *
 *
 * La view sulla mappa viene sempre costruita della forma del rombo. Se la mappa è più corta da una delle due dimensioni, i conti verranno fatti come se fosse comunque un rombo completo. La view sulla mappa viene costruita mediante il
 * metodo [.onBuildView]. Lo schermo può essere sia landscape che portrait, quindi i risultati possono essere:
 *
 *
 *
 * <h2>Passaggio da mappa a window</h2>
 *
 *
 * Il passaggio tra mappa e window è diverso rispetto alla situazione orthogonal perchè in memoria, la mappa ha le tile di una certa dimensione, mentre nella window le dimensioni sono diverse.
 *
 * <img src="./doc-files/Map2Window.png"></img>/ >
 *
 *
 * <h2>Schermo in portrait mode</h2>
 *
 *
 * Quando impostiamo il [com.abubusoft.xenon.mesh.tiledmaps.TiledMapFillScreenType.FILL_HEIGHT]:
 *
 * <img src="./doc-files/view1.png"></img>/ >
 *
 *
 * Quando impostiamo il [com.abubusoft.xenon.mesh.tiledmaps.TiledMapFillScreenType.FILL_WIDTH]:
 *
 * <img src="./doc-files/view2.png"></img>
 *
 *
 * <h2>Schermo in landscape mode</h2>
 *
 *
 * Quando impostiamo il [com.abubusoft.xenon.mesh.tiledmaps.TiledMapFillScreenType.FILL_HEIGHT]:
 *
 * <img src="./doc-files/view3.png"></img>
 *
 *
 * Quando impostiamo il [com.abubusoft.xenon.mesh.tiledmaps.TiledMapFillScreenType.FILL_WIDTH]:
 *
 * <img src="./doc-files/view4.png"></img>
 *
 * @author xcesco
 */
@Uncryptable
class ISSMapHandler(map: TiledMap) : AbstractMapHandler<ISSMapController?>(map) {
    enum class Status {
        STANDARD, ODD, AREA_A, AREA_B, AREA_C, AREA_D, UNSPOSTR
    }

    /**
     *
     *
     * Dimensione della tile nel sistema di riferimento isometrico della mappa.
     *
     */
    var isoTileSize: Float

    /**
     *
     *
     * Dimensione width della window su base isometrica. In altre parole, le dimensioni del diamante di visualizzazione nel sistema della mappa.
     *
     */
    var isoWindowWidth = 0f

    /**
     *
     *
     * Dimensione height della window su base isometrica. In altre parole, le dimensioni del diamante di visualizzazione nel sistema della mappa.
     *
     */
    var isoWindowHeight = 0f
    private var wireWindow: Mesh? = null
    private var lineDrawer: LineDrawer? = null
    private var matrixWire: Matrix4x4? = null
    private var tiledWindowHeight = 0f
    private var tiledWindowWidth = 0f

    /**
     *
     *
     * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
     *
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel [XenonApplication4OpenGL.onSceneReady])
     *
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     * @param deltaTime           tempo trascorso dall'ultimo draw
     * @param modelViewProjection matrice MVP
     */
    fun draw(deltaTime: Long, modelViewProjection: Matrix4x4?) {
        super.draw(deltaTime, modelViewProjection)
        lineDrawer.begin()

        // window quadrata, deve contenere la window reaale
        lineDrawer.setColor(Color.GREEN)
        matrixWire.buildScaleMatrix(map.view().windowDimension, map.view().windowDimension, 1f)
        matrixWire.multiply(modelViewProjection, matrixWire)
        lineDrawer.draw(wireWindow, matrixWire)
        lineDrawer.setColor(Color.RED)
        matrixWire.buildScaleMatrix(map.view().windowWidth, map.view().windowHeight, 1f)
        matrixWire.multiply(modelViewProjection, matrixWire)
        lineDrawer.draw(wireWindow, matrixWire)
        lineDrawer.setColor(Color.BLUE)
        matrixWire.buildScaleMatrix(tiledWindowWidth, tiledWindowHeight, 1f)
        matrixWire.multiply(modelViewProjection, matrixWire)
        lineDrawer.draw(wireWindow, matrixWire)
        lineDrawer.end()
    }

    /**
     *
     *
     * Nella costruzione della window, per le mappe isometriche, bisogna tenere in considerazione che quello che conta per il riempimento dello schermo contano solo le dimensioni del diamante. L'opzione [TiledMapFillScreenType]
     *
     *
     *
     * <img src="./doc-files/windows.png" style="width: 60%"></img>
     *
     *
     *
     *
     */
    fun onBuildView(view: TiledMapView?, camera: Camera?, options: TiledMapOptions?) {
        val screenInfo: ScreenInfo = XenonGL.screenInfo
        // impostiamo metodo di riempimento dello schermo
        view.windowDimension = 0f
        when (options.fillScreenType) {
            TiledMapFillScreenType.FILL_HEIGHT -> {
                run {
                    view.windowHeight = Math.round(map.tileRows * map.tileHeight * 0.5f)
                    view.windowDimension = view.windowHeight.toFloat()
                }
                view.windowWidth = Math.round(view.windowHeight * screenInfo.aspectRatio)

                // le righe sono definite, le colonne vengono ricavate in funzione
                view.windowTileRows = map.tileRows
                view.windowTileColumns = view.windowWidth / map.tileHeight
            }
            TiledMapFillScreenType.FILL_CUSTOM_HEIGHT -> {
                run {
                    view.windowHeight = Math.round(options.visibleTiles * map.tileHeight * 0.5f)
                    view.windowDimension = view.windowHeight.toFloat()
                }
                view.windowWidth = Math.round(view.windowHeight * screenInfo.aspectRatio)

                // le righe sono definite, le colonne vengono ricavate in funzione
                view.windowTileRows = options.visibleTiles
                view.windowTileColumns = view.windowWidth / map.tileWidth
            }
            TiledMapFillScreenType.FILL_WIDTH -> {
                run {
                    view.windowWidth = Math.round(map.tileColumns * map.tileWidth)
                    view.windowDimension = view.windowWidth.toFloat()
                }
                view.windowHeight = Math.round(view.windowDimension / screenInfo.aspectRatio)

                // le righe sono definite, le colonne vengono ricavate in funzione
                view.windowTileColumns = map.tileColumns
                view.windowTileRows = view.windowHeight / map.tileHeight
            }
            TiledMapFillScreenType.FILL_CUSTOM_WIDTH -> {
                run {
                    view.windowWidth = Math.round(options.visibleTiles * map.tileWidth)
                    view.windowDimension = view.windowWidth.toFloat()
                }
                view.windowHeight = Math.round(view.windowDimension / screenInfo.aspectRatio)

                // le righe sono definite, le colonne vengono ricavate in funzione
                view.windowTileColumns = options.visibleTiles
                view.windowTileRows = view.windowHeight / map.tileHeight
            }
        }

        // il numero di righe e colonne non possono andare oltre il numero di definizione
        view.windowTileColumns = XenonMath.clampI(view.windowTileColumns, 0, map.tileColumns)
        view.windowTileRows = XenonMath.clampI(view.windowTileRows, 0, map.tileRows)

        // calcoliamo le dimensioni della window su base isometrica e definiamo i limiti di spostamento
        isoWindowWidth = view.windowTileColumns * map.tileWidth
        isoWindowHeight = view.windowTileRows * isoTileSize

        //TODO da sistemare il limite
        view.mapMaxPositionValueX = map.mapWidth - view.windowWidth - map.tileWidth * 2

        // limiti y ok
        view.mapMaxPositionValueY = map.mapHeight - isoWindowHeight
        view.mapMaxPositionValueY = view.mapMaxPositionValueY / 2 + map.tileHeight / 2

        //view.windowDimension *= options.visiblePercentage;

        // il quadrato della dimensione deve essere costruito sempre sulla dimensione massima
        view.windowDimension = XenonMath.max(view.windowWidth, view.windowHeight) * options.visiblePercentage


        //TODO per vedere da più lontano
        view.distanceFromViewer = XenonMath.zDistanceForSquare(camera, view.windowDimension)
        //view.distanceFromViewer = XenonMath.zDistanceForSquare(camera, view.windowDimension * 4);

        // calcoliamo il centro dello schermo, senza considerare i bordi aggiuntivi
        view.windowCenter.x = view.windowWidth * 0.5f
        view.windowCenter.y = view.windowHeight * 0.5f

        // non ci possono essere reminder
        view.tileRowOffset = if (view.windowWidth > view.windowHeight) 0 else 2
        val windowAddCol = if (view.windowWidth > view.windowHeight) 0 else 1
        val windowAddRow = if (view.windowWidth > view.windowHeight) 4 else 2
        view.windowBorder = 1

        // +2 per i bordi, +1 se la divisione contiene un resto (aggiungiamo sempre +1 )
        view.windowTileColumns += windowAddCol + view.windowBorder * 2
        view.windowTileRows += windowAddRow + view.windowBorder * 2

        // si sta disegnando dal vertice più in alto del rombo
        view.windowVerticesBuffer = ISSHelper.buildISSVertexBuffer(
            view.windowDimension,
            view.tileRowOffset,
            view.windowTileRows,
            view.windowTileColumns,
            map.tileWidth,
            map.tileHeight * .5f,
            map.tileWidth,
            map.tileHeight
        )

        // lo impostiamo una volta per tutte, tanto non verrà mai cambiato
        view.windowVerticesBuffer.update()

        // recupera gli offset X e mY maggiori (che vanno comunque a ricoprire gli alitr più piccoli)
        // e li usa per spostare la matrice della maschera. Tutti i tileset devono avere lo stesso screenOffsetX e Y
        /*
         * float maxLayerOffsetX = 0f; float maxLayerOffsetY = 0f; for (int i = 0; i < map.tileSets.size(); i++) { maxLayerOffsetX = XenonMath.max(map.tileSets.get(i).drawOffsetX, maxLayerOffsetX); maxLayerOffsetY =
		 * XenonMath.max(map.tileSets.get(i).drawOffsetY, maxLayerOffsetY); }
		 */
        val tempMesh: Mesh = MeshFactory.createSprite(1f, 1f, MeshOptions.build().bufferAllocation(BufferAllocationType.STATIC).indicesEnabled(true))
        wireWindow = MeshFactory.createWireframe(tempMesh)

        // calcoliamo
        view.tiledWindowWidth = view.windowTileColumns * map.tileWidth
        view.tiledWindowHeight = view.windowTileRows * map.tileHeight * .5f
        tiledWindowWidth = view.tiledWindowWidth
        tiledWindowHeight = view.tiledWindowHeight

        // punto di partenza delle tile. tiledWindow è sempre maggiore è il vettore dall'origine blu a quella rossa
        view.tileBase.setCoords(view.tiledWindowWidth - view.windowWidth, view.tiledWindowHeight - view.windowHeight)
        view.tileBase.mul(0.5f)
        lineDrawer = LineDrawer()
        lineDrawer.setLineWidth(4)
        lineDrawer.setColor(Color.RED)
        matrixWire = Matrix4x4()
    }

    /*
     * (non-Javadoc)
     *
     * @see com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler#convertScroll(com.abubusoft.xenon.math.Point2, float, float)
     */
    fun convertRawWindow2MapWindow(scrollInMap: Point2?, rawWindowX: Float, rawWindowY: Float) {

        // rawWindowY è rivolto verso il basso, prima di passarlo al metodo, bisogna invertire il segno
        //scrollInMap.set(ISSHelper.convertCenteredWindow2IsoWindow(controller, rawWindowX, -rawWindowY));
        scrollInMap.setCoords(rawWindowX, rawWindowY)
    }

    fun buildMapController(tiledMap: TiledMap?, cameraValue: Camera?): ISSMapController {
        controller = ISSMapController(tiledMap, cameraValue)
        return controller
    }

    fun buildTiledLayerHandler(layer: TiledLayer?): ISSTiledLayerHandler {
        return ISSTiledLayerHandler(layer)
    }

    fun buildObjectLayerHandler(layer: ObjectLayer?): ISSObjectLayerHandler {
        return ISSObjectLayerHandler(layer)
    }

    fun buildImageLayerHandler(layer: ImageLayer?): ISSImageLayerHandler {
        return ISSImageLayerHandler(layer)
    }

    var temp = 0
    var a = 0
    var b = 0

    init {

        // a differenza della orthogonal, le dimensioni delle tile sulla mappa sono diverse rispetto a quelle
        // sulla view.
        // sulla mappa isometrica le dimensioni delle tile sono dimezzate, quindi la larghezza della mappa cambia
        isoTileSize = map.tileHeight.toFloat()

        // dimensioni map nel sistema di coordinate della mappa
        map.mapWidth = map.tileColumns * map.tileWidth
        map.mapHeight = (map.tileRows * isoTileSize).toInt()
    }

    fun roundTileCoord(num: Int, denum: Int): Int {
        if (num / denum >= 0) return num / denum
        return if (Math.abs(num % denum) > 0) num / denum - 1 else {
            num / denum
        }
    }

    override fun convertMap2ViewLayer(offsetHolder: LayerOffsetHolder?, mapX: Int, mapY: Int) {
        val mix: Int
        val miy: Int

        // ricaviamo indici su mappa direttamente su staggered
        mix = (mapX + map.tileWidth / 2) / map.tileWidth
        miy = mapY / map.tileHeight * 2 // moltiplichiamo x 2

        // convertiamo da map a iso
        val ix: Int
        val iy: Int
        ix = (mapX + 2 * mapY) / 2
        iy = (-mapX + 2 * mapY) / 2

        // v2: ok
        // tiled iso index
        a = XenonMath.floorDiv(ix, map.tileHeight)
        offsetHolder.tileIndexX = a
        b = XenonMath.floorDiv(iy, map.tileHeight)
        offsetHolder.tileIndexY = b

        // passiamo da diamon a staggered
        offsetHolder.tileIndexX = XenonMath.floorDiv(a - b + Math.abs((a + b) % 2), 2)
        offsetHolder.tileIndexY = a + b
        /*
        int sx, sy;
        sx = Math.abs(ix % map.tileWidth);
        sy = Math.abs(iy % map.tileHeight);
*/
        val ox: Int = offsetHolder.tileIndexX
        val oy: Int = offsetHolder.tileIndexY

        // v2
        offsetHolder.screenOffsetY = mapY % map.tileHeight

        //v3
        var volo = Status.STANDARD
        if (Math.abs(offsetHolder.tileIndexY % 2) == 1) {
            volo = Status.ODD
            offsetHolder.tileIndexX = mix
            offsetHolder.tileIndexY = miy
            offsetHolder.screenOffsetX = mapX % map.tileWidth
            if (offsetHolder.screenOffsetX < 32) {
            } else {
                offsetHolder.screenOffsetX -= map.tileWidth
            }
        } else {
            offsetHolder.screenOffsetX = (mapX + map.tileWidth / 2) % map.tileWidth - map.tileWidth / 2
        }

        //  XenonLogger.info("[offsetHolder.tileIndexX-mix = %s], map[%s, %s] -> iso[%s, %s], tiles I[%s, %s] -> S[%s, %s] (OS[%s, %s]), map off x,y (%s, %s) [%s] [map idex: %s %s]", offsetHolder.tileIndexX-mix, mapX, mapY, ix, iy, a, b, offsetHolder.tileIndexX, offsetHolder.tileIndexY, ox, oy, offsetHolder.screenOffsetX, offsetHolder.screenOffsetY, volo, mix, miy);

        // inverte y
        offsetHolder.screenOffsetY = -offsetHolder.screenOffsetY
    }
}