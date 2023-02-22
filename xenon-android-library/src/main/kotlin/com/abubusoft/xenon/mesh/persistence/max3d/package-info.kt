/**
 *
 */
/**
 * @author xcesco
 */
package com.abubusoft.xenon.mesh.persistence.max3d

import com.abubusoft.xenon.vbo.IndexBuffer.update
import com.abubusoft.xenon.vbo.VertexBuffer.update
import com.abubusoft.xenon.math.Dimension3.set
import com.abubusoft.xenon.vbo.TextureBuffer.update
import com.abubusoft.xenon.math.Vector3.add
import com.abubusoft.xenon.math.Point3.normalize
import com.abubusoft.xenon.math.Vector3.crossProduct
import com.abubusoft.xenon.mesh.persistence.max3d.Max3dsModelData
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshHelper
import com.abubusoft.xenon.mesh.persistence.max3d.Max3DSAdapter
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.mesh.MeshFactory
import com.abubusoft.xenon.vbo.TextureBuffer
import com.abubusoft.xenon.mesh.MeshDrawModeType
import com.abubusoft.xenon.mesh.persistence.max3d.MeshParserBase
import com.abubusoft.xenon.mesh.persistence.max3d.Max3DSLoader.ChunkType
import com.abubusoft.xenon.math.Vector3
import kotlin.Throws
import com.abubusoft.xenon.mesh.persistence.max3d.Max3DSLoader
import com.abubusoft.xenon.core.XenonRuntimeException
