package com.abubusoft.xenon.settings;

import com.abubusoft.xenon.core.Uncryptable;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

@Uncryptable
@BindType
public class LoggerSettings {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public LoggerLevelType level = LoggerLevelType.NONE;

}
