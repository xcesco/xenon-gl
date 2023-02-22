package com.abubusoft.xenon.texture

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

/**
 * Rappresenta un'area rettangolare una texture. Le coordinate quindi si considerando normalizzate rispetto
 * alla dimensione della texture. In altre parole andranno normalmente nell'intervallo [0..1].
 *
 * @author Francesco Benincasa
 */
@BindType
open class TextureRegion {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var highX = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var lowX = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var highY = 0f

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var lowY = 0f
}