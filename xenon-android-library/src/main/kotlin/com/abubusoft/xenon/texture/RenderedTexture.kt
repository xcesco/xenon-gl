/**
 *
 */
package com.abubusoft.xenon.texture

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.graphic.BitmapManager
import com.abubusoft.xenon.core.util.IOUtility.saveTempPngFile
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10

/**
 * @author Francesco Benincasa
 */
class RenderedTexture(texture: Texture, options: RenderedTextureOptions) : Texture(texture.name, texture.bindingId) {
    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.texture.Texture#unbind()
	 */
    override fun unbind() {
        super.unbind()
        if (frameBuffers != null) {
            Logger.debug("unbind framebuffer with bindingId %s for texture with bindingId %s", frameBuffers!![0], bindingId)
            GLES20.glDeleteFramebuffers(1, frameBuffers, 0)
            checkGlError("glDeleteFramebuffers")
            frameBuffers = null
        }
        if (depthRenderBuffers != null) {
            Logger.debug("unbind renderBuffer with bindingId %s for texture with bindingId %s", depthRenderBuffers!![0], bindingId)
            GLES20.glDeleteRenderbuffers(1, depthRenderBuffers, 0)
            checkGlError("glDeleteRenderbuffers")
            depthRenderBuffers = null
        }
    }

    protected var frameBuffers: IntArray?
    protected var depthRenderBuffers: IntArray?
    val options: RenderedTextureOptions
    private var viewportX = 0
    private var viewportY = 0
    private var viewportWidth = 0
    private var viewportHeight = 0

    init {
        updateInfo(texture.info)
        index = texture.index
        this.options = options
        setup()
    }

    private fun setup() {
        frameBuffers = IntArray(1)
        depthRenderBuffers = null
        // generate
        GLES20.glGenFramebuffers(1, frameBuffers, 0)
        Logger.debug("Generate Framebuffers bindingId %s for texture %s", frameBuffers!![0], bindingId)
        if (options.depthBuffer) {
            depthRenderBuffers = IntArray(1)
            GLES20.glGenRenderbuffers(1, depthRenderBuffers, 0)
            Logger.debug("Generate Renderbuffers bindingId %s for texture %s", depthRenderBuffers!![0], bindingId)

            // create render buffer and bind 16-bit depth buffer
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBuffers!![0])
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, info!!.dimension!!.width, info!!.dimension!!.height)
        }
        renderFactor = options.renderFactor
    }

    // render factor
    var renderFactor: Float
        get() = options.renderFactor
        set(factor) {
            // render factor
            viewportX = (info!!.dimension!!.width * ((1f - factor) * .5f)).toInt()
            viewportY = (info!!.dimension!!.height * ((1f - factor) * .5f)).toInt()
            viewportWidth = (info!!.dimension!!.width * factor).toInt()
            viewportHeight = (info!!.dimension!!.height * factor).toInt()
        }

    override fun reload() {
        super.reload()
        setup()
    }

    fun activate(): Boolean {
        //XenonGL.clearGlError();
        //Logger.info("Framebuffer activated bindingId %s ", frameBuffers[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![0])
        //GLES20.glViewport(0, 0, (int)(info.dimension.width * 0.5f), (int)(info.dimension.height * 0.5f));
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight)
        //GLES20.glViewport(0, 0, (int)(info.dimension.width), (int)(info.dimension.height));
        // specify texture as color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, bindingId, 0)
        // GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
        // GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
        if (options.depthBuffer) {
            // attach render buffer as depth buffer
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderBuffers!![0])
        }
        // GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
        // GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);

        // check status
        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Logger.error("Framebuffer bindingId %s is not complete! Status %s ", frameBuffers!![0], status)
            return false
        }
        return true
    }

    /**
     * Serve a non utilizzare più questa texture come target per il draw, e lo fa tornare allo schermo.
     */
    fun deactivate() {
        // return to screen
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glViewport(0, 0, XenonGL.screenInfo.width, XenonGL.screenInfo.height)
    }

    /**
     *
     *
     * Attiva il framebuffer senza alterare il viewport, dato che si considera della stessa dimensione o comunque impostato altrove.
     *
     *
     * @return
     */
    fun activateWithSameViewport(): Boolean {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![0])

        // specify texture as color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, bindingId, 0)
        // GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
        // GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
        if (options.depthBuffer) {
            // attach render buffer as depth buffer
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderBuffers!![0])
            // GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
            // GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);
        }

        // check status
        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Logger.warn("Framebuffer is not complete!")
            return false
        }
        return true
    }

    fun saveChanges(context: Context?, fileName: String) {
        val size = info!!.dimension!!.width * info!!.dimension!!.height
        var buf = ByteBuffer.allocateDirect(size * 4)
        buf.order(ByteOrder.nativeOrder())
        GLES20.glReadPixels(0, 0, info!!.dimension!!.width, info!!.dimension!!.height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buf)
        val data = IntArray(size)
        buf.asIntBuffer()[data]
        buf = null
        val createdBitmap = Bitmap.createBitmap(info!!.dimension!!.width, info!!.dimension!!.height, Bitmap.Config.ARGB_8888)
        createdBitmap.setPixels(data, 0, info!!.dimension!!.width, 0, 0, info!!.dimension!!.width, info!!.dimension!!.height)
        BitmapManager.instance().wrap(createdBitmap)
        var temp: Int
        val pixels = IntArray(info!!.dimension!!.width)
        for (y in 0 until info!!.dimension!!.height) {
            createdBitmap.getPixels(pixels, 0, info!!.dimension!!.width, 0, y, info!!.dimension!!.height, 1)
            for (x in 0 until info!!.dimension!!.width) {
                // Replace the alpha channel with the r value from the bitmap.
                temp = pixels[x]
                pixels[x] = temp and -0x100 shr 8 or (temp and 0x000000FF shl 24)
            }
            createdBitmap.setPixels(pixels, 0, info!!.dimension!!.width, 0, y, info!!.dimension!!.width, 1)
        }
        saveTempPngFile(context!!, "-rt-$fileName", createdBitmap)
    }
}