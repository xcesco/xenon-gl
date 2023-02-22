/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps;

import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.MeshSprite;
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier;
import com.abubusoft.xenon.mesh.tiledmaps.internal.ImageLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXLoaderType;
import com.abubusoft.xenon.texture.AtlasTexture;
import com.abubusoft.xenon.texture.AtlasTextureOptions;
import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureFilterType;
import com.abubusoft.xenon.texture.TextureManager;
import com.abubusoft.xenon.texture.TextureOptions;
import com.abubusoft.xenon.texture.TextureRepeatType;
import com.abubusoft.xenon.vbo.BufferAllocationOptions;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.core.XenonRuntimeException;
import org.xml.sax.Attributes;

import android.content.Context;

/**
 * <p>
 * Un layer che contiene una singola immagine.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class ImageLayer extends Layer {

	/**
	 * tipo di caricamento dell'immagine: da asset o da risorsa. A seconda del valore, tratta in modo diverso l'imageSource.
	 */
	private final TMXLoaderType loaderType;

	/**
	 * Opzione per la creazione del texture atlas.
	 */
	private TextureOptions textureOptions;

	/**
	 * Nome della texture associata al layer
	 */
	public String imageSource;

	/**
	 * modo di riempimento
	 */
	public FillModeType fillMode;

	/**
	 * <p>
	 * Modi di riempire il layer
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum FillModeType {
			/**
			 * Ricopre la finestra, considerando quando
			 */
			REPEAT_ON_WINDOW,
			/**
			 * Ricopre la window con la texture, senza ripetizione.
			 */
			EXPAND_ON_WINDOW,
			REPEAT_ON_MAP,
			EXPAND_ON_MAP;
	}

	public ImageLayer(TiledMap tiledMap, TMXLoaderType loaderTypeValue, Attributes atts, TextureFilterType textureFilterValue) {
		super(LayerType.IMAGE, tiledMap, atts);
		loaderType = loaderTypeValue;
		textureOptions = TextureOptions.build().textureFilter(textureFilterValue).textureRepeat(TextureRepeatType.REPEAT);

		fillMode = FillModeType.REPEAT_ON_WINDOW;

		// fullWindowTile = new Tile();
	}

	/**
	 * <p>
	 * Evento da invocare quando si effettua il resize della window
	 * </p>
	 */
	public void onBuildView(TiledMapView view) {
		handler.onBuildView(view);
		
		fillMode = FillModeType.REPEAT_ON_WINDOW;
		Texture texture = textureList.get(0);

		// nel costruttore le dimensioni della window non le abbiamo
		// fullWindowTile.setDimensions(tiledMap.windowWidth, tiledMap.windowHeight);

		shape = MeshFactory.createSprite((float) view.windowWidth, (float) view.windowHeight,
				MeshOptions.build().bufferAllocationOptions(BufferAllocationOptions.build().indexAllocation(BufferAllocationType.STATIC).vertexAllocation(BufferAllocationType.STATIC).textureAllocation(BufferAllocationType.STREAM)));

		switch (fillMode) {
		case REPEAT_ON_WINDOW:
			// fullWindowTile.setTextureCoordinate(0f, (float) (1.0 * tiledMap.windowWidth / texture.info.dimension.width), 0, (float) (1.0 * tiledMap.windowHeight /
			// texture.info.dimension.height));
			TextureQuadModifier.setTextureCoords(shape.textures[0], 0, 0f, (float) (1.0 * view.windowWidth / texture.info.dimension.width), 0, (float) (1.0 * view.windowHeight / texture.info.dimension.height), false, true);

			break;
		case EXPAND_ON_WINDOW:
			// ok
			// fullWindowTile.setTextureCoordinate(0, 1, 0, 1);
			TextureQuadModifier.setTextureCoords(shape.textures[0], 0, 0f, 1f, 0f, 1f, false, true);
			break;
		case REPEAT_ON_MAP:
			// fullWindowTile.setTextureCoordinate(0f, (float)(1.0 * tiledMap. / texture.info.dimension.width) , 0, (float)(1.0 *tiledMap.windowHeight /
			// texture.info.dimension.height));
			break;
		case EXPAND_ON_MAP:
			// fullWindowTile.setTextureCoordinate(0f, (float)(1.0 * tiledMap.windowWidth / texture.info.dimension.width) , 0, (float)(1.0 *tiledMap.windowHeight /
			// texture.info.dimension.height));
			break;
		}

	}

	/**
	 * <p>
	 * mesh per disegnare lo sfondo.
	 * </p>
	 */
	public MeshSprite shape;

	protected ImageLayerHandler handler;

	/**
	 * <p>
	 * Carichiamo la texture e la inseriamo tra le texture usate da questo layer
	 * </p>
	 * 
	 * @param context
	 */
	public void loadTexture(Context context) {
		Texture temp = null;
		switch (loaderType) {
		case ASSET_LOADER:
			temp = TextureManager.instance().createTextureFromAssetsFile(context, imageSource, textureOptions);
			break;
		case RES_LOADER:
			temp = TextureManager.instance().createTextureFromResourceString(context, imageSource, textureOptions);
			break;
		default:
			throw new XenonRuntimeException("Type of loader is not supported");
		}

		AtlasTextureOptions tto = AtlasTextureOptions.build();

		AtlasTexture texture;

		tto.tileWidth(temp.info.dimension.width).tileHeight(temp.info.dimension.height).margin(0).spacing(0);
		texture = TextureManager.instance().createAtlasTexture(temp, tto);
		this.textureList.add(texture);
	}

	@Override
	protected void buildHandler(AbstractLayerHandler<?> handler) {
		this.handler=(ImageLayerHandler) handler;		
	}
	
	@Override
	public LayerDrawer drawer() {
		return handler;
	}
	
	public TiledMapView view() { return handler.view(); }

}
