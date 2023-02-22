package com.abubusoft.xenon.texture

import android.graphics.Bitmap
import com.abubusoft.xenon.core.Uncryptable

/**
 * Serve a trasformare una bitmap prima di inserirla come una texture
 * @author Francesco Benincasa
 */
interface BitmapTransformation {
    /**
     *
     * Elabora la bitmap prima di inserirla nella texture.
     *
     * @param source
     * @return
     */
    fun transform(source: Bitmap): Bitmap
}