/**
 *
 */
package com.abubusoft.xenon.mesh

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.animations.TextureTimeline
import com.abubusoft.xenon.entity.GridCell
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.Tile
import com.abubusoft.xenon.texture.TextureRegion
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.vbo.IndexBuffer

/**
 *
 *
 * Mesh atta a contenere l'insieme di tile che devono essere disegnati. Per il fatto che ogni tile può avere delle dimensioni diverse da quelle standard.
 *
 *
 *
 *
 * Viene definito una dimensione massima dello shape, ma si possono utilizzare anche di meno, grazie all'uso del cursore.
 *
 *
 *
 *
 * Ogni tile è definito da un quad tile: due triangoli con due vertici in comune.
 *
 *
 * @author Francesco Benincasa
 */
@BindType
class MeshGrid internal constructor() : QuadMesh() {
    /**
     * indica la tile corrente. Va da 0 fino al massimo di gridCols*gridRows
     */
    @Bind
    var cursor = 0

    /**
     *
     *
     * Imposta al primo tile
     */
    fun cursorReset() {
        cursor = 0
    }

    /**
     *
     *
     * Sposta il cursore al prossimo tile, quindi +1
     *
     */
    fun cursorMove() {
        //
        cursor += 1
    }

    /**
     *
     *
     * Legge il cursore attuale
     *
     *
     */
    fun cursorRead(): Int {
        return cursor
    }

    /**
     * vettore usato per la rototraslazione dei vertici
     */
    @BindDisabled
    @Transient
    private val tempVertexSource: FloatArray

    /**
     * vettore usato per la rototraslazione dei vertici
     */
    @BindDisabled
    @Transient
    private val tempVertexDestV2: FloatArray

    /**
     * vettore usato per la rototraslazione dei vertici
     */
    @BindDisabled
    @Transient
    private val tempVertexDestV1: FloatArray

    /**
     * vettore usato per la rototraslazione dei vertici
     */
    @BindDisabled
    @Transient
    private val tempVertexDestV3: FloatArray

    /**
     * variabili temporanee per l'allocazione dei vertici.
     */
    @BindDisabled
    @Transient
    private var v1x = 0f

    @BindDisabled
    @Transient
    private var v1y = 0f

    @BindDisabled
    @Transient
    private var v2x = 0f

    @BindDisabled
    @Transient
    private var v2y = 0f

    @BindDisabled
    @Transient
    private var v3x = 0f

    @BindDisabled
    @Transient
    private var v3y = 0f

    @BindDisabled
    @Transient
    private var v4x = 0f

    @BindDisabled
    @Transient
    private var v4y = 0f

    /**
     * dimensioni di default della tile
     */
    @Bind
    var tileWidth = 0

    /**
     * dimensioni di default della tile
     */
    @Bind
    var tileHeight = 0

    /**
     * numero di righe
     */
    @Bind
    var gridRows = 0

    /**
     * numero di colonne
     */
    @Bind
    var gridCols = 0

    /**
     * variabile temporanea usata all'interno dei metodi
     */
    @BindDisabled
    @Transient
    private var basePtr = 0

