package com.abubusoft.xenon.texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.ETC1Util.ETC1Texture;

/**
 * @author Francesco Benincasa
 *
 */
public class CompressedTextureLoader {
	
	/**
	 * @param context
	 * @param gl
	 * @param bitmap
	 */
	public static void load(Context context, GL10 gl, Bitmap bitmap) {
		int size = bitmap.getRowBytes() * bitmap.getHeight();
		ByteBuffer bb = ByteBuffer.allocateDirect(size); // size is good
		bb.order(ByteOrder.nativeOrder());
		bitmap.copyPixelsToBuffer(bb);
		bb.position(0);

		
		//ETC1Util.
		ETC1Texture etc1tex;
		// RGB_565 is 2 bytes per pixel
		// ETC1Texture etc1tex = ETC1Util.compressTexture(bb, m_TexWidth,
		// m_TexHeight, 2, 2*m_TexWidth);

		final int encodedImageSize = ETC1.getEncodedDataSize(bitmap.getWidth(), bitmap.getHeight());
		ByteBuffer compressedImage = ByteBuffer.allocateDirect(encodedImageSize).order(ByteOrder.nativeOrder());
		// RGB_565 is 2 bytes per pixel
		ETC1.encodeImage(bb, bitmap.getWidth(), bitmap.getHeight(), 3, 3 * bitmap.getWidth(), compressedImage);
		etc1tex = new ETC1Texture(bitmap.getWidth(), bitmap.getHeight(), compressedImage);

		// ETC1Util.loadTexture(GL10.GL_TEXTURE_2D, 0, 0, GL10.GL_RGB,
		// GL10.GL_UNSIGNED_SHORT_5_6_5, etc1tex);
		gl.glCompressedTexImage2D(GL10.GL_TEXTURE_2D, 0, ETC1.ETC1_RGB8_OES, bitmap.getWidth(), bitmap.getHeight(), 0, etc1tex.getData().capacity(),
				etc1tex.getData());

		bb = null;
		compressedImage = null;
		etc1tex = null;

	}

}
