/**
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import android.util.Base64
import android.util.Base64InputStream
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.mesh.tiledmaps.*
import org.xml.sax.Attributes
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

/**
 *
 *
 * Helper per caricare una layer da un file tmx.
 *
 *
 * @author Francesco Benincasa
 */
object TMXLayerHelper {
    /**
     *
     *
     * Legge il gid sottoforma di long (comprende i flag di flip)
     *
     *
     * @param atts
     */
    fun addTile(layer: TiledLayer, atts: Attributes) {
        addTile(layer, SAXUtil.getLong(atts, "gid"))
    }

    /**
     *
     *
     * Legge il gid, con i flag di flipping ancora inseriti. Per questo motivo al posto di essere un int è messo come long. In read rawGID verrà trasformato.
     *
     *
     * @param dataIn
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readRawGID(dataIn: DataInputStream): Long {
        // devo necessarimente usare long e non int, altrimenti le operazioni di
        // shift mi
        // danno valori negativi!
        val lowestByte = dataIn.read().toLong()
        val secondLowestByte = dataIn.read().toLong()
        val secondHighestByte = dataIn.read().toLong()
        val highestByte = dataIn.read().toLong()
        require((lowestByte < 0 || secondLowestByte < 0 || secondHighestByte < 0 || highestByte) >= 0) { "Couldn't read gid from stream." }
        return lowestByte or (secondLowestByte shl 8) or (secondHighestByte shl 16) or (highestByte shl 24)
    }

    /**
     *
     *
     * Estrae dalla stringa data in input la definizione delle varie tile.
     *
     *
     * @param data
     * @param encoding
     * @param compression
     * @throws IOException
     */
    @Throws(IOException::class)
    fun extract(layer: TiledLayer, data: String, encoding: String?, compression: String?) {
        var dataIn: DataInputStream? = null
        try {
            var `in`: InputStream = ByteArrayInputStream(data.toByteArray(charset("UTF-8")))
            if (encoding != null && encoding == "base64") {
                `in` = Base64InputStream(`in`, Base64.DEFAULT)
            }
            if (compression != null) {
                `in` = if (compression == "gzip") {
                    GZIPInputStream(`in`)
                } else if (compression == "zlib") {
                    InflaterInputStream(`in`)
                } else {
                    throw IllegalArgumentException("compression '$compression' is not supported.")
                }
            }
            dataIn = DataInputStream(`in`)
            val expectedTileCount = layer.tileColumns * layer.tileRows
            while (layer.tileCounter < expectedTileCount) {
                addTile(layer, readRawGID(dataIn))
            }
        } catch (e: Exception) {
            Logger.fatal(e.message)
        } finally {
            try {
                dataIn?.close()
            } catch (e: IOException) {
                Logger.error(e.toString())
            }
        }
    }

    /**
     *
     *
     * Provvede a creare una tile
     *
     *
     * @param gid
     */
    private fun addTile(layer: TiledLayer, rawGid: Long) {
        var rawGid = rawGid
        val column = layer.tileCounter % layer.tileColumns
        val row = layer.tileCounter / layer.tileColumns
        var currentTile: Tile? = null

        // inverte per startX
        val FLIPPED_HORIZONTALLY_FLAG: Long = -0x80000000

        // inverte per startY
        val FLIPPED_VERTICALLY_FLAG: Long = 0x40000000

        // inverte startX con startY
        val FLIPPED_DIAGONALLY_FLAG: Long = 0x20000000

        // Read out the flags
        val flipped_horizontally = rawGid and FLIPPED_HORIZONTALLY_FLAG > 0
        val flipped_vertically = rawGid and FLIPPED_VERTICALLY_FLAG > 0
        val flipped_diagonally = rawGid and FLIPPED_DIAGONALLY_FLAG > 0
        rawGid = rawGid and (FLIPPED_HORIZONTALLY_FLAG or FLIPPED_VERTICALLY_FLAG or FLIPPED_DIAGONALLY_FLAG).inv()
        val gid = rawGid.toInt()
        val currentTileIndex = row * layer.tileColumns + column
        if (gid == 0) {
            layer.tiles[currentTileIndex] = Tile.getEmptyTile(column, row)
        } else {
            val sprite = layer.tiledMap.getSpriteByGID(gid) ?: return

            // creiamo un tile e lo impostiamo adeguatamente
            currentTile =
                Tile(gid, column, row, sprite.tileColumnIndex, sprite.tileRowIndex, sprite.tileWidth.toInt(), sprite.tileHeight.toInt(), sprite.drawOffsetX, sprite.drawOffsetY)
            currentTile.horizontalFlip = flipped_horizontally
            currentTile.verticalFlip = flipped_vertically
            currentTile.diagonalFlip = flipped_diagonally
            layer.tiles[currentTileIndex] = currentTile

            // verifichiamo la dimensione massima dei tile presenti nel layer
            layer.tileWidthMax = Math.max(layer.tileWidthMax, sprite.tileWidth.toInt())
            layer.tileHeightMax = Math.max(layer.tileHeightMax, sprite.tileHeight.toInt())
        }

        // se il layer ha dei tiles con degli offset diversi da quello corrente, lo registriamo
        if (currentTileIndex == 0) {
            layer.drawOffsetUnique = true
            layer.drawOffsetX = layer.tiles[currentTileIndex].drawOffsetX.toFloat()
            layer.drawOffsetY = layer.tiles[currentTileIndex].drawOffsetY.toFloat()
        } else if (layer.drawOffsetUnique && (layer.tiles[currentTileIndex].drawOffsetX.toFloat() != layer.drawOffsetX || layer.tiles[currentTileIndex].drawOffsetY.toFloat() != layer.drawOffsetY)) {
            layer.drawOffsetUnique = false
        }
        layer.tileCounter++
    }

    /**
     *
     *
     * Rimuove tutti i layer considerati come preview (di imageLayer):
     *
     *
     *  * Quelli che contengono nel nome **preview**
     *  * Quelli che hanno una proprietà di nome preview = true
     *
     *
     *
     * Questo metodo rimuove tutti layer da rimuovere, ma i tileset ad essi associati rimangono. Questo perchè nel caso di ImageLayer presumo che i tileSet sono li stessi dei layer
     * fittizi usati per vedere come rendera.
     *
     *
     * @param tiledMap
     */
    fun removePreviewLayer(tiledMap: TiledMap?) {
        var finito = false
        var current: Layer? = null
        while (!finito) {
            finito = true
            // scorriamo i layer per vedere se ci sono degli elementi da
            // rimuovere
            for (i in tiledMap!!.layers.indices) {
                current = tiledMap.layers[i]
                if (current.isPreviewLayer) {
                    finito = false
                    tiledMap.layers.removeAt(i)
                    break
                }
            }
        }
    }
}