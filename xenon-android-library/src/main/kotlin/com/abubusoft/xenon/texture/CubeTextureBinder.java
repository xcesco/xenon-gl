/**
 * 
 */
package com.abubusoft.xenon.texture;

import static com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap;

import com.abubusoft.xenon.math.SizeI2;
import com.abubusoft.xenon.opengl.AsyncOperationManager;
import com.abubusoft.xenon.opengl.AsyncOperationManager.AsyncTextureInfoLoader;
import com.abubusoft.xenon.texture.TextureInfo.TextureLoadType;
import com.abubusoft.xenon.texture.TextureInfo.TextureType;
import com.abubusoft.xenon.core.graphic.BitmapUtility;
import com.abubusoft.xenon.core.graphic.SampledBitmapFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * A differenza del TextureBinder, le immagini non vengono elaborate, vengono prese così come sono.
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class CubeTextureBinder {
	/**
	 * Carica una cube texture (6 immagini) dalle risorse id e la carica in opengl nella dimensione specificata. wrapped
	 * 
	 * @param textureIndex
	 * @param context
	 * @param resourceIdx
	 * @param textureSize
	 * 
	 * @return dimensioni reali della texture
	 */
	public static TextureInfo bindTextureFromResourceId(final CubeTexture texture, final Context context, final int textureBindingId, final int upperX, final int lowerX, final int upperY, final int lowerY, final int upperZ, final int lowerZ, final TextureOptions options, final TextureReplaceOptions loaderOptions) {
		if (!loaderOptions.asyncLoad) {
			// al textureIndex associamo una texture di tipo cube map
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureBindingId);

			TextureDimension dimension = null;

			int[] resourceIdxArray = { upperX, lowerX, upperY, lowerY, upperZ, lowerZ };
			int[] mapTexture = { GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
					GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };

			// carichiamo per tutti i lati un'immagine
			for (int i = 0; i < 6; i++) {
				Bitmap image;

				if (options.textureSize == TextureSizeType.SIZE_UNBOUND) {
					// non abbiamo posto alcun limite alla texture
					image = wrapBitmap(BitmapFactory.decodeResource(context.getResources(), resourceIdxArray[i]));
				} else {
					// carichiamo immagine facendo cmq un resize
					image = SampledBitmapFactory.decodeBitmap(context.getResources(), resourceIdxArray[i], options.textureSize.width, options.textureSize.height, null);
				}

				dimension = bindTextureFromBitmapInternal(textureBindingId, mapTexture[i], image, options);

				if (!image.isRecycled())
					image.recycle();
				image = null;

			}

			if (options.textureFilter.generateMipmap) {
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_CUBE_MAP);
			}

			GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, options.textureFilter.minifier);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, options.textureFilter.magnifier);

			GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, options.textureRepeat.value);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, options.textureRepeat.value);

			// impostiamo le info
			TextureInfo result = new TextureInfo(TextureLoadType.RESOURCE_TEXTURE, TextureType.TEXTURE2D_CUBIC);
			result.resourceContext = context;
			for (int i = 0; i < 6; i++) {
				result.setResourceId(i, resourceIdxArray[i]);
			}
			for (int i = 0; i < 6; i++) {
				result.setFileName(i, null);
			}

			result.options = options;
			result.dimension = dimension;

			return result;
		} else {
			return AsyncOperationManager.instance().load(texture, new AsyncTextureInfoLoader() {

				@Override
				public TextureInfo load(Texture texture) {
					// loaderOptions.asyncLoad(false) perchè il metodo deve
					// essere invocato in sync mode
					return bindTextureFromResourceId((CubeTexture)texture, context, textureBindingId, upperX, lowerX, upperY, lowerY, upperZ, lowerZ, options, loaderOptions.copy().asyncLoad(false));

				}
			}, loaderOptions.asyncLoaderListener);
		}
	}

	/**
	 * effettua il bind di una bitmap. wrapped
	 * 
	 * @param textureBindindId
	 *            idx della texture
	 * @param source
	 *            bitmap source
	 * @param options
	 *            opzioni startX la generazione della texture
	 * @return
	 */
	private static TextureDimension bindTextureFromBitmapInternal(int textureBindindId, int mapId, Bitmap source, TextureOptions options) {
		// prendiamo eventualmente le opzioni di default
		options = (options == null) ? TextureOptions.build() : options;

		Bitmap transformedSource;
		TextureDimension result;

		transformedSource = source;
		if (options.opacity != 1.0f) {
			// se variamo l'opacità allora dobbiamo mutare
			transformedSource = wrapBitmap(BitmapUtility.adjustOpacity(source, options.opacity));
		}

		// effettuiamo transform se esiste
		if (options.transformation != null) {
			Bitmap transformedSource1;
			transformedSource1 = wrapBitmap(options.transformation.transform(transformedSource));

			// azzeriamo la bitmap iniziale dopo averla trasformata
			if (transformedSource != transformedSource1 && !transformedSource.isRecycled()) {
				transformedSource.recycle();
			}

			transformedSource = transformedSource1;
		}

		if (options.textureSize == TextureSizeType.SIZE_UNBOUND) {
			result = bindSideTexture(mapId, transformedSource, options);
		} else {
			result = bindResizedSideTexture(mapId, transformedSource, options);
		}

		// se creiamo bitmap di intermezzo, allora ricicliamo
		if (transformedSource != source && !transformedSource.isRecycled()) {
			transformedSource.recycle();
		}

		return result;

	}

	/**
	 * Mappa un lato della texture
	 * 
	 * @param targetId
	 * @param source
	 * @param options
	 * @return
	 */
	private static TextureDimension bindSideTexture(int targetId, Bitmap source, TextureOptions options) {
		GLUtils.texImage2D(targetId, 0, source, 0);
		TextureDimension ret = new TextureDimension(source.getWidth(), source.getHeight(), 1.0f, 1.0f, TextureSizeType.SIZE_UNBOUND);

		return ret;
	}

	/**
	 * Effettua il bind facendo prima un resize dell'immagine.
	 * 
	 * @param textureIndex
	 * @param source
	 * @param size
	 * @param aspectRatio
	 * 
	 * @return dimensioni della texture
	 */
	private static TextureDimension bindResizedSideTexture(int mapId, Bitmap source, TextureOptions options) {
		SizeI2 effectiveSize = new SizeI2();

		// effettua il resize dell'immagine e mette in effectiveSize le
		// dimensioni dell'immagine
		Bitmap finalBitmap = BitmapResizer.resizeBitmap(source, options.textureSize, options.aspectRatio, effectiveSize);

		bindSideTexture(mapId, finalBitmap, options);

		TextureDimension ret = new TextureDimension(effectiveSize.width, effectiveSize.height, ((float) effectiveSize.width) / options.textureSize.width, ((float) effectiveSize.height / options.textureSize.height), options.textureSize);

		// una volta fatto il binding rilasciamo la risorsa
		if (!finalBitmap.isRecycled())
			finalBitmap.recycle();
		finalBitmap = null;

		return ret;
	}
}
