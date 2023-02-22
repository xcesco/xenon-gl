/**
 *
 */
package com.abubusoft.xenon.mesh.persistence.androidxml

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

/**
 * @author Francesco Benincasa
 */
@BindType
class XmlName {
    @Bind("name")
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var name: String? = null

    @Bind("index")
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var index = 0
}