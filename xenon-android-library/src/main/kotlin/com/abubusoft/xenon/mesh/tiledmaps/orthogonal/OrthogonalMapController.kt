package com.abubusoft.xenon.mesh.tiledmaps.orthogonal

import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController

/**
 * @author Francesco Benincasa
 */
class OrthogonalMapController
/**
 *
 *
 * Costuttore. Effettua tutte le operazioni necessarie per farlo funzionare.
 *
 *
 *
 * @param mapValue
 * @param cameraValue
 */
    (mapValue: TiledMap?, cameraValue: Camera?) : AbstractMapController(mapValue!!, cameraValue!!), MapController {
    /**
     * Muove la posizione della mappa, in pixel.
     *
     * TODO da testare
     *
     * @param x
     * @param y
     */
    override fun position(x: Float, y: Float) {
        map.positionInMap.setCoords(x, y)
        if (map.scrollHorizontalLocked) {
            // se locked, la position non può andare oltre
            if (map.positionInMap.x < 0f) map.positionInMap.x = 0f
            if (map.positionInMap.x > map.view().mapMaxPositionValueX) {
                map.positionInMap.x = map.view().mapMaxPositionValueX
            }
        } else {
            // la posizione nella mappa deve essere sempre circoscritta alle
            // dimensioni della mappa stessa, senza mai andare oltre
            if (map.positionInMap.x < 0f) {
                map.positionInMap.x += map.mapWidth.toFloat()
            }
            if (map.positionInMap.x > map.mapWidth) {
                map.positionInMap.x -= map.mapWidth.toFloat()
            }
            if (map.movementEventListener != null) {
                map.scrollHorizontalCurrentArea = (map.positionInMap.x * map.listenerOptions.horizontalAreaInvSize).toInt()
                if (map.scrollHorizontalPreviousArea != map.scrollHorizontalCurrentArea) {
                    map.scrollHorizontalPreviousArea = map.scrollHorizontalCurrentArea
                }
            }
        }
        if (map.scrollVerticalLocked) {
            // se locked, la position non può andare oltre
            if (map.positionInMap.y < 0f) map.positionInMap.y = 0f
            if (map.positionInMap.y > map.view().mapMaxPositionValueY) {
                map.positionInMap.y = map.view().mapMaxPositionValueY
            }
        } else {
            if (map.positionInMap.y < 0f) map.positionInMap.y += map.mapHeight.toFloat()
            if (map.positionInMap.y > map.mapHeight) map.positionInMap.y -= map.mapHeight.toFloat()
            if (map.movementEventListener != null) {
                map.scrollVerticalCurrentArea = (map.positionInMap.x * map.listenerOptions.verticalAreaInvSize).toInt()
                if (map.scrollVerticalPreviousArea != map.scrollVerticalCurrentArea) {
                    map.scrollVerticalCurrentArea = map.scrollVerticalPreviousArea
                }
            }
        }
        for (i in map.layers.indices) {
            map.layers[i].position(map.positionInMap.x, map.positionInMap.y)
        }

        // in caso di spostamento e di listener!=null mandiamo evento
        if (map.movementEventListener != null) {
            map.movementEventListener.onPosition(map.positionInMap.x, map.positionInMap.y)
        }
        map.resetScrollAreas()
    }
}