    /**
     * Così non può essere definito se non nella factory
     */
    init {
        //
        tempVertexSource = FloatArray(3)
        tempVertexDestV2 = FloatArray(3)
        tempVertexDestV1 = FloatArray(3)
        tempVertexDestV3 = FloatArray(3)
    }
    /**
     *
     *
     * Applica ai vertici di una cell una matrice di trasformazione. Il sistema di riferimento utilizzato è quello dell'intero entity.
     *
     *
     * <pre>
     * +--------+
     * | 1    4 |
     * |        |
     * | 2    3 |
     * +--------+
    </pre> *
     *
     *
     *
     * I triangoli hanno i vertici: 123 e 341
     *
     *
     *
     * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
     *
     *
     * @param selectedRow
     * @param selectedColumn
     * @param leftX
     * @param topY
     * @param tile
     */
    /*
	 * public void setVertexCoords(int selectedRow, int selectedColumn, Matrix4x4 matrix, GridCell cell) {
	 * 
	 * // triangle 1: 123 basePtr = (selectedRow * gridCols + selectedColumn) * VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr +
	 * OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y]; tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; // mettiamo in
	 * tempVertexDestV1 così da poterlo riusare matrix.multiply(tempVertexSource, tempVertexDestV1); vertices.coords[basePtr + OFFSET_X] = tempVertexDestV1[OFFSET_X];
	 * vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV1[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV1[OFFSET_Z];
	 * 
	 * basePtr += VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr + OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y];
	 * tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; matrix.multiply(tempVertexSource, tempVertexDestV2); vertices.coords[basePtr + OFFSET_X] =
	 * tempVertexDestV2[OFFSET_X]; vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z];
	 * 
	 * basePtr += VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr + OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y];
	 * tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; matrix.multiply(tempVertexSource, tempVertexDestV3); vertices.coords[basePtr + OFFSET_X] =
	 * tempVertexDestV3[OFFSET_X]; vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV3[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV3[OFFSET_Z];
	 * 
	 * // triangle 2: 341
	 * 
	 * basePtr += VERTICES_DIMENSIONS; tempVertexSource[OFFSET_X] = vertices.coords[basePtr + OFFSET_X]; tempVertexSource[OFFSET_Y] = vertices.coords[basePtr + OFFSET_Y];
	 * tempVertexSource[OFFSET_Z] = vertices.coords[basePtr + OFFSET_Z]; matrix.multiply(tempVertexSource, tempVertexDestV2); vertices.coords[basePtr + OFFSET_X] =
	 * tempVertexDestV2[OFFSET_X]; vertices.coords[basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y]; vertices.coords[basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z];
	 * 
	 * }
	 */
    /**
     *
     *
     * Imposta i vertici per un tile (row, col). La z viene ricavata dalle posizioni
     *
     *
     * <pre>
     * +--------+
     * | 1    4 |
     * |        |
     * | 2    3 |
     * +--------+
    </pre> *
     *
     *
     *
     * I triangoli hanno i vertici: 123 e 341
     *
     *
     *
     * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
     *
     *
     * @param selectedRow
     * @param selectedColumn
     * @param leftX
     * @param topY
     */
    fun setVertexCoords(selectedRow: Int, selectedColumn: Int, leftX: Float, topY: Float, matrix: Matrix4x4, cell: GridCell) {
        val TILE_WIDTH_T = cell.width / 2f
        val TILE_HEIGHT_T = cell.height / 2f
        val centerX = leftX + TILE_WIDTH_T
        val centerY = topY - TILE_HEIGHT_T

        // triangle 1: 123
        basePtr = (selectedRow * gridCols + selectedColumn) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS
        tempVertexSource[OFFSET_X] = -TILE_WIDTH_T
        tempVertexSource[OFFSET_Y] = TILE_HEIGHT_T
        tempVertexSource[OFFSET_Z] = 0f
        // mettiamo in tempVertexDestV1 così da poterlo riusare
        matrix.multiply(tempVertexSource, tempVertexDestV1)
        vertices!!.coords!![basePtr + OFFSET_X] = tempVertexDestV1[OFFSET_X] + centerX
        vertices!!.coords!![basePtr + OFFSET_Y] = tempVertexDestV1[OFFSET_Y] + centerY
        vertices!!.coords!![basePtr + OFFSET_Z] = tempVertexDestV1[OFFSET_Z]
        basePtr += VERTICES_DIMENSIONS
        tempVertexSource[OFFSET_X] = -TILE_WIDTH_T
        tempVertexSource[OFFSET_Y] = -TILE_HEIGHT_T
        tempVertexSource[OFFSET_Z] = 0f
        matrix.multiply(tempVertexSource, tempVertexDestV2)
        vertices!!.coords!![basePtr + OFFSET_X] = tempVertexDestV2[OFFSET_X] + centerX
        vertices!!.coords!![basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y] + centerY
        vertices!!.coords!![basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z]
        basePtr += VERTICES_DIMENSIONS
        tempVertexSource[OFFSET_X] = TILE_WIDTH_T
        tempVertexSource[OFFSET_Y] = -TILE_HEIGHT_T
        tempVertexSource[OFFSET_Z] = 0f
        matrix.multiply(tempVertexSource, tempVertexDestV3)
        vertices!!.coords!![basePtr + OFFSET_X] = tempVertexDestV3[OFFSET_X] + centerX
        vertices!!.coords!![basePtr + OFFSET_Y] = tempVertexDestV3[OFFSET_Y] + centerY
        vertices!!.coords!![basePtr + OFFSET_Z] = tempVertexDestV3[OFFSET_Z]
        basePtr += VERTICES_DIMENSIONS
        tempVertexSource[OFFSET_X] = TILE_WIDTH_T
        tempVertexSource[OFFSET_Y] = TILE_HEIGHT_T
        tempVertexSource[OFFSET_Z] = 0f
        matrix.multiply(tempVertexSource, tempVertexDestV2)
        vertices!!.coords!![basePtr + OFFSET_X] = tempVertexDestV2[OFFSET_X] + centerX
        vertices!!.coords!![basePtr + OFFSET_Y] = tempVertexDestV2[OFFSET_Y] + centerY
        vertices!!.coords!![basePtr + OFFSET_Z] = tempVertexDestV2[OFFSET_Z]
    }

