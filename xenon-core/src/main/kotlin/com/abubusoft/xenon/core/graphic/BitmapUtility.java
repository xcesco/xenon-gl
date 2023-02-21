/**
 * 
 */
package com.abubusoft.xenon.core.graphic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import static com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap;

/**
 * @author Francesco Benincasa
 * 
 */
public class BitmapUtility {

	/**
	 * Prende l'immagine source e crea un'altra bitmap di dimensioni cropWitdh e cropHeight, partendo da cropLeft,cropTop dalla bitmap source.
	 * 
	 * @param source
	 * @param cropLeft
	 * @param cropTop
	 * @param cropWidth
	 * @param cropHeight
	 * @return
	 */
	public static Bitmap cropBitmap(Bitmap source, int cropLeft, int cropTop, int cropWidth, int cropHeight) {
		Bitmap destBitmap = Bitmap.createBitmap(cropWidth, cropHeight, Config.ARGB_8888);
		BitmapManager.instance().wrap(destBitmap);

		Canvas c = new Canvas(destBitmap);
		c.drawBitmap(source, cropLeft, cropTop, null);

		return destBitmap;
	}

	/**
	 * <p>
	 * Prende l'immagine rgbBitmap e vi applica la bitmap maskBitmap per filtrare i pixel. Se nella seconda maschera un pixel è nero, indica che il relativo pixel in rgbBitmap è
	 * trasparente. Se è bianco, allora il relativo pixel in rgbBitmap è valido.
	 * </p>
	 * 
	 * <table>
	 * <tr>
	 * <td></td>
	 * </tr>
	 * </table>
	 * <image src="doc-files/rgbImage.png"/> + <image src="doc-files/maskImage.png"/> = <image src="doc-files/result.png"/>
	 * 
	 * 
	 * @param rgbBitmap
	 * @param maskBitmap
	 * @return result
	 */
	public static Bitmap compositeWithMask(Bitmap rgbBitmap, Bitmap maskBitmap) {
		int width = rgbBitmap.getWidth();
		int height = rgbBitmap.getHeight();
		if (width != maskBitmap.getWidth() || height != maskBitmap.getHeight()) {
			throw new IllegalStateException("image size mismatch!");
		}

		Bitmap destBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		BitmapManager.instance().wrap(destBitmap);

		int[] pixels = new int[width];
		int[] alpha = new int[width];
		for (int y = 0; y < height; y++) {
			rgbBitmap.getPixels(pixels, 0, width, 0, y, width, 1);
			maskBitmap.getPixels(alpha, 0, width, 0, y, width, 1);

			for (int x = 0; x < width; x++) {
				// Replace the alpha channel with the r value from the bitmap.
				pixels[x] = (pixels[x] & 0x00FFFFFF) | ((alpha[x] << 8) & 0xFF000000);
			}
			destBitmap.setPixels(pixels, 0, width, 0, y, width, 1);
		}

		return destBitmap;
	}

	/**
	 * <p>
	 * Prende il colore color e vi applica la bitmap maskBitmap per filtrare i pixel. Se nella seconda maschera un pixel è nero, indica che il relativo pixel in rgbBitmap è
	 * trasparente. Se è bianco, allora il relativo pixel in rgbBitmap è valido.
	 * </p>
	 * 
	 * <image src="doc-files/rgbImage.png"/> + <image src="doc-files/maskImage.png"/> = <image src="doc-files/result.png"/>
	 * 
	 * <p>
	 * Rispetto al metodo con due bitmap, questo è molto più efficiente se vogliamo semplicemente applicare una maschera ad una bitmap basata su un colore.
	 * </p>
	 * 
	 * @param rgbBitmap
	 * @param maskBitmap
	 * @return result
	 */
	public static Bitmap compositeWithMask(int rgbColor, Bitmap maskBitmap, MaskType mask) {
		int width = maskBitmap.getWidth();
		int height = maskBitmap.getHeight();

		Bitmap destBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		BitmapManager.instance().wrap(destBitmap);

		int[] pixels = new int[width];
		int[] alpha = new int[width];

		if (mask.value > 0) {
			for (int y = 0; y < height; y++) {
				maskBitmap.getPixels(alpha, 0, width, 0, y, width, 1);

				for (int x = 0; x < width; x++) {
					// Replace the alpha channel with the r value from the bitmap.
					pixels[x] = (rgbColor & 0x00FFFFFF) | ((alpha[x] << mask.value) & 0xFF000000);
				}
				destBitmap.setPixels(pixels, 0, width, 0, y, width, 1);
			}
		} else {
			for (int y = 0; y < height; y++) {
				maskBitmap.getPixels(alpha, 0, width, 0, y, width, 1);

				for (int x = 0; x < width; x++) {
					// Replace the alpha channel with the r value from the bitmap.
					pixels[x] = (rgbColor & 0x00FFFFFF) | ((alpha[x] & 0xFF000000));
				}
				destBitmap.setPixels(pixels, 0, width, 0, y, width, 1);
			}

		}

		return destBitmap;
	}

