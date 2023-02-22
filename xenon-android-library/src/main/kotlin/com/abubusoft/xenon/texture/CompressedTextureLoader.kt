package com.abubusoft.xenon.texture

import android.content.Context
import android.graphics.Bitmap
import android.opengl.ETC1
import android.opengl.ETC1Util.ETC1Texture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10

/**
 * @author Francesco Benincasa
 */
object CompressedTextureLoader {
    /**
     * @param context
     * @param gl
     * @param bitmap
     */
    fun load(context: Context?, gl: GL10, bitmap: Bitmap) {
        val size = bitmap.rowBytes * bitmap.height
        var bb = ByteBuffer.allocateDirect(size) // size is good
        bb.order(ByteOrder.nativeOrder())
        bitmap.copyPixelsToBuffer(bb)
        bb.position(0)


        //ETC1Util.
        var etc1tex: ETC1Texture?
        // RGB_565 is 2 bytes per pixel
        // ETC1Texture etc1tex = ETC1Util.compressTexture(bb, m_TexWidth,
        // m_TexHeight, 2, 2*m_TexWidth);
        val encodedImageSize = ETC1.getEncodedDataSize(bitmap.width, bitmap.height)
        var compressedImage = ByteBuffer.allocateDirect(encodedImageSize).order(ByteOrder.nativeOrder())
        // RGB_565 is 2 bytes per pixel
        ETC1.encodeImage(bb, bitmap.width, bitmap.height, 3, 3 * bitmap.width, compressedImage)
        etc1tex = ETC1Texture(bitmap.width, bitmap.height, compressedImage)

        // ETC1Util.loadTexture(GL10.GL_TEXTURE_2D, 0, 0, GL10.GL_RGB,
        // GL10.GL_UNSIGNED_SHORT_5_6_5, etc1tex);
        gl.glCompressedTexImage2D(
            GL10.GL_TEXTURE_2D, 0, ETC1.ETC1_RGB8_OES, bitmap.width, bitmap.height, 0, etc1tex.data.capacity(),
            etc1tex.data
        )
        bb = null
        compressedImage = null
        etc1tex = null
    }
}