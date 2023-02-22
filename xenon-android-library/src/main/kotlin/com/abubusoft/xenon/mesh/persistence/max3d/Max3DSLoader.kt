package com.abubusoft.xenon.mesh.persistence.max3d

import android.content.Context
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.math.Vector3
import java.io.IOException
import java.io.InputStream

/**
 * 3DS object parser. This is a work in progress. Materials aren't parsed yet.
 *
 * @author dennis.ippel
 * @author lacasrac
 */
class Max3DSLoader : MeshParserBase() {
    enum class ChunkType(val value: Int) {
        IDENTIFIER_3DS(0x4D4D), MESH_BLOCK(0x3D3D), OBJECT_BLOCK(0x4000), TRIMESH(0x4100), VERTICES(0x4110), FACES(0x4120), TEXCOORD(0x4140), TEX_MAP(0xA200), TRI_MATERIAL(0x4130), TEX_NAME(
            0xA000
        ),
        TEX_FILENAME(0xA300), MATERIAL(0xAFFF);

        companion object {
            fun parseValue(v: Int): ChunkType? {
                for (item in values()) {
                    if (v == item.value) return item
                }
                return null
            }
        }
    }

    private val mVertices = ArrayList<ArrayList<Vector3>>()
    private val mNormals = ArrayList<Array<Vector3?>>()
    private val mVertNormals = ArrayList<ArrayList<Vector3>>()
    private val mTexCoords = ArrayList<ArrayList<Vector3>>()
    private val mIndices = ArrayList<ArrayList<Int>>()
    private val mObjNames = ArrayList<String>()
    private var mChunkID = 0
    private var mChunkEndOffset = 0
    private var mEndReached = false
    private var mObjects = -1
    private var models: ArrayList<Max3dsModelData>? = null
    @Throws(IOException::class)
    protected fun parseInternal(stream: InputStream): ArrayList<Max3dsModelData>? {
        models = ArrayList()
        Logger.info("Start parsing 3DS")
        readHeader(stream)
        if (mChunkID != ChunkType.IDENTIFIER_3DS.value) {
            Logger.error("Not a valid 3DS file")
            return null
        }
        while (!mEndReached) {
            readChunk(stream)
        }
        build()

        // TODO
        /*
		 * if (mRootObject.getNumChildren() == 1) mRootObject = mRootObject.getChildAt(0);
		 */stream.close()
        Logger.info("End parsing 3DS")
        return models
    }

    @Throws(IOException::class)
    fun readChunk(stream: InputStream) {
        readHeader(stream)
        val chunk = ChunkType.parseValue(mChunkID)
        // Logger.debug("CHUNK %s - %s", mChunkID, chunk);
        if (chunk == null) {
            skipRead(stream)
        } else {
            when (chunk) {
                ChunkType.MESH_BLOCK -> {}
                ChunkType.OBJECT_BLOCK -> {
                    mObjects++
                    mObjNames.add(readString(stream))
                }
                ChunkType.TRIMESH -> {}
                ChunkType.VERTICES -> readVertices(stream)
                ChunkType.FACES -> readFaces(stream)
                ChunkType.TEXCOORD -> readTexCoords(stream)
                ChunkType.TEX_NAME ->                // mCurrentMaterialKey = readString(stream);
                    skipRead(stream)
                ChunkType.TEX_FILENAME -> {
                    val fileName = readString(stream)
                    Logger.debug("TEX_FILENAME %s", fileName)
                }
                ChunkType.TRI_MATERIAL ->                // String materialName = readString(stream);
                    // int numFaces = readShort(stream);
                    //
                    // for (int i = 0; i < numFaces; i++) {
                    // int faceIndex = readShort(stream);
                    // co.faces.get(faceIndex).materialKey = materialName;
                    // }
                    skipRead(stream)
                ChunkType.MATERIAL -> {}
                ChunkType.TEX_MAP -> {}
                else -> skipRead(stream)
            }
        }
    }

