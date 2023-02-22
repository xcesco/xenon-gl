package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.ArrayList;

import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.mesh.tiledmaps.internal.ObjectLayerHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties;
import org.xml.sax.Attributes;

public class ObjectLayer extends Layer {

	/**
	 * <p>
	 * Elenco di oggetti presenti nel layer.
	 * </p>
	 */
	public ArrayList<ObjDefinition> objects;

	/**
	 * <p>
	 * Drawer da utilizzare per il draw del layer.
	 * </p>
	 */
	public ObjectLayerDrawer objectDrawer;

	private ObjectLayerHandler handler;

	/**
	 * <p>
	 * Costruttore
	 * </p>
	 * 
	 * @param tiledMap
	 * @param atts
	 */
	public ObjectLayer(TiledMap tiledMap, Attributes atts) {
		super(LayerType.OBJECTS, tiledMap, atts);

		objects = new ArrayList<ObjDefinition>();
		visible = SAXUtil.getInt(atts, TMXPredefinedProperties.VISIBLE, 1) == 1;
	}

	/**
	 * <p>
	 * Elenco di oggetti definiti nel layer
	 * </p>
	 * 
	 * @return elenco degli oggetti
	 */
	public ArrayList<ObjDefinition> getObjects() {
		return objects;
	}

	/**
	 * <p>
	 * Aggiunge un oggetto.
	 * </p>
	 * 
	 * @param object
	 */
	public void addObject(ObjDefinition object) {
		objects.add(object);
	}

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.mesh.tiledmaps.Layer#onBuildView(com.abubusoft.xenon.mesh.tiledmaps.internal.MapView)
	 */
	@Override
	public void onBuildView(TiledMapView view) {
		handler.onBuildView(view);
	}

	/**
	 * <p>
	 * Imposta il drawer da utilizzare per il rendering.
	 * </p>
	 * 
	 * @param value
	 */
	public void setObjectDrawer(ObjectLayerDrawer value) {
		objectDrawer = value;
	}
	
	@Override
	protected void buildHandler(AbstractLayerHandler<?> handler) {
		this.handler=(ObjectLayerHandler) handler;		
	}
	
	@Override
	public LayerDrawer drawer() {
		return handler;
	}

	@Override
	public TiledMapView view() {
		return handler.view();
	}
}
