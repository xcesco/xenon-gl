/**
 * 
 */
package com.abubusoft.xenon.texture;

import java.util.ArrayList;
import java.util.List;

import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.core.util.IOUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

/**
 * Gestore delle texture. Ha la possibilità di ricaricare in automatico le texture, se il flat viene opporturnamente valorizzato. Il contro effetto è che va a creare delle
 * immmagini di cache.
 * 
 * @author Francesco Benincasa
 * 
 */
public class TextureManager {

	/**
	 * Costruttore
	 */
	private TextureManager() {
		textures = new ArrayList<Texture>();
		reloadable = true;
	}

	/**
	 * Rigenera le texture.
	 */
	public void reloadTextures() {
		// puliamo i binder
		if (textures.size() > 0) {
			int n = textures.size();
			int[] textureIds = new int[n];

			// ricaviamo tutti i bindingId
			for (int i = 0; i < n; i++) {
				textureIds[i] = textures.get(i).bindingId;
				textures.get(i).unbind();
			}

			GLES20.glDeleteTextures(textureIds.length, textureIds, 0);
			GLES20.glFlush();

			// cancelliamo le vecchie texture
			Logger.debug("Unbinded " + n + " old textures, without deleting them ");

			Texture current;
			TextureInfo info;
			for (int i = 0; i < n; i++) {
				current = textures.get(i);
				current.bindingId = newTextureBindingId();
				current.reload();

				if (!current.ready) {
					Logger.warn("Texture %s index: %s not ready (async load)", current.name, current.index);
					continue;
				}

				info = current.info;

				switch (info.type) {				
				case TEXTURE2D:
					Texture currentTexture = (Texture) current;

					switch (info.load) {
					case ASSET_TEXTURE:
						TextureBinder.bindTextureFromAssetsFile(currentTexture, info.resourceContext, info.getFileName(), info.options, TextureReplaceOptions.build());
						break;
					case RESOURCE_TEXTURE:
						TextureBinder.bindTextureFromResourceId(currentTexture, info.resourceContext, info.getResourceId(), info.options, TextureReplaceOptions.build());
						break;
					case FILE_TEXTURE:
						TextureBinder.bindTextureFromFile(currentTexture, info.resourceContext, info.getFileName(), info.options, TextureReplaceOptions.build());
						break;
					case BITMAP_TEXTURE:
						// carichiamo dalla texture temporanea
						TextureBinder.bindTextureFromFile(currentTexture, info.resourceContext, info.getFileName(), info.options, TextureReplaceOptions.build());
						break;
					default:
						break;
					}

					break;
				case TEXTURE2D_CUBIC:
					CubeTexture currentCubeTexture = (CubeTexture) current;
					// TODO fare gli altri metodi

					switch (info.load) {
					/*
					 * case ASSET_TEXTURE: TextureBinder.bindTextureFromAssetsFile(current, info.resourceContext, info.getFileName(), info.options, TextureReplaceOptions.build());
					 * break;
					 */
					case RESOURCE_TEXTURE:
						CubeTextureBinder.bindTextureFromResourceId(currentCubeTexture, info.resourceContext, currentCubeTexture.bindingId, info.getResourceId(0), info.getResourceId(1), info.getResourceId(2), info.getResourceId(3),
								info.getResourceId(4), info.getResourceId(5), info.options, TextureReplaceOptions.build());
						break;
					case ASSET_TEXTURE:
					case BITMAP_TEXTURE:
					case FILE_TEXTURE:
						//TODO
						break;
					/*
					 * case FILE_TEXTURE: TextureBinder.bindTextureFromFile(current, info.resourceContext, info.getFileName(), info.options, TextureReplaceOptions.build()); break;
					 * case BITMAP_TEXTURE: // carichiamo dalla texture temporanea TextureBinder.bindTextureFromFile(current, info.resourceContext, info.getFileName(),
					 * info.options, TextureReplaceOptions.build()); break; default: break; } break;
					 */
					}

					Logger.debug("Rebind texture %s (%s)", i, info.load);
					break;
				case TEXTURE_EXTERNAL:
					ExternalTexture currentExternalTexture = (ExternalTexture) current;
					ExternalTextureBinder.bindTexture(currentExternalTexture, currentExternalTexture.bindingId, currentExternalTexture.info.options , TextureReplaceOptions.build());
					break;
				}

				// Importante: serve per le render texture
				current.reload();
			}
		}
	}

	/**
	 * Singleton
	 */
	private static final TextureManager instance = new TextureManager();

	/**
	 * Get instance
	 * 
	 * @return
	 */
	public static TextureManager instance() {
		return instance;
	}