    /**
     *
     *
     * Usa il cursore per indicare un tile. Questa funzione mette a 0 la z.
     *
     *
     * <pre>
     * +--------+
     * | 1    4 |
     * |        |
     * | 2    3 |
     * +--------+
    </pre> *
     *
     * <pre>
     * 1   1(6)---4(5)
     * |\   \  |
     * | \   \ |
     * |  \   \|
     * 2---3   3 (4)
    </pre> *
     *
     *
     *
     * I triangoli hanno i vertici: 123 e 341
     *
     *
     *
     * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
     *
     *
     *
     *
     * Il controllo validità del tile si presume essere stato fatto prima di invocare questo metodo.
     *
     *
     *
     */
    fun setVertexCoordsOnCursor(leftX: Float, topY: Float, tile: Tile) {
        val TILE_WIDTH = tile.width.toFloat()
        val TILE_HEIGHT = tile.height.toFloat()

        // gestione delle rotazioni startX, startY e diagonali
        // if (!tile.diagonalFlip) {
        v1x = leftX
        v1y = topY
        v2x = leftX
        v2y = topY - TILE_HEIGHT
        v3x = leftX + TILE_WIDTH
        v3y = topY - TILE_HEIGHT
        v4x = leftX + TILE_WIDTH
        v4y = topY

        // triangle 1
        // cursor rappresenta il numero di vertici. Per ogni vertice ci sono
        // 3 coordinate per i vertici
        basePtr = cursor * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = v1x
        vertices!!.coords!![basePtr + OFFSET_Y] = v1y
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = v2x
        vertices!!.coords!![basePtr + OFFSET_Y] = v2y
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = v3x
        vertices!!.coords!![basePtr + OFFSET_Y] = v3y
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = v4x
        vertices!!.coords!![basePtr + OFFSET_Y] = v4y
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
    }

    /**
     *
     *
     * Imposta i vertici per un tile (row, col). Questa funzione mette a 0 la z.
     *
     *
     * <pre>
     * +--------+
     * | 1    4 |
     * |        |
     * | 2    3 |
     * +--------+
    </pre> *
     *
     * <pre>
     * 1   1(6)---4(5)
     * |\   \  |
     * | \   \ |
     * |  \   \|
     * 2---3   3 (4)
    </pre> *
     *
     *
     *
     * I triangoli hanno i vertici: 123 e 341
     *
     *
     *
     * Se tile da disegnare è invalido, ovvero se ha un gid==0 o se è null, non viene disegnato nulla.
     *
     *
     * @param selectedRow
     * @param selectedColumn
     * @param leftX
     * @param topY
     * @param tile
     */
    fun setVertexCoords(selectedRow: Int, selectedColumn: Int, leftX: Float, topY: Float, tile: Tile) {
        if (!Tile.isEmpty(tile)) {
            val TILE_WIDTH = tile.width.toFloat()
            val TILE_HEIGHT = tile.height.toFloat()
            v1x = leftX
            v1y = topY
            v2x = leftX
            v2y = topY - TILE_HEIGHT
            v3x = leftX + TILE_WIDTH
            v3y = topY - TILE_HEIGHT
            v4x = leftX + TILE_WIDTH
            v4y = topY

            // diagonali gestite da texture
            // gestione delle rotazioni startX, startY e diagonali

            // triangle 1
            basePtr = (selectedRow * gridCols + selectedColumn) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = v1x
            vertices!!.coords!![basePtr + OFFSET_Y] = v1y
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f
            basePtr += VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = v2x
            vertices!!.coords!![basePtr + OFFSET_Y] = v2y
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f
            basePtr += VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = v3x
            vertices!!.coords!![basePtr + OFFSET_Y] = v3y
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f

            // triangle 2
            basePtr += VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = v4x
            vertices!!.coords!![basePtr + OFFSET_Y] = v4y
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        } else {
            // triangle 1
            basePtr = (selectedRow * gridCols + selectedColumn) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = 0f
            vertices!!.coords!![basePtr + OFFSET_Y] = 0f
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f
            basePtr += VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = 0f
            vertices!!.coords!![basePtr + OFFSET_Y] = 0f
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f
            basePtr += VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = 0f
            vertices!!.coords!![basePtr + OFFSET_Y] = 0f
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f

            // triangle 2
            basePtr += VERTICES_DIMENSIONS
            vertices!!.coords!![basePtr + OFFSET_X] = 0f
            vertices!!.coords!![basePtr + OFFSET_Y] = 0f
            vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        }
    }

