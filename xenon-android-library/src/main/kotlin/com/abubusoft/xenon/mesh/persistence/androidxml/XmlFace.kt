package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

@BindType
class XmlFace {
    @Bind("v1")
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var vertexIndex0 = 0

    @Bind("v2")
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var vertexIndex1 = 0

    @Bind("v3")
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var vertexIndex2 = 0
}