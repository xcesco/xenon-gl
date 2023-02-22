package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

@BindType
class XmlTexCoord {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var u = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var v = 0f
}