    /**
     *
     *
     * Imposta i vertici per una grid cell (row, col). Questa funzione mette a 0 la z.
     *
     *
     * <pre>
     * +--------+
     * | 1    4 |
     * |        |
     * | 2    3 |
     * +--------+
    </pre> *
     *
     *
     *
     * I triangoli hanno i vertici: 123 e 341
     *
     *
     * @param selectedRow
     * @param selectedColumn
     * @param leftX
     * @param topY
     * @param cell
     */
    fun setVertexCoords(selectedRow: Int, selectedColumn: Int, leftX: Float, topY: Float, cell: GridCell) {

        // triangle 1
        basePtr = (selectedRow * gridCols + selectedColumn) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX
        vertices!!.coords!![basePtr + OFFSET_Y] = topY
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX
        vertices!!.coords!![basePtr + OFFSET_Y] = topY - cell.height
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX + cell.width
        vertices!!.coords!![basePtr + OFFSET_Y] = topY - cell.height
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f

        // triangle 2
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX + cell.width
        vertices!!.coords!![basePtr + OFFSET_Y] = topY
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
    }

    /**
     *
     *
     * Imposta i vertici per una grid cell (row, col). Questa funzione mette a 0 la z.
     *
     *
     * <pre>
     * +--------+
     * | 1    4 |
     * |        |
     * | 2    3 |
     * +--------+
    </pre> *
     *
     *
     *
     * I triangoli hanno i vertici: 123 e 341. Detto quindi in altro modo, leftX e topY rappresentano le coordinate rispetto al sistema di riferimento dello shape del vertex 1.
     *
     *
     * @param selectedRow
     * @param selectedColumn
     * @param leftX
     * @param topY
     * @param widthValue
     * @param heightValue
     */
    fun setVertexCoords(selectedRow: Int, selectedColumn: Int, leftX: Float, topY: Float, widthValue: Int, heightValue: Int) {

        // triangle 1
        basePtr = (selectedRow * gridCols + selectedColumn) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX
        vertices!!.coords!![basePtr + OFFSET_Y] = topY
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX
        vertices!!.coords!![basePtr + OFFSET_Y] = topY - heightValue
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX + widthValue
        vertices!!.coords!![basePtr + OFFSET_Y] = topY - heightValue
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f

        // triangle 2
        basePtr += VERTICES_DIMENSIONS
        vertices!!.coords!![basePtr + OFFSET_X] = leftX + widthValue
        vertices!!.coords!![basePtr + OFFSET_Y] = topY
        vertices!!.coords!![basePtr + OFFSET_Z] = 0f
    }

    /**
     *
     *
     * Imposta le coordinate della texture numero textureIndex.
     *
     *
     */
    fun setTextureCoords(row: Int, col: Int, textureLowX: Float, textureHighX: Float, textureLowY: Float, textureHighY: Float) {
        setTextureCoords(TEXTURE_DEFAULT_INDEX, row, col, textureLowX, textureHighX, textureLowY, textureHighY)
    }

    /**
     *
     *
     * Imposta le coordinate della texture numero textureIndex.
     *
     *
     */
    fun setTextureCoords(textureIndex: Int, row: Int, col: Int, textureLowX: Float, textureHighX: Float, textureLowY: Float, textureHighY: Float) {
        basePtr = (row * gridCols + col) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS
        val current = textures[textureIndex]

        // triangle 1
        current.coords!![basePtr + OFFSET_X] = textureLowX
        current.coords!![basePtr + OFFSET_Y] = textureLowY
        basePtr += TEXTURE_DIMENSIONS
        current.coords!![basePtr + OFFSET_X] = textureLowX
        current.coords!![basePtr + OFFSET_Y] = textureHighY
        basePtr += TEXTURE_DIMENSIONS
        current.coords!![basePtr + OFFSET_X] = textureHighX
        current.coords!![basePtr + OFFSET_Y] = textureHighY
        basePtr += TEXTURE_DIMENSIONS
        current.coords!![basePtr + OFFSET_X] = textureHighX
        current.coords!![basePtr + OFFSET_Y] = textureLowY
    }

