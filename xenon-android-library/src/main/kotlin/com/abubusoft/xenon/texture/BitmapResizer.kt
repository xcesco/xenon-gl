/**
 * 
 */
package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.SizeI2;
import com.abubusoft.kripton.android.Logger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

import static com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap;

/**
 * wrapped
 * @author Francesco Benincasa
 * 
 */
public abstract class BitmapResizer {

	public static int backgroundColor = Color.parseColor("#00000000");

	/**
	 * Effettua il resize delle immagini. wrapped
	 * @param source
	 * @param size
	 * @param aspectXY
	 * 			rapporto tra x e y (width / height)
	 * @param effectiveSize
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap source, TextureSizeType size, double aspectXY, SizeI2 effectiveSize) {
		float Ri = ((float) source.getWidth()) / source.getHeight();
		Bitmap temp;
		Bitmap resizedBitmap;

		if (XenonMath.isLess(Ri,(float) aspectXY)) {
			// dobbiamo far aumentare il rapporto tra w / h --> DEVO aumentare w
			// (non posso aumentare h):
			// DI QUANTO? IN PERCENTUALE RI/RA
			temp = cropBitmapInHeight(source, (float) (Ri / aspectXY));
		} else if (XenonMath.isGreater(Ri, (float) aspectXY)) {
			temp = cropBitmapInWidth(source, (float) (aspectXY / Ri));
		} else {
			temp = source;
		}

		resizedBitmap = resizeBitmapHeight(temp, size, aspectXY, effectiveSize);				

		// TODO recycle
		if ((source != temp) && (temp != source) && (!temp.isRecycled()))
			temp.recycle();
		temp = null;

		return resizedBitmap;
	}

	/**
	 * Riduce in larghezza (CROPPANDO IN CENTRO) un'immagine. Data la
	 * percentuale espressa in 0 - 1.
	 * 
	 * wrapped
	 * 
	 * @param bmpSrc
	 * @param incPercentage
	 * @return
	 */
	static Bitmap cropBitmapInWidth(Bitmap bmpSrc, float incPercentage) {
		int offset = (int) (Math.ceil((1.0f - incPercentage) * bmpSrc.getWidth() / 2.0f));

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap desctBmp = wrapBitmap(Bitmap.createBitmap(bmpSrc.getWidth() - offset * 2, bmpSrc.getHeight(), conf));

		// Logger.error("ADATTO IMMAGINE : inc %s -- w,h = (%s, %s",incPercentage,desctBmp.getWidth(),desctBmp.getHeight());

		Rect src = new Rect(0 + offset, 0, bmpSrc.getWidth() - offset, bmpSrc.getHeight());
		Rect dest = new Rect(0, 0, desctBmp.getWidth(), desctBmp.getHeight());

		Canvas wideBmpCanvas;
		wideBmpCanvas = new Canvas(desctBmp);

		wideBmpCanvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
		wideBmpCanvas.drawBitmap(bmpSrc, src, dest, null);

		return desctBmp;
	}

	/**
	 * Riduce in altezza (CROPPANDO IN CENTRO) un'immagine. Data la percentuale
	 * espressa in 0 - 1.
	 * 
	 * wrapped
	 * 
	 * @param bmpSrc
	 * @param incPercentage
	 * @return
	 */
	static Bitmap cropBitmapInHeight(Bitmap bmpSrc, float incPercentage) {
		int offset = (int) (Math.ceil((1.0f - incPercentage) * bmpSrc.getHeight() / 2.0f));

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap desctBmp = wrapBitmap(Bitmap.createBitmap(bmpSrc.getWidth(), bmpSrc.getHeight() - offset * 2, conf));

		// Logger.error("ADATTO IMMAGINE : inc %s -- w,h = (%s, %s",incPercentage,desctBmp.getWidth(),desctBmp.getHeight());

		Rect src = new Rect(0, 0 + offset, bmpSrc.getWidth(), bmpSrc.getHeight() - offset);
		Rect dest = new Rect(0, 0, desctBmp.getWidth(), desctBmp.getHeight());

		Canvas wideBmpCanvas;
		wideBmpCanvas = new Canvas(desctBmp);

		wideBmpCanvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
		wideBmpCanvas.drawBitmap(bmpSrc, src, dest, null);

		return desctBmp;
	}

