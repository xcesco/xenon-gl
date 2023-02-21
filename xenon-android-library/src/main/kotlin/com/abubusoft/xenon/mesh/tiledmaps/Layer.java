package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.ArrayList;
import java.util.Locale;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.LayerAttributes;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties;
import com.abubusoft.xenon.texture.AtlasTexture;
import org.xml.sax.Attributes;

/**
 * <p>
 * Rappresentazione di base dei layer.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class Layer extends PropertiesCollector {

	/**
	 * Configura l'handler del layer
	 * 
	 * @param handler
	 */
	protected abstract void buildHandler(AbstractLayerHandler<?> handler);

	/**
	 * Restituisce l'oggetto necessario a disegnare il layer stesso. Tipicamente è l'handler stesso.
	 * 
	 * @return drawer
	 */
	public abstract LayerDrawer drawer();

	/**
	 * <p>
	 * Tipo di layer.
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum LayerType {
			/**
			 * <p>
			 * Il layer visualizza un'immagine
			 * </p>
			 */
			IMAGE,
			/**
			 * <p>
			 * Il layer visualizza un'insieme di tile.
			 * </p>
			 */
			TILED,

			/**
			 * <p>
			 * Il lavyer contiene degli oggetti
			 * </p>
			 */
			OBJECTS;
	};

	/**
	 * <p>
	 * Inizializza tutte le proprietà che i vari tipi di layer hanno in comune, a prescindere dal loro tipo. Il nome viene messo in lowercase
	 * </p>
	 * 
	 * @param layerType
	 * @param tiledMap
	 * @param atts
	 */
	public Layer(LayerType layerType, TiledMap tiledMap, Attributes atts) {
		this.type = layerType;
		this.tiledMap = tiledMap;
		this.name = SAXUtil.getString(atts, LayerAttributes.NAME).toLowerCase(Locale.ENGLISH);
		this.visible = SAXUtil.getInt(atts, TMXPredefinedProperties.VISIBLE, 1) == 1;
		this.opacity = SAXUtil.getFloat(atts, LayerAttributes.OPACITY, 1f);
		this.textureList = new ArrayList<AtlasTexture>();

		// istanziamo l'handler in base al tipo di orientation della mappa ed al tipo di layer
		switch (layerType) {
		case TILED:
			buildHandler(tiledMap.handler.buildTiledLayerHandler((TiledLayer) this));
			break;
		case IMAGE:
			buildHandler(tiledMap.handler.buildImageLayerHandler((ImageLayer) this));
			break;
		case OBJECTS:
			buildHandler(tiledMap.handler.buildObjectLayerHandler((ObjectLayer) this));
			break;
		default:
			throw (new RuntimeException("Layer type " + layerType + " is not supported"));
		}

	}

	/**
	 * <p>
	 * Indica se il layer deve essere rimosso.
	 * </p>
	 * 
	 * <ul>
	 * <li>Quelli che contengono nel nome <b>preview</b></li>
	 * <li>Quelli che hanno una proprietà di nome <b>preview</b> = true</li>
	 * </ul>
	 * 
	 * @return true se il layer deve essere rimosso
	 */
	public boolean isPreviewLayer() {
		return ("true".equals(properties.get(TMXPredefinedProperties.PREVIEW))) || (name.contains(TMXPredefinedProperties.PREVIEW));
	}

	/**
	 * <p>
	 * Nome del layer. Non può essere cambiato.
	 * </p>
	 */
	public final String name;

	/**
	 * <p>
	 * Lista ordinata di texture
	 * </p>
	 */
	public ArrayList<AtlasTexture> textureList;

	/**
	 * <p>
	 * Tipo di layer.
	 * </p>
	 */
	public final LayerType type;

	/**
	 * indica se visibile
	 */
	public boolean visible;

	/**
	 * <p>
	 * Livello di opacità del layer, da 0 a 1. Agisce solo a livello di channel Alpha.
	 * </p>
	 */
	public float opacity;

	/**
	 * riferimento alla tiled map
	 */
	public final TiledMap tiledMap;

	/**
	 * percentuale di speed startX da 0 a 1. Deve essere float
	 */
	public float speedPercentageX = 1.0f;

	/**
	 * percentuale di speed startY da 0 a 1. Deve essere float
	 */
	public float speedPercentageY = 1.0f;

	/**
	 * offset del layer rispetto alla posizione iniziale X. In pixel
	 */
	public float layerOffsetX;

	/**
	 * offset del layer rispetto alla posizione iniziale Y. In pixel
	 */
	public float layerOffsetY;

	/**
	 * <p>
	 * marca il layer per essere rimosso se impostato a true
	 * </p>
	 */
	public boolean discard;

	/**
	 * Effettua lo scroll del layer
	 * 
	 * <p>Dobbiamo convertire le dimensioni della mappa in coordinate view riferite al layer.</p>
	 * 
	 * @param mapOffsetX
	 * @param mapOffsetY
	 */
	public void scroll(float mapOffsetX, float mapOffsetY) {
		layerOffsetX += mapOffsetX * speedPercentageX;
		if (tiledMap.scrollHorizontalLocked) {
			//screenOffsetX=XenonMath.clamp(screenOffsetX, 0f, tiledMap.mapWidth - tiledMap.view.windowWidth);
			layerOffsetX= XenonMath.clamp(layerOffsetX, 0f, tiledMap.view.mapMaxPositionValueX);
		}
		// modulo
		layerOffsetX = layerOffsetX % tiledMap.mapWidth;

		layerOffsetY += mapOffsetY * speedPercentageY;
		if (tiledMap.scrollVerticalLocked) {
			//screenOffsetY=XenonMath.clamp(screenOffsetY, 0f, tiledMap.mapHeight - tiledMap.view.windowHeight);
			layerOffsetY= XenonMath.clamp(layerOffsetY, 0f, tiledMap.view.mapMaxPositionValueY);
		}
		// modulo
		layerOffsetY = layerOffsetY % tiledMap.mapHeight;
	}

	/**
	 * <p>Effettua il posizionamento del layer. Siccome la posizione è modulo dimension, rimaniamo sempre nei limiti della mappa.</p>
	 * 
	 * <p>Dobbiamo convertire le dimensioni della mappa in coordinate view riferite al layer.</p>
	 * 
	 * @param mapDistanceX
	 * 		distanza nel sistema mappa
	 * @param mapDistanceY
	 * 		distanza nel sistema mappa
	 */
	public void position(float mapDistanceX, float mapDistanceY) {
		layerOffsetX = (mapDistanceX * speedPercentageX);		

		// se ci sono i lock, vediamo di rispettarli.
		if (tiledMap.scrollHorizontalLocked) {
			//screenOffsetX=XenonMath.clamp(screenOffsetX, 0f, tiledMap.mapWidth - tiledMap.view.windowWidth);
			layerOffsetX= XenonMath.clamp(layerOffsetX, 0f, tiledMap.view.mapMaxPositionValueX);
		}
		
		layerOffsetY = (mapDistanceY * speedPercentageY);
		if (tiledMap.scrollVerticalLocked) {
			//screenOffsetY=XenonMath.clamp(screenOffsetY, 0f, tiledMap.mapHeight - tiledMap.view.windowHeight);
			layerOffsetY= XenonMath.clamp(layerOffsetY, 0f, tiledMap.view.mapMaxPositionValueY);
		}
				
	}

	/**
	 * Indica se è visualizzabile o meno
	 * 
	 * @return true se il layer è visibile
	 */
	public boolean isDrawable() {
		return visible;
	}

	/**
	 * Da invocare quando creo la finestra
	 * 
	 * @param view
	 *            view appena costruita
	 */
	public abstract void onBuildView(TiledMapView view);
	
	public abstract TiledMapView view();

}
