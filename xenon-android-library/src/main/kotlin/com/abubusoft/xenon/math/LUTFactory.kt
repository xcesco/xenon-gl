package com.abubusoft.xenon.math;

import com.abubusoft.xenon.texture.TextureSizeType;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Genera una texture i cui valori sono in realtà una tabella di lookup bidimensionale
 * 
 * @author Francesco Benincasa
 * 
 */
public class LUTFactory {

	public static class ARGB {
		public double a;
		public double r;
		public double g;
		public double b;

		public void clear(float value) {
			a = value;
			r = value;
			g = value;
			b = value;
		}
	}

	public interface OnLUTCreateValueListener {
		/**
		 * Crea una componente, avendo come input le coordinate della texture, normalizzati, da 0 a 1.
		 * 
		 * @param startX
		 *            [0 .. 1]
		 * @param startY
		 *            [0 .. 1]
		 * @param output
		 *            ARGB[0 .. 1]
		 */
		void onCreate(ARGB argb, double x, double y);

	}

	/**
	 * dato un valore da 0 a 1, viene convertito da 0 a 255. viene utilizzato un check di range, quindi rientrerà sempre tra 0 a 255.
	 * 
	 * @param value
	 * @return
	 */
	private static int comp(double value) {
		value = Math.min(Math.max(value, 0f), 1f) * 255f;

		return (int) value;

	}

	public static Bitmap createSinLUT(TextureSizeType size) {
		return createLUT(size, new OnLUTCreateValueListener() {
			
			/* (non-Javadoc)
			 * @see com.abubusoft.xenon.math.LUTFactory.OnLUTCreateValueListener#onCreate(com.abubusoft.xenon.math.LUTFactory.ARGB, double, double)
			 */
			@Override
			public void onCreate(ARGB argb, double x, double y) {
				argb.r=Math.sin(XenonMath.PI_HALF * x);
			}
		});
	}

	/**
	 * <p>
	 * Crea una bitmap contenente una LUT (look up table). Ogni componente può ospitare dei valori da 0 .. 1</b>
	 * 
	 * @param size
	 * @param listener
	 * @return
	 */
	public static Bitmap createLUT(TextureSizeType size, OnLUTCreateValueListener listener) {
		Bitmap bitmap = Bitmap.createBitmap(size.width, size.height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		int color;

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);

		ARGB argb = new ARGB();

		for (int j = 0; j < size.height; j++) {
			for (int i = 0; i < size.width; i++) {
				argb.clear(0f);
				listener.onCreate(argb, ((double) i) / size.height, ((double) j) / size.width);

				// salva nei componenti rg [1..0], [0.. -1]
				color = Color.argb(comp(argb.a), comp(argb.r), comp(argb.g), comp(argb.b));
				paint.setColor(color);

				canvas.drawPoint(i, j, paint);
			}
		}

		return bitmap;
	}
}
