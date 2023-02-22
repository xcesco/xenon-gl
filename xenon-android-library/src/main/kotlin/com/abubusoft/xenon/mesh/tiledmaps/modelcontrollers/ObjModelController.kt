package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.animations.TiledMapAnimation
import com.abubusoft.xenon.animations.TiledMapTimeline
import com.abubusoft.xenon.animations.TranslationFrame
import com.abubusoft.xenon.animations.events.EventFrameListener
import com.abubusoft.xenon.math.XenonMath.clamp
import com.abubusoft.xenon.math.XenonMath.max
import com.abubusoft.xenon.mesh.MeshFactory.createSprite
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.MeshSprite
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalHelper
import com.abubusoft.xenon.mesh.tiledmaps.path.*
import com.abubusoft.xenon.opengl.XenonGL

/**
 * @author Francesco Benincasa
 */
class ObjModelController internal constructor(
    /**
     * oggetto da controllare
     */
    var obj: ObjDefinition?,
    /**
     * map
     */
    var map: TiledMap, capacity: Int, options: MeshOptions?
) {
    /**
     *
     */
    protected var pathFinder: PathFinder? = null

    /**
     * temp x
     */
    protected var tempX = 0f

    /**
     * temp y
     */
    protected var tempY = 0f

    /**
     * sprite
     */
    var sprite: MeshSprite

    /**
     * timeline
     */
    var timeline: TiledMapTimeline

    /**
     * Insieme di animazioni. vengono recuperate mediante ordinal del tipo.
     */
    var animations: ArrayList<TiledMapAnimation?>
    // ObjActionSequence statusSequence;
    /**
     * fattore di conversione tra schermo e tiled map
     */
    private val screenToTiledMapFactor: Float
    private var movementMap: MovementMap? = null

    /**
     * imposta l'azione corrente
     *
     * @param action
     */
    fun action(action: ObjActionType) {
        timeline.removeQueue()
        timeline.add(animations[action.ordinal], true)
    }

    /**
     * imposta l'azione corrente
     *
     * @param action
     */
    fun addAction(action: ObjActionType) {
        timeline.add(animations[action.ordinal], true)
    }

    /**
     * pulisce tutte le azioni
     */
    fun clearActions() {
        timeline.removeQueue()
    }

    /**
     * @param capacity
     */
    init {
        // impostiamo riferimenti vari
        sprite = createSprite(obj!!.width, obj!!.height, options!!)
        animations = ArrayList(capacity)
        if (false) {
            movementMap = MovementMapFactory.buildMovementMapByTileProperties(map, "path", "path")
            pathFinder = DijkstraPathFinder(movementMap, false)
        }
        timeline = TiledMapTimeline()
        timeline.setOnMoveEventListener(object : EventFrameListener<TranslationFrame?> {
            override fun onFrameBegin(currentFrame: TranslationFrame) {
                // applichiamo subito le translazioni
                obj!!.x += currentFrame.translation.x
                obj!!.y += currentFrame.translation.y
                // salviamo questo valore
                tempX = obj!!.x
                tempY = obj!!.y
            }

            override fun onFrameEnd(currentFrame: TranslationFrame) {
                // per ottenere uno spostamento perfetto senza sbavature,
                // ripristinamo i valori presi all'inizio del frame
                obj!!.x = tempX
                obj!!.y = tempY

                // al prossimo frame aggiungeremo tutto il delta calcolato pezzo dopo pezzo
            }
        })
        for (i in 0 until capacity) {
            animations.add(null)
        }
        val screenSize = max(XenonGL.screenInfo.width, XenonGL.screenInfo.height).toFloat()
        val windowOfTileSize = map.view().windowDimension
        screenToTiledMapFactor = windowOfTileSize / screenSize
    }

    /**
     * @param enlapsedTime
     */
    fun update(enlapsedTime: Long) {
        timeline.update(enlapsedTime)

        // calcoliamo il valore corrente
        obj!!.x = tempX + timeline.value()!!.translation.x
        obj!!.y = tempY + timeline.value()!!.translation.y
    }

    fun isNodeAccessible(keyNode: Int): Boolean {
        return movementMap!!.isNodeAccessible(keyNode)
    }

    /**
     * Input dall'utente
     *
     * @param x
     * @param y
     */
    fun positionFromScreen(screenDistanceX: Float, screenDistanceY: Float) {
        position(screenDistanceX * screenToTiledMapFactor, screenDistanceY * screenToTiledMapFactor)
    }

    fun position(x: Float, y: Float) {
        obj!!.x = x
        obj!!.y = y
        tempX = 0f
        tempY = 0f
        if (map.scrollHorizontalLocked) {
            obj!!.x = clamp(obj!!.x, 0f, map.mapWidth.toFloat())
        }
        if (map.scrollVerticalLocked) {
            obj!!.y = clamp(obj!!.y, 0f, map.mapHeight.toFloat())
        }
    }

    /**
     *
     *
     * Effettua lo scroll della definizione. Le coordinate sono quelle dello schermo. Questo implica che devono esssere riportate alle dimensioni della tiled map.
     *
     *
     * @param screenDistanceX
     * @param screenDistanceY
     */
    fun moveFromScreen(screenDistanceX: Float, screenDistanceY: Float) {
        move(-screenDistanceX * screenToTiledMapFactor, -screenDistanceY * screenToTiledMapFactor)
    }

    /**
     *
     *
     * Effettua lo scroll della definizione. Le coordinate sono quelle della mappa. Il movimento è solo in verticale o in orizzontale
     *
     *
     * @param distanceX
     * @param distanceY
     */
    fun move(distanceX: Float, distanceY: Float) { // ci muoviamo solo in orizzontale o in verticale if (Math.abs(distanceX) > Math.abs(distanceY)) { distanceY = 0;
        // distanceX=Math.round(distanceX/movement)*movement; } else if (Math.abs(distanceY) > Math.abs(distanceX)) { distanceX = 0;
        // distanceY=Math.round(distanceY/movement)*movement; }
        Logger.info("Scroll %s , %s", distanceX, distanceY)

        // applichiamo le distanze obj.x += distanceX; obj.y += distanceY;

        // rimaniamo nel boundary della tiled map if (map.scrollHorizontalLocked) { obj.x = XenonMath.clamp(obj.x, 0f, map.mapWidth); }
        if (map.scrollVerticalLocked) {
            obj!!.y = clamp(obj!!.y, 0f, map.mapHeight.toFloat())
        }
        moveInternal(distanceX, distanceY)
    }

    /**
     * Dalla posizione corrente, prova a
     *
     * @param mapX
     * @param mapY
     */
    fun navigateTo(mapX: Float, mapY: Float) {
        val start = OrthogonalHelper.translateMapCoordsToTileId(map, obj!!.x, obj!!.y)
        // vogliamo posizionare il centro dello sprite proprio dove abbiamo selezionato, quindi dobbiamo
        // aggiungere meta size per passare dalle coordinate topleft a quelle centrali.
        val end = OrthogonalHelper.translateMapCoordsToTileId(map, mapX, mapY)
        if (end < 0 || end >= map.tileRows * map.tileColumns) {
            // destinazione non valida
            Logger.warn("Destination not valid %s", end)
        } else {
            Logger.info("NAVIGATE from %s to %s", start, end)
            val path = pathFinder!!.findPath(null, start, end)
            var action: MoveType? = null
            for (i in 0 until path.size()) {
                action = path.getMove(i)
                when (action) {
                    MoveType.UP -> addAction(ObjActionType.MOVE_UP)
                    MoveType.LEFT -> addAction(ObjActionType.MOVE_LEFT)
                    MoveType.DOWN -> addAction(ObjActionType.MOVE_DOWN)
                    MoveType.RIGHT -> addAction(ObjActionType.MOVE_RIGHT)
                }
            }
            if (action != null) {
                when (action) {
                    MoveType.UP -> addAction(ObjActionType.STAY_UP)
                    MoveType.LEFT -> addAction(ObjActionType.STAY_LEFT)
                    MoveType.DOWN -> addAction(ObjActionType.STAY_DOWN)
                    MoveType.RIGHT -> addAction(ObjActionType.STAY_RIGHT)
                }
            }
        }
    }

    fun moveStop() {
        timeline.forceNext()
        var currentAnimationName = timeline.animationName
        if (currentAnimationName != null) {
            currentAnimationName = currentAnimationName.replace("move", "stay")
        }
    }

    protected fun moveInternal(distanceX: Float, distanceY: Float) {
        // impostiamo l'animazione
        if (distanceY == 0f) {
            if (distanceX > 0f) {
                timeline.add(animations[ObjActionType.MOVE_RIGHT.ordinal], true)
            } else {
                timeline.add(animations[ObjActionType.MOVE_LEFT.ordinal], true)
            }
        } else {
            if (distanceY > 0f) {
                timeline.add(animations[ObjActionType.MOVE_DOWN.ordinal], true)
            } else {
                timeline.add(animations[ObjActionType.MOVE_UP.ordinal], true)
            }
        }
    }

    companion object {
        /**
         *
         *
         * Cerca in tutti i layer object una definizione con il nome passato come parametro. Se non trova nulla restituisce `null`.
         *
         *
         * @param name
         * @return
         */
        fun objectFind(map: TiledMap, name: String): ObjDefinition? {
            var layer: ObjectLayer
            var objects: ArrayList<ObjDefinition>
            var current: ObjDefinition
            for (i in map.objectLayers.indices) {
                layer = map.objectLayers[i]
                objects = layer.getObjects()
                for (j in objects.indices) {
                    current = objects[j]
                    if (name == current.name) {
                        return current
                    }
                }
            }
            return null
        }
    }
}