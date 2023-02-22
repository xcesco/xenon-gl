package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.core.Uncryptable;

import android.graphics.Bitmap;

/**
 * Serve a trasformare una bitmap prima di inserirla come una texture
 * @author Francesco Benincasa
 *
 */
public interface BitmapTransformation extends Uncryptable {

	/**
	 * <p>Elabora la bitmap prima di inserirla nella texture.</p>
	 * 
	 * @param source
	 * @return
	 */
	Bitmap transform(Bitmap source);
}
