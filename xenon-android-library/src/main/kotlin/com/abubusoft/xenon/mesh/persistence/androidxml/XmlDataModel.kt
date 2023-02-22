package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType

@BindType("mesh")
class XmlDataModel {
    @Bind("sharedgeometry")
    var geometry: XmlSharedGeometry? = null

    @Bind("submeshes")
    var submeshes: XmlSubmeshes? = null

    @Bind("submeshnames")
    var names: XmlSubmeshnames? = null
}