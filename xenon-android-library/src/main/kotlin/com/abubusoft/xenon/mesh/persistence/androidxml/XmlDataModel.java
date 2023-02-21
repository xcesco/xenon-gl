package com.abubusoft.xenon.mesh.persistence.androidxml;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;

@BindType("mesh")
public class XmlDataModel {

	@Bind("sharedgeometry")
	public XmlSharedGeometry geometry;
	
	@Bind("submeshes")
	public XmlSubmeshes submeshes;
	
	@Bind("submeshnames")
	public XmlSubmeshnames names;
	
}
