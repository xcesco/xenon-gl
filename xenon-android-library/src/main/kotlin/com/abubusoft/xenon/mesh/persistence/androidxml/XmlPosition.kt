package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

@BindType
class XmlPosition {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var x = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var y = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var z = 0f
}