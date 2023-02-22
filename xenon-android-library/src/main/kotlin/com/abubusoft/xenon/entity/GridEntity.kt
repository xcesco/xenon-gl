package com.abubusoft.xenon.entity

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.texture.TextureReference

/**
 * Space partiniong.
 *
 * @author Francesco Benincasa
 */
class GridEntity<E : GridCell?> : BaseEntity() {
    /**
     * Tipo di cella da istanziare
     */
    private var cellClazz: Class<E>? = null

    /**
     * le colonne pari sono messe più in basso di 1/2 tile
     */
    var oddColumnsLower = false
    var items: Array<E>

    /**
     * margine orizzontale della finestra
     */
    var windowHorizontalMargin = 0

    /**
     * distanza tra il centro di un tile ed il successivo
     */
    var windowVerticalMargin = 0

    /**
     * larghezza della tile
     */
    var tileWidth = 0
    var tileEffectiveWidth = 0

    /**
     * altezza della tile
     */
    var tileHeight = 0
    var tileEffectiveHeight = 0

    /**
     *
     */
    var worldMinX = 0f

    /**
     *
     */
    var worldMaxX = 0f

    /**
     *
     */
    var worldMinY = 0f

    /**
     *
     */
    var worldMaxY = 0f

    /**
     * centro della tile in basso a sinistra
     */
    var initialTilePosition: Point2? = null

    /**
     *
     *
     * centro della window
     *
     *
     *
     * <pre>
     * +------+---------------+
     * |      |               |
     * |  W   |               |
     * |      |               |
     * +------+---------------+
    </pre> *
     *
     */
    var windowCenterInitial: Point2

    /**
     * world width
     */
    var worldWidth = 0

    /**
     * world height
     */
    var worldHeight = 0

    /**
     * window width
     */
    var windowWidth = 0

    /**
     * offset massimo in larghezza con il quale la finestra si può spostare.
     */
    var windowMaxWitdhOffset = 0

    /**
     * window height
     */
    var windowHeight = 0

    /**
     * colonne visibili
     */
    var windowCols = 0

    /**
     * indica se lo schermo è portrait
     */
    var windowLandscape = false
    var windowScaleFactor = 0f
    var windowCenter: Point2

    /**
     * righe visibili
     */
    var windowRows = 0
    private val INFINITE_VALUE = 1000000000
    var gridRows = 0
    var gridCols = 0

    // private Point3 oldPosition;
    private var windowsItems: Array<E>
    private var lastOffsetY = 0f
    private var lastOffsetX = 0f

    /**
     * indica se la finestra di visualizzazione è stata spostata.
     */
    var windowMoved = false

    /**
     * Definisce rows e cols
     *
     * @param worldSizeX
     * @param worldSizeY
     * @param tileSizeValue
     * @param options
     */
    private fun defineRowsAndCols(worldSizeX: Int, worldSizeY: Int, options: GridOptions) {
        worldWidth = worldSizeX
        worldHeight = worldSizeY
        tileEffectiveWidth = (tileWidth * options.marginHorizontal).toInt()
        tileEffectiveHeight = (tileHeight * options.marginVertical).toInt()

        // dato che partiamo dal centro delle tile, dobbiamo mettere sempre e
        // comunque un pezzo per riempire
        // l'intera area.
        gridCols = worldWidth / tileEffectiveWidth + 1
        gridRows = worldHeight / tileEffectiveHeight + 1
        if (gridCols * tileEffectiveWidth < worldWidth) gridCols++
        if (gridRows * tileEffectiveHeight < worldHeight) gridRows++
        if (options.oddColumnsLower) {
            gridRows += 1
        }
    }

