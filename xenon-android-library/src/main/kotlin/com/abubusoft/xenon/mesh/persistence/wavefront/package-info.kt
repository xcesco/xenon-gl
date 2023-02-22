/**
 *
 */
/**
 * @author xcesco
 */
package com.abubusoft.xenon.mesh.persistence.wavefront

import com.abubusoft.xenon.math.XenonMath.findNextPowerOf10
import com.abubusoft.xenon.vbo.VertexBuffer.update
import com.abubusoft.xenon.vbo.TextureBuffer.update
import com.abubusoft.xenon.mesh.modifiers.ColorModifier.setColor
import com.abubusoft.xenon.vbo.IndexBuffer.update
import com.abubusoft.xenon.math.Dimension3.set
import com.abubusoft.xenon.vbo.ColorBuffer.update
import android.annotation.SuppressLint
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.FlatVertexF
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontAdapter.VertexKeyBuilder
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.VertexF
import com.abubusoft.xenon.mesh.MeshFactory
import com.abubusoft.xenon.mesh.MeshHelper
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.TextureBuffer
import com.abubusoft.xenon.mesh.modifiers.ColorModifier
import com.abubusoft.xenon.mesh.MeshDrawModeType
import com.abubusoft.xenon.vbo.BufferAllocationOptions
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontModelData.UVCoord
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontAdapter
import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.core.XenonRuntimeException
import kotlin.Throws
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontExporter
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontLoader
