package com.abubusoft.xenon.mesh.persistence.wavefront

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.Mesh
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * Exporter nel formato Wavefront.
 *
 * @author Francesco Benincasa
 */
class WavefrontExporter {
    var writer: BufferedWriter? = null
    val version = "1.0.0"

    /**
     * Exporter del file.
     *
     * @param file
     * @param mesh
     * @return
     */
    fun exportInternal(file: File?, mesh: Mesh): Boolean {
        try {
            writer = BufferedWriter(FileWriter(file))
            writeComment("Abubu exporter %s", version)
            writeComment("File Created %s", Date())
            writeLine()
            writeComment("")
            writeComment("object %s", mesh.name!!)
            writeComment("")
            writeLine()
            var i = 0
            while (i < mesh.vertices!!.vertexCount * 3) {
                writeLine("v %s %s %s", mesh.vertices!!.coords!![i + 0], mesh.vertices!!.coords!![i + 1], mesh.vertices!!.coords!![i + 2])
                i += 3
            }
            writeComment("%s vertices ", mesh.vertices!!.vertexCount)
            writeLine()

            // texture
            if (mesh.texturesCount > 0) {
                var i = 0
                while (i < mesh.textures[0].coords!!.size) {
                    writeLine("vt %s %s %s", mesh.textures[0].coords!![i + 0], mesh.textures[0].coords!![i + 1], 0f)
                    i += 2
                }
                writeComment("%s texture coords", mesh.vertices!!.vertexCount)
                writeLine()
            }

            // indici
            writeLine("g %s", mesh.name!!)
            if (mesh.indexesEnabled) {
                var i = 0
                while (i < mesh.indexes!!.values!!.size) {
                    writeLine(
                        "f %s/%s %s/%s %s/%s",  // vertice 0
                        mesh.indexes!!.values!![i + 0] + 1, mesh.indexes!!.values!![i + 0] + 1,  // vertice 1
                        mesh.indexes!!.values!![i + 1] + 1, mesh.indexes!!.values!![i + 1] + 1,  // vertice 2
                        mesh.indexes!!.values!![i + 2] + 1, mesh.indexes!!.values!![i + 2] + 1
                    )
                    i += 3
                }
                writeComment("%s faces", mesh.indexesCount / 3)
                writeLine()
            }

            // Close writer
            writer!!.close()
        } catch (e: Exception) {
            Logger.error(e.message)
            throw XenonRuntimeException(e)
        } finally {
            if (writer != null) try {
                writer!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }

    /**
     * Aggiunge commento
     *
     * @param msg
     * @param params
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeComment(msg: String, vararg params: Any) {
        writer!!.write(
            """
    # ${String.format(msg, *params)}
    
    """.trimIndent()
        )
    }

    /**
     * Aggiunge linea
     *
     * @param msg
     * @param params
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeLine(msg: String, vararg params: Any) {
        writer!!.write(
            """
    ${String.format(msg, *params)}
    
    """.trimIndent()
        )
    }

    /**
     * Aggiunge linea vuota
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeLine() {
        writer!!.write("\n")
    }

    companion object {
        /**
         * Dato un mesh in cui sono presenti i dati CLIENT (gli array sono presenti nello spazio java), si provvede ad esportare modello in un file.
         *
         * Attualmente sono supportate i vertici, una texture, le normali, ed il nome della mesh.
         *
         * @param file
         * @param mesh
         * @return
         */
        fun export(file: File?, mesh: Mesh): Boolean {
            val instance = WavefrontExporter()
            return instance.exportInternal(file, mesh)
        }
    }
}