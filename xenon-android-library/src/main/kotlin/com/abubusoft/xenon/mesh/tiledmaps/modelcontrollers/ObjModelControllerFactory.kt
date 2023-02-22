package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers

import android.content.Context
import com.abubusoft.xenon.animations.TiledMapAnimation
import com.abubusoft.xenon.core.util.ResourceUtility.resolveArrayOfString
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap

object ObjModelControllerFactory {
    /**
     *
     * Crea un obj controller partendo da due array di stringhe.
     *
     * @param context
     * context
     * @param objName
     * nome del player
     * @param spriteBaseName
     * nome base dell'animazione
     * @param map
     * tiledmape
     * @param controllerOptions
     * opzioni
     * @return
     */
    fun create(context: Context?, objName: String, spriteBaseName: String, map: TiledMap, controllerOptions: ObjModelControllerOptions): ObjModelController {
        var type: ObjActionType
        val keys = resolveArrayOfString(context!!, controllerOptions.resourceKeys)
        val values = resolveArrayOfString(context, controllerOptions.resourceValues)
        if (keys.size != values.size) {
            throw RuntimeException("Can not create ObjModelController, keys and values are not compatible!")
        }
        val obj: ObjDefinition = ObjModelController.Companion.objectFind(map, objName)
        val ret = ObjModelController(obj, map, keys.size, MeshOptions.build())

        // definiamo per ogni key
        for (i in keys.indices) {
            type = ObjActionType.valueOf(keys[i].uppercase())
            if (type == null) throw RuntimeException("Unknown movement " + keys[i].uppercase())
            val animation = TiledMapAnimation()
            animation.setAnimation(type.x * map.tileWidth, type.y * map.tileHeight, spriteBaseName + "_" + type.toString().lowercase())
            // impostiamo il loop a false
            animation.isLoop = false
            ret.animations[type.ordinal] = animation
        }

        // animazione iniziale
        if (controllerOptions.status != null) {
            ret.timeline.add(ret.animations[controllerOptions.status!!.ordinal], true)
        }
        return ret
    }
}