	/**
	 * array delle texture
	 */
	private ArrayList<Texture> textures;

	/**
	 * indica se le texture devono essere caricate in automatico alla creazione dello screen.
	 */
	private boolean reloadable;

	/**
	 * Aggiunge una nuova texture al manager
	 * 
	 * @param newTexture
	 */
	private void appendToManagedTexture(Texture newTexture) {
		textures.add(newTexture);
		newTexture.index = textures.size() - 1;

		Logger.debug("Texture index %s, bindingId %s is created, Loaded %s", newTexture.index, newTexture.bindingId, newTexture.ready);
	}

	/**
	 * Se sono presenti delle texture, dealloca i suoi id. Viene invocato
	 * 
	 * @param gl
	 * @param context
	 * @param resource
	 */
	public int clearTextures() {
		Context context= XenonBeanContext.getBean(XenonBeanType.CONTEXT);
		// puliamo cmq tutte le immagini cachate
		int c = IOUtility.deleteTempFiles(context, "texture_");
		Logger.debug("Deleted " + c + " old textures cached on files");

		if (textures.size() > 0) {
			int n = textures.size();
			
			int[] textureIds = new int[n];
			
			XenonGL.clearGlError();

			// ricaviamo tutti i bindingId
			for (int i = 0; i < n; i++) {
				textureIds[i] = textures.get(i).bindingId;			
				textures.get(i).unbind();
			}

			
			GLES20.glDeleteTextures(textureIds.length, textureIds, 0);
			GLES20.glFlush();

			// cancelliamo le vecchie texture
			  
			  			
			textures.clear();
			Logger.debug("Unbinded " + n + " old textures ");
			return n;
		} else {
			// non dobbiamo cancellare alcuna texture
			Logger.debug("No texture to unbind");
			return 0;
		}
	}

	/**
	 * <p>
	 * Carica una texture da un file.
	 * </p>
	 * 
	 * @param url
	 *            path del file da caricare
	 * @param options
	 *            opzioni per caricare la texture
	 */
	public Texture createTextureFromFile(Context context, String url, TextureOptions options) {
		int bindingId = newTextureBindingId();
		Texture texture = new Texture(options.name, bindingId);
		appendToManagedTexture(texture);

		texture.updateInfo(TextureBinder.bindTextureFromFile(texture, context, url, options, TextureReplaceOptions.build()));

		return texture;
	}

	/**
	 * Carica una texture da un file in un assets
	 * 
	 * @param url
	 *            path del file da caricare
	 * @param options
	 *            opzioni per caricare la texture
	 */
	public Texture createTextureFromAssetsFile(Context context, String url, TextureOptions options) {
		int bindingId = newTextureBindingId();
		Texture texture = new Texture(url, bindingId);
		appendToManagedTexture(texture);

		texture.updateInfo(TextureBinder.bindTextureFromAssetsFile(texture, context, url, options, TextureReplaceOptions.build()));

		return texture;
	}

	/**
	 * Carica una texture da un resource id. Le dimensioni della texture sono quelle dell'immagine stessa, non viene fatto alcun controllo sulla sua dimensione.
	 * 
	 * @param index
	 *            indice della texture da definire
	 * @param resourceIdx
	 */
	public Texture createTextureFromResourceId(Context context, int resourceIdx, TextureOptions options) {
		int bindingId = newTextureBindingId();
						
		String textureName=context.getResources().getResourceEntryName(resourceIdx);
		
		Texture texture = new Texture(textureName, bindingId);
		appendToManagedTexture(texture);

		texture.updateInfo(TextureBinder.bindTextureFromResourceId(texture, context, resourceIdx, options, TextureReplaceOptions.build()));

		return texture;
	}

	/**
	 * <p>
	 * Carica una texture da una bitmap.
	 * </p>
	 * 
	 * @param url
	 *            path del file da caricare
	 * @param options
	 *            opzioni per caricare la texture
	 */
	public Texture createTextureFromBitmap(Context context, Bitmap bitmap, TextureOptions options) {
		int bindingId = newTextureBindingId();
		Texture texture = new Texture(options.name, bindingId);
		appendToManagedTexture(texture);		

		texture.updateInfo(TextureBinder.bindTextureFromBitmap(texture, context, bitmap, options, TextureReplaceOptions.build()));

		return texture;
	}

	/**
	 * Carica una lista di risorse sottoforma di puntatore di risorsa
	 * 
	 * @param gl
	 * @param context
	 * @param resource
	 */
	public void createTextureFromResourceIdList(Context context, List<Integer> resourceIdList, TextureOptions options) {
		int textureIds[] = new int[resourceIdList.size()];

		GLES20.glGenTextures(textureIds.length, textureIds, 0);

		Logger.debug("Generated " + resourceIdList.size() + " texture ids");
		for (int i = 0; i < resourceIdList.size(); i++) {
			createTextureFromResourceId(context, resourceIdList.get(i), options);
		}

	}