    /**
     * Imposta le coordinate in base ad un'animazione
     *
     */
    fun setTextureCoords(textureIndex: Int, row: Int, col: Int, animator: TextureTimeline) {
        setTextureCoords(textureIndex, row, col, animator.handler!!.value()!!.textureRegion)
    }

    fun setTextureCoords(textureIndex: Int, row: Int, col: Int, region: TextureRegion?) {
        basePtr = (row * gridCols + col) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS
        val currentTexturesCoords = textures[textureIndex].coords

        // triangle 1
        currentTexturesCoords!![basePtr + OFFSET_X] = region!!.lowX
        currentTexturesCoords[basePtr + OFFSET_Y] = region.lowY
        basePtr += TEXTURE_DIMENSIONS
        currentTexturesCoords[basePtr + OFFSET_X] = region.lowX
        currentTexturesCoords[basePtr + OFFSET_Y] = region.highY
        basePtr += TEXTURE_DIMENSIONS
        currentTexturesCoords[basePtr + OFFSET_X] = region.highX
        currentTexturesCoords[basePtr + OFFSET_Y] = region.highY
        basePtr += TEXTURE_DIMENSIONS
        currentTexturesCoords[basePtr + OFFSET_X] = region.highX
        currentTexturesCoords[basePtr + OFFSET_Y] = region.lowY
    }

    fun setTextureCoords(row: Int, col: Int, tile: Tile) {
        setTextureCoords(TEXTURE_DEFAULT_INDEX, row, col, tile)
    }

    fun setTextureCoords(row: Int, col: Int, cell: GridCell) {
        setTextureCoords(TEXTURE_DEFAULT_INDEX, row, col, cell)
    }

    fun setTextureCoords(textureIndex: Int, row: Int, col: Int, cell: GridCell) {
        basePtr = (row * gridCols + col) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS
        val currentTexturesCoords = textures[textureIndex].coords

        // triangle 1
        currentTexturesCoords!![basePtr + OFFSET_X] = cell.textureLowX
        currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureLowY
        basePtr += TEXTURE_DIMENSIONS
        currentTexturesCoords[basePtr + OFFSET_X] = cell.textureLowX
        currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureHighY
        basePtr += TEXTURE_DIMENSIONS
        currentTexturesCoords[basePtr + OFFSET_X] = cell.textureHighX
        currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureHighY
        basePtr += TEXTURE_DIMENSIONS
        currentTexturesCoords[basePtr + OFFSET_X] = cell.textureHighX
        currentTexturesCoords[basePtr + OFFSET_Y] = cell.textureLowY
    }

    /**
     *
     *
     * Imposta le coordinate texture della tile attualmente sotto il cursore.
     *
     *
     * <pre>
     *
     * 2--- 3       4
     * |   /       /|
     * |  /       / |
     * | /       /  |
     * |/       /   |
     * 1       6----5
     *
    </pre> *
     *
     */
    fun setTextureCoordsOnCursor(tile: Tile) {
        setTextureCoordsOnCursor(TEXTURE_DEFAULT_INDEX, tile)
    }

    /**
     *
     *
     * Imposta le coordinate texture della tile attualmente sotto il cursore.
     *
     *
     * <pre>
     *
     * 2--- 3       4
     * |   /       /|
     * |  /       / |
     * | /       /  |
     * |/       /   |
     * 1       6----5
     *
    </pre> *
     *
     *
     *
     * Il controllo validità del tile si presume essere stato fatto prima di invocare questo metodo.
     *
     *
     */
    fun setTextureCoordsOnCursor(textureIndex: Int, tile: Tile) {
        // cursor rappresenta il numero di vertici. Per ogni vertice ci sono 2
        // coordinate per le texture
        basePtr = cursor * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS
        val current = textures[textureIndex]
        if (!tile.diagonalFlip) {
            current.coords!![basePtr + OFFSET_X] = tile.lowX
            current.coords!![basePtr + OFFSET_Y] = tile.lowY
        } else {
            current.coords!![basePtr + OFFSET_X] = tile.highX
            current.coords!![basePtr + OFFSET_Y] = tile.highY
        }
        basePtr += TEXTURE_DIMENSIONS
        current.coords!![basePtr + OFFSET_X] = tile.lowX
        current.coords!![basePtr + OFFSET_Y] = tile.highY
        if (!tile.diagonalFlip) {
            basePtr += TEXTURE_DIMENSIONS
            current.coords!![basePtr + OFFSET_X] = tile.highX
            current.coords!![basePtr + OFFSET_Y] = tile.highY
        } else {
            basePtr += TEXTURE_DIMENSIONS
            current.coords!![basePtr + OFFSET_X] = tile.lowX
            current.coords!![basePtr + OFFSET_Y] = tile.lowY
        }
        basePtr += TEXTURE_DIMENSIONS
        current.coords!![basePtr + OFFSET_X] = tile.highX
        current.coords!![basePtr + OFFSET_Y] = tile.lowY
    }

