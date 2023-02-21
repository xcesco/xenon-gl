package com.abubusoft.xenon.core.graphic

import android.graphics.Bitmap
import java.lang.ref.SoftReference

/**
 * Gestore delle bitmap il cui unico scopo è poter cancellare tutte le bitmap
 * che per un motivo o per l'altro non sono state ancora pulice
 *
 * @author Francesco Benincasa
 */
class BitmapManager private constructor() {
    /**
     * indica se il bitmap manager è abilitato
     */
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
    fun wrap(source: Bitmap): Bitmap {
        if (instance.isEnabled) {
            bitmapList.add(SoftReference(source))
            stackList.add(Thread.currentThread().stackTrace)
        }
        return source
    }

    /**
     * pulisce tutte le bitmap ancora in piedi
     */
    fun release() {
        var bitmap: Bitmap?
        var stack: Array<StackTraceElement>
        var counter = 0
        val n = bitmapList.size
        for (i in 0 until n) {
            bitmap = bitmapList[i].get()
            stack = stackList[i]
            if (bitmap != null && !bitmap.isRecycled) {
                // Logger.debug("Bitmap cancello bitmap %s ", stack.toString());
                bitmap.recycle()
                counter++
            }
        }
        bitmapList.clear()
        if (counter > 0) {
            // Logger.debug("Bitmap cancellate %s ", counter);
        }
    }

    companion object {
        private val instance = BitmapManager()
        fun instance(): BitmapManager {
            return instance
        }

        /**
         * @param source
         * @return
         */
        fun wrapBitmap(source: Bitmap): Bitmap {
            return instance.wrap(source)
        }

        /**
         * rilasci le bitmap associate ad un determinato bitmapmanager
         *
         * @param bm
         */
        fun releaseBitmaps(bm: BitmapManager?) {
            bm?.release()
        }
    }
}