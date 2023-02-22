package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

@BindType
class XmlVertexBuffer {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var position = false

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var normals = false

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var texture_coords = 0

    @Bind("vertex")
    @BindXml(xmlType = XmlType.TAG)
    var list: List<XmlVertex>? = null
}