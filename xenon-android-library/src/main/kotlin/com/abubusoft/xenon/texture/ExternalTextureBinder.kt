/**
 * 
 */
package com.abubusoft.xenon.texture;

import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.opengl.XenonGLExtension;
import com.abubusoft.xenon.opengl.AsyncOperationManager;
import com.abubusoft.xenon.opengl.AsyncOperationManager.AsyncTextureInfoLoader;
import com.abubusoft.xenon.texture.TextureInfo.TextureLoadType;
import com.abubusoft.xenon.texture.TextureInfo.TextureType;
import com.abubusoft.kripton.android.Logger;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.view.Surface;

/**
 * A differenza del TextureBinder, le immagini non vengono elaborate, vengono prese così come sono.
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class ExternalTextureBinder {
	/**
	 * Carica una external texture. Ci sono diversi limiti sulla configurazione delle texture esterne.
	 * 
	 * @param textureIndex
	 * @param context
	 * @param resourceIdx
	 * @param textureSize
	 * 
	 * @return dimensioni reali della texture
	 */
	public static TextureInfo bindTexture(final ExternalTexture texture, final int textureBindingId, final TextureOptions options, final TextureReplaceOptions loaderOptions) {
		if (!loaderOptions.asyncLoad) {

			if (!XenonGLExtension.IMAGE_EXTERNAL.isPresent()) {
				String msg = "Unable to create external texture ! (No suitable opengl extensions founded!)";
				Logger.fatal(msg);
				throw (new RuntimeException(msg));
			}

			// al textureIndex associamo una texture di tipo cube map
			GLES20.glBindTexture(XenonGL.TEXTURE_EXTERNAL_OES, textureBindingId);
			XenonGL.checkGlError("glBindTexture TEXTURE_EXTERNAL_OES");

			// min filter is LINEAR or NEAREST
			GLES20.glTexParameterf(XenonGL.TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameterf(XenonGL.TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

			// CLAMP_TO_EDGE
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			// TODO gestire l'aspect ratio
			TextureDimension dimension = new TextureDimension(options.textureSize.width, options.textureSize.height, 1.0f, 1.0f, TextureSizeType.SIZE_UNBOUND);

			setup(texture, textureBindingId);
			
			// impostiamo le info
			// TODO per il momento facciamo finta che sia di tipo resource
			TextureInfo result = new TextureInfo(TextureLoadType.RESOURCE_TEXTURE, TextureType.TEXTURE_EXTERNAL);

			result.options = options;
			result.dimension = dimension;

			return result;
		} else {
			return AsyncOperationManager.instance().load(texture, new AsyncTextureInfoLoader() {

				@Override
				public TextureInfo load(Texture texture) {
					// loaderOptions.asyncLoad(false) perchè il metodo deve
					// essere invocato in sync mode
					return bindTexture((ExternalTexture) texture, textureBindingId, options, loaderOptions.copy().asyncLoad(false));

				}
			}, loaderOptions.asyncLoaderListener);
		}
	}

	private static void setup(final ExternalTexture texture, int textureBindingId) {
		// creiamo una surface associata alla texture
		texture.surface = new SurfaceTexture(textureBindingId);

		// controllo su dimensioni di default
		if (texture.options.textureSize.width > 0 && texture.options.textureSize.height > 0) {
			texture.surface.setDefaultBufferSize(texture.options.textureSize.width, texture.options.textureSize.height);
		} else {
			Logger.warn("No size specified for external texture %s (%s)", texture.name, texture.bindingId);
		}
				
		texture.surface.setOnFrameAvailableListener(texture.options.onFrameAvailableListener);	

		if (texture.options.mediaPlayer != null) {
			texture.options.mediaPlayer.setSurface(new Surface(texture.surface));
		} 

	}

}
