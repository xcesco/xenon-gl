package com.abubusoft.xenon.mesh.persistence.androidxml;

import java.util.List;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;


@BindType
public class XmlVertexBuffer {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public boolean position;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public boolean normals;
	
	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public int texture_coords;
	
	@Bind("vertex")	
	@BindXml(xmlType=XmlType.TAG)
	public List<XmlVertex> list;
}
