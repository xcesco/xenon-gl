/**
 * 
 */
package com.abubusoft.xenon.texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.core.graphic.BitmapManager;
import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.core.util.IOUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

/**
 * @author Francesco Benincasa
 * 
 */
public class RenderedTexture extends Texture {

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.texture.Texture#unbind()
	 */
	@Override
	protected void unbind() {		
		super.unbind();
		
		if (frameBuffers!=null)
		{
			Logger.debug("unbind framebuffer with bindingId %s for texture with bindingId %s", frameBuffers[0], this.bindingId );
			GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
			XenonGL.checkGlError("glDeleteFramebuffers");
			frameBuffers=null;
		}
		
		if (depthRenderBuffers!=null)
		{
			Logger.debug("unbind renderBuffer with bindingId %s for texture with bindingId %s", depthRenderBuffers[0], this.bindingId );
			GLES20.glDeleteRenderbuffers(1, depthRenderBuffers, 0);
			XenonGL.checkGlError("glDeleteRenderbuffers");
			depthRenderBuffers=null;
		}
	}

	protected int[] frameBuffers;
	protected int[] depthRenderBuffers;
	public final RenderedTextureOptions options;
	private int viewportX;
	private int viewportY;
	private int viewportWidth;
	private int viewportHeight;

	public RenderedTexture(Texture texture, RenderedTextureOptions options) {
		super(texture.name, texture.bindingId);
		this.updateInfo(texture.info);

		this.index = texture.index;
		this.options = options;

		setup();
	}

	private void setup() {
		frameBuffers = new int[1];
		depthRenderBuffers=null;
		// generate
		GLES20.glGenFramebuffers(1, frameBuffers, 0);
				
		Logger.debug("Generate Framebuffers bindingId %s for texture %s", frameBuffers[0], this.bindingId );

		if (options.depthBuffer) {
			depthRenderBuffers = new int[1];
			GLES20.glGenRenderbuffers(1, depthRenderBuffers, 0);
			Logger.debug("Generate Renderbuffers bindingId %s for texture %s", depthRenderBuffers[0], this.bindingId );

			// create render buffer and bind 16-bit depth buffer
			GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);
			GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, info.dimension.width, info.dimension.height);
		}
		
		setRenderFactor(options.renderFactor);		
	}

	public void setRenderFactor(float factor) {
		// render factor
		viewportX=(int) (info.dimension.width*((1f-factor)*.5f));
		viewportY=(int) (info.dimension.height*((1f-factor)*.5f));
		
		viewportWidth=(int)(info.dimension.width*factor);
		viewportHeight=(int)(info.dimension.height*factor);
	}
	
	public float getRenderFactor()
	{
		return options.renderFactor;
	}

	@Override
	protected void reload() {
		super.reload();			

		setup();
	}

	public boolean activate() {
		//XenonGL.clearGlError();
		//Logger.info("Framebuffer activated bindingId %s ", frameBuffers[0]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
		//GLES20.glViewport(0, 0, (int)(info.dimension.width * 0.5f), (int)(info.dimension.height * 0.5f));
		GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
		//GLES20.glViewport(0, 0, (int)(info.dimension.width), (int)(info.dimension.height));
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, bindingId, 0);
		// GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
		// GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);

		if (options.depthBuffer) {
			// attach render buffer as depth buffer
			GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);
		}
		// GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
		// GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);

		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			Logger.error("Framebuffer bindingId %s is not complete! Status %s ", frameBuffers[0],status);
			return false;
		}

		return true;
	}
	
	/**
	 * Serve a non utilizzare più questa texture come target per il draw, e lo fa tornare allo schermo.
	 */
	public void deactivate() {
		// return to screen
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, XenonGL.screenInfo.width, XenonGL.screenInfo.height);
	}

	/**
	 * <p>
	 * Attiva il framebuffer senza alterare il viewport, dato che si considera della stessa dimensione o comunque impostato altrove.
	 * </p>
	 * 
	 * @return
	 */
	public boolean activateWithSameViewport() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);

		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, bindingId, 0);
		// GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
		// GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);

		if (options.depthBuffer) {
			// attach render buffer as depth buffer
			GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);
			// GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
			// GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, depthRenderBuffers[0]);
		}

		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			Logger.warn("Framebuffer is not complete!");
			return false;
		}

		return true;
	}

	public void saveChanges(Context context, String fileName) {
		int size = info.dimension.width * info.dimension.height;
		ByteBuffer buf = ByteBuffer.allocateDirect(size * 4);
		buf.order(ByteOrder.nativeOrder());
		GLES20.glReadPixels(0, 0, info.dimension.width, info.dimension.height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buf);

		int data[] = new int[size];
		buf.asIntBuffer().get(data);
		buf = null;
		Bitmap createdBitmap = Bitmap.createBitmap(info.dimension.width, info.dimension.height, Bitmap.Config.ARGB_8888);
		createdBitmap.setPixels(data, 0, info.dimension.width, 0, 0, info.dimension.width, info.dimension.height);

		BitmapManager.instance().wrap(createdBitmap);

		int temp;
		int[] pixels = new int[info.dimension.width];
		for (int y = 0; y < info.dimension.height; y++) {
			createdBitmap.getPixels(pixels, 0, info.dimension.width, 0, y, info.dimension.height, 1);

			for (int x = 0; x < info.dimension.width; x++) {
				// Replace the alpha channel with the r value from the bitmap.
				temp = pixels[x];
				pixels[x] = (temp & 0xFFFFFF00) >> 8 | (temp & 0x000000FF) << 24;
			}
			createdBitmap.setPixels(pixels, 0, info.dimension.width, 0, y, info.dimension.width, 1);
		}
		IOUtility.saveTempPngFile(context, "-rt-"+fileName, createdBitmap);
	}

}
