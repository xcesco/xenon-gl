package com.abubusoft.xenon.animations

import android.annotation.SuppressLint
import com.abubusoft.xenon.texture.*
import java.util.*

/**
 * Analizza la definizione di uno sprite
 *
 * @author Francesco Benincasa
 */
@SuppressLint("DefaultLocale")
internal object GDXParserHelper {
    private const val FIXED_POINT_FLOAT_MULTI = 100000.0f
    private const val FIXED_POINT_INT_MULTI = 100000
    private const val SPRITE_NAME_INDEX = 0
    private const val SPRITE_XY_INDEX = 2
    private const val SPRITE_SIZE_INDEX = 3

    /**
     *
     *
     * Data una texture e la definizione degli sprite, crea una mappa di tile, la cui chiave è data dal nome dello sprite.
     *
     *
     *
     *
     * Le tile definite devono avere un id > 0, altrimenti verranno considerate come tile nulle.
     *
     *
     * @param input
     * @param texture
     * @return
     */
    fun createTiles(input: String, texture: AtlasTexture): HashMap<String, TextureRegion> {
        val map = HashMap<String, TextureRegion>()
        var tile: TextureRegion? = null
        val definition = input.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var temp: Array<String>
        var name: String
        var xy1: Int
        var xy2: Int
        var size1: Int
        var size2: Int
        var tempX1: Float
        var tempX2: Float
        var tempY1: Float
        var tempY2: Float
        var atlasRow: Int
        var atlasColumn: Int
        var height: Int
        var width: Int
        var i = 4
        while (i < definition.size) {
            name = definition[i + SPRITE_NAME_INDEX].trim { it <= ' ' }
            temp = definition[i + SPRITE_XY_INDEX].replace("xy:", "").trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            xy1 = Integer.valueOf(temp[0].trim { it <= ' ' })
            xy2 = Integer.valueOf(temp[1].trim { it <= ' ' })
            temp = definition[i + SPRITE_SIZE_INDEX].replace("size:", "").trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            size1 = Integer.valueOf(temp[0].trim { it <= ' ' })
            size2 = Integer.valueOf(temp[1].trim { it <= ' ' })

            /*
			 * tile = new Tile(c++, -1, -1, (xy1 / size1), (xy2 / size2), size1, size2); // calcolo con fixed point tempY1 = ((tile.atlasRow * tile.height)) * FIXED_POINT_INT_MULTI
			 * / texture.info.dimension.height; tempY2 = tempY1 + ((tile.height) * FIXED_POINT_INT_MULTI / texture.info.dimension.height);
			 * 
			 * tempY1 += FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height); tempY2 -= FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height);
			 * 
			 * // startX tempX1 = (((xy1 / size1) * tile.width)) * FIXED_POINT_INT_MULTI / texture.info.dimension.width; tempX2 = tempX1 + ((tile.width) * FIXED_POINT_INT_MULTI /
			 * texture.info.dimension.width);
			 * 
			 * tempX1 += FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width); tempX2 -= FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width);
			 * 
			 * tile.textureLowY = (float) tempY1 / FIXED_POINT_FLOAT_MULTI; tile.textureHighY = (float) tempY2 / FIXED_POINT_FLOAT_MULTI;
			 * 
			 * tile.textureLowX = (float) tempX1 / FIXED_POINT_FLOAT_MULTI; tile.textureHighX = (float) tempX2 / FIXED_POINT_FLOAT_MULTI;
			 */

            // calcolo posizione
            atlasColumn = xy1 / size1
            atlasRow = xy2 / size2
            width = size1
            height = size2
            tile = TextureRegion()

            // calcolo con fixed point
            tempY1 = (atlasRow * height * FIXED_POINT_INT_MULTI / texture.info.dimension.height).toFloat()
            tempY2 = tempY1 + height * FIXED_POINT_INT_MULTI / texture.info.dimension.height
            tempY1 += (FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height)).toFloat()
            tempY2 -= (FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.height)).toFloat()

            // startX
            tempX1 = (xy1 / size1 * width * FIXED_POINT_INT_MULTI / texture.info.dimension.width).toFloat()
            tempX2 = tempX1 + width * FIXED_POINT_INT_MULTI / texture.info.dimension.width
            tempX1 += (FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width)).toFloat()
            tempX2 -= (FIXED_POINT_INT_MULTI / (2 * texture.info.dimension.width)).toFloat()
            tile.lowY = tempY1 / FIXED_POINT_FLOAT_MULTI
            tile.highY = tempY2 / FIXED_POINT_FLOAT_MULTI
            tile.lowX = tempX1 / FIXED_POINT_FLOAT_MULTI
            tile.highX = tempX2 / FIXED_POINT_FLOAT_MULTI
            map[name] = tile
            i += 7
        }
        return map
    }

    /**
     *
     *
     * Crea un'animazione a partire dalla definizione che si trova nella stringa passata come input. La tileMap contiene la definizione dei frame da utilizzare per l'animazione.
     *
     *
     * @param input
     * @param tilesMap
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun createAnimations(input: String, tilesMap: HashMap<String?, TextureRegion?>?, texture: Texture?): ArrayList<TextureAnimation?> {
        val list = ArrayList<TextureAnimation?>()
        var name: String? = null
        var definition: TextureAnimation? = null
        var frame: TextureKeyFrame? = null
        val inputDefinition = input.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var row: String
        var temp: Array<String>
        for (i in inputDefinition.indices) {
            row = inputDefinition[i].trim { it <= ' ' }
            if ("{" == row) {
                definition = TextureAnimation()
                definition.name = name
            } else if ("}" == row) {
                //map.put(name, definition);
                list.add(definition)
            } else if (row.startsWith("looping:")) {
                row = row.replace("looping:", "").trim { it <= ' ' }
                if ("true".equals(row, ignoreCase = true)) definition.loop = true
            } else if (row.startsWith("frame:")) {
                row = row.replace("frame:", "").trim { it <= ' ' }
                temp = row.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                frame = TextureKeyFrame()

                // inseriamo il nome del frame, il tile relativo e la durata
                frame.name = temp[0].trim { it <= ' ' }.lowercase(Locale.getDefault())
                frame.duration = temp[3].trim { it <= ' ' }.toLong()
                frame.texture = texture
                frame.textureRegion = tilesMap!![frame.name]
                definition!!.frames!!.add(frame)
            } else {
                name = inputDefinition[i].trim { it <= ' ' }.lowercase(Locale.getDefault())
            }
        }
        return list
    }
}