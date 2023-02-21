package com.abubusoft.xenon.core.graphic.animation

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import java.util.*

/**
 * Analizza la definizione di uno sprite
 *
 * @author Francesco Benincasa
 */
object LoaderGDXHelper {
    private const val SPRITE_NAME_INDEX = 0
    private const val SPRITE_XY_INDEX = 2
    private const val SPRITE_SIZE_INDEX = 3

    /**
     *
     * Data una bitmap atlas ed un set di definizione di sprite, crea le sottobitmap
     * che vengono poi referenziate come sprite, mediante il nome
     *
     * @param input
     * @param source
     * @return
     */
    fun createTiles(input: String, source: Bitmap?): HashMap<String, Bitmap> {
        val map = HashMap<String, Bitmap>()
        var tile: Bitmap? = null
        val definition = input.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var temp: Array<String>
        var name: String
        var xy1: Int
        var xy2: Int
        var size1: Int
        var size2: Int
        var i = 4
        while (i < definition.size) {
            name = definition[i + SPRITE_NAME_INDEX].trim { it <= ' ' }
            temp = definition[i + SPRITE_XY_INDEX].replace("xy:", "").trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            xy1 = Integer.valueOf(temp[0].trim { it <= ' ' })
            xy2 = Integer.valueOf(temp[1].trim { it <= ' ' })
            temp = definition[i + SPRITE_SIZE_INDEX].replace("size:", "").trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            size1 = Integer.valueOf(temp[0].trim { it <= ' ' })
            size2 = Integer.valueOf(temp[1].trim { it <= ' ' })
            tile = Bitmap.createBitmap(source!!, xy1, xy2, size1, size2)
            map[name] = tile
            i += 7
        }
        return map
    }

    fun createAnimations(resources: Resources, input: String, tilesMap: HashMap<String, Bitmap>): HashMap<String, BitmapAnimation> {
        val map = HashMap<String, BitmapAnimation>()
        var name = "<undefined>"
        var animation = BitmapAnimation(name)
        val definition = input.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var row: String
        var temp: Array<String>
        for (i in definition.indices) {
            row = definition[i].trim { it <= ' ' }
            if ("{" == row) {
                animation = BitmapAnimation(name)
            } else if ("}" == row) {
                map[name] = animation
            } else if (row.startsWith("looping:")) {
                row = row.replace("looping:", "").trim { it <= ' ' }
                animation.frames.isOneShot = true
                if ("true".equals(row, ignoreCase = true)) animation.frames.isOneShot = false
            } else if (row.startsWith("frame:")) {
                row = row.replace("frame:", "").trim { it <= ' ' }
                temp = row.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                animation.frames.addFrame(BitmapDrawable(resources, tilesMap[temp[0].trim { it <= ' ' }.lowercase(Locale.getDefault())]), temp[3].trim { it <= ' ' }.toInt())
            } else {
                name = definition[i].trim { it <= ' ' }.lowercase(Locale.getDefault())
            }
        }
        return map
    }
}