	/**
	 * Carica una texture da una risorsa. Se manca il drawable davanti, viene messo in automatico.
	 * 
	 * @param context
	 * @param stringResourceIdx
	 * @param options
	 * 
	 */
	public Texture createTextureFromResourceString(Context context, String stringResourceIdx, TextureOptions options) {
		int bindingId = newTextureBindingId();
		
		String textureName=stringResourceIdx;
		
		Texture texture = new Texture(textureName, bindingId);
		appendToManagedTexture(texture);

		texture.updateInfo(TextureBinder.bindTextureFromResourceString(texture, context, stringResourceIdx, options, TextureReplaceOptions.build()));

		return texture;
	}

	/**
	 * <p>
	 * Crea una texture sulla quale scrivere successivamente.
	 * </p>
	 * <p>
	 * <strong>Non può essere eseguito in modo asincrono</strong>
	 * </p>
	 * 
	 * @param context
	 * @param width
	 * @param height
	 * @return
	 */
	public RenderedTexture createRenderedTexture(Context context, TextureSizeType dimensions, RenderedTextureOptions options) {
		Bitmap bitmap = Bitmap.createBitmap(dimensions.width, dimensions.height, Bitmap.Config.ARGB_8888);
		Texture createdTexture = createTextureFromBitmap(context, bitmap, TextureOptions.build().textureRepeat(TextureRepeatType.NO_REPEAT).textureFilter(TextureFilterType.NEAREST).name(options.name).textureInternalFormat(options.textureInternalFormat));
		RenderedTexture renderedTexture = new RenderedTexture(createdTexture, options);

		// sostituiamo la vecchia texture con quella per il rendering
		textures.set(renderedTexture.index, renderedTexture);

		if (!bitmap.isRecycled())
			bitmap.recycle();
		bitmap = null;

		return renderedTexture;
	}
	
	

	/**
	 * <p>
	 * </p>
	 * 
	 * @param context
	 * @param width
	 * @param height
	 * @param options
	 * @return
	 */
	public ExternalTexture createExternalTexture(ExternalTextureOptions options) {
		int bindingId = newTextureBindingId();

		ExternalTexture texture = new ExternalTexture(options.name, bindingId, options);
		appendToManagedTexture(texture);
		texture.updateInfo(ExternalTextureBinder.bindTexture(texture, bindingId, options.toTextureOptions(), TextureReplaceOptions.build()));

		return texture;
	}

	/**
	 * <p>
	 * Incapsula una texture in una tiled. Usato solo da AtlasLoader
	 * </p>
	 * 
	 * @param createdTexture
	 * @param tiledOptions
	 * @return
	 */
	public AtlasTexture createAtlasTexture(Texture createdTexture, AtlasTextureOptions tiledOptions) {
		AtlasTexture tiledTexture = new AtlasTexture(createdTexture, tiledOptions);
		textures.set(tiledTexture.index, tiledTexture);

		return tiledTexture;
	}
	
	/**
	 * Dato uno context e 6 immagini, crea una texture cube.
	 * 
	 * @param context
	 * @param upperX
	 * @param lowerX
	 * @param upperY
	 * @param lowerY
	 * @param upperZ
	 * @param lowerZ
	 * @param options
	 * @return
	 */
	public CubeTexture createCubeTextureFromResourceId(Context context, int upperX, int lowerX, int upperY, int lowerY, int upperZ, int lowerZ, TextureOptions options) {
		int bindingId = newTextureBindingId();
		CubeTexture cubeTexture = new CubeTexture(options.name, bindingId);
		appendToManagedTexture(cubeTexture);

		cubeTexture.updateInfo(CubeTextureBinder.bindTextureFromResourceId(cubeTexture, context, bindingId, upperX, lowerX, upperY, lowerY, upperZ, lowerZ, options, TextureReplaceOptions.build()));

		return cubeTexture;
	}