    fun build() {
        val num = mVertices.size
        for (j in 0 until num) {
            val indices = mIndices[j]
            val vertices = mVertices[j]
            var texCoords: ArrayList<Vector3>? = null
            val vertNormals = mVertNormals[j]
            if (mTexCoords.size > 0) texCoords = mTexCoords[j]
            val len = indices.size
            val aVertices = FloatArray(len * 3)
            val aNormals = FloatArray(len * 3)
            val aTexCoords = FloatArray(len * 2)
            val aIndices = IntArray(len)
            var ic = 0
            var itn = 0
            var itc = 0
            var ivi = 0
            var coord: Vector3
            var texcoord: Vector3
            var normal: Vector3
            var i = 0
            while (i < len) {
                val v1 = indices[i]
                val v2 = indices[i + 1]
                val v3 = indices[i + 2]
                coord = vertices[v1]
                aVertices[ic++] = coord.x
                aVertices[ic++] = coord.y
                aVertices[ic++] = coord.z
                aIndices[ivi] = ivi++
                coord = vertices[v2]
                aVertices[ic++] = coord.x
                aVertices[ic++] = coord.y
                aVertices[ic++] = coord.z
                aIndices[ivi] = ivi++
                coord = vertices[v3]
                aVertices[ic++] = coord.x
                aVertices[ic++] = coord.y
                aVertices[ic++] = coord.z
                aIndices[ivi] = ivi++
                if (texCoords != null && texCoords.size > 0) {
                    texcoord = texCoords[v1]
                    aTexCoords[itc++] = texcoord.x
                    aTexCoords[itc++] = texcoord.y
                    texcoord = texCoords[v2]
                    aTexCoords[itc++] = texcoord.x
                    aTexCoords[itc++] = texcoord.y
                    texcoord = texCoords[v3]
                    aTexCoords[itc++] = texcoord.x
                    aTexCoords[itc++] = texcoord.y
                }
                normal = vertNormals[v1]
                aNormals[itn++] = normal.x
                aNormals[itn++] = normal.y
                aNormals[itn++] = normal.z
                normal = vertNormals[v2]
                aNormals[itn++] = normal.x
                aNormals[itn++] = normal.y
                aNormals[itn++] = normal.z
                normal = vertNormals[v3]
                aNormals[itn++] = normal.x
                aNormals[itn++] = normal.y
                aNormals[itn++] = normal.z
                i += 3
            }
            val targetObj = Max3dsModelData(mObjNames[j])
            targetObj.setData(aVertices, aNormals, aTexCoords, aIndices)
            // -- diffuse material with random color. for now.
            // TODO
            /*
			 * DiffuseMaterial material = new DiffuseMaterial(); material.setUseSingleColor(true); targetObj.setMaterial(material); targetObj.setColor(0xff000000 + (int) (Math.random() * 0xffffff)); mRootObject.addChild(targetObj);
			 */models!!.add(targetObj)
        }
    }

    fun clear() {
        for (i in 0 until mObjects) {
            mIndices[i].clear()
            mVertNormals[i].clear()
            mVertices[i].clear()
            mTexCoords[i].clear()
        }
        mIndices.clear()
        mVertNormals.clear()
        mVertices.clear()
        mTexCoords.clear()
    }

    @Throws(IOException::class)
    protected fun skipRead(stream: InputStream) {
        var i = 0
        while (i < mChunkEndOffset - 6 && !mEndReached) {
            mEndReached = stream.read() < 0
            i++
        }
    }

    @Throws(IOException::class)
    protected fun readVertices(buffer: InputStream) {
        var x: Float
        var y: Float
        var z: Float
        val numVertices = readShort(buffer)
        val vertices = ArrayList<Vector3>()
        for (i in 0 until numVertices) {
            x = readFloat(buffer)
            y = readFloat(buffer)
            z = readFloat(buffer)
            vertices.add(Vector3(x, y, z))
        }
        mVertices.add(vertices)
    }

    @Throws(IOException::class)
    protected fun readTexCoords(buffer: InputStream) {
        val numVertices = readShort(buffer)
        val texCoords = ArrayList<Vector3>()
        for (i in 0 until numVertices) {
            val x = readFloat(buffer)
            val y = 1 - readFloat(buffer)
            texCoords.add(Vector3(x, y, 0f))
        }
        mTexCoords.add(texCoords)
    }