	/**
	 * <p>
	 * Prende il colore color e moltiplica ogni pixel inputBitmap per il colore rgbColor. Normalmente la bitmap come secondo argomento è una bitmap in bianco e nero che in seguito
	 * viene
	 * </p>
	 * 
	 * <p>
	 * A differenza del metodo compositeWithMask, questo metodo non applica una maschera, semplicemente modifica i colori della maschera di input.
	 * </p>
	 * 
	 * <p>
	 * Per ogni componente, compreso l'alpha channel viene applicata la formula
	 * </p>
	 * 
	 * <pre>
	 * 		C  = C  * (1.0/256.0) * C
	 * 		 N	  1                  2
	 * </pre>
	 * 
	 * <p>
	 * Dove C1 è il componente del colore rgbColor e C2 è il componente del pixel inputBitmap.
	 * </p>
	 * 
	 * @param rgbBitmap
	 *            colore da applicare alla bitmap
	 * @param maskBitmap
	 *            bitmap in bianco e nero da elaborare
	 * @return result bitmap risultante
	 */
	public static Bitmap colorize(int rgbColor, Bitmap inputBitmap) {
		int width = inputBitmap.getWidth();
		int height = inputBitmap.getHeight();

		Bitmap destBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		BitmapManager.instance().wrap(destBitmap);

		int[] pixels = new int[width];
		int[] origin = new int[width];
		int[] currentPixel = new int[4];
		int[] rgbColorComponent = new int[4];
		ColorUtil.splitInComponent(rgbColor, rgbColorComponent);

		for (int y = 0; y < height; y++) {
			inputBitmap.getPixels(origin, 0, width, 0, y, width, 1);

			for (int x = 0; x < width; x++) {
				ColorUtil.splitInComponent(origin[x], currentPixel);
				// Replace the alpha channel with the r value from the bitmap.
				pixels[x] = Color.argb((int) (rgbColorComponent[0] * (currentPixel[0] / 255f)), (int) (rgbColorComponent[1] * (currentPixel[1] / 255f)), (int) (rgbColorComponent[2] * (currentPixel[2] / 255f)),
						(int) (rgbColorComponent[3] * (currentPixel[3] / 255f)));
			}
			destBitmap.setPixels(pixels, 0, width, 0, y, width, 1);
		}

		return destBitmap;
	}

	/**
	 * Recupera una bitmap a partire da una risorsa. Non viene fatto alcun scaling.
	 * 
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public static Bitmap loadImageFromResource(Context context, int resourceId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.ARGB_8888;

		Bitmap out = BitmapFactory.decodeResource(context.getResources(), resourceId, opt);

		return out;
	}

	/**
	 * Carica un'immagine da un asset
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap loadImageFromAssets(Context context, String fileName) {
		AssetManager mngr = context.getAssets();
		// Create an input stream to read from the asset folder
		InputStream is = null;
		try {
			is = mngr.open(fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Get the texture from the Android resource directory
		// InputStream is =
		// context.getResources().openRawResource(R.drawable.radiocd5);
		Bitmap bitmap = null;
		try {
			// BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			// Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		return bitmap;
	}

	/**
	 * Carica un'immagine da un file
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap loadImage(String fileName) {
		// Create an input stream to read from the asset folder
		InputStream is = null;
		try {
			is = new FileInputStream(fileName); // mngr.open(fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Get the texture from the Android resource directory
		// InputStream is =
		// context.getResources().openRawResource(R.drawable.radiocd5);
		Bitmap bitmap = null;
		try {
			// BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			// Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		return bitmap;
	}

	/**
	 * http://stackoverflow.com/questions/9021450/android-button-with-image-dim- image-event-button-disabled
	 * 
	 * @param bitmap
	 *            The source bitmap.
	 * @param opacity
	 *            from 0f to 1.0f a value between 0 (completely transparent) and 255 (completely opaque).
	 * @param bitmap
	 *            manager opzionale
	 * @return The opacity-adjusted bitmap. If the source bitmap is mutable it will be adjusted and returned, otherwise a new bitmap is created.
	 * 
	 * 
	 */
	public static Bitmap adjustOpacity(Bitmap bitmap, float opacityPercentage) {
		int opacity = (int) (255f * opacityPercentage);
		Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Config.ARGB_8888, true);
		wrapBitmap(mutableBitmap);

		Canvas canvas = new Canvas(mutableBitmap);
		int colour = (opacity & 0xFF) << 24;
		canvas.drawColor(colour, PorterDuff.Mode.DST_IN);

		return mutableBitmap;
	}

}
