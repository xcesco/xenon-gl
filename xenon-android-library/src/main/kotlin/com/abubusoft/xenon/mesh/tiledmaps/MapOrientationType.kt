package com.abubusoft.xenon.mesh.tiledmaps

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricMapHandler
import com.abubusoft.xenon.mesh.tiledmaps.isostaggered.ISSMapHandler
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalMapHandler

/**
 *
 * Enume relativo all'orientamento della mappa. In base all'orientamento vengono definiti gli handler per la mappa ed i vari layer.
 *
 * @author xcesco
 */
enum class MapOrientationType(
    /**
     * classe dell'handler della mappa
     */
    private val mapHandlerClazz: Class<out MapHandler>?
) {
    ORTHOGONAL(OrthogonalMapHandler::class.java), ISOMETRIC(IsometricMapHandler::class.java), STAGGERED(ISSMapHandler::class.java), HEXAGONAL(null);

    /**
     * Crea il map handler associato
     *
     * @return map handler
     */
    fun createMapHandler(map: TiledMap?): MapHandler {
        try {
            return mapHandlerClazz!!.getDeclaredConstructor(TiledMap::class.java).newInstance(map) as MapHandler
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
        }
        throw RuntimeException("Tiled layer handler not defined for $this")
    }
}