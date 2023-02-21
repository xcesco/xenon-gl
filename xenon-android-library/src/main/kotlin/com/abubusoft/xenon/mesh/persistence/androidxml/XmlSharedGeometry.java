package com.abubusoft.xenon.mesh.persistence.androidxml;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;


@BindType
public class XmlSharedGeometry {

	@Bind("vertexcount")
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public int vertexCount;
	
	@Bind("vertexbuffer")
	@BindXml(xmlType=XmlType.TAG)
	public XmlVertexBuffer vertexBuffer;
}
