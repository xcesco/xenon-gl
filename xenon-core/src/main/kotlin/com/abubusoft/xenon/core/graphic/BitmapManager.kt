package com.abubusoft.xenon.core.graphic

import android.graphics.Bitmap
import java.lang.ref.SoftReference

/**
 * Gestore delle bitmap il cui unico scopo è poter cancellare tutte le bitmap
 * che per un motivo o per l'altro non sono state ancora pulite
 *
 * @author Francesco Benincasa
 */
object BitmapManager {
    /**
     * indica se il bitmap manager è abilitato
     */
    @JvmStatic
    var isEnabled = false

    private val bitmapList: MutableList<SoftReference<Bitmap>>
    private val stackList: MutableList<Array<StackTraceElement>>

    init {
        bitmapList = ArrayList()
        stackList = ArrayList()
    }

    /**
     * Effettua il wrap delle bitmap. Deve essere messo nello stesso metodo in
     * cui viene creata la bitmap.
     *
     * @param source
     * @return
     */
    @JvmStatic
    fun wrap(source: Bitmap): Bitmap {
        if (isEnabled) {
            bitmapList.add(SoftReference(source))
            stackList.add(Thread.currentThread().stackTrace)
        }
        return source
    }

    /**
     * pulisce tutte le bitmap ancora in piedi
     */
    @JvmStatic
    fun release() {
        var bitmap: Bitmap?
        var counter = 0
        val n = bitmapList.size
        for (i in 0 until n) {
            bitmap = bitmapList[i].get()
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
                counter++
            }
        }
        bitmapList.clear()
    }

    /**
     * @param source
     * @return
     */
    @JvmStatic
    fun wrapBitmap(source: Bitmap): Bitmap {
        return wrap(source)
    }
}