    /**
     * Definisce le coordinate delle varie tiles.
     *
     * @param worldSizeX
     * @param worldSizeY
     * @param tileSizeValue
     * @param options
     */
    private fun defineCoords(worldSizeX: Int, worldSizeY: Int, textureRef: TextureReference?, options: GridOptions) {
        worldMinX = INFINITE_VALUE.toFloat()
        worldMaxX = -INFINITE_VALUE.toFloat()
        worldMinY = INFINITE_VALUE.toFloat()
        worldMaxY = -INFINITE_VALUE.toFloat()

        // Delta tra una tiles ed un altra
        var position: Point3
        var texY1: Float
        var texX1: Float
        var texY2: Float
        var texX2: Float
        val offsetX: Int
        val offsetY: Int
        initialTilePosition = Point2()

        // ci posizioniamo sull'elemento in basso a sx
        // questa è la posizione del centro del primo tiles, quindi dobbiamo
        // spostarci della metà delle dimensioni
        // del tile
        val posX = -worldSizeX / 2.0f
        val posY = -worldSizeY / 2.0f

        // aspectRatio del world==1
        /*
		 * if (screenWidth/screenHeight==1 && oddColumnsLower) {
		 * posX-=tileEffectiveWidth*0.5f; }
		 */
        var cx: Float
        var cy: Float

        // posizione in basso a sinistra della tile iniziale
        // questo viene fatto a prescindere dall'eventuale
        // offset delle colonne pari.
        // memorizziamo le coordinate del vertice del primo box più in basso
        initialTilePosition!!.setCoords(posX - tileEffectiveWidth * 0.5f, posY - tileEffectiveHeight * 0.5f)

        // l'incremento è quello legato alle reali dimensioni delle tiles
        offsetX = tileEffectiveWidth
        offsetY = tileEffectiveHeight
        try {
            cx = posX
            for (j in 0 until gridCols) {
                cy = posY
                if (options.oddColumnsLower && j % 2 == 1) {
                    cy -= tileHeight * 0.5f
                }

                // resettiamo startX
                for (i in 0 until gridRows) {
                    // Posizione del tile
                    position = Point3(cx, cy, 0f) // options.distanceFromViewer);

                    // cx,cy rappresenta il centro della tile, lo 0.5 serve in
                    // quanto
                    // il sistema di coordinate ha centro in mezzo, quello delle
                    // texture
                    // in alto a sx
                    // texX1 = (cx - tileWidth / 2f) / worldSizeX+0.5f;
                    // texX2 = (cx + tileWidth / 2f) / worldSizeX+0.5f;
                    // aspectRatio del world==1
                    /*
					 * if (screenWidth/screenHeight==1 && oddColumnsLower) { texX1
					 * = (cx - tileWidth*0.25f) / worldSizeX+0.5f; texX2 = (cx +
					 * tileWidth*0.75f) / worldSizeX+0.5f; } else
					 */run {
                        texX1 = cx / worldSizeX + 0.5f
                        texX2 = (cx + tileWidth) / worldSizeX + 0.5f
                    }
                    texY1 = -(cy + tileHeight / 2f) / worldSizeY + 0.5f
                    texY2 = -(cy - tileHeight / 2f) / worldSizeY + 0.5f
                    if (textureRef != null) {
                        texY1 *= textureRef.get().info.dimension.normalizedMaxHeight
                        texY2 *= textureRef.get().info.dimension.normalizedMaxHeight
                    }
                    val entity = cellClazz!!.newInstance()
                    position.copyInto(entity.position)

                    // deve essere >0, altrimenti viene considerato nullo
                    entity.setTextureCoordinate(texX1, texX2, texY1, texY2)
                    entity.setDimensions(tileWidth, tileHeight)
                    items[gridRows * j + i] = entity
                    cy += offsetY.toFloat()
                }
                cx += offsetX.toFloat()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.fatal(e.message)
        }
    }

    /**
     *
     *
     * Imposta le dimensioni delta griglia.
     *
     *
     * @param rows
     * @param cols
     */
    fun buildGrid(
        worldSizeX: Int,
        worldSizeY: Int,
        windowWidthValue: Int,
        windowHeightValue: Int,
        tileWidthValue: Int,
        tileHeightValue: Int,
        textureRef: TextureReference?,
        cellClazzValue: Class<E>?,
        options: GridOptions
    ) {
        cellClazz = cellClazzValue

        // impostiamo le dimensioni delle tiles
        tileWidth = tileWidthValue
        tileHeight = tileHeightValue

        // rileviamo se siamo in landscape o portrait
        windowLandscape = if (windowWidthValue > windowHeightValue) true else false
        windowScaleFactor = options.windowScaleFactor
        windowHorizontalMargin = ((1f - options.marginHorizontal) * tileWidth * 0.25f).toInt()
        windowVerticalMargin = ((1f - options.marginVertical) * tileHeight * 0.25f).toInt()

        // a causa di alcune situazioni limite, dobbiamo necessariamente
        // aumentare il margine orizzontale
        // in caso di landscape.
        /*
		 * if (windowLandscape) { windowHorizontalMargin*=2; }
		 */

        // senza movimento precedente è stata creato.
        windowMoved = true
        oddColumnsLower = options.oddColumnsLower

        // calcoliamo
        defineRowsAndCols(worldSizeX, worldSizeY, options)

        // impostiamo i parametri
        items = java.lang.reflect.Array.newInstance(cellClazz, gridRows * gridCols) as Array<E>

        // calcoliamo le coordinate
        defineCoords(worldSizeX, worldSizeY, textureRef, options)

        // costruiamo la finestra
        buildVisibleWindow(windowWidthValue, windowHeightValue)
    }

    /**
     *
     *
     * Imposta le dimensioni della finestra in base alle coordinate già
     * trasformate rispetto al camera.
     *
     *
     * @param windowWidthValue
     * @param windowHeightValue
     */
    private fun buildVisibleWindow(windowWidthValue: Int, windowHeightValue: Int) {
        lastOffsetX = -INFINITE_VALUE.toFloat()
        lastOffsetY = -INFINITE_VALUE.toFloat()
        windowWidth = (windowWidthValue * windowScaleFactor).toInt()
        windowHeight = (windowHeightValue * windowScaleFactor).toInt()

        // dobbiamo calcolare quanti tile sono visibili
        // windowCols = (int) (Math.floor(windowWidth / (tileWidth * (1f - (1f -
        // marginHorizontal) / 2f))) + 1);
        // windowRows = (int) (Math.floor(windowHeight / (tileHeight * (1f - (1f
        // - marginVertical) / 2f))) + 1);
        windowCols = (windowWidth / tileEffectiveWidth + 2f).toInt() + 2
        if (windowLandscape) {
            windowCols += 2
        }
        windowRows = (windowHeight / tileEffectiveHeight + 2f).toInt() + 1
        windowCols = Math.min(gridCols, windowCols)
        windowRows = Math.min(gridRows, windowRows)

        // calcoliamo il centro iniziale
        // inizialmente l'origine del mondo coincide con il windowCenter.
        // per fare questo dobbiamo fare una trasformazione invertita:
        // da World -> WindowGrid
        // TODO portait
        // windowCenter.setCoords((-screenWidth+windowWidth-tileEffectiveWidth)*0.5f+windowHorizontalMargin,0f);
        // if (windowLandscape) {
        // dobbiamo diminuire width offset e l'inizio in quanto zdistance in
        // realtà dovrebbe essere
        //
        // windowCenter.setCoords((-screenWidth + windowWidth
        // -tileEffectiveWidth) * 0.5f, 0f);
        // windowMaxWitdhOffset = (int) (screenWidth - windowWidth - 3.2f *
        // windowHorizontalMargin);

        // } else {
        windowCenter.setCoords((-worldWidth + windowWidth - tileEffectiveWidth) * 0.5f + windowHorizontalMargin, 0f)
        // windowCenter.setCoords((-screenWidth + windowWidth -
        // tileEffectiveWidth) * 0.5f + windowHorizontalMargin, 0f);
        windowMaxWitdhOffset = (worldWidth - windowWidth - 3.2f * windowHorizontalMargin).toInt()
        // }
        // windowCenter.setCoords((-screenWidth+windowWidth+tileEffectiveWidth)*0.5f+windowHorizontalMargin,0f);
        // windowCenter.setCoords((-screenWidth+windowWidth)*0.5f+windowHorizontalMargin,0f);
        windowCenter.copyInto(windowCenterInitial)

        // offset width massimo che si può spostare. Togliamo eventualmente il
        // margine orizzontale, 3 volte (sx e dx)
        // TODO portait
        // windowMaxWitdhOffset=(int)
        // (screenWidth-windowWidth-3.2f*windowHorizontalMargin);
        // windowMaxWitdhOffset=(int)
        // (screenWidth-windowWidth-3.2f*windowHorizontalMargin-tileEffectiveWidth);
        windowsItems = java.lang.reflect.Array.newInstance(cellClazz, windowCols * windowRows) as Array<E>
    }

    /**
     *
     *
     *
     *
     * @param row
     * @param col
     * @return
     */
    fun getCell(row: Int, col: Int): E {
        return items[gridRows * col + row]
    }

    fun getVisibleCell(row: Int, col: Int): E {
        return items[windowRows * col + row]
    }

    /**
     * Recupera l'elemento
     *
     * @param currentWindowX
     * @param currentWindowY
     *
     * @return
     */
    fun getTouchedCell(currentWindowX: Float, currentWindowY: Float): E? {
        var index: Int

        // la collochiamo rispetto al sistema di riferimento della tile iniziale
        // initialTilePosition contiene le coordinate del centro della tile più
        // in basso
        // float currentTouchedPositionX = currentWindowX -
        // initialTilePosition.x +tileEffectiveWidth * 0.5f;
        // float currentTouchedPositionY = currentWindowY -
        // initialTilePosition.y + tileEffectiveHeight * 0.5f;

        // float currentTouchedPositionX = (currentWindowX +
        // windowCenter.x)-initialTilePosition.x -tileEffectiveWidth * 0.5f;
        // float currentTouchedPositionY = (currentWindowY +
        // windowCenter.y)-initialTilePosition.y - tileEffectiveHeight * 0.5f;
        val currentTouchedPositionX = currentWindowX + windowCenter.x - initialTilePosition!!.x
        val currentTouchedPositionY = currentWindowY + windowCenter.y - initialTilePosition!!.y

        // float currentTouchedPositionX = currentWindowX +tileEffectiveWidth *
        // 0.5f;
        // float currentTouchedPositionY = currentWindowY + tileEffectiveHeight
        // * 0.5f;

        // dobbiamo calcolare quanti tile sono visibili
        // aggiungiamo tileEffectiveWidth*0.5f per andare sicuramente oltre il
        // centro della tile.
        var currentCol = (currentTouchedPositionX / tileEffectiveWidth).toInt()
        var currentRow = (currentTouchedPositionY / tileEffectiveHeight).toInt()

        /*
		 * if (oddColumnsLower && (currentCol % 2 == 0)) { currentWindowY -=
		 * tileEffectiveHeight * 0.5f; }
		 */currentCol = if (currentCol < 0) 0 else currentCol
        currentRow = if (currentRow < 0) 0 else currentRow
        val touchedRow = intArrayOf( // colonna precedente
            currentRow - 1, currentCol - 1, currentRow, currentCol - 1, currentRow + 1, currentCol - 1,  // colonna selezionata
            currentRow - 1, currentCol, currentRow, currentCol, currentRow + 1, currentCol,  // colonna successiva
            currentRow - 1, currentCol + 1, currentRow, currentCol + 1, currentRow + 1, currentCol + 1
        )
        var c: Int
        var r: Int
        if (tileEffectiveWidth / tileWidth < 0.51f && windowLandscape) {
            Logger.debug("METODO  Landscape")
            // in caso di tile ristretto in larghezza e landscape, andiamo inditro
            var i = touchedRow.size - 2
            while (i >= 0) {
                r = touchedRow[i]
                c = touchedRow[i + 1]
                if (isValid(r, c)) {
                    // elemento centrale
                    index = c * gridRows + r
                    if (isTouched(items[index], currentWindowX + windowCenter.x, currentWindowY + windowCenter.y)) {
                        Logger.debug("---OK")
                        return items[index]
                    } else {
                        Logger.error("NONE")
                    }
                }
                i -= 2
            }
        } else {
            Logger.debug("METODO  Portrait")
            var i = 0
            while (i < touchedRow.size) {
                r = touchedRow[i]
                c = touchedRow[i + 1]
                if (isValid(r, c)) {
                    // elemento centrale
                    index = c * gridRows + r
                    if (isTouched(items[index], currentWindowX + windowCenter.x, currentWindowY + windowCenter.y)) {
                        Logger.debug("---OK")
                        return items[index]
                    } else {
                        Logger.error("NONE")
                    }
                }
                i += 2
            }
        }
        return null
    }

    init {
        windowCenter = Point2()
        windowCenterInitial = Point2()
    }

    private fun isValid(row: Int, col: Int): Boolean {
        return row >= 0 && row < gridRows && col >= 0 && col < gridCols
    }

    private fun isTouched(entity: GridCell, currentX: Float, currentY: Float): Boolean {
        Logger.debug(
            "Range X %s < %s < %s = %s", entity.position.x - tileEffectiveWidth * FACTOR, currentX, entity.position.x + tileEffectiveWidth * FACTOR,
            currentX >= entity.position.x - tileEffectiveWidth * FACTOR && currentX <= entity.position.x + tileEffectiveWidth * FACTOR
        )
        Logger.debug(
            "Range Y %s < %s < %s = %s", entity.position.y - tileEffectiveHeight * FACTOR, currentY, entity.position.y + tileEffectiveHeight * FACTOR,
            currentY >= entity.position.y - tileEffectiveHeight * FACTOR && currentY <= entity.position.y + tileEffectiveHeight * FACTOR
        )
        if (currentX >= entity.position.x - tileEffectiveWidth * FACTOR && currentX <= entity.position.x + tileEffectiveWidth * FACTOR) {
            if (currentY >= entity.position.y - tileEffectiveHeight * FACTOR && currentY <= entity.position.y + tileEffectiveHeight * FACTOR) {
                return true
            }
        }
        return false
    }

    /**
     * @param camera
     * @return
     */
    fun scrollWindowTo(offsetX: Float, offsetY: Float): Array<E> {
        var offsetX = offsetX
        if (offsetX == lastOffsetX && offsetY == lastOffsetY) {
            windowMoved = false
            return windowsItems
        }
        lastOffsetX = offsetX
        lastOffsetY = offsetY
        windowMoved = true
        if (offsetX > windowMaxWitdhOffset) {
            offsetX = windowMaxWitdhOffset.toFloat()
        }
        windowCenter.setCoords(windowCenterInitial.x + offsetX, windowCenterInitial.y + offsetY)

        // dobbiamo calcolare quanti tile sono visibili
        // prendiamo
        val currentCol = (windowCenter.x + worldWidth * 0.5f / tileEffectiveWidth).toInt()
        val currentRow = (windowCenter.y + worldHeight * 0.5f / tileEffectiveHeight).toInt()

        // primi elementi, si parte cmq da 0 come minimo
        var firstCol = currentCol - windowCols / 2
        firstCol = if (firstCol < 0) 0 else firstCol
        var firstRow = currentRow - windowRows / 2
        firstRow = if (firstRow < 0) 0 else firstRow

        // con questo controllo impediamo che la window vada oltre la
        // definizione
        // della grid
        if (firstCol + windowCols > gridCols) {
            firstCol = gridCols - windowCols
        }
        var curCol = firstCol
        var curRow = firstRow

        // ricordiamoci che si parte dall'angolo in basso a sx della griglia, si
        // prosegue in verticale
        // e poi ci si sposta da dx
        for (i in 0 until windowCols) {
            curRow = firstRow
            for (j in 0 until windowRows) {
                windowsItems[i * windowRows + j] = items[curCol * gridRows + curRow]
                curRow++
            }
            curCol++
        }
        return windowsItems
    }

    companion object {
        private const val serialVersionUID = 158695967345870381L
        private const val FACTOR = 0.5f
    }
}