package com.abubusoft.xenon.mesh.persistence.androidxml;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

@BindType
public class XmlTexCoord {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float u;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float v;
}
