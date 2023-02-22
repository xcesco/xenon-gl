package com.abubusoft.xenon.mesh.persistence.androidxml;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

@BindType
public class XmlFace {

	@Bind("v1")
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public int vertexIndex0;
	
	@Bind("v2")
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public int vertexIndex1;
	
	@Bind("v3")
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public int vertexIndex2;
}
