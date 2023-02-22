package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType

@BindType
class XmlVertex {
    @Bind("position")
    var position: XmlPosition? = null

    @Bind("normal")
    var normal: XmlPosition? = null

    @Bind("texcoord")
    var texcoord: XmlTexCoord? = null
}