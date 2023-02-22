package com.abubusoft.xenon.mesh.tiledmaps.isometric

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.math.XenonMath.clamp
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController

class IsometricMapController(mapValue: TiledMap?, cameraValue: Camera?) : AbstractMapController(mapValue, cameraValue) {
    /**
     * Converte un punto dello schermo nelle coordinate
     *
     * @param screenX
     * @param screenY
     *
     * @return punto con coordinate sulla mappa
     */
    override fun touch(screenX: Float, screenY: Float): Point2 {
        run {
            Logger.info(
                "Isometric map position (%s , %s), window size = %s, tile size = %s",
                map.positionInMap.x,
                map.positionInMap.y,
                (map.handler as IsometricMapHandler).isoWindowSize,
                (map.handler as IsometricMapHandler).isoTileSize
            )
        }
        run {
            workScroll.set(IsometricHelper.convertRawScreen2CenteredWindow(this, screenX, screenY))
            Logger.info("Convert raw screen (%s , %s) to window (%s , %s)", screenX, screenY, workScroll.x, workScroll.y)
        }
        run {
            workScroll.set(IsometricHelper.convertRawScreen2IsoMap(this, screenX, screenY))
            Logger.info("Convert raw screen (%s , %s) to map (%s , %s)", screenX, screenY, workScroll.x, workScroll.y)
        }
        run {
            workScroll.set(IsometricHelper.convertRawScreen2IsoTileIndex(this, screenX, screenY))
            Logger.info("Convert raw screen (%s , %s) to tile (%s , %s)", screenX, screenY, workScroll.x, workScroll.y)
        }
        run {
            workScroll.set(IsometricHelper.convertRawScreen2IsoTileOffset(this, screenX, screenY))
            Logger.info("Convert raw screen (%s , %s) to tile offset (%s , %s)", screenX, screenY, workScroll.x, workScroll.y)
        }

        // x verifica
        run {
            workScroll.set(IsometricHelper.convertRawScreen2IsoWindow(this, screenX, screenY))
            Logger.info("Convert raw screen (%s , %s) to iso (%s , %s)", screenX, screenY, workScroll.x, workScroll.y)
            workScroll.set(IsometricHelper.convertIsoWindow2RawScreen(this, workScroll.x, workScroll.y))
            Logger.info("Check raw screen (%s , %s) to calculated (%s , %s)", screenX, screenY, workScroll.x, workScroll.y)
        }
        Logger.info("----ss--------------------")
        /*
		 * scroll.x=scroll.x-(this.map.view().windowCenter.x-map.tileWidth); scroll.y=(this.map.view().windowCenter.y- map.tileHeight)-scroll.y;
		 * 
		 * // versione 2 scroll.x = -(scroll.y -scroll.x / 2); scroll.y = (scroll.y + scroll.x / 2);
		 * 
		 * scroll.x= scroll.x+(map.positionInMap.x+(map.view().windowTileColumns*map.tileWidth*0.5f)); scroll.y=-scroll.y+(map.positionInMap.y+map.view().windowTileRows*map.tileHeight*0.5f);
		 * 
		 * Logger.info("Convert screen (%s , %s) to (%s , %s)", screenX, screenY, scroll.x, scroll.y); Logger.info("Convert screen (%s , %s) to tile (row: %s , col: %s)", screenX, screenY, (int)scroll.y/map.tileWidth,
		 * (int)scroll.x/map.tileHeight);
		 * 
		 * //TODO per il momento non funziona molto bene //map.handler.convertScreen2Map(scroll, screenX* screenToTiledMapFactor, screenY* screenToTiledMapFactor);
		 */return workScroll
    }

    override fun position(x: Float, y: Float) {
        map.positionInMap.setCoords(x, y)
        if (map.scrollHorizontalLocked) {
            // se locked, la position non può andare oltre
            map.positionInMap.x = clamp(map.positionInMap.x, 0f, map.view().mapMaxPositionValueX)
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
            map.positionInMap.y = clamp(map.positionInMap.y, 0f, map.view().mapMaxPositionValueY)
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