package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers

import com.abubusoft.xenon.R

class ObjModelControllerOptions private constructor() {
    /**
     * elenco dei movimenti
     */
    var resourceKeys = 0

    /**
     * elenco delle animazioni
     */
    var resourceValues = 0

    /**
     * movimento iniziale
     */
    var status: ObjActionType? = null
    fun resourceKeys(value: Int): ObjModelControllerOptions {
        resourceKeys = value
        return this
    }

    fun resourceValues(value: Int): ObjModelControllerOptions {
        resourceValues = value
        return this
    }

    fun status(value: ObjActionType?): ObjModelControllerOptions {
        status = value
        return this
    }

    companion object {
        /**
         *
         *  * **resourceKeys**: array di stringhe dei movimenti definiti - R.array.controller_obj_key
         *  * **resourceValues**: array di stringhe delle animazioni associate - R.array.controller_obj_value
         *  * **status**:stato iniziale - `null`
         *
         *
         * @return
         */
        fun build(): ObjModelControllerOptions {
            val ret = ObjModelControllerOptions()
            ret.resourceKeys = R.array.tiledmap_obj_action_keys
            ret.resourceValues = R.array.tiledmap_obj_action_values
            ret.status = null
            return ret
        }
    }
}