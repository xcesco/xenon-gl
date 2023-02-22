/**
 *
 */
/**
 * @author xcesco
 */
package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.xenon.vbo.IndexBuffer.update
import com.abubusoft.xenon.vbo.VertexBuffer.update
import com.abubusoft.xenon.math.Dimension3.set
import com.abubusoft.xenon.vbo.TextureBuffer.update
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlDataModel
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshHelper
import com.abubusoft.xenon.mesh.persistence.androidxml.AndroidXmlAdapter
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlVertex
import com.abubusoft.xenon.mesh.MeshDrawModeType
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlFace
import com.abubusoft.xenon.mesh.MeshFactory
import com.abubusoft.xenon.vbo.TextureBuffer
import kotlin.Throws
import com.abubusoft.kripton.KriptonBinder
import com.abubusoft.xenon.mesh.persistence.androidxml.AndroidXmlLoader
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlSharedGeometry
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlSubmeshes
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlSubmeshnames
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlVertexBuffer
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlFaces
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlSubmesh
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlName
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlPosition
import com.abubusoft.xenon.mesh.persistence.androidxml.XmlTexCoord
