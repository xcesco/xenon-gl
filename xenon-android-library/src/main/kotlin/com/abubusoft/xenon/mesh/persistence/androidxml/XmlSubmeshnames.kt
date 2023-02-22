package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType

@BindType
class XmlSubmeshnames {
    @Bind("submesh")
    var names: ArrayList<XmlName?>? = null
}