    @Throws(IOException::class)
    protected fun readFaces(buffer: InputStream) {
        val triangles = readShort(buffer)
        val normals = arrayOfNulls<Vector3>(triangles)
        val indices = ArrayList<Int>()
        for (i in 0 until triangles) {
            val vertexIDs = IntArray(3)
            vertexIDs[0] = readShort(buffer)
            vertexIDs[1] = readShort(buffer)
            vertexIDs[2] = readShort(buffer)
            readShort(buffer)
            indices.add(vertexIDs[0])
            indices.add(vertexIDs[1])
            indices.add(vertexIDs[2])
            val normal = calculateFaceNormal(vertexIDs)
            normals[i] = normal
        }
        mNormals.add(arrayOfNulls(triangles))
        mIndices.add(indices)
        val numVertices = mVertices[mObjects].size
        val numIndices = indices.size
        val vertNormals = ArrayList<Vector3>()
        for (i in 0 until numVertices) {
            val vertexNormal = Vector3()
            var j = 0
            while (j < numIndices) {
                val id1 = indices[j]
                val id2 = indices[j + 1]
                val id3 = indices[j + 2]
                if (id1 == i || id2 == i || id3 == i) {
                    vertexNormal.add(normals[j / 3]!!)
                }
                j += 3
            }
            vertexNormal.normalize()
            vertNormals.add(vertexNormal)
        }
        mVertNormals.add(vertNormals)
    }

    private fun calculateFaceNormal(vertexIDs: IntArray): Vector3 {
        val vertices = mVertices[mObjects]
        val v1 = vertices[vertexIDs[0]]
        val v2 = vertices[vertexIDs[2]]
        val v3 = vertices[vertexIDs[1]]
        val vector1 = Vector3.subtract(v2, v1)
        val vector2 = Vector3.subtract(v3, v1)
        val normal = Vector3.crossProduct(vector1, vector2)
        normal.normalize()
        return normal
    }

    @Throws(IOException::class)
    protected fun readHeader(stream: InputStream) {
        mChunkID = readShort(stream)
        mChunkEndOffset = readInt(stream)
        mEndReached = mChunkID < 0
    }

    @Throws(IOException::class)
    protected fun readString(stream: InputStream): String {
        var result = String()
        var inByte: Byte
        while (stream.read().toByte().also { inByte = it }.toInt() != 0) result += Char(inByte.toUShort())
        return result
    }

    @Throws(IOException::class)
    override fun readInt(stream: InputStream): Int {
        return stream.read() or (stream.read() shl 8) or (stream.read() shl 16) or (stream.read() shl 24)
    }

    @Throws(IOException::class)
    override fun readShort(stream: InputStream): Int {
        return stream.read() or (stream.read() shl 8)
    }

    @Throws(IOException::class)
    override fun readFloat(stream: InputStream): Float {
        return java.lang.Float.intBitsToFloat(readInt(stream))
    }

    companion object {
        @Throws(IOException::class)
        fun parse(stream: InputStream): ArrayList<Max3dsModelData>? {
            val parser = Max3DSLoader()
            return parser.parseInternal(stream)
        }

        /**
         *
         *
         * Carica un oggetto in formato Max3D dagli assets.
         *
         *
         * @param context
         * @param fileName
         * @return modello in formato 3Dmax
         */
        fun loadFromAsset(context: Context, fileName: String?): Max3dsModelData {
            return try {
                parse(context.assets.open(fileName!!))!![0]
            } catch (e: IOException) {
                Logger.fatal(e.message)
                e.printStackTrace()
                throw XenonRuntimeException(e)
            }
        }

        /**
         * Carica il modello in format Max3D dalle risorse
         *
         * @param context
         * @param resourceId
         * @return modello in formato 3Dmax
         */
        fun loadFromResources(context: Context, resourceId: Int): Max3dsModelData {
            return try {
                parse(context.resources.openRawResource(resourceId))!![0]
            } catch (e: IOException) {
                Logger.fatal(e.message)
                e.printStackTrace()
                throw XenonRuntimeException(e)
            }
        }
    }
}