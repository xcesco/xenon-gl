package com.abubusoft.xenon.mesh.persistence.wavefront

import android.content.Context
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.UVCoord
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.VertexF
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 *
 *
 * Wavefront obj loader
 *
 *
 *
 * Per le [specifiche](http://en.wikipedia.org/wiki/Wavefront_.obj_file) seguire il link.
 *
 *
 * @author Francesco Benincasa
 */
object WavefrontLoader {
    private const val SPACE = "\\s+"
    private const val SLASH = "/"

    /**
     *
     *
     * Carica un oggetto in formato obj.
     *
     *
     * @param context
     * @param fileName
     * @return
     * model data recuperato dal file
     */
    fun loadFromAsset(context: Context, fileName: String?): WavefrontModelData {
        return try {
            load(context.assets.open(fileName!!))
        } catch (e: IOException) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }

    /**
     * @param in
     * @return
     */
    fun load(`in`: InputStream?): WavefrontModelData {
        val model = WavefrontModelData()
        val vertices = ArrayList<VertexF?>()
        val normals = ArrayList<VertexF?>()
        val uvs = ArrayList<UVCoord?>()
        val faces = ArrayList<WavefrontModelData.Face?>()
        try {

            // 1) read in verticies,
            // 2) read in uvs
            // 3) create faces which are verticies and uvs expanded
            // 4) unroll faces into WavefrontModelData using sequential indicies
            val reader = BufferedReader(InputStreamReader(`in`))
            // StringTokenizer st;
            var line: String?
            line = reader.readLine()
            var tokens: Array<String>
            var temp: Array<String>
            var current: Int
            var index: Int
            Logger.info("Loading obj data")
            while (line != null) {
                current = 0
                tokens = line.trim { it <= ' ' }.split(SPACE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                // st = new StringTokenizer(line, SPACE);
                if (line.startsWith("# object")) {
                    model.name = line.replace("# object", "").trim { it <= ' ' }
                }
                if (tokens.size > 1) {
                    val lineType = tokens[current++]
                    if (lineType == "v") {
                        // vertex
                        val vert = VertexF()
                        vert.x = java.lang.Float.valueOf(tokens[current++])
                        vert.y = java.lang.Float.valueOf(tokens[current++])
                        vert.z = java.lang.Float.valueOf(tokens[current++])
                        vertices.add(vert)
                    } else if (lineType == "vn") {
                        // vertex
                        val normal = VertexF()
                        normal.x = java.lang.Float.valueOf(tokens[current++])
                        normal.y = java.lang.Float.valueOf(tokens[current++])
                        normal.z = java.lang.Float.valueOf(tokens[current++])
                        normals.add(normal)
                    } else if (lineType == "vt") {
                        // texture mapping
                        val uv = UVCoord()
                        uv.u = java.lang.Float.valueOf(tokens[current++])
                        uv.v = java.lang.Float.valueOf(tokens[current++])
                        uvs.add(uv)
                    } else if (lineType == "f") {
                        // face
                        val face = WavefrontModelData.Face()
                        for (a in 0..2) {
                            temp = tokens[current++].split(SLASH.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            index = Integer.valueOf(temp[0]) - 1
                            face.vertexIndex[a] = index
                            // face.v[a] = vertices.get(index);

                            // li inizializziamo ora a -1
                            face.textureIndex[a] = -1
                            face.normalIndex[a] = -1
                            if (temp.size > 1 && temp[1].length > 0) {
                                // texture-coordinate
                                index = Integer.valueOf(temp[1]) - 1
                                face.textureIndex[a] = index
                                // face.uv[a] = uvs.get(index);
                            }
                            if (temp.size > 2) {
                                // normal
                                index = Integer.valueOf(temp[2]) - 1
                                face.normalIndex[a] = index
                                // face.n[a] = normals.get(index);
                            }
                        }
                        faces.add(face)
                    }
                }
                line = reader.readLine()
            }
            // printFaces(faces);
            val facesSize = faces.size
            Logger.info("$facesSize polys")
            model.vertices = vertices
            model.tex = uvs
            model.normals = normals
            model.triangles = faces
            reader.close()
        } catch (e: IOException) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
        return model
    }

    fun loadFromResources(context: Context, resourceId: Int): WavefrontModelData {
        return try {
            load(context.resources.openRawResource(resourceId))
        } catch (e: Exception) {
            Logger.fatal(e.message)
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }
}