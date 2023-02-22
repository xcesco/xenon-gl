package com.abubusoft.xenon.mesh.tiledmaps.isometric

import com.abubusoft.xenon.XenonApplication4OpenGL
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.texture.Texture

/**
 *
 *
 * Gestore della mappa.
 *
 *
 * <h1>Costruzione della view</h1>
 *
 *
 *
 * La view sulla mappa viene sempre costruita della forma del rombo. Se la mappa è più corta da una delle due dimensioni, i conti verranno fatti come se fosse comunque un rombo completo. La view sulla mappa viene costruita mediante il
 * metodo [.onBuildView]. Lo schermo può essere sia landscape che portrait, quindi i risultati possono essere:
 *
 *
 * <h2>Passaggio da mappa a window</h2>
 *
 *
 * Il passaggio tra mappa e window è diverso rispetto alla situazione orthogonal perchè in memoria, la mappa ha le tile di una certa dimensione, mentre nella window le dimensioni sono diverse.
 *
 * <img src="./doc-files/Map2Window.png"></img>/ >
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
 *
 * @author xcesco
 */
@Uncryptable
class IsometricMapHandler(map: TiledMap) : AbstractMapHandler<IsometricMapController?>(map) {
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
     * Dimensione (width e height) della window su base isometrica. In altre parole, le dimensioni del diamante di visualizzazione nel sistema della mappa.
     *
     */
    var isoWindowSize = 0f
    private val maskMatrix: Matrix4x4
    private val maskMatrixTotal: Matrix4x4
    private val maskShader: ShaderTexture

    init {

        // a differenza della orthogonal, le dimensioni delle tile sulla mappa sono diverse rispetto a quelle
        // sulla view.
        // sulla mappa isometrica le dimensioni delle tile sono dimezzate, quindi la larghezza della mappa cambia
        isoTileSize = map.tileHeight.toFloat()

        // dimensioni map nel sistema di coordinate della mappa
        map.mapWidth = (map.tileColumns * isoTileSize).toInt()
        map.mapHeight = (map.tileRows * isoTileSize).toInt()

        // creiamo, se serve il mask mesh
        // if (maskMesh == null) {
        maskMesh = MeshFactory.loadFromResources(XenonBeanContext.context, R.raw.tiledmap_mask_diamond_mesh, MeshFileFormatType.KRIPTON_JSON, MeshOptions.build())
        maskTexture = TextureManager.instance().createTextureFromResourceId(XenonBeanContext.context, R.raw.tiledmap_mask_diamond_image, TextureOptions.build())
        // }
        maskMatrix = Matrix4x4()
        maskMatrixTotal = Matrix4x4()
        maskShader = ShaderManager.instance().createShaderTexture()
    }

    /**
     *
     *
     * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
     *
     *
     *
     *
     * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel [XenonApplication4OpenGL.onSceneReady])
     *
     *
     * <pre>
     * GLES20.glEnable(GLES20.GL_BLEND);
     * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    </pre> *
     *
     * @param deltaTime
     * tempo trascorso dall'ultimo draw
     * @param modelViewProjection
     * matrice MVP
     */
    fun draw(deltaTime: Long, modelViewProjection: Matrix4x4?) {
        super.draw(deltaTime, modelViewProjection)

        // disegniamo anche la maschera
        maskShader.use()
        maskShader.setVertexCoordinatesArray(maskMesh.vertices)
        maskShader.setTexture(0, maskTexture)
        maskShader.setTextureCoordinatesArray(0, maskMesh.textures.get(0))
        maskMatrixTotal.buildIdentityMatrix()
        maskMatrixTotal.multiply(modelViewProjection, maskMatrix)
        ShaderDrawer.draw(maskShader, maskMesh, maskMatrixTotal)
    }