	/**
	 * <p>
	 * Crea una texture ricaricabile. Questo in realtà si traduce nella creazione di un sistema di triplo buffering
	 * </p>
	 * 
	 * @param context
	 * @param createdTexture
	 * @param options
	 * @return
	 */
	public DynamicTexture createDynamicTexture(Context context, Texture createdTexture) {
		DynamicTexture rt = new DynamicTexture(createdTexture, createTextureFromBitmap(context, Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888), TextureOptions.build()), createTextureFromBitmap(context,
				Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888), TextureOptions.build()));

		return rt;
	}

	/**
	 * <p>
	 * Recupera la texture.
	 * </p>
	 * 
	 * @param textureIndex
	 * @return
	 */
	public Texture getTexture(int textureIndex) {
		return textures.get(textureIndex);
	}

	/**
	 * <p>
	 * Numero di texture attualmente allocate.
	 * </p>
	 * 
	 * @return
	 */
	public int getNumberOfTextures() {
		return textures.size();
	}

	/**
	 * <p>
	 * Recupera il bindingId.
	 * </p>
	 * 
	 * @param textureIndex
	 * @return
	 */
	public int getTextureBindingId(int textureIndex) {
		return textures.get(textureIndex).bindingId;
	}

	/**
	 * <p>
	 * Recupera la texture come atlas.
	 * </p>
	 * 
	 * @param textureIndex
	 * @return
	 */
	public AtlasTexture getAtlasTexture(int textureIndex) {
		return (AtlasTexture) textures.get(textureIndex);

	}

	/**
	 * <p>
	 * Genera un singolo texture id e lo restituisce.
	 * </p>
	 * 
	 * @return
	 */
	private int newTextureBindingId() {
		int[] resourceId = new int[1];
		GLES20.glGenTextures(resourceId.length, resourceId, 0);

		return resourceId[0];
	}

	/**
	 * <p>
	 * Ricarica una texture da un file. Il binding id rimane invariato.
	 * </p>
	 * 
	 * @param url
	 *            path del file da caricare
	 * @param options
	 *            opzioni per caricare la texture
	 */
	public Texture replaceTextureFromFile(int index, Context context, String url, TextureOptions options, TextureReplaceOptions loadOptions) {
		Texture texture = getTexture(index);

		texture.updateInfo(TextureBinder.bindTextureFromFile(texture, context, url, options, loadOptions));

		return texture;
	}

	/**
	 * <p>
	 * Ricarica una texture da un file assets. Il binding id rimane invariato.
	 * </p>
	 * 
	 * @param url
	 *            path del file da caricare
	 * @param options
	 *            opzioni per caricare la texture
	 */
	public Texture replaceTextureFromAssetsFile(int index, Context context, String url, TextureOptions options, TextureReplaceOptions loadOptions) {
		Texture texture = getTexture(index);

		texture.updateInfo(TextureBinder.bindTextureFromAssetsFile(texture, context, url, options, loadOptions));

		return texture;
	}

	/**
	 * <p>
	 * Ricarica una texture da una bitmap. Il binding id rimane invariato.
	 * </p>
	 * 
	 * @param url
	 *            path del file da caricare
	 * @param options
	 *            opzioni per caricare la texture
	 */
	public Texture replaceTextureFromBitmap(int index, Context context, Bitmap bitmap, TextureOptions options, TextureReplaceOptions loadOptions) {
		Texture texture = getTexture(index);

		texture.updateInfo(TextureBinder.bindTextureFromBitmap(texture, context, bitmap, options, loadOptions));

		return texture;
	}

	/**
	 * Ricarica una texture da un resource id alla posizione definita. Le dimensioni della texture sono quelle dell'immagine stessa, non viene fatto alcun controllo sulla sua
	 * dimensione.
	 * 
	 * @param index
	 *            indice della texture da sostituire
	 * @param context
	 *            contesto
	 * @param resourceIdx
	 *            risorsaId
	 * @param options
	 *            opzioni
	 * @return texture appena creata, con lo stesso binding id di prima
	 */
	public Texture replaceTextureFromResourceId(int index, Context context, int resourceIdx, TextureOptions options, TextureReplaceOptions loadOptions) {
		Texture texture = getTexture(index);

		texture.updateInfo(TextureBinder.bindTextureFromResourceId(texture, context, resourceIdx, options, loadOptions));

		return texture;
	}

	/**
	 * Ricarica una texture da una risorsa alla posizione definita
	 * 
	 * @param index
	 *            indice della texture da sostituire
	 * @param context
	 *            context
	 * @param stringResourceIdx
	 *            risorsa id sottoforma di stringa
	 * @param options
	 *            opzioni
	 * @return texture appena creata, con lo stesso binding id di prima
	 */
	public Texture replaceTextureFromResourceString(int index, Context context, String resourceIdString, TextureOptions options, TextureReplaceOptions loadOptions) {
		Texture texture = getTexture(index);

		texture.updateInfo(TextureBinder.bindTextureFromResourceString(texture, context, resourceIdString, options, loadOptions));

		return texture;
	}

	public boolean isTexturesReloadable() {
		return reloadable;
	}


}
