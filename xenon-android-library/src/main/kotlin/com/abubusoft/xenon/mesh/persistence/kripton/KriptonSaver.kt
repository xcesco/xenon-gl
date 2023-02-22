package com.abubusoft.xenon.mesh.persistence.kripton

import com.abubusoft.kripton.BinderType
import com.abubusoft.kripton.KriptonBinder
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.Mesh
import java.io.OutputStream

object KriptonSaver {
    fun save(output: OutputStream?, binderType: BinderType?, mesh: Mesh) {
        try {
            KriptonBinder.bind(binderType).serialize(mesh, output)
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }

    fun saveMeshIntoXML(output: OutputStream?, mesh: Mesh) {
        save(output, BinderType.XML, mesh)
    }

    fun saveMeshIntoJSON(output: OutputStream?, mesh: Mesh) {
        save(output, BinderType.JSON, mesh)
    }
}