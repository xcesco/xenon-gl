package com.abubusoft.xenon.mesh.persistence.kripton

import android.content.Context
import com.abubusoft.kripton.BinderType
import com.abubusoft.kripton.KriptonBinder
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.BufferHelper.bindBuffer
import com.abubusoft.xenon.vbo.BufferHelper.buildBuffer
import java.io.IOException
import java.io.InputStream

object KriptonLoader {
    /**
     * Effettua il build, il bind per opengl e l'update del buffer
     * @param buffer
     */
    private fun buildAndBindAndUpdate(buffer: AbstractBuffer?) {
        buildBuffer(buffer)
        bindBuffer(buffer)
        buffer!!.update()
    }

    /**
     * carica un file usando il binderReader specificato come parametro in una mesh il cui tipo è stato specificato
     *
     * @param input
     * @param reader
     * @param options
     * @return
     */
    fun load(input: InputStream?, binderType: BinderType?, options: MeshOptions): Mesh {
        return try {
            val mesh = KriptonBinder.bind(binderType).parse(input, options.meshClazz)

            // vertici (ci sono sempre)
            buildAndBindAndUpdate(mesh.vertices)

            // vertici (ci sono sempre)
            if (mesh.normalsEnabled) {
                buildAndBindAndUpdate(mesh.normals)
            }

            // texture
            if (mesh.texturesEnabled) {
                for (i in 0 until mesh.texturesCount) {
                    buildAndBindAndUpdate(mesh.textures[i])
                }
            }

            // attributi
            if (mesh.attributesEnabled) {
                for (i in 0 until mesh.attributesCount) {
                    buildAndBindAndUpdate(mesh.attributes[i])
                }
            }

            // indici
            if (mesh.indexesEnabled) {
                buildAndBindAndUpdate(mesh.indexes)
            }
            mesh
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }

    /**
     * Carica un file da un asset in un context di applicazione android.
     *
     * @param context
     * @param fileName
     * @param reader
     * @param options
     * @return
     */
    fun loadFromAsset(context: Context, fileName: String?, bindType: BinderType?, options: MeshOptions): Mesh {
        return try {
            load(context.assets.open(fileName!!), bindType, options)
        } catch (e: IOException) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }

    private fun loadFromResource(context: Context, resourceId: Int, bindType: BinderType, options: MeshOptions): Mesh {
        return try {
            load(context.resources.openRawResource(resourceId), bindType, options)
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }

    fun loadMeshFromJSON(context: Context, resourceId: Int, options: MeshOptions): Mesh {
        return loadFromResource(context, resourceId, BinderType.JSON, options)
    }

    fun loadMeshFromJSON(context: Context, fileName: String?, options: MeshOptions): Mesh {
        return loadFromAsset(context, fileName, BinderType.JSON, options)
    }

    fun loadMeshFromXML(context: Context, fileName: String?, options: MeshOptions): Mesh {
        return loadFromAsset(context, fileName, BinderType.XML, options)
    }

    fun loadMeshFromXML(context: Context, resourceId: Int, options: MeshOptions): Mesh {
        return loadFromResource(context, resourceId, BinderType.XML, options)
    }
}