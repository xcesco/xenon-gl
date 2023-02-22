package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType

@BindType
class XmlFaces {
    @Bind("face")
    var list: ArrayList<XmlFace>? = null
}