    /**
     *
     *
     * Nella costruzione della window, per le mappe isometriche, bisogna tenere in considerazione che quello che conta per il riempimento dello schermo contano solo le dimensioni del diamante. L'opzione [TiledMapFillScreenType]
     *
     */
    fun onBuildView(view: TiledMapView?, camera: Camera?, options: TiledMapOptions?) {
        val screenInfo: ScreenInfo = XenonGL.screenInfo
        // impostiamo metodo di riempimento dello schermo
        view.windowDimension = 0f
        view.windowBorder = 1

        // serve per calcolare la size del diamante su base window/screen
        var size = 0f
        when (options.fillScreenType) {
            TiledMapFillScreenType.FILL_HEIGHT -> {
                isoWindowSize = isoTileSize * map.tileColumns
                size = map.tileWidth * map.tileColumns
                view.windowDimension = map.mapWidth

                // non possiamo muovere il diamond
                view.mapMaxPositionValueX = 0f
                view.mapMaxPositionValueY = 0f
                view.windowTileColumns = map.tileColumns + 2 * view.windowBorder
                view.windowTileRows = map.tileRows + 2 * view.windowBorder
            }
            TiledMapFillScreenType.FILL_WIDTH -> {
                isoWindowSize = isoTileSize * map.tileColumns
                size = map.tileWidth * map.tileColumns
                view.windowDimension = map.mapWidth

                // non possiamo muovere il diamond  
                view.mapMaxPositionValueX = 0f
                view.mapMaxPositionValueY = 0f

                // view.windowDimension = map.mapWidth * screenInfo.correctionY;
                view.windowTileColumns = map.tileColumns + 2 * view.windowBorder
                view.windowTileRows = map.tileRows + 2 * view.windowBorder
            }
            TiledMapFillScreenType.FILL_CUSTOM_HEIGHT -> {
                isoWindowSize = isoTileSize * options.visibleTiles
                size = map.tileWidth * options.visibleTiles
                view.windowDimension = options.visibleTiles * map.tileHeight
                view.mapMaxPositionValueX = map.mapWidth - view.windowDimension
                view.mapMaxPositionValueY = map.mapWidth - view.windowDimension
                view.windowTileColumns = options.visibleTiles + 2
                view.windowTileRows = options.visibleTiles + 2
            }
            TiledMapFillScreenType.FILL_CUSTOM_WIDTH -> {
                isoWindowSize = isoTileSize * options.visibleTiles
                size = map.tileWidth * options.visibleTiles
                view.windowDimension = options.visibleTiles * map.tileHeight
                view.mapMaxPositionValueX = map.mapWidth - view.windowDimension
                view.mapMaxPositionValueY = map.mapWidth - view.windowDimension
                view.windowTileColumns = options.visibleTiles + 2 * view.windowBorder
                view.windowTileRows = options.visibleTiles + 2 * view.windowBorder
            }
        }
        view.windowDimension *= options.visiblePercentage
        view.distanceFromViewer = XenonMath.zDistanceForSquare(camera, size)

        // scenario 1
        view.windowWidth = (view.windowDimension / screenInfo.correctionY).toInt()
        view.windowHeight = (view.windowDimension / screenInfo.correctionX).toInt()
        // view.windowHeight = view.windowWidth;

        // non ci possono essere reminder
        //int windowRemainderX = 0; // view.windowWidth % map.tileWidth;
        //int windowRemainderY = 0; // ;view.windowHeight % map.tileHeight;

        // con le mappe isometriche si lavora con un rombo per view
        // +2 per i bordi, +1 se la divisione contiene un resto
        // view.windowTileColumns = (int) (view.windowWidth / map.tileWidth) + (view.windowRemainderX == 0 ? 0 : 1) + 2;
        // view.windowTileRows = (int) (view.windowHeight / map.tileHeight) + (view.windowRemainderY == 0 ? 0 : 1) + 2;

        // righe e colonne sono uguali!
        // int maxSize = XenonMath.max(view.windowTileColumns, view.windowTileRows);
        // view.windowTileColumns = maxSize;
        // view.windowTileRows = maxSize;

        // calcoliamo il centro dello schermo, considerando che abbiamo 2
        // elementi in più per lato (quindi dobbiamo
        // togliere una dimensione per parte).
        view.windowCenter.x = view.windowWidth * 0.5f + map.tileWidth
        view.windowCenter.y = view.windowHeight * 0.5f + map.tileHeight

        // si sta disegnando dal vertice più in alto del rombo

        // creiamo il vertici del vertex buffer della tiled map. Questo buffer viene condiviso ed utilizzato da tutti
        // i layer che hanno come dimensione delle tile le stesse di default.
        /*
		 * float sceneX; float sceneY; view.windowVerticesBuffer = BufferManager.instance().createVertexBuffer(view.windowTileColumns * view.windowTileRows * VertexBuffer.VERTEX_IN_QUAD_TILE, BufferAllocationType.STATIC); for (int i = 0; i
		 * < view.windowTileRows; i++) { for (int j = 0; j < view.windowTileColumns; j++) { sceneX = (j - 1 )* (map.tileWidth * 0.5f) - i * (map.tileWidth * 0.5f) ; sceneY =(map.tileRows -1 - i ) * map.tileHeight*0.5f;
		 * 
		 * VertexQuadModifier.setVertexCoords(view.windowVerticesBuffer, i * view.windowTileColumns + j, sceneX, sceneY, map.tileWidth, map.tileHeight, false); } }
		 */view.windowVerticesBuffer =
            IsometricHelper.buildDiamondVertexBuffer(view.windowTileRows, view.windowTileColumns, map.tileWidth * 0.5f, map.tileHeight * 0.5f, map.tileWidth, map.tileHeight)
        // windowAttributeBuffer=IsometricHelper.buildDiamondOffsetAttributeBuffer(view.windowTileRows, view.windowTileColumns);

        // lo impostiamo una volta per tutte, tanto non verrà mai cambiato
        view.windowVerticesBuffer.update()

        // recupera gli offset X e mY maggiori (che vanno comunque a ricoprire gli alitr più piccoli)
        // e li usa per spostare la matrice della maschera. Tutti i tileset devono avere lo stesso screenOffsetX e Y
        /*
		 * float maxLayerOffsetX = 0f; float maxLayerOffsetY = 0f; for (int i = 0; i < map.tileSets.size(); i++) { maxLayerOffsetX = XenonMath.max(map.tileSets.get(i).drawOffsetX, maxLayerOffsetX); maxLayerOffsetY =
		 * XenonMath.max(map.tileSets.get(i).drawOffsetY, maxLayerOffsetY); }
		 */

        // maskMatrix.buildScaleMatrix(map.tileWidth * 10, map.tileHeight * 10, 0);
        maskMatrix.buildIdentityMatrix()
        // maskMatrix.buildTranslationMatrix(maxLayerOffsetX, -maxLayerOffsetY, 0);
        // maskMatrix.scale(view.windowDimension, view.windowDimension, 1);
        maskMatrix.scale(size, size, 1f)
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler#convertScroll(com.abubusoft.xenon.math.Point2, float, float)
	 */
    fun convertRawWindow2MapWindow(scrollInMap: Point2?, rawWindowX: Float, rawWindowY: Float) {
        // rawWindowY è rivolto verso il basso, prima di passarlo al metodo, bisogna invertire il segno
        scrollInMap.set(IsometricHelper.convertCenteredWindow2IsoWindow(controller, rawWindowX, -rawWindowY))
    }

    fun buildMapController(tiledMap: TiledMap?, cameraValue: Camera?): IsometricMapController {
        controller = IsometricMapController(tiledMap, cameraValue)
        return controller
    }

    fun buildTiledLayerHandler(layer: TiledLayer?): IsometricTiledLayerHandler {
        return IsometricTiledLayerHandler(layer)
    }

    fun buildObjectLayerHandler(layer: ObjectLayer?): IsometricObjectLayerHandler {
        return IsometricObjectLayerHandler(layer)
    }

    fun buildImageLayerHandler(layer: ImageLayer?): IsometricImageLayerHandler {
        return IsometricImageLayerHandler(layer)
    }

    override fun convertMap2ViewLayer(offsetHolder: LayerOffsetHolder?, mapX: Int, mapY: Int) {
        // http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
        offsetHolder.tileIndexX = (mapX / map.tileHeight) as Int
        offsetHolder.tileIndexY = (mapY / map.tileHeight) as Int

        // soluzione fixata
        offsetHolder.setOffset(IsometricHelper.convertIsoMapOffset2ScreenOffset(mapX % map.tileHeight, mapY % map.tileHeight))
    }

    companion object {
        private var maskMesh: Mesh
        private var maskTexture: Texture
    }
}