package com.abubusoft.xenon.mesh.tiledmaps;

import com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler;
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricMapHandler;
import com.abubusoft.xenon.mesh.tiledmaps.isostaggered.ISSMapHandler;
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalMapHandler;
import com.abubusoft.kripton.android.Logger;

/**
 * 
 * Enume relativo all'orientamento della mappa. In base all'orientamento vengono definiti gli handler per la mappa ed i vari layer.
 * 
 * @author xcesco
 *
 */
public enum MapOrientationType {
		ORTHOGONAL(OrthogonalMapHandler.class),
		ISOMETRIC(IsometricMapHandler.class),
		STAGGERED(ISSMapHandler.class),
		HEXAGONAL(null);

	/**
	 * classe dell'handler della mappa
	 */
	private Class<? extends MapHandler> mapHandlerClazz;



	private MapOrientationType(Class<? extends MapHandler> mapHandlerClazz) {
		this.mapHandlerClazz = mapHandlerClazz;
	}

	/**
	 * Crea il map handler associato
	 * 
	 * @return map handler
	 */
	public MapHandler createMapHandler(TiledMap map) {
		try {
			return (MapHandler) mapHandlerClazz.getDeclaredConstructor(TiledMap.class).newInstance(map);
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
		}

		throw (new RuntimeException("Tiled layer handler not defined for " + this));
	}

}
