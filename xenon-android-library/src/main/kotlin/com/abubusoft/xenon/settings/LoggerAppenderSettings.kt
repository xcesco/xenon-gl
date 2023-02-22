package com.abubusoft.xenon.settings

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType
import com.abubusoft.xenon.core.Uncryptable

/**
 * Configurazione di un appender di log
 *
 * @author Francesco Benincasa
 */
@Uncryptable
@BindType
class LoggerAppenderSettings {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var tag = ""

    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var level: LoggerLevelType? = null
}