package com.abubusoft.xenon.settings

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType
import com.abubusoft.xenon.core.Uncryptable

@Uncryptable
@BindType
class LoggerSettings {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var level = LoggerLevelType.NONE
}