package com.abubusoft.xenon.mesh.persistence.androidxml;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;


@BindType
public class XmlPosition {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float x;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float y;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public float z;
}