	/**
	 * Prende la bitmap, e la adatta facendo in modo che H siano uguali.
	 * 
	 * wrapped
	 * 
	 * @param bmpSrc
	 * 			bitmap in ingresso
	 * @param textureSize
	 * 			dimensioni della texture
	 * @param aspectRatio
	 * 			rapporto tra width e height della bitmap.
	 * @param effectiveSize
	 * 			se impostato, alla fine del metodo contiene le dimensioni effettivamente utilizzate della bitmap
	 * @return
	 * 			nuova bitmap
	 */
	static Bitmap resizeBitmapHeight(Bitmap bmpSrc, TextureSizeType textureSize, double aspectRatio, SizeI2 effectiveSize) {
		int initialW = bmpSrc.getWidth();
		int initialH = bmpSrc.getHeight();

		double desideredHeight = textureSize.height / aspectRatio;

		float scaleHeight = ((float) desideredHeight / initialH);

		Matrix matrix = new Matrix();
		matrix.postScale(scaleHeight, scaleHeight);

		Bitmap resizedBitmap = wrapBitmap(Bitmap.createBitmap(bmpSrc, 0, 0, initialW, initialH, matrix, false));

		float Iw = resizedBitmap.getWidth();
		float Ih = resizedBitmap.getHeight();

		Logger.error("Resized bitmap " + resizedBitmap.getWidth() + " startX " + resizedBitmap.getHeight());

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap desctBmp = wrapBitmap(Bitmap.createBitmap(textureSize.width, textureSize.height, conf));

		Logger.error("desctBmp bitmap " + desctBmp.getWidth() + " startX " + desctBmp.getHeight());

		int offsetX = 0;
		int offsetY = 0;

		if (XenonMath.isGreater((Iw / Ih),(float) aspectRatio)) {
			// abbiamo più w che h
			offsetX = (int) ((Iw - textureSize.width) / 2.0);
		} else {
			offsetY = (int) ((Ih - textureSize.height) / 2.0);
		}

		Rect src, dest;

		// Logger.error("Offset " + screenOffsetX + "," + screenOffsetY);
		offsetX = offsetX > 0 ? offsetX : 0;
		offsetY = offsetY > 0 ? offsetY : 0;
		// Logger.error("Offset Fixed " + screenOffsetX + "," + screenOffsetY);
		src = new Rect(0 + offsetX, 0 + offsetY, resizedBitmap.getWidth() - offsetX, resizedBitmap.getHeight() - offsetY);
		// Logger.error("rect " + resizedBitmap.getWidth() + " startX " +
		// resizedBitmap.getHeight()); 

		//TODO da verificare: deve essere spostato in basso
		// consideriamo sempre come sia un quadrato
		dest = new Rect(0, 0, textureSize.width, (int) (textureSize.width / aspectRatio));

		// impostiamo in effetti quando è grande la bitmap nella texture
		if (effectiveSize != null) {
			// memorizziamo fino a dove arriva la bitmap reale
			effectiveSize.width = resizedBitmap.getWidth();
			effectiveSize.height = resizedBitmap.getHeight();
		}

		Canvas wideBmpCanvas;
		wideBmpCanvas = new Canvas(desctBmp);

		wideBmpCanvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
		wideBmpCanvas.drawBitmap(resizedBitmap, src, dest, null);

		// TODO recycle bitmap
		// svuotiamo bitmap
		if (resizedBitmap != bmpSrc && resizedBitmap.isRecycled())
			resizedBitmap.recycle();
		resizedBitmap = null;

		return desctBmp;
	}

}
