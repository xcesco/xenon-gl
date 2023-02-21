package com.abubusoft.xenon.texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.SizeI2;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.opengl.XenonGLExtension;
import com.abubusoft.xenon.opengl.AsyncOperationManager;
import com.abubusoft.xenon.opengl.AsyncOperationManager.AsyncTextureInfoLoader;
import com.abubusoft.xenon.texture.TextureInfo.TextureLoadType;
import com.abubusoft.xenon.core.graphic.BitmapUtility;
import com.abubusoft.xenon.core.graphic.SampledBitmapFactory;
import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.core.util.IOUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import static com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap;

/**
 * Permette di convertire una bitmap in una texture. La bitmap viene recuperata da un file, da una risorsa o passata direttamente. Il sistema si preoccupa di effettuare delle
 * operazioni di preparazione prima di renderla.
 * <p>
 * Da file:
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Da risorsa:
 * <ul>
 * <li></li>
 * </ul>
 * <p>
 * Da bitmap:
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Francesco Benincasa
 */
public abstract class TextureBinder {

    /**
     * effettua il bind di una bitmap. wrapped
     *
     * @param textureBindingId idx della texture
     * @param source           bitmap source
     * @param options          opzioni startX la generazione della texture
     * @return
     */
    private static TextureDimension bindBitmapInternal(int textureBindingId, Context context, Bitmap source, TextureOptions options) {
        // prendiamo eventualmente le opzioni di default
        options = (options == null) ? TextureOptions.build() : options;

        // salva la texture iniziale su file
        if (options.debugTextureOnFile) {
            String fileName = IOUtility.saveTempPngFile(context, "debugTexture" + textureBindingId + "Initial", source);
            Logger.debug("Saved texture %s", fileName);
        }

        Bitmap transformedSource;
        TextureDimension result;

        transformedSource = source;
        if (!XenonMath.isEquals(options.opacity, 1.0f)) {
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
            result = bindTextureInternal(textureBindingId, context, transformedSource, options);
        } else {
            result = bindResizedBitmapInternal(textureBindingId, context, transformedSource, options);
        }

        // se creiamo bitmap di intermezzo, allora ricicliamo
        if (transformedSource != source && !transformedSource.isRecycled()) {
            transformedSource.recycle();
        }

        return result;

    }

    /**
     * Effettua il bind facendo prima un resize dell'immagine.
     *
     * @param textureId
     * @param source
     * @return dimensioni della texture
     */
    private static TextureDimension bindResizedBitmapInternal(int textureId, Context context, Bitmap source, TextureOptions options) {
        SizeI2 effectiveSize = new SizeI2();

        // effettua il resize dell'immagine e mette in effectiveSize le
        // dimensioni dell'immagine
        Bitmap finalBitmap = BitmapResizer.resizeBitmap(source, options.textureSize, options.aspectRatio, effectiveSize);

        bindTextureInternal(textureId, context, finalBitmap, options);

        TextureDimension ret = new TextureDimension(effectiveSize.width, effectiveSize.height, ((float) effectiveSize.width) / options.textureSize.width, ((float) effectiveSize.height / options.textureSize.height), options.textureSize);

        // una volta fatto il binding rilasciamo la risorsa
        if (!finalBitmap.isRecycled())
            finalBitmap.recycle();
        finalBitmap = null;

        return ret;
    }