    /**
     * <pre>
     *
     * 2--- 3       4
     * |   /       /|
     * |  /       / |
     * | /       /  |
     * |/       /   |
     * 1       6----5
     *
    </pre> *
     *
     * @param textureIndex
     * @param row
     * @param col
     * @param tile
     */
    fun setTextureCoords(textureIndex: Int, row: Int, col: Int, tile: Tile) {
        basePtr = (row * gridCols + col) * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS
        val coords = textures[textureIndex].coords
        if (!Tile.isEmpty(tile)) {

            // triangle 1
            if (!tile.diagonalFlip) {
                coords!![basePtr + OFFSET_X] = tile.lowX
                coords[basePtr + OFFSET_Y] = tile.lowY
            } else {
                coords!![basePtr + OFFSET_X] = tile.highX
                coords[basePtr + OFFSET_Y] = tile.highY
            }
            basePtr += TEXTURE_DIMENSIONS
            coords[basePtr + OFFSET_X] = tile.lowX
            coords[basePtr + OFFSET_Y] = tile.highY
            if (!tile.diagonalFlip) {
                basePtr += TEXTURE_DIMENSIONS
                coords[basePtr + OFFSET_X] = tile.highX
                coords[basePtr + OFFSET_Y] = tile.highY
            } else {
                basePtr += TEXTURE_DIMENSIONS
                coords[basePtr + OFFSET_X] = tile.lowX
                coords[basePtr + OFFSET_Y] = tile.lowY
            }
            basePtr += TEXTURE_DIMENSIONS
            coords[basePtr + OFFSET_X] = tile.highX
            coords[basePtr + OFFSET_Y] = tile.lowY
        } else {
            // triangle 1
            coords!![basePtr + OFFSET_X] = 0f
            coords[basePtr + OFFSET_Y] = 0f
            basePtr += TEXTURE_DIMENSIONS
            coords[basePtr + OFFSET_X] = 0f
            coords[basePtr + OFFSET_Y] = 0f
            basePtr += TEXTURE_DIMENSIONS
            coords[basePtr + OFFSET_X] = 0f
            coords[basePtr + OFFSET_Y] = 0f

            // triangle 2
            basePtr += TEXTURE_DIMENSIONS
            coords[basePtr + OFFSET_X] = 0f
            coords[basePtr + OFFSET_Y] = 0f
        }
    }

    /**
     *
     *
     * Effettua l'aggiornamento di tutti i buffer che non sono di tipo STATIC, limitandosi però ai vertici definiti mediante il cursore
     *
     */
    fun updateBuffersOnCursor() {
        val size = cursor
        // gli indici non devono essere aggiornati.

        // vertici
        // vertices.put(verticesCoords, 0, size * VERTICES_DIMENSIONS).position(0);
        if (vertices!!.allocation !== BufferAllocationType.STATIC) {
            vertices!!.update(size * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * VERTICES_DIMENSIONS)
        }
        for (i in textures.indices) {
            if (textures[i].allocation !== BufferAllocationType.STATIC) {
                textures[i].update(size * QuadMesh.Companion.VERTEX_IN_INDEXED_QUAD * TEXTURE_DIMENSIONS)
                // textures[i].put(texturesCoords[i], 0, size * TEXTURE_DIMENSIONS).position(0);
            }
        }
        if (indexes!!.allocation !== BufferAllocationType.STATIC) {
            indexes!!.update(size * IndexBuffer.INDEX_IN_QUAD_TILE)
        }
    }

    companion object {
        private const val serialVersionUID = 673553443788789280L
        const val TEXTURE_DEFAULT_INDEX = 0
    }
}