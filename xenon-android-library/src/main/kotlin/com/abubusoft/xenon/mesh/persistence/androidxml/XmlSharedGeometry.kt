package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

@BindType
class XmlSharedGeometry {
    @Bind("vertexcount")
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var vertexCount = 0

    @Bind("vertexbuffer")
    @BindXml(xmlType = XmlType.TAG)
    var vertexBuffer: XmlVertexBuffer? = null
}