    /**
     * <p>
     * Carica una bitmap da un file presente nell'assets.
     * </p>
     *
     * @param context
     * @return
     */
    public static TextureInfo bindTextureFromAssetsFile(Texture texture, final Context context, final String fileNameValue, final TextureOptions optionsValue, final TextureReplaceOptions loaderOptions) {
        if (!loaderOptions.asyncLoad) {
            Bitmap finalBitmap = BitmapUtility.loadImageFromAssets(context, fileNameValue);

            TextureDimension dimensionValue = bindBitmapInternal(texture.bindingId, context, finalBitmap, optionsValue);

            // una volta fatto il binding rilasciamo la risorsa
            if (!finalBitmap.isRecycled())
                finalBitmap.recycle();
            finalBitmap = null;

            // impostiamo le info
            TextureInfo result = new TextureInfo(TextureLoadType.ASSET_TEXTURE);
            result.resourceContext = context;
            result.setFileName(fileNameValue);
            result.options = optionsValue;
            result.dimension = dimensionValue;

            return result;
        } else {
            AsyncOperationManager.instance().load(texture, new AsyncTextureInfoLoader() {

                @Override
                public TextureInfo load(Texture texture) {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromAssetsFile(texture, context, fileNameValue, optionsValue, loaderOptions.copy().asyncLoad(false));

                }
            }, loaderOptions.asyncLoaderListener);

            return null;
        }
    }

    /**
     * <p>
     * Effettua il bind di una bitmap. wrapped.
     * <p>
     * <p>
     * <strong>Non può essere effettuato in modo ansincrono anche se impostato, dato che la bitmap è già presente.</strong>
     * </p>
     *
     * @param source        bitmap source
     * @param optionsValue  opzioni startX la generazione della texture
     * @return
     */
    public static TextureInfo bindTextureFromBitmap(Texture texture, final Context context, final Bitmap source, final TextureOptions optionsValue, final TextureReplaceOptions loaderOptions) {
        if (!loaderOptions.asyncLoad) {
            TextureDimension dimensionValue = bindBitmapInternal(texture.bindingId, context, source, optionsValue);

            // impostiamo le info
            TextureInfo result = new TextureInfo(TextureLoadType.BITMAP_TEXTURE);
            result.options = optionsValue;
            result.dimension = dimensionValue;

            if (TextureManager.instance().isTexturesReloadable()) {
                result.setFileName(IOUtility.saveTempPngFile(context, "texture_", source));
            }

            return result;
        } else {
            AsyncOperationManager.instance().load(texture, new AsyncTextureInfoLoader() {

                @Override
                public TextureInfo load(Texture texture) {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromBitmap(texture, context, source, optionsValue, loaderOptions.copy().asyncLoad(false));

                }
            }, loaderOptions.asyncLoaderListener);

            return null;
        }
    }

    /**
     * Ricarica un'immagine in una texture. wrapped
     *
     * @param url
     */
    public static TextureInfo bindTextureFromFile(final Texture texture, final Context context, final String url, final TextureOptions optionsValue, final TextureReplaceOptions loaderOptions) {
        if (!loaderOptions.asyncLoad) {

            // carica un'immagine le cui dimensioni siano più vicine possibili
            // alle
            // dimensioni della texture richiesta.
            Bitmap image;

            if (optionsValue.textureSize == TextureSizeType.SIZE_UNBOUND) {
                // non abbiamo posto alcun limite alla texture
                image = wrapBitmap(BitmapFactory.decodeFile(url));
            } else {
                // carichiamo immagine facendo cmq un resize
                image = SampledBitmapFactory.decodeBitmap(url, optionsValue.textureSize.width, optionsValue.textureSize.height, null);
            }

            TextureDimension dimensionValue = bindBitmapInternal(texture.bindingId, context, image, optionsValue);

            // impostiamo le info
            TextureInfo result = new TextureInfo(TextureLoadType.FILE_TEXTURE);
            result.resourceContext = null;
            result.setFileName(url);
            result.options = optionsValue;
            result.dimension = dimensionValue;

            if (!image.isRecycled())
                image.recycle();
            image = null;

            return result;
        } else {
            AsyncOperationManager.instance().load(texture, new AsyncTextureInfoLoader() {

                @Override
                public TextureInfo load(Texture texture) {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromFile(texture, context, url, optionsValue, loaderOptions.copy().asyncLoad(false));

                }
            }, loaderOptions.asyncLoaderListener);

            return null;
        }
    }

