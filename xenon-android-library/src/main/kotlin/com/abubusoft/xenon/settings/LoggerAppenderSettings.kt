package com.abubusoft.xenon.settings;

import com.abubusoft.xenon.core.Uncryptable;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

/**
 * Configurazione di un appender di log
 * 
 * @author Francesco Benincasa
 * 
 */
@Uncryptable
@BindType
public class LoggerAppenderSettings {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public String tag = "";

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public LoggerLevelType level;

}