    /**
     * Carica un'immagine da una risorsa id e la carica in opengl nella dimensione specificata. wrapped
     *
     * @param context
     * @param resourceIdx
     * @return dimensioni reali della texture
     */
    public static TextureInfo bindTextureFromResourceId(Texture texture, final Context context, final int resourceIdx, final TextureOptions optionsValue, final TextureReplaceOptions loaderOptions) {
        if (!loaderOptions.asyncLoad) {
            // carica un'immagine le cui dimensioni siano più vicine possibili
            // alle
            // dimensioni della texture richiesta.
            Bitmap image;

            if (optionsValue.textureSize == TextureSizeType.SIZE_UNBOUND) {
                // non abbiamo posto alcun limite alla texture
                image = wrapBitmap(BitmapFactory.decodeResource(context.getResources(), resourceIdx));
            } else {
                // carichiamo immagine facendo cmq un resize
                image = SampledBitmapFactory.decodeBitmap(context.getResources(), resourceIdx, optionsValue.textureSize.width, optionsValue.textureSize.height, null);
            }

            TextureDimension dimensionValue = bindBitmapInternal(texture.bindingId, context, image, optionsValue);

            // impostiamo le info
            TextureInfo result = new TextureInfo(TextureLoadType.RESOURCE_TEXTURE);
            result.resourceContext = context;
            result.setResourceId(resourceIdx);
            result.setFileName(null);
            result.options = optionsValue;
            result.dimension = dimensionValue;

            if (!image.isRecycled())
                image.recycle();
            image = null;

            return result;
        } else {
            return AsyncOperationManager.instance().load(texture, new AsyncTextureInfoLoader() {

                @Override
                public TextureInfo load(Texture texture) {
                    // loaderOptions.asyncLoad(false) perchè il metodo deve
                    // essere invocato in sync mode
                    return bindTextureFromResourceId(texture, context, resourceIdx, optionsValue, loaderOptions.copy().asyncLoad(false));

                }
            }, loaderOptions.asyncLoaderListener);
        }
    }

    /**
     * Carica una risorsa mediante una stringa che contiene il suo id, da un contesto da definire. wrapped
     *
     * @param context
     * @param imageResourceId
     * @param options
     * @return
     */
    public static TextureInfo bindTextureFromResourceString(Texture texture, Context context, String imageResourceId, TextureOptions options, final TextureReplaceOptions loaderOptions) {
        int resourceId;
        String imageName;
        String resourceName;
        if (imageResourceId.lastIndexOf('.') > 0) {
            imageName = imageResourceId.substring(imageResourceId.lastIndexOf('/') + 1, imageResourceId.lastIndexOf('.'));
        } else {
            imageName = imageResourceId.substring(imageResourceId.lastIndexOf('/') + 1);
        }

        resourceName = "drawable/" + imageName;
        resourceId = context.getResources().getIdentifier(resourceName, null, context.getPackageName());

        // alternativamente proviamo a caricarlo da raw
        if (resourceId == 0) {
            resourceName = "raw/" + imageName;
            resourceId = context.getResources().getIdentifier(resourceName, null, context.getPackageName());
        }

        TextureInfo result = bindTextureFromResourceId(texture, context, resourceId, options, loaderOptions);

        return result;
    }

    /**
     * <p>
     * Effettua il binding opengl e restituisce le dimensioni della texture.
     * </p>
     * <p>
     * <p>
     * OpenGL has two parameters that can be set:
     * </p>
     * <p>
     * <p>
     * GL_TEXTURE_MIN_FILTER GL_TEXTURE_MAG_FILTER These correspond to the minification and magnification described earlier. GL_TEXTURE_MIN_FILTER accepts the following options:
     * </p>
     * <p>
     * <ul>
     * <li>GL_NEAREST GL_LINEAR</li>
     * <li>GL_NEAREST_MIPMAP_NEAREST</li>
     * <li>GL_NEAREST_MIPMAP_LINEAR</li>
     * <li>GL_LINEAR_MIPMAP_NEAREST</li>
     * <li>GL_LINEAR_MIPMAP_LINEAR</li>
     * <li>GL_TEXTURE_MAG_FILTER</li>
     * </ul>
     * <p>
     * <p>
     * Accepts the following options:
     * </p>
     * <p>
     * <ul>
     * <li>GL_NEAREST GL_LINEAR GL_NEAREST corresponds to nearest-neighbour rendering, GL_LINEAR corresponds to bilinear filtering,</li>
     * <li>GL_LINEAR_MIPMAP_NEAREST corresponds to bilinear filtering with mipmaps</li>
     * <li>GL_LINEAR_MIPMAP_LINEAR corresponds to trilinear filtering.</li>
     * </ul>
     * <p>
     * Vedi <a href= "http://www.learnopengles.com/android-lesson-six-an-introduction-to-texture-filtering/" >http://www.learnopengles.com/</a>
     *
     * @param textureId
     * @param source
     * @param options
     * @return dimensioni della texture ovvero quanto della texture può essere considerata come buona.
     */
    private static TextureDimension bindTextureInternal(int textureId, Context context, Bitmap source, TextureOptions options) {

        // salva la texture generata su file
        if (options.debugTextureOnFile) {
            String fileName = IOUtility.saveTempPngFile(context, "debugTexture" + textureId + "Final", source);

            XenonLogger.debug("Saved texture %s", fileName);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // impostiamo
        // GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, source, 0);
        if (options.textureInternalFormat == TextureInternalFormatType.FLOAT) {

            ByteBuffer buffer;
            if (XenonGLExtension.TEXTURE_FLOAT.isPresent()) {
                buffer = ByteBuffer.allocateDirect(source.getWidth() * source.getHeight() * 4 * 4).order(ByteOrder.nativeOrder());
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source.getWidth(), source.getHeight(), 0, GLES20.GL_RGBA, TextureInternalFormatType.FLOAT.value, buffer);
                XenonGL.checkGlError("glTexImage2D TEXTURE_FLOAT");
                Logger.debug("Create texture TEXTURE_FLOAT %s %s", source.getWidth(), source.getHeight());
            } else if (XenonGLExtension.TEXTURE_HALF_FLOAT.isPresent()) {
                // lavoriamo con gli half float
                buffer = ByteBuffer.allocateDirect(source.getWidth() * source.getHeight() * 4 * 2).order(ByteOrder.nativeOrder());
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source.getWidth(), source.getHeight(), 0, GLES20.GL_RGBA, XenonGL.GL_HALF_FLOAT_OES, buffer);
                XenonGL.checkGlError("glTexImage2D TEXTURE_HALF_FLOAT");
                Logger.debug("Create texture TEXTURE_HALF_FLOAT %s %s", source.getWidth(), source.getHeight());
            } else {
                String msg = "Unable to create texture of float or half_float! (No suitable opengl extensions founded!)";
                Logger.fatal(msg);
                throw (new RuntimeException(msg));
            }

            // GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source, TextureInternalFormatType.FLOAT.value, 0);

        } else {
            // GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source.getWidth(), source.getHeight(), 0, GLES20.GL_RGBA, TextureInternalFormatType.FLOAT.value, pixels)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, source, TextureInternalFormatType.UNSIGNED_BYTE.value, 0);
        }

        if (options.textureFilter.generateMipmap) {
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, options.textureFilter.minifier);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, options.textureFilter.magnifier);

        // come da http://www.khronos.org/opengles/sdk/docs/man/xhtml/glTexParameter.xml
        // il wrap può essere di tre tipi:
        // GL_CLAMP_TO_EDGE, GL_MIRRORED_REPEAT, or GL_REPEAT
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, options.textureRepeat.value);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, options.textureRepeat.value);
        TextureDimension ret = new TextureDimension(source.getWidth(), source.getHeight(), 1.0f, 1.0f, TextureSizeType.SIZE_UNBOUND);

        return ret;
    }
}
